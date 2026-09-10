import SwiftUI

// MARK: - Tooltip Indicator Placement

/// How far the indicator protrudes past the tooltip body on each physical edge.
struct LemonadeTooltipIndicatorInsets {
    let left: CGFloat
    let top: CGFloat
    let right: CGFloat
    let bottom: CGFloat
}

/// The edge of the tooltip body an indicator protrudes from.
enum LemonadeTooltipIndicatorEdge {
    case none
    case top
    case bottom
    case left
    case right

    /// Rotation that carries the canonical upward-pointing indicator onto this edge.
    var rotationAngle: CGFloat {
        switch self {
        case .none, .top: return 0
        case .right: return .pi / 2
        case .bottom: return .pi
        case .left: return .pi * 3 / 2
        }
    }
}

/// Where along its edge the indicator sits.
///
/// `start` and `end` are physical, like the placements themselves: the left and right ends of the top
/// and bottom edges, the top and bottom ends of the left and right ones.
enum LemonadeTooltipIndicatorAlignment {
    case start
    case center
    case end
}

/// Where the tooltip draws its indicator — the small arrow that points back at the element the
/// tooltip describes.
///
/// The first word names the edge of the tooltip the indicator protrudes from, the second where along
/// that edge it sits. So `.topLeft` puts the arrow on the top edge towards its left end — the tooltip
/// hangs below the element it describes — while `.leftTop` puts it on the left edge towards its top,
/// with the tooltip sitting to the right of that element.
///
/// Placement is physical, not directional: `.topLeft` anchors the indicator to the left edge in both
/// LTR and RTL layouts, so a caller that positions the tooltip by absolute coordinates does not have
/// to compensate for the layout direction.
public enum LemonadeTooltipIndicatorPlacement: Hashable, CaseIterable, Sendable {
    /// No indicator. The tooltip is a plain floating container.
    case none

    case topLeft
    case topCenter
    case topRight
    case bottomLeft
    case bottomCenter
    case bottomRight
    case leftTop
    case leftCenter
    case leftBottom
    case rightTop
    case rightCenter
    case rightBottom

    /// The edge of the tooltip body this placement's indicator protrudes from.
    var edge: LemonadeTooltipIndicatorEdge {
        switch self {
        case .none: return .none
        case .topLeft, .topCenter, .topRight: return .top
        case .bottomLeft, .bottomCenter, .bottomRight: return .bottom
        case .leftTop, .leftCenter, .leftBottom: return .left
        case .rightTop, .rightCenter, .rightBottom: return .right
        }
    }

    /// Where along that edge the indicator sits.
    var alignment: LemonadeTooltipIndicatorAlignment {
        switch self {
        case .topLeft, .bottomLeft, .leftTop, .rightTop: return .start
        case .topRight, .bottomRight, .leftBottom, .rightBottom: return .end
        case .none, .topCenter, .bottomCenter, .leftCenter, .rightCenter: return .center
        }
    }

    /// Whether the indicator runs along the left or right edge, so its position is measured down the
    /// tooltip's height rather than across its width. `.none` counts as horizontal — it has no
    /// indicator, and the container still centres it on its anchor horizontally.
    var isOnVerticalEdge: Bool {
        edge == .left || edge == .right
    }

    /// How far the indicator protrudes past the tooltip body on each edge — `indicatorHeight` on the
    /// indicator's own edge and nothing on the other three.
    ///
    /// The edges are physical, like the placements themselves, so a left indicator stays on the left in
    /// an RTL layout. This is the single statement of "which edge does the indicator add to": the view
    /// pads the body by it and `LemonadeTooltipShape` insets its outline by it, and the two have to
    /// agree exactly or the drawn surface and the padded content come apart.
    var indicatorInsets: LemonadeTooltipIndicatorInsets {
        let protrusion = LemonadeTooltipMetrics.indicatorHeight
        let indicatorEdge = edge

        return LemonadeTooltipIndicatorInsets(
            left: indicatorEdge == .left ? protrusion : 0,
            top: indicatorEdge == .top ? protrusion : 0,
            right: indicatorEdge == .right ? protrusion : 0,
            bottom: indicatorEdge == .bottom ? protrusion : 0
        )
    }

    /// Pivot for the entry scale, placed at the indicator so the tooltip grows out of the element
    /// it points at rather than out of its own centre.
    var transformAnchor: UnitPoint {
        // Derived from indicatorCenterOffset rather than restated, so the pivot cannot drift from where
        // the indicator is actually drawn. The width is fixed, so the fraction is exact.
        let alongTopOrBottom = indicatorCenterOffset(alongEdgeOfLength: LemonadeTooltipMetrics.width)
            / LemonadeTooltipMetrics.width

        // A left or right indicator's position along its edge cannot be turned into a fraction here:
        // the tooltip's height depends on its content and is not known until it has been measured.
        // Those placements pivot on the corner nearest the indicator instead, which over the entry
        // spring does not read differently.
        let alongLeftOrRight: CGFloat
        switch alignment {
        case .start: alongLeftOrRight = 0
        case .center: alongLeftOrRight = 0.5
        case .end: alongLeftOrRight = 1
        }

        switch edge {
        case .top: return UnitPoint(x: alongTopOrBottom, y: 0)
        case .bottom: return UnitPoint(x: alongTopOrBottom, y: 1)
        case .left: return UnitPoint(x: 0, y: alongLeftOrRight)
        case .right: return UnitPoint(x: 1, y: alongLeftOrRight)
        case .none: return .center
        }
    }

    /// Human-readable name, for galleries and documentation.
    public var displayName: String {
        switch self {
        case .none: return "None"
        case .topLeft: return "Top Left"
        case .topCenter: return "Top Center"
        case .topRight: return "Top Right"
        case .bottomLeft: return "Bottom Left"
        case .bottomCenter: return "Bottom Center"
        case .bottomRight: return "Bottom Right"
        case .leftTop: return "Left Top"
        case .leftCenter: return "Left Center"
        case .leftBottom: return "Left Bottom"
        case .rightTop: return "Right Top"
        case .rightCenter: return "Right Center"
        case .rightBottom: return "Right Bottom"
        }
    }

    /// Distance from the start of the indicator's edge to the centre of the indicator — measured from
    /// the body's left edge for a top or bottom placement, from its top edge for a left or right one.
    ///
    /// The indicator sits at one of three fixed positions along its edge, so this is the reach a given
    /// placement can offer. Pass the length of the edge the placement sits on: the body's width for a
    /// top or bottom placement, its height for a left or right one.
    func indicatorCenterOffset(alongEdgeOfLength length: CGFloat) -> CGFloat {
        let halfBase = LemonadeTooltipMetrics.indicatorBaseWidth / 2
        let inset = isOnVerticalEdge
            ? LemonadeTooltipMetrics.indicatorVerticalEdgeInset
            : LemonadeTooltipMetrics.indicatorHorizontalEdgeInset

        // An edge too short for the inset would put `end` ahead of `start`; collapsing both onto the
        // centre keeps the three positions in order instead of crossing them over.
        let center = length / 2

        switch alignment {
        case .start: return min(inset + halfBase, center)
        case .center: return center
        case .end: return max(length - inset - halfBase, center)
        }
    }
}

// MARK: - Tooltip Footer Action Variant

/// Emphasis of a tooltip footer action.
public enum LemonadeTooltipFooterActionVariant: Hashable, Sendable {
    /// Filled action. Use for the single most important action in the footer.
    case primary

    /// Outlined action. Use for secondary or dismissive actions.
    case secondary

    var backgroundColor: Color {
        switch self {
        case .primary: return LemonadeTheme.colors.background.bgDefault
        case .secondary: return Color.clear
        }
    }

    var contentColor: Color {
        switch self {
        case .primary: return LemonadeTheme.colors.content.contentPrimary
        case .secondary: return LemonadeTheme.colors.content.contentPrimaryInverse
        }
    }

    var textStyle: LemonadeTextStyle {
        switch self {
        case .primary: return LemonadeTypography.shared.bodySmallSemiBold
        case .secondary: return LemonadeTypography.shared.bodySmallMedium
        }
    }

    var borderWidth: CGFloat {
        switch self {
        case .primary: return 0
        case .secondary: return LemonadeTheme.borderWidth.base.border25
        }
    }
}

// MARK: - Metrics

private enum LemonadeTooltipMetrics {
    /// Default tooltip width.
    static let width: CGFloat = 280

    /// Height of the visible, rounded-off indicator — how far it protrudes past the tooltip body.
    static let indicatorHeight: CGFloat = 8

    /// Width of the indicator where it meets the tooltip body.
    static let indicatorBaseWidth: CGFloat = 15

    /// Height the indicator would reach if its tip were a sharp point instead of a rounded one.
    static let indicatorApexHeight: CGFloat = 10

    /// Distance from the tooltip corner to the near side of the indicator base, along the top and
    /// bottom edges — so the offset that separates `.topLeft` from `.topCenter`.
    static let indicatorHorizontalEdgeInset: CGFloat = 40

    /// As `indicatorHorizontalEdgeInset`, but along the shorter left and right edges.
    static let indicatorVerticalEdgeInset: CGFloat = 24

    /// Fraction of the way from the indicator base to its notional apex at which the rounded tip starts.
    /// Together with `indicatorTipCurvature` this yields the 3pt tip radius from the design.
    static let indicatorTipStart: CGFloat = 0.68

    /// How far each tip control point is pulled towards the notional apex.
    static let indicatorTipCurvature: CGFloat = 0.5

    /// How far the indicator base is sunk into the tooltip body so the two sub-paths overlap rather
    /// than merely touching — which would leave a hairline across the join.
    static let indicatorBodyOverlap: CGFloat = 1

    /// Cover slot aspect ratio — 272x158, the cover size at the default 280pt tooltip width.
    static let coverAspectRatio: CGFloat = 272.0 / 158.0
}

// MARK: - Tooltip Footer Scope

/// Vends the parts that belong in a tooltip footer.
///
/// The footer is a slot, but only a fixed set of parts belongs in it — they are produced by this
/// scope rather than being standalone components, so they can only be used where they make sense.
/// The scope is handed to the footer builder; you cannot create one yourself.
public struct LemonadeTooltipFooterScope {

    fileprivate init() {}

    /// Progress indicator for a multi-step tooltip tour, rendered as `1 of 3`.
    ///
    /// Follow it with a `Spacer()` to push the actions to the trailing edge.
    ///
    /// - Parameters:
    ///   - currentStep: The step being shown, 1-based.
    ///   - totalSteps: The total number of steps in the tour.
    ///   - separator: Word placed between the two numbers. Defaults to `"of"` — pass a translated
    ///     string to localise it.
    /// - Returns: The step counter view.
    public func stepCounter(
        currentStep: Int,
        totalSteps: Int,
        separator: String = "of"
    ) -> some View {
        LemonadeUi.Text(
            "\(currentStep) \(separator) \(totalSteps)",
            textStyle: LemonadeTypography.shared.bodyXSmallMedium,
            color: LemonadeTheme.colors.content.contentPrimaryInverse
                .opacity(LemonadeTheme.opacity.base.opacity80),
            maxLines: 1
        )
        .fixedSize(horizontal: true, vertical: false)
    }

    /// A tappable action in the tooltip footer.
    ///
    /// - Parameters:
    ///   - label: The text shown on the action.
    ///   - variant: Emphasis of the action. Defaults to `.primary`.
    ///   - onClick: Invoked when the action is tapped.
    /// - Returns: The action view.
    public func action(
        _ label: String,
        variant: LemonadeTooltipFooterActionVariant = .primary,
        onClick: @escaping () -> Void
    ) -> some View {
        LemonadeTooltipFooterActionView(
            label: label,
            variant: variant,
            onClick: onClick
        )
    }
}

// MARK: - Tooltip Component

public extension LemonadeUi {

    /// A floating container that explains or draws attention to another element on screen.
    ///
    /// The tooltip renders its own surface, indicator and content — it does not position itself.
    /// Place it in an overlay or popover of your choosing and pick the `indicatorPlacement` that
    /// points back at the element being described.
    ///
    /// The tooltip body is 280pt wide. The indicator is drawn outside the body, so the view is 8pt
    /// larger than the body on whichever edge the indicator sits — taller for a top or bottom
    /// placement, wider for a left or right one.
    ///
    /// ## Usage
    /// ```swift
    /// // Text only
    /// LemonadeUi.Tooltip(
    ///     content: "Tap here to see everything you sold today.",
    ///     title: "Daily takings",
    ///     indicatorPlacement: .topLeft
    /// )
    ///
    /// // Guided tour step, with a cover and a footer
    /// LemonadeUi.Tooltip(
    ///     content: "Tap here to see everything you sold today.",
    ///     title: "Daily takings",
    ///     indicatorPlacement: .topLeft,
    ///     onClose: { isPresented = false },
    ///     cover: { Image("takings-illustration").resizable() },
    ///     footer: { footer in
    ///         footer.stepCounter(currentStep: step, totalSteps: 3)
    ///         Spacer()
    ///         footer.action("Skip", variant: .secondary) { isPresented = false }
    ///         footer.action("Next") { step += 1 }
    ///     }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - content: The body text. Required — a tooltip always says something.
    ///   - title: Optional bold heading shown above `content`.
    ///   - indicatorPlacement: Where the indicator points from. Defaults to `.none`, which draws no
    ///     indicator.
    ///   - onClose: Invoked when the close button is tapped. The close button is only shown when
    ///     this is non-nil.
    ///   - closeContentDescription: Accessibility label for the close button.
    ///   - cover: Optional slot rendered above the text, in a 272:158 box clipped to the tooltip's
    ///     top corners. Use it for an illustration or screenshot.
    ///   - footer: Optional slot rendered below the text. See `LemonadeTooltipFooterScope` for the
    ///     parts that belong in it.
    /// - Returns: A styled Tooltip view
    @ViewBuilder
    static func Tooltip<Cover: View, Footer: View>(
        content: String,
        title: String? = nil,
        indicatorPlacement: LemonadeTooltipIndicatorPlacement = .none,
        onClose: (() -> Void)? = nil,
        closeContentDescription: String? = nil,
        @ViewBuilder cover: @escaping () -> Cover = { EmptyView() },
        @ViewBuilder footer: @escaping (LemonadeTooltipFooterScope) -> Footer = { _ in EmptyView() }
    ) -> some View {
        // The slots are type-erased here so the view itself can treat "absent" as nil rather than
        // inferring it from a generic parameter. The container builds tooltips from stored,
        // already-erased slots, where that inference is not available.
        LemonadeTooltipView(
            content: content,
            title: title,
            indicatorPlacement: indicatorPlacement,
            onClose: onClose,
            closeContentDescription: closeContentDescription,
            cover: Cover.self == EmptyView.self ? nil : AnyView(cover()),
            footer: Footer.self == EmptyView.self ? nil : { scope in AnyView(footer(scope)) }
        )
    }
}

// MARK: - Internal Tooltip View

struct LemonadeTooltipView: View {
    let content: String
    let title: String?
    let indicatorPlacement: LemonadeTooltipIndicatorPlacement
    let onClose: (() -> Void)?
    let closeContentDescription: String?
    let cover: AnyView?
    let footer: ((LemonadeTooltipFooterScope) -> AnyView)?

    @Environment(\.layoutDirection) private var layoutDirection

    private var hasCover: Bool { cover != nil }
    private var hasFooter: Bool { footer != nil }

    // The indicator is drawn outside the body, so the body has to be inset by its height on whichever
    // edge the indicator sits on — the same insets `LemonadeTooltipShape` builds its outline from.
    private var insets: LemonadeTooltipIndicatorInsets {
        indicatorPlacement.indicatorInsets
    }

    // Those insets are physical, like the placements themselves, but SwiftUI's edges are
    // direction-aware — so they are mapped onto the leading and trailing edges rather than applied to
    // them, which keeps a left indicator on the left in an RTL layout.
    private var indicatorPadding: EdgeInsets {
        let isRightToLeft = layoutDirection == .rightToLeft

        return EdgeInsets(
            top: insets.top,
            leading: isRightToLeft ? insets.right : insets.left,
            bottom: insets.bottom,
            trailing: isRightToLeft ? insets.left : insets.right
        )
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if hasCover {
                coverView
            }

            bodyView
        }
        .padding(LemonadeTheme.spaces.spacing100)
        .padding(indicatorPadding)
        // Covers the body plus whatever the indicator adds beside it, so the body itself stays 280pt
        // wide whichever edge the indicator sits on.
        .frame(width: LemonadeTooltipMetrics.width + insets.left + insets.right)
        .background(surface)
        .background(shadowLayer)
        .overlay(alignment: .topTrailing) {
            if let onClose {
                closeButton(onClose: onClose)
                    .padding(.top, indicatorPadding.top + LemonadeTheme.spaces.spacing100)
                    .padding(
                        .trailing,
                        indicatorPadding.trailing + LemonadeTheme.spaces.spacing100
                    )
            }
        }
        // Without its own compositing group the close button's blend reaches through to whatever is
        // behind the tooltip.
        .compositingGroup()
    }

    // MARK: Surface

    /// The tooltip surface: a blurred backdrop with the fill token layered over it.
    ///
    /// The fill is an 80% layer opacity over `bgDefaultInverse`, so whatever the
    /// tooltip covers shows through. Blurring the backdrop keeps that legible instead of letting
    /// busy content read through the text.
    private var surface: some View {
        let shape = LemonadeTooltipShape(
            indicatorPlacement: indicatorPlacement,
            cornerRadius: LemonadeTheme.radius.radius600
        )

        return ZStack {
            // `.ultraThinMaterial` is the backdrop blur; the token fill on top restores the exact
            // colour the design calls for, so the material's own tint barely reads through.
            shape.fill(.ultraThinMaterial)

            shape.fill(
                LemonadeTheme.colors.background.bgDefaultInverse
                    .opacity(LemonadeTheme.opacity.base.opacity80)
            )
        }
    }

    /// The drop shadow, cast from an opaque silhouette of the surface rather than from the surface
    /// itself.
    ///
    /// `lemonadeShadow` builds its shadow by masking a solid colour with the view it is applied to,
    /// so applying it to the tooltip scaled the shadow by the surface's ~74% alpha and roughly
    /// halved it. Casting it from an opaque copy of the shape keeps the token at full strength, and
    /// punching that shape back out stops the shadow from tinting the translucent fill sitting in
    /// front of it.
    private var shadowLayer: some View {
        let shape = LemonadeTooltipShape(
            indicatorPlacement: indicatorPlacement,
            cornerRadius: LemonadeTheme.radius.radius600
        )

        return shape
            .fill(Color.black)
            .lemonadeShadow(.xlarge)
            .overlay {
                shape
                    .fill(Color.black)
                    .blendMode(.destinationOut)
            }
            .compositingGroup()
    }

    // MARK: Cover

    private var coverView: some View {
        // The cover sits inside the tooltip's outer padding, so its top corners are the tooltip's
        // own radius less that padding; the bottom corners are the smaller radius the design
        // specifies.
        Color.clear
            .aspectRatio(LemonadeTooltipMetrics.coverAspectRatio, contentMode: .fit)
            .overlay { cover }
            .background(LemonadeTheme.colors.background.bgElevatedHigh)
            .clipShape(
                LemonadeTooltipCoverShape(
                    topRadius: LemonadeTheme.radius.radius600 - LemonadeTheme.spaces.spacing100,
                    bottomRadius: LemonadeTheme.radius.radius250
                )
            )
    }

    // MARK: Body

    private var bodyView: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            // Only takes effect when there is a title — VStack spacing sits between children.
            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing100) {
                if let title {
                    LemonadeUi.Text(
                        title,
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                        color: LemonadeTheme.colors.content.contentPrimaryInverse
                    )
                    .frame(maxWidth: .infinity, alignment: .leading)
                }

                LemonadeUi.Text(
                    content,
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: LemonadeTheme.colors.content.contentPrimaryInverse
                        .opacity(LemonadeTheme.opacity.base.opacity90)
                )
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            if let footer {
                HStack(spacing: LemonadeTheme.spaces.spacing100) {
                    footer(LemonadeTooltipFooterScope())
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(LemonadeTheme.spaces.spacing300)
    }

    // MARK: Close button

    private func closeButton(onClose: @escaping () -> Void) -> some View {
        SwiftUI.Button(action: onClose) {
            LemonadeUi.Icon(
                icon: .times,
                contentDescription: closeContentDescription,
                size: .small,
                tint: LemonadeTheme.colors.content.contentPrimaryInverse
            )
            .frame(
                width: LemonadeTheme.sizes.size800,
                height: LemonadeTheme.sizes.size800
            )
            .contentShape(Circle())
        }
        .buttonStyle(.plain)
        .opacity(LemonadeTheme.opacity.base.opacity40)
        // Hard light rather than a flat tint: over the dark light-mode surface a light icon screens
        // and stays visible, and over the light dark-mode surface a dark icon multiplies, so the
        // same token reads on both without a per-theme colour.
        .blendMode(.hardLight)
    }
}

// MARK: - Footer Action View

private struct LemonadeTooltipFooterActionView: View {
    let label: String
    let variant: LemonadeTooltipFooterActionVariant
    let onClick: () -> Void

    var body: some View {
        SwiftUI.Button(action: onClick) {
            LemonadeUi.Text(
                label,
                textStyle: variant.textStyle,
                color: variant.contentColor,
                maxLines: 1
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing300)
            .padding(.vertical, LemonadeTheme.spaces.spacing100)
            .frame(minHeight: LemonadeTheme.sizes.size800)
            .background(variant.backgroundColor)
            .clipShape(Capsule())
            .overlay {
                Capsule()
                    .strokeBorder(
                        LemonadeTheme.colors.border.borderNeutralMediumInverse,
                        lineWidth: variant.borderWidth
                    )
            }
        }
        .buttonStyle(.plain)
        .fixedSize(horizontal: true, vertical: false)
    }
}

// MARK: - Tooltip Shape

/// The tooltip surface: a rounded rectangle with the indicator triangle added to one edge.
///
/// The two are separate sub-paths of a single `Path` rather than two drawn shapes. Non-zero winding
/// fills overlapping sub-paths as their union in one operation, so the join is seamless — drawing
/// them as two views at the surface's ~74% alpha would composite the overlap twice and show a
/// darker wedge across the join. Being one shape also means one shadow outline and one clip for the
/// backdrop blur.
///
/// The indicator is drawn outside the body, so the shape expects a rect that already includes
/// `indicatorHeight` on the indicator's edge.
private struct LemonadeTooltipShape: Shape {
    let indicatorPlacement: LemonadeTooltipIndicatorPlacement
    let cornerRadius: CGFloat

    func path(in rect: CGRect) -> Path {
        let insets = indicatorPlacement.indicatorInsets
        let body = CGRect(
            x: insets.left,
            y: insets.top,
            width: rect.width - insets.left - insets.right,
            height: rect.height - insets.top - insets.bottom
        )
        let radius = min(cornerRadius, min(body.width, body.height) / 2)

        var path = Path()
        path.addRoundedRect(in: body, cornerSize: CGSize(width: radius, height: radius))

        if indicatorPlacement != .none {
            path.addPath(canonicalIndicatorPath(), transform: indicatorTransform(in: body))
        }

        return path
    }

    /// The indicator triangle. Its straight edges stop short of the notional apex and a cubic
    /// bridges the gap, which is what rounds the tip.
    ///
    /// It is built once in a canonical frame — base centred on the origin, apex pointing up — and then
    /// rotated onto whichever edge it belongs to, so the four edges cannot drift apart. The base is
    /// deliberately sunk `indicatorBodyOverlap` into the body, on the far side of the origin from the
    /// apex.
    ///
    /// The triangle is appended as a second sub-path and unioned by the non-zero fill rule. That works
    /// because the canonical path is wound the same way as CoreGraphics winds `addRoundedRect`, and a
    /// rotation preserves winding, so every edge joins cleanly; reverse either and they cancel where
    /// they overlap, leaving a hairline hole across the join.
    private func canonicalIndicatorPath() -> Path {
        let halfBase = LemonadeTooltipMetrics.indicatorBaseWidth / 2
        let apexY = -LemonadeTooltipMetrics.indicatorApexHeight
        let tipStart = LemonadeTooltipMetrics.indicatorTipStart
        let curvature = LemonadeTooltipMetrics.indicatorTipCurvature
        let overlap = LemonadeTooltipMetrics.indicatorBodyOverlap

        let tipStartX = -halfBase * (1 - tipStart)
        let tipEndX = -tipStartX
        let tipY = apexY * tipStart
        let controlY = tipY + (apexY - tipY) * curvature

        var path = Path()
        path.move(to: CGPoint(x: -halfBase, y: overlap))
        path.addLine(to: CGPoint(x: tipStartX, y: tipY))
        path.addCurve(
            to: CGPoint(x: tipEndX, y: tipY),
            control1: CGPoint(x: tipStartX * (1 - curvature), y: controlY),
            control2: CGPoint(x: tipEndX * (1 - curvature), y: controlY)
        )
        path.addLine(to: CGPoint(x: halfBase, y: overlap))
        path.closeSubpath()

        return path
    }

    /// Rotation onto the indicator's edge, then translation to its position along that edge.
    private func indicatorTransform(in body: CGRect) -> CGAffineTransform {
        let centerOffset = indicatorPlacement.indicatorCenterOffset(
            alongEdgeOfLength: indicatorPlacement.isOnVerticalEdge ? body.height : body.width
        )
        let origin: CGPoint
        switch indicatorPlacement.edge {
        case .top, .none: origin = CGPoint(x: body.minX + centerOffset, y: body.minY)
        case .bottom: origin = CGPoint(x: body.minX + centerOffset, y: body.maxY)
        case .left: origin = CGPoint(x: body.minX, y: body.minY + centerOffset)
        case .right: origin = CGPoint(x: body.maxX, y: body.minY + centerOffset)
        }

        return CGAffineTransform(translationX: origin.x, y: origin.y)
            .rotated(by: indicatorPlacement.edge.rotationAngle)
    }
}

/// Rectangle with independent top and bottom corner radii, for the cover slot.
///
/// `UnevenRoundedRectangle` would do this, but it is iOS 16+ and the package targets iOS 15.
private struct LemonadeTooltipCoverShape: Shape {
    let topRadius: CGFloat
    let bottomRadius: CGFloat

    func path(in rect: CGRect) -> Path {
        let maxRadius = min(rect.width, rect.height) / 2
        let top = min(topRadius, maxRadius)
        let bottom = min(bottomRadius, maxRadius)

        var path = Path()
        path.move(to: CGPoint(x: top, y: 0))
        path.addLine(to: CGPoint(x: rect.width - top, y: 0))
        path.addArc(
            center: CGPoint(x: rect.width - top, y: top),
            radius: top,
            startAngle: .degrees(-90),
            endAngle: .degrees(0),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: rect.width, y: rect.height - bottom))
        path.addArc(
            center: CGPoint(x: rect.width - bottom, y: rect.height - bottom),
            radius: bottom,
            startAngle: .degrees(0),
            endAngle: .degrees(90),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: bottom, y: rect.height))
        path.addArc(
            center: CGPoint(x: bottom, y: rect.height - bottom),
            radius: bottom,
            startAngle: .degrees(90),
            endAngle: .degrees(180),
            clockwise: false
        )
        path.addLine(to: CGPoint(x: 0, y: top))
        path.addArc(
            center: CGPoint(x: top, y: top),
            radius: top,
            startAngle: .degrees(180),
            endAngle: .degrees(270),
            clockwise: false
        )
        path.closeSubpath()

        return path
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeTooltip_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                sectionTitle("Indicator Placements")
                ForEach(LemonadeTooltipIndicatorPlacement.allCases, id: \.self) { placement in
                    LemonadeUi.Tooltip(
                        content: "Tap here to see everything you sold today.",
                        title: placement.displayName,
                        indicatorPlacement: placement
                    )
                }

                sectionTitle("Content Only")
                LemonadeUi.Tooltip(
                    content: "A tooltip with no title, no cover and no footer.",
                    indicatorPlacement: .topCenter
                )

                sectionTitle("With Close Button")
                LemonadeUi.Tooltip(
                    content: "Close buttons appear only when you pass onClose.",
                    title: "Dismissible",
                    indicatorPlacement: .topRight,
                    onClose: {},
                    closeContentDescription: "Close"
                )

                sectionTitle("With Cover")
                LemonadeUi.Tooltip(
                    content: "The cover slot takes any view — an illustration, a screenshot, a video.",
                    title: "Cover slot",
                    indicatorPlacement: .bottomCenter,
                    cover: { LemonadeTheme.colors.background.bgBrandSubtle }
                )

                sectionTitle("With Footer")
                LemonadeUi.Tooltip(
                    content: "The footer is a scoped slot — only the step counter and actions belong in it.",
                    title: "Step 1",
                    indicatorPlacement: .topLeft,
                    onClose: {},
                    closeContentDescription: "Close",
                    footer: { footer in
                        footer.stepCounter(currentStep: 1, totalSteps: 3)
                        Spacer()
                        footer.action("Skip", variant: .secondary) {}
                        footer.action("Next") {}
                    }
                )
            }
            .padding()
        }
        .background(LemonadeTheme.colors.background.bgSubtle)
        .previewLayout(.sizeThatFits)
    }

    @ViewBuilder
    private static func sectionTitle(_ text: String) -> some View {
        LemonadeUi.Text(
            text,
            textStyle: LemonadeTypography.shared.headingXSmall,
            color: LemonadeTheme.colors.content.contentSecondary
        )
        .padding(.top, 8)
    }
}
#endif
