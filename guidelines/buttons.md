# Buttons

## `LDS-BUTTON-PAIR` · At most two actions, primary above secondary

**Rule.** A screen offers at most two actions, the primary above the secondary.

The primary is the button's default, Primary · Solid · Large, so it needs no variant, type or size. The
secondary in the hierarchy is always Neutral · Subtle, written out in full. A destructive primary is
Critical · Solid over the same secondary.

**Why.** Two buttons of equal weight ask the user to decide which one the screen wants. A third action
means the screen is doing two jobs.

**`Secondary` is a branded treatment, not a position in the hierarchy.** It never means "the second
button".

**Exceptions.** A third action belongs in the top bar or in the content, not in the action block.

**Example.** The last step of inviting a member: Send invite above Not now.

Compose.

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200)) {
    LemonadeUi.Button(label = "Send invite", onClick = onSend)
    LemonadeUi.Button(
        label = "Not now",
        onClick = onSkip,
        variant = LemonadeButtonVariant.Neutral,
        type = LemonadeButtonType.Subtle,
    )
}
```

SwiftUI.

```swift
VStack(spacing: .space.spacing200) {
    LemonadeUi.Button(label: "Send invite", onClick: onSend)
    LemonadeUi.Button(
        label: "Not now",
        onClick: onSkip,
        variant: .neutral,
        type: .subtle
    )
}
```

HTML kit. `lmnd-button` has no default variant, type or size, so the primary names its classes too.
`lmnd-kit-actions` is the action block.

```html
<div class="lmnd-kit-actions">
  <button type="button" class="lmnd-button lmnd-button--primary lmnd-button--solid lmnd-button--large lmnd-text-body-medium-semibold"><span class="lmnd-button__label">Send invite</span></button>
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--large lmnd-text-body-medium-semibold"><span class="lmnd-button__label">Not now</span></button>
</div>
```

**Don't.** Two bare `LemonadeUi.Button(label = …, onClick = …)` calls stacked, or two
`lmnd-button--primary` buttons: both are primaries. Not `Secondary` for the second button either.

## `LDS-BUTTON-EXPLICIT-TYPE` · Only a primary relies on the defaults

**Rule.** Any variant other than Primary states its type.

**Why.** `variant` and `type` are independent enums and `type` defaults to `Solid`. So
`variant = Neutral` with no `type` renders a solid neutral button — a filled button where a subtle one
was meant.

**Exceptions.** None. A primary may omit both, which is what makes two stacked primaries detectable:
a call with no arguments *is* a primary solid.

**Example.** A Neutral · Subtle Download statement button in a payout's details.

Compose.

```kotlin
LemonadeUi.Button(
    label = "Download statement",
    onClick = onDownload,
    variant = LemonadeButtonVariant.Neutral,
    type = LemonadeButtonType.Subtle,
    size = LemonadeButtonSize.Medium,
)
```

SwiftUI.

```swift
LemonadeUi.Button(
    label: "Download statement",
    onClick: onDownload,
    variant: .neutral,
    type: .subtle,
    size: .medium
)
```

HTML kit. `lmnd-button` has no defaults: every button names its variant, type and size.

```html
<button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--medium lmnd-text-body-medium-semibold"><span class="lmnd-button__label">Download statement</span></button>
```

**Don't.** `variant = Neutral` in Compose or `variant: .neutral` in SwiftUI with no type, which draws a
solid neutral button.
