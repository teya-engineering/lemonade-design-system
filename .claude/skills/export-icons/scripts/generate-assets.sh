#!/usr/bin/env bash
# Regenerate the KMP + SwiftUI assets from svg/, after icons were added or changed.
#
# Usage (from anywhere in the repo):
#   .claude/skills/export-icons/scripts/generate-assets.sh [--skip-api]
#
#   --skip-api   stop after the converters; don't run apiDump or the ABI classifier
#
# Runs the SVG converter, then the country-flags post-generator, then refreshes
# the API baselines that the new enum entries move.
set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

SKIP_API=0
[ "${1:-}" = "--skip-api" ] && SKIP_API=1

KOTLIN_VERSION="2.3.20"
KOTLIN_HOME="$HOME/.local/kotlin-$KOTLIN_VERSION/kotlinc"
KOTLIN_BIN="$KOTLIN_HOME/bin/kotlin"

# The .main.kts scripts crash on Kotlin 2.4.0 (the version Homebrew installs)
# with a FIR compiler error, so 2.3.20 is installed separately and always
# invoked by absolute path — whatever `kotlin` resolves to on PATH is ignored.
ensure_kotlin() {
  if [ -x "$KOTLIN_BIN" ] && "$KOTLIN_BIN" -version 2>&1 | grep -q "$KOTLIN_VERSION"; then
    return
  fi
  echo "==> Installing Kotlin $KOTLIN_VERSION into $KOTLIN_HOME"
  mkdir -p "$HOME/.local/kotlin-$KOTLIN_VERSION"
  local zip="/tmp/kotlin-$KOTLIN_VERSION.zip"
  curl -fSL "https://github.com/JetBrains/kotlin/releases/download/v$KOTLIN_VERSION/kotlin-compiler-$KOTLIN_VERSION.zip" -o "$zip"
  unzip -q -o "$zip" -d "$HOME/.local/kotlin-$KOTLIN_VERSION"
  rm -f "$zip"
  "$KOTLIN_BIN" -version
}

command -v rsvg-convert >/dev/null 2>&1 || {
  echo "error: rsvg-convert not found. Install with: brew install librsvg" >&2
  exit 1
}

ensure_kotlin

# A .main.kts keeps a compiled cache that editing an @file:Import'ed loader does
# not invalidate, so a stale loader can silently produce freshly-dated output.
rm -rf "$HOME/Library/Caches/main.kts.compiled.cache" "$HOME/.cache/main.kts.compiled.cache"

echo "==> svg-asset-converter"
"$KOTLIN_BIN" scripts/svg-asset-converter.main.kts

# The converter rewrites LemonadeCountryFlags.kt from scratch, which drops the
# public `companion object { getOrNull(alpha2) }` at the bottom — a removal that
# classifies as BREAKING. This generator puts it back. Never run the converter
# without it, even when only icons changed.
echo "==> kmp-country-flags-alpha2-generator"
"$KOTLIN_BIN" scripts/kmp-country-flags-alpha2-generator.main.kts

# The converter's skip-list lives in .cache/, which is gitignored. In a fresh
# clone or worktree the cache starts empty, so every SVG counts as changed and
# all ~562 imagesets get re-encoded. rsvg-convert stamps a CreationDate inside
# the PDF, so those show up as binary diffs despite rendering identically.
# Revert any imageset whose source SVG did not actually change.
echo "==> Checking for PDF churn"
changed_svgs="$(git diff --name-only HEAD -- 'svg/*' | sed 's|.*/||;s|\.svg$||' | sort -u)"
churned_list="$(
  git diff --name-only HEAD -- 'swiftui/Sources/Lemonade/Resources/Assets.xcassets/*.pdf' |
    while IFS= read -r pdf; do
      name="$(basename "$pdf" .pdf)"
      printf '%s\n' "$changed_svgs" | grep -qx "$name" || printf '%s\n' "$pdf"
    done
)"
# Restored in one call: a checkout per file takes the index lock hundreds of
# times and races the diff that is still streaming into this loop.
if [ -n "$churned_list" ]; then
  printf '%s\n' "$churned_list" | tr '\n' '\0' | xargs -0 git checkout --
  echo "    reverted $(printf '%s\n' "$churned_list" | wc -l | tr -d ' ') PDF(s) re-encoded without an artwork change"
else
  echo "    none"
fi

if [ "$SKIP_API" -eq 1 ]; then
  echo "==> Skipping apiDump (--skip-api)"
else
  # New enum entries are additive but they still move the checked-in baselines,
  # and CI fails on a stale one. ANDROID_HOME is not set in these worktrees.
  echo "==> apiDump"
  (cd kmp && ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}" ./gradlew apiDump)

  echo "==> API stability classifier"
  # Reads the committed baseline, so an uncommitted apiDump reports NO_CHANGES.
  .claude/skills/binary-compatibility/scripts/bcv-check.sh --ci || true
fi

echo
echo "==> Review:"
git status --short
