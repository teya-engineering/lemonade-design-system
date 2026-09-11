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
