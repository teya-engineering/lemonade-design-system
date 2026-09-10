# Lemonade Design System - Kotlin Multiplatform

A shared **Kotlin Multiplatform** library for UI components, styling, and theming — enabling consistent user experiences across Android, iOS, and JVM Desktop.

---

## Table of Contents

- [Overview](#overview)
- [Supported Platforms](#supported-platforms)
- [Getting Started](#getting-started)
  - [Step 1: Add to libs.versions.toml](#step-1-add-to-libsversionstoml)
  - [Step 2: Apply in build.gradle.kts](#step-2-apply-in-buildgradlekts)
- [Configuring the Theme](#configuring-the-theme)
- [Using Components](#using-components)
- [Components](#components)
- [Design Tokens](#design-tokens)
- [Assets](#assets)
- [Contributing](#contributing)
  - [Pull request flow](#pull-request-flow)
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

---

## Getting Started

You'll need to add the library to your project's build files. We recommend using the version catalog (`libs.versions.toml`).

### Step 1: Add to `libs.versions.toml`

Add the library to your `gradle/libs.versions.toml` file. You can find the latest version by checking for tags matching `lemonade-kmp-*` — the version is the part after the prefix, for example `lemonade-kmp-0.9.0` publishes `0.9.0`.

```toml
[versions]
lemonade = "latest-version"

[libraries]
# Main UI library — exposes lemonade-core and lemonade-tokens transitively
lemonade-ui = { module = "com.teya.foundation:lemonade-ui", version.ref = "lemonade" }

# Overlays and expressive surfaces: BottomSheet, Dialog, Dropdown, BottomTabBar, TimePicker
lemonade-expressive = { module = "com.teya.foundation:lemonade-expressive", version.ref = "lemonade" }

# Date pickers and the inline calendar
lemonade-calendar = { module = "com.teya.foundation:lemonade-calendar", version.ref = "lemonade" }
```

`lemonade-expressive` and `lemonade-calendar` both depend on `lemonade-ui`, so
adding either one is enough on its own.

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
import com.teya.lemonade.LemonadeTheme

@Composable
fun App() {
    LemonadeTheme(
        // Every parameter is defaulted and overrideable to adapt to your app's needs
        colors = LemonadeTheme.colors,
        typography = LemonadeTheme.typography,
        radius = LemonadeTheme.radius,
        shapes = LemonadeTheme.shapes,
        opacities = LemonadeTheme.opacities,
        spaces = LemonadeTheme.spaces,
        borderWidths = LemonadeTheme.borderWidths,
        sizes = LemonadeTheme.sizes,
        effects = LemonadeTheme.effects,
    ) {
        // Your application's content goes here
        MyScreenContent()
    }
}
```

`colors` defaults to `LemonadeLightTheme` or `LemonadeDarkTheme` depending on
`isSystemInDarkTheme()`.

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

Every component is an extension on `LemonadeUi`, so `LemonadeUi.` plus
auto-complete lists the whole catalogue.

| Artifact | Category | Components |
|----------|----------|------------|
| `lemonade-ui` | Actions | Button, IconButton, Link, Chip |
| `lemonade-ui` | Form Controls | TextField, TextFieldWithSelector, SearchField, SelectField, PinCode, Switch, Checkbox, RadioButton, SegmentedControl, BoxSelection |
| `lemonade-ui` | Display | Text, Icon, Asset, Badge, Tag, Card, Tile, SymbolContainer, HorizontalDivider, VerticalDivider, BrandLogo, CountryFlag |
| `lemonade-ui` | Lists & Navigation | ListItem, ContentListItem, ActionListItem, ResourceListItem, SelectListItem, SwipeActionRow, SwipeActionGroup, Tabs, TopBar (Android/iOS only), HistoryTimeline |
| `lemonade-ui` | Feedback | Toast, Tooltip, Notice, Spinner, LineSkeleton, BlockSkeleton, CircleSkeleton |
| `lemonade-expressive` | Overlays & Navigation | BottomSheet, Dialog, Dropdown, DropdownItem, BottomTabBar, TimePicker, TimeInput, TimePickerDialog |
| `lemonade-calendar` | Date | DatePicker, DateRangePicker, InlineCalendar |

`Toast` and `Tooltip` render through `LemonadeToastHost` and
`LemonadeTooltipHost`; place those once near the root of your composition.

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
| **Sizes** | Component sizing tokens |
| **Effects** | Shadow and elevation effects |

---

## Assets

Assets are enums in `lemonade-core`, rendered through `LemonadeUi.Asset`,
`LemonadeUi.Icon`, `LemonadeUi.BrandLogo` and `LemonadeUi.CountryFlag`.

| Asset Type | Enum |
|------------|------|
| **Icons** | `LemonadeIcons` |
| **CountryFlag** | `LemonadeCountryFlags` |
| **BrandLogo** | `LemonadeBrandLogos` |

---

## Contributing

### Pull request flow

1. Branch off `main`. Never commit straight to it.
2. Make the change. `composeApp` is the sample app — a new component gets a
   `<Name>Display.kt` showcase there, registered in
   `composeApp/src/commonMain/kotlin/com/teya/lemonade/app/App.kt`.
3. If the change touches public API in `core`, `tokens`, `ui`, `expressive` or
   `calendar`, run `./gradlew apiDump` from `kmp/` and commit the regenerated
   `api/*.api` and `*.klib.api` files.
4. Run the API stability classifier before opening the PR, so you know the verdict
   ahead of CI:

   ```bash
   .claude/skills/binary-compatibility/scripts/bcv-check.sh --ci
   ```

5. Fill in the **API Dump** section of the PR description. It is mandatory whenever
   the baseline files change: state the verdict (`NO_CHANGES`, `ADDITIONS_ONLY` or
   `BREAKING`), which entries are not a concern and why, and — for a `BREAKING`
   verdict — who needs to approve it and why it is acceptable. If the baseline
   didn't change, write "No public API changes".
6. A `BREAKING` verdict blocks merge until a named maintainer approves the exact
   head commit.

Never move, rename or delete a public declaration just to make `apiCheck` pass.
The root `CLAUDE.md` and the `binary-compatibility` skill carry the full decision
table.

### Documentation Standards

Every public API carries KDoc. The `## Usage` block on a published component is
the canonical documentation downstream repos read, so keep it current.

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

Open with one line saying what the component shows, follow with a `## Usage`
block, and document every parameter:

````kotlin
/**
 * Shows a toggle switch with an animated track and thumb.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Switch(
 *  checked = false,
 *  onCheckedChange = { setTo -> ... },
 * )
 * ```
 *
 * @param checked `true` when the switch is on
 * @param onCheckedChange callback run with the new state when the user toggles the switch
 * @param enabled when `false` the switch ignores input and renders as disabled
 * @param interactionSource [MutableInteractionSource] observed for the hover and press states
 *  that drive the visual feedback
 * @param modifier [Modifier] applied to the root container of the switch
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
````

This is the shipped KDoc for `Switch` —
`kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Switch.kt`.
