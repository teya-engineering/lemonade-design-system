# Lemonade DS Components

Complete component catalog with Android Compose and iOS SwiftUI examples.

## List Items Rule

Never build a list row from raw `Row`/`HStack` + `Icon` + `Text`. Use a DS list item:
`ContentListItem`, `ActionListItem`, `ResourceListItem` or `SelectListItem`. Which one fits is
`LDS-LIST-NO-HANDROLL` in `guidelines/lists.md`.

## Tag

Status indicator with icon and label.

```kotlin
// Android
LemonadeUi.Tag(
    icon = LemonadeIcons.Check,
    label = "Success",
    voice = TagVoice.Positive
)
```

```swift
// iOS
LemonadeUi.Tag(
    icon: .check,
    label: "Success",
    voice: .positive
)
```

**Voice Values:**

- `.positive` - Green (success)
- `.critical` - Red (error)
- `.warning` - Orange (warning)
- `.info` - Blue (info)
- `.neutral` - Gray (default)

## Icon

Standalone icon display.

```kotlin
// Android
LemonadeUi.Icon(
    icon = LemonadeIcons.Download,
    contentDescription = "Download"
)
```

```swift
// iOS
LemonadeUi.Icon(
    icon: .download,
    contentDescription: "Download"
)
```

**Common Icons:**
`.check`, `.times`, `.clock`, `.download`, `.receipt`, `.undo`, `.circleInfo`, `.ellipsisHorizontal`, `.chevronRight`, `.chevronDown`, `.triangleAlert`, `.plus`, `.minus`, `.search`, `.filter`

## SymbolContainer

Icon with background container.

```kotlin
// Android
LemonadeUi.SymbolContainer(
    icon = LemonadeIcons.Check,
    contentDescription = null,
    voice = SymbolContainerVoice.Positive,
    size = SymbolContainerSize.Medium
)
```

```swift
// iOS
LemonadeUi.SymbolContainer(
    icon: .check,
    contentDescription: nil,
    voice: .positive,
    size: .medium
)
```

**Sizes:** `.small`, `.medium`, `.large`
**Voices:** `.positive`, `.critical`, `.warning`, `.info`, `.neutral`

## Button

Action buttons with multiple variants.

```kotlin
// Android
LemonadeUi.Button(
    label = "Submit",
    onClick = { /* action */ },
    variant = LemonadeButtonVariant.Primary,
    type = LemonadeButtonType.Solid,
    size = LemonadeButtonSize.Medium,
)
```

```swift
// iOS
LemonadeUi.Button(
    label: "Submit",
    onClick: { /* action */ },
    variant: .primary,
    type: .solid,
    size: .medium
)
```

**Variants:** `.primary`, `.secondary`, `.neutral`, `.critical`, `.onBrand`, `.onColor`
**Types:** `.solid`, `.subtle`, `.ghost`
**Sizes:** `.xSmall`, `.small`, `.medium`, `.large` (default `.large`)

## ActionListItem

Tappable list item with leading/trailing slots.

```kotlin
// Android
LemonadeUi.ActionListItem(
    label = "View Receipt",
    onItemClicked = { /* action */ },
    leadingSlot = {
        LemonadeUi.Icon(icon = LemonadeIcons.Receipt, contentDescription = null)
    }
)
```

```swift
// iOS
LemonadeUi.ActionListItem(
    label: "View Receipt",
    onItemClicked: { /* action */ },
    leadingSlot: {
        LemonadeUi.Icon(icon: .receipt, contentDescription: nil)
    }
)
```

## ResourceListItem

List item with value and support text.

```kotlin
// Android
LemonadeUi.ResourceListItem(
    label = "Transaction",
    value = "$100.00",
    supportText = "12:30 PM",
    onItemClicked = { /* action */ },
    leadingSlot = { /* icon */ }
)
```

```swift
// iOS
LemonadeUi.ResourceListItem(
    label: "Transaction",
    value: "$100.00",
    supportText: "12:30 PM",
    onItemClicked: { /* action */ },
    leadingSlot: { /* icon */ }
)
```

## BrandLogo

Payment method brand logos.

```kotlin
// Android
LemonadeUi.BrandLogo(
    logo = LemonadeBrandLogos.Visa,
    size = LemonadeAssetSize.Medium,
)
```

```swift
// iOS
LemonadeUi.BrandLogo(
    logo: .visa,
    size: .medium
)
```

**Logos:** `.visa`, `.mastercard`, `.amex`, `.discover`, `.applePay`, `.googlePay`

## Tile

Quick action tile with icon and label. Used for primary actions like "Add money", "Transfer", etc.

```kotlin
// Android
LemonadeUi.Tile(
    modifier = Modifier.weight(1f),
    label = "Add money",
    icon = LemonadeIcons.MoneyArrowDown,
    variant = LemonadeTileVariant.Neutral,
    onClick = { /* action */ }
)
```

```swift
// iOS - IMPORTANT: onClick must come BEFORE variant
LemonadeUi.Tile(
    label: "Add money",
    icon: .moneyArrowDown,
    onClick: { /* action */ },
    variant: .neutral
)
```

**Variants:** `.neutral` (gray background), `.onColor` (for use on coloured backgrounds)

## Chip

Selectable filter chips.

```kotlin
// Android
LemonadeUi.Chip(
    label = "Filter",
    selected = isSelected,
    trailingIcon = LemonadeIcons.ChevronDown,
    onClick = { /* action */ }
)
```

```swift
// iOS
LemonadeUi.Chip(
    label: "Filter",
    selected: isSelected,
    trailingIcon: .chevronDown,
    onClick: { /* action */ }
)
```

## Component Naming Differences

| Component   | Android               | iOS         |
| ----------- | --------------------- | ----------- |
| Icons       | `LemonadeIcons.Check` | `.check`    |
| Voice enums | `TagVoice.Positive`   | `.positive` |
| Size enums  | `LemonadeButtonSize.Medium` | `.medium` |

## iOS Parameter Ordering

**IMPORTANT:** iOS Swift has strict parameter ordering requirements. When generating iOS code, always verify the parameter order matches the component's Swift API. Common patterns:

- `Tile`: `label`, `icon`, `onClick`, `variant` (onClick BEFORE variant)
- `Button`: `label`, `onClick`, `variant`, `type`, `size`

When in doubt, check existing usage in the codebase or the component definition.
