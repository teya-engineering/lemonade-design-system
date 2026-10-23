# List items

## `LDS-LIST-NO-HANDROLL` · A row is a DS list item

**Rule.** A row carrying an icon and text is a DS list item. A label-and-value row is
`ContentListItem`; a tappable row is `ActionListItem`.

**Why.** The components own the row's height, its internal spacing, its slot alignment and its
divider. A hand-built row agrees with them until any of those move, and then it is the one row on the
screen at the wrong height.

**Exceptions.** A row whose layout no list item models — two values side by side under one label, a
row built around a chart. State the reason at the site.

**Do / don't.** `ActionListItem(leading = …, label = …, onClick = …)`. Not a `Row` of an icon, a
`Text` and a `Spacer`.

## `LDS-LIST-LEADING-SLOT` · What goes in the leading slot

**Rule.** A row's leading slot is a `SymbolContainer` when either holds:

- **The slot could hold something other than an icon** — a logo, a photo, initials, a number. Stores,
  people, terminals, products, bank accounts, numbered steps. `SymbolContainer` is the only component
  that can, so a list where some rows have artwork uses it for every row.
- **The slot carries a voice** — status, warning, brand emphasis. The colour belongs to the container,
  not to a bare glyph.

Otherwise the slot is a plain icon tinted `contentSecondary`: the glyph helps someone scan the list
rather than saying what the row is.

**Why.** The two treatments start their labels at different indents, so mixing them down one card
gives the card two left edges.

**One kind per card.** A card whose rows mix the two is a violation whatever the rules above say about
each row on its own.

**Colour.** A `SymbolContainer` that carries no voice can take its colour from the themed layer,
`LemonadeThemedColors` — theme-aware hues, each with a saturated and a subtle palette. That is the
layer for colour the design system does not model: a category, a per-role accent, one product against
another. A voice wins wherever the row means positive, critical, caution or brand, and a primitive
colour is never the answer. See `LDS-COLOR-SEMANTIC`.

**Exceptions.** Where neither test above applies, the consuming product decides by screen kind — a
settings list reads better with plain icons, an explainer with symbol containers — so the weight is
settled once per kind rather than argued screen by screen. That table belongs in the product's own
rules.

## `LDS-LIST-SYMBOL-SHAPE` · A symbol in a row's leading slot is rounded

**Rule.** When a row's leading slot holds a `SymbolContainer` (see `LDS-LIST-LEADING-SLOT`), it uses the
rounded shape — `shape = SymbolContainerShape.Rounded` in Compose, `shape: .rounded` in SwiftUI — never the
circle. This holds for every list row: `ActionListItem`, `ResourceListItem`, `SelectListItem` in both
variants, and any other `ListItem`.

**Why.** One shape down a list keeps its left edge reading as one column. The rounded square is the list
shape; the circle is the component's default on both platforms, so a row that doesn't ask for rounded
gets the wrong one.

**Exceptions.** None in a list row. A `SymbolContainer` outside one — a screen's hero, a badge host —
keeps whichever shape its layout calls for.

**Do / don't.** `leadingSlot = { SymbolContainer(icon = Store, shape = SymbolContainerShape.Rounded) }`.
Not `SymbolContainer(icon = Store)`, which is a circle.
