import XCTest
@testable import Lemonade

final class LemonadeTextAreaLimitTests: XCTestCase {
    func testAnyEditFitsWithoutALimit() {
        XCTAssertTrue(TextAreaLimit.accepts(oldLength: 5, newLength: 5_000, maxLength: nil))
    }

    func testAnEditUpToTheLimitFits() {
        XCTAssertTrue(TextAreaLimit.accepts(oldLength: 9, newLength: 10, maxLength: 10))
    }

    func testAnEditPastTheLimitIsRejected() {
        XCTAssertFalse(TextAreaLimit.accepts(oldLength: 10, newLength: 11, maxLength: 10))
        XCTAssertFalse(TextAreaLimit.accepts(oldLength: 8, newLength: 28, maxLength: 10))
    }

    func testTextAlreadyPastTheLimitCanBeShortened() {
        XCTAssertTrue(TextAreaLimit.accepts(oldLength: 17, newLength: 16, maxLength: 10))
        XCTAssertTrue(TextAreaLimit.accepts(oldLength: 17, newLength: 17, maxLength: 10))
    }

    func testTextAlreadyPastTheLimitCannotGrow() {
        XCTAssertFalse(TextAreaLimit.accepts(oldLength: 17, newLength: 18, maxLength: 10))
    }

    func testAnEmojiCountsAsItsUTF16Length() {
        let text = "Hi 👍"
        XCTAssertEqual(text.utf16.count, 5)
        XCTAssertFalse(TextAreaLimit.accepts(oldLength: 3, newLength: text.utf16.count, maxLength: 4))
    }
}
