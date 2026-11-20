---
name: generate-tokens
description: >
  Regenerate the Lemonade platform token code (KMP + SwiftUI) from the Figma token
  exports in `tokens/*.tokens.json`. Use when a `tokens/*.tokens.json` file changes
  (theme-colors, radius, spacing, size, opacity, border-width, shadow, typography,
  primitive-colors) and the generated Kotlin / Swift needs to be rebuilt, or when
  the user asks to "generate tokens", "run the token converters", or "sync tokens
  to code".
---

# Generate Lemonade tokens

Design tokens are authored in Figma and exported as JSON into `tokens/`. A set of
Kotlin script converters in `scripts/*-token-converter.main.kts` read those JSON
files and (over)write the generated source for each platform. This skill runs the
right converters for whatever token files changed.

## TL;DR

Export from Figma, unpack the zips into `tokens/` with the **unpack-figma-tokens** skill, then:

```bash
# From the repo root — regenerate only what changed vs HEAD
.claude/skills/generate-tokens/scripts/run-converters.sh --changed
```

## Getting the tokens out of Figma

In the Foundations file's variables panel, right-click a collection and choose **Export modes**,
the native export, not a plugin. Each collection exports as its own zip, including **Primitive Colors**, the ramp every theme colour aliases into. The
**unpack-figma-tokens** skill (`.claude/skills/unpack-figma-tokens/scripts/unpack-tokens.py`) writes
each mode file under its `tokens/` name, adds the trailing newline Figma omits, and refuses a
renamed or unknown collection or mode. It owns the collection-to-file table.

The primitives are hidden from publishing in Figma so designers bind to Theme
colours, and the export marks each one with `com.figma.hiddenFromPublishing`.
The primitive converters read them with `includeHidden = true`; every other
converter drops hidden tokens.

The themed colour layer lives inside the **Theme** collection under a `Themed/` group, not in a
collection of its own — one collection carries the Light/Dark modes for the whole file. So the
themed tokens arrive in the same two `Theme` files, and the converters split them: the theme
converters skip everything under `Themed/`, and the themed converters keep only it.

## What happens next

`run-converters.sh --changed` regenerates from whatever changed against `HEAD`.
Then review the diff and commit. That is the routine flow for the common case —
a token *value* changed and the generated code needs rebuilding.
`verify-generated.sh` is a separate, narrower tool; see below for when to reach
for it.

## The hard requirement (it bites silently)

**Kotlin 2.3.20 — NOT Homebrew's 2.4.0.** The `.main.kts` converters fail to
compile on Kotlin 2.4.0 with:
`Expected FirResolvedTypeRef with ConeKotlinType but was FirUserTypeRefImpl`.
The runner installs 2.3.20 into `~/.local/kotlin-2.3.20` on first use and
always calls it by absolute path, ignoring whatever `kotlin` is on `PATH`. If
you run a converter by hand, invoke
`~/.local/kotlin-2.3.20/kotlinc/bin/kotlin scripts/<name>.main.kts` from the
repo root — never bare `kotlin`.

## Why the pipeline looks like this

[`figma-native-migration-design.md`](figma-native-migration-design.md) records the
reasoning behind the parts that look arbitrary — **read it before changing a sort,
a tie-break, or the permitted-reorder list in `verify-generated.sh`.** The short
version of the two that bite:

- **The canonical sort in each loader is load-bearing, not tidiness.** `org.json`'s
  `JSONObject` is backed by a `HashMap`, so key iteration is *not* file order.
  Remove the sort and the generated SDK becomes non-deterministic between runs.
- **`LemonadeFontWeights` sorts by weight descending on purpose.** Its entry order
  sets every ordinal, so it is a public ABI contract. The token carries only a
  style name, so one mapping is the source of truth for both the emitted value and
  the sort key.

## How the converters work

- Each converter is a standalone `kotlin` script run **from the repo root** — it
  reads `tokens/<x>.tokens.json` via a relative path and writes generated source
  into the platform module. Running from any other directory silently
  reads/writes nothing useful.
- Converters overwrite their output files wholesale (each carries a
  "DO NOT MODIFY THIS FILE MANUALLY" banner). Never hand-edit generated files.
- `primitive-colors.tokens.json` did **not** change? Skip its converters — they're
  only needed when the raw color ramp changes, not for semantic (`theme-colors`)
  edits.

## Token file → converter map

| `tokens/` file                                                   | KMP                               | SwiftUI                                             |
|-------------------------------------------------------------------|----------------------------------|-----------------------------------------------------|
| `primitive-colors.tokens.json`                                     | `kmp-color`                      | `swiftui-color`                                     |
| `theme-colors.light.tokens.json` / `theme-colors.dark.tokens.json` | `kmp-theme` + `kmp-themed`       | `swiftui-theme` + `swiftui-color-assets-generator` + `swiftui-themed` + `swiftui-themed-assets-generator` |
| `radius.tokens.json`                                               | `kmp-radius`                     | `swiftui-radius`                                    |
| `spacing.tokens.json`                                              | `kmp-spacing`                    | `swiftui-spacing`                                   |
| `size.tokens.json`                                                 | `kmp-dimension`                  | `swiftui-size`                                      |
| `opacity.tokens.json`                                              | `kmp-opacity`                    | `swiftui-opacity`                                   |
| `border-width.tokens.json`                                         | `kmp-border-width`               | `swiftui-border`                                    |
| `shadow.tokens.json`                                               | `kmp-shadow`                     | `swiftui-shadow`                                    |
| `typography.tokens.json`                                           | `kmp-typography`                 | `swiftui-typography`                                |

(Converter names above omit the `-token-converter.main.kts` suffix, except
`swiftui-color-assets-generator.main.kts` and `swiftui-themed-assets-generator.main.kts`.)

## When to run verify-generated.sh

`verify-generated.sh` is **not** a routine step after a normal token edit — an
intentional token *value* change is expected to fail it, every time, because
the new value is (correctly) not byte-identical to what's on `origin/main`.
Running it there and seeing `FAIL … (must be byte-identical)` is not a
problem to chase down; it is the harness confirming the value actually
changed.

It exists for the opposite case: a change that is **not supposed to alter any
generated output** — a converter refactor, a loader edit, a format migration,
reordering cleanup. For those, run it against the appropriate ref (default
`origin/main`, or a specific commit/tag when comparing against a known-good
snapshot) and expect `PASS: only permitted reordering found`. Anything else
means the "no-op" change actually moved something consumer-visible.

## After generating

- `git status` / `git diff` the generated sources. A semantic color change
  typically touches `LemonadeSemanticColors.{kt,swift}`, the theme classes
  (`LemonadeLightTheme.kt` / `LemonadeDarkTheme.kt`,
  `LemonadeAdaptiveTheme.swift`), and the SwiftUI `Assets.xcassets/Colors` +
  `Color+Lemonade.swift`.
- <a id="binary-compatibility"></a>**Binary compatibility:** adding new public
  token symbols is additive (safe), but renaming/removing one is an ABI break.
  If public API may have shifted, run the classifier from `kmp/` and follow the
  **binary-compatibility** skill:
  `.claude/skills/binary-compatibility/scripts/bcv-check.sh --ci`.
- Commit the cleaned `tokens/*.tokens.json` alongside the regenerated source in
  the same change.
