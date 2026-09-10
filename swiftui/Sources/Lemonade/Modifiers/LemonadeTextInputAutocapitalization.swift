import SwiftUI

#if canImport(UIKit)
import UIKit

// MARK: - Environment

private struct LemonadeAutocapitalizationTypeKey: EnvironmentKey {
    static let defaultValue: UITextAutocapitalizationType = .sentences
}

extension EnvironmentValues {
    var lemonadeAutocapitalizationType: UITextAutocapitalizationType {
        get { self[LemonadeAutocapitalizationTypeKey.self] }
        set { self[LemonadeAutocapitalizationTypeKey.self] = newValue }
    }
}

// MARK: - View Modifier

public extension View {
    /// Sets the autocapitalization behaviour for the Lemonade text fields in this
    /// hierarchy. Applies to every variant — `LemonadeUi.TextField`,
    /// `TextFieldWithSelector`, and their `LemonadeTextFieldValue`-based forms —
    /// including the plain `String`-binding overloads.
    ///
    /// Backed by the platform: on iOS it maps to
    /// `UITextField.autocapitalizationType`. Defaults to `.sentences` (UIKit's
    /// default) when unset, so an un-modified field is unchanged. Pass `.none` for
    /// case-sensitive input such as emails or usernames. Has no effect on views
    /// other than Lemonade text fields.
    ///
    /// Availability: this modifier (and `UITextAutocapitalizationType`) only
    /// exists where UIKit does — iOS, iPadOS, and Mac Catalyst. It is not compiled
    /// for native macOS (AppKit), so guard cross-platform call sites with
    /// `#if canImport(UIKit)`.
    ///
    /// - Note: This is deliberately *not* named `textInputAutocapitalization(_:)`.
    ///   SwiftUI ships a `View.textInputAutocapitalization(_:)` that configures
    ///   only its own native fields and has no effect on the UIKit-backed Lemonade
    ///   field. Use this Lemonade-namespaced modifier instead.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(
    ///     input: $email,
    ///     label: "Email",
    ///     placeholderText: "you@example.com"
    /// )
    /// .lemonadeTextInputAutocapitalization(.none)
    /// ```
    ///
    /// - Parameter type: The `UITextAutocapitalizationType` the field should use.
    /// - Returns: A view whose Lemonade text fields use the given
    ///   autocapitalization.
    func lemonadeTextInputAutocapitalization(_ type: UITextAutocapitalizationType) -> some View {
        environment(\.lemonadeAutocapitalizationType, type)
    }
}
#endif
