#!/usr/bin/env bash
#
# release-status.sh — gather everything the publish-version skill needs to
# propose a Lemonade SDK release. Read-only except for the `git fetch`.
#
# What it does:
#   1. Refreshes origin/main and tags (so we always reason about the freshest tree).
#   2. Resolves RELEASE_SHA = the exact origin/main HEAD that tags will point at.
#      (Tagging this SHA is the worktree-safe equivalent of "checkout origin/main":
#       the release always reflects the latest origin/main, no branch switch needed.)
#   3. Finds the latest released KMP and SwiftUI versions. Both are purely
#      tag-driven — there are no version files to bump.
#         KMP     -> tag  lemonade-kmp-X.Y.Z
#         SwiftUI -> tag  lemonade-swiftui-X.Y.Z  (CI) + plain X.Y.Z (SPM resolution)
#   4. Lists what changed in each SDK's *shipped* source since its last release and
#      prints patch/minor candidates plus a suggested bump per the project rule.
#   5. Reports the lemonade-specialist plugin's state. Its version lives in
#      plugin.json, and tag lemonade-specialist--vX.Y.Z marks each released version:
#         PLUGIN_ACTION=tag   plugin.json is ahead of the latest tag -> tag the commit that
#                             set the version (PLUGIN_TAG_SHA), not RELEASE_SHA
#         PLUGIN_ACTION=bump  the plugin changed since its tag -> open a version bump PR
#         PLUGIN_ACTION=none  nothing to release
#   6. Reports the HTML kit's state. Its version is tag-driven, like the SDKs:
#         HTML kit -> tag lemonade-html-kit-X.Y.Z  (html_kit_release.yml builds and releases it)
#
# Version bump rule (suggestion only — always confirmed with the user):
#   - changes on ONE side only -> PATCH bump for that SDK
#   - changes on BOTH sides     -> MINOR bump for both
#
# Usage:
#   release-status.sh             # fetch + full report
#   release-status.sh --no-fetch  # skip the network fetch, use local refs
#
set -euo pipefail

# Resolve paths against the repo root so the kmp/ and swiftui/ pathspecs are
# correct no matter where this is invoked from.
cd "$(git rev-parse --show-toplevel)"

FETCH=1
[ "${1:-}" = "--no-fetch" ] && FETCH=0

if [ "$FETCH" = "1" ]; then
  echo "Fetching origin/main and tags..." >&2
  git fetch origin main --tags --prune --quiet
fi

if ! git rev-parse --verify --quiet origin/main >/dev/null; then
  echo "ERROR: origin/main not found. Is 'origin' the saltpay/lemonade-design-system remote?" >&2
  exit 1
fi

RELEASE_SHA=$(git rev-parse origin/main)
RELEASE_SHORT=$(git rev-parse --short origin/main)
RELEASE_SUBJECT=$(git log -1 --format='%s' "$RELEASE_SHA")

latest_tag() { git tag -l "$1" --sort=-version:refname | head -n1; }

KMP_PREV_TAG=$(latest_tag 'lemonade-kmp-*')
SWIFT_PREV_TAG=$(latest_tag 'lemonade-swiftui-*')
KMP_PREV_VER=${KMP_PREV_TAG#lemonade-kmp-}
SWIFT_PREV_VER=${SWIFT_PREV_TAG#lemonade-swiftui-}

bump() { # $1=version (X.Y.Z)  $2=part (major|minor|patch)
  local v=$1 part=$2 MA MI PA
  IFS=. read -r MA MI PA <<<"$v"
  case "$part" in
    major) echo "$((MA + 1)).0.0" ;;
    minor) echo "${MA}.$((MI + 1)).0" ;;
    patch) echo "${MA}.${MI}.$((PA + 1))" ;;
  esac
}

# Shipped-source path filters. Everything else (the composeApp sample, the SwiftUI
# SampleApp/Tests, docs, CI) does NOT warrant a consumer release on its own — the
# analysis subagent makes the final call, but these filters drive the suggestion.
KMP_PATHS=(kmp/ ':(exclude)kmp/composeApp/')
SWIFT_PATHS=(swiftui/Sources/)

# Returns commit count touching the given paths in <prev_tag>..RELEASE_SHA.
shipped_count() {
  local prev_tag=$1; shift
  [ -z "$prev_tag" ] && { echo 0; return; }
  git rev-list --count --no-merges "${prev_tag}..${RELEASE_SHA}" -- "$@"
}

KMP_COUNT=$(shipped_count "$KMP_PREV_TAG" "${KMP_PATHS[@]}")
SWIFT_COUNT=$(shipped_count "$SWIFT_PREV_TAG" "${SWIFT_PATHS[@]}")
[ "$KMP_COUNT" -gt 0 ] && KMP_HAS=true || KMP_HAS=false
[ "$SWIFT_COUNT" -gt 0 ] && SWIFT_HAS=true || SWIFT_HAS=false

# Apply the bump rule.
KMP_SUGGEST="none"
SWIFT_SUGGEST="none"
if [ "$KMP_HAS" = true ] && [ "$SWIFT_HAS" = true ]; then
  KMP_SUGGEST="minor"; SWIFT_SUGGEST="minor"
elif [ "$KMP_HAS" = true ]; then
  KMP_SUGGEST="patch"
elif [ "$SWIFT_HAS" = true ]; then
  SWIFT_SUGGEST="patch"
fi

suggested_version() { # $1=prev_ver $2=suggest-part
  [ "$2" = "none" ] && { echo "-"; return; }
  bump "$1" "$2"
}

print_sdk() { # $1=NAME $2=prev_tag $3=prev_ver $4=count $5=has $6=suggest ; then paths...
  local name=$1 prev_tag=$2 prev_ver=$3 count=$4 has=$5 suggest=$6; shift 6
  echo "## ${name}"
  echo "${name}_PREV_TAG=${prev_tag:-<none>}"
  echo "${name}_PREV_VERSION=${prev_ver:-<none>}"
  echo "${name}_SHIPPED_COMMITS=${count}"
  echo "${name}_HAS_CHANGES=${has}"
  echo "${name}_NEXT_PATCH=$(bump "$prev_ver" patch)"
  echo "${name}_NEXT_MINOR=$(bump "$prev_ver" minor)"
  echo "${name}_NEXT_MAJOR=$(bump "$prev_ver" major)"
  echo "${name}_SUGGESTED_BUMP=${suggest}"
  echo "${name}_SUGGESTED_VERSION=$(suggested_version "$prev_ver" "$suggest")"
  echo "${name}_RANGE=${prev_tag}..${RELEASE_SHORT}"
  echo
  if [ "$count" -gt 0 ]; then
    echo "Commits touching shipped source (${prev_tag}..origin/main):"
    git log --no-merges --format='  %h %s' "${prev_tag}..${RELEASE_SHA}" -- "$@"
    echo
    echo "Files changed:"
    git diff --stat "$prev_tag" "$RELEASE_SHA" -- "$@" | sed 's/^/  /'
  else
    echo "No changes to shipped source since ${prev_tag:-<none>}."
  fi
  echo
}

echo "=================================================================="
echo " Lemonade SDK — release status"
echo "=================================================================="
echo "RELEASE_SHA=${RELEASE_SHA}"
echo "RELEASE_SHORT=${RELEASE_SHORT}"
echo "RELEASE_SUBJECT=${RELEASE_SUBJECT}"
echo
print_sdk KMP   "$KMP_PREV_TAG"   "$KMP_PREV_VER"   "$KMP_COUNT"   "$KMP_HAS"   "$KMP_SUGGEST"   "${KMP_PATHS[@]}"
print_sdk SWIFT "$SWIFT_PREV_TAG" "$SWIFT_PREV_VER" "$SWIFT_COUNT" "$SWIFT_HAS" "$SWIFT_SUGGEST" "${SWIFT_PATHS[@]}"

PLUGIN_DIR=plugins/lemonade-specialist
PLUGIN_PREV_TAG=$(latest_tag 'lemonade-specialist--v*')
PLUGIN_PREV_VER=${PLUGIN_PREV_TAG#lemonade-specialist--v}
PLUGIN_MANIFEST="$PLUGIN_DIR/.claude-plugin/plugin.json"
version_at() { # $1=commit -> the plugin version in that commit's plugin.json, or nothing
  git show "$1:$PLUGIN_MANIFEST" 2>/dev/null \
    | sed -n 's/^ *"version": *"\([^"]*\)".*/\1/p' | head -n1 || true
}
PLUGIN_FILE_VER=$(version_at "$RELEASE_SHA")

PLUGIN_COUNT=0
PLUGIN_AFTER_COUNT=0
if [ -z "$PLUGIN_FILE_VER" ]; then
  PLUGIN_ACTION=none
elif [ "$PLUGIN_FILE_VER" != "$PLUGIN_PREV_VER" ]; then
  PLUGIN_ACTION=tag
  # A tag at RELEASE_SHA would claim plugin changes that landed after the bump, and users
  # who installed the bumped version would never receive them.
  for commit in $(git rev-list "$RELEASE_SHA" -- "$PLUGIN_MANIFEST"); do
    if [ "$(version_at "${commit}^")" != "$PLUGIN_FILE_VER" ]; then
      PLUGIN_TAG_SHA=$commit
      break
    fi
  done
  PLUGIN_AFTER_COUNT=$(git rev-list --count --no-merges "${PLUGIN_TAG_SHA}..${RELEASE_SHA}" -- "$PLUGIN_DIR/")
else
  PLUGIN_COUNT=$(shipped_count "$PLUGIN_PREV_TAG" "$PLUGIN_DIR/")
  [ "$PLUGIN_COUNT" -gt 0 ] && PLUGIN_ACTION=bump || PLUGIN_ACTION=none
fi

echo "## PLUGIN (lemonade-specialist)"
echo "PLUGIN_PREV_TAG=${PLUGIN_PREV_TAG:-<none>}"
echo "PLUGIN_FILE_VERSION=${PLUGIN_FILE_VER:-<none>}"
echo "PLUGIN_ACTION=${PLUGIN_ACTION}"
case "$PLUGIN_ACTION" in
  tag)
    echo "PLUGIN_TAG=lemonade-specialist--v${PLUGIN_FILE_VER}"
    echo "PLUGIN_TAG_SHA=${PLUGIN_TAG_SHA}"
    echo "PLUGIN_CHANGED_AFTER_TAG=${PLUGIN_AFTER_COUNT}"
    echo
    echo "plugin.json is at ${PLUGIN_FILE_VER}, the latest tag is ${PLUGIN_PREV_TAG:-<none>}: tag ${PLUGIN_TAG_SHA:0:8}, the commit that set it."
    if [ "$PLUGIN_AFTER_COUNT" -gt 0 ]; then
      echo "The plugin changed after that commit, so the next release needs a bump PR:"
      git log --no-merges --format='  %h %s' "${PLUGIN_TAG_SHA}..${RELEASE_SHA}" -- "$PLUGIN_DIR/"
    fi
    ;;
  bump)
    echo "PLUGIN_CHANGED_COMMITS=${PLUGIN_COUNT}"
    echo "PLUGIN_NEXT_PATCH=$(bump "$PLUGIN_FILE_VER" patch)"
    echo "PLUGIN_NEXT_MINOR=$(bump "$PLUGIN_FILE_VER" minor)"
    echo
    echo "Commits touching ${PLUGIN_DIR}/ (${PLUGIN_PREV_TAG}..origin/main):"
    git log --no-merges --format='  %h %s' "${PLUGIN_PREV_TAG}..${RELEASE_SHA}" -- "$PLUGIN_DIR/"
    ;;
  none)
    echo
    echo "No plugin changes since ${PLUGIN_PREV_TAG:-<none>}."
    ;;
esac
echo

# Everything the kit build reads (web/html-kit/build.mjs). html_kit_release.yml lists the same paths for
# its release notes.
KIT_PATHS=(web/html-kit/ ':(exclude)web/html-kit/mapping.md' ':(exclude)web/html-kit/README.md'
  web/styles/ web/assets/ web/llms.txt guidelines/ swiftui/Sources/Lemonade/Resources/Fonts/)
KIT_PREV_TAG=$(latest_tag 'lemonade-html-kit-*')
KIT_PREV_VER=${KIT_PREV_TAG#lemonade-html-kit-}
if [ -z "$KIT_PREV_TAG" ]; then
  KIT_ACTION=first
  KIT_COUNT=0
else
  KIT_COUNT=$(shipped_count "$KIT_PREV_TAG" "${KIT_PATHS[@]}")
  [ "$KIT_COUNT" -gt 0 ] && KIT_ACTION=release || KIT_ACTION=none
fi

echo "## KIT (HTML kit)"
echo "KIT_PREV_TAG=${KIT_PREV_TAG:-<none>}"
echo "KIT_ACTION=${KIT_ACTION}"
case "$KIT_ACTION" in
  first)
    echo
    echo "No kit has been released. Ask for the first version; the release waits for scripts/check-html-kit-bundle.sh to pass."
    ;;
  release)
    echo "KIT_SHIPPED_COMMITS=${KIT_COUNT}"
    echo "KIT_NEXT_PATCH=$(bump "$KIT_PREV_VER" patch)"
    echo "KIT_NEXT_MINOR=$(bump "$KIT_PREV_VER" minor)"
    echo
    echo "Commits touching the kit (${KIT_PREV_TAG}..origin/main):"
    git log --no-merges --format='  %h %s' "${KIT_PREV_TAG}..${RELEASE_SHA}" -- "${KIT_PATHS[@]}"
    ;;
  none)
    echo
    echo "No kit changes since ${KIT_PREV_TAG}."
    ;;
esac
echo

echo "## SUGGESTION (rule: one side -> patch, both sides -> minor)"
if [ "$KMP_SUGGEST" != none ]; then
  echo "  KMP     ${KMP_PREV_VER} -> $(bump "$KMP_PREV_VER" "$KMP_SUGGEST")   (${KMP_SUGGEST})   tag: lemonade-kmp-$(bump "$KMP_PREV_VER" "$KMP_SUGGEST")"
else
  echo "  KMP     no shipped changes -> skip"
fi
if [ "$SWIFT_SUGGEST" != none ]; then
  V=$(bump "$SWIFT_PREV_VER" "$SWIFT_SUGGEST")
  echo "  SwiftUI ${SWIFT_PREV_VER} -> ${V}   (${SWIFT_SUGGEST})   tags: ${V} + lemonade-swiftui-${V}"
else
  echo "  SwiftUI no shipped changes -> skip"
fi
case "$PLUGIN_ACTION" in
  tag)  echo "  Plugin  tag lemonade-specialist--v${PLUGIN_FILE_VER} at ${PLUGIN_TAG_SHA:0:8}$([ "$PLUGIN_AFTER_COUNT" -gt 0 ] && echo ", then open a bump PR for the ${PLUGIN_AFTER_COUNT} later commit(s)")" ;;
  bump) echo "  Plugin  ${PLUGIN_FILE_VER} -> $(bump "$PLUGIN_FILE_VER" patch)   (patch)   via a version bump PR" ;;
  none) echo "  Plugin  no changes -> skip" ;;
esac
case "$KIT_ACTION" in
  first)   echo "  Kit     first release -> ask for the version   tag: lemonade-html-kit-X.Y.Z" ;;
  release) echo "  Kit     ${KIT_PREV_VER} -> $(bump "$KIT_PREV_VER" patch)   (patch)   tag: lemonade-html-kit-$(bump "$KIT_PREV_VER" patch)" ;;
  none)    echo "  Kit     no changes -> skip" ;;
esac
echo
echo "Note: suggestion only. Confirm exact versions with the user before tagging."
