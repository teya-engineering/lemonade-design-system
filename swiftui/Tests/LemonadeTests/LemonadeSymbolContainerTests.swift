import SwiftUI
import XCTest
@testable import Lemonade

final class LemonadeSymbolContainerTests: XCTestCase {
    func testAHueStylesTheContainerFromItsSolidPalette() {
        let solid = LemonadeTheme.themed.violet
        XCTAssertEqual(
            LemonadeTheme.themed.symbolContainerColors(theme: .violet),
            SymbolContainerColors(
                background: solid.background,
                border: solid.border,
                content: solid.onBackground
            )
        )
    }

    func testASubtleHueStylesTheContainerFromItsSubtlePalette() {
        let subtle = LemonadeTheme.themed.violet.subtle
        XCTAssertEqual(
            LemonadeTheme.themed.symbolContainerColors(theme: .violet.subtle),
            SymbolContainerColors(
                background: subtle.background,
                border: subtle.border,
                content: subtle.onBackground
            )
        )
    }
}
