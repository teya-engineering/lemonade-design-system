package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeIcons

/**
 * Shows the month/year label with previous and next navigation arrows.
 *
 * Backs both [LemonadeUi.DatePicker] and [LemonadeUi.InlineCalendar].
 *
 * @param headerLabel formatted month-year string shown in the center
 * @param canGoPrev whether the "previous month" arrow is enabled
 * @param canGoNext whether the "next month" arrow is enabled
 * @param onPrev called when the user taps the previous-month arrow
 * @param onNext called when the user taps the next-month arrow
 * @param modifier [Modifier] for layout adjustments
 * @param prevMonthContentDescription accessibility label for the previous-month button; defaults to
 *   the English "Previous month", so callers should pass a localized string
 * @param nextMonthContentDescription accessibility label for the next-month button; defaults to the
 *   English "Next month", so callers should pass a localized string
 */
@Composable
internal fun CalendarMonthHeader(
    headerLabel: String,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    prevMonthContentDescription: String = "Previous month",
    nextMonthContentDescription: String = "Next month",
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.IconButton(
            icon = LemonadeIcons.ChevronLeft,
            contentDescription = prevMonthContentDescription,
            type = LemonadeButtonType.Ghost,
            enabled = canGoPrev,
            onClick = onPrev,
        )

        LemonadeUi.Text(
            text = headerLabel,
            textStyle = LocalTypographies.current.bodySmallSemiBold,
            color = LocalColors.current.content.contentPrimary,
        )

        LemonadeUi.IconButton(
            icon = LemonadeIcons.ChevronRight,
            contentDescription = nextMonthContentDescription,
            type = LemonadeButtonType.Ghost,
            enabled = canGoNext,
            onClick = onNext,
        )
    }
}
