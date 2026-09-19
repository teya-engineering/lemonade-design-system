import SwiftUI

#if canImport(UIKit)
import UIKit

// MARK: - Environment

private struct LemonadeReturnKeyTypeKey: EnvironmentKey {
    static let defaultValue: UIReturnKeyType = .default
}

private struct LemonadeSubmitActionKey: EnvironmentKey {
    static let defaultValue: (() -> Void)? = nil
}

extension EnvironmentValues {
    var lemonadeReturnKeyType: UIReturnKeyType {
        get { self[LemonadeReturnKeyTypeKey.self] }
        set { self[LemonadeReturnKeyTypeKey.self] = newValue }
    }

    var lemonadeSubmitAction: (() -> Void)? {
        get { self[LemonadeSubmitActionKey.self] }
        set { self[LemonadeSubmitActionKey.self] = newValue }
    }
}

// MARK: - View Modifiers

public extension View {
    /// Sets the return key of the Lemonade text fields in this hierarchy.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(value: $value, label: "Name")
    ///     .lemonadeReturnKeyType(.next)
    /// ```
    ///
    /// - Parameter type: The `UIReturnKeyType` the keyboard shows.
    /// - Returns: A view whose Lemonade text fields use the given return key.
    func lemonadeReturnKeyType(_ type: UIReturnKeyType) -> some View {
        environment(\.lemonadeReturnKeyType, type)
    }

    /// Runs `action` when the return key is pressed in the Lemonade text fields in this hierarchy.
    ///
    /// The field keeps its focus; move or drop it from `action`. An `onSubmit:` argument on a
    /// `String`-binding overload wins over this modifier.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(value: $value, label: "Name")
    ///     .lemonadeOnSubmit { focusNextField() }
    /// ```
    ///
    /// - Parameter action: Called on each press of the return key.
    /// - Returns: A view whose Lemonade text fields call `action` on return.
    func lemonadeOnSubmit(_ action: @escaping () -> Void) -> some View {
        environment(\.lemonadeSubmitAction, action)
    }
}
#endif
