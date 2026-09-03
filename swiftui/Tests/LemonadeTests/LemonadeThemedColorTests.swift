import XCTest
@testable import Lemonade

final class LemonadeThemedColorTests: XCTestCase {
    func testEveryThemedAssetResolvesInTheBundle() throws {
        // Bare SwiftPM does not run actool, so the .xcassets is not compiled and no
        // asset resolves — including pre-existing semantic ones. Use a semantic asset
        // as a canary: if it cannot resolve, the catalog is absent rather than the
        // themed names being wrong, so skip instead of reporting a false failure.
        // Under `xcodebuild test` the catalog is compiled and every assertion runs.
        #if canImport(UIKit)
        let canary = UIColor(named: "lemonade-background-bg-default", in: .lemonade, compatibleWith: nil)
        #elseif canImport(AppKit)
        let canary = NSColor(named: "lemonade-background-bg-default", bundle: .lemonade)
        #endif
        try XCTSkipIf(canary == nil, "Asset catalog not compiled (SwiftPM does not run actool) — run via xcodebuild to exercise this test")

        // Color(_:bundle:) succeeds even when the asset is missing, so assert on
        // the platform colour type, which returns nil for an unknown name.
        let names = [
            "lemonade-themed-background-bg-blue",
            "lemonade-themed-background-bg-blue-subtle",
            "lemonade-themed-border-border-blue",
            "lemonade-themed-border-border-blue-subtle",
            "lemonade-themed-content-content-blue",
            "lemonade-themed-content-content-blue-on-color",
            "lemonade-themed-content-content-on-blue",
            "lemonade-themed-content-content-on-amber",
            "lemonade-themed-background-bg-green-lime",
        ]
        for name in names {
            #if canImport(UIKit)
            XCTAssertNotNil(UIColor(named: name, in: .lemonade, compatibleWith: nil), "missing asset \(name)")
            #elseif canImport(AppKit)
            XCTAssertNotNil(NSColor(named: name, bundle: .lemonade), "missing asset \(name)")
            #endif
        }
    }
}
