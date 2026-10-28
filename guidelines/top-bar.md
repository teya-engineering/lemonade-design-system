# Top bar

## `LDS-TOP-BAR-ACTIONS` · Top bar buttons are Solid or Subtle

**Rule.** A top bar's buttons are small circular icon buttons, or a single small text button such as Reset
or Done, and their type says what each action is:

- **The primary action**, the one thing the screen is for, such as adding or creating, is Primary · Solid.
- **Every other action** is Neutral · Subtle.
- **A text action** takes the same two treatments: Primary · Solid for a primary action such as Save,
  Neutral · Subtle for any other, such as Reset.
- **Back and Close** are secondary actions, so they are Neutral · Subtle too.

**Why.** A filled button reads as something to press. A ghost icon on the top bar's background looks like
a label rather than a control, and two treatments for the same weight of action make the reader work out
which one matters. Solid marks the one action the screen wants; subtle keeps every other action visible
without competing with it.

**Exceptions.** None. A ghost button never goes in a top bar.

**Example.** A pushed Recipients screen: Back, a Download action, and Add as the primary action.

Compose. Back needs `filled = true`; the default draws a ghost button.

```kotlin
LemonadeUi.TopBar(
    label = "Recipients",
    navigationAction = NavigationAction(
        navigationAction = TopBarAction.Back,
        onNavigationActionClicked = onBack,
        filled = true,
    ),
    trailingSlot = {
        LemonadeUi.IconButton(
            icon = LemonadeIcons.Download,
            contentDescription = "Download recipients",
            onClick = onDownload,
            variant = LemonadeButtonVariant.Neutral,
            type = LemonadeButtonType.Subtle,
            size = LemonadeButtonSize.Small,
            shape = LemonadeIconButtonShape.Circular,
        )
        LemonadeUi.IconButton(
            icon = LemonadeIcons.Plus,
            contentDescription = "Add recipient",
            onClick = onAdd,
            variant = LemonadeButtonVariant.Primary,
            type = LemonadeButtonType.Solid,
            size = LemonadeButtonSize.Small,
            shape = LemonadeIconButtonShape.Circular,
        )
    },
)
```

SwiftUI. The system bar draws `.plain` as its own filled control, which is the subtle treatment, and it
draws Back the same way.

```swift
RecipientsList()
    .lemonadeTopBar(
        label: "Recipients",
        navigationAction: NavigationAction(action: .back, onAction: onBack)
    ) {
        ToolbarItem(placement: .navigationBarTrailing) {
            LemonadeUi.ToolbarIconButton(
                icon: .download,
                contentDescription: "Download recipients",
                action: onDownload
            )
        }
        ToolbarItem(placement: .navigationBarTrailing) {
            LemonadeUi.ToolbarIconButton(
                icon: .plus,
                contentDescription: "Add recipient",
                prominence: .filled(tint: LemonadeTheme.colors.background.bgBrand),
                action: onAdd
            )
        }
    }
```

HTML kit. An icon button with no variant or type class is Neutral · Subtle.

```html
<header class="lmnd-kit-top-bar lmnd-kit-top-bar--small">
  <div class="lmnd-kit-top-bar__bar">
    <div class="lmnd-kit-top-bar__leading">
      <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Back"><span class="lmnd-icon" data-icon="arrow-left"></span></button>
    </div>
    <div class="lmnd-kit-top-bar__center"><span class="lmnd-kit-top-bar__title lmnd-text-heading-xxsmall">Recipients</span></div>
    <div class="lmnd-kit-top-bar__trailing">
      <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Download recipients"><span class="lmnd-icon" data-icon="download"></span></button>
      <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Add recipient"><span class="lmnd-icon" data-icon="plus"></span></button>
    </div>
  </div>
</header>
```

**Don't.** `IconButton(type = Ghost)` or `lmnd-kit-icon-button--ghost` in either slot, a `NavigationAction`
left at `filled = false`, or a SwiftUI `LemonadeUi.IconButton` inside a `ToolbarItem`.
