# Dimension

Spacing, radius, size and border width. Spacing, radius and size are built on a 4 px unit, and a
token's number is its value divided by 4, times 100: `spacing-400` is 16 px, `radius-150` is 6 px.

## `LDS-DIMENSION-SCALE` · Dimensions come from the scales

**Rule.** Padding, margins and gaps are `LemonadeTheme.spaces`. Corner radii are
`LemonadeTheme.radius`. Widths and heights are `LemonadeTheme.sizes`. Stroke widths are
`LemonadeTheme.borderWidths`, and a selection or focus outline is `borderWidths.state.borderSelected` or
`borderWidths.state.focusRing`. No `dp` or `pt` literal stands in for any of them.

**Why.** A literal that matches a token today stays put when the token moves. One that matches no
token — 15, 18, 22 — breaks the rhythm the scale exists to keep.

**Exceptions.**

- A size the scale lacks, fixed by something outside the layout: an image's aspect, an illustration's
  artwork, a platform control's height.
- `0`, written as `spacing0` or `radius0` when it is a decision rather than an absence.

**Do / don't.** `.padding(LemonadeTheme.spaces.spacing400)`. Not `.padding(16.dp)`, even though it is
the same value.

## `LDS-DIMENSION-EDGE` · Content sits 16 from the edge it belongs to

**Rule.** Content sits `spacing400` from the edge of the screen, and `spacing400` from the edge of a
visible container. The distance is measured to what you see, not to the box.

- A Lemonade list item already reaches the margin with its own padding, which is where its pressed
  background is drawn. Stack list items without wrapping them in padding.
- Inside a container that has its own padding, such as `Card` with `contentPadding = Medium`, add no
  margin of your own.
- A large container pads the same on all four sides.

**Why.** Every screen's content starts on the same line, so a list, a card and a heading under one
another read as one column. A list item wrapped in a 16 padding puts its text at 32, out of line with
everything else.

**Exceptions.**

- Optical alignment: a glyph or a shape that looks off at the exact value may be moved to look right.
- A smaller container may use less.
- Margins on a tablet or a terminal are not defined here; this rule covers phone-width layouts.

**Do / don't.** `ActionListItem`s stacked straight on the screen. Not a `Column(Modifier.padding(16.dp))`
around them.

## `LDS-DIMENSION-GAPS` · The gap follows what is being separated

**Rule of thumb.**

| Between | Gap |
| --- | --- |
| Sections — a heading and its card, a group of mixed elements | `spacing800` |
| Cards, and other containers with a visible edge | `spacing400` |
| Outlined list items that stand alone, such as `SelectListItem` with `variant = Outlined` | `spacing200` |
| Small elements next to each other — tags, chips | `spacing200` |
| Plain interactive list items | none; their dividers come from `showDivider` |
| Static items — `ContentListItem` in a card | `spacing400`, inside a `Medium` card |
| Stacked labels — a title and its subtitle | none |
| Paragraphs | the text style's line height |

**Why.** The gap says how closely two things belong together, so the same relationship has to read
the same everywhere. Labels need no gap because each text style's line height already spaces them.

**Exceptions.** Optical alignment, as in `LDS-DIMENSION-EDGE`. A gap between an icon and its label
inside one row is the row's own spacing, not a block gap.

**Do / don't.** A heading, then `spacing800`, then the next section. Not `spacing100` between a title
and its subtitle, and not a hand-drawn divider between `ActionListItem`s.

## `LDS-DIMENSION-RADIUS` · A container's radius is the container token

**Rule of thumb.** A container-sized surface — a card, a dialog, a sheet — uses
`radius.semantic.radiusContainerDefault`. A self-contained item inside a layout — a tile, a notice, a
standalone select row — uses `radius500`. Pills and circles use `radiusFull`. Everything smaller takes
its radius from its component.

`LemonadeUi.Card` and `Dialog` use `radiusContainerDefault`. `Tile`, `Notice`, `SelectListItem`,
`ListItemHighlight` and `SwipeActionRow` use `radius500`. `Badge`, `Chip`, `SearchField`, `Switch` and
`Toast` use `radiusFull`.

An element drawn inside a rounded container takes the container's radius minus the container's
padding: inside a `radiusContainerDefault` card padded `spacing400`, that is `radius200`.

**Why.** `radiusContainerDefault` is a semantic token: it names the decision, so the containers move
together if it changes. Subtracting the padding keeps the inner and outer curves parallel, so the
inner element never looks rounder than its parent.

**Exceptions.** None for containers. A product that needs a container-sized surface the design system
doesn't have names `radiusContainerDefault`, never `radius600`, even though they are equal today.

**Do / don't.** `RoundedCornerShape(LemonadeTheme.radius.semantic.radiusContainerDefault)` on a custom
media panel. Not `radius600`, and not `radius800` to make it feel softer.
