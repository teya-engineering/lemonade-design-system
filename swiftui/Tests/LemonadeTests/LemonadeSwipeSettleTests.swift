import XCTest
@testable import Lemonade

/// Covers `resolveSwipeSettle` — where a released drag lands. Geometry matches one trailing action
/// on a 361pt-wide row: a 60pt reveal, and a full swipe that has to cross half of 361.
final class LemonadeSwipeSettleTests: XCTestCase {

    private func settle(
        travel: CGFloat,
        velocity: CGFloat = 0,
        allowsFullSwipe: Bool = true
    ) -> SwipeSettleTarget {
        resolveSwipeSettle(
            travel: travel,
            velocity: velocity,
            revealWidth: 60,
            rowWidth: 361,
            allowsFullSwipe: allowsFullSwipe
        )
    }

    func testShortDragSettlesClosed() {
        XCTAssertEqual(settle(travel: 20), .closed)
    }

    func testDragPastHalfTheRevealSettlesOpen() {
        XCTAssertEqual(settle(travel: 40), .open)
    }

    func testFlickOpensBeforeTravellingHalfTheReveal() {
        XCTAssertEqual(settle(travel: 12, velocity: 900), .open)
    }

    func testFlickBackClosesAnOpenRow() {
        XCTAssertEqual(settle(travel: 55, velocity: -900), .closed)
    }

    func testCrossingHalfTheRowCommits() {
        XCTAssertEqual(settle(travel: 200), .committed)
    }

    func testLongDragOnlyOpensWhenFullSwipeIsOff() {
        XCTAssertEqual(settle(travel: 200, allowsFullSwipe: false), .open)
    }

    /// A commit outranks a flick back, matching Compose.
    func testCommitBeatsAFlickBack() {
        XCTAssertEqual(settle(travel: 200, velocity: -900), .committed)
    }
}
