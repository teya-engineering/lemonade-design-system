import SwiftUI
import Lemonade

struct ColorSwatch: Identifiable {
    var id: String { "\(path).\(name)" }
    let path: String
    let name: String
    let fill: Color
    let label: Color
}

struct ColorSwatchGroup: Identifiable {
    var id: String { title }
    let title: String
    let swatches: [ColorSwatch]
}

struct ColorSwatchSection: View {
    let group: ColorSwatchGroup

    private let columns = [
        GridItem(.flexible(), spacing: LemonadeTheme.spaces.spacing200),
        GridItem(.flexible(), spacing: LemonadeTheme.spaces.spacing200),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
            LemonadeUi.Text(
                group.title,
                textStyle: LemonadeTypography.shared.headingXXSmall
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing100)

            LazyVGrid(columns: columns, spacing: LemonadeTheme.spaces.spacing200) {
                ForEach(group.swatches) { swatch in
                    ColorSwatchView(swatch: swatch)
                }
            }
        }
        .padding(LemonadeTheme.spaces.spacing400)
    }
}

private struct ColorSwatchView: View {
    let swatch: ColorSwatch

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.Text(
                swatch.path,
                textStyle: LemonadeTypography.shared.bodyXSmallRegular,
                color: swatch.label
            )
            .opacity(LemonadeTheme.opacity.base.opacity70)

            Spacer(minLength: 0)

            LemonadeUi.Text(
                swatch.name,
                textStyle: LemonadeTypography.shared.bodyXSmallMedium,
                color: swatch.label
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
        .padding(.horizontal, LemonadeTheme.spaces.spacing400)
        .padding(.vertical, LemonadeTheme.spaces.spacing500)
        .frame(height: 162)
        .background(swatch.fill)
        .clipShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius600))
    }
}
