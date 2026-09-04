import XCTest
#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif
@testable import Lemonade

final class LemonadeThemedColorTests: XCTestCase {
    private static let hues = [
        "yellow", "amber", "orange", "red", "rose", "pink", "fuchsia", "purple",
        "violet", "indigo", "blue", "cyan", "teal", "green", "green-lime", "yellow-lime",
        // Achromatic, and the one hue whose slots follow the mirror rule rather than
        // the chromatic step map — see LemonadeThemedColors for the values.
        "neutral",
    ]

    private static let slots = [
        "background", "background-subtle", "border", "border-subtle",
        "content", "content-on-color", "on-background",
    ]

    /// Looks an asset up through the platform colour type. `Color(_:bundle:)` succeeds
    /// even when the asset is missing, so it cannot detect a broken lookup.
    private func namedColor(_ name: String) -> Any? {
        #if canImport(UIKit)
        return UIColor(named: name, in: .lemonade, compatibleWith: nil)
        #elseif canImport(AppKit)
        return NSColor(named: name, bundle: .lemonade)
        #else
        return nil
        #endif
    }

    func testEveryThemedAssetResolvesInTheBundle() throws {
        // Bare SwiftPM does not run actool, so the .xcassets is not compiled and no
        // asset resolves — including pre-existing semantic ones. Use a semantic asset
        // as a canary: if it cannot resolve, the catalog is absent rather than the
        // themed names being wrong, so skip instead of reporting a false failure.
        // Under `xcodebuild test` the catalog is compiled and every assertion runs.
        try XCTSkipIf(
            namedColor("lemonade-background-bg-default") == nil,
            "Asset catalog not compiled (SwiftPM does not run actool) — run via xcodebuild to exercise this test"
        )

        for hue in Self.hues {
            for slot in Self.slots {
                let name = "lemonade-themed-\(hue)-\(slot)"
                XCTAssertNotNil(namedColor(name), "missing asset \(name)")
            }
        }
    }
}
