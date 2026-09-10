import SwiftUI

#if canImport(UIKit)
import UIKit

// MARK: - Environment

private struct LemonadeAutocorrectionTypeKey: EnvironmentKey {
    static let defaultValue: UITextAutocorrectionType = .default
}

extension EnvironmentValues {
    var lemonadeAutocorrectionType: UITextAutocorrectionType {
        get { self[LemonadeAutocorrectionTypeKey.self] }
        set { self[LemonadeAutocorrectionTypeKey.self] = newValue }
    }
}

// MARK: - View Modifier

public extension View {
    /// Enables or disables autocorrection for the Lemonade text fields in this
    /// hierarchy. Applies to every variant — `LemonadeUi.TextField`,
    /// `TextFieldWithSelector`, and their `LemonadeTextFieldValue`-based forms —
    /// including the plain `String`-binding overloads.
    ///
    /// Backed by the platform: on iOS it maps to
    /// `UITextField.autocorrectionType` (`.no` when disabled, `.default`
    /// otherwise). Disable it for input that must not be "corrected", such as
    /// emails, usernames, or codes. Has no effect on views other than Lemonade
    /// text fields.
    ///
    /// Availability: this modifier only exists where UIKit does — iOS, iPadOS,
    /// and Mac Catalyst. It is not compiled for native macOS (AppKit), so guard
    /// cross-platform call sites with `#if canImport(UIKit)`.
    ///
    /// - Note: This is deliberately *not* named `autocorrectionDisabled(_:)`.
    ///   SwiftUI ships a `View.autocorrectionDisabled(_:)` that configures only
    ///   its own native fields and has no effect on the UIKit-backed Lemonade
    ///   field. Use this Lemonade-namespaced modifier instead.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(
    ///     input: $email,
    ///     label: "Email",
    ///     placeholderText: "you@example.com"
    /// )
    /// .lemonadeAutocorrectionDisabled()
    /// ```
    ///
    /// - Parameter disabled: Whether autocorrection should be turned off. Defaults
    ///   to `true`, matching SwiftUI's `autocorrectionDisabled(_:)` ergonomics.
    /// - Returns: A view whose Lemonade text fields use the given autocorrection
    ///   setting.
    func lemonadeAutocorrectionDisabled(_ disabled: Bool = true) -> some View {
        environment(\.lemonadeAutocorrectionType, disabled ? .no : .default)
    }
}
#endif
