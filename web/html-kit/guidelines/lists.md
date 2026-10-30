# List items

## `LDS-LIST-NO-HANDROLL` · A row is a DS list item

**Kind.** Rule.

**Rule.** A row carrying an icon and text is a kit list item. A label-and-value row is `lmnd-kit-content-item`;
a tappable row is an `<a>` or `<button>` with `lmnd-kit-list-item`; a row with a switch, checkbox or radio is a
`<label>` with `lmnd-kit-list-item`.

**Why.** The components own the row's height, its internal spacing, its slot alignment, its press
highlight and its divider. A hand-built row agrees with them until any of those move, and then it is the
one row on the screen at the wrong height.

**Exceptions.** A row whose layout no list item models — two values side by side under one label, a row
built around a chart. State the reason in an exemption comment.

**Do / don't.**

```html
<a href="details.html" class="lmnd-kit-list-item">
  <span class="lmnd-kit-list-item__row">
    <span class="lmnd-kit-list-item__leading">…</span>
    <span class="lmnd-kit-list-item__content">…</span>
    <span class="lmnd-kit-list-item__trailing">…</span>
  </span>
</a>
```

Not a `<div style="display: flex">` of an icon, a label and a chevron.

## `LDS-LIST-LEADING-SLOT` · What goes in the leading slot

**Kind.** Rule.

**Rule.** A row's leading slot is a `lmnd-kit-symbol` when either holds:

- **The slot could hold something other than an icon** — a logo, a photo, initials, a number. Stores,
  people, terminals, products, bank accounts, numbered steps. `lmnd-kit-symbol` is the only component that can,
  so a list where some rows have artwork uses it for every row.
- **The slot carries a voice** — status, warning, brand emphasis (`lmnd-kit-symbol--positive`,
  `--critical`, `--warning`, `--info`, `--brand`). The colour belongs to the container, not to a bare
  glyph.

Otherwise the slot is a plain `lmnd-icon`, which the kit tints `content-secondary`: the glyph helps
someone scan the list rather than saying what the row is.

**Why.** The two treatments start their labels at different indents, so mixing them down one card gives
the card two left edges.

**One kind per card.** A card whose rows mix the two is a violation whatever the rules above say about
each row on its own.

**Colour.** A `lmnd-kit-symbol` with no voice stays neutral. A voice wins wherever the row means positive,
critical, caution or brand, and a primitive colour is never the answer. See `LDS-COLOR-SEMANTIC`.

**Exceptions.** Where neither test above applies, the screen kind decides — a settings list reads better
with plain icons, an explainer with symbol containers — so the weight is settled once per kind rather
than argued screen by screen.

## `LDS-LIST-SYMBOL-SHAPE` · A symbol in a row's leading slot is rounded

**Kind.** Rule.

**Rule.** When a row's leading slot holds a `lmnd-kit-symbol` (see `LDS-LIST-LEADING-SLOT`), it is the rounded
shape, `lmnd-kit-symbol--rounded`, never the circle. This holds for every list row: `lmnd-kit-list-item`, a plain
select row, and the outlined `lmnd-kit-select-item`.

```html
<span class="lmnd-kit-list-item__leading">
  <span class="lmnd-kit-symbol lmnd-kit-symbol--rounded"><span class="lmnd-icon" data-icon="store" aria-hidden="true"></span></span>
</span>
```

**Why.** One shape down a list keeps its left edge reading as one column. The rounded square is the list
shape; the circle is the component's default, so a row that doesn't ask for rounded gets the wrong one.

**Exceptions.** None. A symbol outside a list row keeps whichever shape its layout calls for: a details
screen's hero artwork is rounded too (`APP-DETAILS-HERO`), and a widget's avatars are circles
(`APP-WIDGET-BOTTOM`).

**Do / don't.** `<span class="lmnd-kit-symbol lmnd-kit-symbol--rounded">` in `lmnd-kit-list-item__leading`. Not a bare
`<span class="lmnd-kit-symbol">`, which is a circle.
