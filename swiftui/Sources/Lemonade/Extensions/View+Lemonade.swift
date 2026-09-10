import SwiftUI

// MARK: - Conditional View Modifier

extension View {
    /// Applies the given transform only when the condition is `true`.
    ///
    /// ```swift
    /// myView
    ///     .applyIf(stretched) { $0.frame(maxWidth: .infinity) }
    /// ```
    @ViewBuilder
    func applyIf<T: View>(_ condition: Bool, transform: (Self) -> T) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}

// MARK: - Selection Haptic

extension View {
    /// Plays the selection haptic whenever `trigger` changes.
    ///
    /// Uses a heavy `impact` rather than `.selection`: the plain selection tick reads too
    /// faintly for a selection commit. Firing on the value change rather than on tap means a
    /// programmatic selection change buzzes too, and a tap that does not change the selection
    /// stays silent.
    ///
    /// Shared by every component that presents a selectable surface, so the feel of "this was
    /// selected" is defined in one place.
    ///
    /// Relies on the iOS 17+ `sensoryFeedback` API, with a no-op fallback on older versions.
    @ViewBuilder
    func selectionImpactFeedback(trigger: Bool) -> some View {
        if #available(iOS 17.0, macOS 14.0, tvOS 17.0, watchOS 10.0, *) {
            sensoryFeedback(.impact(weight: .heavy), trigger: trigger)
        } else {
            self
        }
    }
}

// MARK: - Minimum Tappable Frame

extension View {
    /// Grows the view's layout frame to at least `minSize` on each side, centered on the
    /// content. Use on icon-only button labels whose rendered size is smaller than the
    /// platform's minimum touch target, so the enclosing `Button`'s tappable area grows with it.
    ///
    /// ```swift
    /// Button(action: onClick) {
    ///     LemonadeUi.Icon(icon: .times, contentDescription: "Close")
    /// }
    /// .clickableFrame(minSize: .size800)
    /// ```
    func clickableFrame(minSize: LemonadeSizes) -> some View {
        frame(minWidth: minSize.value, minHeight: minSize.value)
            .contentShape(Rectangle())
    }
}
