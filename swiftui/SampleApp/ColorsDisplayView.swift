import SwiftUI
import UIKit
import Lemonade

struct ColorsDisplayView: View {
    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 24) {
                ForEach(colorGroups, id: \.title) { group in
                    VStack(alignment: .leading, spacing: 8) {
                        Text(group.title)
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                            .textCase(.uppercase)

                        VStack(spacing: 0) {
                            ForEach(group.colors, id: \.name) { colorItem in
                                HStack {
                                    Text(colorItem.name)
                                        .font(.caption2)
                                        .fontWeight(.semibold)
                                        .foregroundStyle(textColor(for: colorItem.color))
                                    Spacer()
                                }
                                .padding(.horizontal, 12)
                                .frame(height: 40)
                                .background(colorItem.color)
                            }
                        }
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Semantic Colors")
    }

    /// Picks black or white for the swatch label, whichever has the higher WCAG contrast
    /// against the swatch. `.primary` flips with the colour scheme rather than with the
    /// swatch, so it disappears on any token whose lightness runs against the scheme.
    private func textColor(for backgroundColor: Color) -> Color {
        let traits = UITraitCollection(userInterfaceStyle: colorScheme == .dark ? .dark : .light)
        let swatch = UIColor(backgroundColor).resolvedColor(with: traits)
        let page = UIColor(LemonadeTheme.colors.background.bgDefault).resolvedColor(with: traits)

        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        guard swatch.getRed(&red, green: &green, blue: &blue, alpha: &alpha) else { return .primary }

        var pageRed: CGFloat = 0, pageGreen: CGFloat = 0, pageBlue: CGFloat = 0, pageAlpha: CGFloat = 0
        guard page.getRed(&pageRed, green: &pageGreen, blue: &pageBlue, alpha: &pageAlpha) else {
            return .primary
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
        return luminance > 0.179 ? .black : .white
    }

    private func linearised(_ channel: CGFloat) -> Double {
        let value = Double(channel)
        return value <= 0.03928 ? value / 12.92 : pow((value + 0.055) / 1.055, 2.4)
    }
}

// MARK: - Color Data

struct ColorItem: Identifiable {
    let id = UUID()
    let name: String
    let color: Color
}

struct ColorGroup: Identifiable {
    let id = UUID()
    let title: String
    let colors: [ColorItem]
}

private let colorGroups: [ColorGroup] = [
    ColorGroup(title: "Background", colors: [
        ColorItem(name: "bgDefault", color: .bg.bgDefault),
        ColorItem(name: "bgSubtle", color: .bg.bgSubtle),
        ColorItem(name: "bgElevated", color: .bg.bgElevated),
        ColorItem(name: "bgElevatedHigh", color: .bg.bgElevatedHigh),
        ColorItem(name: "bgBrand", color: .bg.bgBrand),
        ColorItem(name: "bgBrandHigh", color: .bg.bgBrandHigh),
        ColorItem(name: "bgBrandSubtle", color: .bg.bgBrandSubtle),
        ColorItem(name: "bgPositive", color: .bg.bgPositive),
        ColorItem(name: "bgPositiveSubtle", color: .bg.bgPositiveSubtle),
        ColorItem(name: "bgCritical", color: .bg.bgCritical),
        ColorItem(name: "bgCriticalSubtle", color: .bg.bgCriticalSubtle),
        ColorItem(name: "bgCaution", color: .bg.bgCaution),
        ColorItem(name: "bgCautionSubtle", color: .bg.bgCautionSubtle),
        ColorItem(name: "bgInfo", color: .bg.bgInfo),
        ColorItem(name: "bgInfoSubtle", color: .bg.bgInfoSubtle),
        ColorItem(name: "bgNeutral", color: .bg.bgNeutral),
        ColorItem(name: "bgNeutralSubtle", color: .bg.bgNeutralSubtle),
    ]),
    ColorGroup(title: "Content", colors: [
        ColorItem(name: "contentPrimary", color: .content.contentPrimary),
        ColorItem(name: "contentSecondary", color: .content.contentSecondary),
        ColorItem(name: "contentTertiary", color: .content.contentTertiary),
        ColorItem(name: "contentBrand", color: .content.contentBrand),
        ColorItem(name: "contentBrandHigh", color: .content.contentBrandHigh),
        ColorItem(name: "contentPositive", color: .content.contentPositive),
        ColorItem(name: "contentCritical", color: .content.contentCritical),
        ColorItem(name: "contentCaution", color: .content.contentCaution),
        ColorItem(name: "contentInfo", color: .content.contentInfo),
        ColorItem(name: "contentNeutral", color: .content.contentNeutral),
    ]),
    ColorGroup(title: "Border", colors: [
        ColorItem(name: "borderNeutralLow", color: .border.borderNeutralLow),
        ColorItem(name: "borderNeutralMedium", color: .border.borderNeutralMedium),
        ColorItem(name: "borderNeutralHigh", color: .border.borderNeutralHigh),
        ColorItem(name: "borderBrand", color: .border.borderBrand),
        ColorItem(name: "borderSelected", color: .border.borderSelected),
        ColorItem(name: "borderPositive", color: .border.borderPositive),
        ColorItem(name: "borderCritical", color: .border.borderCritical),
        ColorItem(name: "borderCaution", color: .border.borderCaution),
        ColorItem(name: "borderInfo", color: .border.borderInfo),
    ]),
]

#Preview {
    NavigationStack {
        ColorsDisplayView()
    }
}
