package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val monthNames = listOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
)

private val weekdayAbbreviations = listOf("S", "M", "T", "W", "T", "F", "S")

private fun formatMonth(month: Int): String = monthNames[month - 1]

@Composable
internal fun DatePickerDisplay() {
    @OptIn(ExperimentalTime::class)
    val today = remember {
        Clock.System
            .todayIn(TimeZone.currentSystemDefault())
    }

    // The picker states are hoisted above the lazy list so a selection survives the item being
    // scrolled out of the viewport and disposed.
    val defaultState = rememberDatePickerState(initialDate = today)
    val futureOnlyState = rememberDatePickerState(minDate = today)
    val pastOnlyState = rememberDatePickerState(maxDate = today)

    val monthNumber = today.month.number
    val customRangeState = rememberDatePickerState(
        minDate = LocalDate(
            year = today.year,
            month = monthNumber,
            day = 1,
        ),
        maxDate = LocalDate(
            year = today.year,
            month = monthNumber,
            day = daysInMonth(
                year = today.year,
                month = monthNumber,
            ),
        ),
    )

    val dynamicState = rememberDatePickerState(initialDate = today)
    var currentMonth by remember {
        mutableStateOf(
            value = YearMonth(
                year = today.year,
                month = today.month.number,
            ),
        )
    }

    // Simulated per-month "sparse" API: every 3rd, 8th, 14th, 21st and 27th of any month
    // comes back as disabled. Swap for a repository call in real usage.
    LaunchedEffect(currentMonth) {
        delay(FAKE_FETCH_DELAY_MS)
        dynamicState.disabledDates = disabledDatesFor(currentMonth)
    }

    val rangeState = rememberDateRangePickerState()
    val limitedRangeState = rememberDateRangePickerState(maxRangeDays = 7)

    SampleScreenDisplayLazyColumn(title = "DatePicker") {
        item(key = "default") {
            DatePickerSample(
                title = "Default (all dates selectable)",
                state = defaultState,
            )
        }

        item(key = "future-only") {
            DatePickerSample(
                title = "Future dates only (minDate: today)",
                state = futureOnlyState,
            )
        }

        item(key = "past-only") {
            DatePickerSample(
                title = "Past dates only (maxDate: today)",
                state = pastOnlyState,
            )
        }

        item(key = "custom-range") {
            DatePickerSample(
                title = "Custom range (minDate & maxDate)",
                state = customRangeState,
            )
        }

        item(key = "dynamic-disabled") {
            DatePickerSection(title = "Dynamic disabled dates (fetched per visible month)") {
                LemonadeUi.DatePicker(
                    state = dynamicState,
                    monthFormatter = ::formatMonth,
                    weekdayAbbreviations = weekdayAbbreviations,
                    onMonthDisplayed = { yearMonth -> currentMonth = yearMonth },
                )
                LemonadeUi.Text(
                    text = "Selected: ${dynamicState.selectedDate?.let { date -> formatDate(date) }} — " +
                        "disabled this month: ${dynamicState.disabledDates.size}",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
            }
        }

        item(key = "range") {
            DateRangePickerSample(
                title = "Date Range Mode",
                state = rangeState,
            )
        }

        item(key = "range-max-7") {
            DateRangePickerSample(
                title = "Date Range with max 7 days",
                state = limitedRangeState,
            )
        }
    }
}

@Composable
private fun DatePickerSample(
    title: String,
    state: DatePickerState,
) {
    DatePickerSection(title = title) {
        LemonadeUi.DatePicker(
            state = state,
            monthFormatter = ::formatMonth,
            weekdayAbbreviations = weekdayAbbreviations,
        )
        LemonadeUi.Text(
            text = "Selected: ${state.selectedDate?.let { date -> formatDate(date) }}",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun DateRangePickerSample(
    title: String,
    state: DateRangePickerState,
) {
    DatePickerSection(title = title) {
        LemonadeUi.DateRangePicker(
            state = state,
            monthFormatter = ::formatMonth,
            weekdayAbbreviations = weekdayAbbreviations,
        )
        LemonadeUi.Text(
            text = "Range: ${state.selectedStartDate?.let { date -> formatDate(date) }} - " +
                "${state.selectedEndDate?.let { date -> formatDate(date) }}",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

@Composable
private fun DatePickerSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}

private fun daysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }

private fun formatDate(date: LocalDate): String = "${date.day}/${date.month.number}/${date.year}"

private const val FAKE_FETCH_DELAY_MS = 200L
private val FAKE_DISABLED_DAY_OFFSETS = intArrayOf(2, 7, 13, 20, 26)

private fun disabledDatesFor(yearMonth: YearMonth): Set<LocalDate> {
    val firstOfMonth = LocalDate(
        year = yearMonth.year,
        month = yearMonth.month.number,
        day = 1,
    )
    return FAKE_DISABLED_DAY_OFFSETS
        .map { offset ->
            firstOfMonth.plus(
                value = offset,
                unit = DateTimeUnit.DAY,
            )
        }.filter { date -> date.month == yearMonth.month }
        .toSet()
}
