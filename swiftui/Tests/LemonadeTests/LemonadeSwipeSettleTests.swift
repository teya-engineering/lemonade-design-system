import XCTest
@testable import Lemonade

/// Covers `resolveSwipeSettle` — where a released drag lands. Geometry matches a trailing action on
/// a 361pt-wide row: 76pt of travel brings it fully out, and a full swipe has to cross half of 361.
final class LemonadeSwipeSettleTests: XCTestCase {

    private let firstActionReveal: CGFloat = 76
    private let rowWidth: CGFloat = 361

    private func settle(
        travel: CGFloat,
        velocity: CGFloat = 0,
        allowsFullSwipe: Bool = true,
        firstActionReveal: CGFloat? = nil
    ) -> SwipeSettleTarget {
        resolveSwipeSettle(
            travel: travel,
            velocity: velocity,
            firstActionReveal: firstActionReveal ?? self.firstActionReveal,
            rowWidth: rowWidth,
            allowsFullSwipe: allowsFullSwipe
        )
    }

    func testShortDragSettlesClosed() {
        XCTAssertEqual(settle(travel: 20), .closed)
    }

    /// A drag that showed most of the action but not all of it still belongs back where it was.
    func testDragShortOfTheWholeActionSettlesClosed() {
        XCTAssertEqual(settle(travel: 70), .closed)
    }

    func testDragThatBringsTheActionFullyOutSettlesOpen() {
        XCTAssertEqual(settle(travel: 76), .open)
    }

    /// Momentum settles the row where it was going, not where the finger let go.
    func testAFlickOpensARowTheFingerDidNotCarryAllTheWay() {
        XCTAssertEqual(settle(travel: 20, velocity: 900), .open)
    }

    /// The same distance without the speed behind it does not, which is the whole point of settling
    /// on the projection rather than on a speed rule of its own.
    func testTheSameDragWithoutTheSpeedDoesNot() {
        XCTAssertEqual(settle(travel: 20), .closed)
    }

    /// A destructive action must not fire off momentum alone: the commit reads the travel itself,
    /// so a flick that projects across the row still only opens it.
    func testMomentumDoesNotCommitASwipeTheFingerNeverCarried() {
        XCTAssertEqual(settle(travel: 100, velocity: 2000), .open)
    }

    func testFlickBackClosesAnOpenRow() {
        XCTAssertEqual(settle(travel: 100, velocity: -900), .closed)
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

    func testDragExactlyOnTheActionsRevealOpens() {
        XCTAssertEqual(settle(travel: firstActionReveal), .open)
    }

    /// A drag back carries its own momentum too, so the projection is what closes the row.
    func testAFlickBackClosesARowTheFingerLeftOpen() {
        XCTAssertEqual(settle(travel: 100, velocity: -400), .closed)
    }

    func testTravelExactlyOnTheCommitThresholdCommits() {
        XCTAssertEqual(settle(travel: rowWidth * 0.55), .committed)
    }

    /// Halfway is no longer enough: iOS asks for a little more than half the row.
    func testTravelOnHalfTheRowDoesNotCommit() {
        XCTAssertNotEqual(settle(travel: rowWidth / 2), .committed)
    }

    func testARowWithNoActionsSettlesClosed() {
        XCTAssertEqual(settle(travel: 0, firstActionReveal: 0), .closed)
    }
}
