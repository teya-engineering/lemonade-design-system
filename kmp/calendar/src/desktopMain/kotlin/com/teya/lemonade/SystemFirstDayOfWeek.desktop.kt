package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.DayOfWeek
import java.util.Calendar

@Composable
internal actual fun rememberSystemFirstDayOfWeek(): DayOfWeek =
    remember { sundayBasedWeekday(Calendar.getInstance().firstDayOfWeek) }
