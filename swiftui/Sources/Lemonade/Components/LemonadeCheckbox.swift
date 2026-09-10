import SwiftUI

// MARK: - Checkbox Status

/// Represents the state of a checkbox.
public enum CheckboxStatus {
    case checked
    case unchecked
    case indeterminate

    var icon: LemonadeIcon? {
        switch self {
        case .checked: return .checkSmall
        case .unchecked: return nil
        case .indeterminate: return .minus
        }
    }
}

// MARK: - Checkbox Component

public extension LemonadeUi {
    /// A form control that lets users select one or more options from a set.
    /// Supports checked, unchecked, and indeterminate states for flexible selection logic.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Checkbox(
    ///     status: .checked,
    ///     onCheckboxClicked: { /* on box toggle behaviour */ },
    ///     label: "Label",
    ///     supportText: "Support text",
    ///     enabled: true
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - status: The current CheckboxStatus of the checkbox
    ///   - onCheckboxClicked: A callback invoked when the user clicks the checkbox
    ///   - label: The primary text label displayed next to the checkbox
    ///   - supportText: Optional secondary text displayed below the label
    ///   - enabled: Controls the enabled state of the checkbox
    /// - Returns: A styled Checkbox view with label
    @ViewBuilder
    static func Checkbox(
        status: CheckboxStatus,
        onCheckboxClicked: @escaping () -> Void,
        label: String,
        supportText: String? = nil,
        enabled: Bool = true
    ) -> some View {
        LemonadeCheckboxWithLabel(
            status: status,
            onCheckboxClicked: onCheckboxClicked,
            label: label,
            supportText: supportText,
            enabled: enabled
        )
    }

    /// This view displays only the visual checkbox element. It's useful for custom layouts
    /// where the label is handled separately.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Checkbox(
    ///     status: .indeterminate,
    ///     onCheckboxClicked: { /* on box toggle behaviour */ },
    ///     enabled: true
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - status: The current CheckboxStatus of the checkbox
    ///   - onCheckboxClicked: A callback invoked when the user clicks the checkbox
    ///   - enabled: Controls the enabled state of the checkbox
    /// - Returns: A styled Checkbox view without label
    @ViewBuilder
    static func Checkbox(
        status: CheckboxStatus,
        onCheckboxClicked: @escaping () -> Void,
        enabled: Bool = true
    ) -> some View {
        LemonadeCoreCheckbox(
            status: status,
            onCheckboxClicked: onCheckboxClicked,
            enabled: enabled
        )
    }
}

// MARK: - Checkbox With Label

private struct LemonadeCheckboxWithLabel: View {
    let status: CheckboxStatus
    let onCheckboxClicked: () -> Void
    let label: String
    let supportText: String?
    let enabled: Bool

    var body: some View {
        SwiftUI.Button(action: onCheckboxClicked) {
            HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing200) {
                LemonadeCoreCheckbox(
                    status: status,
                    onCheckboxClicked: onCheckboxClicked,
                    enabled: enabled
                )
                .allowsHitTesting(false)

                VStack(alignment: .leading, spacing: 0) {
                    LemonadeUi.Text(
                        label,
                        textStyle: LemonadeTypography.shared.bodyMediumMedium
                    )

                    if let supportText = supportText {
                        LemonadeUi.Text(
                            supportText,
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentSecondary
                        )
                    }
                }
                .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            }
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
    }
}

// MARK: - Core Checkbox

private struct LemonadeCoreCheckbox: View {
    let status: CheckboxStatus
    let onCheckboxClicked: () -> Void
    let enabled: Bool

    @State private var isHovered = false

    private let checkboxSize: CGFloat = LemonadeTheme.sizes.size550

    private var backgroundColor: Color {
        switch (enabled, status, isHovered) {
        case (false, _, _):
            return LemonadeTheme.colors.background.bgElevatedHigh
        case (true, .unchecked, true):
            return LemonadeTheme.colors.interaction.bgSubtleInteractive
        case (true, .unchecked, false):
            return LemonadeTheme.colors.background.bgDefault
        case (true, _, true):
            return LemonadeTheme.colors.content.contentPrimaryInverse
        case (true, _, false):
            return LemonadeTheme.colors.background.bgBrandHigh
        }
    }

    private var borderColor: Color {
        switch (enabled, status) {
        case (false, .unchecked):
            return LemonadeTheme.colors.border.borderNeutralMedium
        case (true, .unchecked):
            return LemonadeTheme.colors.border.borderNeutralHigh
        default:
            return .clear
        }
    }

    private var iconTint: Color {
        enabled
            ? LemonadeTheme.colors.content.contentPrimaryInverse
            : LemonadeTheme.colors.content.contentTertiary
    }

    var body: some View {
        SwiftUI.Button(action: onCheckboxClicked) {
            ZStack {
                RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius150)
                    .fill(backgroundColor)
                    .overlay(
                        RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius150)
                            .strokeBorder(borderColor, lineWidth: LemonadeTheme.borderWidth.base.border50)
                    )
                    .frame(width: checkboxSize, height: checkboxSize)

                if let icon = status.icon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: status.description,
                        size: .medium,
                        tint: iconTint
                    )
                    .transition(.scale.combined(with: .opacity))
                }
            }
            .animation(.easeInOut(duration: 0.15), value: status)
            .animation(.easeInOut(duration: 0.15), value: isHovered)
        }
        .frame(height: LemonadeTheme.sizes.size600)
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .onHover { hovering in
            isHovered = hovering
        }
    }
}

// MARK: - CheckboxStatus Description

extension CheckboxStatus: CustomStringConvertible {
    public var description: String {
        switch self {
        case .checked: return "Checked"
        case .unchecked: return "Unchecked"
        case .indeterminate: return "Indeterminate"
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeCheckbox_Previews: PreviewProvider {
    private static var unlabeledRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.Checkbox(
                status: .checked,
                onCheckboxClicked: {}
            )
            LemonadeUi.Checkbox(
                status: .unchecked,
                onCheckboxClicked: {}
            )
            LemonadeUi.Checkbox(
                status: .indeterminate,
                onCheckboxClicked: {}
            )
        }
    }

    private static var disabledRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.Checkbox(
                status: .checked,
                onCheckboxClicked: {},
                enabled: false
            )
            LemonadeUi.Checkbox(
                status: .unchecked,
                onCheckboxClicked: {},
                enabled: false
            )
            LemonadeUi.Checkbox(
                status: .indeterminate,
                onCheckboxClicked: {},
                enabled: false
            )
        }
    }

    private static var labeledColumn: some View {
        VStack(alignment: .leading, spacing: 16) {
            LemonadeUi.Checkbox(
                status: .checked,
                onCheckboxClicked: {},
                label: "Label",
                supportText: "Support text"
            )

            LemonadeUi.Checkbox(
                status: .unchecked,
                onCheckboxClicked: {},
                label: "Label",
                supportText: "Support text"
            )

            LemonadeUi.Checkbox(
                status: .indeterminate,
                onCheckboxClicked: {},
                label: "Label",
                supportText: "Support text",
                enabled: false
            )
        }
    }

    static var previews: some View {
        VStack(spacing: 24) {
            unlabeledRow
            disabledRow
            labeledColumn
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
