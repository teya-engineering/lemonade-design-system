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

/// Travel a drag has to cross for a full swipe to commit, for a row `rowWidth` wide.
///
/// One place, because everything hangs off it: the haptic, the icon's slide, the strip's dimming,
/// where the row parks, and whether a release fires the action. Restating it is how the four drift.
func swipeCommitThreshold(rowWidth: CGFloat) -> CGFloat {
    rowWidth * commitFraction
}

/// Which edge of the row a reveal belongs to, and which way its travel points.
///
/// The sign lives here rather than in each place that needs it: everything sided is resolved on a
/// magnitude and signed back by exactly this, which is what lets one set of rules serve both edges.
enum SwipeActionSide {
    case leading
    case trailing

    var sign: CGFloat { self == .leading ? -1 : 1 }
}

/// The side a signed travel has the row open on, or nil at rest.
///
/// Negative travel is onto the leading actions, positive onto the trailing ones, so what the row is
/// showing is never a separate fact that could disagree with where it is.
func swipeTravelSide(travel: CGFloat) -> SwipeActionSide? {
    if travel > 0 { return .trailing }
    if travel < 0 { return .leading }
    return nil
}

/// The side a gesture owns: the one the row is already open on, or, for a row at rest, the one the
/// finger has set off towards.
///
/// A gesture keeps that side until it ends. A finger dragging an open row back is closing it, and
/// letting it carry on through zero would turn one drag into a commit on the opposite edge — the
/// reader would have had no way to ask for that, having never lifted their finger.
///
/// - Parameter travel: where the row is now, signed.
/// - Parameter delta: how far the finger has moved, in travel's own sign.
func resolveSwipeGestureSide(travel: CGFloat, delta: CGFloat) -> SwipeActionSide? {
    swipeTravelSide(travel: travel) ?? swipeTravelSide(travel: delta)
}

/// How far the row may travel onto one side, as a magnitude.
///
/// Nothing when the side has no actions: a row with actions on one edge only is still draggable,
/// and the empty edge has to hold it where it is rather than let it be carried across to reveal
/// nothing. `revealWidth` is zero exactly when the side is empty, which is what makes that the same
/// question.
///
/// - Parameters:
///   - revealWidth: travel that rests the row on every action of this side.
///   - rowWidth: full width of the row.
///   - allowsFullSwipe: whether a drag across the row may commit this side's first action.
func resolveSwipeCeiling(
    revealWidth: CGFloat,
    rowWidth: CGFloat,
    allowsFullSwipe: Bool
) -> CGFloat {
    guard revealWidth > 0 else { return 0 }
    return allowsFullSwipe ? rowWidth : revealWidth
}

/// Where a delta leaves the row, in signed travel.
///
/// Held to the side the gesture owns, so it stops at rest rather than crossing into the other
/// edge's actions.
///
/// - Parameters:
///   - travel: where the row is now, signed.
///   - delta: how far the finger has moved, in travel's own sign.
///   - side: the side this gesture owns.
///   - ceiling: how far the row may travel onto that side.
func resolveSwipeTravel(
    travel: CGFloat,
    delta: CGFloat,
    side: SwipeActionSide,
    ceiling: CGFloat
) -> CGFloat {
    side.sign * min(max((travel + delta) * side.sign, 0), ceiling)
}

/// Whether the row has been carried far enough for a full swipe to commit.
///
/// One place because both the live drag and the release ask it, and restating it is how the two
/// drift: the drag's own copy once lacked the `rowWidth` guard, and an unmeasured row — whose
/// threshold is zero — read every touch as a commit.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, in either direction.
///   - rowWidth: full width of the row.
///   - allowsFullSwipe: whether a drag across the row may commit an action at all.
func swipeCrossedCommit(
    travel: CGFloat,
    rowWidth: CGFloat,
    allowsFullSwipe: Bool
) -> Bool {
    allowsFullSwipe && rowWidth > 0 && abs(travel) >= swipeCommitThreshold(rowWidth: rowWidth)
}

/// Fraction of the row's width a drag must cross for a full swipe to commit.
///
/// Measured off iOS frame by frame: a 440pt row commits as the drag passes 240pt, which is 0.546 of
/// it. Far enough past halfway that the reader has to mean it.
private let commitFraction: CGFloat = 0.55

/// What is left of the row on screen once a commit has claimed it. iOS stops the row 18.3pt short
/// of carrying it off, which keeps the row a row rather than a bare action.
private let commitInset: CGFloat = 20

/// The spring a commit claims the row on — ω = 35 rad/s, a fifth of a second end to end, against
/// the settle's half.
///
/// Fitted to iOS frame by frame: the action reaches half its width within 50ms of the crossing and
/// 94% within 130ms, which a critically damped spring at that ω tracks to within a frame across the
/// whole animation.
private let commit: Animation = .interpolatingSpring(mass: 1, stiffness: 1225, damping: 70)

/// How far the row may drift on screen before an open one counts as scrolled past. Enough to sit
/// out the rounding a layout pass can move it by, and far short of a deliberate scroll.
private let scrollSlack: CGFloat = 4

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

/// Where the row is drawn while a drag is bringing it back from a commit.
///
/// The claim is not handed back on a spring of its own, which would step the row across whatever
/// the finger is doing. The row keeps the lead the commit gave it and gives it up in proportion to
/// the finger, so it arrives home exactly as the finger does. Continuous at the crossing by
/// construction: a drag can only leave a commit at the threshold, and the threshold times the gain
/// is where the commit had it.
///
/// - Parameter travel: where the finger has the row, always positive.
/// - Parameter commitTravel: where a commit parks the row.
/// - Parameter threshold: travel a drag commits at.
func resolveSwipeReleasedTravel(
    travel: CGFloat,
    commitTravel: CGFloat,
    threshold: CGFloat
) -> CGFloat {
    guard threshold > 0 else { return travel }
    return min(travel * (commitTravel / threshold), commitTravel)
}

/// The row's drawn travel, written by the spring as it interpolates.
///
/// A reference rather than state: it is updated from inside a view update, and nothing renders off
/// it — only the drag reads it, to anchor itself where the row actually is.
private final class SwipeDrawnTravel {
    var value: CGFloat = 0
}

/// Follows the row's travel through an animation and records it in [SwipeDrawnTravel].
///
/// `travel` holds the spring's *target* while it is in flight, so a finger landing on a settling
/// row would anchor the drag at where the row is going rather than where it is, and the row would
/// jump the remaining distance on the first delta.
private struct SwipeDrawnTravelReader: ViewModifier, Animatable {
    var travel: CGFloat
    let drawn: SwipeDrawnTravel

    var animatableData: CGFloat {
        get { travel }
        set {
            travel = newValue
            drawn.value = newValue
        }
    }

    func body(content: Content) -> some View {
        content
    }
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
    // Nothing to open onto: there are no actions. Asked before the commit, because a row with
    // nothing behind it has nothing to fire.
    if firstActionReveal <= 0 {
        return .closed
    }
    if swipeCrossedCommit(travel: travel, rowWidth: rowWidth, allowsFullSwipe: allowsFullSwipe) {
        return .committed
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
///
/// Computed rather than measured: the strip changes width as the first action stretches, so
/// anything measured off it would move under the model driving it.
///
/// - Parameter count: how many of the row's actions the row rests on.
/// - Parameter actionWidth: width of one action.
/// - Parameter gap: space between two actions.
/// - Parameter padding: space ahead of the first action and behind the last.
func resolveSwipeRevealWidth(
    count: Int,
    actionWidth: CGFloat,
    gap: CGFloat,
    padding: CGFloat
) -> CGFloat {
    guard count > 0 else { return 0 }
    return padding + CGFloat(count) * actionWidth + CGFloat(count - 1) * gap
}

/// `resolveSwipeRevealWidth` against the theme the row is drawn in.
private func swipeRevealWidth(through count: Int) -> CGFloat {
    resolveSwipeRevealWidth(
        count: count,
        actionWidth: LemonadeTheme.sizes.size1200,
        gap: LemonadeTheme.spaces.spacing200,
        padding: LemonadeTheme.spaces.spacing300 + LemonadeTheme.spaces.spacing400
    )
}

/// Opacity an action being pushed along has dimmed to once the row has travelled its whole width.
/// `opacity20`, held as a plain number to match the Compose twin, where the reveal is resolved
/// outside a composition and cannot read the theme.
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
    let takeover = swipeCommitThreshold(rowWidth: rowWidth)
    guard rowWidth > takeover else { return 1 }
    let progress = min(max((travel - takeover) / (rowWidth - takeover), 0), 1)
    return 1 - (1 - displacedFloor) * progress
}

// MARK: - Group

/// What a group tells its rows: one of them has taken the open slot, or nothing has.
///
/// The token is what makes it a signal rather than a value. Two rows opening in turn both leave
/// `opener` set, and a row that closed and reopened would look unchanged — the count moves either
/// way, so every row hears every announcement.
///
/// An announcement means two things at once, which is what lets one counter serve both: *I am the
/// open row now*, and *do not close on the tap in flight*. The group's own tap-to-close waits a
/// turn and then stands down if the count moved, so anything inside a group that acts on a tap has
/// to announce or the row underneath it closes.
struct SwipeActionGroupSignal: Equatable {
    var announcements = 0
    var opener: AnyHashable?
}

private struct SwipeActionGroupSignalKey: EnvironmentKey {
    static let defaultValue = SwipeActionGroupSignal()
}

private struct SwipeActionGroupAnnounceKey: EnvironmentKey {
    static let defaultValue: ((AnyHashable?) -> Void)? = nil
}

extension EnvironmentValues {
    var swipeActionGroupSignal: SwipeActionGroupSignal {
        get { self[SwipeActionGroupSignalKey.self] }
        set { self[SwipeActionGroupSignalKey.self] = newValue }
    }

    /// Nil outside a group, which is what leaves a lone row owning its own state.
    var swipeActionGroupAnnounce: ((AnyHashable?) -> Void)? {
        get { self[SwipeActionGroupAnnounceKey.self] }
        set { self[SwipeActionGroupAnnounceKey.self] = newValue }
    }
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
    let keepsRowOpen: Bool

    /// - Parameters:
    ///   - icon: what the action shows
    ///   - contentDescription: what VoiceOver reads, and how the action is reached without the
    ///     gesture
    ///   - onClick: what the action does
    ///   - variant: the button palette it is drawn in
    ///   - keepsRowOpen: whether the row stays open once this has fired. Firing an action normally
    ///     closes the row, which is wrong for one that puts something on screen about it — a
    ///     confirmation asking whether to go ahead reads oddly over a row that has already tidied
    ///     itself away. Nothing inside the row closes it again afterwards: the reader does, by
    ///     tapping away or swiping another row, or the caller does through the `openId` overload.
    ///     An action that opens something modal needs that overload, because the modal takes the
    ///     taps the reader would have closed it with.
    public init(
        icon: LemonadeIcon,
        contentDescription: String,
        onClick: @escaping () -> Void,
        variant: LemonadeButtonVariant = .critical,
        keepsRowOpen: Bool = false
    ) {
        self.icon = icon
        self.contentDescription = contentDescription
        self.onClick = onClick
        self.variant = variant
        self.keepsRowOpen = keepsRowOpen
    }
}

// MARK: - Row

struct LemonadeSwipeActionRowView<Content: View>: View {
    let leadingActions: [LemonadeSwipeAction]
    let trailingActions: [LemonadeSwipeAction]
    let enabled: Bool
    let allowsFullSwipe: Bool
    let showDivider: Bool
    @Binding var open: Bool
    /// Built once rather than held as a closure. The drag rewrites this view's body on every touch
    /// event, and a closure would re-run the caller's whole builder each time — the cost the
    /// Compose row sheds by keeping `travel` out of composition.
    let content: Content
    /// Both edges in the order a reader would find them, joined once for the same reason: the drag
    /// rewrites the body on every touch event, and concatenating there would rebuild the
    /// accessibility subtree with it.
    let allActions: [LemonadeSwipeAction]

    init(
        leadingActions: [LemonadeSwipeAction],
        trailingActions: [LemonadeSwipeAction],
        enabled: Bool,
        allowsFullSwipe: Bool,
        showDivider: Bool,
        open: Binding<Bool>,
        @ViewBuilder content: () -> Content
    ) {
        self.leadingActions = leadingActions
        self.trailingActions = trailingActions
        self.allActions = leadingActions + trailingActions
        self.enabled = enabled
        self.allowsFullSwipe = allowsFullSwipe
        self.showDivider = showDivider
        self._open = open
        self.content = content()
    }

    /// Signed: negative onto the leading actions, positive onto the trailing ones.
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
    /// The side rather than a flag: the strip that stretches and the icon that slides are one
    /// edge's, not both.
    @State private var committedSide: SwipeActionSide?
    /// Which side the row is open on. Nil until a drag or a caller says.
    @State private var openSide: SwipeActionSide?
    /// The side the live gesture owns, decided on its first delta and kept until it ends.
    @State private var gestureSide: SwipeActionSide?
    /// Whether a committed swipe is holding the row where it left it — all the way across, with
    /// the action still stretched behind it — rather than at the reveal. Cleared when the row
    /// closes, or when a finger takes hold of it again.
    @State private var held = false
    /// Whether an action is holding the row open behind something it opened. The actions are then
    /// nothing the reader can act on — whatever they opened is — so they are drawn as inert.
    @State private var holding = false
    /// Whether this drag has left a commit behind and is carrying the row's lead back with it.
    @State private var releasing = false
    /// Where the row is drawn, as opposed to where `travel` says it is heading.
    @State private var drawnTravel = SwipeDrawnTravel()
    #if canImport(UIKit) && !os(watchOS)
    /// Held rather than built at the crossing, and warmed when the drag is claimed: a generator
    /// made on the frame it fires spins the engine up then, which is the one frame the haptic has
    /// to land on.
    @State private var haptics = UIImpactFeedbackGenerator(style: .medium)
    #endif
    @GestureState private var isDragging = false
    /// What this row answers to inside a group. Its own, so an uncontrolled row needs no identity
    /// from the caller to take part.
    @State private var groupIdentity = UUID()
    /// Where the row sits on screen, and where it sat when it opened. A row that has moved since
    /// is being scrolled past, and an open row scrolling away is one the reader has left behind.
    @State private var rowY: CGFloat = 0
    @State private var openedAt: CGFloat?
    @Environment(\.layoutDirection) private var layoutDirection
    @Environment(\.swipeActionGroupSignal) private var groupSignal
    @Environment(\.swipeActionGroupAnnounce) private var announce

    /// A reveal on the trailing edge travels left in LTR and right in RTL.
    private var towardsTrailing: CGFloat { layoutDirection == .rightToLeft ? 1 : -1 }

    // MARK: - Sides

    private func actionsOn(_ side: SwipeActionSide) -> [LemonadeSwipeAction] {
        side == .leading ? leadingActions : trailingActions
    }

    /// Where an open row rests on one side: every action of it, plus the padding they sit in.
    /// Computed rather than measured: the strip changes width as the first action stretches, so
    /// anything measured off it would move under the model driving it.
    private func revealOn(_ side: SwipeActionSide) -> CGFloat {
        swipeRevealWidth(through: actionsOn(side).count)
    }

    /// Where a commit parks the row: as far as it goes, less the sliver iOS leaves of it.
    private func commitTravelOn(_ side: SwipeActionSide) -> CGFloat {
        commitTravel(forReveal: revealOn(side))
    }

    private func commitTravel(forReveal reveal: CGFloat) -> CGFloat {
        max(reveal, rowWidth - commitInset)
    }

    private func ceilingOn(_ side: SwipeActionSide) -> CGFloat {
        resolveSwipeCeiling(
            revealWidth: revealOn(side),
            rowWidth: rowWidth,
            allowsFullSwipe: allowsFullSwipe
        )
    }

    /// The side the row would open onto with nothing having said otherwise: whichever edge has
    /// actions, trailing first, so a row opened by its caller opens the way it always did.
    private var restingSide: SwipeActionSide {
        openSide ?? (trailingActions.isEmpty ? .leading : .trailing)
    }

    /// Where the row rests while open, signed: at the reveal, or wherever a commit is holding it.
    private var restingTravel: CGFloat {
        let side = restingSide
        return side.sign * (held ? commitTravelOn(side) : revealOn(side))
    }

    /// Where an open row rests, signed — what an action list changing under an open row moves it
    /// to.
    private var openReveal: CGFloat {
        restingSide.sign * revealOn(restingSide)
    }

    /// Where the finger has the row: its own travel, or the lead a commit gave it, being given
    /// back in proportion to the finger. Resolved on the magnitude and signed back.
    private var base: CGFloat {
        guard releasing else { return travel }
        let side = swipeTravelSide(travel: travel) ?? restingSide
        return side.sign * resolveSwipeReleasedTravel(
            travel: abs(travel),
            commitTravel: commitTravelOn(side),
            threshold: swipeCommitThreshold(rowWidth: rowWidth)
        )
    }

    /// What the row draws. Crossing the commit threshold takes the row out of the drag's hands and
    /// carries it the rest of the way itself; dragging back below hands it back.
    private var shown: CGFloat {
        guard let side = committedSide else { return base }
        return side.sign * commitTravelOn(side)
    }

    /// What one side's strip has been revealed by: nothing at all unless the row is showing it.
    private func shownOn(_ side: SwipeActionSide) -> CGFloat {
        let reached = shown
        return swipeTravelSide(travel: reached) == side ? abs(reached) : 0
    }

    /// Where the row has been placed. The first placement since it opened is where it opened;
    /// after that, moving more than `scrollSlack` means the reader has scrolled it away.
    ///
    /// Armed from here rather than from `open` changing, because that fires before the row has
    /// been measured — a row composed already open would take zero for where it opened and close
    /// itself the moment the real position arrived.
    private func positioned(at y: CGFloat) {
        rowY = y
        guard open else { return }
        guard let opened = openedAt else {
            // Anchored a turn later, not on the first placement: a screen still finding its own
            // size moves every row on it, and that is not the reader scrolling anything. By the
            // time this runs the layout has settled and `rowY` holds where the row ended up.
            Task { @MainActor in
                if open, openedAt == nil { openedAt = rowY }
            }
            return
        }
        if abs(y - opened) > scrollSlack { open = false }
    }

    /// A tapped action tidies the row away after it, unless it has put something on screen that the
    /// row is the subject of. Claiming the slot again is what keeps the group's own tap — the same
    /// one that fired this — from closing the row underneath it.
    private func fired(_ action: LemonadeSwipeAction) {
        action.onClick()
        if action.keepsRowOpen {
            withAnimation(settle()) { holding = true }
            announce?(groupIdentity)
        } else {
            open = false
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            ZStack(alignment: .trailing) {
                // An edge with no actions is left out entirely: an empty strip is still rebuilt
                // on every drag event to draw nothing, and still takes its padding out of the
                // row's width. Emptiness is not a function of travel, so this cannot fire
                // mid-gesture.
                HStack(spacing: 0) {
                    if !leadingActions.isEmpty { strip(on: .leading) }
                    Spacer(minLength: 0)
                    if !trailingActions.isEmpty { strip(on: .trailing) }
                }
                // Drained of colour and dimmed while something the action opened has the reader's
                // attention: the actions are still there, and still where they were, but they are
                // not what is being answered.
                .grayscale(holding ? 1 : 0)
                .opacity(holding ? .opacity.opacity30 : 1)
                content
                    // A row under the finger rests on the list item's own press highlight rather
                    // than on a surface of its own: same fill, same radius, same gutter. It is on
                    // for the whole gesture, not proportional to the travel — the row is being
                    // handled from the first pixel.
                    .background(
                        RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                            .fill(LemonadeTheme.colors.interaction.bgSubtleInteractive)
                            .opacity(travel == 0 ? 0 : 1)
                            .padding(LemonadeTheme.spaces.spacing100)
                    )
                    // Inside the offset, so it travels with the row. Applied outside it the
                    // overlay would sit on the row's unshifted layout frame — `offset` moves what
                    // is drawn and hit, not the space the row was given — and cover the actions it
                    // has just revealed, taking every tap meant for them.
                    //
                    // Only intercepts taps while open, so a closed row's own button still works.
                    .overlay {
                        if open {
                            Color.clear
                                .contentShape(Rectangle())
                                .onTapGesture { open = false }
                        }
                    }
                    .offset(x: shown * towardsTrailing)
            }
            // Scoped rather than ambient: the drag writes `travel` in the same turn as the
            // crossing, and a plain `withAnimation` around the crossing loses the spring to it.
            .animation(commit, value: committedSide)
            .modifier(SwipeDrawnTravelReader(travel: shown, drawn: drawnTravel))
            .clipped()
            .background(
                GeometryReader { proxy in
                    Color.clear
                        .onAppear { rowWidth = proxy.size.width }
                        .onChange(of: proxy.size.width) { rowWidth = $0 }
                        .onAppear { positioned(at: proxy.frame(in: .global).minY) }
                        .onChange(of: proxy.frame(in: .global).minY) { y in
                            positioned(at: y)
                        }
                }
            )
            // A row handed to us already open is drawn open: `onChange` does not fire for an
            // initial value, so nothing else would move it off zero, and the tap-to-close overlay
            // would sit invisibly over a row that looks shut.
            .onAppear { if open { travel = restingTravel } }
            // High priority, because the wrapped row is usually a `Button` and a plain `.gesture`
            // ranks below the gestures of the view it is attached to: whichever of the two claimed
            // the touch first won, so the same drag opened the row or did nothing depending on
            // where it started. `claimDistance` is what keeps the row's own tap working — a tap
            // never travels far enough for this gesture to claim it.
            //
            // A disabled row must not compete with the enclosing scroll view either, so the mask
            // drops to `.subviews` rather than the gesture checking `enabled` inside.
            .highPriorityGesture(
                drag,
                including: enabled && !(leadingActions.isEmpty && trailingActions.isEmpty)
                    ? .all
                    : .subviews
            )
            // Gated on `enabled`, because a row that will not open must not offer its actions to a
            // reader who cannot see they are unreachable. Through `fired`, not straight to
            // `onClick`: an action reached this way has to close the row, or hold it open and
            // announce, exactly as a tapped one does.
            .modifier(
                // Both edges in the order a reader would find them: the gesture is what is
                // invisible here, not the side.
                SwipeAccessibilityActions(
                    actions: enabled && !holding ? allActions : [],
                    onFired: fired
                )
            )

            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            }
        }
        .onChange(of: open) { newValue in
            if !newValue {
                held = false
                committedSide = nil
                // The side is forgotten with the row. Nothing is drawn off it while the row
                // travels home — the sign `travel` still carries is — and the next opening picks
                // its own.
                openSide = nil
            }
            withAnimation(settle()) {
                travel = newValue ? restingTravel : 0
                if !newValue { holding = false }
            }
            // Armed by `positioned(at:)`, which knows a measured position; this only clears the
            // one the last opening left behind.
            openedAt = nil
            if newValue { announce?(groupIdentity) }
        }
        .onChange(of: groupSignal) { signal in
            // Another row took the slot, or the group was tapped and nothing holds it.
            guard open, signal.opener != AnyHashable(groupIdentity) else { return }
            open = false
        }
        // Either list can change while the row is open, and an open row would otherwise rest at a
        // stale offset. Observed on the counts the reveals are a function of rather than on the
        // reveal itself, which also moves when the side does — and a drag that settles open is
        // what sets the side, so that would restart every release's spring from rest. Never under
        // a live finger either, where it would fight the drag.
        .onChange(of: [leadingActions.count, trailingActions.count]) { _ in
            guard open, dragOrigin == nil, !held else { return }
            withAnimation(settle()) { travel = openReveal }
        }
        .onChange(of: isDragging) { dragging in
            // A cancelled gesture never delivers `onEnded`, so the snap back has to happen here.
            // Only the claim is released: `settleOrigin` is what `onEnded` guards on, and the
            // order these two are observed in is not documented.
            guard !dragging, dragOrigin != nil else { return }
            dragOrigin = nil
            gestureSide = nil
            committedSide = nil
            withAnimation(settle()) {
                travel = open ? restingTravel : 0
                holding = false
            }
        }
    }

    /// One edge's actions, handed the travel that belongs to that edge and nothing else.
    private func strip(on side: SwipeActionSide) -> some View {
        let reveal = revealOn(side)
        return SwipeActionStrip(
            travel: shownOn(side),
            actions: actionsOn(side),
            side: side,
            committed: committedSide == side,
            // How far the first action has stretched once a commit has parked the row: what the
            // icon is sliding towards from the moment the crossing happens.
            committedStretch: commitTravel(forReveal: reveal) - reveal,
            holding: holding,
            rowWidth: rowWidth,
            towardsTrailing: towardsTrailing,
            onFired: fired
        )
    }

    /// What the row owes the finger: everything it has moved since the drag was claimed.
    private func dragged(_ value: DragGesture.Value) -> CGFloat {
        value.translation.width - claimTranslation
    }

    /// Where this drag has the row, held to the side it owns.
    private func draggedTravel(
        from origin: CGFloat,
        by translation: CGFloat,
        side: SwipeActionSide
    ) -> CGFloat {
        resolveSwipeTravel(
            travel: origin,
            delta: translation,
            side: side,
            ceiling: ceilingOn(side)
        )
    }

    private var drag: some Gesture {
        DragGesture(minimumDistance: claimDistance)
            .updating($isDragging) { _, state, _ in state = true }
            .onChanged { value in
                if dragOrigin == nil {
                    // Let a vertical scroll win: start tracking only a predominantly
                    // horizontal drag, and never claim the gesture otherwise.
                    guard abs(value.translation.width) > abs(value.translation.height) else { return }
                    // Off the drawn position, not the model one: grabbing a row mid-settle must
                    // carry on from where it is rather than from where the spring was taking it.
                    dragOrigin = drawnTravel.value
                    settleOrigin = drawnTravel.value
                    claimTranslation = value.translation.width
                    gestureSide = nil
                    #if canImport(UIKit) && !os(watchOS)
                    haptics.prepare()
                    #endif
                    // Claimed, so this is the row being read now. Announced here rather than when
                    // the row settles open: a reader who has started on another row has already
                    // left the open one, and waiting for the release leaves it sitting there
                    // through the whole gesture.
                    announce?(groupIdentity)
                    // Back under a finger, so nothing is holding it any more: this drag settles
                    // the row wherever it asks, like any other.
                    held = false
                    releasing = false
                    withAnimation(settle()) { holding = false }
                }
                guard let origin = dragOrigin else { return }
                let towards = dragged(value) * towardsTrailing
                let side = gestureSide ?? resolveSwipeGestureSide(travel: origin, delta: towards)
                gestureSide = side
                let next = side.map { draggedTravel(from: origin, by: towards, side: $0) } ?? 0
                let crossed: SwipeActionSide? = side.flatMap { side in
                    swipeCrossedCommit(
                        travel: next,
                        rowWidth: rowWidth,
                        allowsFullSwipe: allowsFullSwipe
                    ) ? side : nil
                }
                if crossed != committedSide {
                    // Sprung on the way out, and nothing to animate on the way back: leaving a
                    // commit hands the row to `base`, which picks it up exactly where the claim
                    // had it. Felt either way — crossing back is the moment the gesture stops
                    // belonging to the action, which is as worth knowing as the moment it started
                    // to.
                    committedSide = crossed
                    releasing = crossed == nil
                    playCommitHaptic()
                }
                travel = next
            }
            .onEnded { value in
                guard let origin = settleOrigin else { return }
                settleOrigin = nil
                dragOrigin = nil
                let side = gestureSide ?? restingSide
                gestureSide = nil
                // Everything below then reads as it always did: travel and velocity both positive
                // while the row is still opening.
                let sign = side.sign
                // The claim is spent: the row settles from where it is being drawn.
                travel = shown
                releasing = false
                // Read the release position off the gesture rather than off `travel`: the cancel
                // path may already have snapped `travel` back before this ran.
                let released = draggedTravel(
                    from: origin,
                    by: dragged(value) * towardsTrailing,
                    side: side
                )
                let speed = releaseVelocity(of: value) * towardsTrailing * sign
                let target = resolveSwipeSettle(
                    travel: abs(released),
                    velocity: speed,
                    // Nothing to open onto is what closes a release on an edge with nothing
                    // behind it: an empty side's reveal is zero.
                    firstActionReveal: min(revealOn(side), swipeRevealWidth(through: 1)),
                    rowWidth: rowWidth,
                    allowsFullSwipe: allowsFullSwipe
                )
                let commits = target == .committed
                // A commit fires the first action, so it rests where that action asks: away, or
                // held all the way across, the action still stretched, behind whatever the action
                // has just put on screen.
                let holds = commits && actionsOn(side).first?.keepsRowOpen == true
                // Held locally rather than read back off the binding: `open` may round-trip
                // through the caller's own state, and the settle below must not depend on when
                // that lands.
                let opens = target == .open || holds
                open = opens
                held = holds
                committedSide = holds ? side : nil
                openSide = opens ? side : nil
                if commits {
                    // Before the animation, not after: the row must not wait on a spring to fire.
                    actionsOn(side).first?.onClick()
                }
                // The spring picks up the speed the finger let go at rather than starting from
                // rest, so the row carries straight on out of the drag.
                let settleTo: CGFloat = opens
                    ? sign * (holds ? commitTravelOn(side) : revealOn(side))
                    : 0
                withAnimation(settle(velocity: speed * sign, over: settleTo - travel)) {
                    travel = settleTo
                    holding = holds
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
        haptics.impactOccurred()
        #endif
    }
}

// MARK: - Strip

/// The actions behind the row on one of its edges, drawn as far as the row has revealed them.
///
/// `travel` is the magnitude the row has moved onto *this* side, so the side the row is not showing
/// is handed zero and draws nothing.
///
/// Mirrored off `side`: an action sits against the edge it is revealed from, grows inwards from
/// it, and stacks away from it.
///
/// `Animatable` on `travel`, so that a settle hands it the row's own interpolated position frame by
/// frame. Left to interpolate a scale and a width of its own, it would arrive at the right place by
/// a different route: an action still at full width while the row has come most of the way back,
/// which is exactly where the two would be seen to overlap.
private struct SwipeActionStrip: View, Animatable {
    var travel: CGFloat
    let actions: [LemonadeSwipeAction]
    let side: SwipeActionSide
    let committed: Bool
    /// Whether an action is holding the row open behind something it opened, which is what the
    /// reader is answering — so the capsule is drawn inert and stops taking taps. An inline
    /// confirmation leaves it reachable, and a second tap on a destructive action is the one thing
    /// this must not allow.
    /// How far the first action has stretched once a commit has parked the row: what the icon is
    /// sliding towards from the moment the crossing happens.
    let committedStretch: CGFloat
    let holding: Bool

    /// Which capsule a pointer is over, if any.
    @State private var hovered: Int?
    let rowWidth: CGFloat
    let towardsTrailing: CGFloat
    let onFired: (LemonadeSwipeAction) -> Void

    var animatableData: CGFloat {
        get { travel }
        set { travel = newValue }
    }

    private var leading: Bool { side == .leading }

    /// Into the row, against the side's own outward travel. `offset(x:)` is not direction-aware,
    /// so the row's RTL sign folds in here too.
    private var towardsInside: CGFloat { side.sign * towardsTrailing }

    private var actionSize: CGFloat { LemonadeTheme.sizes.size1200 }

    private var outerPadding: CGFloat { LemonadeTheme.spaces.spacing400 }

    private var innerPadding: CGFloat { LemonadeTheme.spaces.spacing300 }

    /// Distance from one action to the next.
    private var step: CGFloat { actionSize + LemonadeTheme.spaces.spacing200 }

    /// Where the row rests on every one of these actions.
    private var stripReveal: CGFloat { swipeRevealWidth(through: actions.count) }

    private var actionsWidth: CGFloat {
        guard !actions.isEmpty else { return 0 }
        return stripReveal - innerPadding - outerPadding
    }

    private var displacedOpacity: CGFloat {
        resolveSwipeDisplacedOpacity(travel: travel, rowWidth: rowWidth)
    }

    var body: some View {
        let strip = stripReveal
        let dimmed = displacedOpacity
        return ZStack(alignment: leading ? .leading : .trailing) {
            // Outermost last, so it is drawn on top: the first action is the one a full swipe
            // fires, and the one that stretches over the actions beside it.
            ForEach(actions.indices.reversed(), id: \.self) { index in
                let action = actions[index]
                let reveal = resolveSwipeStripReveal(
                    travel: travel,
                    actionReveal: swipeRevealWidth(through: index + 1),
                    stripReveal: strip,
                    actionWidth: actionSize
                )
                // Each action lands as the row clears it, so the second of a pair bumps in on its
                // own rather than with the first.
                let arrived = reveal.scale >= bumpTrigger
                capsule(
                    action,
                    index: index,
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
                .opacity(reveal.scale * (index == 0 ? 1 : dimmed))
                // The slack goes to the first action's width and to everything else's position, so
                // a stretching action pushes the ones beside it along rather than growing over
                // them. Their gaps hold, and the strip still ends exactly one leading gap ahead of
                // the row however far it is dragged.
                .offset(x: (CGFloat(index) * step + (index == 0 ? 0 : reveal.stretch)) * towardsInside)
            }
        }
        .frame(width: actionsWidth, alignment: leading ? .leading : .trailing)
        .padding(.leading, leading ? outerPadding : innerPadding)
        .padding(.trailing, leading ? innerPadding : outerPadding)
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
        index: Int,
        stretch: CGFloat,
        committed: Bool
    ) -> some View {
        let colors = resolveIconButtonColors(variant: action.variant, type: .solid)
        // Centred in the capsule until the swipe commits, then it slides to the centre of the
        // capsule's inner end — where the action would sit if it had stayed a circle and the row
        // had simply carried on past it. Which end that is follows the edge it is revealed from.
        // Against the width the commit is heading for rather than the one it has: an offset that
        // chases a target still moving under it never catches it, and lands behind the capsule it
        // slides in. iOS holds the two within 0.012 of each other the whole way, which is this.
        let iconOffset = committed ? committedStretch / 2 * towardsInside : 0
        return SwiftUI.Button { onFired(action) } label: {
            Capsule()
                // Hovered the way the icon button this stands in for is hovered, so a pointer on
                // iPad or macOS does not find the two out of step.
                .fill(hovered == index ? colors.backgroundHoverColor : colors.backgroundColor)
                .frame(width: actionSize + stretch, height: actionSize)
                .overlay {
                    LemonadeUi.Icon(
                        icon: action.icon,
                        contentDescription: action.contentDescription,
                        size: .large,
                        tint: colors.contentColor
                    )
                    // The icon has a spring of its own, and only the icon: it is centred again the
                    // moment the gesture lets go of the action, whatever the width is doing.
                    .offset(x: iconOffset)
                    .animation(commit, value: committed)
                }
                .contentShape(Capsule())
        }
        .buttonStyle(SwipeActionButtonStyle())
        .disabled(holding)
        .onHover { hovering in
            hovered = hovering ? index : (hovered == index ? nil : hovered)
        }
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
    let onFired: (LemonadeSwipeAction) -> Void

    @ViewBuilder
    func body(content: Content) -> some View {
        let element = content.accessibilityElement(children: .combine)
        if #available(iOS 16.0, macOS 13.0, tvOS 16.0, watchOS 9.0, *) {
            // One modifier holding every action. The drag rewrites the row's body on every touch
            // event, and the fold below erases each action into an `AnyView` — which SwiftUI tears
            // down and rebuilds rather than diffing, N times a frame, for the whole gesture.
            element.accessibilityActions {
                ForEach(actions.indices, id: \.self) { index in
                    SwiftUI.Button(actions[index].contentDescription) {
                        onFired(actions[index])
                    }
                }
            }
        } else {
            actions.reduce(AnyView(element)) { view, action in
                AnyView(
                    view.accessibilityAction(named: Text(action.contentDescription)) {
                        onFired(action)
                    }
                )
            }
        }
    }
}

// MARK: - SwipeActionRow Component

public extension LemonadeUi {
    /// Wraps a row with actions revealed by a horizontal drag.
    ///
    /// Actions may sit on either edge, or both. A drag takes the side it sets off towards and
    /// keeps it for the rest of the gesture, so one drag never reveals both. A row opened by its
    /// caller rather than by a drag opens onto `trailingActions`, falling back to `leadingActions`
    /// only when there are none.
    ///
    /// The wrapped item must not draw its own divider — pass `showDivider: false` to it and set
    /// `showDivider` here instead. A list item draws its divider inside its own body, so it would
    /// travel with the row and leave a gap at the trailing edge.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SwipeActionRow(
    ///     trailingActions: [
    ///         LemonadeSwipeAction(icon: .trash, contentDescription: "Remove account", onClick: { })
    ///     ],
    ///     showDivider: true
    /// ) {
    ///     LemonadeUi.ActionListItem(label: "Label", onItemClicked: { })
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - leadingActions: the actions revealed on the leading edge, outermost first
    ///   - trailingActions: the actions revealed on the trailing edge, outermost first
    ///   - enabled: flag to define whether the drag is active
    ///   - allowsFullSwipe: whether dragging across the row fires the first action of whichever
    ///     edge is being dragged, on release
    ///   - showDivider: flag to show a divider below the row, which does not travel with it
    ///   - content: the row this wraps
    @ViewBuilder
    static func SwipeActionRow<Content: View>(
        leadingActions: [LemonadeSwipeAction] = [],
        trailingActions: [LemonadeSwipeAction] = [],
        enabled: Bool = true,
        allowsFullSwipe: Bool = true,
        showDivider: Bool = false,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeUncontrolledSwipeActionRow(
            leadingActions: leadingActions,
            trailingActions: trailingActions,
            enabled: enabled,
            allowsFullSwipe: allowsFullSwipe,
            showDivider: showDivider,
            content: content
        )
    }

    /// `SwipeActionRow` whose open row is controlled by the caller.
    ///
    /// Keeping one row open at a time needs nothing from the caller — that is what
    /// `LemonadeUi.SwipeActionGroup` is for, and it works on these rows too. Reach for this
    /// overload when the caller has to be able to close the row itself: after an action with
    /// `keepsRowOpen` has fired, the row waits on the reader, and only an `openId` the caller owns
    /// can put it back.
    ///
    /// - Parameters:
    ///   - id: identity of this row, compared against `openId`
    ///   - openId: identity of the row currently open, or nil when none is
    @ViewBuilder
    static func SwipeActionRow<Content: View>(
        id: AnyHashable,
        openId: Binding<AnyHashable?>,
        leadingActions: [LemonadeSwipeAction] = [],
        trailingActions: [LemonadeSwipeAction] = [],
        enabled: Bool = true,
        allowsFullSwipe: Bool = true,
        showDivider: Bool = false,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeSwipeActionRowView(
            leadingActions: leadingActions,
            trailingActions: trailingActions,
            enabled: enabled,
            allowsFullSwipe: allowsFullSwipe,
            showDivider: showDivider,
            open: Binding(
                get: { openId.wrappedValue == id },
                // Only ever clears its own slot: a drag that settles closed on one row would
                // otherwise close whichever row the caller actually has open.
                set: { opening in
                    if opening {
                        openId.wrappedValue = id
                    } else if openId.wrappedValue == id {
                        openId.wrappedValue = nil
                    }
                }
            ),
            content: content
        )
    }
}

public extension LemonadeUi {
    /// Groups swipe rows so that at most one of them is open.
    ///
    /// A row cannot see a touch that lands outside it, so the group is what carries the news:
    /// opening one closes the rest, and a tap anywhere inside closes whichever is open. Wrap the
    /// list, or the screen — anything a reader would take as "somewhere else".
    ///
    /// Rows manage themselves inside it, including the ones given an `id` and `openId`, so nothing
    /// has to be hoisted to get this.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SwipeActionGroup {
    ///     ForEach(accounts) { account in
    ///         LemonadeUi.SwipeActionRow(trailingActions: [remove(account)]) {
    ///             LemonadeUi.ActionListItem(label: account.name, onItemClicked: { })
    ///         }
    ///     }
    /// }
    /// ```
    ///
    /// - Parameter content: the rows, and whatever else the group covers
    @ViewBuilder
    static func SwipeActionGroup<Content: View>(
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeSwipeActionGroupView(content: content)
    }
}

private struct LemonadeSwipeActionGroupView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    @State private var signal = SwipeActionGroupSignal()

    var body: some View {
        content()
            .environment(\.swipeActionGroupSignal, signal)
            .environment(\.swipeActionGroupAnnounce) { opener in
                signal = SwipeActionGroupSignal(
                    announcements: signal.announcements + 1,
                    opener: opener
                )
            }
            // Simultaneous, so the tap still reaches whatever was tapped. Closing an open row is
            // not meant to cost the reader the tap that closed it.
            //
            // Settled a turn later, and only if nothing claimed the slot meanwhile. The tap that
            // fires an action is this same tap, and neither platform says whether the action or the
            // gesture is seen first — waiting a turn means a row that has claimed the slot has said
            // so by the time this decides, and this leaves it alone.
            .simultaneousGesture(
                TapGesture().onEnded {
                    let seen = signal.announcements
                    Task { @MainActor in
                        guard signal.announcements == seen else { return }
                        signal = SwipeActionGroupSignal(
                            announcements: signal.announcements + 1,
                            opener: nil
                        )
                    }
                }
            )
    }
}

/// Holds its own open state, so a single row needs no ceremony at the call site.
private struct LemonadeUncontrolledSwipeActionRow<Content: View>: View {
    let leadingActions: [LemonadeSwipeAction]
    let trailingActions: [LemonadeSwipeAction]
    let enabled: Bool
    let allowsFullSwipe: Bool
    let showDivider: Bool
    @ViewBuilder let content: () -> Content

    @State private var open = false

    var body: some View {
        LemonadeSwipeActionRowView(
            leadingActions: leadingActions,
            trailingActions: trailingActions,
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
                trailingActions: [
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
                trailingActions: [
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

            // An action on each edge. One drag reveals one of them.
            LemonadeUi.SwipeActionRow(
                leadingActions: [
                    LemonadeSwipeAction(
                        icon: .check,
                        contentDescription: "Mark as read",
                        onClick: {},
                        variant: .primary
                    )
                ],
                trailingActions: [
                    LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: {})
                ]
            ) {
                LemonadeUi.ActionListItem(
                    label: "Both edges",
                    supportText: "Drag either way",
                    showDivider: false,
                    onItemClicked: {}
                )
            }

            // Controlled: one open row at a time.
            StatefulPreviewWrapper(AnyHashable?.none) { openId in
                LemonadeUi.SwipeActionRow(
                    id: "row",
                    openId: openId,
                    trailingActions: [
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
