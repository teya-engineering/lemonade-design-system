#!/usr/bin/env bash
# Verify a web change: token pipeline, package, and drift.
#
# Usage (from the repo root):
#   scripts/web-check.sh            # everything
#   scripts/web-check.sh --fast     # skip the full converter run and the tarball install
#
# Exits non-zero on the first failure. Requires Kotlin 2.3.20, which
# run-converters.sh installs into ~/.local on first use.
set -euo pipefail

cd "$(git rev-parse --show-toplevel)"
KOTLIN="$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin"
FAST=${1:-}

step() { printf '\n== %s\n' "$1"; }

[ -x "$KOTLIN" ] || {
  echo "Kotlin 2.3.20 missing. Run .claude/skills/generate-tokens/scripts/run-converters.sh once to install it." >&2
  exit 1
}

step "Loader parity across the four platform loaders"
python3 .claude/skills/generate-tokens/scripts/check-loader-parity.py

step "Kotlin unit tests"
for t in web-loader-dtcg-test web-token-commons-test; do
  [ -f "scripts/$t.main.kts" ] && "$KOTLIN" "scripts/$t.main.kts"
done

step "Text styles match SwiftUI"
[ -f scripts/web-text-style-parity-check.main.kts ] && "$KOTLIN" scripts/web-text-style-parity-check.main.kts

step "Component vocabularies match KMP and SwiftUI"
[ -f scripts/web-component-enum-parity-check.main.kts ] && "$KOTLIN" scripts/web-component-enum-parity-check.main.kts

step "Package: install from the committed lockfile, typecheck, test, build"
( cd web && npm ci --silent && npm run typecheck && npm test && npm run build )

step "The build must not touch committed files"
dirty=$(git status --porcelain -- web/assets web/styles web/src web/llms.txt web/tokens.json text-styles.json)
[ -z "$dirty" ] || { echo "Build modified committed output:"; echo "$dirty"; exit 1; }

if [ "$FAST" = "--fast" ]; then
  printf '\nOK (fast). Skipped the full converter run and the tarball install.\n'
  exit 0
fi

step "Every converter regenerates identical output"
.claude/skills/generate-tokens/scripts/run-converters.sh --all >/dev/null
for s in web-text-style-converter web-css-bundle; do
  [ -f "scripts/$s.main.kts" ] && "$KOTLIN" "scripts/$s.main.kts" >/dev/null
done
drift=$(git status --porcelain -- kmp/ swiftui/ web/ text-styles.json)
[ -z "$drift" ] || { echo "Generated output drifted from its source:"; echo "$drift"; exit 1; }

step "The packed tarball installs and imports"
tgz=$(cd web && npm pack --silent --pack-destination "$TMPDIR")
scratch=$(mktemp -d)
pkg_dir="node_modules/@teya/lemonade-mobile-ds"
( cd "$scratch" && npm init -y >/dev/null && npm install "$TMPDIR/$tgz" --silent \
  && test -f "$pkg_dir/dist/components.css" \
  && test -f "$pkg_dir/dist/react.d.ts" \
  `# React is an optional peer, so everything but ./react has to work without it installed.` \
  && node -e "require('@teya/lemonade-mobile-ds')" \
  && node -e "const {buttonClasses}=require('@teya/lemonade-mobile-ds'); if(!buttonClasses().includes('lmnd-button')) throw new Error('buttonClasses is not usable without React')" \
  && npm install react react-dom --silent \
  && node -e "const {Button}=require('@teya/lemonade-mobile-ds/react'); if(typeof Button!=='function') throw new Error('./react does not export Button')" )
rm -rf "$scratch" "$TMPDIR/$tgz"

printf '\nOK. Pipeline, package and drift all clean.\n'
