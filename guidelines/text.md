# Text

## `LDS-TEXT-INTRO` · An intro is short and secondary

**Rule.** An intro sits below the title, runs to at most two lines, and uses `bodySmallRegular` in
`contentSecondary`. The same holds for any description or subheading under a heading.

**Why.** An intro is read on the way to the content. Past two lines it becomes the content, pushing
what the screen is actually for below the fold. In `contentPrimary`, or at a larger body size, it
competes with the title it sits under.

**Exceptions.** None. Longer explanation belongs in the content — a card of list items, or its own
section — not above it.

**Example.** A Card readers section, with one line under its title saying what it lists.

Compose.

```kotlin
LemonadeUi.Text(
    text = "Card readers",
    textStyle = LemonadeTheme.typography.headingSmall,
)
LemonadeUi.Text(
    text = "Readers paired with this account.",
    textStyle = LemonadeTheme.typography.bodySmallRegular,
    color = LemonadeTheme.colors.content.contentSecondary,
)
```

SwiftUI.

```swift
LemonadeUi.Text(
    "Card readers",
    textStyle: LemonadeTypography.shared.headingSmall
)
LemonadeUi.Text(
    "Readers paired with this account.",
    textStyle: LemonadeTypography.shared.bodySmallRegular,
    color: LemonadeTheme.colors.content.contentSecondary
)
```

HTML kit. `lmnd-kit-intro` sets `contentSecondary`; the `lmnd-text-*` class sets the type.

```html
<h2 class="lmnd-text-heading-small">Card readers</h2>
<p class="lmnd-kit-intro lmnd-text-body-small-regular">Readers paired with this account.</p>
```

**Don't.** `bodyMediumRegular` or `bodyLargeRegular` for an intro, a `Text` left at its default
`contentPrimary`, a four-line paragraph, or a second paragraph beneath the first.

## `LDS-TEXT-STYLE` · Text uses a typography token

**Rule.** Every piece of text takes one of the Lemonade typography styles, in a semantic content colour.
No font size, weight, line height or letter spacing is set at the call site.

**Why.** A style carries the family, size, weight, line height and tracking as one decision. Changing
one of them by hand makes a style the system doesn't have, which drifts when the scale changes.

**Exceptions.** None. A size the scale lacks is a request for the design system, not a local value.

**Example.** A row's "Total" label, which needs to be bolder than the rows above it.

Compose.

```kotlin
LemonadeUi.Text(
    text = "Total",
    textStyle = LemonadeTheme.typography.bodyMediumSemiBold,
)
```

SwiftUI.

```swift
LemonadeUi.Text(
    "Total",
    textStyle: LemonadeTypography.shared.bodyMediumSemiBold
)
```

HTML kit.

```html
<span class="lmnd-text-body-medium-semibold">Total</span>
```

**Don't.** `bodyMediumRegular.copy(fontWeight = FontWeight.Bold)` or `fontSize = 15.sp` in Compose, `fontSize: 15` or
`.font(.system(size: 15, weight: .bold))` in SwiftUI, or a `font-size` or `font-weight` on top of an
`lmnd-text-*` class.

## `LDS-TEXT-HEADING-DISPLAY` · Headings structure, display makes a statement

**Rule of thumb.** A `heading` style titles a screen, a section or a component — it says where you
are. A `display` style is for a figure that is the point of the screen: a balance, a total, a big
statistic. A `body` style is everything that is read: copy, labels, values in a row.
`bodyXSmallOverline` labels a group from above.

**Why.** A heading orders the screen and display stops the eye. A section title in a display style
outranks the content it introduces, and an amount in a heading style reads as a title.

**Exceptions.** An amount that is one value among many — a transaction amount in a row — is body, not
display.

**Example.** An account balance at the top of a screen, with a Recent payouts section below it.

Compose.

```kotlin
LemonadeUi.Text(
    text = "£1,240.50",
    textStyle = LemonadeTheme.typography.displayMedium,
)
LemonadeUi.Text(
    text = "Recent payouts",
    textStyle = LemonadeTheme.typography.headingSmall,
)
```

SwiftUI.

```swift
LemonadeUi.Text(
    "£1,240.50",
    textStyle: LemonadeTypography.shared.displayMedium
)
LemonadeUi.Text(
    "Recent payouts",
    textStyle: LemonadeTypography.shared.headingSmall
)
```

HTML kit.

```html
<p class="lmnd-text-display-medium">£1,240.50</p>
<h2 class="lmnd-text-heading-small">Recent payouts</h2>
```

**Don't.** `displayLarge` for a screen title, `headingLarge` for a balance, or a display style for a
transaction amount in a row.
