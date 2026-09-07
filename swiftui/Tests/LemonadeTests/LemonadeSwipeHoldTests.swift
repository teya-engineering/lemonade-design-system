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
}
