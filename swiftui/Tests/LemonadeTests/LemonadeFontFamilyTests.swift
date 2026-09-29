import SwiftUI
import XCTest
@testable import Lemonade

final class LemonadeFontFamilyTests: XCTestCase {
    private let brand = LemonadeFontFamily(
        regular: "Brand-Regular",
        medium: "Brand-Medium",
        semibold: "Brand-SemiBold"
    )

    func testFigtreeIsTheDefaultFamily() {
        let environment = EnvironmentValues()

        XCTAssertEqual(environment.lemonadeFontFamily, .figtree)
    }

    func testBoldResolvesToTheSemiboldFaceBecauseThereIsNoTrueBold() {
        XCTAssertEqual(brand.fontName(for: .regular), "Brand-Regular")
        XCTAssertEqual(brand.fontName(for: .medium), "Brand-Medium")
        XCTAssertEqual(brand.fontName(for: .semibold), "Brand-SemiBold")
        XCTAssertEqual(brand.fontName(for: .bold), "Brand-SemiBold")
        XCTAssertEqual(brand.fontName(for: .black), "Brand-Regular")
    }

    func testAFamilyRebuiltFromFigtreesOwnFaceNamesIsFigtree() {
        let rebuilt = LemonadeFontFamily(
            regular: "Figtree-Regular",
            medium: "Figtree-Medium",
            semibold: "Figtree-SemiBold"
        )

        XCTAssertEqual(rebuilt, .figtree)
    }

    // The stored `lineSpacing` and `font` exist so the default path costs a property load rather
    // than a CoreText lookup. The family-aware variants have to hand back exactly those.
    func testTheDefaultFamilyTakesTheStoredFastPath() {
        for style in Self.representativeStyles {
            XCTAssertEqual(style.lineSpacing(in: .figtree), style.lineSpacing)
            XCTAssertEqual(style.font(in: .figtree), style.font)
        }
    }

    func testAnUnregisteredFamilyStillYieldsANonNegativeLineSpacing() {
        for style in Self.representativeStyles {
            XCTAssertGreaterThanOrEqual(style.lineSpacing(in: brand), 0)
        }
    }

    func testMarkdownMarksOnlyTheSpansWhoseFaceCanBeSwapped() {
        let parsed = "plain **semi** __under__ ~~italic~~".toLemonadeMarkdown(baseFontSize: 16)

        let marked = parsed.runs.compactMap { run -> (String, LemonadeMarkdownFont)? in
            guard let asked = run.lemonadeMarkdownFont else { return nil }
            return (String(parsed[run.range].characters), asked)
        }

        XCTAssertEqual(marked.map(\.0), ["semi", "italic"])
        XCTAssertEqual(marked.map(\.1.markdown), [.semiBold, .italic])
        XCTAssertEqual(marked.map(\.1.baseFontSize), [16, 16])
    }

    func testMarkdownSpansCarryTheDesignSystemsOwnFaceUntilAViewSwapsIt() {
        let semiBold = LemonadeMarkdown.semiBold

        XCTAssertEqual(
            semiBold.font(in: .figtree, baseFontSize: 16),
            .custom("Figtree-SemiBold", size: 16, relativeTo: .body)
        )
        XCTAssertEqual(
            semiBold.font(in: brand, baseFontSize: 16),
            .custom("Brand-SemiBold", size: 16, relativeTo: .body)
        )
        XCTAssertNil(LemonadeMarkdown.underline.font(in: brand, baseFontSize: 16))
    }

    private static let representativeStyles: [LemonadeTextStyle] = [
        LemonadeTypography.shared.displayLarge,
        LemonadeTypography.shared.headingMedium,
        LemonadeTypography.shared.bodyMediumRegular,
        LemonadeTypography.shared.bodySmallSemiBold,
    ]
}
