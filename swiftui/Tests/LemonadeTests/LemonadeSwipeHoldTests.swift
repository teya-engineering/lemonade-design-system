import XCTest
@testable import Lemonade

/// Covers `swipeHoldReleasesClaim` — whether a touch that has gone quiet keeps the drag it claimed
/// on its way to a context menu. The measure is the row's travel since the claim, against the 24pt
/// of `holdTravel`, and only a closed row ever hands a claim back.
final class LemonadeSwipeHoldTests: XCTestCase {

    private func releases(_ travelSinceClaim: CGFloat, open: Bool = false) -> Bool {
        swipeHoldReleasesClaim(travelSinceClaim: travelSinceClaim, rowIsOpen: open)
    }

    /// The drift a finger rolls through while it waits out a long press: measured at 7.3pt on the
    /// row that raised this, and nothing a reader can act on.
    func testDriftUnderAPressHandsTheClaimBack() {
        XCTAssertTrue(releases(7.3))
        XCTAssertTrue(releases(-7.3))
    }

    /// The boundary. A drag that has carried the row this far has asked for something, and a finger
    /// resting over it does not take it away.
    func testTheBoundaryKeepsTheClaim() {
        XCTAssertTrue(releases(23.9))
        XCTAssertFalse(releases(24))
    }

    /// An open row keeps its claim however little the drag has moved it. It is already showing a
    /// reveal, and handing the claim back would spring it open again under the finger — so a drag
    /// pulling it closed, paused halfway, would lose the close it had asked for.
    func testAnOpenRowAlwaysKeepsItsClaim() {
        XCTAssertFalse(releases(0, open: true))
        XCTAssertFalse(releases(7.3, open: true))
        XCTAssertFalse(releases(-20, open: true))
    }

    /// The invariant the slack exists to hold, checked against the reveal geometry rather than
    /// against a number: a claim is only ever handed back while the row is showing nothing.
    ///
    /// `holdTravel` and the travel at which the first action starts to be drawn are set apart in
    /// different places, and nothing but this ties them together. Raising the slack past that
    /// point would let a finger resting on a row take a reveal the reader can see away from them.
    ///
    /// Read as travel rather than as a delta, which is what it is at the call site for the row this
    /// guards: a closed row is claimed at rest, so the claim's origin is zero.
    func testAHoldNeverTakesBackARevealTheReaderCanSee() {
        let reveal: CGFloat = 76
        let actionWidth: CGFloat = 48
        for step in 0...Int(reveal * 2) {
            let travel = CGFloat(step) / 2
            let drawn = resolveSwipeStripReveal(
                travel: travel,
                actionReveal: reveal,
                stripReveal: reveal,
                actionWidth: actionWidth
            )
            guard drawn.scale > 0 else { continue }
            XCTAssertFalse(
                releases(travel),
                "travel \(travel) draws the action at scale \(drawn.scale) but would be handed back"
            )
        }
    }
}
