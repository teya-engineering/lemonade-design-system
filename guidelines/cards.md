# Cards

## `LDS-CARD-GROUPED-ITEMS` · Grouped rows sit inside a Card

**Rule.** Two or more related rows render inside one `Card`.

**Why.** A card is what tells someone the rows belong together. Rows loose on the background read as
unrelated things that happen to be stacked, and the screen has no visible structure.

**Exceptions.**

- A standalone outlined select row. Its outline is its own container, and carding it draws a border
  inside a border.
- A row group inside a container that already cards it. A list container that renders its own card
  satisfies the rule without the calling code naming `Card`.

**Do / don't.** One `Card` holding three `ContentListItem` rows, dividers between them. Not three list
items as siblings in a `Column` or `VStack`.

## `LDS-CARD-NO-HANDROLL` · A card is the Card component

**Rule.** A card is `LemonadeUi.Card`. Never a background colour plus a corner radius.

**Why.** A hand-built card misses whatever the component does that the design asks for — the elevated
background, the radius, the header, the divider treatment between rows — and it drifts the moment any
of those change in the design system.

**Exceptions.** None.

**Do / don't.** `LemonadeUi.Card { … }`. Not `Surface(shape = shapes.radius400, color =
background.bgElevated)`, and not a `.background(bgElevated).clip(radius400)` modifier chain.

## `LDS-CARD-PADDING` · Padding follows what the card holds

**Rule.** A card of list items uses `None`, which is the component default. A card of custom content
uses `Medium`.

**Why.** List items carry their own padding and their dividers have to reach the card's edges. Custom
content carries none, so without the card's padding it sits against the edge.

**Exceptions.** A card whose sections mix the two keeps `None` and pads the custom section itself, so
the list rows still reach the edges.

**Do / don't.** `LemonadeUi.Card { ContentListItem(…) }` with the default padding. Not `Card(
contentPadding = None)` wrapping a hand-built row — that combination is usually a list item being
rebuilt, which `LDS-LIST-NO-HANDROLL` covers.
