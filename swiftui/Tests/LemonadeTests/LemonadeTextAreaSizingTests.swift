import XCTest
@testable import Lemonade

final class LemonadeTextAreaSizingTests: XCTestCase {
    func testAnEmptyFieldIsMeasuredAsOneEmptyLine() {
        XCTAssertEqual(textAreaSizingText(input: ""), "\u{200B}")
    }

    func testATrailingNewlineKeepsItsEmptyLine() {
        XCTAssertEqual(textAreaSizingText(input: "Hello\n"), "Hello\n\u{200B}")
    }

    func testTrailingSpacesStayInTheMeasuredText() {
        XCTAssertEqual(textAreaSizingText(input: "Hello   "), "Hello   \u{200B}")
    }
}
