import SwiftUI

/// Lays its children out in equal-width columns that together fill the proposed width.
///
/// With no proposed width (`.fixedSize()`), it hugs to the widest child times the child count,
/// rounded up so pixel rounding can't truncate the widest child. An infinite proposal is passed
/// through, so the layout stays as flexible as an `HStack` of `.frame(maxWidth: .infinity)` children.
@available(iOS 16, macOS 13, *)
struct EqualWidthHStack: Layout {
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        if let width = proposal.width, let height = proposal.height {
            return CGSize(width: width, height: height)
        }

        let idealSizes = subviews.map { $0.sizeThatFits(.unspecified) }
        let widestChild = idealSizes.map(\.width).max() ?? 0
        let tallestChild = idealSizes.map(\.height).max() ?? 0
        return CGSize(
            width: proposal.width ?? (widestChild * CGFloat(subviews.count)).rounded(.up),
            height: proposal.height ?? tallestChild.rounded(.up)
        )
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        guard !subviews.isEmpty else { return }
        let columnWidth = bounds.width / CGFloat(subviews.count)
        for (index, subview) in subviews.enumerated() {
            subview.place(
                at: CGPoint(x: bounds.minX + columnWidth * CGFloat(index), y: bounds.minY),
                proposal: ProposedViewSize(width: columnWidth, height: bounds.height)
            )
        }
    }
}
