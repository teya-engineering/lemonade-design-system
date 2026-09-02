import SwiftUI
import Lemonade

/// Every typography token, paired with its camel-cased property name.
///
/// Discovered via `Mirror` on purpose: this is a design-system catalog, and a
/// hand-maintained list silently omits any token added to the library later —
/// exactly the drift that made the Opacity and BorderWidth galleries wrong.
/// Reflection is only affordable because this is a file-scope `let`, so it runs
/// once per process; it used to sit in an *instance* stored property and re-ran
/// on every construction of the view.
private let typographyTokens: [(name: String, style: LemonadeTextStyle)] = {
    Mirror(reflecting: LemonadeTypography.shared).children.compactMap { child in
        guard let name = child.label, let style = child.value as? LemonadeTextStyle else { return nil }
        return (name: name, style: style)
    }
}()

/// The grouped catalog rendered by ``TextDisplayView``.
///
/// A file-scope `let` is lazily initialized exactly once per process, so the
/// sorting and grouping below never runs again — it used to be an *instance*
/// stored property, i.e. it re-ran on every construction of the view.
private let categorizedTypographyStyles: [(category: String, groups: [[TypographyEntry]])] = {
    let entries = typographyTokens
        .map { TypographyEntry(label: $0.name.typographyDisplayLabel(), style: $0.style) }
        .sorted { $0.style.fontSize > $1.style.fontSize }

    let byCategory = Dictionary(grouping: entries) { $0.category }
    let categoryOrder = ["Display", "Heading", "Body"]
    let orderedCategories = byCategory.keys.sorted { a, b in
        let ia = categoryOrder.firstIndex(of: a) ?? categoryOrder.count
        let ib = categoryOrder.firstIndex(of: b) ?? categoryOrder.count
        return ia < ib
    }

    return orderedCategories.compactMap { category in
        guard let styles = byCategory[category] else { return nil }
        let groups = Dictionary(grouping: styles) { $0.subCategory ?? "" }
            .sorted { a, b in
                // preserve size-descending order by taking the max fontSize in each group
                let maxA = a.value.map(\.style.fontSize).max() ?? 0
                let maxB = b.value.map(\.style.fontSize).max() ?? 0
                return maxA > maxB
            }
            .map(\.value)
        return (category: category, groups: groups)
    }
}()

struct TextDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                ForEach(categorizedTypographyStyles, id: \.category) { section in
                    sectionView(title: section.category) {
                        VStack(alignment: .leading, spacing: 12) {
                            ForEach(section.groups.indices, id: \.self) { index in
                                if index > 0 { Divider() }
                                ForEach(section.groups[index]) { entry in
                                    LemonadeUi.Text(entry.label, textStyle: entry.style)
                                }
                            }
                        }
                    }
                }

                // Text Colors — not part of typography struct, kept manual
                sectionView(title: "Colors") {
                    VStack(alignment: .leading, spacing: 12) {
                        LemonadeUi.Text("Primary", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentPrimary)
                        LemonadeUi.Text("Secondary", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentSecondary)
                        LemonadeUi.Text("Tertiary", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentTertiary)
                        LemonadeUi.Text("Critical", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentCritical)
                        LemonadeUi.Text("Positive", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentPositive)
                        LemonadeUi.Text("Info", textStyle: LemonadeTypography.shared.bodyMediumRegular, color: LemonadeTheme.colors.content.contentInfo)
                    }
                }

                // Overflow — behavioural examples, kept manual
                sectionView(title: "Overflow") {
                    VStack(alignment: .leading, spacing: 12) {
                        LemonadeUi.Text(
                            "This is a very long text that will be truncated at the end with ellipsis because it exceeds the available width",
                            textStyle: LemonadeTypography.shared.bodyMediumRegular,
                            overflow: .tail,
                            maxLines: 1
                        )
                        LemonadeUi.Text(
                            "This text truncates in the middle when it exceeds the available width in the container",
                            textStyle: LemonadeTypography.shared.bodyMediumRegular,
                            overflow: .middle,
                            maxLines: 1
                        )
                        LemonadeUi.Text(
                            "This text allows multiple lines but is limited to 2 lines maximum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore.",
                            textStyle: LemonadeTypography.shared.bodyMediumRegular,
                            maxLines: 2
                        )
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Text")
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)
            content()
        }
    }
}

// MARK: - Helpers

private struct TypographyEntry: Identifiable {
    var id: String { label }
    let label: String
    let style: LemonadeTextStyle

    var category: String {
        label.components(separatedBy: " ").first ?? ""
    }

    // Returns nil for Display and Heading, which have no weight variants per size.
    var subCategory: String? {
        let parts = label.components(separatedBy: " ")
        return parts.count > 2 ? parts[1] : nil
    }
}

private extension String {
    /// Splits a camel-cased token name into words and capitalizes the first letter.
    ///
    /// `"bodyXSmallOverline"` -> `"Body XSmall Overline"`. A plain character scan;
    /// the equivalent `NSRegularExpression` (`([a-z])([A-Z0-9])`) had to be
    /// compiled on every call.
    func typographyDisplayLabel() -> String {
        var result = ""
        result.reserveCapacity(count + 4)

        var previousWasLowercase = false
        for character in self {
            let startsNewWord = previousWasLowercase
                && (character.isUppercase || character.isNumber)
            if startsNewWord {
                result.append(" ")
            }
            result.append(character)
            previousWasLowercase = character.isLowercase
        }

        return result.prefix(1).uppercased() + result.dropFirst()
    }
}

#Preview {
    NavigationStack {
        TextDisplayView()
    }
}
