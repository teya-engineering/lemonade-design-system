package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.DayOfWeek
import java.util.Calendar

@Composable
internal actual fun rememberSystemFirstDayOfWeek(): DayOfWeek = remember { systemFirstDayOfWeek() }

private fun systemFirstDayOfWeek(): DayOfWeek = sundayBasedWeekday(Calendar.getInstance().firstDayOfWeek)
