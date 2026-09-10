@file:OptIn(ExperimentalTime::class)

package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * State holder for [LemonadeUi.InlineCalendar].
 *
 * Holds the selected date and the navigation bounds. The composable derives [displayedMonth] from
 * the scroll position.
 *
 * Create via [rememberInlineCalendarState].
 *
 * @param initialDate initially selected date
 * @param initialDisplayedMonth month shown first; defaults to the month of [initialDate], or the
 *   current month
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param firstDayOfWeek reserved for week-start snapping; stored and persisted, but the inline
 *   calendar is a continuous day strip with no grid columns, so it does not affect the layout
 */
@Stable
public class InlineCalendarState internal constructor(
    initialDate: LocalDate? = null,
    initialDisplayedMonth: YearMonth? = null,
    public val minDate: LocalDate? = null,
    public val maxDate: LocalDate? = null,
    public val firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
) {
    /** Selected date, or `null` while nothing is selected. */
    public var selectedDate: LocalDate? by mutableStateOf(initialDate)
        internal set

    /** Read-only; the composable writes it as the strip scrolls. */
    public var displayedMonth: YearMonth by mutableStateOf(
        initialDisplayedMonth
            ?: initialDate?.toYearMonth()
            ?: Clock.System
                .todayIn(TimeZone.currentSystemDefault())
                .toYearMonth(),
    )
        internal set

    /**
     * Navigation target written only by [navigateToMonth] and cleared by the composable once
     * consumed. Separate from [displayedMonth] so a scroll-driven header update cannot loop back
     * into an unwanted scroll to the 1st.
     */
    internal var navigationTarget: YearMonth? by mutableStateOf(null)

    /**
     * Selects [date] when it falls within [minDate] and [maxDate], and ignores it otherwise.
     *
     * The composable scrolls the strip to reveal the selection and keeps [displayedMonth] in sync
     * with the scroll position.
     */
    public fun selectDate(date: LocalDate) {
        if (minDate != null && date < minDate) return
        if (maxDate != null && date > maxDate) return
        selectedDate = date
    }

    /**
     * Navigates to [yearMonth] without changing the selection.
     *
     * The composable scrolls the strip to the first day of that month.
     */
    public fun navigateToMonth(yearMonth: YearMonth) {
        if (minDate != null && yearMonth.lastDay < minDate) return
        if (maxDate != null && yearMonth.firstDay > maxDate) return
        navigationTarget = yearMonth
    }

    internal companion object {
        fun saver(
            minDate: LocalDate?,
            maxDate: LocalDate?,
            firstDayOfWeek: DayOfWeek,
        ): Saver<InlineCalendarState, *> =
            listSaver(
                save = { state ->
                    listOf(
                        state.selectedDate
                            ?.toString()
                            .orEmpty(),
                        state.displayedMonth.year,
                        state.displayedMonth.month.number,
                    )
                },
                restore = { list ->
                    val selectedStr = list[0] as String
                    val year = list[1] as Int
                    val month = list[2] as Int
                    InlineCalendarState(
                        initialDate = selectedStr
                            .takeIf { text -> text.isNotEmpty() }
                            ?.let { text -> LocalDate.parse(text) },
                        initialDisplayedMonth = YearMonth(
                            year = year,
                            month = month,
                        ),
                        minDate = minDate,
                        maxDate = maxDate,
                        firstDayOfWeek = firstDayOfWeek,
                    )
                },
            )
    }
}

/**
 * Creates and remembers an [InlineCalendarState] that survives configuration changes via
 * [rememberSaveable].
 *
 * @param initialDate initially selected date
 * @param initialDisplayedMonth month shown first
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param firstDayOfWeek reserved for week-start snapping; stored and persisted, but the inline
 *   calendar is a continuous day strip with no grid columns, so it does not affect the layout
 */
@Composable
public fun rememberInlineCalendarState(
    initialDate: LocalDate? = null,
    initialDisplayedMonth: YearMonth? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): InlineCalendarState =
    rememberSaveable(
        saver = InlineCalendarState.saver(
            minDate = minDate,
            maxDate = maxDate,
            firstDayOfWeek = firstDayOfWeek,
        ),
    ) {
        InlineCalendarState(
            initialDate = initialDate,
            initialDisplayedMonth = initialDisplayedMonth,
            minDate = minDate,
            maxDate = maxDate,
            firstDayOfWeek = firstDayOfWeek,
        )
    }

private fun LocalDate.toYearMonth(): YearMonth =
    YearMonth(
        year = year,
        month = month.number,
    )
