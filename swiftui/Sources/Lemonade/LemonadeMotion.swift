import SwiftUI
#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

/// Animations for changes a component can't animate on its own, because SwiftUI only animates a
/// layout change that the state change asks for.
public enum LemonadeMotion {
    /// Grows or shrinks a `LemonadeUi.Card` when its content changes size. `nil` when the system's
    /// reduce motion setting is on, so the change is instant.
    ///
    /// ## Usage
    /// ```swift
    /// withAnimation(LemonadeMotion.cardResize) {
    ///     isExpanded.toggle()
    /// }
    /// ```
    @MainActor
    public static var cardResize: Animation? {
        prefersReducedMotion ? nil : .easeInOut(duration: 0.2)
    }

    @MainActor
    private static var prefersReducedMotion: Bool {
        #if canImport(UIKit)
        UIAccessibility.isReduceMotionEnabled
        #elseif canImport(AppKit)
        NSWorkspace.shared.accessibilityDisplayShouldReduceMotion
        #else
        false
        #endif
    }
}
