import SwiftUI
import UIKit
import Lemonade

struct SemanticColorsDisplayView: View {
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 0) {
                ForEach(semanticGroups) { group in
                    LemonadeUi.Text(
                        group.title,
                        textStyle: LemonadeTypography.shared.headingSmall
                    )
                    .padding(.horizontal, LemonadeTheme.spaces.spacing500)
                    .padding(.top, LemonadeTheme.spaces.spacing600)

                    ForEach(group.subgroups) { subgroup in
                        ColorSwatchSection(group: subgroup, outlined: true)
                    }
                }
            }
        }
        .background(.bg.bgDefault)
        .navigationTitle("Semantic Colors")
    }

    private var semanticGroups: [SemanticGroup] {
        let colors = LemonadeTheme.colors
        return [
            group(title: "Background", subgroups: backgroundTokens(colors.background)),
            group(title: "Border", subgroups: borderTokens(colors.border)),
            group(title: "Content", subgroups: contentTokens(colors.content)),
            group(title: "Interaction", subgroups: interactionTokens(colors.interaction)),
            group(title: "Scoped", subgroups: scopedTokens(colors.scoped)),
            group(title: "Shadow", subgroups: shadowTokens(colors.shadow)),
        ]
    }

    private func group(title: String, subgroups: [TokenSubgroup]) -> SemanticGroup {
        SemanticGroup(
            title: title,
            subgroups: subgroups.map { subgroup in
                let path = (subgroup.title ?? title).lowercased()
                return ColorSwatchGroup(
                    id: "\(title)/\(subgroup.title ?? "")",
                    title: subgroup.title,
                    swatches: subgroup.tokens.map { name, color in
                        ColorSwatch(path: path, name: name, fill: color, label: labelColor(for: color))
                    }
                )
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

private struct SemanticGroup: Identifiable {
    var id: String { title }
    let title: String
    let subgroups: [ColorSwatchGroup]
}

private typealias TokenSubgroup = (title: String?, tokens: [(String, Color)])

private func backgroundTokens(_ colors: BackgroundColors) -> [TokenSubgroup] {
    [
        (nil, [
            ("bgDefault", colors.bgDefault),
            ("bgSubtle", colors.bgSubtle),
            ("bgElevated", colors.bgElevated),
            ("bgElevatedHigh", colors.bgElevatedHigh),
        ]),
        ("Brand", [
            ("bgBrand", colors.bgBrand),
            ("bgBrandElevated", colors.bgBrandElevated),
            ("bgBrandSubtle", colors.bgBrandSubtle),
            ("bgBrandHigh", colors.bgBrandHigh),
        ]),
        ("Voice", [
            ("bgCritical", colors.bgCritical),
            ("bgCaution", colors.bgCaution),
            ("bgInfo", colors.bgInfo),
            ("bgPositive", colors.bgPositive),
            ("bgFeatured", colors.bgFeatured),
            ("bgNeutral", colors.bgNeutral),
            ("bgCriticalSubtle", colors.bgCriticalSubtle),
            ("bgCautionSubtle", colors.bgCautionSubtle),
            ("bgInfoSubtle", colors.bgInfoSubtle),
            ("bgPositiveSubtle", colors.bgPositiveSubtle),
            ("bgFeaturedSubtle", colors.bgFeaturedSubtle),
            ("bgNeutralSubtle", colors.bgNeutralSubtle),
        ]),
        ("Inverse", [
            ("bgDefaultInverse", colors.bgDefaultInverse),
            ("bgSubtleInverse", colors.bgSubtleInverse),
            ("bgElevatedInverse", colors.bgElevatedInverse),
        ]),
        ("Fixed", [
            ("bgAlwaysDark", colors.bgAlwaysDark),
            ("bgAlwaysDarkHigh", colors.bgAlwaysDarkHigh),
            ("bgAlwaysDarkMedium", colors.bgAlwaysDarkMedium),
            ("bgAlwaysDarkLow", colors.bgAlwaysDarkLow),
            ("bgAlwaysLight", colors.bgAlwaysLight),
            ("bgAlwaysLightHigh", colors.bgAlwaysLightHigh),
            ("bgAlwaysLightMedium", colors.bgAlwaysLightMedium),
            ("bgAlwaysLightLow", colors.bgAlwaysLightLow),
            ("bgTransparent", colors.bgTransparent),
            ("bgTransparentLight", colors.bgTransparentLight),
            ("bgTransparentDark", colors.bgTransparentDark),
        ]),
    ]
}

private func borderTokens(_ colors: BorderColors) -> [TokenSubgroup] {
    [
        (nil, [
            ("borderNeutralLow", colors.borderNeutralLow),
            ("borderNeutralMedium", colors.borderNeutralMedium),
            ("borderNeutralHigh", colors.borderNeutralHigh),
            ("borderSelected", colors.borderSelected),
        ]),
        ("Brand", [
            ("borderBrand", colors.borderBrand),
            ("borderOnBrandLow", colors.borderOnBrandLow),
            ("borderOnBrandMedium", colors.borderOnBrandMedium),
            ("borderOnBrandHigh", colors.borderOnBrandHigh),
        ]),
        ("Voice", [
            ("borderCritical", colors.borderCritical),
            ("borderCaution", colors.borderCaution),
            ("borderInfo", colors.borderInfo),
            ("borderPositive", colors.borderPositive),
            ("borderFeatured", colors.borderFeatured),
            ("borderCriticalSubtle", colors.borderCriticalSubtle),
            ("borderCautionSubtle", colors.borderCautionSubtle),
            ("borderInfoSubtle", colors.borderInfoSubtle),
            ("borderPositiveSubtle", colors.borderPositiveSubtle),
            ("borderFeaturedSubtle", colors.borderFeaturedSubtle),
        ]),
        ("Inverse", [
            ("borderNeutralLowInverse", colors.borderNeutralLowInverse),
            ("borderNeutralMediumInverse", colors.borderNeutralMediumInverse),
            ("borderNeutralHighInverse", colors.borderNeutralHighInverse),
            ("borderSelectedInverse", colors.borderSelectedInverse),
            ("borderBrandInverse", colors.borderBrandInverse),
        ]),
        ("Fixed", [
            ("borderAlwaysLight", colors.borderAlwaysLight),
            ("borderAlwaysLightLow", colors.borderAlwaysLightLow),
            ("borderAlwaysLightMedium", colors.borderAlwaysLightMedium),
            ("borderAlwaysLightHigh", colors.borderAlwaysLightHigh),
            ("borderAlwaysDark", colors.borderAlwaysDark),
            ("borderAlwaysDarkLow", colors.borderAlwaysDarkLow),
            ("borderAlwaysDarkMedium", colors.borderAlwaysDarkMedium),
            ("borderAlwaysDarkHigh", colors.borderAlwaysDarkHigh),
        ]),
    ]
}

private func contentTokens(_ colors: ContentColors) -> [TokenSubgroup] {
    [
        (nil, [
            ("contentPrimary", colors.contentPrimary),
            ("contentSecondary", colors.contentSecondary),
            ("contentTertiary", colors.contentTertiary),
        ]),
        ("Brand", [
            ("contentBrand", colors.contentBrand),
            ("contentBrandHigh", colors.contentBrandHigh),
            ("contentOnBrandHigh", colors.contentOnBrandHigh),
            ("contentOnBrandLow", colors.contentOnBrandLow),
        ]),
        ("Voice", [
            ("contentCritical", colors.contentCritical),
            ("contentCaution", colors.contentCaution),
            ("contentInfo", colors.contentInfo),
            ("contentPositive", colors.contentPositive),
            ("contentFeatured", colors.contentFeatured),
            ("contentNeutral", colors.contentNeutral),
        ]),
        ("Voice / On Color", [
            ("contentCriticalOnColor", colors.contentCriticalOnColor),
            ("contentCautionOnColor", colors.contentCautionOnColor),
            ("contentInfoOnColor", colors.contentInfoOnColor),
            ("contentPositiveOnColor", colors.contentPositiveOnColor),
            ("contentFeaturedOnColor", colors.contentFeaturedOnColor),
            ("contentNeutralOnColor", colors.contentNeutralOnColor),
        ]),
        ("Inverse", [
            ("contentPrimaryInverse", colors.contentPrimaryInverse),
            ("contentSecondaryInverse", colors.contentSecondaryInverse),
            ("contentTertiaryInverse", colors.contentTertiaryInverse),
            ("contentBrandInverse", colors.contentBrandInverse),
        ]),
        ("Fixed", [
            ("contentAlwaysLight", colors.contentAlwaysLight),
            ("contentAlwaysDark", colors.contentAlwaysDark),
            ("contentCriticalAlwaysOnColor", colors.contentCriticalAlwaysOnColor),
            ("contentCautionAlwaysOnColor", colors.contentCautionAlwaysOnColor),
            ("contentInfoAlwaysOnColor", colors.contentInfoAlwaysOnColor),
            ("contentPositiveAlwaysOnColor", colors.contentPositiveAlwaysOnColor),
            ("contentNeutralAlwaysOnColor", colors.contentNeutralAlwaysOnColor),
        ]),
    ]
}

private func interactionTokens(_ colors: InteractionColors) -> [TokenSubgroup] {
    [
        ("Interactive / Background", [
            ("bgDefaultInteractive", colors.bgDefaultInteractive),
            ("bgSubtleInteractive", colors.bgSubtleInteractive),
            ("bgElevatedInteractive", colors.bgElevatedInteractive),
            ("bgElevatedHighInteractive", colors.bgElevatedHighInteractive),
            ("bgBrandInteractive", colors.bgBrandInteractive),
            ("bgBrandElevatedInteractive", colors.bgBrandElevatedInteractive),
            ("bgBrandHighInteractive", colors.bgBrandHighInteractive),
            ("bgCriticalInteractive", colors.bgCriticalInteractive),
            ("bgCautionInteractive", colors.bgCautionInteractive),
            ("bgInfoInteractive", colors.bgInfoInteractive),
            ("bgPositiveInteractive", colors.bgPositiveInteractive),
            ("bgFeaturedInteractive", colors.bgFeaturedInteractive),
            ("bgNeutralInteractive", colors.bgNeutralInteractive),
            ("bgCriticalSubtleInteractive", colors.bgCriticalSubtleInteractive),
            ("bgCautionSubtleInteractive", colors.bgCautionSubtleInteractive),
            ("bgInfoSubtleInteractive", colors.bgInfoSubtleInteractive),
            ("bgPositiveSubtleInteractive", colors.bgPositiveSubtleInteractive),
            ("bgFeaturedSubtleInteractive", colors.bgFeaturedSubtleInteractive),
            ("bgNeutralSubtleInteractive", colors.bgNeutralSubtleInteractive),
            ("bgAlwaysDarkHighInteractive", colors.bgAlwaysDarkHighInteractive),
            ("bgAlwaysDarkMediumInteractive", colors.bgAlwaysDarkMediumInteractive),
            ("bgAlwaysDarkLowInteractive", colors.bgAlwaysDarkLowInteractive),
            ("bgAlwaysLightHighInteractive", colors.bgAlwaysLightHighInteractive),
            ("bgAlwaysLightMediumInteractive", colors.bgAlwaysLightMediumInteractive),
            ("bgAlwaysLightLowInteractive", colors.bgAlwaysLightLowInteractive),
        ]),
        ("Pressed / Background", [
            ("bgDefaultPressed", colors.bgDefaultPressed),
            ("bgSubtlePressed", colors.bgSubtlePressed),
            ("bgElevatedPressed", colors.bgElevatedPressed),
            ("bgBrandPressed", colors.bgBrandPressed),
            ("bgBrandElevatedPressed", colors.bgBrandElevatedPressed),
            ("bgBrandHighPressed", colors.bgBrandHighPressed),
            ("bgCriticalPressed", colors.bgCriticalPressed),
            ("bgCautionPressed", colors.bgCautionPressed),
            ("bgInfoPressed", colors.bgInfoPressed),
            ("bgPositivePressed", colors.bgPositivePressed),
            ("bgFeaturedPressed", colors.bgFeaturedPressed),
            ("bgNeutralPressed", colors.bgNeutralPressed),
            ("bgCriticalSubtlePressed", colors.bgCriticalSubtlePressed),
            ("bgCautionSubtlePressed", colors.bgCautionSubtlePressed),
            ("bgInfoSubtlePressed", colors.bgInfoSubtlePressed),
            ("bgPositiveSubtlePressed", colors.bgPositiveSubtlePressed),
            ("bgFeaturedSubtlePressed", colors.bgFeaturedSubtlePressed),
            ("bgNeutralSubtlePressed", colors.bgNeutralSubtlePressed),
        ]),
    ]
}

private func scopedTokens(_ colors: ScopedColors) -> [TokenSubgroup] {
    [
        ("Settlements", [
            ("bgSettlementInstant", colors.bgSettlementInstant),
            ("bgSettlementBusinessDays", colors.bgSettlementBusinessDays),
            ("bgSettlementEveryday", colors.bgSettlementEveryday),
            ("bgSettlementScheduled", colors.bgSettlementScheduled),
            ("contentOnSettlementInstant", colors.contentOnSettlementInstant),
            ("contentOnSettlementBusinessDays", colors.contentOnSettlementBusinessDays),
            ("contentOnSettlementEveryday", colors.contentOnSettlementEveryday),
            ("contentOnSettlementScheduled", colors.contentOnSettlementScheduled),
        ]),
    ]
}

private func shadowTokens(_ colors: ShadowColors) -> [TokenSubgroup] {
    [
        (nil, [
            ("shadowDefault", colors.shadowDefault),
        ]),
    ]
}

#Preview {
    NavigationStack {
        SemanticColorsDisplayView()
    }
}
