import SwiftUI

// MARK: - Settle policy

/// The spring a released row travels on, and the one animation the reveal rides.
///
/// Fitted to iOS frame by frame: a settle of 90pt lands within 0.123pt of a critically damped
/// spring at ω = 12.5 rad/s across the whole animation, which is inside the pixel quantisation of
/// the measurement. Stiffness is ω², damping is 2ω at unit mass — critically damped, so the row
/// arrives without springing past and coming back.
private let settleStiffness: Double = 156.25
private let settleDamping: Double = 25

/// That spring, continuing from the speed the finger let go at.
///
/// A settle that starts from rest is the thing that reads as unpolished: the row stalls for a frame
/// where the drag ended and then picks itself back up. The same fit recovers the release velocity
/// carried into the animation, so it is handed on rather than thrown away.
///
/// - Parameter velocity: pt/s at release, positive while still travelling open.
/// - Parameter distance: what `travel` is about to change by, which is what SwiftUI measures the
///   initial velocity against.
private func settle(velocity: CGFloat = 0, over distance: CGFloat = 0) -> Animation {
    .interpolatingSpring(
        mass: 1,
        stiffness: settleStiffness,
        damping: settleDamping,
        initialVelocity: distance == 0 ? 0 : velocity / distance
    )
}

/// Fraction of the row's width a drag must cross for a full swipe to commit.
private let commitFraction: CGFloat = 0.5

/// How far a finger travels before the row claims the drag.
///
/// Further than a scroll view needs to start scrolling, which is what leaves a vertical drag to it:
/// `DragGesture` claims a touch on distance in any direction, and the row's own check that the drag
/// is horizontal comes too late — it stops the row moving, but the scroll has already lost the
/// touch. Losing the race is the only way to give it back.
private let claimDistance: CGFloat = 24

/// Deceleration a released row is left to coast on, `UIScrollView`'s normal rate.
private let decelerationRate: CGFloat = 0.998

/// Where a drag that let go at `velocity` pt/s would have come to rest, by Apple's projection: the
/// distance a second of that speed covers, scaled by how long the deceleration takes to eat it.
private func projectedTravel(from travel: CGFloat, velocity: CGFloat) -> CGFloat {
    travel + velocity / 1000 * decelerationRate / (1 - decelerationRate)
}

/// Where a released drag lands.
enum SwipeSettleTarget {
    case closed
    case open
    case committed
}

/// Resolves where a released drag settles.
///
/// A commit outranks everything: once the row has crossed `commitFraction` of its width the gesture
/// has already been read as a full swipe, and dragging back at speed without crossing the threshold
/// again should not undo it.
///
/// Otherwise the row stays open only if the drag would have brought the first action all the way
/// out — not where the finger let go, but where the row's own momentum was taking it. A flick opens
/// a row the finger never carried that far, and a slow drag of the same length does not, off one
/// threshold rather than a speed rule sitting in front of it. It is also what closes a row flung
/// back: the projection lands short of the threshold.
///
/// The commit is the exception, and reads `travel` itself. Momentum must not fire an action across
/// a row the finger never crossed.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, always positive.
///   - velocity: pt/s at release, positive while still travelling open.
///   - firstActionReveal: travel that brings the first action fully out, which is what a release
///     has to reach for the row to stay open.
///   - rowWidth: full width of the row.
///   - allowsFullSwipe: whether a drag across the row may commit the first action.
func resolveSwipeSettle(
    travel: CGFloat,
    velocity: CGFloat,
    firstActionReveal: CGFloat,
    rowWidth: CGFloat,
    allowsFullSwipe: Bool
) -> SwipeSettleTarget {
    if allowsFullSwipe, travel >= rowWidth * commitFraction {
        return .committed
    }
    // Nothing to open onto: there are no actions.
    if firstActionReveal <= 0 {
        return .closed
    }
    return projectedTravel(from: travel, velocity: velocity) >= firstActionReveal ? .open : .closed
}

/// How much of an action's arrival is held back for the end.
///
/// The last of it springs into place when the row has revealed the action fully, so it lands rather
/// than simply stopping. Measured off iOS, where an action's scale rings about 3% past its resting
/// size before settling — a bounce this shallow overshoots by about that, and at 1.5pt of a 48pt
/// action it stays well inside the gap the action sits in.
private let bumpDepth: CGFloat = 0.12

/// How far out an action has to be before it lands.
///
/// Not all the way: the row settles onto its last action asymptotically, so a trigger sitting on
/// that exact position is only reached as the spring runs out — the action would then start its
/// landing after the row had already stopped. Firing at most of the way out puts the two together,
/// and staggers a pair, each landing as the row clears it.
private let bumpTrigger: CGFloat = 0.7

/// The spring that lands it. Bouncy where the row's own settle is not: this is the one place a
/// little ring is the point.
///
/// Quick, so it lands with the row rather than trailing it: the row's own settle is 0.5s, and a
/// bump that long is still arriving after the row has stopped, which reads as a second movement
/// rather than the end of the first. `dampingFraction` is what makes it ring — 0.4 overshoots by
/// about 2%, the same as iOS.
private let bump: Animation = .spring(response: 0.25, dampingFraction: 0.4)

// MARK: - Reveal policy

/// How the action strip draws itself part-way through a reveal.
struct SwipeStripReveal: Equatable {
    /// 0…1. The strip scales about its centre by this and fades by the same amount, so an action
    /// arriving grows and appears as one movement.
    let scale: CGFloat
    /// Width added to the leading side of the first action once the strip is at full size.
    let stretch: CGFloat
}

/// Resolves how far one action has been revealed.
///
/// An action is the gap the row has opened for it: `travel` less everything between it and the row's
/// trailing edge is the width it wants, and the rest follows from that. Short of its own width it
/// scales into it; past it the first action — the one a full swipe fires — stretches to fill it.
/// Because that width *is* the gap, an action never reaches under the row, whatever the row is
/// drawn on.
///
/// Resolved per action rather than for the strip, so each one arrives as the row uncovers it: the
/// second action of a pair waits until the row has cleared the first.
///
/// Nothing here is animated. Everything is a function of where the row is, so an action can never
/// get out of step with the row it belongs to — a spring chasing the gap overshoots into it the
/// moment the row comes back, and settling animates a spring of its own that no longer agrees with
/// the row's. The row's own animation carries all of it.
///
/// The stretch waits for the whole strip, not for this action's share of it. An action that grew
/// into its own leftover would grow over the actions still queued behind it — for the outermost of
/// a pair, from the moment the row rests open.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, always positive.
///   - actionReveal: travel that rests the row on this action, so the strip's width up to and
///     including it.
///   - stripReveal: travel that rests the row on every action, which is where a stretch starts.
///   - actionWidth: the action's own width, which is what it scales towards.
func resolveSwipeStripReveal(
    travel: CGFloat,
    actionReveal: CGFloat,
    stripReveal: CGFloat,
    actionWidth: CGFloat
) -> SwipeStripReveal {
    guard actionWidth > 0 else { return SwipeStripReveal(scale: 0, stretch: 0) }
    // An action grows over the last half of its own width. Scaling about its centre, that walks its
    // leading edge out at exactly the rate the row is travelling — so from nothing to full size,
    // and on past it as the capsule stretches, the action's leading edge sits one leading gap ahead
    // of the row. The action *is* the gap the row has opened, at every point of the drag.
    let growth = actionWidth / 2
    let scale = min(max((travel - (actionReveal - growth)) / growth, 0), 1)
    return SwipeStripReveal(scale: scale, stretch: max(travel - stripReveal, 0))
}

/// Travel that rests a row on the first `count` of its actions. The whole reveal at the action
/// count, and one action's own share of it at its index plus one.
private func swipeRevealWidth(through count: Int) -> CGFloat {
    guard count > 0 else { return 0 }
    let gaps = CGFloat(count - 1) * LemonadeTheme.spaces.spacing200
    return LemonadeTheme.spaces.spacing300 + CGFloat(count) * LemonadeTheme.sizes.size1200 + gaps
        + LemonadeTheme.spaces.spacing400
}

/// Opacity an action being pushed along has dimmed to once the row has travelled its whole width.
/// `opacity20`, held as a plain number so the reveal stays resolvable without a theme.
private let displacedFloor: CGFloat = 0.2

/// Opacity of the actions a stretching one is pushing along.
///
/// A swipe past the commit threshold is taking the row over, and the actions it is displacing recede
/// as it does rather than riding out at full strength: unchanged through the reveal, down to
/// `displacedFloor` by the time the row has travelled its whole width.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, always positive.
///   - rowWidth: full width of the row.
func resolveSwipeDisplacedOpacity(travel: CGFloat, rowWidth: CGFloat) -> CGFloat {
    let takeover = rowWidth * commitFraction
    guard rowWidth > takeover else { return 1 }
    let progress = min(max((travel - takeover) / (rowWidth - takeover), 0), 1)
    return 1 - (1 - displacedFloor) * progress
}

// MARK: - LemonadeSwipeAction

/// One action revealed behind a `LemonadeUi.SwipeActionRow`.
///
/// `contentDescription` has no default because it is what publishes the action to VoiceOver, where
/// the gesture itself is invisible.
public struct LemonadeSwipeAction {
    let icon: LemonadeIcon
    let contentDescription: String
    let onClick: () -> Void
    let variant: LemonadeButtonVariant

    public init(
        icon: LemonadeIcon,
        contentDescription: String,
        onClick: @escaping () -> Void,
        variant: LemonadeButtonVariant = .critical
    ) {
        self.icon = icon
        self.contentDescription = contentDescription
        self.onClick = onClick
        self.variant = variant
    }
}

// MARK: - Row

struct LemonadeSwipeActionRowView<Content: View>: View {
    let actions: [LemonadeSwipeAction]
    let enabled: Bool
    let allowsFullSwipe: Bool
    let showDivider: Bool
    @Binding var open: Bool
    @ViewBuilder let content: () -> Content

    @State private var travel: CGFloat = 0
    /// Where `travel` stood when this drag was claimed. Released by the cancel path, so the next
    /// drag has to earn the claim again.
    @State private var dragOrigin: CGFloat?
    /// How far the finger had already moved by then, which the row does not owe: without it the row
    /// jumps the whole claim distance the moment it starts following.
    @State private var claimTranslation: CGFloat = 0
    /// The same origin, cleared only by `onEnded`, which is what `onEnded` guards on. Keeping the
    /// two apart is what lets the cancel path snap back without stealing the settle.
    @State private var settleOrigin: CGFloat?
    @State private var rowWidth: CGFloat = 0
    @State private var committed = false
    @GestureState private var isDragging = false
    @Environment(\.layoutDirection) private var layoutDirection

    /// A reveal on the trailing edge travels left in LTR and right in RTL.
    private var towardsTrailing: CGFloat { layoutDirection == .rightToLeft ? 1 : -1 }

    /// Where an open row rests: every action, plus the padding they sit in. Computed rather than
    /// measured: the strip changes width as the first action stretches, so anything measured off it
    /// would move under the model driving it.
    private var revealWidth: CGFloat { swipeRevealWidth(through: actions.count) }

    var body: some View {
        VStack(spacing: 0) {
            ZStack(alignment: .trailing) {
                SwipeActionStrip(
                    travel: travel,
                    actions: actions,
                    committed: committed,
                    rowWidth: rowWidth,
                    towardsTrailing: towardsTrailing
                )
                content()
                    // A row under the finger rests on the list item's own press highlight rather
                    // than on a surface of its own: same fill, same radius, same gutter. It is on
                    // for the whole gesture, not proportional to the travel — the row is being
                    // handled from the first pixel.
                    .background(
                        RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                            .fill(LemonadeTheme.colors.interaction.bgSubtleInteractive)
                            .opacity(travel > 0 ? 1 : 0)
                            .padding(LemonadeTheme.spaces.spacing100)
                    )
                    .offset(x: travel * towardsTrailing)
                    // Only intercepts taps while open, so a closed row's own button still works.
                    .overlay {
                        if open {
                            Color.clear
                                .contentShape(Rectangle())
                                .onTapGesture { open = false }
                        }
                    }
            }
            .clipped()
            .background(
                GeometryReader { proxy in
                    Color.clear
                        .onAppear { rowWidth = proxy.size.width }
                        .onChange(of: proxy.size.width) { rowWidth = $0 }
                }
            )
            // High priority, because the wrapped row is usually a `Button` and a plain `.gesture`
            // ranks below the gestures of the view it is attached to: whichever of the two claimed
            // the touch first won, so the same drag opened the row or did nothing depending on
            // where it started. The 10pt minimum is what keeps the row's own tap working — a tap
            // never travels far enough for this gesture to claim it.
            //
            // A disabled row must not compete with the enclosing scroll view either, so the mask
            // drops to `.subviews` rather than the gesture checking `enabled` inside.
            .highPriorityGesture(drag, including: enabled && !actions.isEmpty ? .all : .subviews)
            .modifier(SwipeAccessibilityActions(actions: actions))

            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            }
        }
        .onChange(of: open) { newValue in
            withAnimation(settle()) { travel = newValue ? revealWidth : 0 }
        }
        .onChange(of: revealWidth) { newValue in
            // `actions` can change while the row is open, and an open row would otherwise rest at
            // a stale offset. Never under a live finger, where it would fight the drag.
            guard open, dragOrigin == nil else { return }
            withAnimation(settle()) { travel = newValue }
        }
        .onChange(of: isDragging) { dragging in
            // A cancelled gesture never delivers `onEnded`, so the snap back has to happen here.
            // Only the claim is released: `settleOrigin` is what `onEnded` guards on, and the
            // order these two are observed in is not documented.
            guard !dragging, dragOrigin != nil else { return }
            dragOrigin = nil
            committed = false
            withAnimation(settle()) { travel = open ? revealWidth : 0 }
        }
    }

    /// What the row owes the finger: everything it has moved since the drag was claimed.
    private func dragged(_ value: DragGesture.Value) -> CGFloat {
        value.translation.width - claimTranslation
    }

    private func clampedTravel(_ value: CGFloat) -> CGFloat {
        let ceiling = allowsFullSwipe ? rowWidth : revealWidth
        return min(max(value, 0), ceiling)
    }

    private var drag: some Gesture {
        DragGesture(minimumDistance: claimDistance)
            .updating($isDragging) { _, state, _ in state = true }
            .onChanged { value in
                if dragOrigin == nil {
                    // Let a vertical scroll win: start tracking only a predominantly
                    // horizontal drag, and never claim the gesture otherwise.
                    guard abs(value.translation.width) > abs(value.translation.height) else { return }
                    dragOrigin = travel
                    settleOrigin = travel
                    claimTranslation = value.translation.width
                }
                guard let origin = dragOrigin else { return }
                let next = clampedTravel(origin + dragged(value) * towardsTrailing)
                let crossed = allowsFullSwipe && next >= rowWidth * commitFraction
                if crossed != committed {
                    committed = crossed
                    if crossed { playCommitHaptic() }
                }
                travel = next
            }
            .onEnded { value in
                guard let origin = settleOrigin else { return }
                settleOrigin = nil
                dragOrigin = nil
                committed = false
                // Read the release position off the gesture rather than off `travel`: the cancel
                // path may already have snapped `travel` back before this ran.
                let released = clampedTravel(origin + dragged(value) * towardsTrailing)
                let speed = releaseVelocity(of: value) * towardsTrailing
                let target = resolveSwipeSettle(
                    travel: released,
                    velocity: speed,
                    firstActionReveal: swipeRevealWidth(through: 1),
                    rowWidth: rowWidth,
                    allowsFullSwipe: allowsFullSwipe
                )
                open = target == .open
                if target == .committed {
                    // Before the animation, not after: the row must not wait on a spring to fire.
                    actions.first?.onClick()
                }
                // The spring picks up the speed the finger let go at rather than starting from
                // rest, so the row carries straight on out of the drag.
                let settleTo: CGFloat = target == .open ? revealWidth : 0
                withAnimation(settle(velocity: speed, over: settleTo - travel)) {
                    travel = settleTo
                }
            }
    }

    /// Speed at release, in pt/s. `DragGesture.Value.velocity` is iOS 17, and this package targets
    /// iOS 15, so below that UIKit's projection stands in: it runs a decelerating drag roughly a
    /// quarter-second ahead, which makes the gap between the predicted end and the current
    /// translation a quarter of the velocity.
    private func releaseVelocity(of value: DragGesture.Value) -> CGFloat {
        if #available(iOS 17.0, macOS 14.0, tvOS 17.0, watchOS 10.0, *) {
            return value.velocity.width
        }
        return (value.predictedEndTranslation.width - value.translation.width) * 4
    }

    private func playCommitHaptic() {
        #if canImport(UIKit) && !os(watchOS)
        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
        #endif
    }
}

// MARK: - Strip

/// The actions behind the row, drawn as far as the row has revealed them.
///
/// `Animatable` on `travel`, so that a settle hands it the row's own interpolated position frame by
/// frame. Left to interpolate a scale and a width of its own, it would arrive at the right place by
/// a different route: an action still at full width while the row has come most of the way back,
/// which is exactly where the two would be seen to overlap.
private struct SwipeActionStrip: View, Animatable {
    var travel: CGFloat
    let actions: [LemonadeSwipeAction]
    let committed: Bool
    let rowWidth: CGFloat
    let towardsTrailing: CGFloat

    var animatableData: CGFloat {
        get { travel }
        set { travel = newValue }
    }

    private var actionSize: CGFloat { LemonadeTheme.sizes.size1200 }

    /// Distance from one action to the next.
    private var step: CGFloat { actionSize + LemonadeTheme.spaces.spacing200 }

    private var actionsWidth: CGFloat {
        guard !actions.isEmpty else { return 0 }
        let gaps = CGFloat(actions.count - 1) * LemonadeTheme.spaces.spacing200
        return CGFloat(actions.count) * actionSize + gaps
    }

    private var displacedOpacity: CGFloat {
        resolveSwipeDisplacedOpacity(travel: travel, rowWidth: rowWidth)
    }

    var body: some View {
        ZStack(alignment: .trailing) {
            // Outermost last, so it is drawn on top: the first action is the one a full swipe
            // fires, and the one that stretches over the actions beside it.
            ForEach(Array(actions.enumerated()).reversed(), id: \.offset) { index, action in
                let reveal = resolveSwipeStripReveal(
                    travel: travel,
                    actionReveal: swipeRevealWidth(through: index + 1),
                    stripReveal: swipeRevealWidth(through: actions.count),
                    actionWidth: actionSize
                )
                // Each action lands as the row clears it, so the second of a pair bumps in on its
                // own rather than with the first.
                let arrived = reveal.scale >= bumpTrigger
                capsule(
                    action,
                    stretch: index == 0 ? reveal.stretch : 0,
                    committed: committed && index == 0
                )
                // Scoped between the two scales, so the spring governs the bump and nothing else.
                // Outside them it takes the reveal's own scale with it, and since that is driven
                // by the row frame by frame the spring restarts from wherever the action had got
                // to — which stalls it mid-arrival and then walks it up again.
                .scaleEffect(arrived ? 1 : 1 - bumpDepth)
                .animation(bump, value: arrived)
                .scaleEffect(reveal.scale)
                .opacity(reveal.scale * (index == 0 ? 1 : displacedOpacity))
                // The slack goes to the first action's width and to everything else's position, so
                // a stretching action pushes the ones beside it along rather than growing over
                // them. Their gaps hold, and the strip still ends exactly one leading gap ahead of
                // the row however far it is dragged.
                .offset(x: (CGFloat(index) * step + (index == 0 ? 0 : reveal.stretch)) * towardsTrailing)
            }
        }
        .frame(width: actionsWidth, alignment: .trailing)
        .padding(.leading, LemonadeTheme.spaces.spacing300)
        .padding(.trailing, LemonadeTheme.spaces.spacing400)
        // Crossing the commit threshold moves the icon without moving the row, so it is the one
        // thing here with an animation of its own.
        .animation(settle(), value: committed)
        // The actions stay in the accessibility tree even while covered by the row, where they
        // would announce a destructive action ahead of the row it belongs to. The row's own custom
        // actions are the accessible path.
        .accessibilityHidden(true)
    }

    /// One action: a capsule that is a circle until a full swipe stretches it.
    ///
    /// Drawn here rather than with `LemonadeUi.IconButton`, whose frame is square and fixed, but
    /// off the same colours so the two stay in step.
    private func capsule(
        _ action: LemonadeSwipeAction,
        stretch: CGFloat,
        committed: Bool
    ) -> some View {
        let colors = resolveColors(variant: action.variant, type: .solid)
        // Centred in the capsule until the swipe commits, then it slides to the centre of the
        // capsule's leading end — where the action would sit if it had stayed a circle and the row
        // had simply carried on past it.
        let iconOffset = committed ? stretch / 2 * towardsTrailing : 0
        return SwiftUI.Button(action: action.onClick) {
            Capsule()
                .fill(colors.backgroundColor)
                .frame(width: actionSize + stretch, height: actionSize)
                .overlay {
                    LemonadeUi.Icon(
                        icon: action.icon,
                        contentDescription: action.contentDescription,
                        size: .large,
                        tint: colors.contentColor
                    )
                    .offset(x: iconOffset)
                }
                .contentShape(Capsule())
        }
        .buttonStyle(SwipeActionButtonStyle())
    }
}

/// The press treatment `LemonadeUi.IconButton` gives its own button, for the capsule that stands in
/// for it here.
private struct SwipeActionButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .opacity(configuration.isPressed ? .opacity.opacityPressed : .opacity.opacity100)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

// MARK: - Accessibility

/// VoiceOver reaches the actions through the row. A `ZStack` is not an accessibility element, so
/// actions applied to it attach to nothing: combining the children makes the row one element, and
/// every action then hangs off it as a custom action.
private struct SwipeAccessibilityActions: ViewModifier {
    let actions: [LemonadeSwipeAction]

    func body(content: Content) -> some View {
        actions.reduce(AnyView(content.accessibilityElement(children: .combine))) { view, action in
            AnyView(view.accessibilityAction(named: Text(action.contentDescription), action.onClick))
        }
    }
}

// MARK: - SwipeActionRow Component

public extension LemonadeUi {
    /// Wraps a row with actions revealed by a horizontal drag.
    ///
    /// The wrapped item must not draw its own divider — pass `showDivider: false` to it and set
    /// `showDivider` here instead. A list item draws its divider inside its own body, so it would
    /// travel with the row and leave a gap at the trailing edge.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SwipeActionRow(
    ///     actions: [
    ///         LemonadeSwipeAction(icon: .trash, contentDescription: "Remove account", onClick: { })
    ///     ],
    ///     showDivider: true
    /// ) {
    ///     LemonadeUi.ActionListItem(label: "Label", onItemClicked: { })
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - actions: the actions revealed on the trailing edge, outermost first
    ///   - enabled: flag to define whether the drag is active
    ///   - allowsFullSwipe: whether dragging across the row fires the first action on release
    ///   - showDivider: flag to show a divider below the row, which does not travel with it
    ///   - content: the row this wraps
    @ViewBuilder
    static func SwipeActionRow<Content: View>(
        actions: [LemonadeSwipeAction],
        enabled: Bool = true,
        allowsFullSwipe: Bool = true,
        showDivider: Bool = false,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeUncontrolledSwipeActionRow(
            actions: actions,
            enabled: enabled,
            allowsFullSwipe: allowsFullSwipe,
            showDivider: showDivider,
            content: content
        )
    }

    /// `SwipeActionRow` whose open row is controlled by the caller, so a list can keep at most one
    /// row open: hoist a single optional id and hand every row the same binding.
    ///
    /// - Parameters:
    ///   - id: identity of this row, compared against `openId`
    ///   - openId: identity of the row currently open, or nil when none is
    @ViewBuilder
    static func SwipeActionRow<Content: View>(
        id: AnyHashable,
        openId: Binding<AnyHashable?>,
        actions: [LemonadeSwipeAction],
        enabled: Bool = true,
        allowsFullSwipe: Bool = true,
        showDivider: Bool = false,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeSwipeActionRowView(
            actions: actions,
            enabled: enabled,
            allowsFullSwipe: allowsFullSwipe,
            showDivider: showDivider,
            open: Binding(
                get: { openId.wrappedValue == id },
                set: { openId.wrappedValue = $0 ? id : nil }
            ),
            content: content
        )
    }
}

/// Holds its own open state, so a single row needs no ceremony at the call site.
private struct LemonadeUncontrolledSwipeActionRow<Content: View>: View {
    let actions: [LemonadeSwipeAction]
    let enabled: Bool
    let allowsFullSwipe: Bool
    let showDivider: Bool
    @ViewBuilder let content: () -> Content

    @State private var open = false

    var body: some View {
        LemonadeSwipeActionRowView(
            actions: actions,
            enabled: enabled,
            allowsFullSwipe: allowsFullSwipe,
            showDivider: showDivider,
            open: $open,
            content: content
        )
    }
}

#if DEBUG
struct LemonadeSwipeActionRow_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: .space.spacing600) {
            // One trailing action, full swipe on.
            LemonadeUi.SwipeActionRow(
                actions: [
                    LemonadeSwipeAction(icon: .trash, contentDescription: "Remove", onClick: {})
                ],
                showDivider: true
            ) {
                LemonadeUi.ActionListItem(
                    label: "Kathryn Murphy",
                    supportText: "kathryn.murphy@mail.com",
                    showNavigationIndicator: true,
                    showDivider: false,
                    onItemClicked: {}
                )
            }

            // Two actions, no full swipe.
            LemonadeUi.SwipeActionRow(
                actions: [
                    LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: {}),
                    LemonadeSwipeAction(
                        icon: .pencilLine,
                        contentDescription: "Edit",
                        onClick: {},
                        variant: .neutral
                    )
                ],
                allowsFullSwipe: false
            ) {
                LemonadeUi.ActionListItem(
                    label: "Two actions",
                    supportText: "Outermost action first",
                    showDivider: false,
                    onItemClicked: {}
                )
            }

            // Controlled: one open row at a time.
            StatefulPreviewWrapper(AnyHashable?.none) { openId in
                LemonadeUi.SwipeActionRow(
                    id: "row",
                    openId: openId,
                    actions: [
                        LemonadeSwipeAction(icon: .trash, contentDescription: "Remove", onClick: {})
                    ]
                ) {
                    LemonadeUi.ActionListItem(
                        label: "Controlled row",
                        showDivider: false,
                        onItemClicked: {}
                    )
                }
            }
        }
        .frame(maxHeight: .infinity, alignment: .top)
    }
}
#endif
