import SwiftUI

// MARK: - Badge Size

/// Size options for the Badge component.
public enum LemonadeBadgeSize {
    case xSmall
    case small

    var height: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size400
        case .small: return LemonadeTheme.sizes.size500
        }
    }

    var horizontalPadding: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.spaces.spacing50
        case .small: return LemonadeTheme.spaces.spacing100
        }
    }

    var textHorizontalPadding: CGFloat {
        LemonadeTheme.spaces.spacing50
    }

    var textVerticalPadding: CGFloat {
        switch self {
        case .xSmall: return 1
        case .small: return 2
        }
    }

    var fontSize: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size250
        case .small: return LemonadeTheme.sizes.size300
        }
    }

    var lineHeight: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size350
        case .small: return LemonadeTheme.sizes.size400
        }
    }
}

// MARK: - Badge Component

public extension LemonadeUi {
    /// Badge component to highlight new or unread items, or to indicate status.
    ///
    /// Badges are small, rounded indicators that can be used to draw attention
    /// to specific elements. They typically display counts, statuses, or categories.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Badge(
    ///     text: "New",
    ///     size: .small
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - text: The text to be displayed inside the badge
    ///   - size: The size of the badge. Defaults to .small
    /// - Returns: A styled Badge view
    @ViewBuilder
    static func Badge(
        text: String,
        size: LemonadeBadgeSize = .small
    ) -> some View {
        LemonadeBadgeView(
            text: text,
            size: size
        )
    }
}

// MARK: - Internal Badge View

private struct LemonadeBadgeView: View {
    @Environment(\.lemonadeFontFamily) private var fontFamily

    let text: String
    let size: LemonadeBadgeSize

    private var overlayGradient: LinearGradient {
        let highlight = LemonadeTheme.colors.background.bgDefault
        return LinearGradient(
            stops: [
                .init(color: highlight, location: 0),
                .init(color: highlight.opacity(0), location: 1)
            ],
            // The design specifies a ~106.6° sweep; topLeading→bottomTrailing approximates
            // that on the badge's short, wide shape without size-dependent angle math.
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }

    var body: some View {
        SwiftUI.Text(text)
            .font(.custom(fontFamily.semibold, size: size.fontSize, relativeTo: .body))
            .foregroundStyle(LemonadeTheme.colors.content.contentOnBrandHigh)
            .lineLimit(1)
            .padding(.horizontal, size.textHorizontalPadding)
            .padding(.vertical, size.textVerticalPadding)
            .padding(.horizontal, size.horizontalPadding)
            .frame(height: size.height)
            // The two Capsule fills already define the pill, and the label is inset by padding
            // so it never reaches the ends. A clipShape over this gradient background only adds
            // an offscreen render pass (the composited subtree can't fold into a layer corner
            // radius), so the shaped fills do the rounding instead. Badges render densely
            // (one per notification dot/count), so the per-instance pass adds up while scrolling.
            .background(
                // Per design: brand fill with a bg-default → transparent highlight composited
                // in `.overlay` blend mode. compositingGroup() isolates the blend so the gradient
                // reacts to the brand fill below it, not whatever sits behind the badge. This is a
                // bounded compositing pass over two coincident capsules — much cheaper than the
                // clipShape avoided above, which would composite the whole shaped subtree.
                ZStack {
                    Capsule()
                        .fill(LemonadeTheme.colors.background.bgBrand)

                    Capsule()
                        .fill(overlayGradient)
                        .blendMode(.overlay)
                }
                .compositingGroup()
            )
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeBadge_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            HStack(spacing: 16) {
                LemonadeUi.Badge(text: "New", size: .xSmall)
                LemonadeUi.Badge(text: "New", size: .small)
            }

            HStack(spacing: 16) {
                LemonadeUi.Badge(text: "5", size: .xSmall)
                LemonadeUi.Badge(text: "99+", size: .small)
            }

            LemonadeUi.Badge(text: "Label", size: .small)
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
