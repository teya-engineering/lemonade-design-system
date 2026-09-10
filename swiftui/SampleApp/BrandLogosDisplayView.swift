import SwiftUI
import Lemonade

/// All brand logos, each paired with a pre-lowercased search haystack.
///
/// Lives at file scope so a keystroke filters against the prebuilt haystacks instead of
/// lowercasing every name again.
private let brandLogoIndex: [(logo: LemonadeBrandLogo, haystack: String)] =
    LemonadeBrandLogo.allCases.map { ($0, $0.rawValue.lowercased()) }

private let allBrandLogos: [LemonadeBrandLogo] = brandLogoIndex.map(\.logo)

private func filteredBrandLogos(matching searchText: String) -> [LemonadeBrandLogo] {
    guard !searchText.isEmpty else { return allBrandLogos }
    let query = searchText.lowercased()
    return brandLogoIndex.compactMap { $0.haystack.contains(query) ? $0.logo : nil }
}

struct BrandLogosDisplayView: View {
    @State private var searchText = ""

    private let columns = [
        GridItem(.adaptive(minimum: 100), spacing: 16)
    ]

    var body: some View {
        let logos = filteredBrandLogos(matching: searchText)

        return ScrollView {
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(logos, id: \.rawValue) { logo in
                    VStack(spacing: 8) {
                        LemonadeUi.BrandLogo(
                            logo: logo,
                            size: .xxLarge
                        )

                        Text(logo.rawValue)
                            .font(.system(size: 10))
                            .foregroundStyle(.content.contentSecondary)
                            .lineLimit(2)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 80)
                    .background(.bg.bgSubtle)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
            }
            .padding()
        }
        .searchable(text: $searchText, prompt: "Search brand logos")
        .navigationTitle("Brand Logos (\(logos.count))")
    }
}

#Preview {
    NavigationStack {
        BrandLogosDisplayView()
    }
}
