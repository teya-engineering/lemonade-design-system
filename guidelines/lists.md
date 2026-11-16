# List items

## `LDS-LIST-NO-HANDROLL` · A row is a DS list item

**Rule.** A row carrying an icon and text is a DS list item. A label-and-value row is
`ContentListItem`; a tappable row is `ActionListItem`.

**Why.** The components own the row's height, its internal spacing, its slot alignment and its
divider. A hand-built row agrees with them until any of those move, and then it is the one row on the
screen at the wrong height.

**Exceptions.** A row whose layout no list item models — two values side by side under one label, a
row built around a chart. State the reason at the site.

**Example.** A Notifications row in Settings that opens its own screen.

Compose.

```kotlin
LemonadeUi.ActionListItem(
    label = "Notifications",
    leadingSlot = {
        LemonadeUi.Icon(
            icon = LemonadeIcons.Bell,
            contentDescription = null,
            tint = LemonadeTheme.colors.content.contentSecondary,
        )
    },
    showNavigationIndicator = true,
    onItemClicked = onOpenNotifications,
)
```

SwiftUI.

```swift
LemonadeUi.ActionListItem(
    label: "Notifications",
    showNavigationIndicator: true,
    onItemClicked: onOpenNotifications,
    leadingSlot: {
        LemonadeUi.Icon(
            icon: .bell,
            contentDescription: nil,
            tint: LemonadeTheme.colors.content.contentSecondary
        )
    }
)
```

HTML kit. An `lmnd-kit-list-item` on an `<a>` or a `<button>` is tappable; a label-and-value row is an
`lmnd-kit-content-item`.

```html
<a href="#notifications" class="lmnd-kit-list-item">
  <span class="lmnd-kit-list-item__row">
    <span class="lmnd-kit-list-item__leading"><span class="lmnd-icon" data-icon="bell" aria-hidden="true"></span></span>
    <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Notifications</span></span>
    <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
  </span>
</a>
```

**Don't.** A `Row` or `HStack` of an icon, a `Text` and a `Spacer`, or a flex `<div>` of an icon and a
label in place of `lmnd-kit-list-item`.

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

**Example.** A Settings card whose Language row only needs a glyph to scan by, and a Stores card whose
rows could carry a store's logo.

Compose.

```kotlin
LemonadeUi.Card {
    LemonadeUi.ActionListItem(
        label = "Language",
        leadingSlot = {
            LemonadeUi.Icon(
                icon = LemonadeIcons.Globe,
                contentDescription = null,
                tint = LemonadeTheme.colors.content.contentSecondary,
            )
        },
        showNavigationIndicator = true,
        onItemClicked = onOpenLanguage,
    )
}
LemonadeUi.Card {
    LemonadeUi.ActionListItem(
        label = "Camden Market",
        leadingSlot = {
            LemonadeUi.SymbolContainer(
                icon = LemonadeIcons.Store,
                contentDescription = null,
                shape = SymbolContainerShape.Rounded,
            )
        },
        showNavigationIndicator = true,
        onItemClicked = onOpenStore,
    )
}
```

SwiftUI.

```swift
LemonadeUi.Card {
    LemonadeUi.ActionListItem(
        label: "Language",
        showNavigationIndicator: true,
        onItemClicked: onOpenLanguage,
        leadingSlot: {
            LemonadeUi.Icon(
                icon: .globe,
                contentDescription: nil,
                tint: LemonadeTheme.colors.content.contentSecondary
            )
        }
    )
}
LemonadeUi.Card {
    LemonadeUi.ActionListItem(
        label: "Camden Market",
        showNavigationIndicator: true,
        onItemClicked: onOpenStore,
        leadingSlot: {
            LemonadeUi.SymbolContainer(icon: .store, contentDescription: nil, shape: .rounded)
        }
    )
}
```

HTML kit. A bare `lmnd-icon` in `lmnd-kit-list-item__leading` takes `contentSecondary` on its own.

```html
<div class="lmnd-kit-card">
  <a href="#language" class="lmnd-kit-list-item">
    <span class="lmnd-kit-list-item__row">
      <span class="lmnd-kit-list-item__leading"><span class="lmnd-icon" data-icon="globe" aria-hidden="true"></span></span>
      <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Language</span></span>
      <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
    </span>
  </a>
</div>
<div class="lmnd-kit-card">
  <a href="#store" class="lmnd-kit-list-item">
    <span class="lmnd-kit-list-item__row">
      <span class="lmnd-kit-list-item__leading"><span class="lmnd-kit-symbol lmnd-kit-symbol--rounded"><span class="lmnd-icon" data-icon="store" aria-hidden="true"></span></span></span>
      <span class="lmnd-kit-list-item__content"><span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Camden Market</span></span>
      <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
    </span>
  </a>
</div>
```

**Don't.** One card with some rows on a `SymbolContainer` and some on a plain icon, a bare glyph tinted
`contentCritical` or `contentPositive` where a voiced `SymbolContainer` belongs, or a plain icon in
any colour but `contentSecondary`.

## `LDS-LIST-SYMBOL-SHAPE` · A symbol in a row's leading slot is rounded

**Rule.** When a row's leading slot holds a `SymbolContainer` (see `LDS-LIST-LEADING-SLOT`), it uses the
rounded shape, never the circle. This holds for every list row: `ActionListItem`, `ResourceListItem`,
`SelectListItem` in both variants, and any other `ListItem`.

**Why.** One shape down a list keeps its left edge reading as one column. The rounded square is the list
shape; the circle is the component's default on every platform, so a row that doesn't ask for rounded
gets the wrong one.

**Exceptions.** None in a list row. A `SymbolContainer` outside one — a screen's hero, a badge host —
keeps whichever shape its layout calls for.

**Example.** A store row leading with a store symbol.

Compose.

```kotlin
LemonadeUi.ActionListItem(
    label = "Camden Market",
    supportText = "2 terminals",
    leadingSlot = {
        LemonadeUi.SymbolContainer(
            icon = LemonadeIcons.Store,
            contentDescription = null,
            shape = SymbolContainerShape.Rounded,
        )
    },
    showNavigationIndicator = true,
    onItemClicked = onOpenStore,
)
```

SwiftUI.

```swift
LemonadeUi.ActionListItem(
    label: "Camden Market",
    supportText: "2 terminals",
    showNavigationIndicator: true,
    onItemClicked: onOpenStore,
    leadingSlot: {
        LemonadeUi.SymbolContainer(icon: .store, contentDescription: nil, shape: .rounded)
    }
)
```

HTML kit.

```html
<a href="#store" class="lmnd-kit-list-item">
  <span class="lmnd-kit-list-item__row">
    <span class="lmnd-kit-list-item__leading"><span class="lmnd-kit-symbol lmnd-kit-symbol--rounded"><span class="lmnd-icon" data-icon="store" aria-hidden="true"></span></span></span>
    <span class="lmnd-kit-list-item__content">
      <span class="lmnd-kit-list-item__label lmnd-text-body-medium-medium">Camden Market</span>
      <span class="lmnd-kit-list-item__support lmnd-text-body-small-regular">2 terminals</span>
    </span>
    <span class="lmnd-kit-list-item__trailing"><span class="lmnd-icon lmnd-kit-list-item__chevron" data-icon="chevron-right" aria-hidden="true"></span></span>
  </span>
</a>
```

**Don't.** A `SymbolContainer` with no `shape`, which is a circle, or an `lmnd-kit-symbol` without
`lmnd-kit-symbol--rounded`.
