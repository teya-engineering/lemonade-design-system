import SwiftUI
import Lemonade

struct SizesDisplayView: View {
    private let sizeValues = LemonadeSizeValuesImpl()

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 16) {
                ForEach(SizeItem.items(reflecting: sizeValues)) { item in
                    HStack(spacing: 16) {
                        Text(item.name)
                            .font(.caption)
                            .frame(width: 80, alignment: .leading)

                        Text(item.measurement)
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                            .frame(width: 50)

                        Circle()
                            .fill(.content.contentBrand)
                            .frame(width: min(item.value, 100), height: min(item.value, 100))

                        Spacer()
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Sizes")
    }
}

private struct SizeItem: Identifiable {
    let name: String
    let value: CGFloat

    var id: String { name }

    var measurement: String {
        value == value.rounded() ? "\(Int(value))pt" : String(format: "%gpt", Double(value))
    }

    /// Reads the names and values straight off the shipped token object, so the
    /// gallery always lists every `LemonadeSizes` case, not a hand-picked subset.
    static func items(reflecting tokens: Any) -> [SizeItem] {
        Mirror(reflecting: tokens).children.compactMap { child in
            guard let name = child.label, let value = child.value as? CGFloat else { return nil }
            return SizeItem(name: name, value: value)
        }
    }
}

#Preview {
    NavigationStack {
        SizesDisplayView()
    }
}
