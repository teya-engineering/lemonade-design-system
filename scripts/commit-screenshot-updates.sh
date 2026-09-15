#!/usr/bin/env bash
#
# Commits and pushes Roborazzi golden screenshots that were re-recorded in place
# by `:ui:verifyAndRecordRoborazziDebug`. Run from the repo root by the
# `android-screenshots` CI job (signed commit + push to the PR branch).
# Requires $BRANCH (the PR head branch) and a pre-configured signing key.
set -euo pipefail
: "${BRANCH:?env var BRANCH is required}"
readonly PATHSPEC='*/screenshots/*.png'
git add -A -- "$PATHSPEC"
if git diff --cached --quiet -- "$PATHSPEC"; then
  echo "No screenshot changes to commit."
  exit 0
fi
git commit -S -m "🤖 Update screenshots"
git push origin "HEAD:${BRANCH}"
