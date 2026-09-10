import SwiftUI
import Lemonade

struct ThemedColorsDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 0) {
                ForEach(themedHues) { hue in
                    ColorSwatchSection(group: hue)
                }
            }
        }
        .background(.bg.bgDefault)
        .navigationTitle("Themed Colors")
    }
}

// MARK: - Themed Color Data

private func themedHue(title: String, name: String, color: ThemedPrimaryColor) -> ColorSwatchGroup {
    let subtle = "\(name).subtle"
    return ColorSwatchGroup(
        id: name,
        title: title,
        swatches: [
            ColorSwatch(path: name, name: "background", fill: color.background, label: color.onBackground),
            ColorSwatch(path: name, name: "border", fill: color.border, label: color.onBackground),
            ColorSwatch(path: name, name: "content", fill: color.content, label: color.contentInverse),
            ColorSwatch(path: name, name: "contentInverse", fill: color.contentInverse, label: color.onBackground),
            ColorSwatch(path: name, name: "onBackground", fill: color.onBackground, label: color.content),
            ColorSwatch(path: name, name: "backgroundHigh", fill: color.backgroundHigh, label: color.contentInverse),
            ColorSwatch(path: name, name: "onBackgroundHigh", fill: color.onBackgroundHigh, label: color.content),
            ColorSwatch(path: subtle, name: "background", fill: color.subtle.background, label: color.subtle.onBackground),
            ColorSwatch(path: subtle, name: "border", fill: color.subtle.border, label: color.subtle.onBackground),
            ColorSwatch(path: subtle, name: "onBackground", fill: color.subtle.onBackground, label: color.contentInverse),
        ]
    )
}

private let themedHues: [ColorSwatchGroup] = [
    themedHue(title: "Amber", name: "amber", color: LemonadeTheme.themed.amber),
    themedHue(title: "Blue", name: "blue", color: LemonadeTheme.themed.blue),
    themedHue(title: "Cyan", name: "cyan", color: LemonadeTheme.themed.cyan),
    themedHue(title: "Fuchsia", name: "fuchsia", color: LemonadeTheme.themed.fuchsia),
    themedHue(title: "Green", name: "green", color: LemonadeTheme.themed.green),
    themedHue(title: "Green Lime", name: "greenLime", color: LemonadeTheme.themed.greenLime),
    themedHue(title: "Indigo", name: "indigo", color: LemonadeTheme.themed.indigo),
    themedHue(title: "Neutral", name: "neutral", color: LemonadeTheme.themed.neutral),
    themedHue(title: "Orange", name: "orange", color: LemonadeTheme.themed.orange),
    themedHue(title: "Pink", name: "pink", color: LemonadeTheme.themed.pink),
    themedHue(title: "Purple", name: "purple", color: LemonadeTheme.themed.purple),
    themedHue(title: "Red", name: "red", color: LemonadeTheme.themed.red),
    themedHue(title: "Rose", name: "rose", color: LemonadeTheme.themed.rose),
    themedHue(title: "Teal", name: "teal", color: LemonadeTheme.themed.teal),
    themedHue(title: "Violet", name: "violet", color: LemonadeTheme.themed.violet),
    themedHue(title: "Yellow", name: "yellow", color: LemonadeTheme.themed.yellow),
    themedHue(title: "Yellow Lime", name: "yellowLime", color: LemonadeTheme.themed.yellowLime),
]

#Preview {
    NavigationStack {
        ThemedColorsDisplayView()
    }
}
