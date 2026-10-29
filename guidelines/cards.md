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

**Example.** A payout's details: its amount, its fees and the account it went to, with dividers between
the rows.

Compose.

```kotlin
LemonadeUi.Card {
    LemonadeUi.ContentListItem(label = "Amount", value = "£1,240.50", showDivider = true)
    LemonadeUi.ContentListItem(label = "Fees", value = "£18.60", showDivider = true)
    LemonadeUi.ContentListItem(label = "Paid to", value = "Barclays •• 2210")
}
```

SwiftUI.

```swift
LemonadeUi.Card {
    LemonadeUi.ContentListItem(label: "Amount", value: "£1,240.50", showDivider: true)
    LemonadeUi.ContentListItem(label: "Fees", value: "£18.60", showDivider: true)
    LemonadeUi.ContentListItem(label: "Paid to", value: "Barclays •• 2210")
}
```

HTML kit. Dividers are separate `lmnd-kit-divider`s between the rows.

```html
<div class="lmnd-kit-card">
  <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Amount</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£1,240.50</span></div>
  <hr class="lmnd-kit-divider">
  <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Fees</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£18.60</span></div>
  <hr class="lmnd-kit-divider">
  <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Paid to</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">Barclays •• 2210</span></div>
</div>
```

**Don't.** The same rows as siblings in a `Column` or `VStack`, or `lmnd-kit-content-item`s placed
straight in `lmnd-kit-content` with no `lmnd-kit-card` around them.

## `LDS-CARD-NO-HANDROLL` · A card is the Card component

**Rule.** A card is Lemonade's `Card` component. Never a background colour plus a corner radius.

**Why.** A hand-built card misses whatever the component does that the design asks for — the elevated
background, the radius, the header, the divider treatment between rows — and it drifts the moment any
of those change in the design system.

**Exceptions.** None.

**Example.** A Today card on the home screen, with a header over the day's sales.

Compose.

```kotlin
LemonadeUi.Card(header = CardHeaderConfig(title = "Today")) {
    LemonadeUi.ContentListItem(label = "Sales", value = "£1,240.50")
    LemonadeUi.ContentListItem(label = "Transactions", value = "48")
}
```

SwiftUI.

```swift
LemonadeUi.Card(header: CardHeaderConfig(title: "Today")) {
    LemonadeUi.ContentListItem(label: "Sales", value: "£1,240.50")
    LemonadeUi.ContentListItem(label: "Transactions", value: "48")
}
```

HTML kit.

```html
<div class="lmnd-kit-card">
  <div class="lmnd-kit-card__header">
    <div class="lmnd-kit-card__heading"><h3 class="lmnd-kit-card__title lmnd-text-heading-xxsmall">Today</h3></div>
  </div>
  <div class="lmnd-kit-card__body">
    <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Sales</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£1,240.50</span></div>
    <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Transactions</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">48</span></div>
  </div>
</div>
```

**Don't.** `Surface(shape = shapes.radius400, color = background.bgElevated)`, a
`.background(bgElevated).clip(radius400)` modifier chain,
`.background(.bg.bgElevated, in: .shape.radius400)` in SwiftUI, or a `<div>` given a `background` and a
`border-radius` in place of `lmnd-kit-card`.

## `LDS-CARD-SURFACE` · Cards sit on a subtle surface

**Rule.** A screen or sheet that holds cards uses the subtle background, `bgSubtle`, and its cards use the
Default card background, the component default.

**Why.** A card stands out by being lighter than what is behind it. A default card on a default screen
needs a grey fill to show at all, and a grey card reads as a disabled or secondary area rather than as
content.

**Exceptions.**

- A screen or sheet with no cards — a form of text fields, a single message — may use the default
  background.
- The Elevated and Subtle card backgrounds are for a card inside a card or inside a coloured area,
  never for a card on the screen.

**Example.** A screen holding a card of the day's sales.

Compose. A sheet takes `LemonadeUi.BottomSheet(background = LemonadeBottomSheetVariant.Subtle)`.

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
    modifier = Modifier
        .fillMaxSize()
        .background(LemonadeTheme.colors.background.bgSubtle)
        .padding(LemonadeTheme.spaces.spacing400),
) {
    LemonadeUi.Card {
        LemonadeUi.ContentListItem(label = "Sales", value = "£1,240.50")
    }
}
```

SwiftUI.

```swift
ScrollView {
    LemonadeUi.Card {
        LemonadeUi.ContentListItem(label: "Sales", value: "£1,240.50")
    }
    .padding(LemonadeTheme.spaces.spacing400)
}
.background(LemonadeTheme.colors.background.bgSubtle)
```

HTML kit. `lmnd-kit-screen` is `bgSubtle` unless it takes `lmnd-kit-screen--default`; a sheet takes
`lmnd-kit-sheet--subtle`.

```html
<div class="lmnd-kit-screen">
  <main class="lmnd-kit-content">
    <div class="lmnd-kit-card">
      <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Sales</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£1,240.50</span></div>
    </div>
  </main>
</div>
```

**Don't.** A `bgDefault` screen, or `lmnd-kit-screen--default`, holding a card with the Elevated
background: `Card(background = LemonadeCardBackground.Elevated)`, `background: .elevated` or
`lmnd-kit-card--elevated`.

## `LDS-CARD-PADDING` · Padding follows what the card holds

**Rule.** A card of list items uses `None`, which is the component default. A card of custom content
uses `Medium`.

**Why.** List items carry their own padding and their dividers have to reach the card's edges. Custom
content carries none, so without the card's padding it sits against the edge.

**Exceptions.** A card whose sections mix the two keeps `None` and pads the custom section itself, so
the list rows still reach the edges.

**Example.** A payout's card of rows, then a card of text saying when the money arrives.

Compose.

```kotlin
LemonadeUi.Card {
    LemonadeUi.ContentListItem(label = "Amount", value = "£1,240.50", showDivider = true)
    LemonadeUi.ContentListItem(label = "Paid to", value = "Barclays •• 2210")
}
LemonadeUi.Card(contentPadding = LemonadeCardPadding.Medium) {
    LemonadeUi.Text(text = "Payouts reach your bank the next working day.")
}
```

SwiftUI.

```swift
LemonadeUi.Card {
    LemonadeUi.ContentListItem(label: "Amount", value: "£1,240.50", showDivider: true)
    LemonadeUi.ContentListItem(label: "Paid to", value: "Barclays •• 2210")
}
LemonadeUi.Card(contentPadding: .medium) {
    LemonadeUi.Text("Payouts reach your bank the next working day.")
}
```

HTML kit. A card with no padding class is `None`.

```html
<div class="lmnd-kit-card">
  <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Amount</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£1,240.50</span></div>
  <hr class="lmnd-kit-divider">
  <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Paid to</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">Barclays •• 2210</span></div>
</div>
<div class="lmnd-kit-card lmnd-kit-card--pad-medium">
  <div class="lmnd-kit-card__body"><p class="lmnd-text-body-medium-regular">Payouts reach your bank the next working day.</p></div>
</div>
```

**Don't.** `Card(contentPadding = None)` (or a bare `lmnd-kit-card`) wrapping a hand-built row — that
combination is usually a list item being rebuilt, which `LDS-LIST-NO-HANDROLL` covers.
