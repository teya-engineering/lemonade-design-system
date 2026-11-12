import SwiftUI

// MARK: - LemonadeProgressStepperItem

/// Describes a single segment inside a ``LemonadeUi/ProgressStepper(items:completedSteps:)``.
public struct LemonadeProgressStepperItem {
    public let label: String?
    public let subheading: String?

    /// Creates a progress stepper segment description.
    ///
    /// - Parameters:
    ///   - label: Optional step title shown below the segment's bar.
    ///   - subheading: Optional secondary text shown below the label.
    public init(
        label: String? = nil,
        subheading: String? = nil
    ) {
        self.label = label
        self.subheading = subheading
    }
}

// MARK: - Public API

public extension LemonadeUi {
    /// A horizontal row of segments that shows how far a known, linear process has progressed,
    /// such as an order moving from ordered to dispatched to delivered.
    ///
    /// The first `completedSteps` segments render a positive bar and full-contrast text; the rest
    /// render muted. The segment right after the completed ones is the step in progress, and a
    /// positive gradient sweeps along its bar on a loop. Set `isFailed` when that step has failed:
    /// its bar turns critical and the sweep stops. Text under the first segment aligns to the
    /// leading edge, under the last to the trailing edge, and under any segment in between to the
    /// centre. Leave `label` and `subheading` `nil` for a bars-only stepper.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.ProgressStepper(
    ///     items: [
    ///         LemonadeProgressStepperItem(label: "Ordered", subheading: "12 Jun"),
    ///         LemonadeProgressStepperItem(label: "Dispatched", subheading: "14 Jun"),
    ///         LemonadeProgressStepperItem(label: "Delivered")
    ///     ],
    ///     completedSteps: 2
    /// )
    ///
    /// LemonadeUi.ProgressStepper(
    ///     items: [
    ///         LemonadeProgressStepperItem(label: "Ordered", subheading: "12 Jun"),
    ///         LemonadeProgressStepperItem(label: "Delivery failed", subheading: "14 Jun"),
    ///         LemonadeProgressStepperItem(label: "Delivered")
    ///     ],
    ///     completedSteps: 1,
    ///     isFailed: true
    /// )
    ///
    /// LemonadeUi.ProgressStepper(
    ///     items: Array(repeating: LemonadeProgressStepperItem(), count: 3),
    ///     completedSteps: 1
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - items: Ordered list of segments.
    ///   - completedSteps: How many segments, counted from the start, render as completed.
    ///     Values outside `0...items.count` are clamped.
    ///   - isFailed: Whether the step in progress, the one at index `completedSteps`, has failed.
    ///     Has no effect once every step is completed. Defaults to `false`.
    @ViewBuilder
    static func ProgressStepper(
        items: [LemonadeProgressStepperItem],
        completedSteps: Int,
        isFailed: Bool = false
    ) -> some View {
        LemonadeProgressStepperView(
            items: items,
            completedSteps: completedSteps,
            isFailed: isFailed
        )
    }
}

// MARK: - Internal Views

private enum ProgressStepperState: String {
    case completed = "Completed"
    case current = "Current"
    case failed = "Failed"
    case upcoming = "Upcoming"

    init(index: Int, completedCount: Int, isFailed: Bool) {
        if index < completedCount {
            self = .completed
        } else if index > completedCount {
            self = .upcoming
        } else {
            self = isFailed ? .failed : .current
        }
    }

    var isReached: Bool {
        switch self {
        case .completed, .failed: return true
        case .current, .upcoming: return false
        }
    }

    var barColor: Color {
        switch self {
        case .completed: return LemonadeTheme.colors.background.bgPositive
        case .failed: return LemonadeTheme.colors.background.bgCritical
        case .current, .upcoming: return LemonadeTheme.colors.background.bgElevatedHigh
        }
    }
}

private enum ProgressStepperPosition {
    case first
    case middle
    case last

    init(index: Int, lastIndex: Int) {
        if index == 0 {
            self = .first
        } else if index == lastIndex {
            self = .last
        } else {
            self = .middle
        }
    }

    var horizontalAlignment: HorizontalAlignment {
        switch self {
        case .first: return .leading
        case .middle: return .center
        case .last: return .trailing
        }
    }

    var textAlignment: TextAlignment {
        switch self {
        case .first: return .leading
        case .middle: return .center
        case .last: return .trailing
        }
    }
}

private struct LemonadeProgressStepperView: View {
    let items: [LemonadeProgressStepperItem]
    let completedSteps: Int
    let isFailed: Bool

    var body: some View {
        if !items.isEmpty {
            let completedCount = min(max(completedSteps, 0), items.count)
            HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                    let state = ProgressStepperState(index: index, completedCount: completedCount, isFailed: isFailed)
                    LemonadeProgressStepperSegment(
                        item: item,
                        state: state,
                        position: ProgressStepperPosition(index: index, lastIndex: items.count - 1),
                        stepNumber: index + 1,
                        stepCount: items.count
                    )
                    .frame(maxWidth: .infinity)
                }
            }
        }
    }
}

private struct LemonadeProgressStepperSegment: View {
    let item: LemonadeProgressStepperItem
    let state: ProgressStepperState
    let position: ProgressStepperPosition
    let stepNumber: Int
    let stepCount: Int

    private var hasText: Bool {
        item.label != nil || item.subheading != nil
    }

    private var labelColor: Color {
        state.isReached
            ? LemonadeTheme.colors.content.contentPrimary
            : LemonadeTheme.colors.content.contentTertiary
    }

    private var subheadingColor: Color {
        state.isReached
            ? LemonadeTheme.colors.content.contentSecondary
            : LemonadeTheme.colors.content.contentTertiary
    }

    var body: some View {
        VStack(alignment: position.horizontalAlignment, spacing: LemonadeTheme.spaces.spacing200) {
            Capsule()
                .fill(state.barColor)
                .overlay {
                    if state == .current {
                        ProgressStepperSweep()
                    }
                }
                .clipShape(Capsule())
                .frame(maxWidth: .infinity)
                .frame(height: LemonadeTheme.sizes.size100)
                .accessibilityLabel("Step \(stepNumber) of \(stepCount)")
                .accessibilityHidden(hasText)

            if hasText {
                VStack(alignment: position.horizontalAlignment, spacing: 0) {
                    if let label = item.label {
                        LemonadeUi.Text(
                            label,
                            textStyle: LemonadeTypography.shared.bodySmallMedium,
                            textAlign: position.textAlignment,
                            color: labelColor
                        )
                    }
                    if let subheading = item.subheading {
                        LemonadeUi.Text(
                            subheading,
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            textAlign: position.textAlignment,
                            color: subheadingColor
                        )
                    }
                }
            }
        }
        .accessibilityElement(children: .combine)
        .accessibilityValue(hasText ? "\(state.rawValue), step \(stepNumber) of \(stepCount)" : state.rawValue)
    }
}

private struct ProgressStepperSweep: View {
    var body: some View {
        LemonadeProgressSweep(cycleDuration: 2) { progress, opacity in
            GeometryReader { proxy in
                let color = LemonadeTheme.colors.background.bgPositive
                Rectangle()
                    .fill(LinearGradient(colors: [color, color.opacity(0)], startPoint: .leading, endPoint: .trailing))
                    .flipsForRightToLeftLayoutDirection(true)
                    .frame(width: proxy.size.width * progress)
                    .opacity(opacity)
            }
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeProgressStepper_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: LemonadeTheme.spaces.spacing600) {
            ForEach(1...3, id: \.self) { completedSteps in
                LemonadeUi.ProgressStepper(
                    items: Array(
                        repeating: LemonadeProgressStepperItem(label: "Step title", subheading: "Subheading"),
                        count: 3
                    ),
                    completedSteps: completedSteps
                )
                LemonadeUi.ProgressStepper(
                    items: Array(repeating: LemonadeProgressStepperItem(), count: 3),
                    completedSteps: completedSteps
                )
            }
            LemonadeUi.ProgressStepper(
                items: Array(
                    repeating: LemonadeProgressStepperItem(label: "Step title", subheading: "Subheading"),
                    count: 3
                ),
                completedSteps: 1,
                isFailed: true
            )
        }
        .padding(LemonadeTheme.spaces.spacing400)
    }
}
#endif
