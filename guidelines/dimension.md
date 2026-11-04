# Dimension

Spacing, radius, size and border width. Spacing, radius and size are built on a 4 px unit, and a
token's number is its value divided by 4, times 100: `spacing-400` is 16 px, `radius-150` is 6 px.

## `LDS-DIMENSION-SCALE` · Dimensions come from the scales

**Rule.** Padding, margins and gaps come from the spacing scale. Corner radii come from the radius scale.
Widths and heights come from the size scale. Stroke widths come from the border-width scale, and a
selection or focus outline is `borderSelected` or `focusRing`. No `dp`, `pt` or `px` literal stands in
for any of them.

**Why.** A literal that matches a token today stays put when the token moves. One that matches no
token — 15, 18, 22 — breaks the rhythm the scale exists to keep.

**Exceptions.**

- A size the scale lacks, fixed by something outside the layout: an image's aspect, an illustration's
  artwork, a platform control's height.
- `0`, written as `spacing0` or `radius0` when it is a decision rather than an absence.

**Example.** A merchant's logo beside the business name, padded 16 with a 12 gap.

Compose. The scales are `LemonadeTheme.spaces`, `radius` (`shapes` for a `Shape`), `sizes` and
`borderWidths`, whose `state` holds `borderSelected` and `focusRing`.

```kotlin
Row(
    modifier = Modifier.padding(LemonadeTheme.spaces.spacing400),
    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
    verticalAlignment = Alignment.CenterVertically,
) {
    Image(
        painter = merchantLogo,
        contentDescription = null,
        modifier = Modifier
            .size(LemonadeTheme.sizes.size1000)
            .clip(LemonadeTheme.shapes.radius300),
    )
    LemonadeUi.Text(text = "Camden Coffee")
}
```

SwiftUI. The same scales, except border widths are `LemonadeTheme.borderWidth`.

```swift
HStack(spacing: LemonadeTheme.spaces.spacing300) {
    Image("merchant-logo")
        .resizable()
        .frame(width: LemonadeTheme.sizes.size1000, height: LemonadeTheme.sizes.size1000)
        .clipShape(LemonadeTheme.shapes.radius300)
    LemonadeUi.Text("Camden Coffee")
}
.padding(LemonadeTheme.spaces.spacing400)
```

HTML kit. The scales are `--lmnd-spacing-*`, `--lmnd-radius-*`, `--lmnd-size-*` and
`--lmnd-border-width-*`, with `--lmnd-border-width-selected` and `--lmnd-border-width-focus-ring`.

```html
<div style="display: flex; align-items: center; gap: var(--lmnd-spacing-300); padding: var(--lmnd-spacing-400)">
  <img src="merchant-logo.png" alt="" style="width: var(--lmnd-size-1000); height: var(--lmnd-size-1000); border-radius: var(--lmnd-radius-300)">
  <span class="lmnd-text-body-medium-medium">Camden Coffee</span>
</div>
```

**Don't.** `.padding(16.dp)`, `.padding(16)` or `padding: 16px`, even though it is the same value, or a
15, 18 or 22 that matches no token.

## `LDS-DIMENSION-EDGE` · Content sits 16 from the edge it belongs to

**Rule.** Content sits `spacing400` from the edge of the screen, and `spacing400` from the edge of a
visible container. The distance is measured to what you see, not to the box.

- A list item always sits inside a card (`LDS-CARD-GROUPED-ITEMS`), and the card sits on the margin.
  The item's own padding, where its pressed background is drawn, is its inset inside the card, so add
  none of your own.
- Inside a container that has its own padding, such as a `Card` with Medium padding, add no margin of
  your own.
- A large container pads the same on all four sides.

**Why.** Every screen's content starts on the same line, so a list, a card and a heading under one
another read as one column. A list item wrapped in a 16 padding inside its card puts its text 32 from
the card's edge, out of line with everything else.

**Exceptions.**

- Optical alignment: a glyph or a shape that looks off at the exact value may be moved to look right.
- A smaller container may use less.
- Margins on a tablet or a terminal are not defined here; this rule covers phone-width layouts.

**Example.** A Payouts screen: a card of two action list items, then a Medium card of text, both 16
from the screen's edge.

Compose.

```kotlin
Column(
    modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing400),
    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
) {
    LemonadeUi.Card {
        LemonadeUi.ActionListItem(
            label = "Bank account",
            onItemClicked = onBankAccount,
            showNavigationIndicator = true,
            showDivider = true,
        )
        LemonadeUi.ActionListItem(
            label = "Payout schedule",
            onItemClicked = onSchedule,
            showNavigationIndicator = true,
        )
    }
    LemonadeUi.Card(contentPadding = LemonadeCardPadding.Medium) {
        LemonadeUi.Text(text = "Payouts reach your bank in one working day.")
    }
}
```

SwiftUI.

```swift
VStack(spacing: LemonadeTheme.spaces.spacing400) {
    LemonadeUi.Card {
        LemonadeUi.ActionListItem(
            label: "Bank account",
            showNavigationIndicator: true,
            showDivider: true,
            onItemClicked: onBankAccount
        )
        LemonadeUi.ActionListItem(
            label: "Payout schedule",
            showNavigationIndicator: true,
            onItemClicked: onSchedule
        )
    }
    LemonadeUi.Card(contentPadding: .medium) {
        LemonadeUi.Text("Payouts reach your bank in one working day.")
    }
}
.padding(.horizontal, LemonadeTheme.spaces.spacing400)
```

HTML kit. `lmnd-kit-content` holds the screen's margin.

```html
<main class="lmnd-kit-content">
  <div class="lmnd-kit-card">
    <a href="#" class="lmnd-kit-list-item lmnd-kit-list-item--divider">
      <span class="lmnd-kit-list-item__row">
        <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Bank account</span></span>
        <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
      </span>
    </a>
    <a href="#" class="lmnd-kit-list-item">
      <span class="lmnd-kit-list-item__row">
        <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Payout schedule</span></span>
        <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
      </span>
    </a>
  </div>
  <div class="lmnd-kit-card lmnd-kit-card--pad-medium">
    <div class="lmnd-kit-card__body"><p class="lmnd-text-body-medium-regular">Payouts reach your bank in one working day.</p></div>
  </div>
</main>
```

**Don't.** List items straight on the screen with no card, padding of your own around list items inside
a card, a Medium card around list items, or a margin of your own inside a Medium card.

## `LDS-DIMENSION-GAPS` · The gap follows what is being separated

**Rule of thumb.**

| Between | Gap |
| --- | --- |
| Sections — a heading and its card, a group of mixed elements | `spacing800` |
| Cards, and other containers with a visible edge | `spacing400` |
| Outlined list items that stand alone, such as `SelectListItem` in its Outlined variant | `spacing200` |
| Small elements next to each other — tags, chips | `spacing200` |
| Plain interactive list items | none; their dividers are the items' own |
| Static items — `ContentListItem` in a card | `spacing400`, inside a `Medium` card |
| Stacked labels — a title and its subtitle | none |
| Paragraphs | the text style's line height |

**Why.** The gap says how closely two things belong together, so the same relationship has to read
the same everywhere. Labels need no gap because each text style's line height already spaces them.

**Exceptions.** Optical alignment, as in `LDS-DIMENSION-EDGE`. A gap between an icon and its label
inside one row is the row's own spacing, not a block gap.

**Example.** A Home screen: a Sales card and a Payouts card 16 apart, then a Recent transactions
section 32 below them.

Compose.

```kotlin
Column(
    modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing400),
    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing800),
) {
    Column(verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400)) {
        SalesCard()
        PayoutsCard()
    }
    RecentTransactionsSection()
}
```

SwiftUI.

```swift
VStack(spacing: LemonadeTheme.spaces.spacing800) {
    VStack(spacing: LemonadeTheme.spaces.spacing400) {
        SalesCard()
        PayoutsCard()
    }
    RecentTransactionsSection()
}
.padding(.horizontal, LemonadeTheme.spaces.spacing400)
```

HTML kit. `lmnd-kit-content` puts `spacing-400` between cards and `spacing-800` around a
`lmnd-kit-section`.

```html
<main class="lmnd-kit-content">
  <div class="lmnd-kit-card lmnd-kit-card--pad-medium">
    <div class="lmnd-kit-card__body"><p class="lmnd-text-body-medium-regular">Sales today: £1,240.50</p></div>
  </div>
  <div class="lmnd-kit-card lmnd-kit-card--pad-medium">
    <div class="lmnd-kit-card__body"><p class="lmnd-text-body-medium-regular">Next payout: £980.00 on Friday</p></div>
  </div>
  <section class="lmnd-kit-section">
    <h2 class="lmnd-kit-section__title lmnd-text-heading-xsmall">Recent transactions</h2>
    <div class="lmnd-kit-card">
      <div class="lmnd-kit-content-item"><span class="lmnd-kit-content-item__label lmnd-text-body-medium-regular">Jo Baker</span><span class="lmnd-kit-content-item__value lmnd-text-body-medium-medium">£24.00</span></div>
    </div>
  </section>
</main>
```

**Don't.** `spacing100` between a title and its subtitle, or a `HorizontalDivider` or `lmnd-kit-divider`
between action list items instead of their own `showDivider` or `lmnd-kit-list-item--divider`.

## `LDS-DIMENSION-RADIUS` · A container's radius is the container token

**Rule of thumb.** A container-sized surface — a card, a dialog, a sheet — uses
`radiusContainerDefault`. A self-contained item inside a layout — a tile, a notice, a standalone
select row — uses `radius500`. Pills and circles use `radiusFull`. Everything smaller takes its radius
from its component.

`Card` and `Dialog` use `radiusContainerDefault`. `Tile`, `Notice`, `SelectListItem`,
`ListItemHighlight` and `SwipeActionRow` use `radius500`. `Badge`, `Chip`, `SearchField`, `Switch` and
`Toast` use `radiusFull`.

An element drawn inside a rounded container takes the container's radius minus the container's
padding: inside a `radiusContainerDefault` card padded `spacing400`, that is `radius200`.

**Why.** `radiusContainerDefault` is a semantic token: it names the decision, so the containers move
together if it changes. Subtracting the padding keeps the inner and outer curves parallel, so the
inner element never looks rounder than its parent.

**Exceptions.** None for containers. A product that needs a container-sized surface the design system
doesn't have names `radiusContainerDefault`, never `radius600`, even though they are equal today.

**Example.** A custom media panel padded 16, with a photo of the terminal inside it.

Compose. The token is `semantic.radiusContainerDefault` on `LemonadeTheme.shapes`, or on `radius` as a
`Dp`.

```kotlin
Box(
    modifier = Modifier
        .clip(LemonadeTheme.shapes.semantic.radiusContainerDefault)
        .background(color = LemonadeTheme.colors.background.bgDefault)
        .padding(LemonadeTheme.spaces.spacing400),
) {
    Image(
        painter = terminalPhoto,
        contentDescription = "Teya terminal",
        modifier = Modifier.clip(LemonadeTheme.shapes.radius200),
    )
}
```

SwiftUI. The same path on `LemonadeTheme.shapes`, or on `LemonadeTheme.radius` as a `CGFloat`.

```swift
Image("terminal-photo")
    .resizable()
    .scaledToFit()
    .clipShape(LemonadeTheme.shapes.radius200)
    .padding(LemonadeTheme.spaces.spacing400)
    .background(LemonadeTheme.colors.background.bgDefault)
    .clipShape(LemonadeTheme.shapes.semantic.radiusContainerDefault)
```

HTML kit. The token is `--lmnd-radius-container-default`.

```html
<div style="padding: var(--lmnd-spacing-400); border-radius: var(--lmnd-radius-container-default); background: var(--lmnd-color-bg-default)">
  <img src="terminal.png" alt="Teya terminal" style="display: block; width: 100%; border-radius: var(--lmnd-radius-200)">
</div>
```

**Don't.** `radius600` or `--lmnd-radius-600` in place of the container token, `radius800` to make the
panel feel softer, or the container's own radius on the photo inside it.
