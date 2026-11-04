# Cards

## `LDS-CARD-GROUPED-ITEMS` · Grouped rows sit inside a Card

**Kind.** Rule.

**Rule.** Two or more related rows render inside one `lmnd-kit-card`.

**Why.** A card is what tells someone the rows belong together. Rows loose on the background read as
unrelated things that happen to be stacked, and the screen has no visible structure.

**Exceptions.**

- A standalone outlined select row, `lmnd-kit-select-item`. Its outline is its own container, and carding it
  draws a border inside a border.
- A row group inside a container that already cards it, such as the card inside a bottom sheet's body.

**Do / don't.** One `lmnd-kit-card` holding three `lmnd-kit-content-item` rows with `lmnd-kit-divider`s between them. Not
three rows as siblings in a `lmnd-kit-content` or `lmnd-kit-stack`.

```html
<div class="lmnd-kit-card">
  <div class="lmnd-kit-content-item">…</div>
  <hr class="lmnd-kit-divider">
  <div class="lmnd-kit-content-item">…</div>
</div>
```

## `LDS-CARD-NO-HANDROLL` · A card is the Card component

**Kind.** Rule.

**Rule.** A card is `lmnd-kit-card`. Never a background colour plus a corner radius.

**Why.** A hand-built card misses whatever the component does that the design asks for — the background,
the radius, the header, the treatment of rows and dividers — and it drifts the moment any of those change
in the design system.

**Exceptions.** None.

**Do / don't.** `<div class="lmnd-kit-card">`, with `lmnd-kit-card--subtle` or `lmnd-kit-card--elevated` for the other
backgrounds. Not `<div style="background: var(--lmnd-color-bg-default); border-radius: var(--lmnd-radius-600)">`.

## `LDS-CARD-SURFACE` · Cards sit on a subtle surface

**Kind.** Rule.

**Rule.** A screen or sheet that holds cards uses the subtle background — `lmnd-kit-screen`, `lmnd-kit-sheet--subtle` —
and its cards use the Default background, `lmnd-kit-card`.

**Why.** A card stands out by being lighter than what is behind it. A white card on a white page needs a
grey fill to show at all, and a grey card reads as a disabled or secondary area rather than as content.

**Exceptions.**

- A screen or sheet with no cards — a form of text fields, a single message — may use the default
  background, `lmnd-kit-screen--default` or `lmnd-kit-sheet`.
- `lmnd-kit-card--elevated` and `lmnd-kit-card--subtle` are for a card inside a card or inside a coloured area, never
  for a card on the page.

**Do / don't.** `<div class="lmnd-kit-screen">` holding `<div class="lmnd-kit-card">`s. Not
`<div class="lmnd-kit-screen lmnd-kit-screen--default">` holding `<div class="lmnd-kit-card lmnd-kit-card--elevated">`s.

## `LDS-CARD-PADDING` · Padding follows what the card holds

**Kind.** Rule.

**Rule.** A card of list items has no padding, which is the default. A card of custom content takes
`lmnd-kit-card--pad-medium`.

**Why.** List items carry their own padding and their dividers have to reach the card's edges. Custom
content carries none, so without the card's padding it sits against the edge.

**Exceptions.** A card whose sections mix the two keeps no padding and pads the custom section itself, so
the list rows still reach the edges.

**Do / don't.** `<div class="lmnd-kit-card">` around `lmnd-kit-list-item`s. `<div class="lmnd-kit-card lmnd-kit-card--pad-medium">`
around a timeline or a paragraph. Not an unpadded card around a hand-built row — that is usually a list
item being rebuilt, which `LDS-LIST-NO-HANDROLL` covers.
