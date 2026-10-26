import SwiftUI

// MARK: - Shared Text Field Constants

enum TextFieldConstants {
    static var minHeight: CGFloat { LemonadeTheme.sizes.size1400 }
    static let cornerRadius: CGFloat = LemonadeTheme.radius.radius400
    static let horizontalPadding: CGFloat = LemonadeTheme.spaces.spacing300
    static let verticalPadding: CGFloat = LemonadeTheme.spaces.spacing300
}

// MARK: - Shared Color Helpers

/// Computes the background color for text field variants that support focus.
/// Used by LemonadeTextFieldView, LemonadeTextFieldWithSelectorView,
/// LemonadeTextFieldValueView (iOS), and LemonadeTextFieldWithSelectorValueView (iOS).
func textFieldBackgroundColor(
    enabled: Bool,
    error: Bool,
    isFocused: Bool,
    isHovered: Bool
) -> Color {
    switch (enabled, error, isFocused, isHovered) {
    case (false, _, _, _):
        return LemonadeTheme.colors.background.bgElevated
    case (true, true, false, _):
        return LemonadeTheme.colors.background.bgCriticalSubtle
    case (true, _, _, true):
        return LemonadeTheme.colors.interaction.bgSubtleInteractive
    default:
        return LemonadeTheme.colors.background.bgDefault
    }
}

/// Computes the background color for select field variants (no focus state).
/// Used by LemonadeSelectFieldView.
func selectFieldBackgroundColor(
    enabled: Bool,
    error: Bool,
    isHovered: Bool
) -> Color {
    switch (enabled, error, isHovered) {
    case (false, _, _):
        return LemonadeTheme.colors.background.bgElevated
    case (true, true, _):
        return LemonadeTheme.colors.background.bgCriticalSubtle
    case (true, _, true):
        return LemonadeTheme.colors.interaction.bgSubtleInteractive
    default:
        return LemonadeTheme.colors.background.bgDefault
    }
}

/// Computes the border color for text field variants that support focus.
func textFieldBorderColor(
    enabled: Bool,
    isFocused: Bool,
    error: Bool
) -> Color {
    switch (enabled, isFocused, error) {
    case (false, _, _):
        return .clear
    case (true, true, _):
        return LemonadeTheme.colors.border.borderSelected
    case (true, false, true):
        return LemonadeTheme.colors.border.borderCritical
    default:
        return LemonadeTheme.colors.border.borderNeutralMedium
    }
}

/// Computes the border color for select field variants (no focus state).
func selectFieldBorderColor(
    enabled: Bool,
    error: Bool
) -> Color {
    switch (enabled, error) {
    case (false, _):
        return .clear
    case (true, true):
        return LemonadeTheme.colors.border.borderCritical
    default:
        return LemonadeTheme.colors.border.borderNeutralMedium
    }
}

// MARK: - Label Row

/// Displays the label and optional indicator row above a text/select field.
struct TextFieldLabelRow: View {
    let label: String?
    let optionalIndicator: String?
    let enabled: Bool

    var body: some View {
        if label != nil || optionalIndicator != nil {
            HStack {
                if let label = label {
                    LemonadeUi.Text(
                        label,
                        textStyle: LemonadeTypography.shared.bodySmallMedium,
                        color: enabled
                            ? LemonadeTheme.colors.content.contentPrimary
                            : LemonadeTheme.colors.content.contentSecondary
                    )
                }

                Spacer()

                if let optionalIndicator = optionalIndicator {
                    LemonadeUi.Text(
                        optionalIndicator,
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing50)
        }
    }
}

// MARK: - Selector

/// The tappable prefix element at the leading edge of a text field with selector.
struct TextFieldSelectorButton<LeadingContent: View>: View {
    let leadingAction: () -> Void
    let leadingContent: () -> LeadingContent
    let enabled: Bool

    var body: some View {
        SwiftUI.Button(action: leadingAction) {
            leadingContent()
                .padding(LemonadeTheme.spaces.spacing400)
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
    }
}

/// The vertical rule between a selector and the text input area beside it.
struct TextFieldSelectorDivider: View {
    var body: some View {
        Rectangle()
            .fill(LemonadeTheme.colors.border.borderNeutralMedium)
            .frame(width: LemonadeTheme.borderWidth.base.border25)
            .frame(minHeight: TextFieldConstants.minHeight)
    }
}

// MARK: - Accessibility Name

private struct LemonadeFieldAccessibilityLabelKey: EnvironmentKey {
    static let defaultValue: String? = nil
}

extension EnvironmentValues {
    /// Read by the platform input inside a text field or text area, which is the only element
    /// VoiceOver focuses there: the visible label row and placeholder are hidden from it.
    var lemonadeFieldAccessibilityLabel: String? {
        get { self[LemonadeFieldAccessibilityLabelKey.self] }
        set { self[LemonadeFieldAccessibilityLabelKey.self] = newValue }
    }
}

/// Names a SwiftUI-backed input (the macOS fallbacks) from ``EnvironmentValues/lemonadeFieldAccessibilityLabel``.
struct FieldAccessibilityLabel: ViewModifier {
    @Environment(\.lemonadeFieldAccessibilityLabel) private var name

    func body(content: Content) -> some View {
        if let name {
            content.accessibilityLabel(Text(name))
        } else {
            content
        }
    }
}

/// The visible label and optional indicator, or the placeholder when there is no label.
func textFieldAccessibilityLabel(label: String?, optionalIndicator: String?, placeholder: String?) -> String? {
    guard let label else { return placeholder }
    return [label, optionalIndicator].compactMap { $0 }.joined(separator: ", ")
}

// MARK: - Support / Error Text

/// Displays the support text or error message, and an optional counter, below a text/select field.
struct TextFieldSupportText: View {
    let supportText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    var counter: String? = nil
    var counterOverLimit = false

    private var showError: Bool { enabled && error && errorMessage != nil }
    private var message: String? { showError ? errorMessage : supportText }
    private var messageColor: Color {
        showError ? LemonadeTheme.colors.content.contentCritical : LemonadeTheme.colors.content.contentSecondary
    }

    var body: some View {
        if message != nil || counter != nil {
            HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing200) {
                if let message = message {
                    LemonadeUi.Text(
                        message,
                        textStyle: LemonadeTypography.shared.bodyXSmallRegular,
                        color: messageColor
                    )
                    .frame(maxWidth: counter == nil ? nil : .infinity, alignment: .leading)
                }

                if let counter = counter {
                    if message == nil {
                        Spacer()
                    }
                    LemonadeUi.Text(
                        counter,
                        textStyle: LemonadeTypography.shared.bodyXSmallRegular,
                        color: enabled && counterOverLimit
                            ? LemonadeTheme.colors.content.contentCritical
                            : LemonadeTheme.colors.content.contentSecondary
                    )
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing50)
        }
    }
}

// MARK: - Container Decoration Modifier

/// Applies the standard text field container styling: rounded fill background, border overlay,
/// focus ring overlay, opacity, and hover tracking.
struct TextFieldContainerModifier: ViewModifier {
    let backgroundColor: Color
    let borderColor: Color
    let enabled: Bool
    let isFocused: Bool
    let cornerRadius: CGFloat
    var applyOpacity: Bool = true
    var minHeight: CGFloat = TextFieldConstants.minHeight
    @Binding var isHovered: Bool

    func body(content: Content) -> some View {
        content
            .frame(minHeight: minHeight)
            .background(RoundedRectangle(cornerRadius: cornerRadius).fill(backgroundColor))
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(borderColor, lineWidth: LemonadeTheme.borderWidth.base.border25)
            )
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(
                        isFocused && enabled ? LemonadeTheme.colors.border.borderSelected : .clear,
                        lineWidth: LemonadeTheme.borderWidth.state.focusRing
                    )
            )
            .opacity(applyOpacity && !enabled ? LemonadeTheme.opacity.state.opacityDisabled : 1.0)
            .onHover { hovering in
                isHovered = hovering
            }
    }
}

/// Applies the select field container styling (no focus ring, no default bg layer).
struct SelectFieldContainerModifier: ViewModifier {
    let backgroundColor: Color
    let borderColor: Color
    let enabled: Bool
    let cornerRadius: CGFloat
    @Binding var isHovered: Bool

    func body(content: Content) -> some View {
        content
            .frame(minHeight: TextFieldConstants.minHeight)
            .background(RoundedRectangle(cornerRadius: cornerRadius).fill(backgroundColor))
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(borderColor, lineWidth: LemonadeTheme.borderWidth.base.border25)
            )
            .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            .onHover { hovering in
                isHovered = hovering
            }
    }
}

// MARK: - UIKit Focus and Dynamic Type

#if canImport(UIKit)
import UIKit

extension LemonadeTextStyle {
    /// Scales the face the way `Font.custom(_:size:relativeTo: .body)` scales ``LemonadeUi/Text``,
    /// so typed text grows with the label and placeholder around it.
    func scaledUIFont(for dynamicTypeSize: DynamicTypeSize) -> UIFont {
        UIFontMetrics(forTextStyle: .body).scaledFont(
            for: uiFont,
            compatibleWith: UITraitCollection(preferredContentSizeCategory: dynamicTypeSize.contentSizeCategory)
        )
    }
}

private extension DynamicTypeSize {
    var contentSizeCategory: UIContentSizeCategory {
        switch self {
        case .xSmall: return .extraSmall
        case .small: return .small
        case .medium: return .medium
        case .large: return .large
        case .xLarge: return .extraLarge
        case .xxLarge: return .extraExtraLarge
        case .xxxLarge: return .extraExtraExtraLarge
        case .accessibility1: return .accessibilityMedium
        case .accessibility2: return .accessibilityLarge
        case .accessibility3: return .accessibilityExtraLarge
        case .accessibility4: return .accessibilityExtraExtraLarge
        case .accessibility5: return .accessibilityExtraExtraExtraLarge
        @unknown default: return .large
        }
    }
}

extension UIView {
    /// Runs on the next main-queue turn: a responder change inside a SwiftUI update can hand focus
    /// back to a view that is redrawing in the same pass.
    func scheduleFirstResponderUpdate(wantsFocus: Bool) {
        guard wantsFocus != isFirstResponder else { return }
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            if wantsFocus, !self.isFirstResponder, self.window != nil {
                self.becomeFirstResponder()
            } else if !wantsFocus, self.isFirstResponder {
                self.resignFirstResponder()
            }
        }
    }
}
#endif
