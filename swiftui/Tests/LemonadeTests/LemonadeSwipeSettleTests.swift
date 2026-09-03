import XCTest
@testable import Lemonade

/// Covers `resolveSwipeSettle` — where a released drag lands. Geometry matches a trailing action on
/// a 361pt-wide row: 76pt of travel brings it fully out, and a full swipe commits at
/// `swipeCommitThreshold`, which is 198.55pt of it.
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

    func testCrossingTheCommitThresholdCommits() {
        XCTAssertEqual(settle(travel: 200), .committed)
    }

    func testLongDragOnlyOpensWhenFullSwipeIsOff() {
        XCTAssertEqual(settle(travel: 200, allowsFullSwipe: false), .open)
    }

    /// A commit outranks a flick back, matching Compose.
    func testCommitBeatsAFlickBack() {
        XCTAssertEqual(settle(travel: 200, velocity: -900), .committed)
    }

    // Threshold cases, mirroring `SwipeSettleTest`: these are what pin the `>=` choices down.

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

    /// Coming back from a commit, the row gives up its lead in proportion to the finger: no step at
    /// the crossing, and home exactly as the finger gets there.
    func testAReleasedCommitHandsTheRowBackInProportionToTheFinger() {
        let threshold = rowWidth * 0.55
        let commitTravel = rowWidth - 20
        func released(_ travel: CGFloat) -> CGFloat {
            resolveSwipeReleasedTravel(travel: travel, commitTravel: commitTravel, threshold: threshold)
        }
        // Continuous at the crossing: a drag can only leave a commit here.
        XCTAssertEqual(released(threshold), commitTravel, accuracy: 0.001)
        XCTAssertEqual(released(threshold / 2), commitTravel / 2, accuracy: 0.001)
        XCTAssertEqual(released(0), 0, accuracy: 0.001)
    }

    /// Never past where the commit had it, however far the finger is.
    func testAReleasedCommitNeverDrawsTheRowFurtherThanTheCommitDid() {
        XCTAssertEqual(
            resolveSwipeReleasedTravel(travel: 300, commitTravel: 400, threshold: 220),
            400,
            accuracy: 0.001
        )
    }

    /// A row with no width to cross has no lead to give back.
    func testAReleasedCommitWithNoThresholdDrawsTheFinger() {
        XCTAssertEqual(
            resolveSwipeReleasedTravel(travel: 40, commitTravel: 0, threshold: 0),
            40,
            accuracy: 0.001
        )
    }

    /// A drag that committed and then came back below the threshold must not fire on release.
    ///
    /// The two resolvers compose: the row is drawn at `resolveSwipeReleasedTravel` of the finger,
    /// which is up to 1.7x further out, and settling on *that* fires the action from a third of the
    /// way across — after the crossing back has already told the reader the gesture is no longer
    /// its. Each function alone is right; only together do they say so.
    func testADragHandedBackFromACommitDoesNotFireOnRelease() {
        let commitTravel = rowWidth - 20
        let threshold = swipeCommitThreshold(rowWidth: rowWidth)
        // Just below the threshold, where crossing back has just happened.
        let reached = threshold - 1
        let drawn = resolveSwipeReleasedTravel(
            travel: reached,
            commitTravel: commitTravel,
            threshold: threshold
        )
        XCTAssertGreaterThan(drawn, reached, "the row is drawn ahead of the finger on the way back")
        XCTAssertEqual(settle(travel: drawn), .committed)
        XCTAssertNotEqual(settle(travel: reached), .committed)
    }
}
