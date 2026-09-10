import SwiftUI

// MARK: - Accessibility Formatter

/// Shared date formatter for generating VoiceOver-friendly date labels.
///
/// Produces full date strings such as "Thursday, April 2, 2026". File scope keeps
/// it to a single allocation; `DateFormatter` initialization is expensive.
private let calendarDayCellAccessibilityFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateStyle = .full
    return formatter
}()

// MARK: - Calendar Day Cell

/// A generic day cell that wraps ``ContentCellView`` with an optional weekday label
/// on top and an optional trailing content slot below.
///
/// Used by both ``LemonadeUi/DatePicker`` and ``LemonadeUi/InlineCalendar``
/// to render individual day cells with consistent styling.
///
/// - Parameters:
///   - TrailingContent: The type of view displayed below the day number.
struct CalendarDayCell<TrailingContent: View>: View {

    // MARK: - Properties

    let date: Date?
    let text: String
    let isCurrent: Bool
    let isSelected: Bool
    let isEnabled: Bool
    let isOutsideVisibleRange: Bool
    let isInsideSelectedRange: Bool
    let showWeekdayLabel: Bool
    let weekdayLabel: String?
    /// Controls the extent of the selection background when `showWeekdayLabel` is `true`.
    ///
    /// When `true` (default), the brand background covers the entire cell - weekday
    /// label, day number, and trailing content. When `false`, only the day number
    /// circle draws the brand background (DatePicker style), and the weekday label
    /// retains its default color.
    let expandSelectionToLabel: Bool
    let showTodayIndicator: Bool
    let selectionBackgroundColor: Color?
    let selectionContentColor: Color?
    let onClick: () -> Void
    let trailingContent: () -> TrailingContent

    init(
        date: Date? = nil,
        text: String,
        isCurrent: Bool,
        isSelected: Bool,
        isEnabled: Bool,
        isOutsideVisibleRange: Bool,
        isInsideSelectedRange: Bool = false,
        showWeekdayLabel: Bool = true,
        weekdayLabel: String? = nil,
        expandSelectionToLabel: Bool = true,
        showTodayIndicator: Bool = false,
        selectionBackgroundColor: Color? = nil,
        selectionContentColor: Color? = nil,
        onClick: @escaping () -> Void,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent = { EmptyView() }
    ) {
        self.date = date
        self.text = text
        self.isCurrent = isCurrent
        self.isSelected = isSelected
        self.isEnabled = isEnabled
        self.isOutsideVisibleRange = isOutsideVisibleRange
        self.isInsideSelectedRange = isInsideSelectedRange
        self.showWeekdayLabel = showWeekdayLabel
        self.weekdayLabel = weekdayLabel
        self.expandSelectionToLabel = expandSelectionToLabel
        self.showTodayIndicator = showTodayIndicator
        self.selectionBackgroundColor = selectionBackgroundColor
        self.selectionContentColor = selectionContentColor
        self.onClick = onClick
        self.trailingContent = trailingContent
    }

    private var cellAccessibilityLabel: String {
        if let date = date {
            return calendarDayCellAccessibilityFormatter.string(from: date)
        }
        return text
    }

    private var weekdayLabelColor: Color {
        if isSelected && expandSelectionToLabel {
            return selectionContentColor ?? LemonadeTheme.colors.content.contentOnBrandHigh
        }
        return LemonadeTheme.colors.content.contentPrimary
    }

    @ViewBuilder
    private func selectionBackground(active: Bool, inset: CGFloat = 0) -> some View {
        if active {
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius200)
                .fill(selectionBackgroundColor ?? LemonadeTheme.colors.interaction.bgBrandInteractive)
                .padding(.horizontal, inset)
                .padding(.vertical, inset)
        }
    }

    private var dayNumberGroup: some View {
        VStack(spacing: 0) {
            ContentCellView(
                text: text,
                accessibilityLabel: cellAccessibilityLabel,
                isCurrent: isCurrent,
                isSelected: isSelected,
                isEnabled: isEnabled,
                isOutsideVisibleRange: isOutsideVisibleRange,
                isInsideSelectedRange: isInsideSelectedRange,
                showSelectionBackground: false,
                showTodayIndicator: showTodayIndicator,
                selectionContentColor: selectionContentColor,
                onClick: onClick
            )

            trailingContent()
        }
        .padding(.vertical, !expandSelectionToLabel ? LemonadeTheme.spaces.spacing100 : 0)
        .background(
            selectionBackground(
                active: !expandSelectionToLabel && isSelected,
                inset: -LemonadeTheme.spaces.spacing100
            )
        )
    }

    var body: some View {
        VStack(spacing: 0) {
            if showWeekdayLabel, let label = weekdayLabel {
                LemonadeUi.Text(
                    label,
                    textStyle: LemonadeTypography.shared.bodyXSmallOverline,
                    color: weekdayLabelColor
                )
                .frame(maxWidth: .infinity)
            }

            dayNumberGroup
        }
        .padding(showWeekdayLabel ? LemonadeTheme.spaces.spacing100 : 0)
        .background(
            selectionBackground(active: expandSelectionToLabel && isSelected)
        )
    }
}
