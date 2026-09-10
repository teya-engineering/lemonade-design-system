import SwiftUI

// MARK: - Toast Animation Phase

/// Represents the animation state of a toast.
/// Using an enum ensures clear state transitions and predictable animations.
enum ToastAnimationPhase: Equatable {
    /// Toast is off-screen, waiting to enter
    case hidden
    /// Toast is animating in from the bottom
    case entering
    /// Toast is fully visible on screen
    case visible
    /// Toast is sliding out to the bottom (natural dismissal)
    case exitingSlide
    /// Toast is fading out with scale/blur (being replaced)
    case exitingFade

    /// The Y offset for this phase
    /// - Parameter toastHeight: The measured height of the toast (used for slide distance)
    func offset(for toastHeight: CGFloat) -> CGFloat {
        let slideDistance = toastHeight > 0 ? toastHeight : ToastAnimationConfig.fallbackSlideOffset
        switch self {
        case .hidden, .entering, .exitingSlide:
            return slideDistance
        case .visible, .exitingFade:
            return 0
        }
    }

    /// The scale for this phase
    var scale: CGFloat {
        switch self {
        case .exitingFade:
            return ToastAnimationConfig.fadeScale
        default:
            return 1.0
        }
    }

    /// The opacity for this phase
    var opacity: Double {
        switch self {
        case .exitingFade:
            return 0
        default:
            return 1.0
        }
    }

    /// The blur radius for this phase
    var blur: CGFloat {
        switch self {
        case .exitingFade:
            return ToastAnimationConfig.fadeBlur
        default:
            return 0
        }
    }

    /// Whether the toast should be visible in the view hierarchy
    var isPresented: Bool {
        switch self {
        case .hidden:
            return false
        default:
            return true
        }
    }
}

// MARK: - Toast Container View

/// Internal view that displays the toast overlay.
/// Uses explicit animation state management for predictable transitions.
struct LemonadeToastContainerView<Content: View>: View {
    @StateObject private var toastManager = LemonadeToastManager()

    /// The toast currently being displayed (may differ from manager during transitions)
    @State private var displayedToast: LemonadeToastItem?
    /// Current animation phase
    @State private var animationPhase: ToastAnimationPhase = .hidden
    /// Drag offset for swipe-to-dismiss gesture
    @State private var dragOffset: CGFloat = 0
    /// Trigger counter for sensory feedback (iOS 17+)
    @State private var feedbackTrigger: Int = 0
    /// Measured toast height for accurate slide animation
    @State private var toastHeight: CGFloat = 0
    /// Tracked exit task to prevent race conditions from fire-and-forget Tasks
    @State private var exitTask: Task<Void, Never>?

    let content: Content

    var body: some View {
        content
            .environmentObject(toastManager)
            .overlay(alignment: .bottom) {
                toastOverlay
            }
            .onChange(of: toastManager.currentToast?.id) { newToastId in
                handleToastChange(newToastId: newToastId)
            }
            .modifier(ToastSensoryFeedbackModifier(
                trigger: feedbackTrigger,
                voice: displayedToast?.voice
            ))
    }

    // MARK: - Toast Change Handling

    private func handleToastChange(newToastId: UUID?) {
        guard newToastId != nil else {
            dismissDisplayedToast()
            return
        }
        if displayedToast != nil {
            replaceDisplayedToast()
        } else {
            enterNewToast()
        }
    }

    private func replaceDisplayedToast() {
        exitCurrentToast(withFade: true) {
            enterNewToast()
        }
    }

    private func dismissDisplayedToast() {
        guard displayedToast != nil else { return }
        exitCurrentToast(withFade: false, completion: nil)
    }

    private func enterNewToast() {
        displayedToast = toastManager.currentToast
        animationPhase = .entering
        feedbackTrigger += 1

        withAnimation(ToastAnimationConfig.spring) {
            animationPhase = .visible
        }
    }

    private func exitCurrentToast(withFade: Bool, completion: (() -> Void)?) {
        let exitPhase: ToastAnimationPhase = withFade ? .exitingFade : .exitingSlide
        let animation: Animation = withFade ? ToastAnimationConfig.smooth : ToastAnimationConfig.spring

        withAnimation(animation) {
            animationPhase = exitPhase
        }

        exitTask?.cancel()
        exitTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: ToastAnimationConfig.duration))
            guard !Task.isCancelled else { return }
            finishExit(completion: completion)
        }
    }

    private func finishExit(completion: (() -> Void)?) {
        displayedToast = nil
        animationPhase = .hidden
        completion?()
    }

    // MARK: - Toast Overlay

    @ViewBuilder
    private var toastOverlay: some View {
        if let toast = toastToRender, animationPhase.isPresented {
            ToastItemView(
                toast: toast,
                onDismiss: { toastManager.dismiss() },
                onDragChanged: handleDragChanged,
                onDragEnded: handleDragEnded,
                onHeightChanged: { height in
                    toastHeight = height
                }
            )
            .scaleEffect(animationPhase.scale, anchor: .bottom)
            .offset(y: animationPhase.offset(for: toastHeight) + dragOffset)
            .animatableBlur(radius: animationPhase.blur)
            .opacity(animationPhase.opacity)
            .animation(ToastAnimationConfig.spring, value: animationPhase)
            .animation(ToastAnimationConfig.interactiveSpring, value: dragOffset)
        }
    }

    /// The toast to draw, chosen by identity rather than by animation phase.
    ///
    /// A manager holding the same `id` we are showing has replaced that toast's content in place
    /// — `LemonadeToastPolicy.replace` — so the update is drawn where the pill stands, with no
    /// transition. A different `id` means a transition is running or is about to start, and until
    /// `enterNewToast` swaps it in, the toast on screen is still the one that must be drawn.
    ///
    /// Deliberately not switched on `animationPhase`: SwiftUI computes `body` before running the
    /// `onChange` that starts the exit, so an incoming toast would be rendered for a frame before
    /// the phase caught up — a flash of the new content, then the old one fading over it.
    private var toastToRender: LemonadeToastItem? {
        guard let displayedToast else { return toastManager.currentToast }
        guard let current = toastManager.currentToast, current.id == displayedToast.id else {
            return displayedToast
        }
        return current
    }

    // MARK: - Drag Gesture Handling

    private func handleDragChanged(_ translation: CGFloat) {
        dragOffset = max(0, translation)
    }

    private func handleDragEnded(_ translation: CGFloat) {
        if translation > ToastAnimationConfig.dragDismissThreshold {
            toastManager.dismiss()
        }
        dragOffset = 0
    }
}

// MARK: - Toast Item View

/// Individual toast view with gesture handling.
/// Only the toast itself is interactive - the rest passes through touches.
struct ToastItemView: View {
    let toast: LemonadeToastItem
    let onDismiss: () -> Void
    let onDragChanged: (CGFloat) -> Void
    let onDragEnded: (CGFloat) -> Void
    let onHeightChanged: (CGFloat) -> Void

    var body: some View {
        VStack {
            Spacer()
                .allowsHitTesting(false)

            toastContent
        }
        .frame(maxWidth: .infinity, alignment: .bottom)
        .allowsHitTesting(true)
        .id(toast.id)
    }

    private var toastContent: some View {
        VStack(spacing: 0) {
            // Only the pill is hittable; the spacers on either side pass touches through to the content
            // beneath, so taps left/right of the toast reach it (a full-width frame would swallow them).
            HStack(spacing: 0) {
                Spacer(minLength: 0).allowsHitTesting(false)
                LemonadeUi.Toast(
                    label: toast.label,
                    voice: toast.voice,
                    icon: toast.icon,
                    actionLabel: toast.actionLabel,
                    onAction: toast.onAction
                )
                .contentShape(Rectangle())
                .simultaneousGesture(dismissGesture)
                // Layout spacing only, and deliberately applied *after* the contentShape. Applied
                // before it, this 72pt of empty space above the pill became hit-testable and
                // swallowed taps aimed at the content beneath — the same problem the spacers
                // above solve horizontally.
                .padding(.top, .space.spacing1800)
                Spacer(minLength: 0).allowsHitTesting(false)
            }
            .padding(.leading, resolvedPadding(toast.paddingValues?.leading, default: .space.spacing200))
            .padding(.trailing, resolvedPadding(toast.paddingValues?.trailing, default: .space.spacing200))

            // Clears whatever sits below the toast (e.g. a bottom action button). Kept out of
            // the contentShape/gesture above so it stays non-interactive and touches pass through.
            Color.clear
                .frame(height: resolvedPadding(toast.paddingValues?.bottom, default: .space.spacing400))
                .allowsHitTesting(false)
        }
        .background(
            GeometryReader { geometry in
                Color.clear
                    .onAppear {
                        onHeightChanged(geometry.size.height)
                    }
                    .onChange(of: geometry.size.height) { newHeight in
                        onHeightChanged(newHeight)
                    }
            }
            .allowsHitTesting(false)
        )
    }

    private var dismissGesture: some Gesture {
        DragGesture()
            .onChanged { value in
                guard toast.isDismissible else { return }
                onDragChanged(value.translation.height)
            }
            .onEnded { value in
                guard toast.isDismissible else { return }
                onDragEnded(value.translation.height)
            }
    }
}

// A zero edge is treated as "not overridden" and falls back to the default — `show()` isn't a
// SwiftUI view, so a caller can't pull the real spacing200/spacing400 token values to build an
// EdgeInsets that only overrides one edge. `top` is never read: the toast is always bottom-anchored
// with intrinsic height, so extra top inset only adds invisible space above it — no visible effect.
private func resolvedPadding(_ override: CGFloat?, default defaultValue: CGFloat) -> CGFloat {
    guard let override, override > 0 else { return defaultValue }
    return override
}
