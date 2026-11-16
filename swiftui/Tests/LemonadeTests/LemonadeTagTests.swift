import SwiftUI
import XCTest
@testable import Lemonade

final class LemonadeTagTests: XCTestCase {
    func testAHueStylesTheTagFromItsSolidPalette() {
        let solid = LemonadeTheme.themed.violet
        XCTAssertEqual(
            LemonadeTheme.themed.tagColors(theme: .violet),
            TagColors(
                background: solid.background,
                border: solid.border,
                content: solid.onBackground
            )
        )
    }

    func testASubtleHueStylesTheTagFromItsSubtlePalette() {
        let subtle = LemonadeTheme.themed.violet.subtle
        XCTAssertEqual(
            LemonadeTheme.themed.tagColors(theme: .violet.subtle),
            TagColors(
                background: subtle.background,
                border: subtle.border,
                content: subtle.onBackground
            )
        )
    }
}
