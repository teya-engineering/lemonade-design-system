import SwiftUI

// MARK: - View Extension

public extension View {
    /// Adds a toast container to this view hierarchy.
    ///
    /// Apply this modifier to your root view to enable toast notifications.
    ///
    /// ## Usage
    /// ```swift
    /// @main
    /// struct MyApp: App {
    ///     var body: some Scene {
    ///         WindowGroup {
    ///             ContentView()
    ///                 .lemonadeToastContainer()
    ///         }
    ///     }
    /// }
    /// ```
    public func lemonadeToastContainer() -> some View {
        LemonadeToastContainerView(content: self)
    }
}
