# Migrating the token export from the Figma plugin to Figma's native export

**Date:** 2026-08-19
**Status:** Implemented and merged — this is the token pipeline in use today

> One part of this design did not survive to merge: the `ingest-tokens.sh` script
> described under [Ingest](#ingest) was removed in favour of a documented manual
> copy. Everything else describes the pipeline as it currently works.
>
> The operational guide is [`SKILL.md`](SKILL.md). This document is the record of
> *why* — read it before changing a sort, a tie-break, or the permitted-reorder
> list in `verify-generated.sh`.

## Problem

Design tokens are authored in Figma and exported into `tokens/*.json` by a
third-party community plugin, [Export/import variables][plugin]. Kotlin script
converters in `scripts/*-token-converter.main.kts` read those files and generate
the Kotlin, Swift and Dart sources that consumers of the SDK compile against.

[plugin]: https://www.figma.com/community/plugin/1256972111705530093

The plugin is a liability. It carries bugs of its own — every re-export injects a
phantom mode id (historically `3932:0`) into `theme-colors.json`, which a
`strip-stray-modes.py` workaround in the converter runner had to strip before
conversion. It also has to keep pace with Figma's own changes, and there is no
guarantee it will.

Figma now ships a native variable export producing [DTCG][dtcg]-format files.
Moving to it removes the third-party dependency and the class of bugs that comes
with it.

[dtcg]: https://www.designtokens.org/tr/drafts/format/

## Goals

1. `tokens/` is populated by Figma's native export. The plugin is retired.
2. **No change to anything consumers compile against.** Public API, token values,
   enum ordinals and asset contents are all preserved.
3. The generated-code diff on the migration is empty, and provably so.

## Non-goals

- Rewriting the converters, or changing the language they are written in.
- Automating the export (Figma's REST variables endpoint is Enterprise-only;
  the manual download ritual is unchanged).
- Picking up the two drifted shadow values. See
  [Baseline drift](#baseline-drift).
- Fixing the orphaned `.colorset` issue. See [Follow-ups](#follow-ups).

## Findings

The two formats were compared field by field across all nine collections. Both
exports were reduced to a common shape — `{name, resolvedValue, aliasName,
hiddenFromPublishing}` per token, with DTCG aliases resolved — and diffed.

The baseline is `origin/main` at `b32247e`, which added the ten Voice/featured
theme tokens.

| Collection | Tokens | Missing | Extra | Value diffs | Alias diffs | Hidden diffs |
|---|---|---|---|---|---|---|
| `border-width` | 8 | 0 | 0 | 0 | 0 | 0 |
| `opacity` | 14 | 0 | 0 | 0 | 0 | 0 |
| `radius` | 15 | 0 | 0 | 0 | 0 | 0 |
| `shadow` | 45 | 0 | 0 | **2** | 0 | 0 |
| `size` | 27 | 0 | 0 | 0 | 0 | 0 |
| `spacing` | 15 | 0 | 0 | 0 | 0 | 0 |
| `typography` | 38 | 0 | 0 | 0 | 0 | 0 |
| `theme-colors` (×2 modes) | 151 | 0 | 0 | 0 | 0 | 0 |
| `primitive-colors` | 396 | 0 | 0 | 0 | 0 | 0 |

709 tokens, and the *only* discrepancy in the entire export is two shadow
numbers — real Figma drift, not a format artifact. See
[Baseline drift](#baseline-drift).

Field-level mapping, all verified against the real exports:

| Plugin | Native DTCG | Notes |
|---|---|---|
| `variables[].name` | nested group path + leaf key | `Content/Brand/content-accent` reconstructs exactly |
| `resolvedValue.{r,g,b,a}` | `$value.components[]` + `$value.alpha` | bit-identical floats |
| `resolvedValue` (number/string) | `$value` | direct |
| `aliasName` | `$extensions."com.figma.aliasData".targetVariableName` | 151/151 identical |
| `alias` | `$extensions."com.figma.aliasData".targetVariableId` | direct |
| `hiddenFromPublishing` | `$extensions."com.figma.hiddenFromPublishing"` | present on all 4 hidden theme vars |
| `description` | `$description` | 151/151 identical |
| `scopes` | `$extensions."com.figma.scopes"` | 151/151 as sets; `STROKE_COLOR` renamed `STROKE`. No converter reads scopes. |
| `codeSyntax` | `$extensions."com.figma.codeSyntax"` | 151/151 on theme; older elsewhere — see [Metadata freshness](#metadata-freshness). No converter reads codeSyntax. |
| `modes` map | `$extensions."com.figma.modeName"` (file-level) | one file per mode |
| collection `id` / `name` / `variableIds` | *absent* | unused by any converter |

Three behaviours of the native format that the design has to accommodate:

1. **Aliases are not uniformly resolved.** A variable aliasing another variable
   *in the same collection* is emitted as an unresolved DTCG reference —
   `"$value": "{base.border-50}"` — with no `aliasData`. A variable aliasing a
   *remote library* is emitted fully resolved, plus `aliasData`. There are 5
   local references in the entire export: 2 in `border-width`, 2 in `opacity`,
   1 in `radius`. All 5 resolve to values matching the plugin exactly.
2. **Filenames are not a contract.** The `Size` collection exports as
   `sizing.tokens.json`; the `.Shadow` collection exports as
   `shadow.tokens.json`; theme modes export as `light.tokens.json` /
   `dark.tokens.json`. Names track whatever designers call things in Figma.
3. **Only local variables are exported.** The primitive `Colors` collection
   lives in a separate Figma library file (every theme alias resolves to
   `targetVariableSetName: "Colors"` with a remote-file hash in its id), so it
   needs its own export run. This matches the existing ritual.

The theme→primitives linkage was checked directly, since
`kmp-theme-token-converter` maps each semantic token onto a primitive property
by `aliasName`: **296/296 alias targets across light and dark resolve into
`primitive-colors.tokens.json`**, including nested ones like `neutral/alpha/900`.
Three tokens per mode (`bg-transparent`, `bg-transparent-light`,
`bg-transparent-dark`) carry no alias in either export; the converter already
skips blank-alias tokens.

### Metadata freshness

`theme-colors` matches the committed baseline on every metadata field — 151/151
on `description`, `scopes` and `codeSyntax`, and 0 hidden-flag drift.

The non-theme collections do differ on metadata, but in every case the *export*
is newer than the committed file, which was last written by the plugin some time
ago:

| Collection | Field | Committed | Export |
|---|---|---|---|
| `border-width`, `spacing`, `size` | `codeSyntax` iOS | `context.lemonade.spaces.spacing0` | `LemonadeTheme.spaces.spacing0` |
| `spacing` | `description` | empty | `"0px"`, `"4px"`, … |

No converter reads `codeSyntax`, `description` or `scopes`, so none of this can
reach generated output. It will appear in the `tokens/` diff on migration as a
one-time metadata refresh, and should be described as such in the PR rather than
mistaken for drift.

One genuine defect surfaces from this: `radius-350` carries a **typo in its
Figma code syntax** — `LemonadeTheme.r adius.radius350`, with a space inside
`radius`. It is the only such case. Worth fixing at source in Figma; it has no
effect on anything generated.

## Design

### File layout

`tokens/` holds the raw native export, flat, with the DTCG extension preserved
so the format is evident from the name:

```
tokens/border-width.tokens.json      tokens/spacing.tokens.json
tokens/opacity.tokens.json           tokens/theme-colors.light.tokens.json
tokens/primitive-colors.tokens.json  tokens/theme-colors.dark.tokens.json
tokens/radius.tokens.json            tokens/typography.tokens.json
tokens/shadow.tokens.json            tokens/size.tokens.json
```

Flat rather than nested: `run-converters.sh --changed` globs `tokens/*.json` and
`converters_for()` keys off exact filenames, and a subdirectory breaks both. The
glob becomes `tokens/*.tokens.json`; `converters_for()` gains an entry mapping
both theme files to the theme converters.

The old `tokens/*.json` files are **deleted**, not overwritten, so a stale
plugin-format file can never be silently picked up by the glob. Git history is
the rollback: a revert restores tokens and generated code atomically.

### The loader shim

Format translation lives in exactly one place per platform — the shared resource
loaders, `kmp-resource-file-loading.main.kts`,
`swiftui-resource-file-loading.main.kts`, and
`flutter-resource-file-loading.main.kts`.

Those loaders expose `readFileResourceFile` / `readFileResourceFileByMode`, which
walk `variables[]` and hand each converter the `resolvedValuesByMode[mode]` JSON
object. Downstream converters then read `resolvedValue` and `aliasName` off it.

Of the nineteen KMP and SwiftUI converters, only three read the file format
directly — `kmp-theme-token-converter`, `swiftui-theme-token-converter` and
`swiftui-color-assets-generator`, all of which enumerate the `modes` map. The
other sixteen see nothing beyond the object the loader hands them. All nine
Flutter converters go through their loader without exception.

So each loader gains a private `dtcgToResolvedValue(token) -> JSONObject` that
synthesizes the object the converters already expect:

| DTCG input | Synthesized |
|---|---|
| `$type: "color"` → `$value.components[]`, `$value.alpha` | `{"resolvedValue": {r,g,b,a}}` |
| `$type: "number"` → `$value` | `{"resolvedValue": <number>}` |
| `$type: "string"` → `$value` | `{"resolvedValue": "<string>"}` |
| `$extensions."com.figma.aliasData"` | `{"alias": …, "aliasName": …}` |

Plus:

- **Group-path reconstruction.** Recursive descent over the nested objects,
  joining keys with `/`; a node is a leaf when it has `$type`. Keys beginning
  with `$` are skipped (the file-level `$extensions` block is a sibling of the
  top-level groups). This reproduces `variables[].name` exactly, so
  `sanitizedGroups()`, `sanitizedValueName()` and `sanitizedClassName()` are
  unchanged.
- **Local alias resolution.** When `$value` is a string of the form `{a.b.c}`,
  translate `.` to `/` and look the target up in the same file, recursively.
  Fail loudly on an unresolvable reference rather than emitting a default.
- **Hidden filter.** Reads `$extensions."com.figma.hiddenFromPublishing"`
  instead of the top-level `hiddenFromPublishing`. Absent means false.

### Mode identity

`readFileResourceFileByMode(file, modeKey)` becomes
`readFileResourceFileByMode(files, modeName)`. The mode name is read from each
file's `$extensions."com.figma.modeName"` — never inferred from the filename.
Single-mode collections report `"Default"`.

Three converters enumerate modes today and switch to enumerating files:
`kmp-theme-token-converter`, `swiftui-theme-token-converter`, and
`swiftui-color-assets-generator` (whose `parseThemeColors(file, modeKey)` becomes
`parseThemeColors(file)`, called once per mode file — everything downstream of it
already works off a plain `Map<String, ColorValue>`).

### Ordering

Six of the nine collections already sort their own output
(`sortedBy { it.value.radiusValue }` and equivalents in radius, spacing, size,
opacity, border-width, and the numeric half of typography), so input order cannot
reach them. Shadow sorts groups explicitly via `shadowGroupOrder`.

Three code paths are input-order dependent. Two are harmless and one is not:

- **Colour converters** (`kmp-color`, `swiftui-color`, both theme converters,
  the assets generator) emit only independent scalar constants — no enums, no
  arrays, no ordinals. Reordering is cosmetic. The current order is in fact
  scrambled (`yellowLime900, 600, 400, 300, 200, 800, 100, …`) because it
  inherits the plugin's arbitrary array order; the native export is ramp-ordered.
- **Shadow levels.** `resources.groupBy { groups[2] }` preserves encounter
  order, and layer order within a `sequenceOf` is client-visible rendering
  behaviour. Verified identical under both exports (`level-1, level-2` in every
  group), but it is incidental rather than guaranteed.
- **`LemonadeFontWeights`.** `fontWeightResources` is a bare `.filter {}` with
  no sort, and it feeds a **public enum**. The committed order is
  `bold, semibold, medium, regular`; the native export emits
  `regular, medium, semibold, bold`. Adopting that unsorted would flip every
  ordinal (`Bold` 0→3), change `entries`/`values()` iteration, move the Swift
  cases, and alter the klib dump. This is a genuine consumer-visible break.

**Decision: no ordering manifest. Add explicit sorts where order matters.**

- `fontWeightResources` gains an explicit sort. Note the token itself carries no
  numeric weight — `$value` is the string `"Bold"` / `"SemiBold"` / `"Medium"` /
  `"Regular"`, and the `700/600/500/400` in the generated enum comes from a
  `when (resource.value.stringValue)` mapping inside
  `buildFontWeightsDefinitionCode`. That mapping is therefore extracted into a
  small shared helper and used both to emit the value and to sort descending, so
  the two cannot diverge. This reproduces `bold, semibold, medium, regular`
  *by construction* and closes a latent hole: today those ordinals depend on the
  order a third-party plugin happened to emit, and a designer reordering rows in
  Figma could have silently rewritten them at any point.
- Shadow levels gain an explicit sort by level name, making the current
  behaviour guaranteed rather than incidental.
- Colour converters sort deterministically by group and ramp, and the resulting
  one-time reshuffle is accepted as an improvement.

A stateful `token-order.json` manifest was considered and rejected: it would
freeze the plugin's arbitrary ordering permanently, leave the font-weight
fragility unfixed, and add machinery whose only purpose is to make one PR's diff
empty.

### Ingest

**Superseded — see the note at the top of this document.** The design called for
an `ingest-tokens.sh` that populated `tokens/` from a downloaded export, routing
each incoming file by `com.figma.variableId` set overlap rather than by filename.
It was built, and it worked: all ten files routed at an overlap of **1.000**
against the `main` baseline with a runner-up of **0.000** in every case —
including `light` / `dark` → `theme-colors`, and `sizing` → `size` where the
names disagree. Figma variable ids are stable across both exporters and disjoint
between collections, so every file identified its destination unambiguously.

It was removed before merge in favour of a documented manual copy. Two things
made the trade reasonable: the real export is a zip of per-collection folders
whose files are named after the *mode*, so most arrive as `Default.tokens.json`
and a rename is unavoidable either way; and the guards it provided are largely
covered downstream — the loaders fail loudly on a malformed export, the
converters exit non-zero, `apiCheck` catches a truncated collection as a
breaking change, and the Token Drift CI job catches a forgotten regeneration.

The gap that remains silent is a **renamed Figma mode**: copying `Day` in
without deleting `light` leaves the converters generating a new public theme
class alongside a stale one, with no error. `SKILL.md` calls this out.

The routing-by-variable-id idea is recorded here because it is the right answer
if this is ever revisited — filenames are not a contract, but variable ids are.

### Platform scope

Flutter's converters are migrated too. Only one Flutter script touches the raw
format — `flutter-resource-file-loading.main.kts` — and all nine converters go
through it, so it is the same shim in a third file. Flutter is not dead code: its
token output was regenerated as recently as commit `76fb481` (2026-06-30), and
`.github/workflows/flutter_release.yml` publishes it on `lemonade-flutter-*` tags
(last release `v0.8.3`, 2026-03-31). Leaving its loader on a format that no
longer exists would break it silently. The `generate-tokens` skill's policy of
not running the Flutter converters by default is unchanged.

Files touching the raw format, and therefore changing:

| Platform | Files |
|---|---|
| KMP | `kmp-resource-file-loading`, `kmp-theme-token-converter` |
| SwiftUI | `swiftui-resource-file-loading`, `swiftui-theme-token-converter`, `swiftui-color-assets-generator` |
| Flutter | `flutter-resource-file-loading` |
| Ordering | `kmp-typography-token-converter`, `swiftui-typography-token-converter`, both shadow converters, the colour converters |

The remaining converters change by zero lines.

### Removals

- `.claude/skills/generate-tokens/scripts/strip-stray-modes.py` and its
  invocation in `run-converters.sh`. The native format has no `modes` map, so
  the `3932:0` phantom-mode bug has no surface on which to exist.
- The corresponding "two hard requirements" section of the `generate-tokens`
  skill, reduced to the Kotlin 2.3.20 requirement alone.

### CI

At design time `kmp_ci.yml` ran detekt, ktlint, the dependency allowlist,
`apiCheck` and the API Stability Review, and no workflow ran the token converters.
Nothing verified that committed generated code corresponded to committed tokens: a
forgotten regeneration or a hand-edited generated file passed CI silently, and
`apiCheck` compares signatures only, so a changed colour value was invisible to it.

A **Token Drift** job closes that gap — `.github/workflows/token_drift.yml`. It is
gated on changes to `tokens/**`, the token scripts and this skill directory; it
regenerates and fails if `kmp/` or `swiftui/` is dirty afterwards. This makes the
migration's central claim — the input format changed and the output did not — a
permanently enforced invariant, and it catches a bad ingest, a stale regeneration,
and hand-edited generated files.

Gating on changed paths keeps the cost proportionate: the `.main.kts` scripts
compile before running, so the job is slow enough that it should not run on every
PR.

## Verification

The acceptance criterion is mechanical, not editorial.

1. Regenerate with the **current** pipeline and confirm `kmp/`, `swiftui/` and
   `flutter/` are clean. This proves the baseline is reproducible before
   anything changes.
2. Trim the native export to the committed baseline by restoring the two
   drifted shadow values (`sd-xs-lv1-blur` to 2, `sd-xs-lv1-offset-y` to 1).
   Two numbers, stated in the PR description so a reviewer can reproduce it.
3. Run the new pipeline against the trimmed export. Then:

| Surface | Criterion |
|---|---|
| Every non-colour generated file | **byte-identical** — all enums live here |
| All 137 `.colorset` directories | **byte-identical** — per-token files, immune to reordering |
| Colour source files | **sorted-line-identical** — catches any changed value, name or count; permits only reordering |
| `bcv-check.sh --ci` | `NO_CHANGES` |

Byte-identity is required precisely where consumer-visible ordering exists
(`LemonadeFontWeights`, `LemonadeRadius`, `LemonadeSizes`, `LemonadeSpaces`,
`LemonadeFontSizes`, `LemonadeLineHeights`, `LemonadeShadowsSequence`) and
relaxed only for the colour files, which emit independent scalars.

If step 3 fails, the diff itself names the defect.

## Baseline drift

Work must start from `origin/main` at `b32247e` or later — that commit added the
ten Voice/featured tokens, and against it the export shows no missing or extra
tokens at all.

Against that baseline the export differs in exactly **two values**:
`sd-xs-lv1-blur` 2 → 1 and `sd-xs-lv1-offset-y` 1 → 0.5. Notably these halve
exactly the values behind the known "KMP shadows render ~2× darker than Figma"
problem, so this looks like an intentional Figma-side fix rather than noise.

Everything else — all 709 tokens across nine collections, including the 396
primitives and both theme modes — matches exactly.

Because the drift is now two numbers rather than ten tokens, the trim needed to
produce a zero-diff baseline is trivial: restore those two values in
`shadow.tokens.json` before the verification run. A reviewer can check the trim
by eye; no tooling is required.

The migration PR proves itself against the trimmed export out-of-tree, then
commits the honest untrimmed export together with the shadow changes it
produces. The repo never holds a doctored export, and the PR description carries
the zero-diff proof plus the two-value delta, called out explicitly as separate
from the migration.

## Follow-ups

Recorded here so they are not lost, and explicitly out of scope:

- **`radius-350` code-syntax typo in Figma** (`LemonadeTheme.r adius.radius350`).
  Cosmetic, unread by any converter, but worth correcting at source.
- **Orphaned colour assets.** `swiftui-color-assets-generator` creates
  `.colorset` directories but never deletes them, so a removed or renamed
  semantic token leaves an orphan shipped in the SDK forever.
- **Flutter release drift.** Flutter's generated code is current in-tree but its
  last release is five months behind SwiftUI's.
- **Kotlin 2.3.20 pin.** The converters still fail to compile on 2.4.0. Unrelated
  to this migration, but it constrains anyone touching these scripts.

## Risks

| Risk | Mitigation |
|---|---|
| A future Figma change alters the DTCG output shape | Loader fails loudly on unknown `$type` or unresolvable reference rather than emitting defaults; the Token Drift CI job catches silent output changes |
| A partial export truncates a collection | Not guarded at ingest (see Ingest); a truncated collection removes public properties, which `apiCheck` reports as a breaking change |
| A designer reorders variables in Figma | Explicit sorts make generated ordering independent of input order |
| Primitive colours and theme fall out of sync (separate Figma files) | Loader fails loudly on an alias target missing from the primitives; verified 296/296 today |
