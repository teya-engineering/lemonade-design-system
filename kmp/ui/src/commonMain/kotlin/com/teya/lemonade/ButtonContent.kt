package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * The labeled button's content: the label, with optional leading and trailing icons that track
 * the reader's text size and stack over and under the label once it runs out of width beside
 * them.
 */
@Composable
internal fun ButtonAdaptiveContent(
    label: String,
    textStyle: LemonadeTextStyle,
    contentColor: Color,
    leadingIcon: LemonadeIcons?,
    trailingIcon: LemonadeIcons?,
    modifier: Modifier = Modifier,
) {
    val stackedGapPx = with(LocalDensity.current) {
        LocalSpaces.current.spacing200.roundToPx()
    }
    Layout(
        contents = listOf(
            {
                if (leadingIcon != null) {
                    LemonadeUi.TextTrackedIcon(
                        icon = leadingIcon,
                        tint = contentColor,
                        contentDescription = null,
                    )
                }
            },
            {
                LemonadeUi.Text(
                    text = label,
                    textStyle = textStyle,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing200),
                )
            },
            {
                if (trailingIcon != null) {
                    LemonadeUi.TextTrackedIcon(
                        icon = trailingIcon,
                        tint = contentColor,
                        contentDescription = null,
                    )
                }
            },
        ),
        modifier = modifier,
        measurePolicy = ButtonContentMeasurePolicy(stackedGapPx = stackedGapPx),
    )
}

/** Measures the icons and the label, then places them beside each other or stacked. */
internal class ButtonContentMeasurePolicy(
    private val stackedGapPx: Int,
) : MultiContentMeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints,
    ): MeasureResult {
        val leading = measurables[0].firstOrNull()?.measure(Constraints())
        val trailing = measurables[2].firstOrNull()?.measure(Constraints())
        val labelMeasurable = measurables[1].first()
        val iconsWidth = (leading?.width ?: 0) + (trailing?.width ?: 0)

        val labelMinWidth = if (constraints.hasBoundedWidth && iconsWidth > 0) {
            labelMeasurable.minIntrinsicWidth(height = constraints.maxHeight)
        } else {
            0
        }
        val stacked = shouldStackButtonContent(
            hasBoundedWidth = constraints.hasBoundedWidth,
            availableWidthPx = constraints.maxWidth,
            iconsWidthPx = iconsWidth,
            labelMinIntrinsicWidthPx = labelMinWidth,
        )

        val label = labelMeasurable.measure(
            if (!stacked && constraints.hasBoundedWidth) {
                constraints.copy(
                    minWidth = 0,
                    maxWidth = (constraints.maxWidth - iconsWidth).coerceAtLeast(0),
                )
            } else {
                constraints.copy(minWidth = 0)
            },
        )

        return if (stacked) {
            placeStacked(
                leading = leading,
                label = label,
                trailing = trailing,
                constraints = constraints,
            )
        } else {
            placeRow(
                leading = leading,
                label = label,
                trailing = trailing,
                constraints = constraints,
            )
        }
    }

    private fun MeasureScope.placeRow(
        leading: Placeable?,
        label: Placeable,
        trailing: Placeable?,
        constraints: Constraints,
    ): MeasureResult {
        val children = listOfNotNull(leading, label, trailing)
        val width = constraints.constrainWidth(width = children.sumOf { child -> child.width })
        val height = constraints.constrainHeight(height = children.maxOf { child -> child.height })
        return layout(width = width, height = height) {
            var x = 0
            children.forEach { child ->
                child.placeRelative(
                    x = x,
                    y = Alignment.CenterVertically.align(size = child.height, space = height),
                )
                x += child.width
            }
        }
    }

    private fun MeasureScope.placeStacked(
        leading: Placeable?,
        label: Placeable,
        trailing: Placeable?,
        constraints: Constraints,
    ): MeasureResult {
        val children = listOfNotNull(leading, label, trailing)
        val contentHeight = children.sumOf { child -> child.height } +
            stackedGapPx * (children.size - 1)
        val width = constraints.constrainWidth(width = children.maxOf { child -> child.width })
        val height = constraints.constrainHeight(height = contentHeight)
        return layout(width = width, height = height) {
            var y = 0
            children.forEach { child ->
                child.placeRelative(
                    x = (width - child.width) / 2,
                    y = y,
                )
                y += child.height + stackedGapPx
            }
        }
    }
}

/** True when the label no longer has room for its longest unbreakable word beside the icons. */
internal fun shouldStackButtonContent(
    hasBoundedWidth: Boolean,
    availableWidthPx: Int,
    iconsWidthPx: Int,
    labelMinIntrinsicWidthPx: Int,
): Boolean =
    hasBoundedWidth &&
        iconsWidthPx > 0 &&
        availableWidthPx - iconsWidthPx < labelMinIntrinsicWidthPx
