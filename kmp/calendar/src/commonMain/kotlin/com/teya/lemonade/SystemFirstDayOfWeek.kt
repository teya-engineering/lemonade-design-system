package com.teya.lemonade

import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek

/**
 * The first day of the week the device's user expects, read from the system region and, where the
 * platform has one, the user's first-day-of-week preference.
 *
 * Reads the system locale rather than the app's, so an app that sets its own language does not
 * lose the user's region.
 */
@Composable
internal expect fun rememberSystemFirstDayOfWeek(): DayOfWeek

/** Maps a Sunday-based weekday number (1 = Sunday … 7 = Saturday) to a [DayOfWeek]. */
internal fun sundayBasedWeekday(weekday: Int): DayOfWeek {
    if (weekday == 1) return DayOfWeek.SUNDAY
    return DayOfWeek(isoDayNumber = weekday - 1)
}
