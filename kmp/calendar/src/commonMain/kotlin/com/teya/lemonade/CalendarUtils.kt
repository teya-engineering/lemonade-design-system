package com.teya.lemonade

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minusMonth
import kotlinx.datetime.onDay
import kotlinx.datetime.plusMonth

/** Counts the previous-month cells before [month]'s first day when the grid starts on [firstDayOfWeek]. */
private fun leadingOffset(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek,
): Int {
    val firstDayOrdinal = month.onDay(1).dayOfWeek.ordinal
    val startOrdinal = firstDayOfWeek.ordinal
    return (firstDayOrdinal - startOrdinal + DayOfWeek.entries.size) % DayOfWeek.entries.size
}

/**
 * Builds the 42-cell grid for [month], padded with adjacent-month days.
 *
 * @param month target [YearMonth]
 * @param firstDayOfWeek day shown in the first column (e.g. [DayOfWeek.MONDAY] for ISO locales)
 * @return exactly 42 [LocalDate] values in grid order
 */
internal fun daysForMonth(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): List<LocalDate> {
    val previousDays = previousMonthTrailingDays(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )
    val nextDays = nextMonthLeadingDays(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )
    return previousDays + currentMonthDays(month = month) + nextDays
}

private fun previousMonthTrailingDays(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek,
): List<LocalDate> {
    val offset = leadingOffset(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )
    val previousMonth = month.minusMonth()
    val lastDay = previousMonth.numberOfDays
    return (lastDay - offset + 1..lastDay).map { day ->
        previousMonth.onDay(day)
    }
}

private fun currentMonthDays(month: YearMonth): List<LocalDate> =
    (1..month.numberOfDays).map { day ->
        month.onDay(day)
    }

private fun nextMonthLeadingDays(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek,
): List<LocalDate> {
    val offset = leadingOffset(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )
    val remaining = CALENDAR_GRID_CELLS - offset - month.numberOfDays
    val nextMonth = month.plusMonth()
    return (1..remaining).map { day ->
        nextMonth.onDay(day)
    }
}

/** Returns the previous-month days that pad [month]'s first week. */
internal fun peekDaysBefore(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): List<LocalDate> =
    previousMonthTrailingDays(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )

/** Returns the next-month days that fill the grid after [month]'s last day. */
internal fun peekDaysAfter(
    month: YearMonth,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): List<LocalDate> =
    nextMonthLeadingDays(
        month = month,
        firstDayOfWeek = firstDayOfWeek,
    )

/**
 * Orders the [DayOfWeek] entries starting from [firstDayOfWeek].
 *
 * For example, if [firstDayOfWeek] is [DayOfWeek.MONDAY], the result is
 * `[MONDAY, TUESDAY, ..., SUNDAY]`.
 */
internal fun weekdayOrder(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<DayOfWeek> {
    val all = DayOfWeek.entries
    val startIndex = all.indexOf(firstDayOfWeek)
    return all.drop(startIndex) + all.take(startIndex)
}

/** Number of cells in a 6-week calendar grid. */
private const val CALENDAR_GRID_CELLS = 42
