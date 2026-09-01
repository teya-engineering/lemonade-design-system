import SwiftUI

// MARK: - Settle policy

/// Fraction of the row's width a drag must cross for a full swipe to commit.
private let commitFraction: CGFloat = 0.5

/// Speed, in pt/s, past which a flick decides the settle regardless of how far it travelled.
private let flingVelocity: CGFloat = 400

/// Growth applied to the first action while a full swipe is committed, taking it from the small
/// button's 40pt to the 48pt the design asks for.
private let committedScale: CGFloat = 1.2

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
/// again should not undo it. Otherwise a flick wins over position, so a short fast drag opens.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, always positive.
///   - velocity: pt/s at release, positive while still travelling open.
///   - revealWidth: width of the action strip, which is where an open row rests.
///   - rowWidth: full width of the row.
///   - allowsFullSwipe: whether a drag across the row may commit the first action.
func resolveSwipeSettle(
    travel: CGFloat,
    velocity: CGFloat,
    revealWidth: CGFloat,
    rowWidth: CGFloat,
    allowsFullSwipe: Bool
) -> SwipeSettleTarget {
    if allowsFullSwipe, travel >= rowWidth * commitFraction {
        return .committed
    }
    // Nothing to open onto: the strip has not measured yet, or there are no actions.
    if revealWidth <= 0 {
        return .closed
    }
    if velocity >= flingVelocity {
        return .open
    }
    if velocity <= -flingVelocity {
        return .closed
    }
    return travel >= revealWidth / 2 ? .open : .closed
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
    /// The same origin, cleared only by `onEnded`, which is what `onEnded` guards on. Keeping the
    /// two apart is what lets the cancel path snap back without stealing the settle.
    @State private var settleOrigin: CGFloat?
    @State private var revealWidth: CGFloat = 0
    @State private var rowWidth: CGFloat = 0
    @State private var committed = false
    @GestureState private var isDragging = false
    @Environment(\.layoutDirection) private var layoutDirection

    /// A reveal on the trailing edge travels left in LTR and right in RTL.
    private var towardsTrailing: CGFloat { layoutDirection == .rightToLeft ? 1 : -1 }

    private var progress: CGFloat {
        guard revealWidth > 0 else { return 0 }
        return min(max(travel / revealWidth, 0), 1)
    }

    var body: some View {
        VStack(spacing: 0) {
            ZStack(alignment: .trailing) {
                actionStrip
                content()
                    // The open row rests on the list item's own press highlight rather than a
                    // surface of its own: same fill, same radius, same gutter.
                    .background(
                        RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                            .fill(LemonadeTheme.colors.interaction.bgSubtleInteractive)
                            .opacity(progress)
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
            // A disabled row must not compete with the enclosing scroll view for the gesture, so
            // the mask drops to `.subviews` rather than the gesture checking `enabled` inside.
            .gesture(drag, including: enabled && !actions.isEmpty ? .all : .subviews)
            .modifier(SwipeAccessibilityActions(actions: actions))

            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            }
        }
        .onChange(of: open) { newValue in
            withAnimation(.spring()) { travel = newValue ? revealWidth : 0 }
        }
        .onChange(of: revealWidth) { newValue in
            // The strip can measure after the row is already open, or re-measure when `actions`
            // change, so an open row would otherwise rest at a stale offset. Never under a live
            // finger, where it would fight the drag.
            guard open, dragOrigin == nil else { return }
            withAnimation(.spring()) { travel = newValue }
        }
        .onChange(of: isDragging) { dragging in
            // A cancelled gesture never delivers `onEnded`, so the snap back has to happen here.
            // Only the claim is released: `settleOrigin` is what `onEnded` guards on, and the
            // order these two are observed in is not documented.
            guard !dragging, dragOrigin != nil else { return }
            dragOrigin = nil
            committed = false
            withAnimation(.spring()) { travel = open ? revealWidth : 0 }
        }
    }

    private var actionStrip: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing200) {
            ForEach(Array(actions.enumerated()), id: \.offset) { index, action in
                // The first action is the one a full swipe fires, so it is the one that grows. It
                // grows by scaling rather than by changing size: a size change re-measures the
                // strip mid-drag, which rewrites the reveal width the settle is resolved against.
                LemonadeUi.IconButton(
                    icon: action.icon,
                    contentDescription: action.contentDescription,
                    onClick: action.onClick,
                    variant: action.variant,
                    type: .solid,
                    size: .small,
                    shape: .circular
                )
                .scaleEffect(committed && index == 0 ? committedScale : 1)
            }
        }
        .padding(.leading, LemonadeTheme.spaces.spacing300)
        .padding(.trailing, LemonadeTheme.spaces.spacing200)
        .background(
            GeometryReader { proxy in
                Color.clear
                    .onAppear { revealWidth = measuredRevealWidth(proxy.size.width) }
                    .onChange(of: proxy.size.width) { revealWidth = measuredRevealWidth($0) }
            }
        )
        // The strip's buttons stay in the accessibility tree even while covered by the row, where
        // they would announce a destructive action ahead of the row it belongs to. The row's own
        // custom actions are the accessible path.
        .accessibilityHidden(true)
    }

    /// An empty strip still measures its padding, which would open the row onto a bare gap.
    private func measuredRevealWidth(_ width: CGFloat) -> CGFloat {
        actions.isEmpty ? 0 : width
    }

    private func clampedTravel(_ value: CGFloat) -> CGFloat {
        let ceiling = allowsFullSwipe ? rowWidth : revealWidth
        return min(max(value, 0), ceiling)
    }

    private var drag: some Gesture {
        DragGesture(minimumDistance: 10)
            .updating($isDragging) { _, state, _ in state = true }
            .onChanged { value in
                if dragOrigin == nil {
                    // Let a vertical scroll win: start tracking only a predominantly
                    // horizontal drag, and never claim the gesture otherwise.
                    guard abs(value.translation.width) > abs(value.translation.height) else { return }
                    dragOrigin = travel
                    settleOrigin = travel
                }
                guard let origin = dragOrigin else { return }
                let next = clampedTravel(origin + value.translation.width * towardsTrailing)
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
                let released = clampedTravel(origin + value.translation.width * towardsTrailing)
                let target = resolveSwipeSettle(
                    travel: released,
                    velocity: releaseVelocity(of: value) * towardsTrailing,
                    revealWidth: revealWidth,
                    rowWidth: rowWidth,
                    allowsFullSwipe: allowsFullSwipe
                )
                switch target {
                case .committed:
                    open = false
                    actions.first?.onClick()
                    withAnimation(.spring()) { travel = 0 }
                case .open:
                    open = true
                    withAnimation(.spring()) { travel = revealWidth }
                case .closed:
                    open = false
                    withAnimation(.spring()) { travel = 0 }
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
