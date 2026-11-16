#!/usr/bin/env bash
#
# Copy guidelines/ and the HTML kit's mapping.md into the lemonade-specialist plugin. A plugin install
# caches only the plugin's own folder, and symlinks out of it break, so the skill carries real copies.
#
# Usage: scripts/sync-lemonade-specialist.sh

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SKILL="$ROOT/plugins/lemonade-specialist/skills/native-ui"
DEST="$SKILL/guidelines"

banner() { echo "<!-- DO NOT MODIFY THIS FILE MANUALLY. Copied from $1 by scripts/sync-lemonade-specialist.sh. -->"; echo; }

rm -rf "$DEST"
mkdir -p "$DEST"
for path in "$ROOT/guidelines"/*.md; do
  name="$(basename "$path")"
  { banner "guidelines/$name"; cat "$path"; } > "$DEST/$name"
done
{ banner "web/html-kit/mapping.md"; cat "$ROOT/web/html-kit/mapping.md"; } > "$SKILL/mapping.md"
echo "copied guidelines/ and web/html-kit/mapping.md into ${SKILL#"$ROOT"/}"
