import SwiftUI
import Lemonade

struct ColorSwatch: Identifiable {
    var id: String { "\(path).\(name)" }
    let path: String
    let name: String
    let fill: Color
    let label: Color
}

/// Titles repeat across a page (every semantic group has a "Brand"), and a lazy stack
/// flattens nested `ForEach`es into one list, so the id must be unique page-wide.
struct ColorSwatchGroup: Identifiable {
    let id: String
    let title: String?
    let swatches: [ColorSwatch]
}

struct ColorSwatchSection: View {
    let group: ColorSwatchGroup
    var outlined: Bool = false

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: LemonadeTheme.spaces.spacing200),
        count: 2
    )

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
            if let title = group.title {
                LemonadeUi.Text(
                    title,
                    textStyle: LemonadeTypography.shared.headingXXSmall
                )
                .padding(.horizontal, LemonadeTheme.spaces.spacing100)
            }

            LazyVGrid(columns: columns, spacing: LemonadeTheme.spaces.spacing200) {
                ForEach(group.swatches) { swatch in
                    ColorSwatchView(swatch: swatch, outlined: outlined)
                }
            }
        }
        .padding(LemonadeTheme.spaces.spacing400)
    }
}

private struct ColorSwatchView: View {
    let swatch: ColorSwatch
    let outlined: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.Text(
                swatch.path,
                textStyle: LemonadeTypography.shared.bodyXSmallRegular,
                color: swatch.label.opacity(LemonadeTheme.opacity.base.opacity70)
            )

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
        .background(swatch.fill, in: LemonadeTheme.shapes.radius600)
        .overlay {
            if outlined {
                LemonadeTheme.shapes.radius600.strokeBorder(.border.borderNeutralLow, lineWidth: LemonadeTheme.borderWidth.base.border25)
            }
        }
    }
}
