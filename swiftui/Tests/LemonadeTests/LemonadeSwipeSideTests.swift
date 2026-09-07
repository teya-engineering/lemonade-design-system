import XCTest
@testable import Lemonade

/// Covers the rules that give a swipe a side: which one a gesture owns, how far the row may travel
/// onto it, and where a delta leaves the row. Geometry matches one action a side on a 361pt-wide
/// row, where 76pt of travel brings an action fully out.
final class LemonadeSwipeSideTests: XCTestCase {

    private let revealWidth: CGFloat = 76
    private let rowWidth: CGFloat = 361

    // MARK: - resolveSwipeGestureSide

    func testAClosedRowTakesTheSideTheFingerSetsOffTowards() {
        XCTAssertEqual(resolveSwipeGestureSide(travel: 0, delta: 12), .trailing)
        XCTAssertEqual(resolveSwipeGestureSide(travel: 0, delta: -12), .leading)
    }

    /// One gesture, one side. A finger dragging an open row back is closing it, not setting off
    /// towards the actions on the other edge.
    func testAnOpenRowKeepsItsOwnSideWhicheverWayTheFingerGoes() {
        XCTAssertEqual(resolveSwipeGestureSide(travel: 76, delta: -400), .trailing)
        XCTAssertEqual(resolveSwipeGestureSide(travel: -76, delta: 400), .leading)
    }

    func testNothingIsDecidedWhileNothingHasMoved() {
        XCTAssertNil(resolveSwipeGestureSide(travel: 0, delta: 0))
    }

    // MARK: - resolveSwipeCeiling

    /// The one case the trailing-only row never had: a row with actions on one edge only is still
    /// draggable, and the empty edge has to hold it at rest rather than let it be carried across.
    func testASideWithNoActionsHasNowhereToGo() {
        XCTAssertEqual(
            resolveSwipeCeiling(revealWidth: 0, rowWidth: rowWidth, allowsFullSwipe: true),
            0
        )
    }

    func testAFullSwipeMayCarryTheRowAcross() {
        XCTAssertEqual(
            resolveSwipeCeiling(revealWidth: revealWidth, rowWidth: rowWidth, allowsFullSwipe: true),
            rowWidth
        )
    }

    func testWithoutOneTheRowCapsAtTheReveal() {
        XCTAssertEqual(
            resolveSwipeCeiling(
                revealWidth: revealWidth,
                rowWidth: rowWidth,
                allowsFullSwipe: false
            ),
            revealWidth
        )
    }

    // MARK: - resolveSwipeTravel

    private func travel(
        _ travel: CGFloat,
        delta: CGFloat,
        side: SwipeActionSide?,
        ceiling: CGFloat? = nil
    ) -> CGFloat {
        resolveSwipeTravel(
            travel: travel,
            delta: delta,
            side: side,
            ceiling: ceiling ?? rowWidth
        )
    }

    /// Leading travel is negative, which is the whole of what tells the row which strip to draw.
    func testTravelCarriesTheSideInItsSign() {
        XCTAssertEqual(travel(0, delta: 40, side: .trailing), 40)
        XCTAssertEqual(travel(0, delta: -40, side: .leading), -40)
    }

    /// A gesture that has closed the row stops there. Carrying on into the other side's actions
    /// would turn one drag back into a commit on the opposite edge.
    func testAGestureCannotCrossIntoTheOtherSide() {
        XCTAssertEqual(travel(10, delta: -50, side: .trailing), 0)
        XCTAssertEqual(travel(-10, delta: 50, side: .leading), 0)
    }

    func testTravelCapsAtItsOwnSidesCeiling() {
        XCTAssertEqual(travel(0, delta: 400, side: .trailing, ceiling: revealWidth), revealWidth)
        XCTAssertEqual(travel(0, delta: -400, side: .leading, ceiling: revealWidth), -revealWidth)
    }

    /// A side with nothing behind it holds the row at rest however hard it is dragged, and does
    /// not come back carrying a sign that would read as the side it could not move onto.
    func testAnEmptySideDoesNotMove() {
        let held = travel(0, delta: -400, side: .leading, ceiling: 0)
        XCTAssertEqual(held, 0)
        XCTAssertNil(swipeTravelSide(travel: held))
    }

    /// A gesture that owns no side yet is a row at rest that has not been moved, so it has not
    /// gone anywhere.
    func testAGestureThatOwnsNoSideHasNotMoved() {
        XCTAssertEqual(travel(0, delta: 0, side: nil), 0)
    }

    // MARK: - swipeCrossedCommit

    func testADragPastTheThresholdCrosses() {
        XCTAssertTrue(
            swipeCrossedCommit(travel: 200, rowWidth: rowWidth, allowsFullSwipe: true)
        )
        XCTAssertTrue(
            swipeCrossedCommit(travel: -200, rowWidth: rowWidth, allowsFullSwipe: true)
        )
    }

    func testADragShortOfItDoesNot() {
        XCTAssertFalse(
            swipeCrossedCommit(travel: 190, rowWidth: rowWidth, allowsFullSwipe: true)
        )
    }

    /// An unmeasured row has no width to have crossed half of: without the guard the threshold is
    /// zero and a drag that never moved reads as a commit.
    func testAnUnmeasuredRowNeverCrosses() {
        XCTAssertFalse(swipeCrossedCommit(travel: 0, rowWidth: 0, allowsFullSwipe: true))
    }

    func testNothingCrossesWithoutFullSwipe() {
        XCTAssertFalse(
            swipeCrossedCommit(travel: 300, rowWidth: rowWidth, allowsFullSwipe: false)
        )
    }
}
