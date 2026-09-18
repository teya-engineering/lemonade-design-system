import SwiftUI

// MARK: - Font Extension for Lemonade Typography

/// Enables shorthand font access like `.bodyMediumRegular`
/// Usage:
/// ```swift
/// Text("Hello")
///     .font(.bodyMediumRegular)
/// ```

public extension Font {
    // MARK: - Display Styles

    static var displayXSmall: Font { LemonadeTypography.shared.displayXSmall.font }
    static var displaySmall: Font { LemonadeTypography.shared.displaySmall.font }
    static var displayMedium: Font { LemonadeTypography.shared.displayMedium.font }
    static var displayLarge: Font { LemonadeTypography.shared.displayLarge.font }
    static var displayXLarge: Font { LemonadeTypography.shared.displayXLarge.font }
    static var display2XLarge: Font { LemonadeTypography.shared.display2XLarge.font }
    static var display3XLarge: Font { LemonadeTypography.shared.display3XLarge.font }

    // MARK: - Heading Styles

    static var headingXLarge: Font { LemonadeTypography.shared.headingXLarge.font }
    static var headingLarge: Font { LemonadeTypography.shared.headingLarge.font }
    static var headingMedium: Font { LemonadeTypography.shared.headingMedium.font }
    static var headingSmall: Font { LemonadeTypography.shared.headingSmall.font }
    static var headingXSmall: Font { LemonadeTypography.shared.headingXSmall.font }
    static var headingXXSmall: Font { LemonadeTypography.shared.headingXXSmall.font }

    // MARK: - Body XLarge Styles

    static var bodyXLargeRegular: Font { LemonadeTypography.shared.bodyXLargeRegular.font }
    static var bodyXLargeMedium: Font { LemonadeTypography.shared.bodyXLargeMedium.font }
    static var bodyXLargeSemiBold: Font { LemonadeTypography.shared.bodyXLargeSemiBold.font }

    // MARK: - Body Large Styles

    static var bodyLargeRegular: Font { LemonadeTypography.shared.bodyLargeRegular.font }
    static var bodyLargeMedium: Font { LemonadeTypography.shared.bodyLargeMedium.font }
    static var bodyLargeSemiBold: Font { LemonadeTypography.shared.bodyLargeSemiBold.font }

    // MARK: - Body Medium Styles

    static var bodyMediumRegular: Font { LemonadeTypography.shared.bodyMediumRegular.font }
    static var bodyMediumMedium: Font { LemonadeTypography.shared.bodyMediumMedium.font }
    static var bodyMediumSemiBold: Font { LemonadeTypography.shared.bodyMediumSemiBold.font }
    @available(*, deprecated, renamed: "bodyMediumSemiBold", message: "Bold is not a public Lemonade text style.")
    static var bodyMediumBold: Font { LemonadeTypography.shared.bodyMediumBold.font }

    // MARK: - Body Small Styles

    static var bodySmallRegular: Font { LemonadeTypography.shared.bodySmallRegular.font }
    static var bodySmallMedium: Font { LemonadeTypography.shared.bodySmallMedium.font }
    static var bodySmallSemiBold: Font { LemonadeTypography.shared.bodySmallSemiBold.font }

    // MARK: - Body XSmall Styles

    static var bodyXSmallRegular: Font { LemonadeTypography.shared.bodyXSmallRegular.font }
    static var bodyXSmallMedium: Font { LemonadeTypography.shared.bodyXSmallMedium.font }
    static var bodyXSmallSemiBold: Font { LemonadeTypography.shared.bodyXSmallSemiBold.font }
    static var bodyXSmallOverline: Font { LemonadeTypography.shared.bodyXSmallOverline.font }
}
