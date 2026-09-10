import SwiftUI

// MARK: - DividerVariant

/// Variants available for dividers.
public enum DividerVariant {
    /// Solid line divider.
    case solid

    /// Dashed line divider.
    case dashed
}

// MARK: - HorizontalDivider Component

public extension LemonadeUi {
    /// A horizontal divider to separate content. Optionally displays a label in the center.
    ///
    /// ## Usage
    /// ```swift
    /// // Simple divider
    /// LemonadeUi.HorizontalDivider()
    ///
    /// // Divider with label
    /// LemonadeUi.HorizontalDivider(label: "OR")
    ///
    /// // Dashed divider
    /// LemonadeUi.HorizontalDivider(variant: .dashed)
    /// ```
    ///
    /// - Parameters:
    ///   - label: Optional String label to display in the center of the divider
    ///   - variant: Variant of the divider. Defaults to `.solid`
    /// - Returns: A styled horizontal divider view
    @ViewBuilder
    static func HorizontalDivider(
        label: String? = nil,
        variant: DividerVariant = .solid
    ) -> some View {
        let thickness = LemonadeTheme.borderWidth.base.border25
        let dividerColor = variant == .solid
            ? LemonadeTheme.colors.border.borderNeutralLow
            : LemonadeTheme.colors.border.borderNeutralMedium

        if let label = label {
            HStack(spacing: 0) {
                CoreDivider(
                    color: dividerColor,
                    variant: variant,
                    thickness: thickness,
                    isHorizontal: true
                )

                LemonadeUi.Text(
                    label,
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    textAlign: .center,
                    color: LemonadeTheme.colors.content.contentSecondary,
                    maxLines: 2
                )
                .padding(.horizontal, LemonadeTheme.spaces.spacing300)
                .layoutPriority(1)

                CoreDivider(
                    color: dividerColor,
                    variant: variant,
                    thickness: thickness,
                    isHorizontal: true
                )
            }
        } else {
            CoreDivider(
                color: dividerColor,
                variant: variant,
                thickness: thickness,
                isHorizontal: true
            )
        }
    }
}

// MARK: - VerticalDivider Component

public extension LemonadeUi {
    /// A vertical divider to separate content.
    ///
    /// ## Usage
    /// ```swift
    /// // Simple vertical divider
    /// LemonadeUi.VerticalDivider()
    ///
    /// // Dashed vertical divider
    /// LemonadeUi.VerticalDivider(variant: .dashed)
    /// ```
    ///
    /// - Parameters:
    ///   - variant: Variant of the divider. Defaults to `.solid`
    /// - Returns: A styled vertical divider view
    @ViewBuilder
    static func VerticalDivider(
        variant: DividerVariant = .solid
    ) -> some View {
        let thickness = LemonadeTheme.borderWidth.base.border25
        let dividerColor = variant == .solid
            ? LemonadeTheme.colors.border.borderNeutralLow
            : LemonadeTheme.colors.border.borderNeutralMedium

        CoreDivider(
            color: dividerColor,
            variant: variant,
            thickness: thickness,
            isHorizontal: false
        )
    }
}

// MARK: - Core Divider Views

/// A line down the middle of whatever rect it is given.
///
/// A `Shape` receives the resolved rect in `path(in:)`, so neither variant needs a
/// `GeometryReader`, which would take all offered space and force a second layout pass.
private struct DividerLine: Shape {
    let isHorizontal: Bool

    func path(in rect: CGRect) -> Path {
        var path = Path()
        if isHorizontal {
            path.move(to: CGPoint(x: rect.minX, y: rect.midY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.midY))
        } else {
            path.move(to: CGPoint(x: rect.midX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY))
        }
        return path
    }
}

private struct CoreDivider: View {
    let color: Color
    let variant: DividerVariant
    let thickness: CGFloat
    let isHorizontal: Bool

    var body: some View {
        DividerLine(isHorizontal: isHorizontal)
            .stroke(
                color,
                style: StrokeStyle(lineWidth: thickness, dash: variant.dashPattern)
            )
            .frame(
                width: isHorizontal ? nil : thickness,
                height: isHorizontal ? thickness : nil
            )
    }
}

private extension DividerVariant {
    /// The stroke dash pattern for this variant; empty means a continuous line.
    var dashPattern: [CGFloat] {
        switch self {
        case .solid:
            return []
        case .dashed:
            return [LemonadeTheme.sizes.size100, LemonadeTheme.spaces.spacing100]
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeDivider_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            VStack(alignment: .leading) {
                Text("Simple Divider")
                    .font(.caption)
                LemonadeUi.HorizontalDivider()
            }

            VStack(alignment: .leading) {
                Text("Divider with Label")
                    .font(.caption)
                LemonadeUi.HorizontalDivider(label: "OR")
            }

            VStack(alignment: .leading) {
                Text("Dashed Divider")
                    .font(.caption)
                LemonadeUi.HorizontalDivider(variant: .dashed)
            }

            VStack(alignment: .leading) {
                Text("Dashed Divider with Label")
                    .font(.caption)
                LemonadeUi.HorizontalDivider(label: "OR", variant: .dashed)
            }

            HStack(spacing: 24) {
                VStack {
                    Text("Simple")
                        .font(.caption)
                    LemonadeUi.VerticalDivider()
                        .frame(height: 48)
                }

                VStack {
                    Text("Dashed")
                        .font(.caption)
                    LemonadeUi.VerticalDivider(variant: .dashed)
                        .frame(height: 48)
                }
            }
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
