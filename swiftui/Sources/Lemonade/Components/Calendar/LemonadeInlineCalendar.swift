import SwiftUI

#if canImport(UIKit)
import UIKit
#endif

// MARK: - Public API

public extension LemonadeUi {
    /// An inline (horizontal-scrolling) calendar component from the Lemonade Design System.
    ///
    /// Displays a continuous horizontal scroll strip spanning approximately 10 years
    /// (5 years before and after today) using index-based virtual scrolling. The header
    /// label updates to reflect which month occupies the center of the viewport.
    /// Arrow buttons navigate month-by-month.
    ///
    /// ## Usage
    /// ```swift
    /// @StateObject var state = LemonadeInlineCalendarState()
    ///
    /// LemonadeUi.InlineCalendar(state: state)
    ///
    /// // With trailing content dots:
    /// LemonadeUi.InlineCalendar(state: state) { date in
    ///     Circle()
    ///         .fill(hasEvent(date) ? Color.red : Color.clear)
    ///         .frame(width: 6, height: 6)
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - state: The calendar state managing selection, displayed month, and bounds.
    ///     Observe ``LemonadeInlineCalendarState/selectedDate`` via `onChange(of:)`.
    ///   - dayLabelFormat: Format for weekday labels above each cell.
    ///     Defaults to `.narrow` (single letter).
    ///   - expandSelectionToLabel: When `true` (default), the brand background covers
    ///     the full cell - weekday label, day number, and trailing content. When `false`,
    ///     only the day number circle is highlighted (DatePicker style).
    ///   - onMonthDisplayed: Optional callback invoked when the displayed month changes.
    ///     Receives a `DateComponents` with `.year` and `.month` set.
    ///   - enabledDates: Optional closure that determines which dates are tappable.
    ///     When provided, a date is enabled only when this closure returns `true`.
    ///     When `nil`, falls back to the default `minDate`/`maxDate` logic.
    ///   - selectionBackgroundColor: Optional color override for the selection background.
    ///     When `nil`, defaults to the brand interactive background color.
    ///   - selectionContentColor: Optional color override for text on selected cells.
    ///     When `nil`, defaults to `contentOnBrandHigh`.
    ///   - trailingContent: Optional view builder providing content below each day cell.
    ///     Receives the `Date` and a `Bool` indicating whether the date is selected.
    ///     Max height is 16pt (clipped).
    @ViewBuilder
    static func InlineCalendar<TrailingContent: View>(
        state: LemonadeInlineCalendarState,
        dayLabelFormat: DayLabelFormat = .narrow,
        expandSelectionToLabel: Bool = true,
        onMonthDisplayed: ((DateComponents) -> Void)? = nil,
        enabledDates: ((Date) -> Bool)? = nil,
        selectionBackgroundColor: Color? = nil,
        selectionContentColor: Color? = nil,
        @ViewBuilder trailingContent: @escaping (Date, Bool) -> TrailingContent
    ) -> some View {
        LemonadeInlineCalendarView(
            state: state,
            dayLabelFormat: dayLabelFormat,
            expandSelectionToLabel: expandSelectionToLabel,
            enabledDates: enabledDates,
            onMonthDisplayed: onMonthDisplayed,
            selectionBackgroundColor: selectionBackgroundColor,
            selectionContentColor: selectionContentColor,
            trailingContent: trailingContent
        )
    }

    /// An inline (horizontal-scrolling) calendar component from the Lemonade Design System.
    ///
    /// Convenience overload without trailing content.
    ///
    /// - Parameters:
    ///   - state: The calendar state managing selection, displayed month, and bounds.
    ///   - dayLabelFormat: Format for weekday labels above each cell.
    ///   - expandSelectionToLabel: When `true` (default), the brand background covers
    ///     the full cell - weekday label, day number, and trailing content. When `false`,
    ///     only the day number circle is highlighted (DatePicker style).
    ///   - onMonthDisplayed: Optional callback invoked when the displayed month changes.
    ///   - enabledDates: Optional closure that determines which dates are tappable.
    ///   - selectionBackgroundColor: Optional color override for the selection background.
    ///     When `nil`, defaults to the brand interactive background color.
    ///   - selectionContentColor: Optional color override for text on selected cells.
    ///     When `nil`, defaults to `contentOnBrandHigh`.
    @ViewBuilder
    static func InlineCalendar(
        state: LemonadeInlineCalendarState,
        dayLabelFormat: DayLabelFormat = .narrow,
        expandSelectionToLabel: Bool = true,
        onMonthDisplayed: ((DateComponents) -> Void)? = nil,
        enabledDates: ((Date) -> Bool)? = nil,
        selectionBackgroundColor: Color? = nil,
        selectionContentColor: Color? = nil
    ) -> some View {
        LemonadeInlineCalendarView(
            state: state,
            dayLabelFormat: dayLabelFormat,
            expandSelectionToLabel: expandSelectionToLabel,
            enabledDates: enabledDates,
            onMonthDisplayed: onMonthDisplayed,
            selectionBackgroundColor: selectionBackgroundColor,
            selectionContentColor: selectionContentColor,
            trailingContent: { _, _ in EmptyView() }
        )
    }
}

// MARK: - Constants

/// Virtual scroll range (~+/- 5 years). Odd so `centerIndex` has equal days on each side.
private let totalDays = 3651
private let centerIndex = totalDays / 2

/// Time before a programmatic scroll intent is abandoned. Must exceed the
/// `.spring(response: 0.35)` animation settle time (~350ms) plus gesture
/// recognition margin.
private let scrollIntentAbandonNanos: UInt64 = 600_000_000

/// Settle delay after the initial scroll jump, so the jump itself does not fire a haptic.
private let initialScrollSettleNanos: UInt64 = 50_000_000

/// Estimated height of the weekday label text (bodyXSmallOverline metrics).
private let calendarWeekdayLabelHeight: CGFloat = 16

/// Estimated height of the day number cell (bodyMediumMedium metrics + vertical padding).
private let calendarDayCellHeight: CGFloat = 36

// MARK: - Scroll Tracking Types

private struct ScrollContentOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

/// Programmatic scroll target. `id` is bumped per request so `onChange` fires
/// even when consecutive targets are equal.
private struct ProgrammaticScrollRequest: Equatable {
    let id: Int
    let index: Int
}

/// Maps a measured content offset to the virtual day index it corresponds to.
private struct OffsetCalibration {
    let offset: CGFloat
    let index: Int
}

private let scrollCoordinateSpaceName = "lemonadeInlineCalendarScroll"

// MARK: - Internal View

private struct LemonadeInlineCalendarView<TrailingContent: View>: View {
    @ObservedObject var state: LemonadeInlineCalendarState
    let dayLabelFormat: DayLabelFormat
    let expandSelectionToLabel: Bool
    let enabledDates: ((Date) -> Bool)?
    let onMonthDisplayed: ((DateComponents) -> Void)?
    let selectionBackgroundColor: Color?
    let selectionContentColor: Color?
    let trailingContent: (Date, Bool) -> TrailingContent

    @Environment(\.sizeCategory) private var sizeCategory
    @Environment(\.calendar) private var calendar

    @State private var visibleCenterIndex: Int = centerIndex
    @State private var observedScrollPositionIndex: Int?
    @State private var didInitialScroll = false
    @State private var offsetCalibration: OffsetCalibration?
    @State private var programmaticScrollTarget: ProgrammaticScrollRequest?
    @State private var lastObservedHeaderMonth: DateComponents?

    /// Set on a cross-month tap so the next header transition into that month
    /// suppresses its scroll-driven haptic (the tap already fired one).
    @State private var pendingSelectionMonthChange: DateComponents?

    /// Pinned at view init so the index space stays stable across midnight.
    /// Re-derived from the environment calendar in the initial scroll `.task`.
    @State private var anchorDate: Date = Date()

    /// `Calendar.monthSymbols` allocates a fresh array on every access; cache
    /// it and refresh via `onChange(of: calendar)` in `body`.
    @State private var cachedMonthSymbols: [String] = []

    /// Shared so the Taptic Engine stays warm; allocating per-haptic would
    /// re-pay the warmup cost on every cell tap and month crossing.
#if canImport(UIKit)
    @State private var selectionFeedbackGenerator = UISelectionFeedbackGenerator()
#endif

    private var visibleCellCount: CGFloat {
        sizeCategory.isAccessibilityCategory ? 5 : 7
    }

    private var weekdayLabels: [String] {
        CalendarUtils.weekdayLabels(format: dayLabelFormat, calendar: calendar)
    }

    // MARK: - Index-Date Mapping

    /// Clipped to the `minDate`/`maxDate` window with one viewport of disabled
    /// padding on each side.
    private var indexRange: Range<Int> {
        let padding = Int(visibleCellCount)
        var lower = 0
        var upper = totalDays
        if let minDate = state.minDate {
            lower = max(0, dateToIndex(minDate) - padding)
        }
        if let maxDate = state.maxDate {
            upper = min(totalDays, dateToIndex(maxDate) + padding + 1)
        }
        guard lower < upper else { return 0..<totalDays }
        return lower..<upper
    }

    private func clampedIndex(_ index: Int) -> Int {
        let range = indexRange
        return min(max(index, range.lowerBound), range.upperBound - 1)
    }

    private func indexToDate(_ index: Int) -> Date {
        calendar.date(byAdding: .day, value: index - centerIndex, to: anchorDate) ?? anchorDate
    }

    private func dateToIndex(_ date: Date) -> Int {
        let days = calendar.dateComponents(
            [.day],
            from: anchorDate,
            to: CalendarUtils.startOfDay(date, calendar: calendar)
        ).day ?? 0
        return centerIndex + days
    }

    // MARK: - Header Month

    private var headerMonth: DateComponents {
        let date = indexToDate(clampedIndex(visibleCenterIndex))
        return DateComponents(
            year: calendar.component(.year, from: date),
            month: calendar.component(.month, from: date)
        )
    }

    private var monthYearLabel: String {
        CalendarUtils.monthYearLabel(for: headerMonth) { monthNum in
            let symbols = cachedMonthSymbols.isEmpty ? calendar.monthSymbols : cachedMonthSymbols
            return symbols[monthNum - 1]
        }
    }

    // MARK: - Navigation Helpers

    /// Centers the viewport on the 1st of `headerMonth + delta`. When called
    /// while a previous request is still in flight, chains off that target so
    /// rapid clicks accumulate instead of restarting from the visible month.
    private func navigateMonth(delta: Int) {
        let baseMonth: DateComponents
        if let pendingTarget = programmaticScrollTarget {
            let pendingDate = indexToDate(pendingTarget.index)
            baseMonth = calendar.dateComponents([.year, .month], from: pendingDate)
        } else {
            baseMonth = headerMonth
        }

        guard let year = baseMonth.year,
              let month = baseMonth.month,
              let firstOfCurrentMonth = calendar.date(from: DateComponents(year: year, month: month, day: 1)),
              let firstOfTargetMonth = calendar.date(byAdding: .month, value: delta, to: firstOfCurrentMonth) else {
            return
        }

        let targetIndex = clampedIndex(dateToIndex(firstOfTargetMonth))
        let nextId = (programmaticScrollTarget?.id ?? 0) + 1
        programmaticScrollTarget = ProgrammaticScrollRequest(id: nextId, index: targetIndex)
    }

    // MARK: - Helpers

    private func isSameMonth(_ lhs: DateComponents, _ rhs: DateComponents) -> Bool {
        lhs.year == rhs.year && lhs.month == rhs.month
    }

    private func performCalendarHaptic() {
#if canImport(UIKit)
        selectionFeedbackGenerator.selectionChanged()
        selectionFeedbackGenerator.prepare()
#endif
    }

    // MARK: - Body

    var body: some View {
        VStack(spacing: 0) {
            CalendarMonthHeader(
                headerLabel: monthYearLabel,
                canGoPrev: state.canGoPrev(calendar: calendar),
                canGoNext: state.canGoNext(calendar: calendar),
                onPrev: { navigateMonth(delta: -1) },
                onNext: { navigateMonth(delta: 1) }
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)

            Spacer().frame(height: LemonadeTheme.spaces.spacing200)

            GeometryReader { geo in
                let viewportWidth = geo.size.width
                let cellWidth = viewportWidth / visibleCellCount

                if #available(iOS 17.0, macOS 14.0, *) {
                    ios17ScrollView(cellWidth: cellWidth, viewportWidth: viewportWidth)
                } else {
                    legacyScrollView(cellWidth: cellWidth, viewportWidth: viewportWidth)
                }
            }
            .frame(height: estimatedRowHeight)
        }
        .accessibilityElement(children: .contain)
        .onAppear {
            if lastObservedHeaderMonth == nil {
                lastObservedHeaderMonth = headerMonth
            }
            cachedMonthSymbols = calendar.monthSymbols
#if canImport(UIKit)
            selectionFeedbackGenerator.prepare()
#endif
        }
        .onChange(of: calendar) { newCalendar in
            cachedMonthSymbols = newCalendar.monthSymbols
        }
        .onChange(of: headerMonth) { newMonth in
            state.navigate(toMonth: newMonth, calendar: calendar)

            guard let previousMonth = lastObservedHeaderMonth else {
                lastObservedHeaderMonth = newMonth
                return
            }

            guard !isSameMonth(previousMonth, newMonth) else {
                lastObservedHeaderMonth = newMonth
                return
            }

            guard didInitialScroll else {
                pendingSelectionMonthChange = nil
                lastObservedHeaderMonth = newMonth
                return
            }

            onMonthDisplayed?(newMonth)

            let shouldSuppressMonthHaptic = pendingSelectionMonthChange
                .map { isSameMonth($0, newMonth) } ?? false
            pendingSelectionMonthChange = nil

            if !shouldSuppressMonthHaptic {
                performCalendarHaptic()
            }

            if let target = programmaticScrollTarget {
                let targetMonth = calendar.dateComponents([.year, .month], from: indexToDate(target.index))
                if isSameMonth(targetMonth, newMonth) {
                    programmaticScrollTarget = nil
                }
            }

            lastObservedHeaderMonth = newMonth
        }
        // A drag can pull away before the animation reaches the target, leaving the
        // intent stale.
        .task(id: programmaticScrollTarget?.id) {
            guard programmaticScrollTarget != nil else { return }
            do {
                try await Task.sleep(nanoseconds: scrollIntentAbandonNanos)
            } catch {
                return
            }
            programmaticScrollTarget = nil
        }
    }

    // MARK: - iOS 17+ Scroll View

    @available(iOS 17.0, macOS 14.0, *)
    @ViewBuilder
    private func ios17ScrollView(cellWidth: CGFloat, viewportWidth: CGFloat) -> some View {
        ScrollViewReader { proxy in
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack(alignment: .top, spacing: 0) {
                    ForEach(indexRange, id: \.self) { index in
                        dayCellView(for: index, cellWidth: cellWidth)
                            .id(index)
                    }
                }
                .scrollTargetLayout()
            }
            .scrollTargetBehavior(.viewAligned)
            .scrollPosition(id: $observedScrollPositionIndex, anchor: .center)
            .task {
                guard !didInitialScroll else { return }
                anchorDate = CalendarUtils.startOfDay(Date(), calendar: calendar)
                let initialIdx = clampedIndex(state.selectedDate.map { dateToIndex($0) } ?? centerIndex)
                visibleCenterIndex = initialIdx
                observedScrollPositionIndex = initialIdx
                offsetCalibration = nil
                proxy.scrollTo(initialIdx, anchor: .center)
                try? await Task.sleep(nanoseconds: initialScrollSettleNanos)
                didInitialScroll = true
            }
            .onChange(of: observedScrollPositionIndex) { newIndex in
                guard let newIndex else { return }
                let clamped = clampedIndex(newIndex)

                // Updating on every day costs scroll FPS during a free drag.
                let currentMonth = calendar.dateComponents([.year, .month], from: indexToDate(visibleCenterIndex))
                let candidateMonth = calendar.dateComponents([.year, .month], from: indexToDate(clamped))
                let isProgrammatic = programmaticScrollTarget != nil

                if isProgrammatic || !isSameMonth(currentMonth, candidateMonth) {
                    visibleCenterIndex = clamped
                }
            }
            .onChange(of: state.selectedDate) { newDate in
                guard let date = newDate else { return }
                let idx = clampedIndex(dateToIndex(date))
                offsetCalibration = nil
                visibleCenterIndex = idx
                withAnimation(.spring(response: 0.35, dampingFraction: 0.9)) {
                    proxy.scrollTo(idx, anchor: .center)
                }
            }
            .onChange(of: programmaticScrollTarget) { request in
                guard let request = request else { return }
                let idx = clampedIndex(request.index)
                offsetCalibration = nil
                visibleCenterIndex = idx
                withAnimation(.spring(response: 0.35, dampingFraction: 0.9)) {
                    proxy.scrollTo(idx, anchor: .center)
                }
            }
        }
    }

    /// Maps a measured content offset back to a virtual index for the iOS 15-16 path,
    /// which has no `.scrollPosition` to report the centered cell.
    private func updateVisibleCenterFromOffset(contentOffset: CGFloat, cellWidth: CGFloat) {
        guard cellWidth > 0 else { return }

        guard let calibration = offsetCalibration else {
            let anchorIndex = clampedIndex(programmaticScrollTarget?.index ?? visibleCenterIndex)
            if anchorIndex != visibleCenterIndex {
                visibleCenterIndex = anchorIndex
            }
            offsetCalibration = OffsetCalibration(offset: contentOffset, index: anchorIndex)
            return
        }

        let deltaInCells = (contentOffset - calibration.offset) / cellWidth
        let rawIndex = calibration.index + Int(deltaInCells.rounded())
        let clamped = clampedIndex(rawIndex)

        // ScrollView may rebase its content offset under lazy virtualization;
        // re-anchor when we detect a large discontinuity.
        if abs(clamped - visibleCenterIndex) > 180 {
            let fallbackIndex = programmaticScrollTarget?.index ?? clamped
            let anchoredIndex = clampedIndex(fallbackIndex)
            visibleCenterIndex = anchoredIndex
            offsetCalibration = OffsetCalibration(offset: contentOffset, index: anchoredIndex)
            return
        }

        if clamped != visibleCenterIndex {
            visibleCenterIndex = clamped
            offsetCalibration = OffsetCalibration(offset: contentOffset, index: clamped)
        }
    }

    // MARK: - iOS 15-16 Scroll View

    @ViewBuilder
    private func legacyScrollView(cellWidth: CGFloat, viewportWidth: CGFloat) -> some View {
        ScrollViewReader { proxy in
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack(alignment: .top, spacing: 0) {
                    ForEach(indexRange, id: \.self) { index in
                        dayCellView(for: index, cellWidth: cellWidth)
                            .id(index)
                    }
                }
                .background(
                    GeometryReader { stackGeo in
                        Color.clear.preference(
                            key: ScrollContentOffsetKey.self,
                            value: -stackGeo.frame(in: .named(scrollCoordinateSpaceName)).minX
                        )
                    }
                )
            }
            .coordinateSpace(name: scrollCoordinateSpaceName)
            .onPreferenceChange(ScrollContentOffsetKey.self) { contentOffset in
                updateVisibleCenterFromOffset(
                    contentOffset: contentOffset,
                    cellWidth: cellWidth
                )
            }
            .task {
                guard !didInitialScroll else { return }
                anchorDate = CalendarUtils.startOfDay(Date(), calendar: calendar)
                let initialIdx = clampedIndex(state.selectedDate.map { dateToIndex($0) } ?? centerIndex)
                visibleCenterIndex = initialIdx
                offsetCalibration = nil
                proxy.scrollTo(initialIdx, anchor: .center)
                try? await Task.sleep(nanoseconds: initialScrollSettleNanos)
                didInitialScroll = true
            }
            .onChange(of: state.selectedDate) { newDate in
                guard let date = newDate else { return }
                let idx = clampedIndex(dateToIndex(date))
                offsetCalibration = nil
                visibleCenterIndex = idx
                withAnimation(.spring(response: 0.35, dampingFraction: 0.9)) {
                    proxy.scrollTo(idx, anchor: .center)
                }
            }
            .onChange(of: programmaticScrollTarget) { request in
                guard let request = request else { return }
                let idx = clampedIndex(request.index)
                offsetCalibration = nil
                visibleCenterIndex = idx
                withAnimation(.spring(response: 0.35, dampingFraction: 0.9)) {
                    proxy.scrollTo(idx, anchor: .center)
                }
            }
        }
    }

    // MARK: - Day Cell

    /// `date`, `state.minDate/maxDate`, and `state.selectedDate` are all
    /// pre-normalized to start-of-day, so per-frame `startOfDay` calls are
    /// not needed.
    @ViewBuilder
    private func dayCellView(for index: Int, cellWidth: CGFloat) -> some View {
        let date = indexToDate(index)
        let yearMonth = calendar.dateComponents([.year, .month], from: date)
        let dayNumber = calendar.component(.day, from: date)
        let weekdayIndex = calendar.component(.weekday, from: date)
        let labelIndex = (weekdayIndex - calendar.firstWeekday + 7) % 7
        let weekdayText = weekdayLabels[labelIndex]

        let outsideMonth = !isSameMonth(yearMonth, headerMonth)

        let beforeMin = state.minDate.map { date < $0 } ?? false
        let afterMax = state.maxDate.map { date > $0 } ?? false
        let enabledByPredicate = enabledDates?(date) ?? true
        let isEnabled = !beforeMin && !afterMax && enabledByPredicate

        let isSelectedCell = state.selectedDate.map { $0 == date } ?? false
        let isToday = calendar.isDateInToday(date)

        CalendarDayCell(
            date: date,
            text: "\(dayNumber)",
            isCurrent: isToday,
            isSelected: isSelectedCell,
            isEnabled: isEnabled,
            isOutsideVisibleRange: outsideMonth,
            showWeekdayLabel: true,
            weekdayLabel: weekdayText,
            expandSelectionToLabel: expandSelectionToLabel,
            selectionBackgroundColor: selectionBackgroundColor,
            selectionContentColor: selectionContentColor,
            onClick: {
                performCalendarHaptic()
                pendingSelectionMonthChange = isSameMonth(yearMonth, headerMonth)
                    ? nil
                    : yearMonth
                state.select(date: date, calendar: calendar)
            }
        ) {
            trailingContent(date, isSelectedCell)
        }
        .frame(width: cellWidth)
    }

    // MARK: - Layout Metrics

    private var estimatedRowHeight: CGFloat {
        let weekdayHeight = calendarWeekdayLabelHeight
        let dayHeight = calendarDayCellHeight
        let trailing: CGFloat = LemonadeTheme.spaces.spacing400
        let spacing: CGFloat = LemonadeTheme.spaces.spacing100
        let selectionPadding: CGFloat = LemonadeTheme.spaces.spacing100 * 2
        return weekdayHeight + spacing + dayHeight + trailing + selectionPadding
    }
}

// MARK: - Previews

#if DEBUG
private struct InlineCalendarDefaultPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    var body: some View {
        LemonadeUi.InlineCalendar(state: state)
    }
}

private struct InlineCalendarWithDotsPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    private let eventDays: Set<Int> = [3, 7, 12, 18, 25]

    var body: some View {
        LemonadeUi.InlineCalendar(state: state) { date, isSelected in
            let day = Calendar.current.component(.day, from: date)
            if eventDays.contains(day) {
                Circle()
                    .fill(isSelected
                        ? LemonadeTheme.colors.content.contentOnBrandHigh
                        : LemonadeTheme.colors.content.contentBrand)
                    .frame(width: 6, height: 6)
            }
        }
    }
}

private struct InlineCalendarShortLabelsPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    var body: some View {
        LemonadeUi.InlineCalendar(
            state: state,
            dayLabelFormat: .short
        )
    }
}

private struct InlineCalendarConstrainedPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState(
        minDate: Calendar.current.date(byAdding: .month, value: -2, to: Date()),
        maxDate: Calendar.current.date(byAdding: .month, value: 3, to: Date())
    )

    var body: some View {
        LemonadeUi.InlineCalendar(state: state)
    }
}

private struct InlineCalendarCompactSelectionPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    var body: some View {
        LemonadeUi.InlineCalendar(
            state: state,
            expandSelectionToLabel: false
        )
    }
}

private struct InlineCalendarCompactWithDotsPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    private let eventDays: Set<Int> = [3, 6, 9, 12, 15, 18, 21, 24, 27, 30]

    var body: some View {
        LemonadeUi.InlineCalendar(
            state: state,
            expandSelectionToLabel: false
        ) { date, isSelected in
            let day = Calendar.current.component(.day, from: date)
            if eventDays.contains(day) {
                Circle()
                    .fill(isSelected
                        ? LemonadeTheme.colors.content.contentOnBrandHigh
                        : LemonadeTheme.colors.content.contentBrand)
                    .frame(width: 6, height: 6)
            }
        }
    }
}

private struct InlineCalendarCustomColorsPreview: View {
    @StateObject private var state = LemonadeInlineCalendarState()

    var body: some View {
        LemonadeUi.InlineCalendar(
            state: state,
            selectionBackgroundColor: LemonadeTheme.colors.background.bgPositive,
            selectionContentColor: LemonadeTheme.colors.content.contentAlwaysLight
        )
    }
}

struct LemonadeInlineCalendar_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: 32) {
                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Default",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarDefaultPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "With trailing dots",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarWithDotsPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Short labels",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarShortLabelsPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Constrained date range",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarConstrainedPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Compact selection",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarCompactSelectionPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Compact with dots",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarCompactWithDotsPreview()
                }

                VStack(alignment: .leading) {
                    LemonadeUi.Text(
                        "Custom colors",
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold
                    )
                    InlineCalendarCustomColorsPreview()
                }
            }
            .padding()
        }
        .previewLayout(.sizeThatFits)
    }
}
#endif
