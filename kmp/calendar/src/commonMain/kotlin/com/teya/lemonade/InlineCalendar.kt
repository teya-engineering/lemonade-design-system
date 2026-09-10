@file:OptIn(ExperimentalTime::class)
@file:Suppress("TooManyFunctions")

package com.teya.lemonade

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.DayLabelFormat
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.onDay
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TOTAL_DAYS = 3651
private const val CENTER_INDEX = TOTAL_DAYS / 2

private const val DEFAULT_VISIBLE_CELLS = 7
private const val LARGE_FONT_SCALE_VISIBLE_CELLS = 5
private const val LARGE_FONT_SCALE_THRESHOLD = 1.3f

/** English fallback weekday labels, Monday through Sunday. */
private val DEFAULT_WEEKDAY_LABELS_NARROW: List<String> =
    listOf("M", "T", "W", "T", "F", "S", "S")

/** English fallback weekday names for screen readers, Monday through Sunday. */
private val DEFAULT_WEEKDAY_FULL_NAMES: List<String> = listOf(
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
)

private val DEFAULT_WEEKDAY_LABELS_SHORT: List<String> =
    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

/** English fallback month names indexed 0-11, January through December. */
private val DEFAULT_MONTH_NAMES: List<String> = listOf(
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

/**
 * An inline calendar strip from the Lemonade Design System.
 *
 * Renders a horizontally scrollable row of day cells over a fixed window of roughly five years
 * either side of today. The list never regenerates; the displayed month follows the scroll
 * position, so the strip never snaps back.
 *
 * ## Localization
 * By default the component uses English labels. For localized UIs, provide
 * [weekdayLabels] and [monthFormatter] matching the user's locale, as
 * [LemonadeUi.DatePicker] does.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberInlineCalendarState(initialDate = today)
 * LemonadeUi.InlineCalendar(
 *     state = state,
 *     weekdayLabels = listOf("L", "M", "M", "J", "V", "S", "D"), // French
 *     monthFormatter = { month -> frenchMonthNames[month - 1] },
 *     onDateSelected = { date -> /* handle selection */ },
 * )
 * // Observe: state.selectedDate
 * ```
 *
 * @param state state holder created via [rememberInlineCalendarState]
 * @param modifier [Modifier] for layout adjustments
 * @param dayLabelFormat picks [DayLabelFormat.Narrow] (single char) or [DayLabelFormat.Short]
 *   (3 chars) labels; ignored once [weekdayLabels] is given
 * @param weekdayLabels exactly 7 localized weekday abbreviations ordered Monday through Sunday;
 *   overrides [dayLabelFormat], and callers should pass it for proper localization
 * @param monthFormatter returns a localized month name for a month number (1-12) for the header
 *   label; defaults to English month names
 * @param onDateSelected called when the user taps a date
 * @param onMonthDisplayed called when the displayed month changes
 * @param enabledDates decides which dates stay interactive; a date is enabled only when this
 *   returns `true` **and** it falls inside the
 *   [InlineCalendarState.minDate]..[InlineCalendarState.maxDate] range, which suits greying out
 *   days that carry no content
 * @param prevMonthContentDescription accessibility label for the previous-month button; defaults
 *   to the English "Previous month", so callers should pass a localized string
 * @param nextMonthContentDescription accessibility label for the next-month button; defaults to
 *   the English "Next month", so callers should pass a localized string
 * @param weekdayAccessibilityLabels exactly 7 full weekday names ordered Monday through Sunday,
 *   read by screen readers only; defaults to English names, so callers should pass a localized
 *   list
 * @param expandSelectionToLabel when `true` (default), the selection background covers the weekday
 *   label, day number and trailing content; when `false`, only the day number circle carries the
 *   brand background ([LemonadeUi.DatePicker] style)
 * @param selectionBackgroundColor when non-null, overrides the brand background color of selected
 *   cells
 * @param selectionContentColor when non-null, overrides the text color on selected cells
 * @param today date drawn as today; defaults to the device's current date
 * @param trailingContent composable rendered below each day cell (e.g. event dots); receives the
 *   cell's [LocalDate] and whether that date is selected
 */
@ExperimentalLemonadeComponent
@Composable
public fun LemonadeUi.InlineCalendar(
    state: InlineCalendarState,
    modifier: Modifier = Modifier,
    dayLabelFormat: DayLabelFormat = DayLabelFormat.Narrow,
    weekdayLabels: List<String>? = null,
    monthFormatter: ((month: Int) -> String)? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null,
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
    enabledDates: ((LocalDate) -> Boolean)? = null,
    prevMonthContentDescription: String = "Previous month",
    nextMonthContentDescription: String = "Next month",
    weekdayAccessibilityLabels: List<String>? = null,
    expandSelectionToLabel: Boolean = true,
    selectionBackgroundColor: Color? = null,
    selectionContentColor: Color? = null,
    today: LocalDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    trailingContent: @Composable ((LocalDate, Boolean) -> Unit)? = null,
) {
    val anchorDate = remember(today) { today }

    val density = LocalDensity.current
    val visibleCells = remember(density.fontScale) {
        if (density.fontScale > LARGE_FONT_SCALE_THRESHOLD) {
            LARGE_FONT_SCALE_VISIBLE_CELLS
        } else {
            DEFAULT_VISIBLE_CELLS
        }
    }

    val resolvedWeekdayLabels = remember(weekdayLabels, dayLabelFormat) {
        resolveWeekdayLabels(
            weekdayLabels = weekdayLabels,
            dayLabelFormat = dayLabelFormat,
        )
    }

    val resolvedMonthFormatter: (Int) -> String = remember(monthFormatter) {
        monthFormatter
            ?: { month -> DEFAULT_MONTH_NAMES[month - 1] }
    }

    val resolvedAccessibilityLabels = remember(weekdayAccessibilityLabels) {
        weekdayAccessibilityLabels
            ?: DEFAULT_WEEKDAY_FULL_NAMES
    }

    // selectedDate must stay out of this remember key: recomputing firstIndex on selection would
    // invalidate every index-to-date mapping and desync listState from the virtual index space.
    val indexRange = remember(state.minDate, state.maxDate, anchorDate, visibleCells) {
        calculateIndexRange(
            minDate = state.minDate,
            maxDate = state.maxDate,
            anchorDate = anchorDate,
            rangePadding = visibleCells,
        )
    }
    val firstIndex = indexRange.firstIndex
    val itemCount = indexRange.itemCount

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = indexRange.initialIndex,
    )

    // Guards the later scroll effects from racing the initial, non-animated centering scroll.
    val initialScrollDone = remember { mutableStateOf(false) }

    val headerMonth by remember {
        derivedStateOf {
            deriveHeaderMonth(
                listState = listState,
                firstIndex = firstIndex,
                visibleCells = visibleCells,
                anchorDate = anchorDate,
            )
        }
    }

    val haptic = LocalHapticFeedback.current

    // Set by the day tap handler so a cross-month tap does not fire a second haptic on top of the
    // one onClick already fired.
    val selectionTriggeredMonthChange = remember { BoolRef(false) }

    // The month effect fires once on initial composition; swallowing that emission keeps mounting
    // the component silent, which matters when several calendars share a screen.
    val hasEmittedInitialMonth = remember { BoolRef(false) }

    LaunchedEffect(headerMonth) {
        state.displayedMonth = headerMonth
        onMonthDisplayed?.invoke(headerMonth)
        tickHapticOnScrolledMonthChange(
            haptic = haptic,
            hasEmittedInitialMonth = hasEmittedInitialMonth,
            selectionTriggeredMonthChange = selectionTriggeredMonthChange,
        )
    }

    val coroutineScope = rememberCoroutineScope()

    val scrollToDate: (LocalDate) -> Unit = { date ->
        val targetIndex = indexRange.localIndexOf(
            date = date,
            anchorDate = anchorDate,
        )
        coroutineScope.launch {
            listState.scrollToCenteredIndex(
                targetIndex = targetIndex,
                visibleCells = visibleCells,
                animate = true,
            )
        }
        Unit
    }

    LaunchedEffect(Unit) {
        listState.awaitMeasuredViewport()
        val targetDate = state.selectedDate
            ?: today
        listState.scrollToCenteredIndex(
            targetIndex = indexRange.localIndexOf(
                date = targetDate,
                anchorDate = anchorDate,
            ),
            visibleCells = visibleCells,
            animate = false,
        )
        initialScrollDone.value = true
    }

    LaunchedEffect(state.selectedDate) {
        if (!initialScrollDone.value) return@LaunchedEffect
        val targetDate = state.selectedDate
            ?: return@LaunchedEffect
        val targetIndex = indexRange.localIndexOf(
            date = targetDate,
            anchorDate = anchorDate,
        )
        listState.awaitMeasuredViewport()
        listState.scrollToCenteredIndex(
            targetIndex = targetIndex,
            visibleCells = visibleCells,
            animate = true,
        )
    }

    LaunchedEffect(state.navigationTarget) {
        val target = state.navigationTarget
            ?: return@LaunchedEffect
        scrollToDate(target.onDay(1))
        state.navigationTarget = null
    }

    Column(modifier = modifier) {
        CalendarMonthHeader(
            modifier = Modifier.fillMaxWidth(),
            headerLabel = buildHeaderLabel(
                yearMonth = headerMonth,
                monthFormatter = resolvedMonthFormatter,
            ),
            canGoPrev = canNavigatePrev(
                headerMonth = headerMonth,
                minDate = state.minDate,
            ),
            canGoNext = canNavigateNext(
                headerMonth = headerMonth,
                maxDate = state.maxDate,
            ),
            prevMonthContentDescription = prevMonthContentDescription,
            nextMonthContentDescription = nextMonthContentDescription,
            onPrev = {
                val firstOfPrev = headerMonth
                    .minus(
                        value = 1,
                        unit = DateTimeUnit.MONTH,
                    ).onDay(1)
                scrollToDate(firstOfPrev)
            },
            onNext = {
                val firstOfNext = headerMonth
                    .plus(
                        value = 1,
                        unit = DateTimeUnit.MONTH,
                    ).onDay(1)
                scrollToDate(firstOfNext)
            },
        )

        Spacer(modifier = Modifier.height(LocalSpaces.current.spacing200))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellWidth: Dp = maxWidth / visibleCells

            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        collectionInfo = CollectionInfo(
                            rowCount = 1,
                            columnCount = itemCount,
                        )
                    },
            ) {
                items(
                    count = itemCount,
                    key = { localIndex -> localIndex + firstIndex },
                ) { localIndex ->
                    val globalIndex = localIndex + firstIndex
                    val date = remember(globalIndex) {
                        globalIndexToDate(
                            globalIndex = globalIndex,
                            anchorDate = anchorDate,
                        )
                    }
                    InlineCalendarDayItem(
                        date = date,
                        today = today,
                        cellWidth = cellWidth,
                        state = state,
                        headerMonth = headerMonth,
                        enabledDates = enabledDates,
                        resolvedWeekdayLabels = resolvedWeekdayLabels,
                        resolvedAccessibilityLabels = resolvedAccessibilityLabels,
                        resolvedMonthFormatter = resolvedMonthFormatter,
                        expandSelectionToLabel = expandSelectionToLabel,
                        selectionBackgroundColor = selectionBackgroundColor,
                        selectionContentColor = selectionContentColor,
                        trailingContent = trailingContent,
                        onDateTap = { tappedDate ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (tappedDate.yearMonth != headerMonth) {
                                selectionTriggeredMonthChange.value = true
                            }
                            state.selectDate(tappedDate)
                            onDateSelected?.invoke(tappedDate)
                        },
                    )
                }
            }
        }
    }
}

@ExperimentalLemonadeComponent
@Composable
public fun LemonadeUi.InlineCalendar(
    state: InlineCalendarState,
    modifier: Modifier = Modifier,
    dayLabelFormat: DayLabelFormat = DayLabelFormat.Narrow,
    weekdayLabels: List<String>? = null,
    monthFormatter: ((month: Int) -> String)? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null,
    onMonthDisplayed: ((YearMonth) -> Unit)? = null,
    enabledDates: ((LocalDate) -> Boolean)? = null,
    prevMonthContentDescription: String = "Previous month",
    nextMonthContentDescription: String = "Next month",
    weekdayAccessibilityLabels: List<String>? = null,
    expandSelectionToLabel: Boolean = true,
    selectionBackgroundColor: Color? = null,
    selectionContentColor: Color? = null,
    trailingContent: @Composable ((LocalDate, Boolean) -> Unit)? = null,
) {
    InlineCalendar(
        state = state,
        modifier = modifier,
        dayLabelFormat = dayLabelFormat,
        weekdayLabels = weekdayLabels,
        monthFormatter = monthFormatter,
        onDateSelected = onDateSelected,
        onMonthDisplayed = onMonthDisplayed,
        enabledDates = enabledDates,
        prevMonthContentDescription = prevMonthContentDescription,
        nextMonthContentDescription = nextMonthContentDescription,
        weekdayAccessibilityLabels = weekdayAccessibilityLabels,
        expandSelectionToLabel = expandSelectionToLabel,
        selectionBackgroundColor = selectionBackgroundColor,
        selectionContentColor = selectionContentColor,
        today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
        trailingContent = trailingContent,
    )
}

/** Fires the month-boundary haptic only for scroll-driven changes. */
private fun tickHapticOnScrolledMonthChange(
    haptic: HapticFeedback,
    hasEmittedInitialMonth: BoolRef,
    selectionTriggeredMonthChange: BoolRef,
) {
    when {
        !hasEmittedInitialMonth.value -> hasEmittedInitialMonth.value = true
        selectionTriggeredMonthChange.value -> selectionTriggeredMonthChange.value = false
        else -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
}

/** Indexes [weekdayLabels] by [DayOfWeek] ordinal — Monday at 0 through Sunday at 6. */
private fun resolveWeekdayLabel(
    dayOfWeek: DayOfWeek,
    weekdayLabels: List<String>,
): String = weekdayLabels[dayOfWeek.ordinal]

/** Builds a "Month Year" header string, naming the month through [monthFormatter]. */
private fun buildHeaderLabel(
    yearMonth: YearMonth,
    monthFormatter: (Int) -> String,
): String {
    val monthName = monthFormatter(yearMonth.month.number)
    return "$monthName ${yearMonth.year}"
}

/**
 * Builds a day cell's accessibility description.
 *
 * Format: "Weekday, Month Day, Year" (e.g. "Monday, April 14, 2025").
 */
private fun buildCellContentDescription(
    date: LocalDate,
    weekdayLabels: List<String>,
    monthFormatter: (Int) -> String,
): String {
    val weekday = weekdayLabels[date.dayOfWeek.ordinal]
    val month = monthFormatter(date.month.number)
    return "$weekday, $month ${date.day}, ${date.year}"
}

/** True when [date] sits within [minDate]/[maxDate] and [enabledDates] does not reject it. */
private fun isDateEnabled(
    date: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    enabledDates: ((LocalDate) -> Boolean)?,
): Boolean {
    val inRange = (minDate == null || date >= minDate) &&
        (maxDate == null || date <= maxDate)
    return inRange && enabledDates?.invoke(date) != false
}

/** Converts a global virtual index to a [LocalDate], with [anchorDate] pinned at [CENTER_INDEX]. */
private fun globalIndexToDate(
    globalIndex: Int,
    anchorDate: LocalDate,
): LocalDate {
    val daysFromAnchor = (globalIndex - CENTER_INDEX).toLong()
    return anchorDate.plus(
        value = daysFromAnchor,
        unit = DateTimeUnit.DAY,
    )
}

/** Inverse of [globalIndexToDate]: returns the global virtual index for [date]. */
private fun dateToGlobalIndex(
    date: LocalDate,
    anchorDate: LocalDate,
): Int = CENTER_INDEX + anchorDate.daysUntil(date)

/** Returns [weekdayLabels] when given, else the English labels for [dayLabelFormat]. */
private fun resolveWeekdayLabels(
    weekdayLabels: List<String>?,
    dayLabelFormat: DayLabelFormat,
): List<String> =
    weekdayLabels
        ?: when (dayLabelFormat) {
            DayLabelFormat.Narrow -> DEFAULT_WEEKDAY_LABELS_NARROW
            DayLabelFormat.Short -> DEFAULT_WEEKDAY_LABELS_SHORT
        }

/** Bounded index window and initial scroll position derived from the `minDate`/`maxDate` bounds. */
private data class CalendarIndexRange(
    val firstIndex: Int,
    val lastIndex: Int,
    val itemCount: Int,
    val initialIndex: Int,
)

/**
 * Computes the clamped virtual index window for the strip.
 *
 * [CalendarIndexRange.initialIndex] tracks [CENTER_INDEX] rather than the selected date, so
 * [rememberLazyListState] receives a stable value and the composable can center exactly once the
 * viewport is measured. Feeding the selection in here would shift [CalendarIndexRange.firstIndex]
 * on every tap and desync the index-to-date mappings from [LazyListState].
 */
private fun calculateIndexRange(
    minDate: LocalDate?,
    maxDate: LocalDate?,
    anchorDate: LocalDate,
    rangePadding: Int,
): CalendarIndexRange {
    val firstIndex = minDate
        ?.let { date ->
            val index = dateToGlobalIndex(
                date = date,
                anchorDate = anchorDate,
            )
            (index - rangePadding).coerceAtLeast(0)
        }
        ?: 0
    val lastIndex = maxDate
        ?.let { date ->
            val index = dateToGlobalIndex(
                date = date,
                anchorDate = anchorDate,
            )
            (index + rangePadding).coerceAtMost(TOTAL_DAYS - 1)
        }
        ?: TOTAL_DAYS - 1
    val itemCount = lastIndex - firstIndex + 1
    val initialIndex = (CENTER_INDEX - firstIndex).coerceIn(
        minimumValue = 0,
        maximumValue = itemCount - 1,
    )
    return CalendarIndexRange(
        firstIndex = firstIndex,
        lastIndex = lastIndex,
        itemCount = itemCount,
        initialIndex = initialIndex,
    )
}

/** Maps [date] onto a list index inside this window, clamped to its bounds. */
private fun CalendarIndexRange.localIndexOf(
    date: LocalDate,
    anchorDate: LocalDate,
): Int {
    val globalIndex = dateToGlobalIndex(
        date = date,
        anchorDate = anchorDate,
    )
    return (globalIndex - firstIndex).coerceIn(
        minimumValue = 0,
        maximumValue = itemCount - 1,
    )
}

private fun canNavigatePrev(
    headerMonth: YearMonth,
    minDate: LocalDate?,
): Boolean = minDate == null || headerMonth > minDate.yearMonth

private fun canNavigateNext(
    headerMonth: YearMonth,
    maxDate: LocalDate?,
): Boolean = maxDate == null || headerMonth < maxDate.yearMonth

/**
 * Returns the [YearMonth] for the header label.
 *
 * Picks the month owning the cell whose center sits closest to the viewport center. Falls back to
 * `firstVisibleItemIndex + visibleCells / 2` while `layoutInfo` has no visible items yet, such as
 * on the first frame.
 */
private fun deriveHeaderMonth(
    listState: LazyListState,
    firstIndex: Int,
    visibleCells: Int,
    anchorDate: LocalDate,
): YearMonth {
    val layoutInfo = listState.layoutInfo
    val viewportCenter = layoutInfo.viewportStartOffset +
        (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
    val centerItem = layoutInfo.visibleItemsInfo.minByOrNull { item ->
        abs(item.offset + item.size / 2 - viewportCenter)
    }
    val localIndex = centerItem?.index
        ?: listState.firstVisibleItemIndex + visibleCells / 2
    val globalIndex = (localIndex + firstIndex).coerceIn(
        minimumValue = 0,
        maximumValue = TOTAL_DAYS - 1,
    )
    val date = globalIndexToDate(
        globalIndex = globalIndex,
        anchorDate = anchorDate,
    )
    return YearMonth(
        year = date.year,
        month = date.month.number,
    )
}

/** Suspends until the list reports a measured viewport. */
private suspend fun LazyListState.awaitMeasuredViewport() {
    snapshotFlow { layoutInfo.viewportSize.width }
        .filter { width -> width > 0 }
        .first()
}

/** Scrolls so the cell at [targetIndex] sits in the horizontal center of the viewport. */
private suspend fun LazyListState.scrollToCenteredIndex(
    targetIndex: Int,
    visibleCells: Int,
    animate: Boolean,
) {
    val viewportWidth = layoutInfo.viewportSize.width
    if (viewportWidth <= 0) return
    val cellWidthPx = viewportWidth / visibleCells
    val scrollOffset = -(viewportWidth - cellWidthPx) / 2
    if (animate) {
        animateScrollToItem(
            index = targetIndex,
            scrollOffset = scrollOffset,
        )
    } else {
        scrollToItem(
            index = targetIndex,
            scrollOffset = scrollOffset,
        )
    }
}

/**
 * A single day cell inside the inline calendar strip.
 *
 * Takes [resolvedWeekdayLabels], [resolvedAccessibilityLabels] and [resolvedMonthFormatter]
 * already resolved so the item does not re-resolve them on every recomposition.
 */
@Composable
@Suppress("LongParameterList")
private fun InlineCalendarDayItem(
    date: LocalDate,
    today: LocalDate,
    cellWidth: Dp,
    state: InlineCalendarState,
    headerMonth: YearMonth,
    enabledDates: ((LocalDate) -> Boolean)?,
    resolvedWeekdayLabels: List<String>,
    resolvedAccessibilityLabels: List<String>,
    resolvedMonthFormatter: (Int) -> String,
    expandSelectionToLabel: Boolean,
    selectionBackgroundColor: Color?,
    selectionContentColor: Color?,
    trailingContent: @Composable ((LocalDate, Boolean) -> Unit)?,
    onDateTap: (LocalDate) -> Unit,
) {
    val isInHeaderMonth = date.yearMonth == headerMonth

    val isEnabled = remember(date, state.minDate, state.maxDate, enabledDates) {
        isDateEnabled(
            date = date,
            minDate = state.minDate,
            maxDate = state.maxDate,
            enabledDates = enabledDates,
        )
    }

    val weekdayLabel = remember(date, resolvedWeekdayLabels) {
        resolveWeekdayLabel(
            dayOfWeek = date.dayOfWeek,
            weekdayLabels = resolvedWeekdayLabels,
        )
    }

    val accessibilityDescription = remember(
        date,
        resolvedAccessibilityLabels,
        resolvedMonthFormatter,
    ) {
        buildCellContentDescription(
            date = date,
            weekdayLabels = resolvedAccessibilityLabels,
            monthFormatter = resolvedMonthFormatter,
        )
    }

    val isSelected = date == state.selectedDate
    CalendarDayCell(
        modifier = Modifier.width(cellWidth),
        text = "${date.day}",
        contentDescription = accessibilityDescription,
        isCurrent = date == today,
        isSelected = isSelected,
        isEnabled = isEnabled,
        isOutsideVisibleRange = !isInHeaderMonth,
        isInsideSelectedRange = false,
        showWeekdayLabel = true,
        weekdayLabel = weekdayLabel,
        expandSelectionToLabel = expandSelectionToLabel,
        selectionBackgroundColor = selectionBackgroundColor,
        selectionContentColor = selectionContentColor,
        onClick = { onDateTap(date) },
        trailingContent = trailingContent?.let { content ->
            {
                content(date, isSelected)
            }
        },
    )
}
