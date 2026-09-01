import XCTest
@testable import Lemonade

/// Covers `resolveSwipeSettle` — where a released drag lands. Geometry matches one trailing action
/// on a 361pt-wide row: a 60pt reveal, and a full swipe that has to cross half of 361.
final class LemonadeSwipeSettleTests: XCTestCase {

    private let revealWidth: CGFloat = 60
    private let rowWidth: CGFloat = 361

    private func settle(
        travel: CGFloat,
        velocity: CGFloat = 0,
        allowsFullSwipe: Bool = true,
        revealWidth: CGFloat? = nil
    ) -> SwipeSettleTarget {
        resolveSwipeSettle(
            travel: travel,
            velocity: velocity,
            revealWidth: revealWidth ?? self.revealWidth,
            rowWidth: rowWidth,
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

    /// Threshold cases, mirroring `SwipeSettleTest`: these are what pin the `>=` choices down.

    func testDragExactlyOnHalfTheRevealOpens() {
        XCTAssertEqual(settle(travel: revealWidth / 2), .open)
    }

    func testFlickExactlyOnTheFlingThresholdOpens() {
        XCTAssertEqual(settle(travel: 0, velocity: 400), .open)
    }

    func testFlickBackExactlyOnTheFlingThresholdCloses() {
        XCTAssertEqual(settle(travel: 55, velocity: -400), .closed)
    }

    func testTravelExactlyOnHalfTheRowCommits() {
        XCTAssertEqual(settle(travel: rowWidth / 2), .committed)
    }

    func testFirstFrameWithNothingMeasuredSettlesClosed() {
        XCTAssertEqual(settle(travel: 0, revealWidth: 0), .closed)
    }
}
