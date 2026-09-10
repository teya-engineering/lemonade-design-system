import SwiftUI

// MARK: - Content Cell Button Style

/// A button style that scales the cell down while it is pressed.
///
/// The spring is short so the scale-down and rebound absorb rapid taps without
/// a linear-looking jitter.
private struct CalendarCellPressStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.92 : 1.0)
            .animation(
                .spring(response: 0.25, dampingFraction: 0.7),
                value: configuration.isPressed
            )
    }
}

// MARK: - Content Cell View

/// A reusable cell view used internally by calendar components.
///
/// Renders a single day number with the appropriate styling based on its state
/// (current day, selected, disabled, outside visible month, or inside a selected range).
struct ContentCellView: View {
    let text: String
    let accessibilityLabel: String
    let isCurrent: Bool
    let isSelected: Bool
    let isEnabled: Bool
    let isOutsideVisibleRange: Bool
    let isInsideSelectedRange: Bool
    let showSelectionBackground: Bool
    let showTodayIndicator: Bool
    let selectionContentColor: Color?
    let onClick: () -> Void

    init(
        text: String,
        accessibilityLabel: String,
        isCurrent: Bool,
        isSelected: Bool,
        isEnabled: Bool,
        isOutsideVisibleRange: Bool,
        isInsideSelectedRange: Bool,
        showSelectionBackground: Bool = true,
        showTodayIndicator: Bool? = nil,
        selectionContentColor: Color? = nil,
        onClick: @escaping () -> Void
    ) {
        self.text = text
        self.accessibilityLabel = accessibilityLabel
        self.isCurrent = isCurrent
        self.isSelected = isSelected
        self.isEnabled = isEnabled
        self.isOutsideVisibleRange = isOutsideVisibleRange
        self.isInsideSelectedRange = isInsideSelectedRange
        self.showSelectionBackground = showSelectionBackground
        self.showTodayIndicator = showTodayIndicator ?? showSelectionBackground
        self.selectionContentColor = selectionContentColor
        self.onClick = onClick
    }

    private var textColor: Color {
        if !isEnabled {
            return LemonadeTheme.colors.content.contentTertiary
        } else if isSelected {
            return selectionContentColor ?? LemonadeTheme.colors.content.contentOnBrandHigh
        } else if isInsideSelectedRange {
            return LemonadeTheme.colors.content.contentOnBrandHigh
        } else if isCurrent {
            return LemonadeTheme.colors.content.contentBrand
        } else if isOutsideVisibleRange {
            return LemonadeTheme.colors.content.contentSecondary
        } else {
            return LemonadeTheme.colors.content.contentPrimary
        }
    }

    private var textStyle: LemonadeTextStyle {
        isCurrent
            ? LemonadeTypography.shared.bodyMediumSemiBold
            : LemonadeTypography.shared.bodyMediumMedium
    }

    private var backgroundColor: Color {
        isSelected && showSelectionBackground
            ? LemonadeTheme.colors.interaction.bgBrandInteractive
            : Color.clear
    }

    var body: some View {
        SwiftUI.Button(action: onClick) {
            ZStack(alignment: .bottom) {
                LemonadeUi.Text(
                    text,
                    textStyle: textStyle,
                    color: textColor
                )
                .padding(.vertical, LemonadeTheme.spaces.spacing200)

                if isCurrent && showTodayIndicator {
                    Circle()
                        .fill(textColor)
                        .frame(
                            width: LemonadeTheme.spaces.spacing100,
                            height: LemonadeTheme.spaces.spacing100
                        )
                        .padding(.bottom, LemonadeTheme.spaces.spacing100)
                }
            }
            .frame(maxWidth: .infinity)
            .background(
                RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius200)
                    .fill(backgroundColor)
            )
        }
        .buttonStyle(CalendarCellPressStyle())
        .disabled(!isEnabled)
        .accessibilityLabel(accessibilityLabel)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
        .accessibilityAddTraits(.isButton)
        .accessibilityHint(isEnabled ? NSLocalizedString("calendar.cell.hint.select", value: "Double tap to select", comment: "VoiceOver hint for selectable calendar day cell") : "")
    }
}
