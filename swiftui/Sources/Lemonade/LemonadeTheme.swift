import SwiftUI

/// Lemonade Design System Theme
/// Provides a centralized access point for all design system tokens.
///
/// Colors automatically adapt to light/dark mode via asset catalog named colors.
///
/// Usage:
/// ```swift
/// // Access colors through the theme
/// Text("Hello")
///     .foregroundStyle(LemonadeTheme.colors.content.contentPrimary)
///
/// // Or use shorthand extensions
/// Text("Hello")
///     .foregroundStyle(.content.contentPrimary)
///     .background(.bg.bgDefault)
///
/// // Access spacing
/// VStack(spacing: LemonadeTheme.spaces.spacing100) {
///     // content
/// }
/// ```

/// Main theme entry point for Lemonade Design System
public enum LemonadeTheme {
    /// Semantic color tokens - automatically adapts to light/dark mode
    /// via asset catalog named colors.
    public static let colors: LemonadeSemanticColors = LemonadeAdaptiveTheme()

    /// Generic, theme-aware hues for colour that carries application meaning the
    /// design system does not model — chart series, categories, per-role accents.
    ///
    /// Prefer ``colors`` whenever a semantic token fits. Light and dark are
    /// resolved by the asset catalog, so no manual switching is needed.
    ///
    /// > Experimental: the shape of this layer is still settling and may change
    /// > without a deprecation cycle. The KMP side gates this behind
    /// > `@OptIn(ExperimentalLemonadeApi::class)`; Swift has no equivalent, so
    /// > treat it as opt-in by convention and expect to revisit call sites.
    public static let themed: LemonadeThemedColors = LemonadeAdaptiveThemedColors()

    /// Spacing tokens
    public static let spaces: LemonadeSpaceValues = LemonadeSpaceValuesImpl()

    /// Radius tokens
    public static let radius: LemonadeRadiusValues = LemonadeRadiusValuesImpl()

    /// Shape tokens (rounded rectangles with predefined radii)
    public static let shapes: LemonadeShapes = LemonadeShapesImpl()

    /// Size tokens
    public static let sizes: LemonadeSizeValues = LemonadeSizeValuesImpl()

    /// Opacity tokens
    public static let opacity: LemonadeOpacity = LemonadeOpacityTokens()

    /// Border width tokens
    public static let borderWidth: LemonadeBorderWidth = LemonadeBorderWidthTokens()
}
