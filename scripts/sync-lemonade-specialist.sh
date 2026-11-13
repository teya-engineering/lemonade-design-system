#!/usr/bin/env bash
#
# Copy guidelines/ into the lemonade-specialist plugin. A plugin install caches only the plugin's own
# folder, and symlinks out of it break, so the skill carries a real copy.
#
# Usage: scripts/sync-lemonade-specialist.sh

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEST="$ROOT/plugins/lemonade-specialist/skills/native-ui/guidelines"

rm -rf "$DEST"
mkdir -p "$DEST"
for path in "$ROOT/guidelines"/*.md; do
  name="$(basename "$path")"
  {
    echo "<!-- DO NOT MODIFY THIS FILE MANUALLY. Copied from guidelines/$name by scripts/sync-lemonade-specialist.sh. -->"
    echo
    cat "$path"
  } > "$DEST/$name"
done
echo "copied guidelines/ into ${DEST#"$ROOT"/}"
