import SwiftUI

#if canImport(UIKit)
import UIKit
#endif

/// The set of faces the design system draws with.
///
/// The type scale carries metrics only — size, line height, weight, letter spacing — and never a
/// family, so the family is a separate axis. Supply one to render Lemonade in another typeface:
///
/// ```swift
/// ContentView().lemonadeFontFamily(
///     LemonadeFontFamily(regular: "Brand-Regular", medium: "Brand-Medium", semibold: "Brand-SemiBold")
/// )
/// ```
///
/// Registering the faces is the consumer's job — `LemonadeFonts.registerFonts()` only knows about
/// Figtree. An unregistered name falls back to the system font rather than failing loudly.
///
/// The line heights in the scale were drawn against Figtree's metrics, so a face with a very
/// different ascender/descender ratio will sit differently inside them.
public struct LemonadeFontFamily: Sendable, Equatable {
    public let regular: String
    public let medium: String
    public let semibold: String

    public init(regular: String, medium: String, semibold: String) {
        self.regular = regular
        self.medium = medium
        self.semibold = semibold
    }

    /// The design system's own faces, and the default everywhere.
    public static let figtree = LemonadeFontFamily(
        regular: "Figtree-Regular",
        medium: "Figtree-Medium",
        semibold: "Figtree-SemiBold"
    )

    /// Figtree ships no true bold, so `.bold` resolves to the semibold face — the same mapping the
    /// design system has always used.
    public func fontName(for fontWeight: Font.Weight) -> String {
        switch fontWeight {
        case .regular: return regular
        case .medium: return medium
        case .semibold, .bold: return semibold
        default: return regular
        }
    }
}

private struct LemonadeFontFamilyKey: EnvironmentKey {
    static let defaultValue: LemonadeFontFamily = .figtree
}

extension EnvironmentValues {
    public var lemonadeFontFamily: LemonadeFontFamily {
        get { self[LemonadeFontFamilyKey.self] }
        set { self[LemonadeFontFamilyKey.self] = newValue }
    }
}

extension View {
    /// Draws every Lemonade component in this subtree with `family`.
    public func lemonadeFontFamily(_ family: LemonadeFontFamily) -> some View {
        environment(\.lemonadeFontFamily, family)
    }
}

/// Represents a text style with typographic properties.
public struct LemonadeTextStyle: Sendable {
    /// The font size in points
    public let fontSize: CGFloat
    /// The line height in points
    public let lineHeight: CGFloat
    /// The font weight
    public let fontWeight: Font.Weight
    /// The letter spacing in points, nil if default
    public let letterSpacing: CGFloat?

    /// The line spacing needed to achieve the desired line height.
    ///
    /// Resolved once here rather than on every read. This is read from inside `body` by every
    /// ``LemonadeUi/Text(_:textStyle:textAlign:color:maxLines:)``, the most instantiated component
    /// in the library, and asking a `UIFont` for its `lineHeight` is a CoreText lookup roughly
    /// three orders of magnitude more expensive than a stored-property load. Every input is a
    /// stored `let`, so the answer cannot change for a given style.
    public let lineSpacing: CGFloat

    /// Fallback line height ratio for Figtree font (used on platforms without UIKit).
    /// Calculated from font metrics: (ascender - descender + lineGap) / unitsPerEm
    /// Figtree: (950 - (-250) + 0) / 1000 = 1.20
    private static let fallbackLineHeightRatio: CGFloat = 1.20

    public init(
        fontSize: CGFloat,
        lineHeight: CGFloat,
        fontWeight: Font.Weight,
        letterSpacing: CGFloat? = nil
    ) {
        self.fontSize = fontSize
        self.lineHeight = lineHeight
        self.fontWeight = fontWeight
        self.letterSpacing = letterSpacing

#if canImport(UIKit)
        let naturalLineHeight = Self.resolvedUIFont(
            name: Self.fontName(for: fontWeight),
            size: fontSize
        ).lineHeight
#else
        let naturalLineHeight = fontSize * Self.fallbackLineHeightRatio
#endif
        self.lineSpacing = max(0, lineHeight - naturalLineHeight)
    }

    /// The font name for a weight.
    ///
    /// Static so ``init(fontSize:lineHeight:fontWeight:letterSpacing:)`` can resolve the font
    /// metric before `self` is fully initialized.
    private static func fontName(for fontWeight: Font.Weight) -> String {
        return LemonadeFontFamily.figtree.fontName(for: fontWeight)
    }

    /// The font name based on the weight
    public var fontName: String {
        Self.fontName(for: fontWeight)
    }

    /// Returns a SwiftUI Font based on this text style, in the design system's own faces.
    public var font: Font {
        font(in: .figtree)
    }

    /// Returns a SwiftUI Font based on this text style, drawn with `family`.
    public func font(in family: LemonadeFontFamily) -> Font {
        .custom(family.fontName(for: fontWeight), size: fontSize, relativeTo: .body)
    }

    /// The line spacing needed to reach this style's `lineHeight` when drawn with `family`.
    ///
    /// ``lineSpacing`` is resolved once in `init` against Figtree, which is only correct for
    /// Figtree: the top-up depends on the face's own natural line height. Any other family has to
    /// be measured.
    ///
    /// The stored value is kept as the fast path, so consumers who never swap the family pay
    /// nothing — which is the point of resolving it in `init` in the first place.
    public func lineSpacing(in family: LemonadeFontFamily) -> CGFloat {
        guard family != .figtree else {
            return lineSpacing
        }
#if canImport(UIKit)
        let natural = Self.resolvedUIFont(
            name: family.fontName(for: fontWeight),
            size: fontSize
        ).lineHeight
#else
        let natural = fontSize * Self.fallbackLineHeightRatio
#endif
        return max(0, lineHeight - natural)
    }

#if canImport(UIKit)
    /// The single place a text style turns into a concrete face.
    ///
    /// `registerFonts()` is idempotent and cheap after the first call. Calling it here means the
    /// `.systemFont` fallback is only ever reached if a face is genuinely missing from the bundle,
    /// rather than because a consumer had not registered yet — which matters now that
    /// ``lineSpacing`` resolves the metric once and keeps it.
    private static func resolvedUIFont(name: String, size: CGFloat) -> UIFont {
        LemonadeFonts.registerFonts()
        return UIFont(name: name, size: size) ?? .systemFont(ofSize: size)
    }

    /// Returns a UIFont based on this text style
    public var uiFont: UIFont {
        Self.resolvedUIFont(name: fontName, size: fontSize)
    }
#endif
}

/// Protocol defining all available text styles in the Lemonade Design System.
public protocol LemonadeTypographyProtocol {
    // Display styles
    var displayXSmall: LemonadeTextStyle { get }
    var displaySmall: LemonadeTextStyle { get }
    var displayMedium: LemonadeTextStyle { get }
    var displayLarge: LemonadeTextStyle { get }
    var displayXLarge: LemonadeTextStyle { get }
    var display2XLarge: LemonadeTextStyle { get }
    var display3XLarge: LemonadeTextStyle { get }

    // Heading styles
    var headingXLarge: LemonadeTextStyle { get }
    var headingLarge: LemonadeTextStyle { get }
    var headingMedium: LemonadeTextStyle { get }
    var headingSmall: LemonadeTextStyle { get }
    var headingXSmall: LemonadeTextStyle { get }
    var headingXXSmall: LemonadeTextStyle { get }

    // Body XLarge styles
    var bodyXLargeRegular: LemonadeTextStyle { get }
    var bodyXLargeMedium: LemonadeTextStyle { get }
    var bodyXLargeSemiBold: LemonadeTextStyle { get }

    // Body Large styles
    var bodyLargeRegular: LemonadeTextStyle { get }
    var bodyLargeMedium: LemonadeTextStyle { get }
    var bodyLargeSemiBold: LemonadeTextStyle { get }

    // Body Medium styles
    var bodyMediumRegular: LemonadeTextStyle { get }
    var bodyMediumMedium: LemonadeTextStyle { get }
    var bodyMediumSemiBold: LemonadeTextStyle { get }
    var bodyMediumBold: LemonadeTextStyle { get }

    // Body Small styles
    var bodySmallRegular: LemonadeTextStyle { get }
    var bodySmallMedium: LemonadeTextStyle { get }
    var bodySmallSemiBold: LemonadeTextStyle { get }

    // Body XSmall styles
    var bodyXSmallRegular: LemonadeTextStyle { get }
    var bodyXSmallMedium: LemonadeTextStyle { get }
    var bodyXSmallSemiBold: LemonadeTextStyle { get }
    var bodyXSmallOverline: LemonadeTextStyle { get }
}

/// Default implementation of LemonadeTypography following the Lemonade Design System specifications.
public struct LemonadeTypography: LemonadeTypographyProtocol {
    /// Shared instance to avoid repeated allocations
    public static let shared = LemonadeTypography()

    /// The font family used across the design system.
    public static let fontFamily = "Figtree"

    public init() {}

    // Display styles
    public let displayXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize600.value,
        lineHeight: LemonadeLineHeights.lineHeight800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let displaySmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize700.value,
        lineHeight: LemonadeLineHeights.lineHeight900.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let displayMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize900.value,
        lineHeight: LemonadeLineHeights.lineHeight1100.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let displayLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1200.value,
        lineHeight: LemonadeLineHeights.lineHeight1400.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let displayXLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1400.value,
        lineHeight: LemonadeLineHeights.lineHeight1600.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let display2XLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1600.value,
        lineHeight: LemonadeLineHeights.lineHeight1800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
    public let display3XLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1800.value,
        lineHeight: LemonadeLineHeights.lineHeight2000.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )

    // Heading styles
    public let headingXLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1000.value,
        lineHeight: LemonadeLineHeights.lineHeight1200.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let headingLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize800.value,
        lineHeight: LemonadeLineHeights.lineHeight1000.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let headingMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize700.value,
        lineHeight: LemonadeLineHeights.lineHeight900.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let headingSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize600.value,
        lineHeight: LemonadeLineHeights.lineHeight800.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let headingXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight650.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let headingXXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )

    // Body XLarge styles
    public let bodyXLargeRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.regular.value
    )
    public let bodyXLargeMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.medium.value
    )
    public let bodyXLargeSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )

    // Body Large styles
    public let bodyLargeRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.regular.value
    )
    public let bodyLargeMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.medium.value
    )
    public let bodyLargeSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )

    // Body Medium styles
    public let bodyMediumRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.regular.value
    )
    public let bodyMediumMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.medium.value
    )
    public let bodyMediumSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    /// Maps to `.semibold` weight because the Figtree font family does not include a true bold weight.
    public let bodyMediumBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )

    // Body Small styles
    public let bodySmallRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.regular.value
    )
    public let bodySmallMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.medium.value
    )
    public let bodySmallSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )

    // Body XSmall styles
    public let bodyXSmallRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.regular.value
    )
    public let bodyXSmallMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.medium.value
    )
    public let bodyXSmallSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.semibold.value
    )
    public let bodyXSmallOverline = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: 1.5
    )
}

// MARK: - Environment Key

private struct LemonadeTypographyKey: EnvironmentKey {
    // Reuses the shared instance rather than building a second full set of text styles.
    static let defaultValue: LemonadeTypographyProtocol = LemonadeTypography.shared
}

extension EnvironmentValues {
    public var lemonadeTypography: LemonadeTypographyProtocol {
        get { self[LemonadeTypographyKey.self] }
        set { self[LemonadeTypographyKey.self] = newValue }
    }
}
