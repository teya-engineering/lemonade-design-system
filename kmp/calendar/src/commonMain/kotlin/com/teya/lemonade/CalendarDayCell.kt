package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

/**
 * Renders one day cell with an optional weekday label above the [ContentCell] and a trailing slot
 * below.
 *
 * Backs both the [LemonadeUi.DatePicker] month grid and the [LemonadeUi.InlineCalendar] strip.
 *
 * @param text day-of-month text (e.g. "14")
 * @param isCurrent whether the date is today
 * @param isSelected whether the date is currently selected
 * @param isEnabled whether the cell is interactive
 * @param isOutsideVisibleRange whether the date falls outside the displayed month
 * @param isInsideSelectedRange whether the date is within a selected range
 * @param onClick called when the cell is tapped
 * @param modifier [Modifier] for the root container
 * @param contentDescription when non-null, what screen readers announce instead of the bare day
 *   number
 * @param showWeekdayLabel whether to show the weekday label above the day number
 * @param weekdayLabel short weekday text (e.g. "M", "Mon")
 * @param expandSelectionToLabel when `true` (default), the selection background covers the whole
 *   cell column including the weekday label and trailing content; when `false`, only the day
 *   number circle carries the brand background ([LemonadeUi.DatePicker] style)
 * @param selectionBackgroundColor when non-null, overrides the brand background color of selected
 *   cells
 * @param selectionContentColor when non-null, overrides the text color on selected cells; also
 *   applied to the weekday label when [expandSelectionToLabel] is `true`
 * @param trailingContent composable rendered below the day cell
 */
@Composable
internal fun CalendarDayCell(
    text: String,
    isCurrent: Boolean,
    isSelected: Boolean,
    isEnabled: Boolean,
    isOutsideVisibleRange: Boolean,
    isInsideSelectedRange: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    showWeekdayLabel: Boolean = true,
    weekdayLabel: String = "",
    expandSelectionToLabel: Boolean = true,
    selectionBackgroundColor: Color? = null,
    selectionContentColor: Color? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val selectionShape = LocalShapes.current.radius200
    val resolvedSelectionBgColor = selectionBackgroundColor
        ?: LocalColors.current.interaction.bgBrandInteractive
    val weekdayLabelColor = resolveWeekdayLabelColor(
        isSelected = isSelected,
        isEnabled = isEnabled,
        expandSelectionToLabel = expandSelectionToLabel,
        selectionContentColor = selectionContentColor,
    )

    Column(
        modifier = modifier
            .then(
                expandedSelectionModifier(
                    isSelected = isSelected,
                    expandSelectionToLabel = expandSelectionToLabel,
                    selectionShape = selectionShape,
                    selectionBgColor = resolvedSelectionBgColor,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showWeekdayLabel && weekdayLabel.isNotEmpty()) {
            LemonadeUi.Text(
                text = weekdayLabel,
                textStyle = LocalTypographies.current.bodyXSmallOverline,
                color = weekdayLabelColor,
            )
        }

        Column(
            modifier = compactSelectionModifier(
                isSelected = isSelected,
                expandSelectionToLabel = expandSelectionToLabel,
                selectionShape = selectionShape,
                selectionBgColor = resolvedSelectionBgColor,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ContentCell(
                text = text,
                isCurrent = isCurrent,
                isSelected = isSelected,
                isEnabled = isEnabled,
                isOutsideVisibleRange = isOutsideVisibleRange,
                isInsideSelectedRange = isInsideSelectedRange,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = contentDescription,
                showSelectionBackground = false,
                showTodayIndicator = false,
                selectionContentColor = selectionContentColor,
            )

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Composable
private fun resolveWeekdayLabelColor(
    isSelected: Boolean,
    isEnabled: Boolean,
    expandSelectionToLabel: Boolean,
    selectionContentColor: Color?,
): Color {
    val hasExpandedSelection = isSelected && expandSelectionToLabel
    return when {
        hasExpandedSelection && selectionContentColor != null -> selectionContentColor
        hasExpandedSelection -> LocalColors.current.content.contentOnBrandHigh
        !isEnabled -> LocalColors.current.content.contentTertiary
        else -> LocalColors.current.content.contentPrimary
    }
}

@Composable
private fun expandedSelectionModifier(
    isSelected: Boolean,
    expandSelectionToLabel: Boolean,
    selectionShape: androidx.compose.ui.graphics.Shape,
    selectionBgColor: Color,
): Modifier =
    if (isSelected && expandSelectionToLabel) {
        Modifier
            .clip(selectionShape)
            .background(
                color = selectionBgColor,
                shape = selectionShape,
            ).padding(vertical = LocalSpaces.current.spacing100)
    } else {
        Modifier
            .padding(vertical = LocalSpaces.current.spacing100)
    }

@Composable
private fun compactSelectionModifier(
    isSelected: Boolean,
    expandSelectionToLabel: Boolean,
    selectionShape: androidx.compose.ui.graphics.Shape,
    selectionBgColor: Color,
): Modifier =
    when {
        isSelected && !expandSelectionToLabel ->
            Modifier
                .clip(selectionShape)
                .background(
                    color = selectionBgColor,
                    shape = selectionShape,
                ).padding(
                    horizontal = LocalSpaces.current.spacing100,
                    vertical = LocalSpaces.current.spacing100,
                )
        !expandSelectionToLabel ->
            Modifier.padding(
                horizontal = LocalSpaces.current.spacing100,
                vertical = LocalSpaces.current.spacing100,
            )
        else ->
            Modifier
    }
