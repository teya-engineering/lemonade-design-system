---
name: figma-themed-component
description: >
  Create a themed copy of an existing component set in the Lemonade DS Components Figma file: a
  `<Component> (Themed)` set whose Theme property has one value per themed hue, each variant bound
  to the Foundations `Themed (Beta)` colours. Use when asked to "add themed support in Figma",
  "create <X> (Themed)", add a Theme variant or property to a component set, or mirror a component's
  `theme: ThemedStyle` API in Figma. Not for the code side (`ThemedStyle` comes from the themed
  token converters, see `generate-tokens`) or for building a component from scratch.
---

# Create a themed component set in Figma

A themed set is a copy of an existing component set with its colour property (usually `◇ Voice`)
replaced by `◇ Theme`: one value per hue of the themed palette, every variant bound to that hue's
`background`, `border` and `on-background` colours. Five `use_figma` scripts in `scripts/` do the
work. The rule that matters most: **never duplicate variants with `variant.clone()`**. Clone the
whole component set, then move its variants across (see Anti-patterns).

## Before you start

- **Load `figma:figma-use`** before any `use_figma` call, and pass `skillNames: "figma-use"`.
- **File:** `🍋 Lemonade DS - Components`, file key `91S16rhVrl5wivqV66fNjm`. The themed colours come
  from the `🔷 Lemonade DS - Foundations` library, which the file already has enabled.
- **Scripts:** each file in `scripts/` is a `use_figma` script with a `CONFIG` block at the top. Read
  it, fill in `CONFIG`, and pass the whole file as the `code` argument. Run them one at a time, never
  in parallel.

## Decisions to settle with the designer

| Decision | Default |
|---|---|
| Source set | The node from the URL they share. Run `inspect-source.js` before asking anything else. |
| Properties to keep | Their list. Drop content types that don't take a colour (images, brand logos). A variant property they don't want is pinned to one value and removed. |
| Nested instances | Leave them alone (accessories, badges, counters). Opt in the ones that carry the component's own colour, such as an icon instance that owns its fill. |
| Palette | Subtle (pale fill, hue-coloured content), solid, or both, matching what the code component accepts. `find-themed-variables.js` reads one palette per run (`'subtle'` or `''` for solid). For both, run it twice and suffix the subtle keys (`Blue Subtle`, following the `Brand` / `Brand Subtle` voices) before passing them to `add-themes.js`. |
| Neutral | Leave it out when the source already has a Neutral voice, because the two look the same. The code enum keeps it either way. |
| Property and values | `◇ Theme`, using the file's emoji prefix. Values are Title Case hue names (`green-lime` becomes `Green Lime`), which line up with the code's `ThemedHue` entries. |
| Name and place | `<Component> (Themed)`, on the source's page, in empty canvas next to it. |

## Steps

1. **`inspect-source.js`** (read-only) reports the set's properties, `layoutMode`, bounds and parent,
   plus every bound colour in one variant, marking which ones sit inside a nested instance or slot.
   Every colour that should follow the theme must be bound to a `Background/*`, `Border/*` or
   `Content/*` variable, because that is what the mapping below keys on. Use `bounds` to pick an
   empty spot next to the set.
2. **`find-themed-variables.js`** returns `{ themes: { 'Green Lime': { background, border, 'on-background' } } }`.
   Check that `count` is 17 and `incomplete` is empty.
3. **`prepare-themed-set.js`** clones the source into the same parent and keeps the variants matching
   `keep`. That must be exactly one value of the colour property, and exactly one value of each
   property in `dropProperties`, which it then removes. It deletes non-variant properties that
   nothing references (for example an image swap), renames the colour property to Theme, and parks a
   template copy 10,000px above the set. Booleans, instance swaps and slots that are still in use
   carry over. It returns `setId` and `templateId`. Check `droppedProperties`.
4. **`add-themes.js`**, once per batch of about 150 new variants: 150 divided by `baseVariants`,
   rounded down, is the number of themes per call. Paste that slice of step 2's `themes` into
   `CONFIG.themes`, and list any opted-in instances in `recolourInstances`. Every batch must return
   `unmapped: []`, because anything listed there keeps its source colour; decide with the designer
   before moving on. Check that `skippedInstances` holds only the instances you meant to leave alone.
   Until step 5 the new variants pile up on the first grid cells, which is expected.
5. **`finalize-themed-set.js`** deletes the base variants and the template, lays out the grid and sets
   the description. The top-left cell, the first value of every `rows` and `columns` list, is
   Figma's default variant, so list each property's default first. `variants` must equal `expected`.
6. **Match the page.** If the source has row labels or a backdrop frame beside it, clone them for the
   new set. The doc `Label` instances use variable SF Pro, which the plugin cannot lay out:
   `setProperties` throws and edited text keeps rendering the old string. Detach the clones and set
   their text in Figtree Medium.
7. **Verify.**
   - Take `await set.screenshot()` inside `use_figma`.
   - Create one instance, turn on its boolean and slot properties, screenshot it, then delete it.
   - Hand the designer the node URL, and tell them the set only reaches consumers once they publish
     the library.

## How `add-themes.js` maps colours

| Source binding | Theme slot |
|---|---|
| `Background/*`, or `Themed…/background` | `background` |
| `Border/*`, or `Themed…/border` | `border` |
| `Content/*`, or `Themed…/on-background` | `on-background` |

The mapping reads the variant's own layers only. It stops at nested instances and slots unless
their layer name is in `recolourInstances`, so an accessory or badge inside the component keeps its
colours. Without that stop, the accessory would be repainted too: Symbol Container's status indicator
uses the same border token as the container. Text with per-character fills is reported in `unmapped`
rather than rebound.

## Anti-patterns

- **`variant.clone()` to duplicate variants.** The clone turns every SLOT into a plain FRAME and loses
  its `componentPropertyReferences` (boolean visibility, instance swaps). Clone the whole set, which
  keeps them, then `appendChild` its variants into the themed set. `add-themes.js` does this.
- **Naming a variant before moving it.** A variant that leaves its set is renamed to
  `<set name>, <value>, <value>…`, and the target set then reports "Component set has existing
  errors". Set the name after `appendChild`.
- **Removing an emptied set.** A component set deletes itself when its last variant moves out, so a
  later `remove()` throws "node does not exist". Guard it with `if (!set.removed)`.
- **Positioning variants with `x`/`y` in a GRID set.** Lemonade's sets use GRID auto-layout, which
  ignores coordinates. The giveaway is every theme stacked in one cell, so the rows look like a single
  muddy colour. Use `setGridChildPosition`, parking variants in spare rows first so every target cell
  is free.
- **Matching variables by `Themed/…`.** The library names them `Themed (Beta)/<hue>/…`. Match
  `^Themed[^/]*/`, as the scripts do.
- **Retrying blindly.** A script that throws is rolled back as a whole, so fix it and rerun. A call
  that times out or loses its connection may have applied part of the change, so read the set's
  variant count and theme values first. `add-themes.js` skips themes the set already has.
- **One enum per component in code.** Every themed component takes the shared `ThemedStyle`; see
  "Code side".

## Worked example

`Symbol Container (Themed)`, node `21779:772` on the `❖ · Symbol Container` page. It has 16 subtle
themes (no Neutral) × Size × Content Type (Icon, Text) × Shape, which makes 384 variants.

| Script | Config |
|---|---|
| `prepare-themed-set.js` | Source `2210:520`. `keep` = `◇ Voice: ['Neutral']` and `◇ Content Type: ['Icon', 'Text']`. `colourProperty` = `◇ Voice`. It dropped the unused Brand Logo swap on its own and kept the Icon swap, the Accessory slot and the `◇ Show Accessory` boolean. |
| `add-themes.js` | 24 base variants, so six themes per call. `recolourInstances` stays empty: the icon's colour is an `Icon Color` rectangle masked by the icon instance, so it is already one of the variant's own layers. |
| `finalize-themed-set.js` | Rows are `◇ Content Type` × `◇ Theme`. Columns are `◇ Shape` (Rounded, Circular) × `↕ Size`, with Medium first so it becomes the default. |

## Code side

Components take `theme: ThemedStyle`: a bare hue (`ThemedHue.Blue` on KMP, `.blue` on SwiftUI)
selects the solid palette, and `.subtle` the subtle one. `ThemedStyle` and `ThemedHue` are generated
with the rest of the themed palette by the themed token converters (see `generate-tokens`). A
component reads `background`, `border` and `onBackground` from `LemonadeTheme.themed[theme]`
(`[style: theme]` on SwiftUI). For the pattern, see the themed overloads in
`kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SymbolContainer.kt` and
`swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift`.
