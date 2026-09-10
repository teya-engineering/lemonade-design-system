package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics

/**
 * Renders a calendar date cell styled by its selection, today, disabled and outside-month state.
 *
 * Backs both the [LemonadeUi.DatePicker] grid and the inline calendar strip. A dot appears below
 * the text when [isCurrent] is true.
 *
 * @param text display text, typically the day-of-month number
 * @param isCurrent whether this cell represents today's date
 * @param isSelected whether this cell is the selected date
 * @param isEnabled whether the cell is interactive
 * @param isOutsideVisibleRange whether the date falls outside the displayed month
 * @param isInsideSelectedRange whether the cell is within a selected date range
 * @param onClick called when the cell is tapped
 * @param modifier [Modifier] applied to the root container
 * @param contentDescription when non-null, what screen readers announce instead of the bare [text]
 * @param showSelectionBackground whether to draw the selection background inside this cell; pass
 *   `false` when the parent supplies its own selection styling, as [CalendarDayCell] does
 * @param showTodayIndicator whether to draw the today dot; follows [showSelectionBackground]
 * @param selectionContentColor when non-null and the cell [isSelected], overrides the text color
 *   used on selected cells
 * @param interactionSource interaction source for ripple/focus handling
 */
@Composable
internal fun ContentCell(
    text: String,
    isCurrent: Boolean,
    isSelected: Boolean,
    isEnabled: Boolean,
    isOutsideVisibleRange: Boolean,
    isInsideSelectedRange: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    showSelectionBackground: Boolean = true,
    showTodayIndicator: Boolean = showSelectionBackground,
    selectionContentColor: Color? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textColor = contentCellTextColor(
        isEnabled = isEnabled,
        isSelected = isSelected,
        isCurrent = isCurrent,
        isInsideSelectedRange = isInsideSelectedRange,
        isOutsideVisibleRange = isOutsideVisibleRange,
        showSelectionBackground = showSelectionBackground,
        selectionContentColor = selectionContentColor,
    )

    val textStyle = if (isCurrent) {
        LocalTypographies.current.bodyMediumSemiBold
    } else {
        LocalTypographies.current.bodyMediumMedium
    }

    val backgroundColor = if (isSelected && showSelectionBackground) {
        LocalColors.current.interaction.bgBrandInteractive
    } else {
        Color.Transparent
    }

    val cellShape = LocalShapes.current.radius200

    Box(
        modifier = modifier
            .then(
                other = if (isFocused) {
                    Modifier
                        .border(
                            width = LocalBorderWidths.current.base.border50,
                            color = LocalColors.current.border.borderSelected,
                            shape = cellShape,
                        )
                } else {
                    Modifier
                },
            ).clip(
                shape = cellShape,
            ).clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                role = Role.Button,
                enabled = isEnabled,
                indication = LocalEffects.current.interactionIndication,
            ).semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
                selected = isSelected
                if (!isEnabled) {
                    disabled()
                }
            }.background(
                color = backgroundColor,
            ),
        contentAlignment = Alignment.Center,
    ) {
        LemonadeUi.Text(
            modifier = Modifier.padding(vertical = LocalSpaces.current.spacing200),
            text = text,
            textStyle = textStyle,
            color = textColor,
        )

        if (isCurrent && showTodayIndicator) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = LocalSpaces.current.spacing100)
                    .size(LocalSpaces.current.spacing100)
                    .background(
                        color = textColor,
                        shape = LocalShapes.current.radiusFull,
                    ),
            )
        }
    }
}

@Composable
private fun contentCellTextColor(
    isEnabled: Boolean,
    isSelected: Boolean,
    isCurrent: Boolean,
    isInsideSelectedRange: Boolean,
    isOutsideVisibleRange: Boolean,
    showSelectionBackground: Boolean,
    selectionContentColor: Color?,
): Color =
    when {
        !isEnabled -> LocalColors.current.content.contentTertiary
        isSelected && selectionContentColor != null -> selectionContentColor
        isSelected && !showSelectionBackground -> LocalColors.current.content.contentOnBrandHigh
        isSelected -> LocalColors.current.content.contentOnBrandHigh
        isInsideSelectedRange -> LocalColors.current.content.contentPrimary
        isCurrent -> LocalColors.current.content.contentBrand
        isOutsideVisibleRange -> LocalColors.current.content.contentSecondary
        else -> LocalColors.current.content.contentPrimary
    }
