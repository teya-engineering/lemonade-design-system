import XCTest
@testable import Lemonade

/// Covers `resolveSwipeStripReveal` — how far one action has been revealed. Geometry matches a 48pt
/// trailing action in a 76pt reveal: 12pt of padding ahead of it and 16pt behind, which is what
/// `resolveSwipeRevealWidth` gives for one action at the theme's own tokens.
final class LemonadeSwipeRevealTests: XCTestCase {

    private let revealWidth: CGFloat = 76
    private let actionWidth: CGFloat = 48

    private func reveal(
        travel: CGFloat,
        actionWidth: CGFloat? = nil,
        revealWidth: CGFloat? = nil,
        stripReveal: CGFloat? = nil
    ) -> SwipeStripReveal {
        let actionReveal = revealWidth ?? self.revealWidth
        return resolveSwipeStripReveal(
            travel: travel,
            actionReveal: actionReveal,
            stripReveal: stripReveal ?? actionReveal,
            actionWidth: actionWidth ?? self.actionWidth
        )
    }

    /// An action grows over the last half of its own width — the last 24pt of a 48pt action.
    func testNothingIsDrawnUntilTheLastHalfOfTheAction() {
        XCTAssertEqual(reveal(travel: 51), SwipeStripReveal(scale: 0, stretch: 0))
        XCTAssertEqual(reveal(travel: 52), SwipeStripReveal(scale: 0, stretch: 0))
    }

    func testTheActionGrowsWithTheRowOverThatLastHalf() {
        XCTAssertEqual(reveal(travel: 64), SwipeStripReveal(scale: 0.5, stretch: 0))
    }

    func testTheActionIsAtFullSizeWhenTheRowRestsOpen() {
        XCTAssertEqual(reveal(travel: 76), SwipeStripReveal(scale: 1, stretch: 0))
    }

    func testTravelPastTheRevealStretchesTheActionInsteadOfScalingIt() {
        XCTAssertEqual(reveal(travel: 116), SwipeStripReveal(scale: 1, stretch: 40))
    }

    /// The invariant the whole model rests on: whatever the row has opened, the action's leading
    /// edge sits one leading gap — 12pt — ahead of it, scaling or stretched.
    func testTheActionIsAlwaysExactlyTheGapTheRowHasOpened() {
        let trailingPad: CGFloat = 16
        for travel in stride(from: CGFloat(52), through: 200, by: 5) {
            let r = reveal(travel: travel)
            let leadingEdge = trailingPad + actionWidth / 2 + (actionWidth / 2) * r.scale + r.stretch
            XCTAssertEqual(leadingEdge, travel - 12, accuracy: 0.001, "travel \(travel)")
        }
    }

    /// Actions being pushed along by a stretching one recede as it takes the row over: unchanged
    /// through the reveal and up to the commit threshold, down to a fifth by the time the row has
    /// gone.
    func testDisplacedActionsDimAsTheSwipeTakesTheRowOver() {
        let rowWidth: CGFloat = 400
        XCTAssertEqual(resolveSwipeDisplacedOpacity(travel: 0, rowWidth: rowWidth), 1, accuracy: 0.001)
        XCTAssertEqual(resolveSwipeDisplacedOpacity(travel: 220, rowWidth: rowWidth), 1, accuracy: 0.001)
        XCTAssertEqual(resolveSwipeDisplacedOpacity(travel: 310, rowWidth: rowWidth), 0.6, accuracy: 0.001)
        XCTAssertEqual(resolveSwipeDisplacedOpacity(travel: 400, rowWidth: rowWidth), 0.2, accuracy: 0.001)
    }

    /// A row that has not measured yet must not dim anything.
    func testNothingDimsBeforeTheRowHasAWidth() {
        XCTAssertEqual(resolveSwipeDisplacedOpacity(travel: 0, rowWidth: 0), 1)
    }

    func testAnEmptyStripIsNeverDrawn() {
        XCTAssertEqual(reveal(travel: 200, actionWidth: 0, revealWidth: 0), SwipeStripReveal(scale: 0, stretch: 0))
    }

    /// The second of two actions sits behind the first, so the row has to travel 132pt to rest on
    /// it rather than the 76pt that rests it on the first. Each action waits for its own share.
    func testAnActionBehindAnotherIsRevealedAgainstItsOwnShare() {
        XCTAssertEqual(reveal(travel: 108, revealWidth: 132), SwipeStripReveal(scale: 0, stretch: 0))
        XCTAssertEqual(reveal(travel: 120, revealWidth: 132), SwipeStripReveal(scale: 0.5, stretch: 0))
        XCTAssertEqual(reveal(travel: 132, revealWidth: 132), SwipeStripReveal(scale: 1, stretch: 0))
    }

    /// The outermost of a pair is out at 76pt but must not start filling the space behind it: that
    /// space is where the second action arrives, and a stretch would cover it before it ever did.
    func testTheOutermostActionWaitsForTheWholeStripBeforeStretching() {
        XCTAssertEqual(
            reveal(travel: 132, revealWidth: 76, stripReveal: 132),
            SwipeStripReveal(scale: 1, stretch: 0)
        )
        XCTAssertEqual(
            reveal(travel: 172, revealWidth: 76, stripReveal: 132),
            SwipeStripReveal(scale: 1, stretch: 40)
        )
    }

    /// Where the row rests: the padding it opens into, every action, and a gap between each pair.
    /// The one formula the whole reveal is measured from, so it is worth pinning at the sizes the
    /// theme actually gives it.
    func testTheRevealIsTheActionsPlusTheirGapsPlusThePaddingTheySitIn() {
        func through(_ count: Int) -> CGFloat {
            resolveSwipeRevealWidth(count: count, actionWidth: 48, gap: 8, padding: 28)
        }
        XCTAssertEqual(through(0), 0, accuracy: 0.001)
        XCTAssertEqual(through(1), 76, accuracy: 0.001)
        XCTAssertEqual(through(2), 132, accuracy: 0.001)
        XCTAssertEqual(through(3), 188, accuracy: 0.001)
    }

    /// A row with no actions has nothing to open onto.
    func testARevealWithNoActionsIsClosed() {
        XCTAssertEqual(
            resolveSwipeRevealWidth(count: -1, actionWidth: 48, gap: 8, padding: 28),
            0,
            accuracy: 0.001
        )
    }
}
