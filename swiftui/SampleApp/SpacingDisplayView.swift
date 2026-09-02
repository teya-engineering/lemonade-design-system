import SwiftUI
import Lemonade

struct SpacingDisplayView: View {
    private let spaceValues = LemonadeSpaceValuesImpl()

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 16) {
                ForEach(SpacingItem.items(reflecting: spaceValues)) { item in
                    HStack(spacing: 16) {
                        Text(item.name)
                            .font(.caption)
                            .frame(width: 100, alignment: .leading)

                        Text(item.measurement)
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                            .frame(width: 50)

                        Rectangle()
                            .fill(.content.contentPositive)
                            .frame(width: item.value, height: 24)
                            .clipShape(RoundedRectangle(cornerRadius: 4))

                        Spacer()
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Spacing")
    }
}

private struct SpacingItem: Identifiable {
    let name: String
    let value: CGFloat

    var id: String { name }

    var measurement: String {
        value == value.rounded() ? "\(Int(value))pt" : String(format: "%gpt", Double(value))
    }

    /// Reads the names and values straight off the shipped token object, so the
    /// gallery always lists every `LemonadeSpacing` case, not a hand-picked subset.
    static func items(reflecting tokens: Any) -> [SpacingItem] {
        Mirror(reflecting: tokens).children.compactMap { child in
            guard let name = child.label, let value = child.value as? CGFloat else { return nil }
            return SpacingItem(name: name, value: value)
        }
    }
}

#Preview {
    NavigationStack {
        SpacingDisplayView()
    }
}
