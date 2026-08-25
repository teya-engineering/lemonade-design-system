import SwiftUI

// MARK: - Skeleton Size

public enum LemonadeSkeletonSize: CaseIterable {
    case xSmall
    case small
    case medium
    case large
    case xLarge
    case xxLarge
    case xxxLarge
}

// MARK: - Skeleton Components

public extension LemonadeUi {
    /// Line skeleton placeholder for text content loading.
    /// Full width by default, height determined by size.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.LineSkeleton()
    /// LemonadeUi.LineSkeleton(size: .large)
    /// ```
    @ViewBuilder
    static func LineSkeleton(
        size: LemonadeSkeletonSize = .medium
    ) -> some View {
        SkeletonView(variant: .line, size: size)
    }

    /// Circle skeleton placeholder for avatar/icon loading.
    /// Diameter determined by size.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.CircleSkeleton()
    /// LemonadeUi.CircleSkeleton(size: .large)
    /// ```
    @ViewBuilder
    static func CircleSkeleton(
        size: LemonadeSkeletonSize = .medium
    ) -> some View {
        SkeletonView(variant: .circle, size: size)
    }

    /// Block skeleton placeholder for card/image loading.
    /// Full width, fixed height (size1600 token).
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.BlockSkeleton()
    /// ```
    @ViewBuilder
    static func BlockSkeleton() -> some View {
        SkeletonView(variant: .block, size: .medium)
    }
}

// MARK: - Internal Types

private enum SkeletonVariant {
    case line
    case circle
    case block
}

// MARK: - Size Mappings

private extension LemonadeSkeletonSize {
    var lineHeight: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size400
        case .small: return LemonadeTheme.sizes.size500
        case .medium: return LemonadeTheme.sizes.size600
        case .large: return LemonadeTheme.sizes.size700
        case .xLarge: return LemonadeTheme.sizes.size800
        case .xxLarge: return LemonadeTheme.sizes.size900
        case .xxxLarge: return LemonadeTheme.sizes.size1000
        }
    }

    var circleDiameter: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size300
        case .small: return LemonadeTheme.sizes.size400
        case .medium: return LemonadeTheme.sizes.size500
        case .large: return LemonadeTheme.sizes.size600
        case .xLarge: return LemonadeTheme.sizes.size800
        case .xxLarge: return LemonadeTheme.sizes.size1000
        case .xxxLarge: return LemonadeTheme.sizes.size1200
        }
    }
}

// MARK: - Skeleton View

private struct SkeletonView: View {
    let variant: SkeletonVariant
    let size: LemonadeSkeletonSize

    @State private var shimmerOffset: CGFloat = -1

    /// The shimmer ramp, built once rather than per body evaluation.
    ///
    /// Only the gradient's start and end points move, so the colour ramp itself is invariant.
    /// Holding it here is safe for appearance changes: the theme's colours are asset-catalog
    /// colours that resolve per trait at render time.
    private static let shimmerRamp = Gradient(
        colors: [
            LemonadeTheme.colors.background.bgElevated,
            LemonadeTheme.colors.background.bgElevatedHigh,
            LemonadeTheme.colors.background.bgElevated
        ]
    )

    var body: some View {
        skeletonShape
            .onAppear {
                withAnimation(
                    .easeInOut(duration: 1.0)
                    .repeatForever(autoreverses: true)
                ) {
                    shimmerOffset = 1
                }
            }
    }

    @ViewBuilder
    private var skeletonShape: some View {
        let gradient = LinearGradient(
            gradient: Self.shimmerRamp,
            startPoint: UnitPoint(x: shimmerOffset - 0.5, y: 0.5),
            endPoint: UnitPoint(x: shimmerOffset + 0.5, y: 0.5)
        )

        switch variant {
        case .line:
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius100)
                .fill(gradient)
                .frame(height: size.lineHeight)
                .padding(.vertical, LemonadeTheme.spaces.spacing100)

        case .circle:
            Circle()
                .fill(gradient)
                .frame(width: size.circleDiameter, height: size.circleDiameter)

        case .block:
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                .fill(gradient)
                .frame(height: LemonadeTheme.sizes.size1600)
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeSkeleton_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing600) {
            LemonadeUi.LineSkeleton(size: .small)
            LemonadeUi.LineSkeleton(size: .medium)
            LemonadeUi.LineSkeleton(size: .large)

            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.CircleSkeleton(size: .small)
                LemonadeUi.CircleSkeleton(size: .medium)
                LemonadeUi.CircleSkeleton(size: .large)
            }

            LemonadeUi.BlockSkeleton()
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
