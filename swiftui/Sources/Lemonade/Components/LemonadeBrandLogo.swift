import SwiftUI

// MARK: - Brand Logo Size

/// Size variants for the Brand Logo component
public enum LemonadeBrandLogoSize {
    case small
    case medium
    case large
    case xLarge
    case xxLarge
    case xxxLarge
    case xxxxLarge

    /// Returns the CGFloat value for this size
    public var value: CGFloat {
        switch self {
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

// MARK: - Brand Logo Component

public extension LemonadeUi {
    /// Brand Logo component, to display Card Schemes in a standardized way.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.BrandLogo(
    ///     logo: .visa,
    ///     size: .medium
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - logo: The `LemonadeBrandLogo` to be displayed
    ///   - size: The `LemonadeBrandLogoSize` to be applied. Defaults to `.medium`
    /// - Returns: A styled brand logo view
    @ViewBuilder
    static func BrandLogo(
        logo: LemonadeBrandLogo,
        size: LemonadeBrandLogoSize = .medium
    ) -> some View {
        LemonadeBrandLogoView(logo: logo, size: size)
    }
}

// MARK: - Internal View

private struct LemonadeBrandLogoView: View {
    let logo: LemonadeBrandLogo
    let size: LemonadeBrandLogoSize

    var body: some View {
        logoImage
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width: size.value, height: size.value)
    }

    private var logoImage: Image {
        Image(logo.rawValue, bundle: .lemonade)
    }
}

// MARK: - Preview

#Preview("Brand Logo Sizes") {
    VStack(spacing: 16) {
        HStack(spacing: 16) {
            LemonadeUi.BrandLogo(logo: .visa, size: .small)
            LemonadeUi.BrandLogo(logo: .visa, size: .medium)
            LemonadeUi.BrandLogo(logo: .visa, size: .large)
        }
        HStack(spacing: 16) {
            LemonadeUi.BrandLogo(logo: .visa, size: .xLarge)
            LemonadeUi.BrandLogo(logo: .visa, size: .xxLarge)
        }
    }
    .padding()
}

#Preview("Multiple Brand Logos") {
    HStack(spacing: 12) {
        LemonadeUi.BrandLogo(logo: .visa, size: .xxLarge)
        LemonadeUi.BrandLogo(logo: .mastercard, size: .xxLarge)
        LemonadeUi.BrandLogo(logo: .amex, size: .xxLarge)
        LemonadeUi.BrandLogo(logo: .applePay, size: .xxLarge)
    }
    .padding()
}
