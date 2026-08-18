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

## Where to go next

- [Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/) — the one
  rule worth reading before you write any styling
- [Forms](/lemonade-design-system/patterns/forms/) — the most common screen, done properly
- [Colour](/lemonade-design-system/foundations/colour/) — every token, with its usage
