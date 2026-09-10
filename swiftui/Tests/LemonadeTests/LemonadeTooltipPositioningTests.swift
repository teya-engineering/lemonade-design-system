import XCTest
@testable import Lemonade

final class LemonadeTooltipPositioningTests: XCTestCase {

    /// A 393x852 iPhone container.
    private let container = CGSize(width: 393, height: 852)

    /// The store selector, in the top half of the screen.
    private let topAnchor = CGRect(x: 142, y: 185, width: 109, height: 36)

    /// The payment-method chip, in the bottom half of the screen.
    private let bottomAnchor = CGRect(x: 123, y: 461, width: 146, height: 36)

    /// A leading-edge icon, with room beside it for a tooltip pointing left at it.
    private let leadingAnchor = CGRect(x: 16, y: 400, width: 40, height: 40)

    /// The same icon on the trailing edge, for a tooltip pointing right at it.
    private let trailingAnchor = CGRect(x: 337, y: 400, width: 40, height: 40)

    /// A title-and-content tooltip: 280pt of body plus the 8pt the side indicator adds.
    private let sideTooltip = CGSize(width: 288, height: 76)

    func testAutoPlacementPutsTheTooltipBelowAnAnchorInTheTopHalf() {
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: topAnchor,
                containerSize: container,
                forcedPlacement: nil
            ),
            .top
        )
    }

    func testAutoPlacementPutsTheTooltipAboveAnAnchorInTheBottomHalf() {
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: bottomAnchor,
                containerSize: container,
                forcedPlacement: nil
            ),
            .bottom
        )
    }

    func testAForcedPlacementWinsOverTheAnchorsHalfOfTheContainer() {
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: topAnchor,
                containerSize: container,
                forcedPlacement: .bottomCenter
            ),
            .bottom
        )
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: bottomAnchor,
                containerSize: container,
                forcedPlacement: .topCenter
            ),
            .top
        )
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: bottomAnchor,
                containerSize: container,
                forcedPlacement: .leftCenter
            ),
            .left
        )
    }

    func testAnIndicatorlessPlacementFallsBackToAutoPlacement() {
        // `LemonadeTooltipIndicatorPlacement.none` must be spelled out: a bare `.none` in an
        // Optional parameter resolves to `Optional.none`, which is a different test.
        XCTAssertEqual(
            LemonadeTooltipPositioning.edge(
                anchor: topAnchor,
                containerSize: container,
                forcedPlacement: LemonadeTooltipIndicatorPlacement.none
            ),
            .top
        )
    }

    func testAutoPlacementNeverPicksASidePlacement() {
        for edge in [LemonadeTooltipIndicatorEdge.top, .bottom] {
            let placement = LemonadeTooltipPositioning.indicatorPlacement(
                anchor: leadingAnchor,
                containerSize: container,
                tooltipWidth: 280,
                edge: edge
            )
            XCTAssertFalse(placement.isOnVerticalEdge)
        }
    }

    func testALeftPlacementPutsTheTooltipToTheRightOfItsAnchor() {
        let origin = LemonadeTooltipPositioning.origin(
            anchor: leadingAnchor,
            containerSize: container,
            tooltipSize: sideTooltip,
            placement: .leftCenter
        )

        // Anchor right edge plus the 4pt gap, and centred on the anchor vertically.
        XCTAssertEqual(origin.x, 60)
        XCTAssertEqual(origin.y, 420 - 76 / 2)
    }

    func testARightPlacementPutsTheTooltipToTheLeftOfItsAnchor() {
        let origin = LemonadeTooltipPositioning.origin(
            anchor: trailingAnchor,
            containerSize: container,
            tooltipSize: sideTooltip,
            placement: .rightCenter
        )

        // Anchor left edge, less the 4pt gap and the tooltip's own width.
        XCTAssertEqual(origin.x, 337 - 4 - 288)
        XCTAssertEqual(origin.y, 420 - 76 / 2)
    }

    func testASidePlacementLinesItsIndicatorUpWithTheAnchorCentre() {
        let top = LemonadeTooltipPositioning.origin(
            anchor: leadingAnchor,
            containerSize: container,
            tooltipSize: sideTooltip,
            placement: .leftTop
        )
        let bottom = LemonadeTooltipPositioning.origin(
            anchor: leadingAnchor,
            containerSize: container,
            tooltipSize: sideTooltip,
            placement: .leftBottom
        )

        // The indicator is fixed within the tooltip, so reaching the same anchor with it lower down
        // has to lift the tooltip itself.
        XCTAssertLessThan(bottom.y, top.y)
        // Both point at the anchor's centre, 420pt down: the offset from the tooltip's top edge to the
        // indicator is 24pt of inset plus half of the 15pt base for the top variant, and 13pt further
        // down for the bottom one.
        XCTAssertEqual(top.y, 420 - 31.5)
        XCTAssertEqual(bottom.y, 420 - 44.5)
    }

    func testAnEdgeTooShortForTheInsetCollapsesAllThreePositionsOntoItsCentre() {
        // A content-only tooltip is barely taller than the inset on both ends, which would otherwise
        // put the bottom indicator above the top one.
        let placements: [LemonadeTooltipIndicatorPlacement] = [.leftTop, .leftCenter, .leftBottom]
        let positions = placements.map { placement in
            placement.indicatorCenterOffset(alongEdgeOfLength: 40)
        }

        XCTAssertEqual(positions, [20, 20, 20])
    }
}
