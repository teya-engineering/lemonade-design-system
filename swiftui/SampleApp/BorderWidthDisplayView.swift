import SwiftUI
import Lemonade

struct BorderWidthDisplayView: View {
    private let borderWidthTokens = LemonadeBorderWidthTokens()

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 24) {
                Text("Base Border Widths")
                    .font(.headline)

                ForEach(BorderWidthItem.items(reflecting: borderWidthTokens.base)) { item in
                    BorderWidthRow(item: item)
                }

                Text("State Border Widths")
                    .font(.headline)
                    .padding(.top, 8)

                ForEach(BorderWidthItem.items(reflecting: borderWidthTokens.state)) { item in
                    BorderWidthRow(item: item)
                }
            }
            .padding()
        }
        .navigationTitle("Border Width")
    }
}

private struct BorderWidthRow: View {
    let item: BorderWidthItem

    var body: some View {
        HStack(spacing: 16) {
            Text(item.name)
                .font(.caption)
                .frame(width: 120, alignment: .leading)

            Text(item.measurement)
                .font(.caption)
                .foregroundStyle(.content.contentSecondary)
                .frame(width: 50)

            RoundedRectangle(cornerRadius: 8)
                .stroke(.content.contentInfo, lineWidth: item.value)
                .frame(width: 80, height: 60)

            Spacer()
        }
    }
}

private struct BorderWidthItem: Identifiable {
    let name: String
    let value: CGFloat

    var id: String { name }

    /// Fractional tokens such as `border40` (1.5) must not be rounded away.
    var measurement: String {
        value == value.rounded() ? "\(Int(value))pt" : String(format: "%gpt", Double(value))
    }

    /// Reads the names and values straight off the shipped token object, so the
    /// gallery can never claim a border width the SDK does not have.
    static func items(reflecting tokens: Any) -> [BorderWidthItem] {
        Mirror(reflecting: tokens).children.compactMap { child in
            guard let name = child.label, let value = child.value as? CGFloat else { return nil }
            return BorderWidthItem(name: name, value: value)
        }
    }
}

#Preview {
    NavigationStack {
        BorderWidthDisplayView()
    }
}
