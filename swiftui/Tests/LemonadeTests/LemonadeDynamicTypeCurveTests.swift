import XCTest
import SwiftUI
@testable import Lemonade

#if canImport(UIKit)
import UIKit
#endif

/// Locks the Dynamic Type curve each text style follows.
///
/// Every style used to resolve through `relativeTo: .body`, which handed display text the growth
/// curve Apple designed for body copy. These tests pin the mapping so a future edit cannot quietly
/// collapse it back to a single curve.
final class LemonadeDynamicTypeCurveTests: XCTestCase {
    private let typography = LemonadeTypography.shared

    // MARK: - The mapping itself

    func testDisplayStylesFollowTheLargeTitleCurve() {
        let displayStyles = [
            typography.displayXSmall,
            typography.displaySmall,
            typography.displayMedium,
            typography.displayLarge,
            typography.displayXLarge,
            typography.display2XLarge,
            typography.display3XLarge
        ]
        for style in displayStyles {
            XCTAssertEqual(style.relativeTextStyle, .largeTitle)
        }
    }

    func testHeadingStylesTakeTheTitleCurveNearestTheirSize() {
        XCTAssertEqual(typography.headingXLarge.relativeTextStyle, .title)
        XCTAssertEqual(typography.headingLarge.relativeTextStyle, .title2)
        XCTAssertEqual(typography.headingMedium.relativeTextStyle, .title2)
        XCTAssertEqual(typography.headingSmall.relativeTextStyle, .title3)
        XCTAssertEqual(typography.headingXSmall.relativeTextStyle, .headline)
        XCTAssertEqual(typography.headingXXSmall.relativeTextStyle, .headline)
    }

    func testBodyStylesKeepTheBodyCurve() {
        let bodyStyles = [
            typography.bodyXLargeRegular,
            typography.bodyLargeRegular,
            typography.bodyMediumRegular,
            typography.bodyMediumSemiBold
        ]
        for style in bodyStyles {
            XCTAssertEqual(style.relativeTextStyle, .body)
        }
    }

    func testSmallBodyStylesTakeTheSteeperFootnoteAndCaptionCurves() {
        XCTAssertEqual(typography.bodySmallRegular.relativeTextStyle, .footnote)
        XCTAssertEqual(typography.bodySmallSemiBold.relativeTextStyle, .footnote)
        XCTAssertEqual(typography.bodyXSmallRegular.relativeTextStyle, .caption)
        XCTAssertEqual(typography.bodyXSmallOverline.relativeTextStyle, .caption)
    }

    /// The two 24pt styles want different curves, which is why the mapping is stored per style
    /// rather than derived from `fontSize`.
    func testSameSizedDisplayAndHeadingStylesDivergeOnCurve() {
        XCTAssertEqual(typography.displayXSmall.fontSize, typography.headingSmall.fontSize)
        XCTAssertNotEqual(
            typography.displayXSmall.relativeTextStyle,
            typography.headingSmall.relativeTextStyle
        )
    }

    func testStylesBuiltOutsideTheDesignSystemDefaultToBody() {
        let custom = LemonadeTextStyle(fontSize: 16, lineHeight: 24, fontWeight: .regular)
        XCTAssertEqual(custom.relativeTextStyle, .body)
    }

    // MARK: - What the mapping buys, measured

#if canImport(UIKit)
    /// `UIFontMetrics` is the mechanism behind SwiftUI's `relativeTo:`, so scaling a style's point
    /// size through its own metrics gives the size the text actually renders at.
    private func scaledSize(
        of style: LemonadeTextStyle,
        at category: UIContentSizeCategory
    ) -> CGFloat {
        let uiTextStyle = UIFont.TextStyle(style.relativeTextStyle)
        return UIFontMetrics(forTextStyle: uiTextStyle).scaledValue(
            for: style.fontSize,
            compatibleWith: UITraitCollection(preferredContentSizeCategory: category)
        )
    }

    func testNothingScalesAtTheDefaultContentSize() {
        let styles = [
            typography.display3XLarge,
            typography.headingLarge,
            typography.bodyMediumRegular,
            typography.bodyXSmallRegular
        ]
        for style in styles {
            XCTAssertEqual(scaledSize(of: style, at: .large), style.fontSize, accuracy: 0.5)
        }
    }

    /// The point of the change: at AX5 the largest display style must grow by a smaller factor
    /// than body copy does.
    func testDisplayTextGrowsMoreSlowlyThanBodyAtAccessibilitySizes() {
        let ax5 = UIContentSizeCategory.accessibilityExtraExtraExtraLarge
        let displayGrowth = scaledSize(of: typography.display3XLarge, at: ax5)
            / typography.display3XLarge.fontSize
        let bodyGrowth = scaledSize(of: typography.bodyMediumRegular, at: ax5)
            / typography.bodyMediumRegular.fontSize

        XCTAssertLessThan(displayGrowth, bodyGrowth)
        // Apple's own curves put these near 1.8x and 3.1x. Assert a clear gap rather than exact
        // figures, which are Apple's to change.
        XCTAssertLessThan(displayGrowth, bodyGrowth * 0.75)
    }

    /// Rule 6 of the text-scaling contract: a style may grow less, but never so little that it
    /// ends up rendering smaller than body copy at the same setting.
    func testEveryStyleStaysAtLeastAsLargeAsBodyAtAccessibilitySizes() {
        let ax5 = UIContentSizeCategory.accessibilityExtraExtraExtraLarge
        let bodySize = scaledSize(of: typography.bodyMediumRegular, at: ax5)
        let atLeastBodySized = [
            typography.display3XLarge,
            typography.displayXSmall,
            typography.headingXLarge,
            typography.headingSmall
        ]
        for style in atLeastBodySized {
            XCTAssertGreaterThanOrEqual(scaledSize(of: style, at: ax5), bodySize)
        }
    }

    func testSmallTextGrowsAtLeastAsFastAsBody() {
        let ax5 = UIContentSizeCategory.accessibilityExtraExtraExtraLarge
        let bodyGrowth = scaledSize(of: typography.bodyMediumRegular, at: ax5)
            / typography.bodyMediumRegular.fontSize
        let captionGrowth = scaledSize(of: typography.bodyXSmallRegular, at: ax5)
            / typography.bodyXSmallRegular.fontSize

        XCTAssertGreaterThanOrEqual(captionGrowth, bodyGrowth)
    }
#endif
}

#if canImport(UIKit)
private extension UIFont.TextStyle {
    /// SwiftUI's `Font.TextStyle` and UIKit's `UIFont.TextStyle` describe the same curves but do
    /// not bridge, so the test maps across explicitly.
    init(_ textStyle: Font.TextStyle) {
        switch textStyle {
        case .largeTitle: self = .largeTitle
        case .title: self = .title1
        case .title2: self = .title2
        case .title3: self = .title3
        case .headline: self = .headline
        case .subheadline: self = .subheadline
        case .body: self = .body
        case .callout: self = .callout
        case .footnote: self = .footnote
        case .caption: self = .caption1
        case .caption2: self = .caption2
        @unknown default: self = .body
        }
    }
}
#endif
