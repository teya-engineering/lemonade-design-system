#!/usr/bin/env bash
# Generate platform token code (KMP + SwiftUI) from tokens/*.tokens.json.
#
# Usage (run from the repo root):
#   .claude/skills/generate-tokens/scripts/run-converters.sh [--changed | --all | <file.tokens.json>...]
#
#   --changed   (default) run converters only for tokens/*.tokens.json changed vs HEAD
#   --all       run every converter
#   <file>...   run converters for the named token files (e.g. theme-colors.light.tokens.json radius.tokens.json)
#
# Requires Kotlin 2.3.20 — Homebrew's 2.4.0 crashes the .main.kts converters with
# a FIR compiler error. This script installs 2.3.20 into ~/.local on first run
# and always invokes it explicitly, ignoring whatever `kotlin` is on PATH.
set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"
SKILL_DIR="$REPO_ROOT/.claude/skills/generate-tokens/scripts"

KOTLIN_VERSION="2.3.20"
KOTLIN_HOME="$HOME/.local/kotlin-$KOTLIN_VERSION/kotlinc"
KOTLIN_BIN="$KOTLIN_HOME/bin/kotlin"

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

# token file -> space-separated converter basenames (no extension).
# Flutter converters are intentionally excluded — this repo generates KMP + SwiftUI + web.
#
# The web converters are consolidated into a single tokens.css artifact rather than
# one converter per category, so every token file maps to the same three web
# converters: web-token-converter and web-llms-txt-converter must run before
# web-css-bundle, which concatenates their output. Each web converter reads all of
# tokens/*.tokens.json itself and is idempotent, so listing them under every case
# below is correct even though multiple changed files re-run them.
converters_for() {
  case "$1" in
    primitive-colors.tokens.json) echo "kmp-color-token-converter swiftui-color-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    theme-colors.light.tokens.json|theme-colors.dark.tokens.json)
                                  echo "kmp-theme-token-converter swiftui-theme-token-converter swiftui-color-assets-generator web-token-converter web-llms-txt-converter web-css-bundle" ;;
    radius.tokens.json)           echo "kmp-radius-token-converter swiftui-radius-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    spacing.tokens.json)          echo "kmp-spacing-token-converter swiftui-spacing-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    size.tokens.json)             echo "kmp-dimension-token-converter swiftui-size-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    opacity.tokens.json)          echo "kmp-opacity-token-converter swiftui-opacity-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    border-width.tokens.json)     echo "kmp-border-width-token-converter swiftui-border-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    shadow.tokens.json)           echo "kmp-shadow-token-converter swiftui-shadow-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    typography.tokens.json)       echo "kmp-typography-token-converter swiftui-typography-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
    *) echo "" ;;
  esac
}

ALL_FILES="primitive-colors.tokens.json theme-colors.light.tokens.json radius.tokens.json spacing.tokens.json size.tokens.json opacity.tokens.json border-width.tokens.json shadow.tokens.json typography.tokens.json"

# Resolve the target token files from args.
mode="${1:---changed}"
files=()
case "$mode" in
  --all)     files=($ALL_FILES) ;;
  --changed) while IFS= read -r f; do files+=("$(basename "$f")"); done \
               < <(git diff --name-only HEAD -- 'tokens/*.tokens.json'; git diff --cached --name-only -- 'tokens/*.tokens.json') ;;
  *)         for a in "$@"; do files+=("$(basename "$a")"); done ;;
esac

# de-dupe. Guard the empty case: under `set -u` an empty array makes
# "${files[@]}" an unbound-variable error, so --changed with no token
# change would abort here instead of reaching the friendly message below.
if [ "${#files[@]}" -gt 0 ]; then
  files=($(printf '%s\n' "${files[@]}" | sort -u))
fi

if [ "${#files[@]}" -eq 0 ]; then
  echo "No token files to process. Pass file names, --all, or change a tokens/*.tokens.json first."
  exit 0
fi

ensure_kotlin

# A converter's compiled script is cached, and editing an @file:Import'ed loader
# does NOT invalidate it — the converter would silently run the previous loader's
# code and produce stale output that still looks freshly generated. Clear it.
rm -rf "$HOME/Library/Caches/main.kts.compiled.cache" "$HOME/.cache/main.kts.compiled.cache"

echo "==> Token files: ${files[*]}"

# Run each converter.
ran=0
for f in "${files[@]}"; do
  convs="$(converters_for "$f")"
  if [ -z "$convs" ]; then
    echo "!! No converter mapping for '$f' — skipping"
    continue
  fi
  for c in $convs; do
    echo "==> $c   (tokens/$f)"
    "$KOTLIN_BIN" "scripts/$c.main.kts"
    ran=$((ran + 1))
  done
done

echo "==> Done. Ran $ran converter(s)."
echo "==> Review generated changes:"
git status --short | grep -vE 'tokens/.*\.json' || true
