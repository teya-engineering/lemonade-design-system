import SwiftUI
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

    private static let primarySlots = [
        "background", "background-high", "border", "content", "content-inverse",
        "on-background", "on-background-high",
    ]

    private static let subtleSlots = ["background", "border", "on-background"]

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

    func testThemedColorsCanBePassedAroundAsValues() {
        // The point of grouping by hue: one colour is a value, so a chart series or a
        // per-role mapping can hold them in a collection and read slots off them. This
        // only compiles because every group conforms to ThemedColor — without it the
        // array infers as [Any] and `\.background` does not resolve.
        let series: [ThemedPrimaryColor] = [
            LemonadeTheme.themed.blue,
            LemonadeTheme.themed.amber,
            LemonadeTheme.themed.neutral,
        ]
        XCTAssertEqual(series.map(\.background).count, 3)

        // Either palette is a ThemedColor, so a component picks one and styles from it.
        func style(_ colour: ThemedColor) -> [Color] {
            [colour.background, colour.border, colour.onBackground]
        }
        XCTAssertEqual(style(LemonadeTheme.themed.violet).count, 3)
        XCTAssertEqual(style(LemonadeTheme.themed.violet.subtle).count, 3)
    }

    /// `background-high` from `backgroundHigh`, `green-lime` from `greenLime`.
    private func kebab(_ camel: String) -> String {
        camel.reduce(into: "") { out, ch in
            if ch.isUppercase { out += "-" + ch.lowercased() } else { out.append(ch) }
        }
    }

    func testTheHardcodedListsStillMatchTheGeneratedPalette() {
        // The lists above are hand-written, so they can drift from what the converter
        // emits: a hue added to the Figma export would simply not be checked, and a hue
        // dropped from it would keep passing. Deriving the same lists by reflection and
        // comparing pins them to the generated palette in both directions.
        let hues = Mirror(reflecting: LemonadeTheme.themed).children.compactMap(\.label).map(kebab)
        XCTAssertEqual(
            Set(hues), Set(Self.hues),
            "themed hue list is stale - regenerate, then update LemonadeThemedColorTests.hues"
        )

        let blue = LemonadeTheme.themed.blue
        let primary = Mirror(reflecting: blue).children.compactMap(\.label).map(kebab)
            .filter { $0 != "subtle" }
        XCTAssertEqual(
            Set(primary), Set(Self.primarySlots),
            "themed primary slots are stale - update LemonadeThemedColorTests.primarySlots"
        )

        let subtle = Mirror(reflecting: blue.subtle).children.compactMap(\.label).map(kebab)
        XCTAssertEqual(
            Set(subtle), Set(Self.subtleSlots),
            "themed subtle slots are stale - update LemonadeThemedColorTests.subtleSlots"
        )
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
            for slot in Self.primarySlots {
                let name = "lemonade-themed-\(hue)-\(slot)"
                XCTAssertNotNil(namedColor(name), "missing asset \(name)")
            }
            for slot in Self.subtleSlots {
                let name = "lemonade-themed-\(hue)-subtle-\(slot)"
                XCTAssertNotNil(namedColor(name), "missing asset \(name)")
            }
        }
    }
}
