# Figma Code Connect — Compose

Maps Lemonade Figma components to their Compose call sites, so Figma Dev Mode and
MCP-driven agents emit real `LemonadeUi.*` code instead of raw layer output.

## Layout

```
figma.config.json    label "Compose", language "kotlin"
connect/*.figma.ts   one template per component
```

## Icons

`connect/Icon.figma.ts` covers the `Icon` *wrapper* in the Components library,
which pairs a swapped glyph with a size and emits a full `LemonadeUi.Icon(...)`
call. The raw glyphs it swaps in live in the separate Icons library and are
mapped on their own branch; until that lands, `icon = ...` renders as the bare
instance rather than a `LemonadeIcons` value.

Note the wrapper's size names differ from code: Figma writes
`2XLarge`/`3XLarge`/`4XLarge` where `LemonadeAssetSize` writes
`XXLarge`/`XXXLarge`/`XXXXLarge`.

Templates are **parserless** — they emit Kotlin as strings via `` figma.kotlin`...` ``.
Nothing is added to `kmp/ui`, so this has no effect on the published API surface or
the Binary Compatibility Validator baseline.

The file key lives only in `documentUrlSubstitutions`; templates reference
`<LEMONADE_COMPONENTS>?node-id=...`.

## Validate (no side effects, no token)

```bash
cd figma && npm install
./node_modules/.bin/figma connect publish --config figma.config.json \
  --dry-run --exit-on-unreadable-files
```

This is the check to wire into CI. It catches broken templates. It does *not*
catch Figma-side drift: if a designer renames a property, `getEnum` silently
returns `undefined` and the snippet degrades without failing.

## Publish

Needs a Figma personal access token with Code Connect write plus file read.

```bash
FIGMA_ACCESS_TOKEN=figd_... ./node_modules/.bin/figma connect publish --config figma.config.json
```

Publishing writes to the shared team library. To trial changes safely, set
`label` to `"Compose (test)"` first, verify in Dev Mode, then
`figma connect unpublish` and republish under `Compose`.

The file also carries an unrelated `React` label pointing at a personal
exploration repo. Labels are independent namespaces; publishing `Compose` does
not touch it.

## Known gaps

- `TextField` / `SelectField` leading and trailing items are assumed to be icons
  and wrapped in `LemonadeUi.Icon`. The Figma sets also allow input addons in
  those slots; an addon would be wrapped incorrectly. Revisit if addons get
  templates of their own.
- `◇ Interaction State` and `📱 Device` are intentionally unmapped everywhere —
  the former is runtime state driven by `interactionSource`, the latter has no
  code equivalent.
- `Card`'s `Show Heading` / `Show Footer Action` are unmapped: Compose takes
  `CardHeaderConfig` / `CardFooterActionConfig` objects, which Figma models as
  nested components rather than properties.
- `optionalIndicator = "Optional"` maps Figma's boolean onto Compose's `String?`.
  "Optional" is the literal every call site in the repo uses, on both platforms.
  Note the snippet therefore emits English copy that a consumer shipping in
  another locale has to replace.
