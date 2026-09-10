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
    let text: String
    let size: LemonadeBadgeSize

    private var overlayGradient: LinearGradient {
        let highlight = LemonadeTheme.colors.background.bgDefault
        return LinearGradient(
            stops: [
                .init(color: highlight, location: 0),
                .init(color: highlight.opacity(0), location: 1)
            ],
            // A fixed diagonal keeps the sweep free of size-dependent angle math on the
            // badge's short, wide shape.
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }

    var body: some View {
        SwiftUI.Text(text)
            .font(.custom(LemonadeTypography.fontFamily, size: size.fontSize).weight(.semibold))
            .foregroundStyle(LemonadeTheme.colors.content.contentOnBrandHigh)
            .lineLimit(1)
            .padding(.horizontal, size.textHorizontalPadding)
            .padding(.vertical, size.textVerticalPadding)
            .padding(.horizontal, size.horizontalPadding)
            .frame(height: size.height)
            // No clipShape: the capsule fills already round the badge, and clipping the
            // composited subtree would only add an offscreen render pass.
            .background(
                // compositingGroup() isolates the blend so the gradient reacts to the brand
                // fill below it, not to whatever sits behind the badge.
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
