import SwiftUI

// MARK: - LemonadeHistoryItemVoice

/// Defines the semantic voice of a HistoryItem indicator dot when rendered as the current step.
public enum LemonadeHistoryItemVoice {
    case neutral
    case positive
    case critical

    var currentColor: Color {
        switch self {
        case .neutral: return LemonadeTheme.colors.background.bgNeutral
        case .positive: return LemonadeTheme.colors.background.bgPositive
        case .critical: return LemonadeTheme.colors.background.bgCritical
        }
    }
}

// MARK: - LemonadeHistoryTimelineVariant

/// How a ``LemonadeUi/HistoryTimeline(items:currentIndex:variant:)`` styles the rows around its
/// current step.
public enum LemonadeHistoryTimelineVariant {
    /// Only the current row takes its voice colour; every other row is muted.
    case `default`
    /// A progress stepper. Rows before the current one show a check, the current row shows an
    /// in-progress ring with an animated positive gradient line, and rows after it are muted with a dashed
    /// line and tertiary labels. A reached row with a critical voice shows an error icon instead.
    case progress
}

// MARK: - LemonadeHistoryTimelineItem

/// Describes a single row inside a ``LemonadeUi/HistoryTimeline(items:currentIndex:variant:)``.
///
/// Use `contentSlot` to inject arbitrary view content (e.g. a button, tag, or nested
/// layout) below the description.
public struct LemonadeHistoryTimelineItem {
    public let label: String
    public let subheading: String?
    public let description: String?
    public let voice: LemonadeHistoryItemVoice
    let contentSlot: (() -> AnyView)?

    /// Creates a history timeline row description.
    ///
    /// - Parameters:
    ///   - label: Primary row text.
    ///   - subheading: Optional secondary text shown immediately below the label.
    ///   - description: Optional tertiary paragraph shown below the subheading.
    ///   - voice: Semantic color of the indicator dot when this row is the current step. In a
    ///     ``LemonadeHistoryTimelineVariant/progress`` timeline, `.critical` marks a reached step
    ///     as failed; other voices are ignored.
    ///   - contentSlot: Optional view builder for custom content rendered below the
    ///     description (e.g. a button or tag).
    public init<Content: View>(
        label: String,
        subheading: String? = nil,
        description: String? = nil,
        voice: LemonadeHistoryItemVoice = .neutral,
        @ViewBuilder contentSlot: @escaping () -> Content
    ) {
        self.label = label
        self.subheading = subheading
        self.description = description
        self.voice = voice
        self.contentSlot = { AnyView(contentSlot()) }
    }

    /// Creates a history timeline row description without a custom content slot.
    public init(
        label: String,
        subheading: String? = nil,
        description: String? = nil,
        voice: LemonadeHistoryItemVoice = .neutral
    ) {
        self.label = label
        self.subheading = subheading
        self.description = description
        self.voice = voice
        self.contentSlot = nil
    }
}

// MARK: - Public API

public extension LemonadeUi {
    /// A vertical list of history steps with a left-aligned indicator column.
    ///
    /// Each row shows a colored dot; the current row uses the item's
    /// ``LemonadeHistoryItemVoice`` color, while all other rows use a muted neutral.
    /// A connecting line joins every row except the last.
    ///
    /// For rows with custom content (buttons, tags, etc.), set `contentSlot` on the
    /// ``LemonadeHistoryTimelineItem``.
    ///
    /// Set `variant` to ``LemonadeHistoryTimelineVariant/progress`` for a stepper: rows before the
    /// current one show a check, the current row shows an in-progress ring, and upcoming rows are
    /// muted with a dashed line. A reached row with a `.critical` voice shows an error icon instead.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.HistoryTimeline(
    ///     items: [
    ///         LemonadeHistoryTimelineItem(
    ///             label: "Find a Visa PayPoint next to you",
    ///             description: "PayPoint locations collect cash deposits on our behalf",
    ///             voice: .positive
    ///         ) {
    ///             LemonadeUi.Button(label: "Find a PayPoint") { /* ... */ }
    ///         },
    ///         LemonadeHistoryTimelineItem(
    ///             label: "Enter the amount and generate a barcode",
    ///             description: "Show the barcode to the shopkeeper and deposit the funds"
    ///         )
    ///     ],
    ///     currentIndex: 0
    /// )
    ///
    /// LemonadeUi.HistoryTimeline(
    ///     items: [
    ///         LemonadeHistoryTimelineItem(label: "Invitation accepted"),
    ///         LemonadeHistoryTimelineItem(label: "Logs into the app"),
    ///         LemonadeHistoryTimelineItem(label: "Card activation")
    ///     ],
    ///     currentIndex: 1,
    ///     variant: .progress
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - items: Ordered list of rows to display.
    ///   - currentIndex: Index of the row to render as the current step. Pass `nil`
    ///     to render every row in its non-current (muted) state. Defaults to `0`. In a
    ///     ``LemonadeHistoryTimelineVariant/progress`` timeline, an index past the last row
    ///     shows every step completed.
    ///   - variant: How rows around the current step are styled. Defaults to `.default`.
    @ViewBuilder
    static func HistoryTimeline(
        items: [LemonadeHistoryTimelineItem],
        currentIndex: Int? = 0,
        variant: LemonadeHistoryTimelineVariant = .default
    ) -> some View {
        LemonadeHistoryTimelineView(
            items: items,
            currentIndex: currentIndex,
            variant: variant
        )
    }
}

// MARK: - Internal Views

private enum HistoryTimelineMetrics {
    static let indicatorWidth: CGFloat = 20
    static let dotSize: CGFloat = 10
    static let dotTopOffset: CGFloat = 7
    static let dotLineGap: CGFloat = 6
    static let progressDotAreaHeight: CGFloat = 24
    static let progressRingSize: CGFloat = 16
    static let progressActiveDotSize: CGFloat = 8
    static let progressLineInset: CGFloat = 2
    static let progressDashPeriod: CGFloat = 10
    static let progressDashFraction: CGFloat = 0.4
}

private enum HistoryItemState {
    case current
    case inactive
    case completed
    case error
    case active
    case next

    init(
        index: Int,
        currentIndex: Int?,
        voice: LemonadeHistoryItemVoice,
        variant: LemonadeHistoryTimelineVariant
    ) {
        switch variant {
        case .default:
            self = index == currentIndex ? .current : .inactive
        case .progress:
            guard let currentIndex, index <= currentIndex else {
                self = .next
                return
            }
            if voice == .critical {
                self = .error
            } else {
                self = index == currentIndex ? .active : .completed
            }
        }
    }
}

private struct LemonadeHistoryTimelineView: View {
    let items: [LemonadeHistoryTimelineItem]
    let currentIndex: Int?
    let variant: LemonadeHistoryTimelineVariant

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                CoreHistoryTimelineItemView(
                    item: item,
                    state: HistoryItemState(
                        index: index,
                        currentIndex: currentIndex,
                        voice: item.voice,
                        variant: variant
                    ),
                    isLast: index == items.count - 1
                )
            }
        }
    }
}

private struct CoreHistoryTimelineItemView: View {
    let item: LemonadeHistoryTimelineItem
    let state: HistoryItemState
    let isLast: Bool

    private var dotColor: Color {
        switch state {
        case .current: return item.voice.currentColor
        case .inactive, .completed, .error, .active, .next: return LemonadeTheme.colors.border.borderNeutralMedium
        }
    }

    private var labelColor: Color {
        switch state {
        case .next: return LemonadeTheme.colors.content.contentTertiary
        case .current, .inactive, .completed, .error, .active: return LemonadeTheme.colors.content.contentPrimary
        }
    }

    var body: some View {
        HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing300) {
            Color.clear
                .frame(width: HistoryTimelineMetrics.indicatorWidth)

            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
                VStack(alignment: .leading, spacing: 0) {
                    LemonadeUi.Text(
                        item.label,
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        color: labelColor
                    )

                    if let subheading = item.subheading {
                        LemonadeUi.Text(
                            subheading,
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentSecondary
                        )
                    }
                }

                if let description = item.description {
                    LemonadeUi.Text(
                        description,
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }

                if let contentSlot = item.contentSlot {
                    contentSlot()
                }
            }
            .padding(.bottom, isLast ? 0 : LemonadeTheme.spaces.spacing600)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(alignment: .topLeading) {
            switch state {
            case .current, .inactive:
                HistoryItemIndicator(dotColor: dotColor, isLast: isLast)
            case .completed, .error, .active, .next:
                HistoryProgressIndicator(state: state, isLast: isLast)
            }
        }
    }
}

private struct HistoryItemIndicator: View {
    let dotColor: Color
    let isLast: Bool

    private var lineWidth: CGFloat { LemonadeTheme.borderWidth.base.border40 }

    // Round caps extend past the path's endpoints by half the stroke, as in Figma.
    private var lineCapRadius: CGFloat { lineWidth / 2 }

    var body: some View {
        VStack(spacing: 0) {
            Spacer()
                .frame(height: HistoryTimelineMetrics.dotTopOffset)

            Circle()
                .fill(dotColor)
                .frame(
                    width: HistoryTimelineMetrics.dotSize,
                    height: HistoryTimelineMetrics.dotSize
                )

            if !isLast {
                Spacer()
                    .frame(height: HistoryTimelineMetrics.dotLineGap - lineCapRadius)

                Capsule()
                    .fill(LemonadeTheme.colors.border.borderNeutralMedium)
                    .frame(width: lineWidth)
                    .frame(maxHeight: .infinity)
                    .padding(.bottom, -lineCapRadius)
            }
        }
        .frame(width: HistoryTimelineMetrics.indicatorWidth)
    }
}

private struct HistoryProgressIndicator: View {
    let state: HistoryItemState
    let isLast: Bool

    private var lineStyle: StrokeStyle {
        StrokeStyle(lineWidth: LemonadeTheme.borderWidth.base.border50, lineCap: .round)
    }

    var body: some View {
        VStack(spacing: 0) {
            marker
                .frame(height: HistoryTimelineMetrics.progressDotAreaHeight)

            if !isLast {
                line
                    .frame(maxHeight: .infinity)
                    .padding(.vertical, HistoryTimelineMetrics.progressLineInset)
            }
        }
        .frame(width: HistoryTimelineMetrics.indicatorWidth)
    }

    @ViewBuilder
    private var marker: some View {
        switch state {
        case .completed:
            LemonadeUi.Icon(
                icon: .circleCheckSolid,
                contentDescription: nil,
                size: .medium,
                tint: LemonadeTheme.colors.background.bgPositive
            )
        case .error:
            LemonadeUi.Icon(
                icon: .circleXSolid,
                contentDescription: nil,
                size: .medium,
                tint: LemonadeTheme.colors.background.bgCritical
            )
        case .active:
            ring.overlay {
                Circle()
                    .fill(LemonadeTheme.colors.background.bgNeutral)
                    .frame(
                        width: HistoryTimelineMetrics.progressActiveDotSize,
                        height: HistoryTimelineMetrics.progressActiveDotSize
                    )
            }
        case .next:
            ring
        case .current, .inactive:
            EmptyView()
        }
    }

    private var ring: some View {
        Circle()
            .fill(LemonadeTheme.colors.background.bgDefault)
            .overlay {
                Circle().strokeBorder(
                    LemonadeTheme.colors.border.borderNeutralHigh,
                    lineWidth: LemonadeTheme.borderWidth.base.border40
                )
            }
            .frame(
                width: HistoryTimelineMetrics.progressRingSize,
                height: HistoryTimelineMetrics.progressRingSize
            )
    }

    @ViewBuilder
    private var line: some View {
        switch state {
        case .next:
            HistoryProgressDashedLine()
                .stroke(LemonadeTheme.colors.border.borderNeutralHigh, style: lineStyle)
        case .active:
            HistoryProgressLine()
                .stroke(LemonadeTheme.colors.border.borderNeutralMedium, style: lineStyle)
                .overlay { HistoryActiveLineFill(style: lineStyle) }
        case .completed, .error, .current, .inactive:
            HistoryProgressLine()
                .stroke(LemonadeTheme.colors.border.borderNeutralMedium, style: lineStyle)
        }
    }
}

private struct HistoryActiveLineFill: View {
    let style: StrokeStyle

    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var startDate = Date()

    private static let fadeStart: TimeInterval = 1.35
    private static let fillDuration: TimeInterval = 1.8
    private static let cycleDuration: TimeInterval = 2.0

    var body: some View {
        if reduceMotion {
            fill(progress: 1, opacity: 1)
        } else {
            TimelineView(.animation) { context in
                let time = context.date.timeIntervalSince(startDate)
                    .truncatingRemainder(dividingBy: Self.cycleDuration)
                fill(progress: Self.progress(at: time), opacity: Self.opacity(at: time))
            }
        }
    }

    private func fill(progress: CGFloat, opacity: Double) -> some View {
        GeometryReader { proxy in
            let color = LemonadeTheme.colors.border.borderPositive
            HistoryProgressLine()
                .stroke(
                    LinearGradient(colors: [color, color.opacity(0)], startPoint: .top, endPoint: .bottom),
                    style: style
                )
                .frame(height: proxy.size.height * progress)
                .opacity(progress > 0 ? opacity : 0)
        }
    }

    private static func progress(at time: TimeInterval) -> CGFloat {
        guard time < fillDuration else { return 1 }
        return easeInOut(time / fillDuration)
    }

    private static func opacity(at time: TimeInterval) -> Double {
        guard time > fadeStart else { return 1 }
        return max(0, 1 - (time - fadeStart) / (fillDuration - fadeStart))
    }

    /// `cubic-bezier(0.42, 0, 0.58, 1)`, the curve SwiftUI's `.easeInOut` and Compose's `EaseInOut` use.
    private static func easeInOut(_ x: Double) -> CGFloat {
        var low = 0.0
        var high = 1.0
        var t = x
        for _ in 0..<20 {
            let inverse = 1 - t
            let curveX = 3 * inverse * inverse * t * 0.42 + 3 * inverse * t * t * 0.58 + t * t * t
            if curveX < x { low = t } else { high = t }
            t = (low + high) / 2
        }
        return CGFloat(3 * t * t - 2 * t * t * t)
    }
}

private struct HistoryProgressLine: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY))
        return path
    }
}

private struct HistoryProgressDashedLine: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        guard rect.height > 0 else { return path }
        let periods = max(1, Int((rect.height / HistoryTimelineMetrics.progressDashPeriod).rounded()))
        let period = rect.height / CGFloat(periods)
        let halfDash = period * HistoryTimelineMetrics.progressDashFraction / 2
        for index in 0...periods {
            let center = rect.minY + CGFloat(index) * period
            path.move(to: CGPoint(x: rect.midX, y: max(rect.minY, center - halfDash)))
            path.addLine(to: CGPoint(x: rect.midX, y: min(rect.maxY, center + halfDash)))
        }
        return path
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeHistoryTimeline_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing600) {
                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Find a Visa PayPoint next to you",
                            description: "PayPoint locations collect cash deposits on our behalf",
                            voice: .positive
                        ),
                        LemonadeHistoryTimelineItem(
                            label: "Enter the amount and generate a barcode",
                            description: "Show the barcode to the shopkeeper and deposit the funds"
                        ),
                        LemonadeHistoryTimelineItem(
                            label: "After deposit, your money will be available in 10 minutes",
                            description: "We'll notify you once the funds are in your account"
                        )
                    ]
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Find a Visa PayPoint next to you",
                            description: "PayPoint locations collect cash deposits on our behalf",
                            voice: .positive
                        ) {
                            LemonadeUi.Button(
                                label: "Find a PayPoint",
                                onClick: {},
                                variant: .secondary
                            )
                        },
                        LemonadeHistoryTimelineItem(
                            label: "Enter the amount and generate a barcode",
                            description: "Show the barcode to the shopkeeper and deposit the funds"
                        ),
                        LemonadeHistoryTimelineItem(
                            label: "After deposit, your money will be available in 10 minutes",
                            description: "We'll notify you once the funds are in your account"
                        )
                    ],
                    currentIndex: 0
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Positive current",
                            subheading: "Subheading",
                            voice: .positive
                        )
                    ]
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Critical current",
                            subheading: "Subheading",
                            voice: .critical
                        )
                    ]
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Neutral current",
                            subheading: "Subheading",
                            voice: .neutral
                        )
                    ]
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(
                            label: "Neutral past",
                            subheading: "Subheading",
                            voice: .neutral
                        )
                    ],
                    currentIndex: nil
                )

                LemonadeUi.HistoryTimeline(
                    items: [
                        LemonadeHistoryTimelineItem(label: "Invitation accepted", voice: .positive),
                        LemonadeHistoryTimelineItem(
                            label: "Logs into the Teya App",
                            subheading: "Sofia needs to download the Teya app and log in."
                        ),
                        LemonadeHistoryTimelineItem(label: "Card activation")
                    ],
                    currentIndex: 1,
                    variant: .progress
                )
            }
            .padding()
        }
        .previewLayout(.sizeThatFits)
    }
}
#endif
