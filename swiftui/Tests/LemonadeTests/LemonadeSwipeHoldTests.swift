import XCTest
@testable import Lemonade

/// Covers `swipeHoldReleasesClaim` — whether a touch that has gone quiet keeps the drag it claimed
/// on its way to a context menu. The measure is the row's travel since the claim, against the 24pt
/// of `holdTravel`.
final class LemonadeSwipeHoldTests: XCTestCase {

    /// The drift a finger rolls through while it waits out a long press: measured at 7.3pt on the
    /// row that raised this, and nothing a reader can act on.
    func testDriftUnderAPressHandsTheClaimBack() {
        XCTAssertTrue(swipeHoldReleasesClaim(travelSinceClaim: 7.3))
        XCTAssertTrue(swipeHoldReleasesClaim(travelSinceClaim: -7.3))
    }

    /// The boundary. A drag that has carried the row this far has asked for something, and a finger
    /// resting over it does not take it away.
    func testTheBoundaryKeepsTheClaim() {
        XCTAssertTrue(swipeHoldReleasesClaim(travelSinceClaim: 23.9))
        XCTAssertFalse(swipeHoldReleasesClaim(travelSinceClaim: 24))
    }

    /// The case the slack is really there for: a reveal a reader is holding open to look at stays
    /// open under their finger.
    func testAReadableRevealKeepsIt() {
        XCTAssertFalse(swipeHoldReleasesClaim(travelSinceClaim: 76))
    }

    /// The invariant the slack exists to hold, checked against the reveal geometry rather than
    /// against a number: a claim is only ever handed back while the row is showing nothing.
    ///
    /// `holdTravel` and the travel at which the first action starts to be drawn are set apart in
    /// different places, and nothing but this ties them together. Raising the slack past that
    /// point would let a finger resting on a row take a reveal the reader can see away from them.
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
                swipeHoldReleasesClaim(travelSinceClaim: travel),
                "travel \(travel) draws the action at scale \(drawn.scale) but would be handed back"
            )
        }
    }
}
