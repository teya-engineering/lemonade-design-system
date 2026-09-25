---
title: Kotlin Multiplatform
description: Add Lemonade to a Kotlin Multiplatform project and render your first component.
---

Add the dependency to your version catalog. The version below is the latest published
at the time of writing; check the repository's tag list for a `lemonade-kmp-*` tag
newer than `0.37.1` (tags have no `v` prefix) before you publish.

```toml
[versions]
lemonade = "0.37.1"

[libraries]
lemonade-ui = { module = "com.teya.foundation:lemonade-ui", version.ref = "lemonade" }
```

Then pull it into `commonMain`:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.lemonade.ui)
            }
        }
    }
}
```

Wrap your app once, at the root:

```kotlin
@Composable
fun App() {
    LemonadeTheme {
        MyScreenContent()
    }
}
```

`LemonadeTheme` is required, not optional: colour has no fallback, so the first
component that reads one throws at composition time if you skip the wrapper. Wrap at
the root before rendering anything. See
[Theming & dark mode](/lemonade-design-system/standards/theming/) for why.

Components hang off the `LemonadeUi` object, which exists so autocomplete tells you what
the design system offers and so a reader can see at a glance where a component came from:

```kotlin
import com.teya.lemonade.LemonadeUi

@Composable
fun MyScreenContent() {
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(text = "Notifications")
        LemonadeUi.Switch(checked = checked, onCheckedChange = { checked = it })
    }
}
```

## Using the tokens

Wrap the app once in `LemonadeTheme`. Everything inside it can read the tokens; outside
it, reading one throws `No default colors set. Wrap your content in LemonadeTheme.`

```kotlin
@Composable
fun App() {
    LemonadeTheme {
        Screen()
    }
}
```

The theme follows the system appearance on its own — it picks `LemonadeLightTheme` or
`LemonadeDarkTheme` from `isSystemInDarkTheme()` — so a semantic token resolves to the
right value in both themes without anything at the call site.

Inside, reach a token through its group:

```kotlin
@Composable
private fun Screen() {
    Column(
        modifier = Modifier
            .background(LemonadeTheme.colors.background.bgDefault)
            .padding(LemonadeTheme.spaces.spacing400),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = "Payment received",
            textStyle = LemonadeTheme.typography.bodyMediumSemiBold,
            color = LemonadeTheme.colors.content.contentPositive,
        )
    }
}
```

| Tokens | Accessor | Example |
|---|---|---|
| Colour | `LemonadeTheme.colors` | `colors.content.contentPositive` |
| Typography | `LemonadeTheme.typography` | `typography.bodyMediumSemiBold` |
| Spacing | `LemonadeTheme.spaces` | `spaces.spacing400` |
| Radius | `LemonadeTheme.radius` | `radius.radius600` |
| Shape | `LemonadeTheme.shapes` | `shapes.radiusContainerDefault` |
| Size | `LemonadeTheme.sizes` | `sizes.size500` |
| Opacity | `LemonadeTheme.opacities` | `opacities.opacity5` |
| Border width | `LemonadeTheme.borderWidths` | `borderWidths.borderWidth100` |

Colour is grouped the same way [Foundations](/lemonade-design-system/foundations/colour/)
lists it: `background`, `border`, `content`, `interaction`, `scoped` and `shadow`.
`LemonadeTheme.colors.isDark` tells you which theme resolved, for the rare case where a
decision genuinely depends on it.

Shadows are a modifier rather than a value — `Modifier.lemonadeShadow(...)`.

:::note
Every accessor is `@Composable`. They read composition locals, so they can only be
called from inside a composable function — pull the value out and pass it down if you
need it somewhere else.
:::

## Where to go next

- [Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/) — the one
  rule worth reading before you write any styling
- [Lists](/lemonade-design-system/patterns/lists/) — a list screen, done properly
- [Colour](/lemonade-design-system/foundations/colour/) — every token, with its usage
