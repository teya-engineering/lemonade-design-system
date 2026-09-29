#!/usr/bin/env bash
# Compare generated platform code in the working tree against a git ref.
#
# Usage (from the repo root):
#   .claude/skills/generate-tokens/scripts/verify-generated.sh [<git-ref>]
#
# Colour source files are allowed to differ ONLY by line order — the token
# loader imposes a canonical sort that differs from the old plugin export's
# arbitrary array order. Everything else, including every .colorset, must be
# byte-identical: that is where the public enums live, and enum entry order
# is consumer-visible.
set -euo pipefail

REF="${1:-origin/main}"
cd "$(git rev-parse --show-toplevel)"

# Files permitted to differ by ordering alone.
reorderable() {
  case "$1" in
    */LemonadePrimitiveColors.kt|*/LemonadePrimitiveColors.swift) return 0 ;;
    */LemonadeLightTheme.kt|*/LemonadeDarkTheme.kt) return 0 ;;
    */LemonadeSemanticColors.kt|*/LemonadeSemanticColors.swift) return 0 ;;
    */LemonadeAdaptiveTheme.swift|*/Color+Lemonade.swift) return 0 ;;
    # Emits only independent scalar constants and interface/protocol members —
    # no enum, no ordered collection — so declaration order is not consumer-visible.
    */LemonadeBorderWidthExtension.kt) return 0 ;;
    */LemonadeBorderWidth.swift) return 0 ;;
    *) return 1 ;;
  esac
}

# Color+Lemonade.swift's `/// Usage:` examples name each namespace's first member,
# so they track ordering by construction and change whenever members reorder.
# Compare its code lines only; the guarantee on every non-comment line is unchanged.
doc_comment_volatile() {
  case "$1" in
    */Color+Lemonade.swift) return 0 ;;
    *) return 1 ;;
  esac
}

# Strips `///` doc-comment lines for files where doc_comment_volatile() applies;
# otherwise passes the stream through unchanged.
strip_volatile_docs() {
  if doc_comment_volatile "$1"; then
    grep -v '^[[:space:]]*///'
  else
    cat
  fi
}

if ! changed="$(git diff --name-only "$REF" -- kmp/ swiftui/)"; then
  echo "FAIL: could not diff against $REF (bad or unreachable ref?)"
  exit 1
fi

# New generated files that were never staged/committed don't show up in
# `git diff` at all — catch them separately so a converter emitting a
# brand-new file can't silently slip past the harness. Check the git exit
# code before the grep, so a git-status failure (lock contention, corrupt
# or inaccessible .git, permission/disk errors) FAILs loudly instead of
# looking identical to "zero untracked files" once it hits grep.
if ! status_out="$(git status --porcelain -- kmp/ swiftui/)"; then
  echo "FAIL: could not read git status"
  exit 1
fi
untracked=""
if [ -n "$status_out" ]; then
  untracked="$(printf '%s\n' "$status_out" | grep '^??' | cut -c4- || true)"
fi

if [ -z "$changed" ] && [ -z "$untracked" ]; then
  echo "PASS: generated output is byte-identical to $REF"
  exit 0
fi

fail=0

while IFS= read -r f; do
  [ -z "$f" ] && continue
  echo "FAIL  $f (untracked new file, not compared against $REF)"
  fail=1
done <<< "$untracked"

while IFS= read -r f; do
  [ -z "$f" ] && continue
  if [ ! -f "$f" ]; then
    echo "FAIL  $f (deleted or missing in working tree)"
    fail=1
    continue
  fi
  if reorderable "$f"; then
    if diff -q <(git show "$REF:$f" | strip_volatile_docs "$f" | sort) <(strip_volatile_docs "$f" < "$f" | sort) >/dev/null 2>&1; then
      if doc_comment_volatile "$f"; then
        echo "ok    $f (reordered only, doc comments excluded)"
      else
        echo "ok    $f (reordered only)"
      fi
    else
      echo "FAIL  $f (content changed, not just order)"
      diff_out="$(diff <(git show "$REF:$f" | strip_volatile_docs "$f" | sort) <(strip_volatile_docs "$f" < "$f" | sort) || true)"
      printf '%s\n' "$diff_out" | head -20
      fail=1
    fi
  else
    echo "FAIL  $f (must be byte-identical)"
    git diff --stat "$REF" -- "$f"
    fail=1
  fi
done <<< "$changed"

if [ "$fail" -eq 0 ]; then
  echo "PASS: only permitted reordering found"
else
  echo "FAIL: generated output diverged from $REF"
fi
exit "$fail"
