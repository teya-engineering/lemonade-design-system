package com.teya.lemonade

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.LocaleManagerCompat
import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek
import kotlinx.datetime.DayOfWeek

@Composable
internal actual fun rememberSystemFirstDayOfWeek(): DayOfWeek {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration) { systemFirstDayOfWeek(context) }
}

private fun systemFirstDayOfWeek(context: Context): DayOfWeek {
    val systemLocale = LocaleManagerCompat.getSystemLocales(context)[0]
    val firstDay = if (systemLocale != null) {
        LocalePreferences.getFirstDayOfWeek(systemLocale)
    } else {
        LocalePreferences.getFirstDayOfWeek()
    }
    return when (firstDay) {
        FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
        FirstDayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
        FirstDayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
        FirstDayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
        FirstDayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
        FirstDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
        else -> DayOfWeek.SUNDAY
    }
}
