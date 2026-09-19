import SwiftUI

// MARK: - Environment

private struct LemonadeTextFieldFocusKey: EnvironmentKey {
    static let defaultValue: Binding<Bool>? = nil
}

extension EnvironmentValues {
    var lemonadeTextFieldFocus: Binding<Bool>? {
        get { self[LemonadeTextFieldFocusKey.self] }
        set { self[LemonadeTextFieldFocusKey.self] = newValue }
    }
}

// MARK: - View Modifier

public extension View {
    /// Binds the focus of the Lemonade text field in this hierarchy to `isFocused`.
    ///
    /// The field writes `true` when it gains focus and `false` when it loses it. Setting the
    /// binding moves focus: `true` raises the keyboard, `false` dismisses it.
    ///
    /// ## Usage
    /// ```swift
    /// @State private var isFocused = false
    ///
    /// LemonadeUi.TextField(value: $value, label: "Name")
    ///     .lemonadeFocused($isFocused)
    /// ```
    ///
    /// - Parameter isFocused: The field's focus.
    /// - Returns: A view whose Lemonade text field follows and reports `isFocused`.
    func lemonadeFocused(_ isFocused: Binding<Bool>) -> some View {
        environment(\.lemonadeTextFieldFocus, isFocused)
    }
}
