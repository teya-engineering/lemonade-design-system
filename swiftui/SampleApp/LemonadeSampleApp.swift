import SwiftUI
import Lemonade

@main
struct LemonadeSampleApp: App {
    @StateObject private var styleHandler = LemonadeStyleHandler()

    init() {
        // `registerFonts()` deliberately swallows failures, so never force-unwrap
        // `UIFont(name:size:)` afterwards - a resource-bundling regression would turn a
        // cosmetic problem into a launch crash.
        LemonadeFonts.registerFonts()

        Self.applyLargeTitleFont()
        Self.applyInlineTitleFont()
        Self.applySearchBarFont()
    }

    private static func applyLargeTitleFont() {
        UINavigationBar.appearance().largeTitleTextAttributes = [
            .font: figtreeSemibold(size: LemonadeTypography.shared.headingLarge.fontSize)
        ]
    }

    private static func applyInlineTitleFont() {
        UINavigationBar.appearance().titleTextAttributes = [
            .font: figtreeSemibold(size: LemonadeTypography.shared.headingXXSmall.fontSize)
        ]
    }

    private static func applySearchBarFont() {
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).font = UIFont(
            name: "Figtree",
            size: LemonadeTypography.shared.bodyMediumMedium.fontSize
        )
    }

    /// The Figtree semibold face, falling back to the system font at the same
    /// size and weight if the custom font failed to register.
    private static func figtreeSemibold(size: CGFloat) -> UIFont {
        if let font = UIFont(name: "Figtree-Semibold", size: size) {
            return font
        }
        return .systemFont(ofSize: size, weight: .semibold)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(styleHandler)
                .environment(\.font, Font.custom("Figtree", size: LemonadeTypography.shared.bodyMediumMedium.fontSize))
        }
    }
}

struct ContentView: View {
    @EnvironmentObject private var styleHandler: LemonadeStyleHandler

    var body: some View {
        NavigationStack {
            HomeView()
        }
        .lemonadeToastContainer()
        .lemonadeTooltipContainer()
        .onAppear { styleHandler.applyToWindows() }
    }
}
