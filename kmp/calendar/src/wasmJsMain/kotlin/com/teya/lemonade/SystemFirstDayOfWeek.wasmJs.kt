package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.DayOfWeek

@Composable
internal actual fun rememberSystemFirstDayOfWeek(): DayOfWeek =
    remember { DayOfWeek(isoDayNumber = browserFirstDayOfWeek()) }

/** ISO first day of the week (1 = Monday … 7 = Sunday) for the browser's language; Sunday when the browser cannot tell. */
private fun browserFirstDayOfWeek(): Int = js(
    "(() => { try { const locale = new Intl.Locale(navigator.language); " +
        "const info = typeof locale.getWeekInfo === 'function' ? locale.getWeekInfo() : locale.weekInfo; " +
        "return info && info.firstDay ? info.firstDay : 7; } catch (e) { return 7; } })()",
)
