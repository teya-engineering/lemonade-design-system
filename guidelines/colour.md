# Colour

## `LDS-COLOR-SEMANTIC` · Semantic first, themed where the meaning is not modelled, primitives never

**Rule.** Three layers, in this order.

**Semantic** — `contentPrimary`, `bgSubtle` — whenever one fits, which is nearly always. A semantic
token names what the colour means, so it follows the theme and it survives a palette change.

**Themed** — theme-aware hues, each with a saturated and a subtle palette — for colour the design
system does not model: chart series, categories, per-role or per-product accents, a `SymbolContainer`
that carries no voice. The layer is experimental, so expect its call sites to be revisited.

**Primitive** — never in product code. The layer is marked internal, and its own documentation says to
use the theme instead. A primitive is as wrong as a hex literal and for the same reason: it cannot follow
the theme.

Pick a token by what it means, even when another holds the same value today. `bgAlwaysLight` and
`bgDefault` match in the light theme and part in the dark one.

**Why.** A primitive or a literal is a colour that looks right in the theme it was written in and
wrong in the other one.

**Exceptions.** None for primitives. Reach for the themed layer only after checking that no semantic
token carries the meaning.

**Example.** An error under a failed payment, and the fourth series in a sales chart.

Compose. The themed layer needs `@OptIn(ExperimentalLemonadeApi::class)`.

```kotlin
LemonadeUi.Text(
    text = "Payment failed",
    textStyle = LemonadeTheme.typography.bodySmallRegular,
    color = LemonadeTheme.colors.content.contentCritical,
)

@OptIn(ExperimentalLemonadeApi::class)
val refundsSeries = LemonadeTheme.themed.teal.background
```

SwiftUI. The themed layer has no opt-in here, but it is just as experimental.

```swift
LemonadeUi.Text(
    "Payment failed",
    textStyle: LemonadeTypography.shared.bodySmallRegular,
    color: LemonadeTheme.colors.content.contentCritical
)

let refundsSeries = LemonadeTheme.themed.teal.background
```

HTML kit. The package ships no primitive tokens, so the wrong layer here is a literal.

```html
<p class="lmnd-text-body-small-regular lmnd-kit-critical">Payment failed</p>
<div style="height: 40%; background: var(--lmnd-color-themed-teal-background)"></div>
```

**Don't.** `LemonadePrimitiveColors` in Compose (`@InternalLemonadeApi`) or SwiftUI, a `Color(0xFF…)` or
`Color(red:green:blue:)` literal, a hex or `rgb()` value in a page's CSS, or a themed hue where a semantic
token such as `contentCritical` fits.

## `LDS-COLOR-SURFACE` · A container is lighter than the page

**Rule of thumb.** Fill a container with a colour lighter than the page under it: a `bgDefault` card on
a `bgSubtle` page. That is the Card's default background.

A darker container — `bgSubtle` or `bgElevated` on a `bgDefault` page — is allowed, but crowds the
screen.

`bgElevatedHigh` is one step above `bgElevated`, for a surface inside an elevated one.

**Why.** A lighter container lifts its content off the page. A container in its page's own colour reads
as no container at all.

**Exceptions.** A brand or voice surface — `bgBrandSubtle` behind a promotion, `bgCriticalSubtle`
behind an error — is chosen for its meaning, not its depth.

**Example.** A card with today's sales, on a screen.

Compose.

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .background(LemonadeTheme.colors.background.bgSubtle)
        .padding(LemonadeTheme.spaces.spacing400),
) {
    LemonadeUi.Card(contentPadding = LemonadeCardPadding.Medium) {
        LemonadeUi.Text(text = "Sales today: £1,240.50")
    }
}
```

SwiftUI.

```swift
ScrollView {
    LemonadeUi.Card(contentPadding: .medium) {
        LemonadeUi.Text("Sales today: £1,240.50")
    }
    .padding(LemonadeTheme.spaces.spacing400)
}
.background(LemonadeTheme.colors.background.bgSubtle)
```

HTML kit. `lmnd-kit-screen` is `bgSubtle` and `lmnd-kit-card` is `bgDefault`.

```html
<div class="lmnd-kit-screen">
  <main class="lmnd-kit-content">
    <div class="lmnd-kit-card lmnd-kit-card--pad-medium">
      <div class="lmnd-kit-card__body"><p class="lmnd-text-body-medium-regular">Sales today: £1,240.50</p></div>
    </div>
  </main>
</div>
```

**Don't.** A `Default` card on a `bgDefault` page, such as an `lmnd-kit-card` on an
`lmnd-kit-screen--default`, or a `bgElevatedHigh` card straight on the page.

## `LDS-COLOR-ON-SURFACE` · Content on a filled surface uses that surface's pair

**Rule.** Text and icons on a filled background use the content token made for it:

| Background | Content |
| --- | --- |
| `bgBrand`, `bgBrandHigh`, `bgBrandElevated` | `contentOnBrandHigh`, and `contentOnBrandLow` for secondary content |
| `bgBrandSubtle` | `contentBrandHigh` |
| `bgCritical`, `bgCaution`, `bgInfo`, `bgPositive`, `bgNeutral`, `bgFeatured` | `contentAlwaysLight` |
| `bgDefaultInverse`, `bgSubtleInverse`, `bgElevatedInverse` | `contentPrimaryInverse`, `contentSecondaryInverse`, `contentTertiaryInverse` |

On the neutral backgrounds and on a voice's subtle background, content is the ordinary
`contentPrimary` / `contentSecondary` set, or the voice's own content token (`contentCritical` on
`bgCriticalSubtle`).

**Why.** A pair is checked for contrast as a pair, in both themes. `contentPrimary` on `bgCritical`
can pass in one theme and fail in the other, and the `content<Voice>OnColor` tokens don't reach enough
contrast on a voice's fill in either.

**Exceptions.** None.

**Example.** A promotion filled with `bgBrand`: a title and a line under it.

Compose.

```kotlin
Column(
    modifier = Modifier
        .background(LemonadeTheme.colors.background.bgBrand)
        .padding(LemonadeTheme.spaces.spacing400),
) {
    LemonadeUi.Text(
        text = "Get paid the same day",
        textStyle = LemonadeTheme.typography.headingXSmall,
        color = LemonadeTheme.colors.content.contentOnBrandHigh,
    )
    LemonadeUi.Text(
        text = "Instant payouts are free this month.",
        textStyle = LemonadeTheme.typography.bodySmallRegular,
        color = LemonadeTheme.colors.content.contentOnBrandLow,
    )
}
```

SwiftUI.

```swift
VStack(alignment: .leading) {
    LemonadeUi.Text(
        "Get paid the same day",
        textStyle: LemonadeTypography.shared.headingXSmall,
        color: LemonadeTheme.colors.content.contentOnBrandHigh
    )
    LemonadeUi.Text(
        "Instant payouts are free this month.",
        textStyle: LemonadeTypography.shared.bodySmallRegular,
        color: LemonadeTheme.colors.content.contentOnBrandLow
    )
}
.padding(LemonadeTheme.spaces.spacing400)
.background(LemonadeTheme.colors.background.bgBrand)
```

HTML kit.

```html
<div style="padding: var(--lmnd-spacing-400); background: var(--lmnd-color-bg-brand)">
  <h3 class="lmnd-text-heading-xsmall" style="color: var(--lmnd-color-content-on-brand-high)">Get paid the same day</h3>
  <p class="lmnd-text-body-small-regular" style="color: var(--lmnd-color-content-on-brand-low)">Instant payouts are free this month.</p>
</div>
```

**Don't.** `contentPrimary` or `contentSecondary` on `bgBrand`, or `contentPrimary` or
`contentCriticalOnColor` on `bgCritical`, where `contentAlwaysLight` belongs.

## `LDS-COLOR-INVERSE` · Inverse is an opposite surface, `Always` is a fixed one, neither is dark mode

**Rule.** An `Inverse` token is for a surface that contrasts with the theme — a dark toast or tooltip
in light mode, which turns light in dark mode. An `Always` token (`bgAlwaysDark`, `contentAlwaysLight`)
is for something that keeps one look in both themes — content over a photo or a camera feed, a
surface whose colour is part of the brand.

Dark mode is the theme's job. A screen reaches it by using semantic tokens, never by picking inverse or
`Always` tokens by hand.

**Why.** An `Always` token where an inverse one belongs stays dark on a dark screen and disappears. An
inverse token used to build dark mode flips back the moment the theme does.

**Exceptions.** The SwiftUI toast is `bgAlwaysDark` with `contentAlwaysLight` in both themes, because
its Liquid Glass tint needs a dark surface. Compose and the HTML kit draw it on the inverse surface.

**Example.** A floating bar counting the selected rows, and a product's name over its photo.

Compose.

```kotlin
Box(
    modifier = Modifier
        .background(LemonadeTheme.colors.background.bgDefaultInverse)
        .padding(LemonadeTheme.spaces.spacing400),
) {
    LemonadeUi.Text(
        text = "3 selected",
        color = LemonadeTheme.colors.content.contentPrimaryInverse,
    )
}

Box(contentAlignment = Alignment.BottomStart) {
    ProductPhoto()
    LemonadeUi.Text(
        text = "Flat white",
        color = LemonadeTheme.colors.content.contentAlwaysLight,
    )
}
```

SwiftUI.

```swift
LemonadeUi.Text(
    "3 selected",
    color: LemonadeTheme.colors.content.contentPrimaryInverse
)
.padding(LemonadeTheme.spaces.spacing400)
.background(LemonadeTheme.colors.background.bgDefaultInverse)

ProductPhoto()
    .overlay(alignment: .bottomLeading) {
        LemonadeUi.Text(
            "Flat white",
            color: LemonadeTheme.colors.content.contentAlwaysLight
        )
    }
```

HTML kit.

```html
<div style="padding: var(--lmnd-spacing-400); background: var(--lmnd-color-bg-default-inverse); color: var(--lmnd-color-content-primary-inverse)">
  <span class="lmnd-text-body-medium-regular">3 selected</span>
</div>
<figure style="position: relative">
  <img src="flat-white.jpg" alt="">
  <figcaption class="lmnd-text-body-medium-regular" style="position: absolute; bottom: 0; color: var(--lmnd-color-content-always-light)">Flat white</figcaption>
</figure>
```

**Don't.** `bgAlwaysDark` for a tooltip or a bar like this one, `contentPrimaryInverse` over a photo, or
`contentPrimaryInverse` to make a screen dark.

## `LDS-COLOR-STATE` · Pressed and disabled come from tokens

**Rule.** A pressed element takes an interaction token of the surface it is drawn on — the
`Interactive` or `Pressed` variant of that background — never a colour picked by hand. A disabled
element is drawn whole at `opacityDisabled`, in its usual colours.

The Lemonade controls show both variants. A solid `Button` presses its fill to the fill's own
`Interactive` token (`bgBrand` to `bgBrandInteractive`); a `Subtle` or `Ghost` one, which has no fill
of its own, shows `bgSubtlePressed`. `Switch` presses its track to `bgBrandHighPressed` when on and
`bgElevatedPressed` when off.

**Why.** Each background has its own interaction tokens, tuned so the change shows on it in both
themes. A darker or greyer colour picked by hand is tuned for neither.

**Exceptions.** A component that animates its press as opacity, as `BoxSelection` and `Tabs` do with
`opacityPressed`.

**Example.** A day's bar in a custom sales chart, on a `bgSubtle` card, that a person presses to see that
day, and the same bar disabled. A Lemonade component already does both, so this is only for an element
built in the product.

Compose.

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val pressed by interactionSource.collectIsPressedAsState()

Box(
    modifier = Modifier
        .alpha(if (enabled) 1f else LemonadeTheme.opacities.state.opacityDisabled)
        .background(if (pressed) LemonadeTheme.colors.interaction.bgSubtlePressed else Color.Transparent)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onSelectDay,
        ),
) {
    SalesBar(day = "Mon")
}
```

SwiftUI.

```swift
struct DayBarStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .background(configuration.isPressed ? LemonadeTheme.colors.interaction.bgSubtlePressed : .clear)
    }
}

Button(action: onSelectDay) {
    SalesBar(day: "Mon")
}
.buttonStyle(DayBarStyle())
.disabled(!enabled)
.opacity(enabled ? 1 : LemonadeTheme.opacity.state.opacityDisabled)
```

HTML kit.

```html
<style>
  .day-bar:active { background: var(--lmnd-color-bg-subtle-pressed); }
  .day-bar:disabled { opacity: var(--lmnd-opacity-disabled); }
</style>
<button type="button" class="day-bar lmnd-text-body-small-regular">Mon</button>
```

**Don't.** `bgElevated` borrowed as a pressed colour, or `contentTertiary` text for a disabled label.
