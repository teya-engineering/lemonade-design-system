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

## A note on auditing coverage

Do not audit Figma-against-code by matching names. Several components differ in
name between the two without being unmapped: `Selection List Item` is
`SelectListItem` in code, and `Divider` is `HorizontalDivider` and
`VerticalDivider`. A name-matching pass reports both as missing on one side. Any
"this exists in Figma but not in code" claim needs checking by hand before it is
acted on.

## Components

Thirty-five components per platform, hand-written and kept at parity. A few needed
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

- `SymbolContainer` has four content modes and both platforms have four matching
  overloads, but they do not line up one-to-one. Icon and Text map directly;
  **Brand Logo** has no overload of its own and renders through the content slot
  as a nested `BrandLogo`; **Image** has no source in Figma to carry over, so the
  slot is emitted with a TODO for the developer to fill.
- Figma still calls SymbolContainer's amber voice **`Caution`** while the enum
  calls it `Warning`. Tag and ActionListItem both had this shape of mismatch and
  were renamed in Figma; this set is the last one outstanding. The template maps
  across it, but the library disagreeing with itself is worth fixing at source —
  and note that a rename is exactly the change `getEnum` degrades to `undefined`
  on, silently, until someone republishes.

- `Tabs` resolves its tab children the way SegmentedControl does, through a
  `TabItem` template on the internal `_Tab Item` component. Its `◇ Items` variant
  tops out at `5+`, but the tabs are real named instances, so the count comes
  from resolving them — the `5+` variant lays out nine. Selection is a property
  of each tab in Figma and an index on the parent, so the child surfaces it
  through `metadata.props` and the parent folds it into `selectedIndex`.

- **Slot content cannot be inlined.** Figma hoists an instance-bearing `SLOT`
  into React-shaped nested functions, so `getSlot()` interpolated into a Kotlin
  or Swift snippet emits `<LeadingSlot_1 />` rather than the child's code. Slots
  are therefore used only as a presence signal: a lambda-typed parameter gets a
  `/* … */` placeholder, and an enum-typed one is left out entirely, because a
  slot cannot resolve to an enum value. That is why `Chip`'s leading and trailing
  icons are omitted and `Tile`'s required `icon` emits a TODO instead of a guess.
  `INSTANCE_SWAP` properties do not have this problem and inline correctly.
- `Tooltip` maps all thirteen indicator placements. `History Timeline` resolves
  its rows through a `.History Item` template, which reads its text from the
  nested content instance and its voice from the nested indicator via
  `metadata.props`, then folds the current row into `currentIndex`.

- `SwipeActionRow` maps little on purpose. `actions` is a list of `SwipeAction`
  data objects with enum-typed icons, which a slot cannot resolve to, so the
  snippet emits an empty list and a TODO. Figma also offers a **Leading**
  actions placement that neither platform implements; rather than quietly
  emitting a trailing row, the snippet carries a NOTE saying the design cannot
  be built as drawn. That gap is worth closing on one side or the other.

- `Divider` is one Figma component over two composables: `Orientation` picks
  between `HorizontalDivider` and `VerticalDivider` rather than being a
  parameter. Only the horizontal one takes a label, which is why the labelled
  variant has no vertical counterpart in code.
- Connecting `ListItem` and `Divider` also fixed an import leak. Figma
  aggregates imports from nested children, and both were previously mapped only
  under the `React` label, so React imports were appearing in Kotlin snippets
  for any component that nests them — `SwipeActionRow` most visibly. A component
  left unmapped does not only lose its own snippet; it degrades its parents'.

- The list-item family keeps its strings in **text layers**, not properties, so
  `ListItem`, `ResourceListItem` and `ActionListItem` all read them with
  `findText`. The booleans beside them only toggle visibility. Layer names are
  case-sensitive and inconsistent — `Top label` and `Support text` are not
  title-cased the way `Label` and `Description` are.

- `SelectListItem` is `Selection List Item` in Figma, and its borderless variant
  is `Ghost` there against `Plain` in the enum. Neither is a gap, but both are
  the kind of near-miss that a name-based audit reports as missing — see below.

- `DatePicker` maps **nothing**. Its Figma component is a representative
  rendering with two properties, `Device` and `View Type`, and neither has a code
  counterpart — the code has no months view at all. The template exists purely so
  a calendar in a design is recognised as `LemonadeUi.DatePicker` rather than
  rebuilt from layers, and it says outright that the locale data is not carried.
  A Months design gets a NOTE saying the component does not implement it. If the
  Figma component is ever modelled properly, this template should be rewritten
  rather than extended.

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
