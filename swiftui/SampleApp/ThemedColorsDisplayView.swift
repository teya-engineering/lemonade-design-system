import SwiftUI
import Lemonade

struct ThemedColorsDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 0) {
                ForEach(themedHues) { hue in
                    ThemedHueSection(hue: hue)
                }
            }
        }
        .background(.bg.bgDefault)
        .navigationTitle("Themed Colors")
    }
}

private struct ThemedHueSection: View {
    let hue: ThemedHue

    private let columns = Array(
        repeating: GridItem(.flexible(), spacing: LemonadeTheme.spaces.spacing200),
        count: 2
    )

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
            LemonadeUi.Text(
                hue.title,
                textStyle: LemonadeTypography.shared.headingXXSmall
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing100)

            LazyVGrid(columns: columns, spacing: LemonadeTheme.spaces.spacing200) {
                ForEach(hue.swatches) { swatch in
                    ThemedSwatchView(swatch: swatch)
                }
            }
        }
        .padding(LemonadeTheme.spaces.spacing400)
    }
}

private struct ThemedSwatchView: View {
    let swatch: ThemedSwatch

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.Text(
                swatch.path,
                textStyle: LemonadeTypography.shared.bodyXSmallRegular,
                color: swatch.label.opacity(LemonadeTheme.opacity.base.opacity70)
            )

            Spacer(minLength: 0)

            LemonadeUi.Text(
                swatch.slot,
                textStyle: LemonadeTypography.shared.bodyXSmallMedium,
                color: swatch.label
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
        .padding(.horizontal, LemonadeTheme.spaces.spacing400)
        .padding(.vertical, LemonadeTheme.spaces.spacing500)
        .frame(height: 162)
        .background(swatch.fill, in: LemonadeTheme.shapes.radius600)
    }
}

private struct ThemedSwatch: Identifiable {
    var id: String { "\(path).\(slot)" }
    let path: String
    let slot: String
    let fill: Color
    let label: Color
}

private struct ThemedHue: Identifiable {
    var id: String { title }
    let title: String
    let swatches: [ThemedSwatch]

    init(title: String, name: String, color: ThemedPrimaryColor) {
        let subtle = "\(name).subtle"
        self.title = title
        self.swatches = [
            ThemedSwatch(path: name, slot: "background", fill: color.background, label: color.onBackground),
            ThemedSwatch(path: name, slot: "border", fill: color.border, label: color.onBackground),
            ThemedSwatch(path: name, slot: "content", fill: color.content, label: color.contentInverse),
            ThemedSwatch(path: name, slot: "contentInverse", fill: color.contentInverse, label: color.onBackground),
            ThemedSwatch(path: name, slot: "onBackground", fill: color.onBackground, label: color.content),
            ThemedSwatch(path: name, slot: "backgroundHigh", fill: color.backgroundHigh, label: color.contentInverse),
            ThemedSwatch(path: name, slot: "onBackgroundHigh", fill: color.onBackgroundHigh, label: color.content),
            ThemedSwatch(path: subtle, slot: "background", fill: color.subtle.background, label: color.subtle.onBackground),
            ThemedSwatch(path: subtle, slot: "border", fill: color.subtle.border, label: color.subtle.onBackground),
            ThemedSwatch(path: subtle, slot: "onBackground", fill: color.subtle.onBackground, label: color.contentInverse),
        ]
    }
}

private let themedHues: [ThemedHue] = [
    ThemedHue(title: "Amber", name: "amber", color: LemonadeTheme.themed.amber),
    ThemedHue(title: "Blue", name: "blue", color: LemonadeTheme.themed.blue),
    ThemedHue(title: "Cyan", name: "cyan", color: LemonadeTheme.themed.cyan),
    ThemedHue(title: "Fuchsia", name: "fuchsia", color: LemonadeTheme.themed.fuchsia),
    ThemedHue(title: "Green", name: "green", color: LemonadeTheme.themed.green),
    ThemedHue(title: "Green Lime", name: "greenLime", color: LemonadeTheme.themed.greenLime),
    ThemedHue(title: "Indigo", name: "indigo", color: LemonadeTheme.themed.indigo),
    ThemedHue(title: "Neutral", name: "neutral", color: LemonadeTheme.themed.neutral),
    ThemedHue(title: "Orange", name: "orange", color: LemonadeTheme.themed.orange),
    ThemedHue(title: "Pink", name: "pink", color: LemonadeTheme.themed.pink),
    ThemedHue(title: "Purple", name: "purple", color: LemonadeTheme.themed.purple),
    ThemedHue(title: "Red", name: "red", color: LemonadeTheme.themed.red),
    ThemedHue(title: "Rose", name: "rose", color: LemonadeTheme.themed.rose),
    ThemedHue(title: "Teal", name: "teal", color: LemonadeTheme.themed.teal),
    ThemedHue(title: "Violet", name: "violet", color: LemonadeTheme.themed.violet),
    ThemedHue(title: "Yellow", name: "yellow", color: LemonadeTheme.themed.yellow),
    ThemedHue(title: "Yellow Lime", name: "yellowLime", color: LemonadeTheme.themed.yellowLime),
]

#Preview {
    NavigationStack {
        ThemedColorsDisplayView()
    }
}
