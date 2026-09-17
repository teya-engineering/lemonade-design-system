import SwiftUI

// MARK: - Link Size

/// Size of a link, chosen to match the body text around it: `small` pairs with `bodySmall` text,
/// `medium` with `bodyMedium` text and `large` with `bodyLarge` text.
public enum LemonadeLinkSize {
    case small
    case medium
    case large

    var textStyle: LemonadeTextStyle {
        switch self {
        case .small: return LemonadeTypography.shared.bodySmallMedium
        case .medium: return LemonadeTypography.shared.bodyMediumMedium
        case .large: return LemonadeTypography.shared.bodyLargeMedium
        }
    }
}

// MARK: - Link Component

public extension LemonadeUi {
    /// A clickable text component styled as a hyperlink.
    /// Displays underlined text in brand color with optional trailing icon,
    /// and provides animated color feedback for pressed states.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Link(
    ///     text: "Learn more",
    ///     onClick: { print("link clicked!") }
    /// )
    ///
    /// LemonadeUi.Link(
    ///     text: "External link",
    ///     onClick: { },
    ///     icon: .externalLink
    /// )
    ///
    /// LemonadeUi.Link(
    ///     text: "Contact us",
    ///     onClick: { },
    ///     size: .small
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - text: String to be displayed as the link label
    ///   - onClick: Callback to be invoked when the link is tapped
    ///   - enabled: Boolean flag to enable or disable the link
    ///   - icon: Optional trailing LemonadeIcon shown after the text
    ///   - size: LemonadeLinkSize matching the body text around the link, defaults to `.medium`
    /// - Returns: A styled link view
    @ViewBuilder
    static func Link(
        text: String,
        onClick: @escaping () -> Void,
        enabled: Bool = true,
        icon: LemonadeIcon? = nil,
        size: LemonadeLinkSize = .medium
    ) -> some View {
        LemonadeLinkView(
            text: text,
            onClick: onClick,
            enabled: enabled,
            icon: icon,
            size: size
        )
    }
}

// MARK: - Internal Link View

private struct LemonadeLinkView: View {
    let text: String
    let onClick: () -> Void
    let enabled: Bool
    let icon: LemonadeIcon?
    let size: LemonadeLinkSize

    @State private var isPressed = false

    private var currentColor: Color {
        if isPressed {
            return LemonadeTheme.colors.interaction.bgBrandPressed
        }
        return LemonadeTheme.colors.content.contentBrand
    }

    var body: some View {
        SwiftUI.Button(action: onClick) {
            HStack(spacing: LemonadeTheme.spaces.spacing100) {
                SwiftUI.Text(text)
                    .font(size.textStyle.font)
                    .foregroundColor(currentColor)
                    .underline(true, color: currentColor)

                if let icon = icon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .small,
                        tint: currentColor
                    )
                }
            }
        }
        .buttonStyle(LemonadePressTrackingButtonStyle(isPressed: $isPressed))
        .disabled(!enabled)
        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
        .accessibilityAddTraits(.isLink)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeLink_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 24) {
            LemonadeUi.Link(
                text: "Learn more",
                onClick: { }
            )

            LemonadeUi.Link(
                text: "External link",
                onClick: { },
                icon: .externalLink
            )

            LemonadeUi.Link(
                text: "Small link",
                onClick: { },
                icon: .externalLink,
                size: .small
            )

            LemonadeUi.Link(
                text: "Disabled link",
                onClick: { },
                enabled: false
            )

            LemonadeUi.Link(
                text: "Disabled with icon",
                onClick: { },
                enabled: false,
                icon: .externalLink
            )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
