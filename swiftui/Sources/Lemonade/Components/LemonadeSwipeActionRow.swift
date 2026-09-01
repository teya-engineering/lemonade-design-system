import SwiftUI

// MARK: - Settle policy

/// Fraction of the row's width a drag must cross for a full swipe to commit.
private let commitFraction: CGFloat = 0.5

/// Speed, in pt/s, past which a flick decides the settle regardless of how far it travelled.
private let flingVelocity: CGFloat = 400

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
    if velocity >= flingVelocity {
        return .open
    }
    if velocity <= -flingVelocity {
        return .closed
    }
    return travel >= revealWidth / 2 ? .open : .closed
}

// MARK: - SwipeAction

/// One action revealed behind a `LemonadeUi.SwipeActionRow`.
///
/// `contentDescription` has no default because it is what publishes the action to VoiceOver, where
/// the gesture itself is invisible.
public struct SwipeAction {
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
    let actions: [SwipeAction]
    let enabled: Bool
    let allowsFullSwipe: Bool
    let showDivider: Bool
    @Binding var open: Bool
    @ViewBuilder let content: () -> Content

    @State private var travel: CGFloat = 0
    @State private var dragOrigin: CGFloat?
    @State private var revealWidth: CGFloat = 0
    @State private var rowWidth: CGFloat = 0
    @State private var committed = false
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
                    Color.clear.onAppear { rowWidth = proxy.size.width }
                }
            )
            .gesture(drag)
            .modifier(SwipeAccessibilityActions(actions: actions))

            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing100)
            }
        }
        .onChange(of: open) { newValue in
            withAnimation(.spring()) { travel = newValue ? revealWidth : 0 }
        }
    }

    private var actionStrip: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing200) {
            ForEach(Array(actions.enumerated()), id: \.offset) { index, action in
                LemonadeUi.IconButton(
                    icon: action.icon,
                    contentDescription: action.contentDescription,
                    onClick: action.onClick,
                    variant: action.variant,
                    type: .solid,
                    // The first action is the one a full swipe fires, so it is the one that grows.
                    size: committed && index == 0 ? .medium : .small,
                    shape: .circular
                )
            }
        }
        .padding(.leading, LemonadeTheme.spaces.spacing300)
        .padding(.trailing, LemonadeTheme.spaces.spacing200)
        .background(
            GeometryReader { proxy in
                Color.clear.onAppear { revealWidth = proxy.size.width }
            }
        )
        .modifier(SwipeStripAccessibility())
    }

    private var drag: some Gesture {
        DragGesture(minimumDistance: 10)
            .onChanged { value in
                guard enabled, !actions.isEmpty else { return }
                if dragOrigin == nil {
                    // Let a vertical scroll win: start tracking only a predominantly
                    // horizontal drag, and never claim the gesture otherwise.
                    guard abs(value.translation.width) > abs(value.translation.height) else { return }
                    dragOrigin = travel
                }
                guard let origin = dragOrigin else { return }
                let ceiling = allowsFullSwipe ? rowWidth : revealWidth
                let next = min(max(origin + value.translation.width * towardsTrailing, 0), ceiling)
                let crossed = allowsFullSwipe && next >= rowWidth * commitFraction
                if crossed != committed {
                    committed = crossed
                    if crossed { playCommitHaptic() }
                }
                travel = next
            }
            .onEnded { value in
                guard dragOrigin != nil else { return }
                dragOrigin = nil
                committed = false
                let target = resolveSwipeSettle(
                    travel: travel,
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

    /// `DragGesture.Value.velocity` is iOS 17, and this package targets iOS 15. UIKit projects a
    /// decelerating drag roughly a quarter-second ahead, so the gap between the predicted end and
    /// the current translation is a quarter of the velocity.
    private func releaseVelocity(of value: DragGesture.Value) -> CGFloat {
        (value.predictedEndTranslation.width - value.translation.width) * 4
    }

    private func playCommitHaptic() {
        #if canImport(UIKit) && !os(watchOS)
        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
        #endif
    }
}

// MARK: - Accessibility

/// VoiceOver reaches the actions through the row on iOS 16+, where `accessibilityActions` exists.
private struct SwipeAccessibilityActions: ViewModifier {
    let actions: [SwipeAction]

    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 16.0, macOS 13.0, *) {
            content.accessibilityActions {
                ForEach(Array(actions.enumerated()), id: \.offset) { _, action in
                    Button(action.contentDescription, action: action.onClick)
                }
            }
        } else {
            content
        }
    }
}

/// The strip's buttons stay in the accessibility tree even while covered by the row. Wherever the
/// row publishes custom actions they would be announced twice, so the strip is hidden there — and
/// left visible below iOS 16, where the buttons are the only accessible path to the action.
private struct SwipeStripAccessibility: ViewModifier {
    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 16.0, macOS 13.0, *) {
            content.accessibilityHidden(true)
        } else {
            content
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
    ///         SwipeAction(icon: .trash, contentDescription: "Remove account", onClick: { })
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
        actions: [SwipeAction],
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
        actions: [SwipeAction],
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
    let actions: [SwipeAction]
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
