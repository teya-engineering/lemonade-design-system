import SwiftUI

// MARK: - Icon Scaling Environment

private struct LemonadeIconScalingKey: EnvironmentKey {
    static let defaultValue: Bool = false
}

extension EnvironmentValues {
    /// Whether ``LemonadeUi/Icon`` scales with the reader's Dynamic Type setting in this subtree.
    ///
    /// It is off by default, and a component turns it on for the icons it pairs with a label of
    /// its own. Scaling every icon in the library instead would push a larger icon into every
    /// container drawn at a fixed size, which clips it — the inline calendar loses the bottom
    /// half of its day numbers that way. Opt a component in once its container can carry the
    /// larger icon.
    var lemonadeIconScaling: Bool {
        get { self[LemonadeIconScalingKey.self] }
        set { self[LemonadeIconScalingKey.self] = newValue }
    }
}

// MARK: - Dynamic Type Scaling

/// The factors that put a fixed design token in proportion with the reader's text size.
///
/// The geometry tokens — heights, paddings, icon sizes, radii — are authored in points against
/// the default text size. The fonts already scale: `LemonadeTextStyle` resolves to
/// `Font.custom(_:size:)`, which `UIFontMetrics` scales relative to the `body` text style. The
/// tokens do not. Left unscaled, the label outgrows the fixed height of its container, and a
/// fixed-size icon shrinks against the text it sits beside.
///
/// A single `@ScaledMetric` seeded with `1` returns the ratio `UIFontMetrics` applies to `body`,
/// so one metric per view scales every token in it.
///
/// ```swift
/// @LemonadeScale private var scale: LemonadeScaleFactors
/// ...
/// .frame(width: size.value * scale.content)
/// .padding(.horizontal, spacing * scale.container)
/// ```
///
/// `LemonadeScale` is a `DynamicProperty`. SwiftUI keeps it up to date only while a `View` holds
/// it as a stored property. A copy held anywhere else reports the default text size.
@propertyWrapper
struct LemonadeScale: DynamicProperty {
    @ScaledMetric(relativeTo: .body) private var unit: CGFloat = 1

    var wrappedValue: LemonadeScaleFactors {
        LemonadeScaleFactors(unit: unit)
    }

    init() {}
}

/// The two factors ``LemonadeScale`` reports. They differ above the largest non-accessibility
/// text size. See ``container`` for the reason.
struct LemonadeScaleFactors {
    /// The ceiling on ``container``. It sits near the ratio `UIFontMetrics` reports for the
    /// first accessibility text size.
    ///
    /// Past that point the reader gains far more from the text than from the box around it, and
    /// a padding that keeps growing takes the width the label needs.
    private static let containerCeiling: CGFloat = 1.6

    private let unit: CGFloat

    init(unit: CGFloat) {
        self.unit = unit
    }

    /// The factor for content: icons, and the line metrics of the text they sit beside. It
    /// tracks the reader's text size with no ceiling, the way the glyphs do.
    var content: CGFloat { unit }

    /// The factor for the box around the content: heights, paddings, radii. It tracks the
    /// reader's text size from the default one up to ``containerCeiling``, and holds at both
    /// ends.
    ///
    /// A container is sized from the content it holds, so it keeps growing past the ceiling.
    /// What stops is the padding, which is a fixed inset that a growing label has to pay for
    /// twice, on both sides, out of a width the label does not control.
    ///
    /// Below the default text size the factor holds at `1`. The tokens are the floor the design
    /// draws for a touch target, and a reader who asks for smaller text is not asking for a
    /// smaller button.
    var container: CGFloat { min(max(unit, 1), Self.containerCeiling) }
}
