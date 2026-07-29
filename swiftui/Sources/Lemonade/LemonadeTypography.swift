import SwiftUI

#if canImport(UIKit)
import UIKit
#endif

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
    /// The Apple text style whose Dynamic Type curve this style follows.
    ///
    /// Apple ships one growth curve per text style, and they are not the same shape: between the
    /// default size and `AX5`, `.body` grows about 3.1x while `.largeTitle` grows about 1.8x. Text
    /// that already starts big is meant to grow less, because it is legible to begin with.
    ///
    /// Every style used to resolve through `.body`, which handed a 72pt display style the growth
    /// curve of 17pt body copy. Each style now names the Apple style whose curve suits its size,
    /// so the platform does the scaling it was designed to do.
    ///
    /// Defaults to `.body`, which keeps the previous behaviour for styles built outside the
    /// design system.
    public let relativeTextStyle: Font.TextStyle

    /// The line spacing needed to achieve the desired line height.
    ///
    /// Resolved once here rather than on every read. This is read from inside `body` by every
    /// ``LemonadeUi/Text(_:textStyle:textAlign:color:maxLines:)``, the most instantiated component
    /// in the library, and asking a `UIFont` for its `lineHeight` is a CoreText lookup roughly
    /// three orders of magnitude more expensive than a stored-property load. Every input is a
    /// stored `let`, so the answer cannot change for a given style.
    ///
    /// This is the gap at the unscaled font size. Use ``lineSpacing(for:)`` to hold the design's
    /// line-height ratio once Dynamic Type has grown the glyphs.
    public let lineSpacing: CGFloat

    /// Fallback line height ratio for Figtree font (used on platforms without UIKit).
    /// Calculated from font metrics: (ascender - descender + lineGap) / unitsPerEm
    /// Figtree: (950 - (-250) + 0) / 1000 = 1.20
    private static let fallbackLineHeightRatio: CGFloat = 1.20

    public init(
        fontSize: CGFloat,
        lineHeight: CGFloat,
        fontWeight: Font.Weight,
        letterSpacing: CGFloat? = nil,
        relativeTextStyle: Font.TextStyle = .body
    ) {
        self.fontSize = fontSize
        self.lineHeight = lineHeight
        self.fontWeight = fontWeight
        self.letterSpacing = letterSpacing
        self.relativeTextStyle = relativeTextStyle

        let resolvedName = Self.fontName(for: fontWeight)
        self.fontName = resolvedName
        self.font = .custom(resolvedName, size: fontSize, relativeTo: relativeTextStyle)
        self.weightedFont = .custom(
            LemonadeTypography.fontFamily,
            size: fontSize,
            relativeTo: relativeTextStyle
        ).weight(fontWeight)

#if canImport(UIKit)
        let naturalLineHeight = Self.resolvedUIFont(
            name: resolvedName,
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
        switch fontWeight {
        case .regular:
            return "Figtree-Regular"
        case .medium:
            return "Figtree-Medium"
        case .semibold, .bold:
            return "Figtree-SemiBold"
        default:
            return "Figtree-Regular"
        }
    }

    /// The font name based on the weight
    public let fontName: String

    /// Returns a SwiftUI Font based on this text style.
    ///
    /// Stored for the same reason as ``lineSpacing``: it is read from inside `body`, and
    /// `Font.custom(_:size:relativeTo:)` boxes a new font provider on every call.
    public let font: Font

    /// The face asked for by the family name plus a weight modifier, which is how the string
    /// overloads of ``LemonadeUi/Text(_:)`` resolve their font. Stored for the same reason as
    /// ``font``.
    let weightedFont: Font

#if canImport(UIKit)
    /// The single place a text style turns into a concrete face.
    ///
    /// `registerFonts()` is idempotent and cheap after the first call. Calling it here means the
    /// `.systemFont` fallback is only ever reached if a face is genuinely missing from the bundle,
    /// rather than because a consumer had not registered yet.
    private static func resolvedUIFont(name: String, size: CGFloat) -> UIFont {
        LemonadeFonts.registerFonts()
        return UIFont(name: name, size: size) ?? .systemFont(ofSize: size)
    }

    /// Returns a UIFont based on this text style
    public var uiFont: UIFont {
        Self.resolvedUIFont(name: fontName, size: fontSize)
    }
#endif

    /// Returns the line spacing needed to hold this style's `lineHeight` ratio once Dynamic Type has
    /// scaled the font.
    ///
    /// ``lineSpacing`` is a fixed number of points derived from the unscaled font. SwiftUI's
    /// `.lineSpacing` adds that gap verbatim, so as the glyphs grow the gap stays put and lines
    /// close in on each other. At `AX5` a `bodyMediumRegular` paragraph renders at a 1.31 line-height
    /// ratio instead of the 1.50 the design asks for.
    ///
    /// Font metrics scale linearly with point size, so the whole correction is the growth factor of
    /// the font itself:
    ///
    /// ```
    /// spacing(scaled) = scaledFontSize / fontSize * (lineHeight - naturalLineHeight)
    ///                 = growth * lineSpacing
    /// ```
    ///
    /// At the default content size the growth factor is 1 and this returns ``lineSpacing`` unchanged.
    public func lineSpacing(for dynamicTypeSize: DynamicTypeSize) -> CGFloat {
#if canImport(UIKit)
        let metrics = UIFontMetrics(forTextStyle: relativeTextStyle.uiKitTextStyle)
        let traits = UITraitCollection(
            preferredContentSizeCategory: dynamicTypeSize.uiKitContentSizeCategory
        )
        let scaledFontSize = metrics.scaledValue(for: fontSize, compatibleWith: traits)
        guard fontSize > 0 else {
            return lineSpacing
        }
        return lineSpacing * (scaledFontSize / fontSize)
#else
        // No Dynamic Type off UIKit, so the unscaled gap is already correct.
        return lineSpacing
#endif
    }
}

#if canImport(UIKit)
extension Font.TextStyle {
    /// SwiftUI's `Font.TextStyle` and UIKit's `UIFont.TextStyle` name the same Dynamic Type curves
    /// but do not bridge, and `UIFontMetrics` only takes the UIKit one.
    internal var uiKitTextStyle: UIFont.TextStyle {
        switch self {
        case .largeTitle: return .largeTitle
        case .title: return .title1
        case .title2: return .title2
        case .title3: return .title3
        case .headline: return .headline
        case .subheadline: return .subheadline
        case .body: return .body
        case .callout: return .callout
        case .footnote: return .footnote
        case .caption: return .caption1
        case .caption2: return .caption2
        @unknown default: return .body
        }
    }
}

extension DynamicTypeSize {
    /// `UIFontMetrics` measures against a trait collection, which wants the UIKit category.
    internal var uiKitContentSizeCategory: UIContentSizeCategory {
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
#endif

/// Protocol defining all available text styles in the Lemonade Design System.
public protocol LemonadeTypographyProtocol {
    var displayXSmall: LemonadeTextStyle { get }
    var displaySmall: LemonadeTextStyle { get }
    var displayMedium: LemonadeTextStyle { get }
    var displayLarge: LemonadeTextStyle { get }
    var displayXLarge: LemonadeTextStyle { get }
    var display2XLarge: LemonadeTextStyle { get }
    var display3XLarge: LemonadeTextStyle { get }

    var headingXLarge: LemonadeTextStyle { get }
    var headingLarge: LemonadeTextStyle { get }
    var headingMedium: LemonadeTextStyle { get }
    var headingSmall: LemonadeTextStyle { get }
    var headingXSmall: LemonadeTextStyle { get }
    var headingXXSmall: LemonadeTextStyle { get }

    var bodyXLargeRegular: LemonadeTextStyle { get }
    var bodyXLargeMedium: LemonadeTextStyle { get }
    var bodyXLargeSemiBold: LemonadeTextStyle { get }

    var bodyLargeRegular: LemonadeTextStyle { get }
    var bodyLargeMedium: LemonadeTextStyle { get }
    var bodyLargeSemiBold: LemonadeTextStyle { get }

    var bodyMediumRegular: LemonadeTextStyle { get }
    var bodyMediumMedium: LemonadeTextStyle { get }
    var bodyMediumSemiBold: LemonadeTextStyle { get }
    @available(*, deprecated, renamed: "bodyMediumSemiBold", message: "Bold is not a public Lemonade text style.")
    var bodyMediumBold: LemonadeTextStyle { get }

    var bodySmallRegular: LemonadeTextStyle { get }
    var bodySmallMedium: LemonadeTextStyle { get }
    var bodySmallSemiBold: LemonadeTextStyle { get }

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
    //
    // Display text is set between 24pt and 72pt, so it is already legible before any scaling.
    // `.largeTitle` is Apple's slowest curve for exactly this case: it grows enough to respect
    // the user's setting without pushing the rest of the screen out of the way.
    public let displayXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize600.value,
        lineHeight: LemonadeLineHeights.lineHeight800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let displaySmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize700.value,
        lineHeight: LemonadeLineHeights.lineHeight900.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let displayMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize900.value,
        lineHeight: LemonadeLineHeights.lineHeight1100.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let displayLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1200.value,
        lineHeight: LemonadeLineHeights.lineHeight1400.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let displayXLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1400.value,
        lineHeight: LemonadeLineHeights.lineHeight1600.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let display2XLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1600.value,
        lineHeight: LemonadeLineHeights.lineHeight1800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )
    public let display3XLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1800.value,
        lineHeight: LemonadeLineHeights.lineHeight2000.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25,
        relativeTextStyle: .largeTitle
    )

    // Heading styles
    //
    // Headings run from 40pt down to 16pt, so they cross the whole range of Apple's title curves.
    // Each one takes the curve of the Apple style closest to its own size. The two smallest are
    // body-sized semibold text, which is what `.headline` is.
    public let headingXLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize1000.value,
        lineHeight: LemonadeLineHeights.lineHeight1200.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .title
    )
    public let headingLarge = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize800.value,
        lineHeight: LemonadeLineHeights.lineHeight1000.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .title2
    )
    public let headingMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize700.value,
        lineHeight: LemonadeLineHeights.lineHeight900.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .title2
    )
    public let headingSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize600.value,
        lineHeight: LemonadeLineHeights.lineHeight800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .title3
    )
    public let headingXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight650.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .headline
    )
    public let headingXXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .headline
    )

    // Body XLarge styles
    //
    // 20pt down to 16pt is reading text, which is what `.body` is for.
    public let bodyXLargeRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.regular.value,
        relativeTextStyle: .body
    )
    public let bodyXLargeMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.medium.value,
        relativeTextStyle: .body
    )
    public let bodyXLargeSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize500.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .body
    )

    public let bodyLargeRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.regular.value,
        relativeTextStyle: .body
    )
    public let bodyLargeMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.medium.value,
        relativeTextStyle: .body
    )
    public let bodyLargeSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize450.value,
        lineHeight: LemonadeLineHeights.lineHeight700.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .body
    )

    public let bodyMediumRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.regular.value,
        relativeTextStyle: .body
    )
    public let bodyMediumMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.medium.value,
        relativeTextStyle: .body
    )
    public let bodyMediumSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .body
    )
    @available(*, deprecated, renamed: "bodyMediumSemiBold", message: "Bold is not a public Lemonade text style.")
    public let bodyMediumBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize400.value,
        lineHeight: LemonadeLineHeights.lineHeight600.value,
        fontWeight: LemonadeFontWeights.bold.value,
        relativeTextStyle: .body
    )

    // Body Small styles
    //
    // 14pt is Apple's `.footnote` size, and 12pt its `.caption` size. Both curves are steeper
    // than `.body`, so this tier grows slightly more than it does today. That is deliberate:
    // small text is the text that most needs the room at accessibility sizes.
    public let bodySmallRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.regular.value,
        relativeTextStyle: .footnote
    )
    public let bodySmallMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.medium.value,
        relativeTextStyle: .footnote
    )
    public let bodySmallSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize350.value,
        lineHeight: LemonadeLineHeights.lineHeight500.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .footnote
    )

    public let bodyXSmallRegular = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.regular.value,
        relativeTextStyle: .caption
    )
    public let bodyXSmallMedium = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.medium.value,
        relativeTextStyle: .caption
    )
    public let bodyXSmallSemiBold = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        relativeTextStyle: .caption
    )
    public let bodyXSmallOverline = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize300.value,
        lineHeight: LemonadeLineHeights.lineHeight400.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: 1.5,
        relativeTextStyle: .caption
    )
}

// MARK: - Environment Key

private struct LemonadeTypographyKey: EnvironmentKey {
    static let defaultValue: LemonadeTypographyProtocol = LemonadeTypography.shared
}

extension EnvironmentValues {
    public var lemonadeTypography: LemonadeTypographyProtocol {
        get { self[LemonadeTypographyKey.self] }
        set { self[LemonadeTypographyKey.self] = newValue }
    }
}
