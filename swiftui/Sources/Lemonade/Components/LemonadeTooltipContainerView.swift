import SwiftUI

// MARK: - Layout Metrics

private enum LemonadeTooltipLayout {
    /// Gap between the indicator tip and the anchor it points at.
    static let anchorGap: CGFloat = 4

    /// Smallest distance the tooltip keeps from the edges of the container.
    static let containerMargin: CGFloat = 8

    /// Space left around the anchor when the spotlight scrim punches through.
    static let spotlightPadding: CGFloat = 4

    /// Corner radius of the spotlight cut-out.
    static let spotlightRadius: CGFloat = 12

    /// Scale the tooltip grows from as it enters.
    static let enterInitialScale: CGFloat = 0.8

    /// Entry spring. The damping is below 1 so the scale overshoots and settles — the "bubble" feel.
    ///
    /// Stiffness 400 at unit mass gives ω = 20 rad/s, so response = 2π/ω ≈ 0.31s.
    static let enterAnimation: Animation = .spring(response: 0.31, dampingFraction: 0.55)

    /// Exit is a plain fade, with no scale.
    static let exitAnimation: Animation = .easeOut(duration: 0.09)

    /// The scrim only fades — scaling it with the tooltip would sweep the dimming across the screen.
    static let scrimAnimation: Animation = .easeInOut(duration: 0.18)

    /// Matches `exitAnimation`, used to unmount once the fade has finished.
    static let exitDuration: TimeInterval = 0.09

}

// MARK: - Anchor Registration

/// Reports this view's global frame to the manager under `key`.
///
/// Frames are reported in global coordinates and translated by the container, rather than published
/// through a `PreferenceKey`: preferences do not propagate out of a `NavigationStack` destination,
/// so an anchor on a pushed screen would never reach the container. The environment does propagate
/// down, so writing to the manager directly works from anywhere inside the container.
private struct LemonadeTooltipAnchorModifier: ViewModifier {
    let key: String

    @EnvironmentObject private var manager: LemonadeTooltipManager

    func body(content: Content) -> some View {
        content.background(
            GeometryReader { proxy in
                let frame = proxy.frame(in: .global)

                Color.clear
                    .onAppear { manager.updateAnchor(key, frame: frame) }
                    .onChange(of: frame) { newFrame in
                        manager.updateAnchor(key, frame: newFrame)
                    }
                    .onDisappear { manager.removeAnchor(key) }
            }
        )
    }
}

public extension View {
    /// Registers this view as a tooltip anchor under `key`, so ``LemonadeTooltipManager/show(anchor:content:title:indicatorPlacement:scrim:dismissOnOutsideTap:showCloseButton:closeContentDescription:)``
    /// and ``LemonadeTooltipManager/startTour(steps:labels:scrim:showCloseButton:onFinish:onSkip:)`` can point at it.
    ///
    /// Keys are global to the enclosing `lemonadeTooltipContainer()`. A tooltip aimed at an anchor
    /// that is not on screen simply does not render — which is what lets a tour walk across several
    /// screens.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Button(label: "Takings") { }
    ///     .lemonadeTooltipAnchor("takings")
    /// ```
    func lemonadeTooltipAnchor(_ key: String) -> some View {
        modifier(LemonadeTooltipAnchorModifier(key: key))
    }

    /// Adds a tooltip container to this view hierarchy.
    ///
    /// Apply this modifier to your root view to enable anchored tooltips and guided tours. It
    /// provides a ``LemonadeTooltipManager`` as an environment object and renders the tooltip
    /// overlay — the scrim and the anchored tooltip itself — above your content.
    ///
    /// ## Usage
    /// ```swift
    /// @main
    /// struct MyApp: App {
    ///     var body: some Scene {
    ///         WindowGroup {
    ///             ContentView()
    ///                 .lemonadeTooltipContainer()
    ///         }
    ///     }
    /// }
    /// ```
    func lemonadeTooltipContainer() -> some View {
        LemonadeTooltipContainerView(content: self)
    }
}

// MARK: - Container

struct LemonadeTooltipContainerView<Content: View>: View {
    let content: Content

    @StateObject private var manager = LemonadeTooltipManager()

    var body: some View {
        // The overlay hangs off `content` rather than wrapping it in a ZStack: a NavigationStack
        // nested inside a GeometryReader/ZStack takes over the full screen through UIKit and paints
        // over its SwiftUI siblings, so an overlay built that way never becomes visible.
        content
            .environmentObject(manager)
            .overlay {
                // Its own view, and not this body, is what observes the anchor geometry. This body
                // builds `content` — the whole app — so reading scroll-rate geometry here would
                // re-diff the entire view tree several times a frame.
                LemonadeTooltipOverlayView(manager: manager)
                    // Without this the overlay is inset by the safe area while anchors are reported
                    // in global coordinates, so everything would sit a status bar's height too low.
                    .ignoresSafeArea()
            }
    }
}

// MARK: - Overlay

private struct LemonadeTooltipOverlayView: View {
    @ObservedObject private var manager: LemonadeTooltipManager
    @ObservedObject private var geometry: LemonadeTooltipGeometry

    @State private var tooltipSize: CGSize = .zero
    @State private var retained: LemonadeTooltipPresentation?
    @State private var isVisible = false
    // Tracked apart from `isVisible`, which restarts on every step so each tooltip gets its own
    // entrance. The scrim spans the whole tour, so it must only fade when the tour itself ends.
    @State private var isScrimVisible = false

    init(manager: LemonadeTooltipManager) {
        _manager = ObservedObject(wrappedValue: manager)
        _geometry = ObservedObject(wrappedValue: manager.geometry)
    }

    var body: some View {
        GeometryReader { proxy in
            let containerFrame = proxy.frame(in: .global)

            ZStack(alignment: .topLeading) {
                // `retained` outlives manager.presentation being cleared so the exit
                // animation still has something to fade out. Scale and opacity are animated
                // explicitly rather than via `.transition`: an insertion transition on a
                // subtree that appears in the same pass as its data does not reliably pick
                // up the transaction's animation, and the tooltip simply popped in.
                //
                // The anchor comes from the geometry's tracked frame rather than a lookup by key,
                // so it keeps following its element while the screen scrolls without every other
                // anchor's movement costing a view update.
                if let presentation = retained,
                   let anchor = geometry.presentedAnchorFrame {
                    overlay(
                        presentation: presentation,
                        anchor: anchor,
                        containerSize: proxy.size
                    )
                }
            }
            .onChange(of: manager.presentation?.id) { _ in
                if let presentation = manager.presentation {
                    setRetained(presentation)
                    isVisible = false
                    withAnimation(LemonadeTooltipLayout.scrimAnimation) {
                        isScrimVisible = true
                    }
                    // A frame at the start scale before springing up to 1, so the growth is
                    // actually animated rather than applied on the same pass as the mount.
                    DispatchQueue.main.async {
                        withAnimation(LemonadeTooltipLayout.enterAnimation) {
                            isVisible = true
                        }
                    }
                } else {
                    withAnimation(LemonadeTooltipLayout.exitAnimation) { isVisible = false }
                    withAnimation(LemonadeTooltipLayout.scrimAnimation) {
                        isScrimVisible = false
                    }
                    let dismissed = retained?.id
                    DispatchQueue.main.asyncAfter(
                        deadline: .now() + LemonadeTooltipLayout.exitDuration
                    ) {
                        // Cleared only once the exit fade has unmounted it: dropping the
                        // tracked frame any earlier would cut the tooltip instead of fading it.
                        if manager.presentation == nil, retained?.id == dismissed {
                            setRetained(nil)
                        }
                    }
                }
            }
            .onAppear { geometry.updateContainerFrame(containerFrame) }
            .onChange(of: containerFrame) { newFrame in
                geometry.updateContainerFrame(newFrame)
            }
        }
    }

    /// Sets the presentation the overlay is mounted for, and the anchor whose frame is tracked.
    ///
    /// These two must always move together, in the same transaction: the tooltip's anchor frame
    /// has to be resolved on the very next pass, or the mount misses the enter animation.
    private func setRetained(_ presentation: LemonadeTooltipPresentation?) {
        retained = presentation
        geometry.track(presentation?.anchor)
    }

    // MARK: Overlay

    @ViewBuilder
    private func overlay(
        presentation: LemonadeTooltipPresentation,
        anchor: CGRect,
        containerSize: CGSize
    ) -> some View {
        let edge = LemonadeTooltipPositioning.edge(
            anchor: anchor,
            containerSize: containerSize,
            forcedPlacement: presentation.indicatorPlacement
        )
        let placement = presentation.indicatorPlacement
            ?? LemonadeTooltipPositioning.indicatorPlacement(
                anchor: anchor,
                containerSize: containerSize,
                tooltipWidth: tooltipSize.width > 0 ? tooltipSize.width : 280,
                edge: edge
            )
        let origin = LemonadeTooltipPositioning.origin(
            anchor: anchor,
            containerSize: containerSize,
            tooltipSize: tooltipSize,
            placement: placement
        )

        ZStack(alignment: .topLeading) {
            // Kept outside the `.id` below. Identity is per step, so anything carrying it is torn
            // down and rebuilt when a tour advances — which would flash the backdrop between steps.
            scrim(presentation: presentation, anchor: anchor)

            tooltip(presentation: presentation, placement: placement)
                .background(
                    GeometryReader { proxy in
                        Color.clear.preference(
                            key: LemonadeTooltipSizePreferenceKey.self,
                            value: proxy.size
                        )
                    }
                )
                // Grows out of its indicator rather than its own centre, so it reads as coming from
                // the element it points at. Exit is opacity only — no scale — so it just fades.
                .scaleEffect(
                    isVisible ? 1 : LemonadeTooltipLayout.enterInitialScale,
                    anchor: placement.transformAnchor
                )
                .offset(x: origin.x, y: origin.y)
                // Also hidden until measured, so it never flashes at the wrong position.
                .opacity(tooltipSize == .zero || !isVisible ? 0 : 1)
                .onPreferenceChange(LemonadeTooltipSizePreferenceKey.self) { size in
                    tooltipSize = size
                }
                // Deliberately not reset when a step disappears: the outgoing step's teardown runs
                // after the incoming one has measured, so clearing it there would race and leave the
                // new tooltip stuck at zero size, and therefore invisible. Carrying the previous size
                // over is harmless — it is only used until the new measurement lands a frame later.
                .id(presentation.id)
        }
    }

    @ViewBuilder
    private func scrim(
        presentation: LemonadeTooltipPresentation,
        anchor: CGRect
    ) -> some View {
        let scrimColor = LemonadeTheme.colors.background.bgAlwaysDark
            .opacity(LemonadeTheme.opacity.base.opacity40)

        Group {
            switch presentation.scrim {
            case .none:
                Color.clear

            case .dim:
                scrimColor

            case .spotlight:
                GeometryReader { proxy in
                    Path { path in
                        path.addRect(CGRect(origin: .zero, size: proxy.size))
                        path.addRoundedRect(
                            in: anchor.insetBy(
                                dx: -LemonadeTooltipLayout.spotlightPadding,
                                dy: -LemonadeTooltipLayout.spotlightPadding
                            ),
                            cornerSize: CGSize(
                                width: LemonadeTooltipLayout.spotlightRadius,
                                height: LemonadeTooltipLayout.spotlightRadius
                            )
                        )
                    }
                    .fill(scrimColor, style: FillStyle(eoFill: true))
                }
            }
        }
        .opacity(isScrimVisible ? 1 : 0)
        .contentShape(Rectangle())
        .onTapGesture {
            // The tap is always swallowed so the UI underneath cannot be acted on while a tooltip
            // is up; whether it also dismisses is the caller's choice.
            if presentation.dismissOnOutsideTap {
                manager.dismiss()
            }
        }
    }

    private func tooltip(
        presentation: LemonadeTooltipPresentation,
        placement: LemonadeTooltipIndicatorPlacement
    ) -> some View {
        let onClose: (() -> Void)? = presentation.showCloseButton ? { manager.dismiss() } : nil

        let footer: ((LemonadeTooltipFooterScope) -> AnyView)?
        if presentation.isTourStep, let labels = presentation.tourLabels {
            footer = { scope in
                AnyView(
                    tourFooter(footer: scope, presentation: presentation, labels: labels)
                )
            }
        } else {
            footer = presentation.footer
        }

        return LemonadeTooltipView(
            content: presentation.content,
            title: presentation.title,
            indicatorPlacement: placement,
            onClose: onClose,
            closeContentDescription: presentation.closeContentDescription,
            cover: presentation.cover,
            footer: footer
        )
    }

    @ViewBuilder
    private func tourFooter(
        footer: LemonadeTooltipFooterScope,
        presentation: LemonadeTooltipPresentation,
        labels: LemonadeTooltipTourLabels
    ) -> some View {
        let isLast = presentation.stepIndex == presentation.stepCount - 1

        // `1 of 1` says nothing, so a single-step tour gets no counter at all.
        if presentation.stepCount > 1 {
            footer.stepCounter(
                currentStep: presentation.stepIndex + 1,
                totalSteps: presentation.stepCount,
                separator: labels.stepSeparator
            )
        }

        Spacer()

        if let skip = labels.skip, !isLast {
            footer.action(skip, variant: .secondary) { manager.skip() }
        }

        footer.action(isLast ? labels.done : labels.next) { manager.next() }
    }
}

private struct LemonadeTooltipSizePreferenceKey: PreferenceKey {
    static var defaultValue: CGSize { .zero }

    static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
        let next = nextValue()
        if next != .zero {
            value = next
        }
    }
}

// MARK: - Positioning

enum LemonadeTooltipPositioning {

    /// Which edge of the tooltip its indicator protrudes from, and therefore which side of `anchor`
    /// the tooltip sits on.
    ///
    /// A caller that forces a placement is choosing the side too — an indicator drawn on top of the
    /// body only makes sense with the body below the anchor. Without one, and for `.none` which names
    /// no edge, the tooltip goes below an anchor in the top half of the container and above one in the
    /// bottom half. Never returns `.none`: the tooltip is always on one side of its anchor, even when
    /// no indicator is drawn.
    static func edge(
        anchor: CGRect,
        containerSize: CGSize,
        forcedPlacement: LemonadeTooltipIndicatorPlacement?
    ) -> LemonadeTooltipIndicatorEdge {
        if let forcedEdge = forcedPlacement?.edge, forcedEdge != .none {
            return forcedEdge
        }
        return anchor.midY < containerSize.height / 2 ? .top : .bottom
    }

    /// Picks the placement whose indicator lands closest to the centre of `anchor` once the tooltip
    /// has been kept inside the container. The indicator has only three possible positions, so near
    /// an edge the left or right variant reaches an anchor that the centred one cannot.
    ///
    /// Only the top and bottom placements are candidates: choosing to sit beside an anchor rather than
    /// above or below it is a deliberate call, so the left and right ones have to be asked for.
    static func indicatorPlacement(
        anchor: CGRect,
        containerSize: CGSize,
        tooltipWidth: CGFloat,
        edge: LemonadeTooltipIndicatorEdge
    ) -> LemonadeTooltipIndicatorPlacement {
        let candidates: [LemonadeTooltipIndicatorPlacement] = edge == .bottom
            ? [.bottomCenter, .bottomLeft, .bottomRight]
            : [.topCenter, .topLeft, .topRight]

        let margin = LemonadeTooltipLayout.containerMargin
        let maxX = max(margin, containerSize.width - tooltipWidth - margin)

        var best = candidates[0]
        var bestError = CGFloat.greatestFiniteMagnitude

        for candidate in candidates {
            let offset = candidate.indicatorCenterOffset(alongEdgeOfLength: tooltipWidth)
            let x = min(max(anchor.midX - offset, margin), maxX)
            let error = abs(x + offset - anchor.midX)
            if error < bestError {
                bestError = error
                best = candidate
            }
        }

        return best
    }

    /// Where to place the tooltip inside the container so its indicator points at `anchor`, kept
    /// inside the container's margins.
    ///
    /// One rule, applied per edge: along the indicator's own axis the tooltip clears the corresponding
    /// anchor bound by `anchorGap`; across it the indicator lines up with the anchor's centre. The
    /// indicator tip sits on the tooltip's own bounds, so the gap is measured from those.
    ///
    /// The edge is resolved here rather than passed in, so it cannot contradict `placement`, and so
    /// `.none` — which names no edge — still lands above or below its anchor.
    static func origin(
        anchor: CGRect,
        containerSize: CGSize,
        tooltipSize: CGSize,
        placement: LemonadeTooltipIndicatorPlacement
    ) -> CGPoint {
        let resolvedEdge = edge(
            anchor: anchor,
            containerSize: containerSize,
            forcedPlacement: placement
        )
        let margin = LemonadeTooltipLayout.containerMargin
        let gap = LemonadeTooltipLayout.anchorGap

        let besideAnchor = resolvedEdge == .left || resolvedEdge == .right
        let alongIndicatorEdge = placement.indicatorCenterOffset(
            alongEdgeOfLength: besideAnchor ? tooltipSize.height : tooltipSize.width
        )
        let centredOnAnchorX = anchor.midX - alongIndicatorEdge
        let centredOnAnchorY = anchor.midY - alongIndicatorEdge

        let raw: CGPoint
        switch resolvedEdge {
        case .top, .none:
            raw = CGPoint(x: centredOnAnchorX, y: anchor.maxY + gap)
        case .bottom:
            raw = CGPoint(x: centredOnAnchorX, y: anchor.minY - gap - tooltipSize.height)
        case .left:
            raw = CGPoint(x: anchor.maxX + gap, y: centredOnAnchorY)
        case .right:
            raw = CGPoint(x: anchor.minX - gap - tooltipSize.width, y: centredOnAnchorY)
        }

        let maxX = max(margin, containerSize.width - tooltipSize.width - margin)
        let maxY = max(margin, containerSize.height - tooltipSize.height - margin)

        return CGPoint(
            x: min(max(raw.x, margin), maxX),
            y: min(max(raw.y, margin), maxY)
        )
    }
}
