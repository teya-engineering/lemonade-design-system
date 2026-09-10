import SwiftUI
import UIKit
import Lemonade

struct ColorsDisplayView: View {
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 0) {
                ForEach(semanticGroups) { group in
                    ColorSwatchSection(group: group, outlined: true)
                }
            }
        }
        .background(.bg.bgDefault)
        .navigationTitle("Semantic Colors")
    }

    private var semanticGroups: [ColorSwatchGroup] {
        let colors = LemonadeTheme.colors
        return [
            group(title: "Background", tokens: backgroundTokens(colors.background)),
            group(title: "Border", tokens: borderTokens(colors.border)),
            group(title: "Content", tokens: contentTokens(colors.content)),
            group(title: "Interaction", tokens: interactionTokens(colors.interaction)),
            group(title: "Scoped", tokens: scopedTokens(colors.scoped)),
            group(title: "Shadow", tokens: shadowTokens(colors.shadow)),
        ]
    }

    private func group(title: String, tokens: [(String, Color)]) -> ColorSwatchGroup {
        ColorSwatchGroup(
            title: title,
            swatches: tokens.map { name, color in
                ColorSwatch(path: title.lowercased(), name: name, fill: color, label: labelColor(for: color))
            }
        )
    }

    /// Picks whichever of the always-dark and always-light content tokens has the higher
    /// WCAG contrast against the swatch. A scheme-relative token flips with the colour
    /// scheme rather than with the swatch, so it disappears on any token whose lightness
    /// runs against the scheme.
    private func labelColor(for backgroundColor: Color) -> Color {
        let content = LemonadeTheme.colors.content
        let traits = UITraitCollection(userInterfaceStyle: colorScheme == .dark ? .dark : .light)
        let swatch = UIColor(backgroundColor).resolvedColor(with: traits)
        let page = UIColor(LemonadeTheme.colors.background.bgDefault).resolvedColor(with: traits)

        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        guard swatch.getRed(&red, green: &green, blue: &blue, alpha: &alpha) else { return content.contentPrimary }

        var pageRed: CGFloat = 0, pageGreen: CGFloat = 0, pageBlue: CGFloat = 0, pageAlpha: CGFloat = 0
        guard page.getRed(&pageRed, green: &pageGreen, blue: &pageBlue, alpha: &pageAlpha) else {
            return content.contentPrimary
        }

        // Translucent tokens are drawn over the page background, so the luminance that
        // matters is the composite, not the token's own.
        let composited = (
            red: red * alpha + pageRed * (1 - alpha),
            green: green * alpha + pageGreen * (1 - alpha),
            blue: blue * alpha + pageBlue * (1 - alpha)
        )

        let luminance = 0.2126 * linearised(composited.red)
            + 0.7152 * linearised(composited.green)
            + 0.0722 * linearised(composited.blue)

        // 0.179 is where contrast against black overtakes contrast against white.
        return luminance > 0.179 ? content.contentAlwaysDark : content.contentAlwaysLight
    }

    private func linearised(_ channel: CGFloat) -> Double {
        let value = Double(channel)
        return value <= 0.03928 ? value / 12.92 : pow((value + 0.055) / 1.055, 2.4)
    }
}

// MARK: - Semantic Color Data

private func backgroundTokens(_ colors: BackgroundColors) -> [(String, Color)] {
    [
        ("bgBrand", colors.bgBrand),
        ("bgBrandElevated", colors.bgBrandElevated),
        ("bgBrandHigh", colors.bgBrandHigh),
        ("bgBrandSubtle", colors.bgBrandSubtle),
        ("bgAlwaysDark", colors.bgAlwaysDark),
        ("bgAlwaysDarkHigh", colors.bgAlwaysDarkHigh),
        ("bgAlwaysDarkLow", colors.bgAlwaysDarkLow),
        ("bgAlwaysDarkMedium", colors.bgAlwaysDarkMedium),
        ("bgAlwaysLight", colors.bgAlwaysLight),
        ("bgAlwaysLightHigh", colors.bgAlwaysLightHigh),
        ("bgAlwaysLightLow", colors.bgAlwaysLightLow),
        ("bgAlwaysLightMedium", colors.bgAlwaysLightMedium),
        ("bgTransparent", colors.bgTransparent),
        ("bgTransparentDark", colors.bgTransparentDark),
        ("bgTransparentLight", colors.bgTransparentLight),
        ("bgDefaultInverse", colors.bgDefaultInverse),
        ("bgElevatedInverse", colors.bgElevatedInverse),
        ("bgSubtleInverse", colors.bgSubtleInverse),
        ("bgCaution", colors.bgCaution),
        ("bgCautionSubtle", colors.bgCautionSubtle),
        ("bgCritical", colors.bgCritical),
        ("bgCriticalSubtle", colors.bgCriticalSubtle),
        ("bgFeatured", colors.bgFeatured),
        ("bgFeaturedSubtle", colors.bgFeaturedSubtle),
        ("bgInfo", colors.bgInfo),
        ("bgInfoSubtle", colors.bgInfoSubtle),
        ("bgNeutral", colors.bgNeutral),
        ("bgNeutralSubtle", colors.bgNeutralSubtle),
        ("bgPositive", colors.bgPositive),
        ("bgPositiveSubtle", colors.bgPositiveSubtle),
        ("bgDefault", colors.bgDefault),
        ("bgElevated", colors.bgElevated),
        ("bgElevatedHigh", colors.bgElevatedHigh),
        ("bgSubtle", colors.bgSubtle),
    ]
}

private func borderTokens(_ colors: BorderColors) -> [(String, Color)] {
    [
        ("borderBrand", colors.borderBrand),
        ("borderOnBrandHigh", colors.borderOnBrandHigh),
        ("borderOnBrandLow", colors.borderOnBrandLow),
        ("borderOnBrandMedium", colors.borderOnBrandMedium),
        ("borderAlwaysDark", colors.borderAlwaysDark),
        ("borderAlwaysDarkHigh", colors.borderAlwaysDarkHigh),
        ("borderAlwaysDarkLow", colors.borderAlwaysDarkLow),
        ("borderAlwaysDarkMedium", colors.borderAlwaysDarkMedium),
        ("borderAlwaysLight", colors.borderAlwaysLight),
        ("borderAlwaysLightHigh", colors.borderAlwaysLightHigh),
        ("borderAlwaysLightLow", colors.borderAlwaysLightLow),
        ("borderAlwaysLightMedium", colors.borderAlwaysLightMedium),
        ("borderBrandInverse", colors.borderBrandInverse),
        ("borderNeutralHighInverse", colors.borderNeutralHighInverse),
        ("borderNeutralLowInverse", colors.borderNeutralLowInverse),
        ("borderNeutralMediumInverse", colors.borderNeutralMediumInverse),
        ("borderSelectedInverse", colors.borderSelectedInverse),
        ("borderCaution", colors.borderCaution),
        ("borderCautionSubtle", colors.borderCautionSubtle),
        ("borderCritical", colors.borderCritical),
        ("borderCriticalSubtle", colors.borderCriticalSubtle),
        ("borderFeatured", colors.borderFeatured),
        ("borderFeaturedSubtle", colors.borderFeaturedSubtle),
        ("borderInfo", colors.borderInfo),
        ("borderInfoSubtle", colors.borderInfoSubtle),
        ("borderPositive", colors.borderPositive),
        ("borderPositiveSubtle", colors.borderPositiveSubtle),
        ("borderNeutralHigh", colors.borderNeutralHigh),
        ("borderNeutralLow", colors.borderNeutralLow),
        ("borderNeutralMedium", colors.borderNeutralMedium),
        ("borderSelected", colors.borderSelected),
    ]
}

private func contentTokens(_ colors: ContentColors) -> [(String, Color)] {
    [
        ("contentBrand", colors.contentBrand),
        ("contentBrandHigh", colors.contentBrandHigh),
        ("contentOnBrandHigh", colors.contentOnBrandHigh),
        ("contentOnBrandLow", colors.contentOnBrandLow),
        ("contentAlwaysDark", colors.contentAlwaysDark),
        ("contentAlwaysLight", colors.contentAlwaysLight),
        ("contentCautionAlwaysOnColor", colors.contentCautionAlwaysOnColor),
        ("contentCriticalAlwaysOnColor", colors.contentCriticalAlwaysOnColor),
        ("contentInfoAlwaysOnColor", colors.contentInfoAlwaysOnColor),
        ("contentNeutralAlwaysOnColor", colors.contentNeutralAlwaysOnColor),
        ("contentPositiveAlwaysOnColor", colors.contentPositiveAlwaysOnColor),
        ("contentBrandInverse", colors.contentBrandInverse),
        ("contentPrimaryInverse", colors.contentPrimaryInverse),
        ("contentSecondaryInverse", colors.contentSecondaryInverse),
        ("contentTertiaryInverse", colors.contentTertiaryInverse),
        ("contentCautionOnColor", colors.contentCautionOnColor),
        ("contentCriticalOnColor", colors.contentCriticalOnColor),
        ("contentFeaturedOnColor", colors.contentFeaturedOnColor),
        ("contentInfoOnColor", colors.contentInfoOnColor),
        ("contentNeutralOnColor", colors.contentNeutralOnColor),
        ("contentPositiveOnColor", colors.contentPositiveOnColor),
        ("contentCaution", colors.contentCaution),
        ("contentCritical", colors.contentCritical),
        ("contentFeatured", colors.contentFeatured),
        ("contentInfo", colors.contentInfo),
        ("contentNeutral", colors.contentNeutral),
        ("contentPositive", colors.contentPositive),
        ("contentPrimary", colors.contentPrimary),
        ("contentSecondary", colors.contentSecondary),
        ("contentTertiary", colors.contentTertiary),
    ]
}

private func interactionTokens(_ colors: InteractionColors) -> [(String, Color)] {
    [
        ("bgAlwaysDarkHighInteractive", colors.bgAlwaysDarkHighInteractive),
        ("bgAlwaysDarkLowInteractive", colors.bgAlwaysDarkLowInteractive),
        ("bgAlwaysDarkMediumInteractive", colors.bgAlwaysDarkMediumInteractive),
        ("bgAlwaysLightHighInteractive", colors.bgAlwaysLightHighInteractive),
        ("bgAlwaysLightLowInteractive", colors.bgAlwaysLightLowInteractive),
        ("bgAlwaysLightMediumInteractive", colors.bgAlwaysLightMediumInteractive),
        ("bgBrandElevatedInteractive", colors.bgBrandElevatedInteractive),
        ("bgBrandHighInteractive", colors.bgBrandHighInteractive),
        ("bgBrandInteractive", colors.bgBrandInteractive),
        ("bgCautionInteractive", colors.bgCautionInteractive),
        ("bgCautionSubtleInteractive", colors.bgCautionSubtleInteractive),
        ("bgCriticalInteractive", colors.bgCriticalInteractive),
        ("bgCriticalSubtleInteractive", colors.bgCriticalSubtleInteractive),
        ("bgDefaultInteractive", colors.bgDefaultInteractive),
        ("bgElevatedHighInteractive", colors.bgElevatedHighInteractive),
        ("bgElevatedInteractive", colors.bgElevatedInteractive),
        ("bgFeaturedInteractive", colors.bgFeaturedInteractive),
        ("bgFeaturedSubtleInteractive", colors.bgFeaturedSubtleInteractive),
        ("bgInfoInteractive", colors.bgInfoInteractive),
        ("bgInfoSubtleInteractive", colors.bgInfoSubtleInteractive),
        ("bgNeutralInteractive", colors.bgNeutralInteractive),
        ("bgNeutralSubtleInteractive", colors.bgNeutralSubtleInteractive),
        ("bgPositiveInteractive", colors.bgPositiveInteractive),
        ("bgPositiveSubtleInteractive", colors.bgPositiveSubtleInteractive),
        ("bgSubtleInteractive", colors.bgSubtleInteractive),
        ("bgBrandElevatedPressed", colors.bgBrandElevatedPressed),
        ("bgBrandHighPressed", colors.bgBrandHighPressed),
        ("bgBrandPressed", colors.bgBrandPressed),
        ("bgCautionPressed", colors.bgCautionPressed),
        ("bgCautionSubtlePressed", colors.bgCautionSubtlePressed),
        ("bgCriticalPressed", colors.bgCriticalPressed),
        ("bgCriticalSubtlePressed", colors.bgCriticalSubtlePressed),
        ("bgDefaultPressed", colors.bgDefaultPressed),
        ("bgElevatedPressed", colors.bgElevatedPressed),
        ("bgFeaturedPressed", colors.bgFeaturedPressed),
        ("bgFeaturedSubtlePressed", colors.bgFeaturedSubtlePressed),
        ("bgInfoPressed", colors.bgInfoPressed),
        ("bgInfoSubtlePressed", colors.bgInfoSubtlePressed),
        ("bgNeutralPressed", colors.bgNeutralPressed),
        ("bgNeutralSubtlePressed", colors.bgNeutralSubtlePressed),
        ("bgPositivePressed", colors.bgPositivePressed),
        ("bgPositiveSubtlePressed", colors.bgPositiveSubtlePressed),
        ("bgSubtlePressed", colors.bgSubtlePressed),
    ]
}

private func scopedTokens(_ colors: ScopedColors) -> [(String, Color)] {
    [
        ("bgSettlementBusinessDays", colors.bgSettlementBusinessDays),
        ("bgSettlementEveryday", colors.bgSettlementEveryday),
        ("bgSettlementInstant", colors.bgSettlementInstant),
        ("bgSettlementScheduled", colors.bgSettlementScheduled),
        ("contentOnSettlementBusinessDays", colors.contentOnSettlementBusinessDays),
        ("contentOnSettlementEveryday", colors.contentOnSettlementEveryday),
        ("contentOnSettlementInstant", colors.contentOnSettlementInstant),
        ("contentOnSettlementScheduled", colors.contentOnSettlementScheduled),
    ]
}

private func shadowTokens(_ colors: ShadowColors) -> [(String, Color)] {
    [
        ("shadowDefault", colors.shadowDefault),
    ]
}

#Preview {
    NavigationStack {
        ColorsDisplayView()
    }
}
