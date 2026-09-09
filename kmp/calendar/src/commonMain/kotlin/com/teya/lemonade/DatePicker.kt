@file:OptIn(ExperimentalTime::class)

package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.minusMonth
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.plusMonth
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val PAGES_TOTAL = 1200
private const val CENTER_PAGE = PAGES_TOTAL / 2

/**
 * State holder for [LemonadeUi.DatePicker].
 *
 * Holds the selected date as observable state, plus the selectable range and an observable
 * [disabledDates] set that greys out (and blocks selection of) specific days regardless of
 * [minDate] / [maxDate].
 *
 * ## Dynamic disable via API
 *
 * Because [disabledDates] is a Compose [mutableStateOf] set, callers can update it in response
 * to month navigation and the picker will re-render automatically. Wire it up alongside the
 * `onMonthDisplayed` callback on [LemonadeUi.DatePicker]:
 *
 * ```kotlin
 * val state = rememberDatePickerState(initialDate = today)
 * val scope = rememberCoroutineScope()
 *
 * // Seed the initial month yourself: `onMonthDisplayed` doesn't fire for the initially
 * // displayed month — it only fires when the merchant navigates.
 * LaunchedEffect(Unit) {
 *     state.disabledDates = repository.disabledDatesFor(YearMonth.now())
 * }
 *
 * LemonadeUi.DatePicker(
 *     state = state,
 *     monthFormatter = ::formatMonth,
 *     weekdayAbbreviations = weekdayAbbreviations,
 *     onMonthDisplayed = { yearMonth ->
 *         scope.launch { state.disabledDates = repository.disabledDatesFor(yearMonth) }
 *     },
 * )
 * ```
 *
 * Or pass the set synchronously via [initialDisabledDates] if it's already known. The caller keeps
 * ownership of caching, cancellation and error handling; the picker observes whatever set the
 * state currently holds and treats its members as non-interactive.
 *
 * @param initialDate seeds [selectedDate]
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param initialDisabledDates days that start out disabled (greyed out and non-tappable); assign
 *   [disabledDates] later — including from an API call keyed on the displayed month — to change
 *   which days are blocked
 * @see rememberDatePickerState
 */
@Stable
public class DatePickerState internal constructor(
    initialDate: LocalDate? = null,
    public val minDate: LocalDate? = null,
    public val maxDate: LocalDate? = null,
    initialDisabledDates: Set<LocalDate> = emptySet(),
) {
    public var selectedDate: LocalDate? by mutableStateOf(initialDate)
        internal set

    /**
     * Days rendered as disabled, on top of the [minDate] / [maxDate] bounds.
     *
     * The picker greys these days out and ignores taps on them. Empty by default; update the set
     * in response to `onMonthDisplayed` when it comes from an API keyed on the visible month.
     *
     * Assign a new set to trigger recomposition (`state.disabledDates = newSet`); the setter copies
     * defensively, so mutating a set you passed earlier has no effect.
     */
    public var disabledDates: Set<LocalDate>
        get() = _disabledDates
        set(value) {
            _disabledDates = value.toSet()
        }
    private var _disabledDates: Set<LocalDate> by mutableStateOf(initialDisabledDates.toSet())
}

/**
 * Creates and remembers a [DatePickerState].
 *
 * @param initialDate initially selected date
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param initialDisabledDates days that start out disabled; pass an empty set and assign
 *   [DatePickerState.disabledDates] later when the disabled list comes from an async source
 */
@Composable
public fun rememberDatePickerState(
    initialDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    initialDisabledDates: Set<LocalDate> = emptySet(),
): DatePickerState =
    remember(initialDate, minDate, maxDate, initialDisabledDates) {
        DatePickerState(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            initialDisabledDates = initialDisabledDates,
        )
    }

/**
 * State holder for [LemonadeUi.DateRangePicker].
 *
 * Holds the selected start/end dates as observable state, plus the selectable range, the maximum
 * range length, and an observable [disabledDates] set that greys out (and blocks selection of)
 * specific days regardless of [minDate] / [maxDate].
 *
 * See [DatePickerState] for the dynamic-disable pattern — the same `onMonthDisplayed` approach
 * applies. A disabled day inside a completed range still greys out, and the range spans it; the
 * user just cannot pick it as start or end.
 *
 * @param initialStartDate seeds [selectedStartDate]
 * @param initialEndDate seeds [selectedEndDate]
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param maxRangeDays largest number of days a range may span
 * @param initialDisabledDates days that start out disabled (greyed out and non-tappable)
 * @see rememberDateRangePickerState
 */
@Stable
public class DateRangePickerState internal constructor(
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    public val minDate: LocalDate? = null,
    public val maxDate: LocalDate? = null,
    public val maxRangeDays: Int? = null,
    initialDisabledDates: Set<LocalDate> = emptySet(),
) {
    public var selectedStartDate: LocalDate? by mutableStateOf(initialStartDate)
        internal set
    public var selectedEndDate: LocalDate? by mutableStateOf(initialEndDate)
        internal set

    /**
     * Days rendered as disabled, on top of the [minDate] / [maxDate] bounds.
     *
     * The picker greys these days out and ignores taps on them. Empty by default; update the set
     * in response to `onMonthDisplayed` when it comes from an API keyed on the visible month.
     *
     * Assign a new set to trigger recomposition (`state.disabledDates = newSet`); the setter copies
     * defensively, so mutating a set you passed earlier has no effect.
     */
    public var disabledDates: Set<LocalDate>
        get() = _disabledDates
        set(value) {
            _disabledDates = value.toSet()
        }
    private var _disabledDates: Set<LocalDate> by mutableStateOf(initialDisabledDates.toSet())
}

/**
 * Creates and remembers a [DateRangePickerState].
 *
 * @param initialStartDate initial start date for the range
 * @param initialEndDate initial end date for the range
 * @param minDate earliest selectable date (inclusive)
 * @param maxDate latest selectable date (inclusive)
 * @param maxRangeDays largest number of days a range may span
 * @param initialDisabledDates days that start out disabled; pass an empty set and assign
 *   [DateRangePickerState.disabledDates] later when the disabled list comes from an async source
 */
@Composable
public fun rememberDateRangePickerState(
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    maxRangeDays: Int? = null,
    initialDisabledDates: Set<LocalDate> = emptySet(),
): DateRangePickerState =
    remember(initialStartDate, initialEndDate, minDate, maxDate, maxRangeDays, initialDisabledDates) {
        DateRangePickerState(
            initialStartDate = initialStartDate,
            initialEndDate = initialEndDate,
            minDate = minDate,
            maxDate = maxDate,
            maxRangeDays = maxRangeDays,
            initialDisabledDates = initialDisabledDates,
        )
    }

/**
 * A single-date picker widget from the Lemonade Design System.
 *
 * A scrollable month grid for picking one date. Pages animate between months, and
 * [DatePickerState.minDate] / [DatePickerState.maxDate] bound what the user can select.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberDatePickerState(initialDate = today)
 * LemonadeUi.DatePicker(
 *     state = state,
 *     monthFormatter = { monthNumber -> monthNames[monthNumber - 1] },
 *     weekdayAbbreviations = listOf("S", "M", "T", "W", "T", "F", "S"),
 * )
 * // Observe: state.selectedDate
 * ```
 *
 * @param monthFormatter returns the month name for a month number (1-12); the caller localizes it
 * @param weekdayAbbreviations exactly 7 items, Sunday through Saturday; the caller localizes them
 * @param modifier [Modifier] for layout adjustments
 * @param state configuration state created via [rememberDatePickerState]; observe
 *   [DatePickerState.selectedDate] to react to user selections
 * @param firstDayOfWeek first day of the week shown in the grid; callers should pass the value for
 *   their locale (e.g. [DayOfWeek.MONDAY] for ISO / European locales)
 * @param today date drawn as today; defaults to the device's current date
 * @param onMonthDisplayed called when the displayed month changes
 */
@Composable
public fun LemonadeUi.DatePicker(
    monthFormatter: (month: Int) -> String,
    weekdayAbbreviations: List<String>,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    today: LocalDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
) {
    CoreDatePicker(
        monthFormatter = monthFormatter,
        weekdayAbbreviations = weekdayAbbreviations,
        modifier = modifier,
        selectedDates = setOfNotNull(state.selectedDate),
        onDateSelected = { date -> state.selectedDate = date },
        minDate = state.minDate,
        maxDate = state.maxDate,
        disabledDates = state.disabledDates,
        firstDayOfWeek = firstDayOfWeek,
        today = today,
        onMonthDisplayed = onMonthDisplayed,
    )
}

/**
 * A date range picker widget from the Lemonade Design System.
 *
 * A scrollable month grid for picking a date range. The first tap sets the start date and the
 * second sets the end; a second tap before the start swaps the two.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberDateRangePickerState(maxRangeDays = 7)
 * LemonadeUi.DateRangePicker(
 *     state = state,
 *     monthFormatter = { monthNumber -> monthNames[monthNumber - 1] },
 *     weekdayAbbreviations = listOf("S", "M", "T", "W", "T", "F", "S"),
 * )
 * // Observe: state.selectedStartDate, state.selectedEndDate
 * ```
 *
 * @param monthFormatter returns the month name for a month number (1-12); the caller localizes it
 * @param weekdayAbbreviations exactly 7 items, Sunday through Saturday; the caller localizes them
 * @param modifier [Modifier] for layout adjustments
 * @param state configuration state created via [rememberDateRangePickerState]; observe
 *   [DateRangePickerState.selectedStartDate] and [DateRangePickerState.selectedEndDate] to react
 *   to user selections
 * @param firstDayOfWeek first day of the week shown in the grid; callers should pass the value for
 *   their locale (e.g. [DayOfWeek.MONDAY] for ISO / European locales)
 * @param today date drawn as today; defaults to the device's current date
 * @param onMonthDisplayed called when the displayed month changes
 */
@Composable
public fun LemonadeUi.DateRangePicker(
    monthFormatter: (month: Int) -> String,
    weekdayAbbreviations: List<String>,
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    today: LocalDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
) {
    val isSelectingEndDate = state.selectedStartDate != null && state.selectedEndDate == null

    val effectiveMin = remember(
        state.minDate,
        state.selectedStartDate,
        isSelectingEndDate,
        state.maxRangeDays,
    ) {
        var min = state.minDate
        if (isSelectingEndDate && state.maxRangeDays != null) {
            state.selectedStartDate?.let { start ->
                val rangeMin = start.minus(
                    value = state.maxRangeDays,
                    unit = DateTimeUnit.DAY,
                )
                if (min == null || rangeMin > min) {
                    min = rangeMin
                }
            }
        }
        min
    }
    val effectiveMax = remember(
        state.maxDate,
        state.selectedStartDate,
        isSelectingEndDate,
        state.maxRangeDays,
    ) {
        var max = state.maxDate
        if (isSelectingEndDate && state.maxRangeDays != null) {
            state.selectedStartDate?.let { start ->
                val rangeMax = start.plus(
                    value = state.maxRangeDays,
                    unit = DateTimeUnit.DAY,
                )

                if (max == null || rangeMax < max) {
                    max = rangeMax
                }
            }
        }
        max
    }

    CoreDatePicker(
        monthFormatter = monthFormatter,
        weekdayAbbreviations = weekdayAbbreviations,
        modifier = modifier,
        selectedDates = setOfNotNull(state.selectedStartDate, state.selectedEndDate),
        onDateSelected = { date ->
            val start = state.selectedStartDate

            if (!isSelectingEndDate || start == null) {
                state.selectedStartDate = date
                state.selectedEndDate = null
                return@CoreDatePicker
            }

            state.selectedStartDate = minOf(
                a = start,
                b = date,
            )
            state.selectedEndDate = maxOf(
                a = start,
                b = date,
            )
        },
        minDate = effectiveMin,
        maxDate = effectiveMax,
        disabledDates = state.disabledDates,
        firstDayOfWeek = firstDayOfWeek,
        today = today,
        onMonthDisplayed = onMonthDisplayed,
    )
}

@Composable
public fun LemonadeUi.DatePicker(
    monthFormatter: (month: Int) -> String,
    weekdayAbbreviations: List<String>,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
) {
    DatePicker(
        monthFormatter = monthFormatter,
        weekdayAbbreviations = weekdayAbbreviations,
        modifier = modifier,
        state = state,
        firstDayOfWeek = firstDayOfWeek,
        today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
        onMonthDisplayed = onMonthDisplayed,
    )
}

@Composable
public fun LemonadeUi.DateRangePicker(
    monthFormatter: (month: Int) -> String,
    weekdayAbbreviations: List<String>,
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
) {
    DateRangePicker(
        monthFormatter = monthFormatter,
        weekdayAbbreviations = weekdayAbbreviations,
        modifier = modifier,
        state = state,
        firstDayOfWeek = firstDayOfWeek,
        today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
        onMonthDisplayed = onMonthDisplayed,
    )
}

@Suppress("LongParameterList")
@Composable
private fun CoreDatePicker(
    modifier: Modifier,
    monthFormatter: (Int) -> String,
    weekdayAbbreviations: List<String>,
    selectedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek,
    today: LocalDate,
    onMonthDisplayed: ((YearMonth) -> Unit)?,
) {
    val startMonth = remember(today) {
        YearMonth(
            year = today.year,
            month = today.month.number,
        )
    }

    val pagerState = rememberPagerState(initialPage = CENTER_PAGE) { PAGES_TOTAL }
    val coroutineScope = rememberCoroutineScope()

    val centerYearMonth = startMonth.plus(
        value = pagerState.currentPage.toLong() - CENTER_PAGE,
        unit = DateTimeUnit.MONTH,
    )

    val hasEmittedInitialMonth = remember { BoolRef(false) }

    if (onMonthDisplayed != null) {
        LaunchedEffect(centerYearMonth) {
            if (hasEmittedInitialMonth.value) {
                onMonthDisplayed(centerYearMonth)
            } else {
                hasEmittedInitialMonth.value = true
            }
        }
    }

    val headerLabel = "${monthFormatter(centerYearMonth.month.number)} ${centerYearMonth.year}"

    val previousMonthLastDay = centerYearMonth
        .minus(
            value = 1,
            unit = DateTimeUnit.MONTH,
        ).lastDay
    val nextMonthFirstDay = centerYearMonth
        .plus(
            value = 1,
            unit = DateTimeUnit.MONTH,
        ).firstDay

    val canGoPrev = pagerState.currentPage > 0 &&
        (minDate == null || minDate <= previousMonthLastDay)

    val canGoNext = pagerState.currentPage < PAGES_TOTAL - 1 &&
        (maxDate == null || maxDate >= nextMonthFirstDay)

    val horizontalPadding = LocalSpaces.current.spacing400

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalendarMonthHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            headerLabel = headerLabel,
            canGoPrev = canGoPrev,
            canGoNext = canGoNext,
            onPrev = {
                val newYearMonth = centerYearMonth.minusMonth()
                val diff = centerYearMonth.monthsUntil(newYearMonth)
                val targetPage = (pagerState.currentPage + diff).coerceIn(
                    minimumValue = 0,
                    maximumValue = PAGES_TOTAL - 1,
                )
                coroutineScope.launch {
                    pagerState.animateScrollToPage(targetPage)
                }
            },
            onNext = {
                val newYearMonth = centerYearMonth.plusMonth()
                val diff = centerYearMonth.monthsUntil(newYearMonth)
                val targetPage = (pagerState.currentPage + diff).coerceIn(
                    minimumValue = 0,
                    maximumValue = PAGES_TOTAL - 1,
                )
                coroutineScope.launch {
                    pagerState.animateScrollToPage(targetPage)
                }
            },
        )

        Spacer(modifier = Modifier.height(LocalSpaces.current.spacing200))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
        ) {
            weekdayAbbreviations
                .take(DayOfWeek.entries.size)
                .forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        LemonadeUi.Text(
                            text = day,
                            textStyle = LocalTypographies.current.bodyXSmallOverline,
                            color = LocalColors.current.content.contentPrimary,
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(LocalSpaces.current.spacing100))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = horizontalPadding,
        ) { pageIndex ->
            MonthGrid(
                yearMonth = startMonth.plus(
                    value = pageIndex.toLong() - CENTER_PAGE,
                    unit = DateTimeUnit.MONTH,
                ),
                selectedDates = selectedDates,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDates = disabledDates,
                firstDayOfWeek = firstDayOfWeek,
                onDateSelected = onDateSelected,
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    selectedDates: Set<LocalDate>,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDates: Set<LocalDate>,
    firstDayOfWeek: DayOfWeek,
    onDateSelected: (LocalDate) -> Unit,
) {
    val days = remember(yearMonth, firstDayOfWeek) {
        daysForMonth(
            month = yearMonth,
            firstDayOfWeek = firstDayOfWeek,
        )
    }

    val isRangeComplete = selectedDates.size >= 2
    val rangeStartDate = if (isRangeComplete) selectedDates.min() else null
    val rangeEndDate = if (isRangeComplete) selectedDates.max() else null

    val cellHorizontalPadding = LocalSpaces.current.spacing200

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalSpaces.current.spacing100),
    ) {
        days
            .chunked(DayOfWeek.entries.size)
            .forEach { week ->
                Row(
                    modifier = Modifier.drawRangeHighlight(
                        week = week,
                        rangeStartDate = rangeStartDate,
                        rangeEndDate = rangeEndDate,
                        cellHorizontalPadding = cellHorizontalPadding,
                    ),
                ) {
                    week.forEach { current ->
                        val isInRange = rangeStartDate != null &&
                            rangeEndDate != null &&
                            current in rangeStartDate..rangeEndDate

                        val isBeforeMin = minDate != null && current < minDate
                        val isAfterMax = maxDate != null && current > maxDate
                        val isExplicitlyDisabled = current in disabledDates

                        ContentCell(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = cellHorizontalPadding),
                            text = "${current.day}",
                            isCurrent = current == today,
                            isSelected = current in selectedDates,
                            isEnabled = !isBeforeMin && !isAfterMax && !isExplicitlyDisabled,
                            isOutsideVisibleRange = current.yearMonth != yearMonth,
                            isInsideSelectedRange = isInRange,
                            onClick = { onDateSelected(current) },
                        )
                    }
                }
            }
    }
}

@Composable
private fun Modifier.drawRangeHighlight(
    week: List<LocalDate>,
    rangeStartDate: LocalDate?,
    rangeEndDate: LocalDate?,
    cellHorizontalPadding: Dp,
): Modifier {
    if (rangeStartDate == null || rangeEndDate == null) return this

    val cellRadius = LocalRadius.current.radius200
    val highlightColor = LocalColors.current.background.bgBrandSubtle

    return drawBehind {
        val startIndex = week.indexOfFirst { day ->
            day >= rangeStartDate
        }
        val endIndex = week.indexOfLast { day ->
            day <= rangeEndDate
        }

        if (startIndex != -1 && endIndex != -1) {
            val cellWidth = size.width / DayOfWeek.entries.size
            val paddingPx = cellHorizontalPadding.toPx()
            val left = cellWidth * startIndex + paddingPx
            val right = cellWidth * (endIndex + 1) - paddingPx

            drawRoundRect(
                color = highlightColor,
                topLeft = Offset(
                    x = left,
                    y = 0f,
                ),
                size = Size(
                    width = right - left,
                    height = size.height,
                ),
                cornerRadius = CornerRadius(cellRadius.toPx()),
            )
        }
    }
}
