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
    /// A progress stepper. The current row and every row before it keep their voice colour and
    /// join the next row with a selected-border line; rows after the current one are muted, with
    /// tertiary labels.
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
    ///   - voice: Semantic color of the indicator dot when this row is the current step, or a
    ///     completed step in a ``LemonadeHistoryTimelineVariant/progress`` timeline.
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
    /// Set `variant` to ``LemonadeHistoryTimelineVariant/progress`` for a stepper: completed rows
    /// keep their voice colour (typically `.positive`) and upcoming rows are muted.
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
    ///         LemonadeHistoryTimelineItem(label: "Invitation accepted", voice: .positive),
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
}

private enum HistoryItemState {
    case current
    case inactive
    case reached
    case upcoming

    init(index: Int, currentIndex: Int?, variant: LemonadeHistoryTimelineVariant) {
        switch variant {
        case .default:
            self = index == currentIndex ? .current : .inactive
        case .progress:
            if let currentIndex, index <= currentIndex {
                self = .reached
            } else {
                self = .upcoming
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
                    state: HistoryItemState(index: index, currentIndex: currentIndex, variant: variant),
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
        case .current, .reached: return item.voice.currentColor
        case .inactive, .upcoming: return LemonadeTheme.colors.border.borderNeutralMedium
        }
    }

    private var lineColor: Color {
        switch state {
        case .reached: return LemonadeTheme.colors.border.borderSelected
        case .current, .inactive, .upcoming: return LemonadeTheme.colors.border.borderNeutralMedium
        }
    }

    private var labelColor: Color {
        switch state {
        case .upcoming: return LemonadeTheme.colors.content.contentTertiary
        case .current, .inactive, .reached: return LemonadeTheme.colors.content.contentPrimary
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
            HistoryItemIndicator(dotColor: dotColor, lineColor: lineColor, isLast: isLast)
        }
    }
}

private struct HistoryItemIndicator: View {
    let dotColor: Color
    let lineColor: Color
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
                    .fill(lineColor)
                    .frame(width: lineWidth)
                    .frame(maxHeight: .infinity)
                    .padding(.bottom, -lineCapRadius)
            }
        }
        .frame(width: HistoryTimelineMetrics.indicatorWidth)
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
