import XCTest
import SwiftUI
@testable import Lemonade

#if canImport(UIKit)
import UIKit
#endif

/// Covers the line spacing correction: the gap between lines has to grow with the font, or a
/// paragraph closes in on itself at accessibility sizes.
final class LemonadeLineSpacingTests: XCTestCase {
    private let typography = LemonadeTypography.shared

    func testUnscaledLineSpacingStillHitsTheDesignLineHeight() {
        let style = typography.bodyMediumRegular
        // lineSpacing tops up the font's natural line height to the design value.
        XCTAssertEqual(style.lineSpacing + naturalLineHeight(of: style), style.lineHeight, accuracy: 0.5)
    }

#if canImport(UIKit)
    private func naturalLineHeight(of style: LemonadeTextStyle) -> CGFloat {
        return style.uiFont.lineHeight
    }

    /// At the default content size the growth factor is 1, so nothing moves.
    func testNothingChangesAtTheDefaultContentSize() {
        let styles = [
            typography.bodyMediumRegular,
            typography.bodySmallRegular,
            typography.headingLarge,
            typography.displayLarge
        ]
        for style in styles {
            XCTAssertEqual(style.lineSpacing(for: .large), style.lineSpacing, accuracy: 0.01)
        }
    }

    /// The whole point: at AX5 the gap grows roughly in step with the font.
    func testLineSpacingGrowsWithTheFontAtAccessibilitySizes() {
        let style = typography.bodyMediumRegular
        let scaled = style.lineSpacing(for: .accessibility5)

        XCTAssertGreaterThan(scaled, style.lineSpacing)

        // The correction is the font's own growth factor, so the ratio of the two spacings should
        // match the ratio of the two font sizes.
        let metrics = UIFontMetrics(forTextStyle: style.relativeTextStyle.uiKitTextStyle)
        let traits = UITraitCollection(preferredContentSizeCategory: .accessibilityExtraExtraExtraLarge)
        let growth = metrics.scaledValue(for: style.fontSize, compatibleWith: traits) / style.fontSize
        XCTAssertEqual(scaled / style.lineSpacing, growth, accuracy: 0.01)
    }

    /// The behaviour this fixes, stated as a ratio: the rendered line height over the rendered font
    /// size should stay at the design ratio instead of collapsing toward 1.
    func testLineHeightRatioSurvivesAtAccessibilitySizes() {
        let style = typography.bodyMediumRegular
        let designRatio = style.lineHeight / style.fontSize

        let metrics = UIFontMetrics(forTextStyle: style.relativeTextStyle.uiKitTextStyle)
        let traits = UITraitCollection(preferredContentSizeCategory: .accessibilityExtraExtraExtraLarge)
        let scaledFontSize = metrics.scaledValue(for: style.fontSize, compatibleWith: traits)
        let scaledNatural = (UIFont(name: style.fontName, size: scaledFontSize)
            ?? .systemFont(ofSize: scaledFontSize)).lineHeight

        let fixedRatio = (scaledNatural + style.lineSpacing(for: .accessibility5)) / scaledFontSize
        let oldRatio = (scaledNatural + style.lineSpacing) / scaledFontSize

        XCTAssertEqual(fixedRatio, designRatio, accuracy: 0.03)
        // And the old behaviour really was tighter, so the test would catch a regression to it.
        XCTAssertLessThan(oldRatio, designRatio - 0.1)
    }

    /// A style whose design line height already sits below the font's natural one clamps to zero and
    /// must not go negative or start growing.
    func testStylesWithNoRoomToSpareStayAtZero() {
        let style = typography.displayLarge
        XCTAssertEqual(style.lineSpacing, 0, accuracy: 0.01)
        XCTAssertEqual(style.lineSpacing(for: .accessibility5), 0, accuracy: 0.01)
    }

    func testContentSizeCategoryMappingCoversEveryDynamicTypeSize() {
        // A missed case would silently fall back to .large and freeze the correction.
        for size in DynamicTypeSize.allCases where size != .large {
            XCTAssertNotEqual(
                size.uiKitContentSizeCategory,
                UIContentSizeCategory.large,
                "\(size) falls through to .large"
            )
        }
    }

    func testTextStyleMappingIsOneToOne() {
        let pairs: [(Font.TextStyle, UIFont.TextStyle)] = [
            (.largeTitle, .largeTitle), (.title, .title1), (.title2, .title2), (.title3, .title3),
            (.headline, .headline), (.subheadline, .subheadline), (.body, .body),
            (.callout, .callout), (.footnote, .footnote), (.caption, .caption1), (.caption2, .caption2)
        ]
        for (swiftUI, uiKit) in pairs {
            XCTAssertEqual(swiftUI.uiKitTextStyle, uiKit)
        }
    }
#else
    private func naturalLineHeight(of style: LemonadeTextStyle) -> CGFloat {
        return style.fontSize * 1.20
    }

    func testLineSpacingIsUnchangedWithoutUIKit() {
        let style = typography.bodyMediumRegular
        XCTAssertEqual(style.lineSpacing(for: .accessibility5), style.lineSpacing, accuracy: 0.01)
    }
#endif
}
