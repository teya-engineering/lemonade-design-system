# Figma Code Connect

Maps Lemonade Figma components to their call sites on **both platforms**, so Figma
Dev Mode and MCP-driven agents emit real `LemonadeUi.*` code instead of raw layer
output. One Figma component, two labels: `Compose` and `SwiftUI`.

## Layout

```
figma.compose.config.json   label "Compose", language "kotlin", reads connect/
figma.swiftui.config.json   label "SwiftUI", language "swift",  reads connect-swiftui/
icons.manifest.json         Figma icon name -> node id        (shared by both platforms)
flags.manifest.json         Figma flag name -> node id        (shared by both platforms)
brand-logos.manifest.json   Figma brand logo name -> node id  (shared by both platforms)
connect/                    Compose templates
connect-swiftui/            SwiftUI templates
  icons/                    GENERATED — do not edit
  flags/                    GENERATED — do not edit
  brand-logos/              GENERATED — do not edit
scripts/generate-asset-templates.mjs
```

## Assets

Icons, country flags and brand logos are enums in code and plain property-less
components in Figma, so their 1,200 mappings are generated rather than written:

```bash
node scripts/generate-asset-templates.mjs                # everything
node scripts/generate-asset-templates.mjs flags swiftui  # one of each
```

It cross-checks each manifest against that platform's enum **in both directions**
and fails rather than emitting a broken mapping. Only checking manifest → enum
would catch a deleted asset but stay silent on an added one, which is the
direction that actually happens: an icon lands in code and quietly has no
mapping.

The generated directories are marked `linguist-generated` in `.gitattributes`, so
GitHub collapses them in diffs and leaves them out of language statistics. They
stay committed on purpose — what is published to Figma should be inspectable in
git, and `git diff --exit-code` after regenerating is what proves the two agree.

A code enum entry with no Figma component is recorded in the manifest's
`knownUnmapped` list, so the gap stays visible instead of being tolerated
silently. Today that is one flag, `CD-congo-democratic-republic`.

Two asset sets are not one-to-one, and the generator handles both:

- **Brand logos ship a `-dark` component per brand.** `BrandLogo` resolves the
  dark artwork from the theme, so both nodes reference the same enum entry —
  50 templates over 25 entries.
- **`LemonadeBrandLogos` carries both `Diners` and `Dinners`.** Only the
  correctly-spelled component exists in Figma, and `Dinners` is served by it.
  That is recorded in the manifest's `aliases` rather than dropped, so the
  duplicate stays visible until someone removes it from the enums.

Each asset emits a bare enum reference (`LemonadeIcons.Search`,
`LemonadeCountryFlag.aCAscensionIsland`), because that is what every consumer
takes. Templates whose parameter is a composable or view slot wrap it
themselves.

Each label is published separately from its own config, which is the structure
Figma documents for multi-framework repos.

**Neither config is named `figma.config.json` on purpose.** That is the CLI's
default filename, so a bare `figma connect publish` would silently publish just
that one platform and report success. Always pass `--config`.

Templates are **parserless** — they emit Kotlin or Swift as strings via
`` figma.kotlin`...` `` / `` figma.swift`...` ``. Nothing is added to `kmp/ui` or
`swiftui/Sources`, so this has no effect on either published API surface or the
Binary Compatibility Validator baseline.

File keys live only in `documentUrlSubstitutions`; templates reference
`<LEMONADE_COMPONENTS>`, `<LEMONADE_ICONS>` and `<LEMONADE_FLAGS>`.

## Validate

```bash
cd figma && npm ci
FIGMA_ACCESS_TOKEN=figd_... npm run validate
```

`--dry-run` writes nothing, but it **still needs a token** — it resolves nodes
against the file before reporting, and exits 1 without one. CI therefore needs
the token as a secret.

Two other things it will not do:

- A config whose `include` glob matches **zero** templates is an error, not a
  no-op. Both `connect/` and `connect-swiftui/` must contain at least one
  template for validation to pass.
- It cannot catch Figma-side drift. If a designer renames a property, `getEnum`
  silently returns `undefined` and the snippet degrades without failing.

## Publish

Needs a Figma personal access token with `file_code_connect:write` and
`file_content:read`. See the `publish-figma-connect` skill for the full
procedure, including first-run setup on a new machine.

```bash
FIGMA_ACCESS_TOKEN=figd_... npm run publish:compose
FIGMA_ACCESS_TOKEN=figd_... npm run publish:swiftui
```

Publishing writes to the shared team library. **Publish both labels**, and never
a platform's components without its assets — Figma resolves a nested icon by
node, and a label with no template for that node falls back to another label's,
so a missing SwiftUI icon renders the *Kotlin* snippet inside a Swift call.

The components file also carries an unrelated `React` label pointing at a
personal exploration repo. Labels are independent namespaces; publishing these
two does not touch it.

## Components

Twenty-one components per platform, hand-written and kept at parity. A few needed
more than a property lookup:

- `SegmentedControl` numbers the selected segment from 1 in Figma while
  `selectedTab` is a 0-based index; the template converts. Its tabs resolve
  through `SegmentedControlTab{Large,Small}`, which map the internal `_Button`
  components onto `TabButtonProperties`, so the snippet carries the designer's
  real labels and icons. Those are the only internal `_` components worth
  connecting — the child has a genuine code representation the parent cannot
  otherwise obtain. It falls back to `"Tab 1".."Tab n"` if no tab resolves.
- `Toast`'s message is a plain text layer rather than a property, read with
  `findText('Label')`. Its icon is baked into the Success and Error variants, so
  the swap is only emitted for Neutral.
- `Chip` folds disabled into `Interaction State` instead of a separate flag, and
  has no slot overload — its Figma slots map onto `leadingIcon`/`trailingIcon`.
- SwiftUI rejects a trailing comma in an argument list, so those templates
  compose optional arguments with a **leading** comma. Kotlin permits either.
- `TextField.input` is a `Binding` on SwiftUI, so the snippet emits
  `.constant("…")` — it keeps the designed text visible and compiles as written;
  swap it for real `@State` when wiring the screen up.

- `SearchField`'s query and placeholder are text layers rather than properties,
  read with `findText('Value')` / `findText('Placeholder')`. In the filled
  variant Figma hides the placeholder layer, so the snippet carries no
  `placeholder` there — faithful to the design, but a real field usually wants
  one.
- `BoxSelection`'s `◇ Background` includes `N/A`, which is the Outlined variant
  where the background does not apply. That maps to omitting the argument rather
  than inventing a value.

### Deliberately unmapped

- `◇ Interaction State` and `📱 Device` everywhere — the former is runtime state
  driven by `interactionSource`, the latter has no code equivalent.
- `Card`'s `Show Heading` / `Show Footer Action` — the code takes
  `CardHeaderConfig` / `CardFooterActionConfig` objects, which Figma models as
  nested components rather than properties.
- `Link`'s `Show Indicator` — no code equivalent.
- `Notice`'s icon swap — the code has `showIcon` only and derives the glyph from
  the voice, so there is no parameter to map the swap onto.
- `SearchField`'s trailing slot — it holds the clear button, which the code owns
  through `dismissible` / `onInputClear` rather than exposing as content.
- `optionalIndicator = "Optional"` maps Figma's boolean onto a `String?`.
  "Optional" is the literal every call site in the repo uses, on both platforms.
  Note the snippet therefore emits English copy that a consumer shipping in
  another locale has to replace.
