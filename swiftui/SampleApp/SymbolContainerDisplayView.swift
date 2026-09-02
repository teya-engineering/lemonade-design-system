import SwiftUI
import Lemonade

struct SymbolContainerDisplayView: View {
    private let allSizes: [(SymbolContainerSize, String)] = [
        (.xSmall, "XSmall"),
        (.small, "Small"),
        (.medium, "Medium"),
        (.large, "Large"),
        (.xLarge, "XLarge"),
        (.xxLarge, "XXLarge"),
    ]

    private let allVoices: [(SymbolContainerVoice, String, LemonadeIcon)] = [
        (.neutral, "Neutral", .heart),
        (.critical, "Critical", .circleX),
        (.warning, "Warning", .triangleAlert),
        (.info, "Info", .circleInfo),
        (.positive, "Positive", .circleCheck),
        (.brand, "Brand", .star),
        (.brandSubtle, "Brand Subtle", .star),
    ]

    private let allShapes: [(SymbolContainerShape, String)] = [
        (.circle, "Circle"),
        (.rounded, "Rounded"),
    ]

    private let badgeSizes: [(SymbolContainerSize, String)] = [
        (.small, "Small"),
        (.medium, "Medium"),
        (.large, "Large"),
        (.xLarge, "XLarge"),
    ]

    private let imageSizes: [(SymbolContainerSize, String)] = [
        (.small, "Small"),
        (.medium, "Medium"),
        (.large, "Large"),
        (.xLarge, "XLarge"),
        (.xxLarge, "XXLarge"),
    ]

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sizesSection
                voicesSection
                shapesSection
                badgeSection
                imageSection
                textSection
                customContentSection
            }
            .padding()
        }
        .navigationTitle("SymbolContainer")
    }

    // MARK: - Sizes

    private var sizesSection: some View {
        sectionView(title: "Sizes (Icon)") {
            FlowLayout(spacing: 16) {
                ForEach(allSizes, id: \.0) { size, label in
                    VStack(spacing: 8) {
                        LemonadeUi.SymbolContainer(
                            icon: .heart,
                            contentDescription: nil,
                            size: size
                        )
                        Text(label).font(.caption)
                    }
                }
            }
        }
    }

    // MARK: - Voices

    private var voicesSection: some View {
        sectionView(title: "Voices") {
            FlowLayout(spacing: 16) {
                ForEach(allVoices, id: \.0) { voice, label, icon in
                    VStack(spacing: 8) {
                        LemonadeUi.SymbolContainer(
                            icon: icon,
                            contentDescription: nil,
                            voice: voice,
                            size: .medium
                        )
                        Text(label).font(.caption)
                    }
                }
            }
        }
    }

    // MARK: - Shapes

    private let roundedScalingSizes: [(SymbolContainerSize, String)] = [
        (.large, "Rounded L"),
        (.xLarge, "Rounded XL"),
        (.xxLarge, "Rounded XXL"),
    ]

    private var shapesSection: some View {
        sectionView(title: "Shapes") {
            VStack(alignment: .leading, spacing: 16) {
                FlowLayout(spacing: 16) {
                    ForEach(allShapes, id: \.0) { shape, label in
                        VStack(spacing: 8) {
                            LemonadeUi.SymbolContainer(
                                icon: .heart,
                                contentDescription: nil,
                                size: .medium,
                                shape: shape
                            )
                            Text(label).font(.caption)
                        }
                    }
                }
                FlowLayout(spacing: 16) {
                    ForEach(roundedScalingSizes, id: \.0) { size, label in
                        VStack(spacing: 8) {
                            LemonadeUi.SymbolContainer(
                                icon: .heart,
                                contentDescription: nil,
                                size: size,
                                shape: .rounded
                            )
                            Text(label).font(.caption)
                        }
                    }
                }
            }
        }
    }

    // MARK: - Badge

    private var badgeSection: some View {
        sectionView(title: "With Badge") {
            FlowLayout(spacing: 24) {
                ForEach(badgeSizes, id: \.0) { size, label in
                    VStack(spacing: 8) {
                        LemonadeUi.SymbolContainer(
                            icon: .heart,
                            contentDescription: nil,
                            size: size
                        ) {
                            LemonadeUi.Badge(text: "3", size: .xSmall)
                        }
                        Text(label).font(.caption)
                    }
                }
            }
        }
    }

    // MARK: - Image

    private var imageSection: some View {
        sectionView(title: "Image Variants") {
            VStack(alignment: .leading, spacing: 16) {
                Text("fill = false").font(.subheadline)
                FlowLayout(spacing: 16) {
                    ForEach(imageSizes, id: \.0) { size, label in
                        VStack(spacing: 8) {
                            LemonadeUi.SymbolContainer(
                                image: Image(systemName: "star.fill"),
                                contentDescription: "Star",
                                fill: false,
                                size: size
                            )
                            Text(label).font(.caption)
                        }
                    }
                }

                Text("fill = true").font(.subheadline)
                FlowLayout(spacing: 16) {
                    ForEach(allShapes, id: \.0) { shape, label in
                        VStack(spacing: 8) {
                            LemonadeUi.SymbolContainer(
                                image: Image(systemName: "photo"),
                                contentDescription: "Photo",
                                fill: true,
                                size: .medium,
                                shape: shape
                            )
                            Text(label).font(.caption)
                        }
                    }
                }
            }
        }
    }

    // MARK: - Text

    private var textSection: some View {
        sectionView(title: "Text Variant") {
            FlowLayout(spacing: 16) {
                ForEach(allVoices.filter { $0.0 != .brand && $0.0 != .brandSubtle }, id: \.0) { voice, label, _ in
                    VStack(spacing: 8) {
                        LemonadeUi.SymbolContainer(
                            text: String(label.prefix(1)),
                            voice: voice,
                            size: .medium
                        )
                        Text(label).font(.caption)
                    }
                }
            }
        }
    }

    // MARK: - Custom Content

    private var customContentSection: some View {
        sectionView(title: "Custom Content") {
            FlowLayout(spacing: 16) {
                LemonadeUi.SymbolContainer(voice: .neutral, size: .large) {
                    // A token, not `.yellow`: the container is a `.neutral` voice, and a
                    // system colour has no contrast guarantee against its surface.
                    Image(systemName: "star.fill")
                        .foregroundStyle(.content.contentNeutral)
                        .accessibilityLabel("Star")
                }

                LemonadeUi.SymbolContainer(voice: .info, size: .large) {
                    Image(systemName: "person.fill")
                        .foregroundStyle(.content.contentInfo)
                        .accessibilityLabel("Person")
                }
            }
        }
    }

    // MARK: - Helpers

    private func sectionView<Content: View>(
        title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)
            content()
        }
    }
}

// MARK: - Flow Layout

private struct FlowLayout: Layout {
    var spacing: CGFloat = 8

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = arrange(proposal: proposal, subviews: subviews)
        return result.size
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = arrange(proposal: proposal, subviews: subviews)
        for (index, position) in result.positions.enumerated() {
            subviews[index].place(
                at: CGPoint(x: bounds.minX + position.x, y: bounds.minY + position.y),
                proposal: .unspecified
            )
        }
    }

    private func arrange(proposal: ProposedViewSize, subviews: Subviews) -> (positions: [CGPoint], size: CGSize) {
        let maxWidth = proposal.width ?? .infinity
        var positions: [CGPoint] = []
        var x: CGFloat = 0
        var y: CGFloat = 0
        var rowHeight: CGFloat = 0
        var totalSize: CGSize = .zero

        for subview in subviews {
            let size = subview.sizeThatFits(.unspecified)
            if x + size.width > maxWidth, x > 0 {
                x = 0
                y += rowHeight + spacing
                rowHeight = 0
            }
            positions.append(CGPoint(x: x, y: y))
            rowHeight = max(rowHeight, size.height)
            x += size.width + spacing
            totalSize.width = max(totalSize.width, x - spacing)
            totalSize.height = max(totalSize.height, y + rowHeight)
        }

        return (positions, totalSize)
    }
}

#Preview {
    NavigationStack {
        SymbolContainerDisplayView()
    }
}
