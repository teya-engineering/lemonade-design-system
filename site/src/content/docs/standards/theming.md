---
title: Theming & dark mode
description: How Lemonade resolves light and dark, and what a host app controls.
---

Lemonade does not have a light mode and a dark mode you switch between. It has one
theme, and every semantic token in it carries both a light and a dark value. Your job
is to name the role you want — `content-positive`, `bg-critical-subtle` — and let the
token resolve. The resolution isn't your problem.

## One theme, two resolutions

There is no `if (isDarkTheme)` in product code. When you write `content-positive`, the
token itself knows it is `#497d00` in light and `#7ccf00` in dark — that decision was
made once, by design, and every screen that reaches for the token gets it for free.

Writing a theme conditional in your own code is a sign you've reached for a primitive,
or hard-coded a colour, instead of a semantic token. If you find yourself branching on
theme, back up and look for the token that already encodes the difference.

## Wrapping your app

On Kotlin Multiplatform, wrap the app once, at the root:

```kotlin
@Composable
fun App() {
    LemonadeTheme {
        MyScreenContent()
    }
}
```

Every parameter — colours, typography, spacing, radius, shapes — has a default and is
independently overridable, so a host app can swap in its own values for one axis
without touching the rest. Left alone, `LemonadeTheme` follows the system setting: light
or dark based on what the device reports.

The wrapper is not optional. Colour has no fallback: skip `LemonadeTheme { }` and the
first component that reads a colour throws at composition time, because there is no
default to fall back to. Every other axis — typography, spacing, radius, shapes,
opacity, border widths, sizes — does have a built-in default and would render fine on
its own, but that doesn't save you, because colour is the one that's missing and colour
is what almost every visual component reads. Wrap at the root, always.

On SwiftUI there is no equivalent wrapper to add, and that's by design rather than an
oversight: colours resolve through Asset Catalog named colours that adapt to the
system's light/dark appearance automatically, so there's no root token to install before
a colour is available.

## Fixed tokens

A handful of tokens are exceptions to "everything resolves per theme": `bg-always-dark`,
`content-always-light`, and the `*-always-on-color` family (`content-critical-always-on-color`
and siblings) hold the same value in both themes. See
[Colour](/lemonade-design-system/foundations/colour/) for the full set marked **fixed**.

These exist for surfaces that are genuinely fixed regardless of the surrounding theme —
a photo overlay, a coloured banner that keeps its own colour in dark mode. The content
sitting on top of that banner needs to stay legible against it no matter what theme the
rest of the screen is in, which is exactly what an `-always-on-color` token is for.

Using one of these because the surface really doesn't flip is correct. Using one because
you want a colour to just stay put — because chasing it through both themes is
inconvenient — is a misuse. If a value looks pinned for convenience rather than because
the underlying surface is fixed, it's worth a second look.

## Testing both themes

Check both themes before merging, not just the one your simulator happened to boot into.
The two failures that show up nearly every time are the same: a hard-coded colour that
was never a token in the first place, and a fixed token reached for because a real
dark-mode value was never chosen. Neither is caught by looking at light mode alone.
