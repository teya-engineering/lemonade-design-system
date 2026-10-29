# Top bar

## `LDS-TOP-BAR-ACTIONS` · Top bar buttons are Solid or Subtle

**Rule.** A top bar's buttons are small circular icon buttons, or a single small text button such as Reset
or Done, and their type says what each action is:

- **The primary action**, the one thing the screen is for, such as adding or creating, is Primary · Solid.
- **Every other action** is Neutral · Subtle.
- **A text action** takes the same two treatments: Primary · Solid for a primary action such as Save,
  Neutral · Subtle for any other, such as Reset.
- **Back and Close** are secondary actions, so they are Neutral · Subtle too.
- **A destructive action** is Critical · Subtle instead (`LDS-TOP-BAR-CRITICAL`).

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

## `LDS-TOP-BAR-CRITICAL` · A destructive bar action is Critical · Subtle

**Rule.** A destructive action, such as remove, delete or cancel an invite, that sits in a top bar or a
sheet bar as a button is Critical · Subtle: a `bgCriticalSubtle` fill with a `contentCritical` glyph or
label. It keeps the bar's small circular shape (`LDS-TOP-BAR-ACTIONS`). Inside the bar's menu it is a
critical item instead. Whether it is a bar button or a menu item is the product's call.

**Why.** A destructive action has to look different from the bar's other actions before it is pressed.
A critical glyph on a neutral fill is easy to miss next to Subtle buttons; the critical fill and glyph
together mark it the same way everywhere.

**Exceptions.** On iOS 26 and later the system bar draws every button as its own neutral control, so a
destructive action is that control with a `contentCritical` glyph.

**Example.** A screen for removing the members picked on it.

Compose.

```kotlin
LemonadeUi.IconButton(
    icon = LemonadeIcons.Trash,
    contentDescription = "Remove selected members",
    onClick = onRemove,
    variant = LemonadeButtonVariant.Critical,
    type = LemonadeButtonType.Subtle,
    size = LemonadeButtonSize.Small,
    shape = LemonadeIconButtonShape.Circular,
)
```

SwiftUI. `.critical` draws the bar's neutral control with a `contentCritical` glyph on iOS 26 and
later, and `.plain` before that.

```swift
ToolbarItem(placement: .navigationBarTrailing) {
    LemonadeUi.ToolbarIconButton(
        icon: .trash,
        contentDescription: "Remove selected members",
        prominence: .critical,
        action: onRemove
    )
}
```

HTML kit.

```html
<div class="lmnd-kit-top-bar__trailing">
  <button type="button" class="lmnd-kit-icon-button lmnd-kit-icon-button--critical lmnd-kit-icon-button--small lmnd-kit-icon-button--circular" aria-label="Remove selected members"><span class="lmnd-icon" data-icon="trash"></span></button>
</div>
```

**Don't.** A Neutral · Subtle button with a critical glyph outside iOS 26, or Critical · Solid, which is
for the confirming button in a dialog.

## `LDS-TOP-BAR-DESCRIPTION` · A large title's description sits right under it

**Rule.** A screen with a large title puts its description in the title block, directly under the title,
with no gap. It is never a paragraph of its own in the content. It follows `LDS-TEXT-INTRO`: at most two
lines of `bodySmallRegular` in `contentSecondary`.

**Why.** The title and its description are one unit, and the top bar draws them as one. A description
written as its own paragraph takes the content's gap above it, drifts away from the title it explains
and reads as the start of the content.

**Exceptions.** A screen with an inline title has no title block to hold it. Its intro is the content's
first paragraph.

**Example.** A role screen titled "What can Dan do?", described as "Tick what this role can do".

Compose. The top bar's `subtitle` is the description.

```kotlin
LemonadeUi.TopBar(
    label = "What can Dan do?",
    subtitle = "Tick what this role can do",
)
```

SwiftUI. The top bar's `subheading` is the description. Before iOS 26 a top bar with a subheading is
always inline, with the two stacked in the bar.

```swift
RolePermissions()
    .lemonadeTopBar(
        label: "What can Dan do?",
        subheading: "Tick what this role can do"
    )
```

HTML kit.

```html
<div class="lmnd-kit-large-title">
  <h1 class="lmnd-text-heading-large">What can Dan do?</h1>
  <p class="lmnd-kit-large-title__description lmnd-text-body-small-regular">Tick what this role can do</p>
</div>
```

**Don't.** The description as a paragraph of its own after the title, with the content's gap above it.
