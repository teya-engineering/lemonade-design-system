import Foundation

// MARK: - Calendar Utilities

/// Shared date utilities used by calendar components.
///
/// Contains helpers for date normalization, month grid generation,
/// and label formatting used by both ``LemonadeUi/DatePicker`` and
/// ``LemonadeUi/InlineCalendar``.
enum CalendarUtils {

    // MARK: - Date Normalization

    /// Returns the start of day for the given date, stripping time components.
    static func startOfDay(_ date: Date, calendar: Calendar = .current) -> Date {
        calendar.startOfDay(for: date)
    }

    // MARK: - Month Boundaries

    /// Returns the last day of the given month as a `Date`, or `nil` if the date cannot be constructed.
    static func lastDayOfMonth(year: Int, month: Int, calendar: Calendar) -> Date? {
        var components = DateComponents(year: year, month: month)
        guard let monthDate = calendar.date(from: components),
              let range = calendar.range(of: .day, in: .month, for: monthDate) else { return nil }
        components.day = range.upperBound - 1
        return calendar.date(from: components)
    }

    // MARK: - Month Grid Generation

    /// Generate 42 dates (6 weeks) for a given month, with leading/trailing days.
    ///
    /// The grid always starts on the calendar's `firstWeekday` and fills
    /// exactly 6 rows of 7 columns, padding with days from adjacent months.
    static func generateMonthDays(year: Int, month: Int, calendar: Calendar) -> [Date] {
        guard let firstOfMonth = calendar.date(from: DateComponents(year: year, month: month, day: 1)),
              let leadingPadding = previousMonthPadding(before: firstOfMonth, calendar: calendar),
              let monthDays = daysInMonth(year: year, month: month, firstOfMonth: firstOfMonth, calendar: calendar)
        else { return [] }

        let days = leadingPadding + monthDays
        guard let trailingPadding = nextMonthPadding(
            after: firstOfMonth,
            count: 42 - days.count,
            calendar: calendar
        ) else { return [] }

        return days + trailingPadding
    }

    private static func previousMonthPadding(before firstOfMonth: Date, calendar: Calendar) -> [Date]? {
        let weekday = calendar.component(.weekday, from: firstOfMonth)
        let firstDayOffset = (weekday - calendar.firstWeekday + 7) % 7
        var days: [Date] = []
        for i in (0..<firstDayOffset).reversed() {
            guard let date = calendar.date(byAdding: .day, value: -(i + 1), to: firstOfMonth) else { return nil }
            days.append(date)
        }
        return days
    }

    private static func daysInMonth(
        year: Int,
        month: Int,
        firstOfMonth: Date,
        calendar: Calendar
    ) -> [Date]? {
        guard let range = calendar.range(of: .day, in: .month, for: firstOfMonth) else { return nil }
        var days: [Date] = []
        for day in range {
            guard let date = calendar.date(from: DateComponents(year: year, month: month, day: day)) else { return nil }
            days.append(date)
        }
        return days
    }

    private static func nextMonthPadding(after firstOfMonth: Date, count: Int, calendar: Calendar) -> [Date]? {
        guard let nextMonthStart = calendar.date(byAdding: .month, value: 1, to: firstOfMonth) else { return nil }
        guard count > 0 else { return [] }
        var days: [Date] = []
        for offset in 0..<count {
            guard let date = calendar.date(byAdding: .day, value: offset, to: nextMonthStart) else { return nil }
            days.append(date)
        }
        return days
    }

    // MARK: - Inline Calendar Helpers

    /// Returns only the days that belong to the given month (no padding from adjacent months).
    ///
    /// - Parameters:
    ///   - year: The year of the month.
    ///   - month: The month number (1-12).
    ///   - calendar: The calendar to use for date calculations.
    /// - Returns: An array of `Date` values for each day in the month.
    static func daysForMonth(year: Int, month: Int, calendar: Calendar) -> [Date] {
        guard let firstOfMonth = calendar.date(from: DateComponents(year: year, month: month, day: 1)),
              let range = calendar.range(of: .day, in: .month, for: firstOfMonth) else { return [] }
        return range.compactMap { day in
            calendar.date(from: DateComponents(year: year, month: month, day: day))
        }
    }

    /// Returns up to `count` days from the end of the previous month.
    ///
    /// Used to show "peek" days before the current month in an inline calendar.
    ///
    /// - Parameters:
    ///   - year: The year of the current month.
    ///   - month: The current month number (1-12).
    ///   - count: Maximum number of days to return.
    ///   - calendar: The calendar to use for date calculations.
    /// - Returns: An array of `Date` values from the previous month, ordered chronologically.
    static func peekDaysBefore(year: Int, month: Int, count: Int, calendar: Calendar) -> [Date] {
        guard let firstOfMonth = calendar.date(from: DateComponents(year: year, month: month, day: 1)),
              let prevMonthEnd = calendar.date(byAdding: .day, value: -1, to: firstOfMonth) else {
            return []
        }
        let prevYM = calendar.dateComponents([.year, .month], from: prevMonthEnd)
        guard let prevYear = prevYM.year, let prevMonth = prevYM.month else { return [] }
        let prevRange = calendar.range(of: .day, in: .month, for: prevMonthEnd)
        let prevDayCount = prevRange?.count ?? 0
        let startDay = max(1, prevDayCount - count + 1)
        var result: [Date] = []
        for day in startDay...prevDayCount {
            if let date = calendar.date(from: DateComponents(year: prevYear, month: prevMonth, day: day)) {
                result.append(date)
            }
        }
        return result
    }

    /// Returns up to `count` days from the beginning of the next month.
    ///
    /// Used to show "peek" days after the current month in an inline calendar.
    ///
    /// - Parameters:
    ///   - year: The year of the current month.
    ///   - month: The current month number (1-12).
    ///   - count: Maximum number of days to return.
    ///   - calendar: The calendar to use for date calculations.
    /// - Returns: An array of `Date` values from the next month, ordered chronologically.
    static func peekDaysAfter(year: Int, month: Int, count: Int, calendar: Calendar) -> [Date] {
        guard let firstOfMonth = calendar.date(from: DateComponents(year: year, month: month, day: 1)),
              let nextMonth = calendar.date(byAdding: .month, value: 1, to: firstOfMonth) else { return [] }
        let nextYM = calendar.dateComponents([.year, .month], from: nextMonth)
        guard let nextYear = nextYM.year, let nextMonth = nextYM.month else { return [] }
        let nextDays = daysForMonth(year: nextYear, month: nextMonth, calendar: calendar)
        return Array(nextDays.prefix(count))
    }

    /// Returns a formatted "Month Year" label for the given date components.
    ///
    /// - Parameters:
    ///   - yearMonth: Date components containing at least `.year` and `.month`.
    ///   - monthFormatter: A closure that converts a month number (1-12) to a display string.
    /// - Returns: A string in the format "Month Year" (e.g. "April 2026").
    static func monthYearLabel(
        for yearMonth: DateComponents,
        monthFormatter: (Int) -> String
    ) -> String {
        guard let month = yearMonth.month, let year = yearMonth.year else { return "" }
        return "\(monthFormatter(month)) \(year)"
    }

    /// Rotates 7 Sunday-first weekday labels so index 0 is the calendar's `firstWeekday`.
    ///
    /// A month grid from ``generateMonthDays(year:month:calendar:)`` places its first column
    /// on `calendar.firstWeekday`, so a header row must be rotated with this before rendering
    /// or its labels drift from the day cells on any non-Sunday-first calendar.
    ///
    /// - Parameters:
    ///   - abbreviations: Exactly 7 labels ordered Sunday through Saturday. Anything past the
    ///     first 7 is dropped; fewer than 7 are returned unchanged.
    ///   - calendar: The calendar whose `firstWeekday` decides the rotation.
    /// - Returns: The labels reordered to start at the calendar's first weekday.
    static func orderedWeekdayAbbreviations(_ abbreviations: [String], calendar: Calendar) -> [String] {
        let labels = Array(abbreviations.prefix(7))
        guard labels.count == 7 else { return labels }
        let first = calendar.firstWeekday - 1
        return (0..<7).map { labels[(first + $0) % 7] }
    }

    /// Returns an array of 7 weekday labels starting from the calendar's first weekday.
    ///
    /// - Parameters:
    ///   - format: The label format (`.narrow` for single-letter, `.short` for abbreviated).
    ///   - calendar: The calendar to use for determining first weekday and symbols.
    /// - Returns: An array of 7 weekday label strings.
    static func weekdayLabels(format: DayLabelFormat, calendar: Calendar = .current) -> [String] {
        let symbols: [String]
        switch format {
        case .narrow:
            symbols = calendar.veryShortWeekdaySymbols
        case .short:
            symbols = calendar.shortWeekdaySymbols
        }
        return orderedWeekdayAbbreviations(symbols, calendar: calendar)
    }
}
