#!/usr/bin/env bash
#
# Fail if a built HTML kit carries product content. The kit release is a public download, so it holds
# only Lemonade: no product screens, no APP- rules, no text about a product's app. The package names
# (@teya/lemonade-mobile-ds, com.teya.lemonade) are Lemonade's own and pass.
#
# Usage: scripts/check-html-kit-bundle.sh [<kit dir>]   (default web/dist/lemonade-html-kit)

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIR="${1:-$ROOT/web/dist/lemonade-html-kit}"
[ -f "$DIR/lemonade-kit.css" ] || { echo "error: no built kit at $DIR. Run: (cd web && npm run html-kit)" >&2; exit 1; }

found=0
report() { echo "::error::$1"; found=1; }

[ -d "$DIR/screens" ] && report "the kit ships product screens in screens/"

while IFS= read -r file; do
  report "${file#"$DIR"/} names an APP- rule: $(grep -ohE '\bAPP-[A-Z][A-Z0-9]*(-[A-Z0-9]+)*' "$file" | sort -u | head -5 | tr '\n' ' ')"
done < <(grep -rlE '\bAPP-[A-Z][A-Z0-9]*' "$DIR" || true)

while IFS= read -r file; do
  report "${file#"$DIR"/} describes a product's app: $(grep -ohE '.{0,30}\bTeya app\b.{0,30}' "$file" | head -1)"
done < <(grep -rlE '\bTeya app\b' "$DIR" || true)

if [ "$found" = 1 ]; then
  echo "The kit holds product content and must not be released." >&2
  exit 1
fi
echo "The kit holds only Lemonade."
