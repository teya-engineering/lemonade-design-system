import SwiftUI

// MARK: - Icon Size (matching KMP API)

/// Icon sizes following the Lemonade Design System.
/// These sizes match the KMP implementation exactly.
public enum LemonadeUiIconSize {
    case xSmall
    case small
    case medium
    case large
    case xLarge
    case xxLarge
    case xxxLarge
    case xxxxLarge

    /// Returns the CGFloat value for this icon size in points
    public var value: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size300
        case .small: return LemonadeTheme.sizes.size400
        case .medium: return LemonadeTheme.sizes.size500
        case .large: return LemonadeTheme.sizes.size600
        case .xLarge: return LemonadeTheme.sizes.size800
        case .xxLarge: return LemonadeTheme.sizes.size1000
        case .xxxLarge: return LemonadeTheme.sizes.size1200
        case .xxxxLarge: return LemonadeTheme.sizes.size1400
        }
    }
}

// MARK: - Icon Component

public extension LemonadeUi {
    /// Icon component to indicate status and possible actions via iconography.
    ///
    /// Icons are small images with predefined sizes that can or cannot be clickable
    /// and explain behavior to the users visually.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Icon(
    ///     icon: .check,
    ///     contentDescription: "Checkmark icon",
    ///     size: .medium,
    ///     tint: LemonadeTheme.colors.content.contentPrimary
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - icon: The LemonadeIcon to be displayed
    ///   - contentDescription: A localized text that describes the icon or its action. Optional but strongly recommended for accessibility.
    ///   - size: The LemonadeUiIconSize to be applied to the icon. Defaults to .medium
    ///   - tint: The tint color to be applied to the icon. Defaults to contentPrimary
    /// - Returns: A styled Icon view
    @ViewBuilder
    static func Icon(
        icon: LemonadeIcon,
        contentDescription: String?,
        size: LemonadeUiIconSize = .medium,
        tint: Color = LemonadeTheme.colors.content.contentPrimary
    ) -> some View {
        LemonadeIconView(
            icon: icon,
            contentDescription: contentDescription,
            size: size,
            tint: tint
        )
    }
}

// MARK: - Internal Icon View

private struct LemonadeIconView: View {
    let icon: LemonadeIcon
    let contentDescription: String?
    let size: LemonadeUiIconSize
    let tint: Color

    @Environment(\.lemonadeIconScaling) private var scalesWithText
    @LemonadeScale private var scale: LemonadeScaleFactors

    /// The rendered size. An icon paired with a label follows the reader's Dynamic Type setting
    /// the way that label does, because a fixed size shrinks against a scaled label and breaks
    /// the pair. The component opts in, since a larger icon needs a container that can carry it.
    private var renderedSize: CGFloat {
        scalesWithText ? size.value * scale.content : size.value
    }

    var body: some View {
        icon.image
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: renderedSize, height: renderedSize)
            .foregroundStyle(tint)
            .accessibilityLabel(contentDescription ?? "")
            .accessibilityHidden(contentDescription == nil)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeIcon_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            HStack(spacing: 16) {
                LemonadeUi.Icon(
                    icon: .check,
                    contentDescription: "Check",
                    size: .xSmall
                )
                LemonadeUi.Icon(
                    icon: .check,
                    contentDescription: "Check",
                    size: .small
                )
                LemonadeUi.Icon(
                    icon: .check,
                    contentDescription: "Check",
                    size: .medium
                )
                LemonadeUi.Icon(
                    icon: .check,
                    contentDescription: "Check",
                    size: .large
                )
                LemonadeUi.Icon(
                    icon: .check,
                    contentDescription: "Check",
                    size: .xLarge
                )
            }

            HStack(spacing: 16) {
                LemonadeUi.Icon(
                    icon: .airplane,
                    contentDescription: "Airplane",
                    tint: LemonadeTheme.colors.content.contentBrand
                )
                LemonadeUi.Icon(
                    icon: .bell,
                    contentDescription: "Bell",
                    tint: LemonadeTheme.colors.content.contentCritical
                )
                LemonadeUi.Icon(
                    icon: .calendar,
                    contentDescription: "Calendar",
                    tint: LemonadeTheme.colors.content.contentPositive
                )
            }
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
