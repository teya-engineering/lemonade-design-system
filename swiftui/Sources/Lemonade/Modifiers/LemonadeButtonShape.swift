import SwiftUI

// MARK: - Environment

private struct LemonadeButtonFullShapeKey: EnvironmentKey {
    static let defaultValue: Bool = false
}

extension EnvironmentValues {
    var lemonadeButtonFullShape: Bool {
        get { self[LemonadeButtonFullShapeKey.self] }
        set { self[LemonadeButtonFullShapeKey.self] = newValue }
    }
}

// MARK: - View Modifier

public extension View {
    /// Renders `LemonadeUi.Button` views in this hierarchy with a pill (full
    /// corner radius) shape. Has no effect on other views.
    ///
    /// Pass `false` to opt back into the per-size default radius after the
    /// modifier has been applied higher up the hierarchy.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Button(label: "Pill", onClick: { })
    ///     .fullShape()
    /// ```
    func fullShape(_ enabled: Bool = true) -> some View {
        environment(\.lemonadeButtonFullShape, enabled)
    }
}
