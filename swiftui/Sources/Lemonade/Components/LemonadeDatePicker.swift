import SwiftUI

// MARK: - State Classes

/// State holder for ``LemonadeUi/DatePicker(state:monthFormatter:weekdayAbbreviations:)``.
///
/// Holds the currently selected date as observable state, plus the selectable date range.
/// Observe ``selectedDate`` to react to user selections.

public struct LemonadeDatePickerState {
    public internal(set) var selectedDate: Date?
    public let minDate: Date?
    public let maxDate: Date?
    /// Days rendered as disabled (greyed out and non-tappable) in addition to any
    /// ``minDate`` / ``maxDate`` bounds. Assign a new set — typically from the
    /// ``LemonadeUi/DatePicker(state:monthFormatter:weekdayAbbreviations:onMonthDisplayed:)``
    /// `onMonthDisplayed` callback — when the disabled list needs to come from an API keyed
    /// on the visible month. Compared using calendar-day granularity (time components ignored).
    ///
    /// > Note: `onMonthDisplayed` does not fire for the initially displayed month. Seed the
    /// > first month synchronously via ``init(initialDate:minDate:maxDate:initialDisabledDates:)``
    /// > if the disabled set is already known, or trigger the first fetch yourself in an
    /// > `.onAppear` / `.task` on your view.
    public var disabledDates: Set<Date>

    public init(
        initialDate: Date? = nil,
        minDate: Date? = nil,
        maxDate: Date? = nil,
        initialDisabledDates: Set<Date> = []
    ) {
        self.selectedDate = initialDate
        self.minDate = minDate
        self.maxDate = maxDate
        self.disabledDates = initialDisabledDates
    }
}

/// State holder for ``LemonadeUi/DateRangePicker(state:monthFormatter:weekdayAbbreviations:)``.
///
/// Holds the currently selected start/end dates as observable state, plus the selectable
/// date range and maximum range length. Observe ``selectedStartDate`` and ``selectedEndDate``
/// to react to user selections.
public struct LemonadeDateRangePickerState {
    public internal(set) var selectedStartDate: Date?
    public internal(set) var selectedEndDate: Date?
    public let minDate: Date?
    public let maxDate: Date?
    public let maxRangeDays: Int?
    /// Days rendered as disabled (greyed out and non-tappable) in addition to any
    /// ``minDate`` / ``maxDate`` bounds. A disabled day inside a completed range is greyed
    /// but doesn't break the range — the user just can't pick it as start or end.
    /// Compared using calendar-day granularity.
    public var disabledDates: Set<Date>

    public init(
        initialStartDate: Date? = nil,
        initialEndDate: Date? = nil,
        minDate: Date? = nil,
        maxDate: Date? = nil,
        maxRangeDays: Int? = nil,
        initialDisabledDates: Set<Date> = []
    ) {
        self.selectedStartDate = initialStartDate
        self.selectedEndDate = initialEndDate
        self.minDate = minDate
        self.maxDate = maxDate
        self.maxRangeDays = maxRangeDays
        self.disabledDates = initialDisabledDates
    }
}

// MARK: - Public API

public extension LemonadeUi {
    /// A single-date picker component from the Lemonade Design System.
    ///
    /// Provides a swipeable month view that allows users to select a single date.
    ///
    /// ## Usage
    /// ```swift
    /// @StateObject var state = LemonadeDatePickerState()
    /// LemonadeUi.DatePicker(
    ///     state: state,
    ///     monthFormatter: { month in DateFormatter().monthSymbols[month - 1] },
    ///     weekdayAbbreviations: ["S", "M", "T", "W", "T", "F", "S"]
    /// )
    /// // Observe: state.selectedDate
    /// ```
    ///
    /// - Parameters:
    ///   - state: Configuration state holding the selected date, min/max bounds.
    ///     Observe ``LemonadeDatePickerState/selectedDate`` to react to user selections.
    ///   - monthFormatter: Formatter that returns the month name for a given month number (1-12).
    ///   - weekdayAbbreviations: Exactly 7 items representing Sunday through Saturday. The header
    ///     renders them starting from the current calendar's first weekday, so the labels stay
    ///     aligned with the day grid on every device region.
    ///   - today: The date treated as "today" — drives both the "today" indicator and the pager's
    ///     initial month. Defaults to `Date()`. Callers can pass a different value (e.g. the
    ///     currently-selected day) to have the pager open on that day's month; the day will also
    ///     get the "today" dot, which is usually visually swamped by the selected-day highlight.
    ///     Time components are ignored — the picker normalises to start-of-day internally.
    ///   - onMonthDisplayed: Optional callback invoked when the displayed month changes.
    ///     Receives a `DateComponents` with `.year` and `.month` set.
    @ViewBuilder
    static func DatePicker(
        state: Binding<LemonadeDatePickerState>,
        monthFormatter: @escaping (Int) -> String,
        weekdayAbbreviations: [String],
        today: Date = Date(),
        onMonthDisplayed: ((DateComponents) -> Void)? = nil
    ) -> some View {
        LemonadeDatePickerView(
            state: state,
            monthFormatter: monthFormatter,
            weekdayAbbreviations: weekdayAbbreviations,
            today: today,
            onMonthDisplayed: onMonthDisplayed
        )
    }
    
    /// A date range picker component from the Lemonade Design System.
    ///
    /// Provides a swipeable month view that allows users to select a start and end date.
    /// The user first taps to select a start date, then taps again to select an end date.
    /// If the second tap is before the start date, the dates are swapped automatically.
    ///
    /// ## Usage
    /// ```swift
    /// @StateObject var state = LemonadeDateRangePickerState()
    /// LemonadeUi.DateRangePicker(
    ///     state: state,
    ///     monthFormatter: { month in DateFormatter().monthSymbols[month - 1] },
    ///     weekdayAbbreviations: ["S", "M", "T", "W", "T", "F", "S"]
    /// )
    /// // Observe: state.selectedStartDate, state.selectedEndDate
    /// ```
    ///
    /// - Parameters:
    ///   - state: Configuration state holding the selected dates, min/max bounds, and max range.
    ///     Observe ``LemonadeDateRangePickerState/selectedStartDate`` and
    ///     ``LemonadeDateRangePickerState/selectedEndDate`` to react to user selections.
    ///   - monthFormatter: Formatter that returns the month name for a given month number (1-12).
    ///   - weekdayAbbreviations: Exactly 7 items representing Sunday through Saturday. The header
    ///     renders them starting from the current calendar's first weekday, so the labels stay
    ///     aligned with the day grid on every device region.
    ///   - today: The date treated as "today" — drives both the "today" indicator and the pager's
    ///     initial month. Defaults to `Date()`. See ``DatePicker(state:monthFormatter:weekdayAbbreviations:today:onMonthDisplayed:)``
    ///     for the intended usage.
    ///   - onMonthDisplayed: Optional callback invoked when the displayed month changes.
    ///     Receives a `DateComponents` with `.year` and `.month` set.
    @ViewBuilder
    static func DateRangePicker(
        state: Binding<LemonadeDateRangePickerState>,
        monthFormatter: @escaping (Int) -> String,
        weekdayAbbreviations: [String],
        today: Date = Date(),
        onMonthDisplayed: ((DateComponents) -> Void)? = nil
    ) -> some View {
        LemonadeDateRangePickerView(
            state: state,
            monthFormatter: monthFormatter,
            weekdayAbbreviations: weekdayAbbreviations,
            today: today,
            onMonthDisplayed: onMonthDisplayed
        )
    }
}

// MARK: - Single Date Picker View

private struct LemonadeDatePickerView: View {
    @Binding var state: LemonadeDatePickerState
    let monthFormatter: (Int) -> String
    let weekdayAbbreviations: [String]
    let today: Date
    let onMonthDisplayed: ((DateComponents) -> Void)?

    var body: some View {
        CoreDatePickerView(
            monthFormatter: monthFormatter,
            weekdayAbbreviations: weekdayAbbreviations,
            selectedDates: state.selectedDate.map { Set([$0]) } ?? [],
            onDateSelected: { date in
                state.selectedDate = date
            },
            minDate: state.minDate,
            maxDate: state.maxDate,
            disabledDates: state.disabledDates,
            today: today,
            onMonthDisplayed: onMonthDisplayed
        )
    }
}

// MARK: - Date Range Picker View

private struct LemonadeDateRangePickerView: View {
    @Binding var state: LemonadeDateRangePickerState
    let monthFormatter: (Int) -> String
    let weekdayAbbreviations: [String]
    let today: Date
    let onMonthDisplayed: ((DateComponents) -> Void)?
    
    private var isSelectingEndDate: Bool {
        state.selectedStartDate != nil && state.selectedEndDate == nil
    }
    
    private var effectiveMin: Date? {
        var min = state.minDate
        if isSelectingEndDate, let maxDays = state.maxRangeDays, let start = state.selectedStartDate,
           let rangeMin = Calendar.current.date(byAdding: .day, value: -maxDays, to: start) {
            if min == nil || rangeMin > min! {
                min = rangeMin
            }
        }
        return min
    }
    
    private var effectiveMax: Date? {
        var max = state.maxDate
        if isSelectingEndDate, let maxDays = state.maxRangeDays, let start = state.selectedStartDate,
           let rangeMax = Calendar.current.date(byAdding: .day, value: maxDays, to: start) {
            if max == nil || rangeMax < max! {
                max = rangeMax
            }
        }
        return max
    }
    
    var body: some View {
        CoreDatePickerView(
            monthFormatter: monthFormatter,
            weekdayAbbreviations: weekdayAbbreviations,
            selectedDates: Set([state.selectedStartDate, state.selectedEndDate].compactMap { $0 }),
            onDateSelected: { date in
                guard isSelectingEndDate, let start = state.selectedStartDate else {
                    state.selectedStartDate = date
                    state.selectedEndDate = nil
                    return
                }

                let newStart = min(date, start)
                let newEnd = max(date, start)
                state.selectedStartDate = newStart
                state.selectedEndDate = newEnd
            },
            minDate: effectiveMin,
            maxDate: effectiveMax,
            disabledDates: state.disabledDates,
            today: today,
            onMonthDisplayed: onMonthDisplayed
        )
    }
}

// MARK: - Core Date Picker View

private struct CoreDatePickerView: View {
    let monthFormatter: (Int) -> String
    let weekdayAbbreviations: [String]
    let selectedDates: Set<Date>
    let onDateSelected: (Date) -> Void
    let minDate: Date?
    let maxDate: Date?
    let disabledDates: Set<Date>
    /// Caller-provided "today". Drives the "today" indicator and the pager's initial month.
    /// Compared as start-of-day via ``normalizedToday``.
    let today: Date
    let onMonthDisplayed: ((DateComponents) -> Void)?

    /// Disabled dates normalised to start-of-day so callers can pass anything without worrying
    /// about time components — matches how the min/max checks below use `startOfDay`.
    private var normalizedDisabledDates: Set<Date> {
        Set(disabledDates.map { CalendarUtils.startOfDay($0) })
    }

    private let totalPages = 240
    private var centerPage: Int { totalPages / 2 }

    @State private var displayedMonthOffset: Int = 0

    private let calendar = Calendar.current
    /// Start-of-day snapshot of the caller-provided `today`. Anything comparing against a
    /// specific day (the "today" dot, prev/next-month reachability) uses this, not the raw
    /// `today` — a `Date` with a wall-clock time wouldn't match a start-of-day cell date.
    private var normalizedToday: Date { CalendarUtils.startOfDay(today) }
    
    private var currentYearMonth: DateComponents {
        guard let target = calendar.date(byAdding: .month, value: displayedMonthOffset, to: today) else {
            return calendar.dateComponents([.year, .month], from: today)
        }
        return calendar.dateComponents([.year, .month], from: target)
    }
    
    private var headerLabel: String {
        CalendarUtils.monthYearLabel(for: currentYearMonth, monthFormatter: monthFormatter)
    }
    
    private var canGoPrev: Bool {
        guard displayedMonthOffset > -centerPage else { return false }
        guard let min = minDate else { return true }
        guard let prevMonth = calendar.date(byAdding: .month, value: displayedMonthOffset - 1, to: today) else {
            return false
        }
        let prevYM = calendar.dateComponents([.year, .month], from: prevMonth)
        guard let prevYear = prevYM.year, let prevMonth = prevYM.month,
              let lastDayOfPrev = CalendarUtils.lastDayOfMonth(year: prevYear, month: prevMonth, calendar: calendar) else {
            return false
        }
        return min <= lastDayOfPrev
    }
    
    private var canGoNext: Bool {
        guard displayedMonthOffset < centerPage - 1 else { return false }
        guard let max = maxDate else { return true }
        guard let nextMonth = calendar.date(byAdding: .month, value: displayedMonthOffset + 1, to: today) else {
            return false
        }
        let nextYM = calendar.dateComponents([.year, .month], from: nextMonth)
        guard let nextYear = nextYM.year, let nextMonth = nextYM.month,
              let firstDayOfNext = calendar.date(from: DateComponents(year: nextYear, month: nextMonth, day: 1)) else {
            return false
        }
        return max >= firstDayOfNext
    }
    
    var body: some View {
        VStack(spacing: 0) {
            CalendarMonthHeader(
                headerLabel: headerLabel,
                canGoPrev: canGoPrev,
                canGoNext: canGoNext,
                onPrev: { displayedMonthOffset = max(displayedMonthOffset - 1, -centerPage) },
                onNext: { displayedMonthOffset = min(displayedMonthOffset + 1, centerPage - 1) }
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            
            Spacer().frame(height: LemonadeTheme.spaces.spacing200)
            
            HStack(spacing: 0) {
                ForEach(
                    Array(CalendarUtils.orderedWeekdayAbbreviations(weekdayAbbreviations, calendar: calendar).enumerated()),
                    id: \.offset
                ) { _, day in
                    LemonadeUi.Text(
                        day,
                        textStyle: LemonadeTypography.shared.bodyXSmallOverline,
                        color: LemonadeTheme.colors.content.contentPrimary
                    )
                    .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            
            Spacer().frame(height: LemonadeTheme.spaces.spacing100)
            
            #if os(iOS)
            TabView(selection: $displayedMonthOffset) {
                ForEach(-centerPage..<centerPage, id: \.self) { offset in
                    MonthGridView(
                        monthOffset: offset,
                        baseDate: today,
                        today: normalizedToday,
                        selectedDates: selectedDates,
                        minDate: minDate,
                        maxDate: maxDate,
                        disabledDates: normalizedDisabledDates,
                        onDateSelected: onDateSelected
                    )
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
                    .tag(offset)
                }
            }
            #if os(iOS)
            // `.page` (PageTabViewStyle) is unavailable on macOS; fall back to the
            // default TabView style there so the package builds for its declared
            // macOS deployment target.
            .tabViewStyle(.page(indexDisplayMode: .never))
            #endif
            .frame(height: 300)
            #else
            MonthGridView(
                monthOffset: displayedMonthOffset,
                baseDate: today,
                today: normalizedToday,
                selectedDates: selectedDates,
                minDate: minDate,
                maxDate: maxDate,
                disabledDates: normalizedDisabledDates,
                onDateSelected: onDateSelected
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            .frame(height: 300)
            #endif
        }
        .onChange(of: displayedMonthOffset) { _ in
            onMonthDisplayed?(currentYearMonth)
        }
    }
}

// MARK: - Month Grid View

private struct MonthGridView: View {
    let monthOffset: Int
    let baseDate: Date
    /// Start-of-day snapshot of "today". Compared against each cell's start-of-day to place the
    /// "today" dot on the correct cell. Passed in by the parent so a caller-provided `today`
    /// override propagates all the way down to the individual day cell.
    let today: Date
    let selectedDates: Set<Date>
    let minDate: Date?
    let maxDate: Date?
    let disabledDates: Set<Date>
    let onDateSelected: (Date) -> Void

    private let calendar = Calendar.current
    
    private var yearMonth: DateComponents {
        guard let target = calendar.date(byAdding: .month, value: monthOffset, to: baseDate) else {
            return calendar.dateComponents([.year, .month], from: baseDate)
        }
        return calendar.dateComponents([.year, .month], from: target)
    }
    
    private var days: [Date] {
        guard let year = yearMonth.year, let month = yearMonth.month else { return [] }
        return CalendarUtils.generateMonthDays(year: year, month: month, calendar: calendar)
    }
    
    private var normalizedSelectedDates: Set<Date> {
        Set(selectedDates.map { CalendarUtils.startOfDay($0) })
    }
    
    var body: some View {
        let allDays = days
        let isRangeComplete = normalizedSelectedDates.count >= 2
        let rangeStart = isRangeComplete ? normalizedSelectedDates.min() : nil
        let rangeEnd = isRangeComplete ? normalizedSelectedDates.max() : nil
        
        VStack(spacing: LemonadeTheme.spaces.spacing100) {
            ForEach(0..<6, id: \.self) { weekIndex in
                let weekStart = weekIndex * 7
                let weekDays = Array(allDays[weekStart..<min(weekStart + 7, allDays.count)])
                
                HStack(spacing: 0) {
                    ForEach(Array(weekDays.enumerated()), id: \.offset) { _, current in
                        let normalized = CalendarUtils.startOfDay(current)
                        let isInRange = isRangeComplete &&
                        rangeStart.map { normalized >= $0 } ?? false &&
                        rangeEnd.map { normalized <= $0 } ?? false
                        
                        let currentMonth = calendar.component(.month, from: current)
                        let isOutsideMonth = currentMonth != yearMonth.month
                        
                        let isBeforeMin = minDate.map { normalized < CalendarUtils.startOfDay($0) } ?? false
                        let isAfterMax = maxDate.map { normalized > CalendarUtils.startOfDay($0) } ?? false
                        let isExplicitlyDisabled = disabledDates.contains(normalized)

                        CalendarDayCell(
                            date: current,
                            text: "\(calendar.component(.day, from: current))",
                            isCurrent: normalized == today,
                            isSelected: normalizedSelectedDates.contains(normalized),
                            isEnabled: !isBeforeMin && !isAfterMax && !isExplicitlyDisabled,
                            isOutsideVisibleRange: isOutsideMonth,
                            isInsideSelectedRange: isInRange,
                            showWeekdayLabel: false,
                            showTodayIndicator: true,
                            onClick: { onDateSelected(normalized) }
                        )
                        .padding(.horizontal, LemonadeTheme.spaces.spacing200)
                    }
                }
                .background(
                    GeometryReader { geo in
                        if let rangeStart = rangeStart, let rangeEnd = rangeEnd {
                            let cellWidth = geo.size.width / 7
                            let padding = LemonadeTheme.spaces.spacing200
                            let radius = LemonadeTheme.radius.radius200
                            
                            let startIdx = weekDays.firstIndex { CalendarUtils.startOfDay($0) >= rangeStart }
                            let endIdx = weekDays.lastIndex { CalendarUtils.startOfDay($0) <= rangeEnd }
                            
                            if let startIdx = startIdx, let endIdx = endIdx {
                                let left = cellWidth * CGFloat(startIdx) + padding
                                let right = cellWidth * CGFloat(endIdx + 1) - padding
                                
                                RoundedRectangle(cornerRadius: radius)
                                    .fill(LemonadeTheme.colors.background.bgBrandSubtle)
                                    .frame(width: right - left, height: geo.size.height)
                                    .offset(x: left)
                            }
                        }
                    }
                )
            }
        }
        .frame(maxHeight: .infinity, alignment: .top)
    }
}

// MARK: - Previews

#if DEBUG
private struct DatePickerPreview: View {
    @State private var state = LemonadeDatePickerState()
    
    var body: some View {
        LemonadeUi.DatePicker(
            state: $state,
            monthFormatter: { month in DateFormatter().monthSymbols[month - 1] },
            weekdayAbbreviations: ["S", "M", "T", "W", "T", "F", "S"]
        )
    }
}

private struct DateRangePickerPreview: View {
    @State private var state = LemonadeDateRangePickerState()
    
    var body: some View {
        LemonadeUi.DateRangePicker(
            state: $state,
            monthFormatter: { month in DateFormatter().monthSymbols[month - 1] },
            weekdayAbbreviations: ["S", "M", "T", "W", "T", "F", "S"]
        )
    }
}

struct LemonadeDatePicker_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: 32) {
                DatePickerPreview()
                DateRangePickerPreview()
            }
            .padding()
        }
        .previewLayout(.sizeThatFits)
    }
}
#endif
