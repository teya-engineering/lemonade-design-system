import SwiftUI

// MARK: - CGFloat Namespace Extensions

/// Enables shorthand access for spacing, sizes, radius, and border width tokens
/// Usage:
/// ```swift
/// VStack(spacing: .space.spacing200) { ... }
/// .frame(width: .size.size600)
/// .clipShape(.rect(cornerRadius: .radius.radius300))
/// .overlay(RoundedRectangle(cornerRadius: 8).stroke(lineWidth: .borderWidth.border50))
/// ```

// MARK: - Spacing Shorthand

public struct LemonadeSpacingShorthand {
    public var spacing0: CGFloat { LemonadeSpacing.spacing0.value }
    public var spacing50: CGFloat { LemonadeSpacing.spacing50.value }
    public var spacing100: CGFloat { LemonadeSpacing.spacing100.value }
    public var spacing200: CGFloat { LemonadeSpacing.spacing200.value }
    public var spacing300: CGFloat { LemonadeSpacing.spacing300.value }
    public var spacing400: CGFloat { LemonadeSpacing.spacing400.value }
    public var spacing500: CGFloat { LemonadeSpacing.spacing500.value }
    public var spacing600: CGFloat { LemonadeSpacing.spacing600.value }
    public var spacing800: CGFloat { LemonadeSpacing.spacing800.value }
    public var spacing1000: CGFloat { LemonadeSpacing.spacing1000.value }
    public var spacing1200: CGFloat { LemonadeSpacing.spacing1200.value }
    public var spacing1400: CGFloat { LemonadeSpacing.spacing1400.value }
    public var spacing1600: CGFloat { LemonadeSpacing.spacing1600.value }
    public var spacing1800: CGFloat { LemonadeSpacing.spacing1800.value }
    public var spacing2000: CGFloat { LemonadeSpacing.spacing2000.value }
}

// MARK: - Sizes Shorthand

public struct LemonadeSizesShorthand {
    public var size0: CGFloat { LemonadeSizes.size0.value }
    public var size50: CGFloat { LemonadeSizes.size50.value }
    public var size100: CGFloat { LemonadeSizes.size100.value }
    public var size150: CGFloat { LemonadeSizes.size150.value }
    public var size200: CGFloat { LemonadeSizes.size200.value }
    public var size250: CGFloat { LemonadeSizes.size250.value }
    public var size300: CGFloat { LemonadeSizes.size300.value }
    public var size350: CGFloat { LemonadeSizes.size350.value }
    public var size400: CGFloat { LemonadeSizes.size400.value }
    public var size450: CGFloat { LemonadeSizes.size450.value }
    public var size500: CGFloat { LemonadeSizes.size500.value }
    public var size550: CGFloat { LemonadeSizes.size550.value }
    public var size600: CGFloat { LemonadeSizes.size600.value }
    public var size700: CGFloat { LemonadeSizes.size700.value }
    public var size750: CGFloat { LemonadeSizes.size750.value }
    public var size800: CGFloat { LemonadeSizes.size800.value }
    public var size900: CGFloat { LemonadeSizes.size900.value }
    public var size1000: CGFloat { LemonadeSizes.size1000.value }
    public var size1100: CGFloat { LemonadeSizes.size1100.value }
    public var size1200: CGFloat { LemonadeSizes.size1200.value }
    public var size1400: CGFloat { LemonadeSizes.size1400.value }
    public var size1600: CGFloat { LemonadeSizes.size1600.value }
}

// MARK: - Radius Shorthand

public struct LemonadeRadiusShorthand {
    public var radius0: CGFloat { LemonadeRadius.radius0.value }
    public var radius50: CGFloat { LemonadeRadius.radius50.value }
    public var radius100: CGFloat { LemonadeRadius.radius100.value }
    public var radius150: CGFloat { LemonadeRadius.radius150.value }
    public var radius200: CGFloat { LemonadeRadius.radius200.value }
    public var radius300: CGFloat { LemonadeRadius.radius300.value }
    public var radius400: CGFloat { LemonadeRadius.radius400.value }
    public var radius500: CGFloat { LemonadeRadius.radius500.value }
    public var radius600: CGFloat { LemonadeRadius.radius600.value }
    public var radius800: CGFloat { LemonadeRadius.radius800.value }
    public var radiusFull: CGFloat { LemonadeRadius.radiusFull.value }
}

// MARK: - Border Width Shorthand

public struct LemonadeBorderWidthShorthand {
    private let tokens = LemonadeBorderWidthTokens()

    public var border0: CGFloat { tokens.base.border0 }
    public var border25: CGFloat { tokens.base.border25 }
    public var border40: CGFloat { tokens.base.border40 }
    public var border50: CGFloat { tokens.base.border50 }
    public var border75: CGFloat { tokens.base.border75 }
    public var border100: CGFloat { tokens.base.border100 }

    public var focusRing: CGFloat { tokens.state.focusRing }
    public var borderSelected: CGFloat { tokens.state.borderSelected }
}

// MARK: - CGFloat Extensions

public extension CGFloat {
    /// Spacing tokens
    /// Usage: `VStack(spacing: .space.spacing200)`
    static var space: LemonadeSpacingShorthand { LemonadeSpacingShorthand() }

    /// Size tokens
    /// Usage: `.frame(width: .size.size600)`
    static var size: LemonadeSizesShorthand { LemonadeSizesShorthand() }

    /// Radius tokens
    /// Usage: `.clipShape(.rect(cornerRadius: .radius.radius300))`
    static var radius: LemonadeRadiusShorthand { LemonadeRadiusShorthand() }

    /// Border width tokens
    /// Usage: `.stroke(lineWidth: .borderWidth.border50)`
    static var borderWidth: LemonadeBorderWidthShorthand { LemonadeBorderWidthShorthand() }
}
