import SwiftUI

// MARK: - Switch Component

public extension LemonadeUi {
    /// A toggle switch component that provides visual feedback for on/off states.
    ///
    /// Uses the native SwiftUI `Toggle` for full accessibility, haptic feedback,
    /// and automatic Liquid Glass support on iOS 26+.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Switch(
    ///     checked: true,
    ///     onCheckedChange: { newValue in /* handle change */ }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - checked: `true` if the switch is in the "on" state, `false` otherwise.
    ///   - onCheckedChange: A callback invoked when the user interacts with the switch.
    ///   - enabled: Controls the enabled state of the switch. Defaults to true.
    /// - Returns: A styled Switch view
    @ViewBuilder
    static func Switch(
        checked: Bool,
        onCheckedChange: @escaping (Bool) -> Void,
        enabled: Bool = true
    ) -> some View {
        LemonadeSwitchView(
            checked: checked,
            onCheckedChange: onCheckedChange,
            enabled: enabled
        )
    }

    /// A toggle switch component with label and optional support text.
    ///
    /// Uses the native SwiftUI `Toggle` for full accessibility, haptic feedback,
    /// and automatic Liquid Glass support on iOS 26+.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Switch(
    ///     checked: true,
    ///     onCheckedChange: { newValue in /* handle change */ },
    ///     label: "Instant Settlements",
    ///     supportText: "Enable instant settlement processing"
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - checked: `true` if the switch is in the "on" state, `false` otherwise.
    ///   - onCheckedChange: A callback invoked when the user interacts with the switch.
    ///   - label: A String to be shown as the label for the component.
    ///   - supportText: Optional String shown as support text below the label.
    ///   - enabled: Controls the enabled state of the switch. Defaults to true.
    /// - Returns: A styled Switch view with label
    @ViewBuilder
    static func Switch(
        checked: Bool,
        onCheckedChange: @escaping (Bool) -> Void,
        label: String,
        supportText: String? = nil,
        enabled: Bool = true
    ) -> some View {
        LemonadeSwitchWithLabel(
            checked: checked,
            onCheckedChange: onCheckedChange,
            label: label,
            supportText: supportText,
            enabled: enabled
        )
    }
}

// MARK: - Core Switch (Native Toggle)

private struct LemonadeSwitchView: View {
    let checked: Bool
    let onCheckedChange: (Bool) -> Void
    let enabled: Bool

    private var isOn: Binding<Bool> {
        Binding(
            get: { checked },
            set: { onCheckedChange($0) }
        )
    }

    var body: some View {
        Toggle("", isOn: isOn)
            .labelsHidden()
            .tint(LemonadeTheme.colors.background.bgBrandHigh)
            .accessibilityLabel("Switch")
            .disabled(!enabled)
    }
}

// MARK: - Switch With Label (Native Toggle)

private struct LemonadeSwitchWithLabel: View {
    let checked: Bool
    let onCheckedChange: (Bool) -> Void
    let label: String
    let supportText: String?
    let enabled: Bool

    private var isOn: Binding<Bool> {
        Binding(
            get: { checked },
            set: { onCheckedChange($0) }
        )
    }

    var body: some View {
        Toggle(isOn: isOn) {
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
        }
        .tint(LemonadeTheme.colors.background.bgBrandHigh)
        .disabled(!enabled)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeSwitch_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            unlabeledSwitches
            disabledSwitches
            labeledSwitches
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }

    private static var unlabeledSwitches: some View {
        HStack(spacing: 16) {
            LemonadeUi.Switch(
                checked: true,
                onCheckedChange: { _ in }
            )
            LemonadeUi.Switch(
                checked: false,
                onCheckedChange: { _ in }
            )
        }
    }

    private static var disabledSwitches: some View {
        HStack(spacing: 16) {
            LemonadeUi.Switch(
                checked: true,
                onCheckedChange: { _ in },
                enabled: false
            )
            LemonadeUi.Switch(
                checked: false,
                onCheckedChange: { _ in },
                enabled: false
            )
        }
    }

    private static var labeledSwitches: some View {
        VStack(alignment: .leading, spacing: 16) {
            LemonadeUi.Switch(
                checked: true,
                onCheckedChange: { _ in },
                label: "Instant Settlements",
                supportText: "Enable instant settlement processing"
            )

            LemonadeUi.Switch(
                checked: false,
                onCheckedChange: { _ in },
                label: "Notifications",
                supportText: "Receive push notifications"
            )

            LemonadeUi.Switch(
                checked: true,
                onCheckedChange: { _ in },
                label: "Disabled Option",
                supportText: "This option cannot be changed",
                enabled: false
            )
        }
    }
}
#endif
