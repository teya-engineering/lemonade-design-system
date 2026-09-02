import SwiftUI
import Lemonade

struct RadiusDisplayView: View {
    private let radiusValues = LemonadeRadiusValuesImpl()

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 24) {
                ForEach(RadiusItem.items(reflecting: radiusValues)) { item in
                    RadiusRow(item: item)
                }

                Text("Semantic")
                    .font(.headline)
                    .padding(.top, 8)

                ForEach(RadiusItem.items(reflecting: radiusValues.semantic)) { item in
                    RadiusRow(item: item)
                }
            }
            .padding()
        }
        .navigationTitle("Radius")
    }
}

private struct RadiusRow: View {
    let item: RadiusItem

    var body: some View {
        HStack(spacing: 16) {
            Text(item.name)
                .font(.caption)
                .frame(width: 100, alignment: .leading)
                .minimumScaleFactor(0.8)

            Text(item.measurement)
                .font(.caption)
                .foregroundStyle(.content.contentSecondary)
                .frame(width: 50)

            RoundedRectangle(cornerRadius: item.value)
                .fill(.content.contentInfo)
                .frame(width: 80, height: 80)

            Spacer()
        }
    }
}

private struct RadiusItem: Identifiable {
    let name: String
    let value: CGFloat

    var id: String { name }

    var measurement: String {
        value == value.rounded() ? "\(Int(value))pt" : String(format: "%gpt", Double(value))
    }

    /// Reads the names and values straight off the shipped token object, so the
    /// gallery always lists every `LemonadeRadius` case, not a hand-picked subset.
    static func items(reflecting tokens: Any) -> [RadiusItem] {
        Mirror(reflecting: tokens).children.compactMap { child in
            guard let name = child.label, let value = child.value as? CGFloat else { return nil }
            return RadiusItem(name: name, value: value)
        }
    }
}

#Preview {
    NavigationStack {
        RadiusDisplayView()
    }
}
