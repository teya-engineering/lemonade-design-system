import XCTest
@testable import Lemonade

final class LemonadeTextAreaSizingTests: XCTestCase {
    func testAnEmptyFieldIsMeasuredWithItsPlaceholder() {
        XCTAssertEqual(textAreaSizingText(input: "", placeholder: "Type here"), "Type here")
    }

    func testAnEmptyFieldWithoutPlaceholderIsMeasuredAsOneLine() {
        XCTAssertEqual(textAreaSizingText(input: "", placeholder: nil), " ")
    }

    func testTheInputIsMeasuredOverThePlaceholder() {
        XCTAssertEqual(textAreaSizingText(input: "Hello", placeholder: "Type here"), "Hello")
    }

    func testATrailingNewlineKeepsItsEmptyLine() {
        XCTAssertEqual(textAreaSizingText(input: "Hello\n", placeholder: nil), "Hello\n ")
    }
}
