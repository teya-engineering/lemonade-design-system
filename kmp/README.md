# Lemonade Design System - Kotlin Multiplatform

A shared **Kotlin Multiplatform** library for UI components, styling, and theming — enabling consistent user experiences across Android, iOS, JVM Desktop and the browser.

---

## Table of Contents

- [Overview](#overview)
- [Supported Platforms](#supported-platforms)
- [Getting Started](#getting-started)
  - [Step 1: Add to libs.toml](#step-1-add-to-libstoml)
  - [Step 2: Apply in build.gradle.kts](#step-2-apply-in-buildgradlekts)
- [Configuring the Theme](#configuring-the-theme)
- [Using Components](#using-components)
- [Components](#components)
- [Design Tokens](#design-tokens)
- [Assets](#assets)
- [Contributing](#contributing)
  - [Documentation Standards](#documentation-standards)

---

## Overview

This guide provides the essential steps to integrate the Lemonade Design System KMP library into your Android, iOS, or JVM application.

By the end of this guide, you will have:

- Added the library dependency
- Wrapped your application in the core theme provider
- Used a basic component from the library

---

## Supported Platforms

| Platform | Target |
|----------|--------|
| Android | Mobile |
| iOS | Mobile |
| JVM | Desktop |
| Wasm | Browser |

---

## Getting Started

You'll need to add the library to your project's build files. We recommend using the version catalog (`libs.toml`).

### Step 1: Add to `libs.toml`

Add the library to your `gradle/libs.toml` file. You can find the latest version by checking for tags with `lemonade-kmp-v*`.

```toml
[versions]
lemonade = "latest-version"

[libraries]
# Main UI library
lemonade-ui = { module = "com.teya.foundation:lemonade-ui", version.ref = "lemonade" }

# Core definitions (included in lemonade-ui)
# Contains core component definitions for server-driven UI support
lemonade-core = { module = "com.teya.foundation:lemonade-core", version.ref = "lemonade" }
```

### Step 2: Apply in `build.gradle.kts`

Add the library to the `commonMain` source set in your module-level `build.gradle.kts` (e.g., `shared/build.gradle.kts`):

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // ... other dependencies

                // Add the design system library
                implementation(libs.lemonade.ui)
            }
        }
        // ... other source sets
    }
}
```

---

## Configuring the Theme

To make all the colors, typography, shapes, and other design tokens available to your app, wrap your content in the `LemonadeTheme` provider. This is **optional** — if not implemented, default values will be used for all Lemonade components.

This is typically done once at the root of your application, usually in your `commonMain` entry point (e.g., `App.kt`):

```kotlin
import com.teya.lemonade

@Composable
fun App() {
    LemonadeTheme(
        // All options are overrideable to adapt to your app's needs
        colors = LemonadeTheme.colors,
        typography = LemonadeTheme.typography,
        radius = LemonadeTheme.radius,
        shapes = LemonadeTheme.shapes,
        opacities = LemonadeTheme.opacities,
        spaces = LemonadeTheme.spaces,
        borderWidths = LemonadeTheme.borderWidths,
    ) {
        // Your application's content goes here
        MyScreenContent()
    }
}
```

---

## Using Components

For easy discovery, Lemonade DS provides a `LemonadeUi` object. Import it via `import com.teya.lemonade.LemonadeUi` — auto-complete will help you discover available components, and it makes it easy to distinguish where components are coming from.

### Basic Usage Example

Here is a simple example using `Switch` and `Text` from the library:

```kotlin
import com.teya.lemonade.LemonadeUi

@Composable
fun MyScreenContent() {
    var checked by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(LemonadeTheme.spaces.spacing100), // Using spacing tokens
    ) {
        LemonadeUi.Text(
            text = "Welcome to Lemonade - Are you loving it?",
        )

        LemonadeUi.Switch(
            checked = checked,
            onCheckedChange = { checked = !checked },
        )
    }
}
```

---

## Components

| Category | Examples |
|----------|----------|
| **Form Controls** | Switch, Input, Checkbox |
| **Display** | Text, Badge, Avatar |
| **Selection & Lists** | List, Dropdown |

---

## Design Tokens

| Token | Description |
|-------|-------------|
| **Colors** | Primitive and semantic color tokens |
| **Typography** | Figtree font family with multiple styles |
| **Spacing** | Consistent spacing tokens (`spaces.spacing100`, etc.) |
| **Radius** | Border radius tokens |
| **Shapes** | Pre-defined shape configurations |
| **Opacities** | Opacity level tokens |
| **Border Widths** | Border width tokens |

---

## Assets

| Asset Type | Description |
|------------|-------------|
| **BrandLogo** | Brand logo assets |
| **CountryFlag** | Country flag icons |
| **SVG Icons** | Comprehensive icon library |

---

## Contributing

### Documentation Standards

All public APIs must be thoroughly documented. Clear documentation helps other developers understand and use the components correctly.

#### Don't

Leave public APIs undocumented or with lazy comments:

```kotlin
// This is the switch
@Composable
public fun LemonadeUi.Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    // ...
}
```

#### Do

Write clear, comprehensive KDoc for all public APIs:

```kotlin
/**
 * This composable provides the fundamental visual and interactive elements of a toggle switch,
 * including the track and thumb. It handles animations for state changes like checked, enabled,
 * hover, and press. It is designed to be the internal building block for a higher-level,
 * public-facing switch component.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Switch(
 *     checked = true,
 *     onCheckedChange = { isChecked -> /* ... */ },
 * )
 * ```
 *
 * ## Parameters
 * @param checked `true` if the switch is in the "on" state, `false` otherwise.
 * @param onCheckedChange A callback invoked when the user interacts with the switch to change its state.
 * @param enabled Optional - controls the enabled state of the switch. When `false`, interaction is
 *   disabled and it is visually styled as such. Defaults to true.
 * @param interactionSource Optional [MutableInteractionSource] used to observe interaction states
 *   like hover and press to drive visual feedback.
 * @param modifier Optional [Modifier] to be applied to the root container of the switch.
 */
@Composable
public fun LemonadeUi.Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    // ...
}
```
