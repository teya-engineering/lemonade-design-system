import SwiftUI

// MARK: - Toast Voice

/// The voice/variant of a toast notification.
/// Determines the icon and color scheme used for the toast.
public enum LemonadeToastVoice: Sendable {
    /// Success toast with a checkmark icon.
    case success
    /// Error toast with an error icon.
    case error
    /// Neutral toast with a customizable icon.
    case neutral
    /// Loading toast communicating an ongoing action. Shows an animated spinner as the leading
    /// element and persists until it is explicitly dismissed or replaced (no auto-dismiss, not
    /// swipe-dismissible).
    case loading

    internal var icon: LemonadeIcon? {
        switch self {
        case .success: return .circleCheck
        case .error: return .circleX
        case .neutral, .loading: return nil
        }
    }

    internal func iconColor(colors: LemonadeSemanticColors) -> Color {
        switch self {
        case .success: return colors.content.contentPositiveAlwaysOnColor
        case .error: return colors.content.contentCriticalAlwaysOnColor
        case .neutral, .loading: return colors.content.contentNeutralAlwaysOnColor
        }
    }

    /// The sensory feedback type for this voice.
    @available(iOS 17.0, macOS 14.0, tvOS 17.0, watchOS 10.0, *)
    var sensoryFeedback: SensoryFeedback {
        switch self {
        case .success:
            return .success
        case .error:
            return .error
        case .neutral, .loading:
            return .impact
        }
    }
}

// MARK: - Toast Component

public extension LemonadeUi {
    /// Toast component for displaying brief feedback messages with an optional tappable action.
    ///
    /// Displays a brief message with an icon in a pill-shaped container.
    /// An optional action button can be shown at the trailing end of the toast.
    ///
    /// ## Usage
    /// ```swift
    /// // Success toast
    /// LemonadeUi.Toast(label: "Changes saved", voice: .success)
    ///
    /// // Toast with action button
    /// LemonadeUi.Toast(label: "Item deleted", voice: .neutral, actionLabel: "Undo") {
    ///     // handle undo
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - label: The message to display in the toast
    ///   - voice: The voice/variant of the toast (success, error, neutral)
    ///   - icon: Custom icon for neutral toasts only. Ignored for success/error voices.
    ///   - actionLabel: Optional label for the action button shown at the trailing end of the toast.
    ///   - onAction: Optional callback invoked when the action button is tapped. The button is only shown when both `actionLabel` and `onAction` are non-nil.
    /// - Returns: A styled Toast view
    @ViewBuilder
    static func Toast(
        label: String,
        voice: LemonadeToastVoice = .neutral,
        icon: LemonadeIcon? = nil,
        actionLabel: String? = nil,
        onAction: (() -> Void)? = nil
    ) -> some View {
        LemonadeToastView(
            label: label,
            voice: voice,
            customIcon: icon,
            actionLabel: actionLabel,
            onAction: onAction
        )
    }
}

// MARK: - Internal Toast View

private struct LemonadeToastView: View {
    let label: String
    let voice: LemonadeToastVoice
    let customIcon: LemonadeIcon?
    let actionLabel: String?
    let onAction: (() -> Void)?

    private var displayIcon: LemonadeIcon? {
        switch voice {
        case .neutral:
            return customIcon
        case .success, .error:
            return voice.icon
        case .loading:
            // The leading element is a spinner, not a static icon.
            return nil
        }
    }

    private var voiceAccessibilityText: String {
        switch voice {
        case .success: return "Success"
        case .error: return "Error"
        case .neutral: return "Notice"
        case .loading: return "Loading"
        }
    }

    var body: some View {
        HStack(spacing: .space.spacing200) {
            if voice == .loading {
                LemonadeUi.Spinner(
                    tint: voice.iconColor(colors: LemonadeTheme.colors)
                )
            } else if let icon = displayIcon {
                LemonadeUi.Icon(
                    icon: icon,
                    contentDescription: nil,
                    size: .medium,
                    tint: voice.iconColor(colors: LemonadeTheme.colors)
                )
            }

            Text(label)
                .font(LemonadeTypography.shared.bodySmallMedium.font)
                .foregroundStyle(.content.contentAlwaysLight)
                .lineLimit(nil)
                .truncationMode(.tail)
                .padding(.horizontal, .space.spacing100)

            if let actionLabel, let onAction {
                Button(action: onAction) {
                    Text(actionLabel)
                        .font(LemonadeTypography.shared.bodySmallMedium.font)
                        .foregroundStyle(.content.contentInfoAlwaysOnColor)
                        .lineLimit(1)
                        .fixedSize(horizontal: true, vertical: false)
                }
                .buttonStyle(.plain)
                .padding(.vertical, .space.spacing100)
                .contentShape(Rectangle())
                // The action keeps its intrinsic width whatever the message does: the message wraps,
                // and ellipsizes on the last line, rather than the action breaking across lines.
                .layoutPriority(1)
            }
        }
        .padding(
            EdgeInsets(
                top: .space.spacing300,
                leading: .space.spacing400,
                bottom: .space.spacing300,
                trailing: .space.spacing400
            )
        )
        .frame(minHeight: .size.size1100)
        .modifier(ToastBackgroundModifier())
        .lemonadeShadow(.large)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(voiceAccessibilityText): \(label)")
        .modifier(ToastAccessibilityActionModifier(actionLabel: actionLabel, onAction: onAction))
    }
}

// MARK: - Style Helpers

private struct ToastBackgroundModifier: ViewModifier {
    func body(content: Content) -> some View {
        #if compiler(>=6.2)
        if #available(iOS 26.0, macOS 26.0, *) {
            content
                .glassEffect(
                    .regular.tint(.bg.bgAlwaysDark.opacity(.opacity.opacity90)),
                    in: RoundedRectangle(cornerRadius: .radius.radiusFull)
                )
        } else {
            content
                .background(RoundedRectangle(cornerRadius: .radius.radiusFull).fill(.bg.bgAlwaysDark))
        }
        #else
        content
            .background(.bg.bgAlwaysDark)
            .clipShape(RoundedRectangle(cornerRadius: .radius.radiusFull))
        #endif
    }
}

// MARK: - Accessibility Helpers

private struct ToastAccessibilityActionModifier: ViewModifier {
    let actionLabel: String?
    let onAction: (() -> Void)?

    func body(content: Content) -> some View {
        if let actionLabel, let onAction {
            content.accessibilityAction(named: Text(actionLabel), onAction)
        } else {
            content
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeToast_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: .space.spacing600) {
            LemonadeUi.Toast(label: "Changes saved successfully", voice: .success)
            LemonadeUi.Toast(label: "Something went wrong", voice: .error)
            LemonadeUi.Toast(label: "Your session will expire soon", voice: .neutral, icon: .circleAlert)
            LemonadeUi.Toast(label: "Added to favorites", voice: .neutral, icon: .heart)
            LemonadeUi.Toast(label: "Toast without an icon", voice: .neutral)
            LemonadeUi.Toast(label: "Downloading your document…", voice: .loading)
            LemonadeUi.Toast(label: "Changes saved", voice: .success, actionLabel: "Undo") {}
            LemonadeUi.Toast(label: "Something went wrong", voice: .error, actionLabel: "Retry") {}
            LemonadeUi.Toast(label: "Added to favorites", voice: .neutral, icon: .heart, actionLabel: "View") {}
            LemonadeUi.Toast(
                label: "We couldn't sync your latest changes because the connection dropped partway through",
                voice: .error,
                actionLabel: "Try again"
            ) {}
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
        .background(.bg.bgDefault)
        .previewLayout(.sizeThatFits)
    }
}
#endif
