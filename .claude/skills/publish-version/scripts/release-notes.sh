#!/usr/bin/env bash
#
# release-notes.sh — print GitHub release notes for one SDK, listing only the PRs
# that touched its shipped source. `gh release create --generate-notes` lists every
# PR merged in the range, so web, docs or SwiftUI-only work would land in the KMP
# release.
#
# Usage:
#   release-notes.sh <prev_tag> <new_tag> <release_sha> <pathspec>...
#   release-notes.sh lemonade-kmp-0.42.0 lemonade-kmp-0.42.1 <sha> kmp/ ':(exclude)kmp/composeApp/'
#
set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

[ $# -ge 4 ] || { echo "usage: $0 <prev_tag> <new_tag> <release_sha> <pathspec>..." >&2; exit 1; }
PREV_TAG=$1 NEW_TAG=$2 SHA=$3; shift 3

REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)

echo "## What's Changed"
FOUND=0
SEEN=" "
while read -r COMMIT; do
  PR=$(gh api "repos/${REPO}/commits/${COMMIT}/pulls" \
    -q '[.[] | select(.merged_at != null)][0] | "\(.number)\t\(.title)\t\(.user.login)\t\(.html_url)"')
  if [ -z "$PR" ] || [ "$PR" = "null" ]; then
    echo "* $(git log -1 --format='%s' "$COMMIT") (${COMMIT:0:8})"
    FOUND=1
    continue
  fi
  IFS=$'\t' read -r NUMBER TITLE AUTHOR URL <<<"$PR"
  case "$SEEN" in *" ${NUMBER} "*) continue ;; esac
  SEEN="${SEEN}${NUMBER} "
  echo "* ${TITLE} by @${AUTHOR} in ${URL}"
  FOUND=1
done < <(git rev-list --reverse --no-merges "${PREV_TAG}..${SHA}" -- "$@")

[ "$FOUND" = 1 ] || echo "* No changes to the shipped SDK in this release"

echo
echo "**Full Changelog**: https://github.com/${REPO}/compare/${PREV_TAG}...${NEW_TAG}"
