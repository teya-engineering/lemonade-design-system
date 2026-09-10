import SwiftUI

// MARK: - Shadow Border Modifier

/// A modifier that applies a shadow-based border effect to a view.
/// This creates a border using drop shadow with spread, which doesn't affect layout.
public struct ShadowBorderModifier<S: Shape>: ViewModifier {
    let width: CGFloat
    let color: Color
    let shape: S

    public init(width: CGFloat, color: Color, shape: S) {
        self.width = width
        self.color = color
        self.shape = shape
    }

    public func body(content: Content) -> some View {
        content
            .overlay(
                shape
                    .stroke(color, lineWidth: width)
            )
    }
}

// MARK: - View Extension

public extension View {
    /// Applies a shadow-based border to the view.
    ///
    /// The border is drawn as an overlay stroke, so it does not affect layout.
    ///
    /// ## Usage
    /// ```swift
    /// MyView()
    ///     .shadowBorder(
    ///         width: 2,
    ///         color: LemonadeTheme.colors.border.borderNeutralMedium,
    ///         shape: RoundedRectangle(cornerRadius: 8)
    ///     )
    /// ```
    ///
    /// - Parameters:
    ///   - width: The width of the border
    ///   - color: The color of the border
    ///   - shape: The shape to use for the border
    /// - Returns: A view with the shadow border applied
    func shadowBorder<S: Shape>(
        width: CGFloat,
        color: Color,
        shape: S
    ) -> some View {
        modifier(ShadowBorderModifier(width: width, color: color, shape: shape))
    }

    /// Applies a shadow-based border with a rounded rectangle shape.
    ///
    /// - Parameters:
    ///   - width: The width of the border
    ///   - color: The color of the border
    ///   - cornerRadius: The corner radius for the rounded rectangle
    /// - Returns: A view with the shadow border applied
    func shadowBorder(
        width: CGFloat,
        color: Color,
        cornerRadius: CGFloat
    ) -> some View {
        shadowBorder(
            width: width,
            color: color,
            shape: RoundedRectangle(cornerRadius: cornerRadius)
        )
    }

    /// Applies a shadow-based border with a capsule shape.
    ///
    /// - Parameters:
    ///   - width: The width of the border
    ///   - color: The color of the border
    /// - Returns: A view with the capsule shadow border applied
    func shadowBorderCapsule(
        width: CGFloat,
        color: Color
    ) -> some View {
        shadowBorder(
            width: width,
            color: color,
            shape: Capsule()
        )
    }

    /// Applies a shadow-based border with a circle shape.
    ///
    /// - Parameters:
    ///   - width: The width of the border
    ///   - color: The color of the border
    /// - Returns: A view with the circle shadow border applied
    func shadowBorderCircle(
        width: CGFloat,
        color: Color
    ) -> some View {
        shadowBorder(
            width: width,
            color: color,
            shape: Circle()
        )
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeBorder_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            SwiftUI.Text("Rounded Rectangle")
                .padding()
                .shadowBorder(
                    width: 2,
                    color: LemonadeTheme.colors.border.borderNeutralMedium,
                    cornerRadius: 8
                )

            SwiftUI.Text("Capsule")
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .shadowBorderCapsule(
                    width: 1,
                    color: LemonadeTheme.colors.border.borderSelected
                )

            SwiftUI.Text("C")
                .frame(width: 40, height: 40)
                .shadowBorderCircle(
                    width: 2,
                    color: LemonadeTheme.colors.border.borderBrand
                )

            SwiftUI.Text("Custom Shape")
                .padding()
                .shadowBorder(
                    width: 3,
                    color: LemonadeTheme.colors.content.contentCritical,
                    shape: RoundedRectangle(cornerRadius: 16)
                )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
