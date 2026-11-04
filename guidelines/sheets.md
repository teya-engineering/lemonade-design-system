# Sheets

## `LDS-SHEET-CHROME` · The scaffold owns the chrome

**Rule.** A sheet's title and close button belong to the scaffold. A sheet closes with `Close`, never
`Back`.

**Why.** Hand-built chrome gets the title's typography, its collapse behaviour and the close button's
position slightly wrong, and every sheet gets them wrong differently. `Back` on a sheet promises a
screen underneath that the gesture does not go to.

**Exceptions.** A sheet that is genuinely a step inside a sheet-hosted flow may carry `Back` to reach
the previous step, as long as the flow's root still closes with `Close`.

**Example.** A sheet titled "Filter by payment method" that closes with Close.

Compose. The bottom sheet draws only its surface and handle, so the top bar is its first child.

```kotlin
LemonadeUi.BottomSheet(
    expanded = showFilters,
    onDismissRequest = onDismiss,
) {
    LemonadeUi.TopBar(
        label = "Filter by payment method",
        navigationAction = NavigationAction(
            navigationAction = TopBarAction.Close,
            onNavigationActionClicked = onDismiss,
            filled = true,
        ),
    )
    PaymentMethodOptions()
}
```

SwiftUI. The sheet hosts a `NavigationStack`, and the top bar draws Close in the navigation bar.

```swift
Transactions()
    .sheet(isPresented: $showFilters) {
        NavigationStack {
            PaymentMethodOptions()
                .lemonadeTopBar(
                    label: "Filter by payment method",
                    navigationAction: NavigationAction(action: .close, onAction: { showFilters = false })
                )
        }
    }
```

HTML kit.

```html
<div class="lmnd-kit-sheet" role="dialog" aria-labelledby="filters-title">
  <span class="lmnd-kit-sheet__handle"></span>
  <div class="lmnd-kit-sheet__bar">
    <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Close"><span class="lmnd-icon" data-icon="times"></span></button>
  </div>
  <h2 class="lmnd-kit-sheet__title lmnd-text-heading-large" id="filters-title">Filter by payment method</h2>
  <div class="lmnd-kit-sheet__body">…</div>
</div>
```

**Don't.** A title `Text` and an icon button laid out at the top of the sheet's content, `Back` or
`arrow-left` at a sheet's root, or a drag handle on top of the scaffold's own spacing.

## `LDS-SHEET-BAR-ACTIONS` · A sheet's bar follows the top bar's button rules

**Rule.** The buttons in a sheet's bar — the scaffold's close slot and any actions beside it — follow
`LDS-TOP-BAR-ACTIONS`, exactly as in a screen's top bar: small circular icon buttons, or one small text
button such as Reset. The sheet's primary action, if the bar has one, is Primary · Solid, and every other
action, Close included, is Neutral · Subtle.

**Why.** A sheet's bar sits where a top bar sits and does the same job. Different buttons there make the
same action look different depending on whether it opened as a screen or as a sheet.

**Exceptions.** None.

**Example.** A Team sheet: Close, and Add member as the primary action.

Compose. Close needs `filled = true`; the default draws a ghost button.

```kotlin
LemonadeUi.TopBar(
    label = "Team",
    navigationAction = NavigationAction(
        navigationAction = TopBarAction.Close,
        onNavigationActionClicked = onDismiss,
        filled = true,
    ),
    trailingSlot = {
        LemonadeUi.IconButton(
            icon = LemonadeIcons.Plus,
            contentDescription = "Add member",
            onClick = onAdd,
            variant = LemonadeButtonVariant.Primary,
            type = LemonadeButtonType.Solid,
            size = LemonadeButtonSize.Small,
            shape = LemonadeIconButtonShape.Circular,
        )
    },
)
```

SwiftUI. The navigation bar draws Close and `.plain` as its own subtle control; `.filled(tint:)` marks the
primary action.

```swift
TeamList()
    .lemonadeTopBar(
        label: "Team",
        navigationAction: NavigationAction(action: .close, onAction: onDismiss)
    ) {
        ToolbarItem(placement: .navigationBarTrailing) {
            LemonadeUi.ToolbarIconButton(
                icon: .plus,
                contentDescription: "Add member",
                prominence: .filled(tint: LemonadeTheme.colors.background.bgBrand),
                action: onAdd
            )
        }
    }
```

HTML kit. An icon button with no variant or type class is Neutral · Subtle.

```html
<div class="lmnd-kit-sheet__bar">
  <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Close"><span class="lmnd-icon" data-icon="times"></span></button>
  <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Add member"><span class="lmnd-icon" data-icon="plus"></span></button>
</div>
```

A filter sheet's text action: Close, and Reset.

Compose. Reset is a small Neutral · Subtle `Button`.

```kotlin
LemonadeUi.TopBar(
    label = "Filters",
    navigationAction = NavigationAction(
        navigationAction = TopBarAction.Close,
        onNavigationActionClicked = onDismiss,
        filled = true,
    ),
    trailingSlot = {
        LemonadeUi.Button(
            label = "Reset",
            onClick = onReset,
            variant = LemonadeButtonVariant.Neutral,
            type = LemonadeButtonType.Subtle,
            size = LemonadeButtonSize.Small,
        )
    },
)
```

SwiftUI. Reset is a plain `Button` holding a Lemonade `Text`, which the navigation bar draws as its own
control, as it does `.plain`.

```swift
FilterOptions()
    .lemonadeTopBar(
        label: "Filters",
        navigationAction: NavigationAction(action: .close, onAction: onDismiss)
    ) {
        ToolbarItem(placement: .navigationBarTrailing) {
            Button(action: onReset) {
                LemonadeUi.Text("Reset", textStyle: LemonadeTypography.shared.bodyMediumMedium)
            }
        }
    }
```

HTML kit. `lmnd-kit-button--full-shape` gives the button its pill shape.

```html
<div class="lmnd-kit-sheet__bar">
  <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Close"><span class="lmnd-icon" data-icon="times"></span></button>
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--small lmnd-kit-button--full-shape lmnd-text-body-small-semibold"><span class="lmnd-button__label">Reset</span></button>
</div>
```

**Don't.** A bare `Text` link in the bar, `IconButton(type = Ghost)` or `lmnd-kit-icon-button--ghost`,
a Close `NavigationAction` left at `filled = false`, or a SwiftUI `LemonadeUi.IconButton` inside a
`ToolbarItem`.
