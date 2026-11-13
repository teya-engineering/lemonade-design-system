# Lemonade DS Layout Patterns

Common layout patterns and status mapping for Android and iOS.

## Card Container

A card is the `Card` component, on a screen with the `bgSubtle` background (`LDS-CARD-NO-HANDROLL`,
`LDS-CARD-SURFACE` in `guidelines/cards.md`).

```kotlin
// Android
LemonadeUi.Card(header = CardHeaderConfig(title = "Today")) {
    LemonadeUi.ContentListItem(label = "Sales", value = "£1,240.50")
}
```

```swift
// iOS
LemonadeUi.Card(header: CardHeaderConfig(title: "Today")) {
    LemonadeUi.ContentListItem(label: "Sales", value: "£1,240.50")
}
```

## List Item Row

Label on left, value on right: `ContentListItem`, never a hand-built row (`LDS-LIST-NO-HANDROLL` in
`guidelines/lists.md`).

```kotlin
// Android
LemonadeUi.ContentListItem(label = "Label", value = "Value")
```

```swift
// iOS
LemonadeUi.ContentListItem(label: "Label", value: "Value")
```

## Section Header

```kotlin
// Android
Text(
    text = "Section Title",
    style = LemonadeTheme.typography.headingXSmall,
    color = LemonadeTheme.colors.content.contentPrimary,
    modifier = Modifier.padding(
        horizontal = LemonadeTheme.spaces.spacing400,
        vertical = LemonadeTheme.spaces.spacing200
    )
)
```

```swift
// iOS
Text("Section Title")
    .font(LemonadeTypography.shared.headingXSmall.font)
    .foregroundColor(LemonadeTheme.colors.content.contentPrimary)
    .padding(.horizontal, LemonadeSpacing.spacing400.value)
    .padding(.vertical, LemonadeSpacing.spacing200.value)
```

---

## Status Mapping Pattern

For status enums that need UI representation (icon, voice, label).

**Resolve the mapping once, on the model, not in the view.** Expose the Lemonade types (`TagVoice`, the icon) as properties of the model, and let the view pass them straight to the component. The label comes from the app's own localised strings. A `when` or `switch` on the status inside the view gets repeated by every screen that shows it.

```kotlin
// Android
data class TransactionDetailModel(
    val status: TransactionStatus,
) {
    val statusVoice: TagVoice get() = when (status) {
        TransactionStatus.PENDING   -> TagVoice.Warning
        TransactionStatus.COMPLETED -> TagVoice.Positive
        TransactionStatus.FAILED    -> TagVoice.Critical
    }

    val statusIcon: LemonadeIcons get() = when (status) {
        TransactionStatus.PENDING   -> LemonadeIcons.Clock
        TransactionStatus.COMPLETED -> LemonadeIcons.Check
        TransactionStatus.FAILED    -> LemonadeIcons.Times
    }
}

LemonadeUi.Tag(
    label = statusLabel,
    icon = model.statusIcon,
    voice = model.statusVoice,
)
```

```swift
// iOS
extension TransactionStatus {
    var voice: TagVoice {
        switch self {
        case .pending: .warning
        case .completed: .positive
        case .failed: .critical
        }
    }

    var icon: LemonadeIcon {
        switch self {
        case .pending: .clock
        case .completed: .check
        case .failed: .times
        }
    }
}

LemonadeUi.Tag(
    label: statusLabel,
    icon: model.status.icon,
    voice: model.status.voice
)
```

### When you need background/foreground colours directly

Most status surfaces should use `LemonadeUi.Tag` or `LemonadeUi.SymbolContainer` - those components own their background and content colours based on `voice`. Only when a design requires a custom layout that exposes colours directly, expose **the semantic role on the Model** (reuse `TagVoice` or define a feature enum), then map to platform tokens at the leaf using the Status Colour Reference table below. That table is generic Lemonade theming - not feature logic - so a tiny `voice → token` map per platform is acceptable there.

## Status Colour Reference

| Status            | Background         | Foreground         | Icon          | Voice       |
| ----------------- | ------------------ | ------------------ | ------------- | ----------- |
| Success/Completed | `bgPositiveSubtle` | `contentPositive`  | `.check`      | `.positive` |
| Error/Failed      | `bgCriticalSubtle` | `contentCritical`  | `.times`      | `.critical` |
| Warning/Pending   | `bgCautionSubtle`  | `contentCaution`   | `.clock`      | `.warning`  |
| Info              | `bgInfoSubtle`     | `contentInfo`      | `.circleInfo` | `.info`     |
| Neutral/Default   | `bgSubtle`         | `contentSecondary` | varies        | `.neutral`  |
