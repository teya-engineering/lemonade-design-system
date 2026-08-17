---
title: Kotlin Multiplatform
description: Add Lemonade to a Kotlin Multiplatform project and render your first component.
---

Add the dependency to your version catalog. The latest version is whatever the most
recent `lemonade-kmp-v*` tag says.

```toml
[versions]
lemonade = "1.0.0"

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

`LemonadeTheme` is optional — components fall back to defaults without it — but without
it you cannot theme anything, so wrap early.

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

- [Semantic tokens first](../../standards/semantic-tokens/) — the one
  rule worth reading before you write any styling
- [Forms](../../patterns/forms/) — the most common screen, done properly
- [Colour](../../foundations/colour/) — every token, with its usage
