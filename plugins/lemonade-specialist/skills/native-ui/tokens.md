# Lemonade DS Tokens

Complete token reference for typography, colours, spacing, and radius.

## Typography

| Style                | Android                                       | iOS                                                 |
| -------------------- | --------------------------------------------- | --------------------------------------------------- |
| Display Large        | `LemonadeTheme.typography.displayLarge`       | `LemonadeTypography.shared.displayLarge.font`       |
| Display Medium       | `LemonadeTheme.typography.displayMedium`      | `LemonadeTypography.shared.displayMedium.font`      |
| Display Small        | `LemonadeTheme.typography.displaySmall`       | `LemonadeTypography.shared.displaySmall.font`       |
| Heading XLarge       | `LemonadeTheme.typography.headingXLarge`      | `LemonadeTypography.shared.headingXLarge.font`      |
| Heading Large        | `LemonadeTheme.typography.headingLarge`       | `LemonadeTypography.shared.headingLarge.font`       |
| Heading Medium       | `LemonadeTheme.typography.headingMedium`      | `LemonadeTypography.shared.headingMedium.font`      |
| Heading Small        | `LemonadeTheme.typography.headingSmall`       | `LemonadeTypography.shared.headingSmall.font`       |
| Heading XSmall       | `LemonadeTheme.typography.headingXSmall`      | `LemonadeTypography.shared.headingXSmall.font`      |
| Heading XXSmall      | `LemonadeTheme.typography.headingXXSmall`     | `LemonadeTypography.shared.headingXXSmall.font`     |
| Body Large Regular   | `LemonadeTheme.typography.bodyLargeRegular`   | `LemonadeTypography.shared.bodyLargeRegular.font`   |
| Body Large Medium    | `LemonadeTheme.typography.bodyLargeMedium`    | `LemonadeTypography.shared.bodyLargeMedium.font`    |
| Body Large SemiBold  | `LemonadeTheme.typography.bodyLargeSemiBold`  | `LemonadeTypography.shared.bodyLargeSemiBold.font`  |
| Body Medium Regular  | `LemonadeTheme.typography.bodyMediumRegular`  | `LemonadeTypography.shared.bodyMediumRegular.font`  |
| Body Medium Medium   | `LemonadeTheme.typography.bodyMediumMedium`   | `LemonadeTypography.shared.bodyMediumMedium.font`   |
| Body Medium SemiBold | `LemonadeTheme.typography.bodyMediumSemiBold` | `LemonadeTypography.shared.bodyMediumSemiBold.font` |
| Body Small Regular   | `LemonadeTheme.typography.bodySmallRegular`   | `LemonadeTypography.shared.bodySmallRegular.font`   |
| Body Small Medium    | `LemonadeTheme.typography.bodySmallMedium`    | `LemonadeTypography.shared.bodySmallMedium.font`    |
| Body Small SemiBold  | `LemonadeTheme.typography.bodySmallSemiBold`  | `LemonadeTypography.shared.bodySmallSemiBold.font`  |

### Typography Usage

```kotlin
// Android
Text(
    text = "Hello",
    style = LemonadeTheme.typography.bodyMediumRegular,
    color = LemonadeTheme.colors.content.contentPrimary
)
```

```swift
// iOS
Text("Hello")
    .font(LemonadeTypography.shared.bodyMediumRegular.font)
    .foregroundColor(LemonadeTheme.colors.content.contentPrimary)
```

## Colours

Colours use the same access pattern on both platforms: `LemonadeTheme.colors.{category}.{token}`

### Background Colours

| Token                         | Use Case                                                  |
| ----------------------------- | --------------------------------------------------------- |
| `background.bgDefault`        | Screen background, and the `Card` component's own fill    |
| `background.bgElevated`       | Elevated surfaces                                         |
| `background.bgSubtle`         | A screen or sheet that holds cards, secondary backgrounds |
| `background.bgPositiveSubtle` | Success state backgrounds                                 |
| `background.bgCriticalSubtle` | Error state backgrounds                                   |
| `background.bgCautionSubtle`  | Warning state backgrounds                                 |
| `background.bgInfoSubtle`     | Info state backgrounds                                    |

### Content Colours

| Token                      | Use Case                   |
| -------------------------- | -------------------------- |
| `content.contentPrimary`   | Primary text               |
| `content.contentSecondary` | Secondary/support text     |
| `content.contentTertiary`  | Placeholder, disabled text |
| `content.contentPositive`  | Success text/icons         |
| `content.contentCritical`  | Error text/icons           |
| `content.contentCaution`   | Warning text/icons         |
| `content.contentInfo`      | Info text/icons            |

### Border Colours

| Token                         | Use Case                     |
| ----------------------------- | ---------------------------- |
| `border.borderNeutralLow`     | Dividers, quiet outlines     |
| `border.borderNeutralMedium`  | Input and control outlines   |
| `border.borderNeutralHigh`    | Strong outlines              |
| `border.borderPositive`       | Success borders              |
| `border.borderCritical`       | Error borders                |
| `border.borderSelected`       | Selected controls            |

### Interaction Colours

The pressed state of a filled surface is the `Interactive` variant of its background:
`interaction.bg{Role}Interactive`, such as `bgBrandInteractive`, `bgNeutralSubtleInteractive` or
`bgCriticalInteractive`. Components apply these themselves; a hand-built pressable surface takes the
variant of the background it sits on (`LDS-` rules in `guidelines/colour.md`).

## Spacing

| Token      | Value | Android                           | iOS                                |
| ---------- | ----- | --------------------------------- | ---------------------------------- |
| spacing100 | 4pt   | `LemonadeTheme.spaces.spacing100` | `LemonadeSpacing.spacing100.value` |
| spacing200 | 8pt   | `LemonadeTheme.spaces.spacing200` | `LemonadeSpacing.spacing200.value` |
| spacing300 | 12pt  | `LemonadeTheme.spaces.spacing300` | `LemonadeSpacing.spacing300.value` |
| spacing400 | 16pt  | `LemonadeTheme.spaces.spacing400` | `LemonadeSpacing.spacing400.value` |
| spacing500 | 20pt  | `LemonadeTheme.spaces.spacing500` | `LemonadeSpacing.spacing500.value` |
| spacing600 | 24pt  | `LemonadeTheme.spaces.spacing600` | `LemonadeSpacing.spacing600.value` |
| spacing800 | 32pt  | `LemonadeTheme.spaces.spacing800` | `LemonadeSpacing.spacing800.value` |

### Spacing Usage

```kotlin
// Android
Modifier.padding(LemonadeTheme.spaces.spacing400)
Modifier.padding(horizontal = LemonadeTheme.spaces.spacing400)
```

```swift
// iOS
.padding(LemonadeSpacing.spacing400.value)
.padding(.horizontal, LemonadeSpacing.spacing400.value)
```

## Radius

**A corner comes from a shape token whenever one carries its exact geometry - otherwise the hand-built shape is correct and stays.**

Reach for `LemonadeTheme.shapes.*` / `LemonadeRadius.*.shape` instead of rebuilding the shape from a
radius value. Keep the hand-built form where no token matches: `CircleShape` and `Capsule()` (a 50%
corner is not `radiusFull`'s 999pt), SwiftUI's `style: .continuous` (a different corner curve), a
single rounded corner (no shape token covers one corner - build it from the radius token), and any
value off the scale below. Swapping one of those changes the UI.

| Token      | Value | Android shape                     | iOS shape                          |
| ---------- | ----- | --------------------------------- | ---------------------------------- |
| radius0    | 0pt   | `LemonadeTheme.shapes.radius0`    | `LemonadeRadius.radius0.shape`     |
| radius50   | 2pt   | `LemonadeTheme.shapes.radius50`   | `LemonadeRadius.radius50.shape`    |
| radius100  | 4pt   | `LemonadeTheme.shapes.radius100`  | `LemonadeRadius.radius100.shape`   |
| radius150  | 6pt   | `LemonadeTheme.shapes.radius150`  | `LemonadeRadius.radius150.shape`   |
| radius200  | 8pt   | `LemonadeTheme.shapes.radius200`  | `LemonadeRadius.radius200.shape`   |
| radius250  | 10pt  | `LemonadeTheme.shapes.radius250`  | `LemonadeRadius.radius250.shape`   |
| radius300  | 12pt  | `LemonadeTheme.shapes.radius300`  | `LemonadeRadius.radius300.shape`   |
| radius350  | 14pt  | `LemonadeTheme.shapes.radius350`  | `LemonadeRadius.radius350.shape`   |
| radius400  | 16pt  | `LemonadeTheme.shapes.radius400`  | `LemonadeRadius.radius400.shape`   |
| radius500  | 20pt  | `LemonadeTheme.shapes.radius500`  | `LemonadeRadius.radius500.shape`   |
| radius600  | 24pt  | `LemonadeTheme.shapes.radius600`  | `LemonadeRadius.radius600.shape`   |
| radius800  | 32pt  | `LemonadeTheme.shapes.radius800`  | `LemonadeRadius.radius800.shape`   |
| radius900  | 40pt  | `LemonadeTheme.shapes.radius900`  | `LemonadeRadius.radius900.shape`   |
| radiusFull | 999pt | `LemonadeTheme.shapes.radiusFull` | `LemonadeRadius.radiusFull.shape`  |

The container default is `LemonadeTheme.shapes.semantic.radiusContainerDefault` / `LemonadeShapesImpl().semantic.radiusContainerDefault` (24pt).

For the raw dimension - a single corner, a stroke inset, a Canvas draw - swap `shapes` for `radius`
on Android (`LemonadeTheme.radius.radius400`) and `.shape` for `.value` on iOS
(`LemonadeRadius.radius400.value`).

### Radius Usage

```kotlin
// Android
.clip(LemonadeTheme.shapes.radius400)
Surface(shape = LemonadeTheme.shapes.radiusFull) { … }
// One corner only: no shape token covers it, so build it from the radius token.
.clip(RoundedCornerShape(topStart = LemonadeTheme.radius.radius900, topEnd = LemonadeTheme.radius.radius900))
```

```swift
// iOS - the `.shape` shorthand covers every token except radius250, radius350 and radius900.
.clipShape(.shape.radius400)
.background(LemonadeTheme.colors.background.bgDefault, in: .shape.radius400)
.clipShape(LemonadeRadius.radius250.shape)
```
