import SwiftUI

// MARK: - Double Namespace Extensions

/// Enables shorthand access for opacity tokens
/// Usage:
/// ```swift
/// .opacity(.opacity.opacity50)
/// .opacity(.opacity.opacityDisabled)
/// ```

// MARK: - Opacity Shorthand

public struct LemonadeOpacityShorthand {
    private let tokens = LemonadeOpacityTokens()

    public var opacity0: Double { tokens.base.opacity0 }
    public var opacity5: Double { tokens.base.opacity5 }
    public var opacity10: Double { tokens.base.opacity10 }
    public var opacity20: Double { tokens.base.opacity20 }
    public var opacity30: Double { tokens.base.opacity30 }
    public var opacity40: Double { tokens.base.opacity40 }
    public var opacity50: Double { tokens.base.opacity50 }
    public var opacity60: Double { tokens.base.opacity60 }
    public var opacity70: Double { tokens.base.opacity70 }
    public var opacity80: Double { tokens.base.opacity80 }
    public var opacity90: Double { tokens.base.opacity90 }
    public var opacity100: Double { tokens.base.opacity100 }

    public var opacityPressed: Double { tokens.state.opacityPressed }
    public var opacityDisabled: Double { tokens.state.opacityDisabled }
}

// MARK: - Double Extensions

public extension Double {
    /// Opacity tokens
    /// Usage: `.opacity(.opacity.opacity50)`
    static var opacity: LemonadeOpacityShorthand { LemonadeOpacityShorthand() }
}
