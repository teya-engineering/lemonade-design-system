import SwiftUI
import Lemonade

/// A country flag with its display strings and search haystack resolved up front.
///
/// The code, name and haystack are derived values, and resolving them once here keeps that
/// work off every keystroke and off every visible cell.
private struct FlagEntry: Identifiable {
    let flag: LemonadeCountryFlag
    let code: String
    let name: String
    let haystack: String

    var id: String { flag.rawValue }
}

private let flagIndex: [FlagEntry] = LemonadeCountryFlag.allCases.map { flag in
    let code = flag.countryCode
    let name = flag.countryName
    return FlagEntry(
        flag: flag,
        code: code,
        name: name,
        haystack: "\(flag.rawValue)\n\(code)\n\(name)".lowercased()
    )
}

private func filteredFlags(matching searchText: String) -> [FlagEntry] {
    guard !searchText.isEmpty else { return flagIndex }
    let query = searchText.lowercased()
    return flagIndex.filter { $0.haystack.contains(query) }
}

struct FlagsDisplayView: View {
    @State private var searchText = ""
    @State private var shape: LemonadeCountryFlagShape = .circular

    private let columns = [
        GridItem(.adaptive(minimum: 80), spacing: 12)
    ]

    private let sizes: [(size: LemonadeCountryFlagSize, label: String)] = [
        (.small, "S"),
        (.medium, "M"),
        (.large, "L"),
        (.xLarge, "XL"),
        (.xxLarge, "2XL"),
        (.xxxLarge, "3XL"),
        (.xxxxLarge, "4XL")
    ]

    private var allSizesRow: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("All sizes")
                .font(.system(size: 11, weight: .semibold))
                .foregroundStyle(.content.contentSecondary)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .bottom, spacing: 12) {
                    ForEach(sizes, id: \.size) { item in
                        VStack(spacing: 4) {
                            LemonadeUi.CountryFlag(
                                flag: .gBUnitedKingdom,
                                size: item.size,
                                shape: shape
                            )

                            Text(item.label)
                                .font(.system(size: 9))
                                .foregroundStyle(.content.contentSecondary)
                        }
                    }
                }
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(.bg.bgSubtle)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }

    var body: some View {
        let flags = filteredFlags(matching: searchText)

        return VStack(spacing: 0) {
            Picker("Shape", selection: $shape) {
                Text("Circular").tag(LemonadeCountryFlagShape.circular)
                Text("Rounded").tag(LemonadeCountryFlagShape.rounded)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)
            .padding(.top)

            ScrollView {
                allSizesRow
                    .padding(.horizontal)
                    .padding(.top)

                LazyVGrid(columns: columns, spacing: 12) {
                    ForEach(flags) { entry in
                        VStack(spacing: 6) {
                            LemonadeUi.CountryFlag(
                                flag: entry.flag,
                                size: .xxLarge,
                                shape: shape
                            )

                            Text(entry.code)
                                .font(.system(size: 11, weight: .semibold))
                                .foregroundStyle(.primary)

                            Text(entry.name)
                                .font(.system(size: 8))
                                .foregroundStyle(.content.contentSecondary)
                                .lineLimit(2)
                                .multilineTextAlignment(.center)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 90)
                        .background(.bg.bgSubtle)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                }
                .padding()
            }
        }
        .searchable(text: $searchText, prompt: "Search by code or country name")
        .navigationTitle("Country Flags (\(flags.count))")
    }
}

#Preview {
    NavigationStack {
        FlagsDisplayView()
    }
}
