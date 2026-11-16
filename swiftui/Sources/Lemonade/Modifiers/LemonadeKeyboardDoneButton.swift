import SwiftUI

#if canImport(UIKit)

// MARK: - Environment

private struct LemonadeKeyboardDoneButtonKey: EnvironmentKey {
    static let defaultValue = true
}

extension EnvironmentValues {
    var lemonadeKeyboardDoneButton: Bool {
        get { self[LemonadeKeyboardDoneButtonKey.self] }
        set { self[LemonadeKeyboardDoneButtonKey.self] = newValue }
    }
}

// MARK: - View Modifier

public extension View {
    /// Shows or hides the "Done" bar that ``LemonadeUi/TextArea`` puts above the keyboard.
    ///
    /// The return key inserts a new line in a text area, so the bar is the only way to close the
    /// keyboard from the keyboard itself. It is shown by default; hide it only when the screen
    /// offers its own way to dismiss, such as its own keyboard toolbar.
    ///
    /// Availability: iOS, iPadOS, and Mac Catalyst only. Guard cross-platform call sites with
    /// `#if canImport(UIKit)`.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextArea(
    ///     input: $notes,
    ///     label: "Notes"
    /// )
    /// .lemonadeKeyboardDoneButton(false)
    /// ```
    ///
    /// - Parameter isShown: `false` hides the bar.
    /// - Returns: A view whose Lemonade text areas show or hide the bar.
    func lemonadeKeyboardDoneButton(_ isShown: Bool) -> some View {
        environment(\.lemonadeKeyboardDoneButton, isShown)
    }
}
#endif
