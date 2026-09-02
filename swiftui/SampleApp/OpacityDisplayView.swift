import SwiftUI
import Lemonade

struct OpacityDisplayView: View {
    private let opacityTokens = LemonadeOpacityTokens()

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 24) {
                Text("Base Opacities")
                    .font(.headline)

                VStack(alignment: .leading, spacing: 12) {
                    ForEach(OpacityItem.items(reflecting: opacityTokens.base)) { item in
                        OpacityRow(item: item)
                    }
                }

                Text("State Opacities")
                    .font(.headline)
                    .padding(.top, 8)

                VStack(alignment: .leading, spacing: 12) {
                    ForEach(OpacityItem.items(reflecting: opacityTokens.state)) { item in
                        OpacityRow(item: item)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Opacity")
    }
}

private struct OpacityRow: View {
    let item: OpacityItem

    var body: some View {
        HStack(spacing: 16) {
            Text(item.name)
                .font(.caption)
                .frame(width: 120, alignment: .leading)

            Text(item.percentage)
                .font(.caption)
                .foregroundStyle(.content.contentSecondary)
                .frame(width: 50)

            Rectangle()
                .fill(.content.contentPositive.opacity(item.value))
                .frame(height: 40)
                .clipShape(RoundedRectangle(cornerRadius: 8))
        }
    }
}

private struct OpacityItem: Identifiable {
    let name: String
    let value: Double

    var id: String { name }

    var percentage: String {
        "\(Int((value * 100).rounded()))%"
    }

    /// Reads the names and values straight off the shipped token object, so the
    /// gallery can never claim an opacity the SDK does not have.
    static func items(reflecting tokens: Any) -> [OpacityItem] {
        Mirror(reflecting: tokens).children.compactMap { child in
            guard let name = child.label, let value = child.value as? Double else { return nil }
            return OpacityItem(name: name, value: value)
        }
    }
}

#Preview {
    NavigationStack {
        OpacityDisplayView()
    }
}
