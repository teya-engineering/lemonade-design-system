package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.DayOfWeek
import platform.Foundation.NSCalendar

@Composable
internal actual fun rememberSystemFirstDayOfWeek(): DayOfWeek =
    remember { sundayBasedWeekday(NSCalendar.currentCalendar.firstWeekday.toInt()) }
