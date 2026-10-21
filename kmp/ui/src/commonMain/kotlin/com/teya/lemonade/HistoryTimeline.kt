package com.teya.lemonade

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.HistoryItemVoice
import com.teya.lemonade.core.HistoryTimelineVariant
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private val HistoryItemIndicatorWidth: Dp = 20.dp
private val HistoryItemDotSize: Dp = 10.dp
private val HistoryItemDotTopOffset: Dp = 7.dp
private val HistoryItemDotLineGap: Dp = 6.dp
private val HistoryStepsDotAreaHeight: Dp = 24.dp
private val HistoryStepsIconTopOffset: Dp = 2.dp
private val HistoryStepsRingSize: Dp = 16.dp
private val HistoryStepsActiveDotSize: Dp = 8.dp
private val HistoryStepsLineInset: Dp = 2.dp
private val HistoryStepsDashPeriod: Dp = 10.dp
private const val HISTORY_STEPS_DASH_FRACTION: Float = 0.4f
private const val HISTORY_ACTIVE_LINE_FADE_START_MILLIS: Int = 1800
private const val HISTORY_ACTIVE_LINE_FILL_MILLIS: Int = 2400
private const val HISTORY_ACTIVE_LINE_CYCLE_MILLIS: Int = 3000

/**
 * Describes a single row inside a [LemonadeUi.HistoryTimeline].
 *
 * @param label primary row text
 * @param subheading optional secondary text rendered immediately below [label]
 * @param description optional tertiary paragraph text rendered below the subheading
 * @param voice semantic color of the indicator dot when this row is the current step. In a
 *   [HistoryTimelineVariant.Steps] timeline, [HistoryItemVoice.Critical] marks a reached step
 *   as failed; other voices are ignored
 * @param contentSlot optional slot for custom composable content (e.g. a button or tag)
 *   rendered below the description
 */
public data class HistoryTimelineItem(
    public val label: String,
    public val subheading: String? = null,
    public val description: String? = null,
    public val voice: HistoryItemVoice = HistoryItemVoice.Neutral,
    public val contentSlot: (@Composable ColumnScope.() -> Unit)? = null,
)

/**
 * A vertical list of history steps with a left-aligned indicator column. The current row uses
 * the item's [HistoryItemVoice] colour; past rows render muted. A connecting line joins every
 * row except the last.
 *
 * Set [variant] to [HistoryTimelineVariant.Steps] for a stepper: rows before the current one
 * show a check, the current row shows an in-progress ring, and upcoming rows are muted with a
 * dashed line. A reached row with [HistoryItemVoice.Critical] shows an error icon instead.
 *
 * For rows with custom content, set [HistoryTimelineItem.contentSlot].
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.HistoryTimeline(
 *     items = listOf(
 *         HistoryTimelineItem(
 *             label = "Find a Visa PayPoint next to you",
 *             description = "PayPoint locations collect cash deposits on our behalf",
 *             voice = HistoryItemVoice.Positive,
 *             contentSlot = {
 *                 LemonadeUi.Button(
 *                     label = "Find a PayPoint",
 *                     onClick = { /* ... */ },
 *                 )
 *             },
 *         ),
 *         HistoryTimelineItem(
 *             label = "Enter the amount and generate a barcode",
 *             description = "Show the barcode to the shopkeeper and deposit the funds",
 *         ),
 *     ),
 *     currentIndex = 0,
 * )
 *
 * LemonadeUi.HistoryTimeline(
 *     items = listOf(
 *         HistoryTimelineItem(label = "Invitation accepted"),
 *         HistoryTimelineItem(label = "Logs into the app"),
 *         HistoryTimelineItem(label = "Card activation"),
 *     ),
 *     currentIndex = 1,
 *     variant = HistoryTimelineVariant.Steps,
 * )
 * ```
 *
 * @param items ordered list of [HistoryTimelineItem]s to display
 * @param modifier [Modifier] applied to the root column
 * @param currentIndex index of the row to render as the current step. Pass `null` to render
 *   every row in its non-current (muted) state. Defaults to `0` (first row is current). In a
 *   [HistoryTimelineVariant.Steps] timeline, an index past the last row shows every step completed
 * @param variant how rows around the current step are styled
 */
@Composable
public fun LemonadeUi.HistoryTimeline(
    items: List<HistoryTimelineItem>,
    modifier: Modifier = Modifier,
    currentIndex: Int? = 0,
    variant: HistoryTimelineVariant = HistoryTimelineVariant.Events,
) {
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val state = historyItemState(
                index = index,
                currentIndex = currentIndex,
                voice = item.voice,
                variant = variant,
            )
            CoreHistoryTimelineItem(
                item = item,
                state = state,
                stepStatus = historyStepStatus(
                    state = state,
                    index = index,
                    count = items.size,
                ),
                isLast = index == items.lastIndex,
            )
        }
    }
}

@Deprecated(
    message = "Use the overload with a variant parameter.",
    replaceWith = ReplaceWith(
        expression = "HistoryTimeline(items, modifier, currentIndex, HistoryTimelineVariant.Events)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.HistoryTimeline(
    items: List<HistoryTimelineItem>,
    modifier: Modifier = Modifier,
    currentIndex: Int? = 0,
) {
    HistoryTimeline(
        items = items,
        modifier = modifier,
        currentIndex = currentIndex,
        variant = HistoryTimelineVariant.Events,
    )
}

private enum class HistoryItemState {
    Current,
    Inactive,
    Completed,
    Error,
    Active,
    Next,
}

private fun historyItemState(
    index: Int,
    currentIndex: Int?,
    voice: HistoryItemVoice,
    variant: HistoryTimelineVariant,
): HistoryItemState =
    when (variant) {
        HistoryTimelineVariant.Events -> if (index == currentIndex) {
            HistoryItemState.Current
        } else {
            HistoryItemState.Inactive
        }

        HistoryTimelineVariant.Steps -> when {
            currentIndex == null || index > currentIndex -> HistoryItemState.Next
            voice == HistoryItemVoice.Critical -> HistoryItemState.Error
            index == currentIndex -> HistoryItemState.Active
            else -> HistoryItemState.Completed
        }
    }

private fun historyStepStatus(
    state: HistoryItemState,
    index: Int,
    count: Int,
): String? {
    val status = when (state) {
        HistoryItemState.Completed -> "Completed"
        HistoryItemState.Error -> "Failed"
        HistoryItemState.Active -> "Current"
        HistoryItemState.Next -> "Upcoming"
        HistoryItemState.Current, HistoryItemState.Inactive -> return null
    }
    return "$status, step ${index + 1} of $count"
}

@Composable
private fun CoreHistoryTimelineItem(
    item: HistoryTimelineItem,
    state: HistoryItemState,
    stepStatus: String?,
    isLast: Boolean,
) {
    val spaces = LocalSpaces.current
    val borderWidths = LocalBorderWidths.current
    val typographies = LocalTypographies.current
    val colors = LocalColors.current
    val contentColors = colors.content
    val activeLineElapsedMillis: State<Float>? = if (state == HistoryItemState.Active && !isLast) {
        rememberActiveLineElapsedMillis()
    } else {
        null
    }
    val dotColor: Color = if (state == HistoryItemState.Current) {
        item.voice.currentDotColor
    } else {
        colors.border.borderNeutralMedium
    }
    val labelColor: Color = if (state == HistoryItemState.Next) {
        contentColors.contentTertiary
    } else {
        contentColors.contentPrimary
    }

    Row(
        modifier = Modifier.drawBehind {
            when (state) {
                HistoryItemState.Current, HistoryItemState.Inactive -> drawEventsIndicator(
                    dotColor = dotColor,
                    lineColor = colors.border.borderNeutralMedium,
                    lineWidth = borderWidths.base.border40.toPx(),
                    isLast = isLast,
                )

                HistoryItemState.Completed, HistoryItemState.Error, HistoryItemState.Active,
                HistoryItemState.Next,
                -> drawStepsIndicator(
                    state = state,
                    isLast = isLast,
                    colors = colors,
                    borderWidths = borderWidths,
                    activeLineElapsedMillis = activeLineElapsedMillis?.value,
                )
            }
        },
        horizontalArrangement = Arrangement.spacedBy(space = spaces.spacing300),
    ) {
        Box(
            modifier = Modifier.width(width = HistoryItemIndicatorWidth),
            contentAlignment = Alignment.TopCenter,
        ) {
            HistoryStepIcon(state = state)
        }

        Column(
            modifier = Modifier.padding(bottom = if (isLast) 0.dp else spaces.spacing600),
            verticalArrangement = Arrangement.spacedBy(space = spaces.spacing200),
        ) {
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {
                    if (stepStatus != null) {
                        stateDescription = stepStatus
                    }
                },
                verticalArrangement = Arrangement.spacedBy(space = spaces.spacing200),
            ) {
                Column {
                    LemonadeUi.Text(
                        text = item.label,
                        textStyle = typographies.bodyMediumMedium,
                        color = labelColor,
                    )

                    val subheading = item.subheading
                    if (subheading != null) {
                        LemonadeUi.Text(
                            text = subheading,
                            textStyle = typographies.bodySmallRegular,
                            color = contentColors.contentSecondary,
                        )
                    }
                }

                val description = item.description
                if (description != null) {
                    LemonadeUi.Text(
                        text = description,
                        textStyle = typographies.bodySmallRegular,
                        color = contentColors.contentSecondary,
                    )
                }
            }

            val contentSlot = item.contentSlot
            if (contentSlot != null) {
                contentSlot()
            }
        }
    }
}

@Composable
private fun HistoryStepIcon(state: HistoryItemState) {
    val colors = LocalColors.current
    val stepIcon = when (state) {
        HistoryItemState.Completed -> LemonadeIcons.CircleCheckSolid to colors.background.bgPositive
        HistoryItemState.Error -> LemonadeIcons.CircleXSolid to colors.background.bgCritical
        else -> null
    }
    if (stepIcon != null) {
        LemonadeUi.Icon(
            icon = stepIcon.first,
            contentDescription = null,
            size = LemonadeAssetSize.Medium,
            tint = stepIcon.second,
            modifier = Modifier.padding(top = HistoryStepsIconTopOffset),
        )
    }
}

private fun DrawScope.indicatorCenterX(): Float {
    val indicatorCenterX = HistoryItemIndicatorWidth.toPx() / 2f
    return when (layoutDirection) {
        LayoutDirection.Ltr -> indicatorCenterX
        LayoutDirection.Rtl -> size.width - indicatorCenterX
    }
}

private fun DrawScope.drawEventsIndicator(
    dotColor: Color,
    lineColor: Color,
    lineWidth: Float,
    isLast: Boolean,
) {
    val centerX = indicatorCenterX()
    val dotRadius = HistoryItemDotSize.toPx() / 2f
    val dotCenterY = HistoryItemDotTopOffset.toPx() + dotRadius
    drawCircle(
        color = dotColor,
        radius = dotRadius,
        center = Offset(
            x = centerX,
            y = dotCenterY,
        ),
    )
    if (!isLast) {
        drawLine(
            color = lineColor,
            start = Offset(
                x = centerX,
                y = dotCenterY + dotRadius + HistoryItemDotLineGap.toPx(),
            ),
            end = Offset(
                x = centerX,
                y = size.height,
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawStepsIndicator(
    state: HistoryItemState,
    isLast: Boolean,
    colors: LemonadeSemanticColors,
    borderWidths: LemonadeBorderWidth,
    activeLineElapsedMillis: Float?,
) {
    val centerX = indicatorCenterX()
    val ringCenter = Offset(
        x = centerX,
        y = HistoryStepsDotAreaHeight.toPx() / 2f,
    )
    if (state == HistoryItemState.Active || state == HistoryItemState.Next) {
        val ringRadius = HistoryStepsRingSize.toPx() / 2f
        val ringWidth = borderWidths.base.border40.toPx()
        drawCircle(
            color = colors.background.bgDefault,
            radius = ringRadius,
            center = ringCenter,
        )
        drawCircle(
            color = colors.border.borderNeutralHigh,
            radius = ringRadius - ringWidth / 2f,
            center = ringCenter,
            style = Stroke(width = ringWidth),
        )
    }
    if (state == HistoryItemState.Active) {
        drawCircle(
            color = colors.background.bgNeutral,
            radius = HistoryStepsActiveDotSize.toPx() / 2f,
            center = ringCenter,
        )
    }
    if (!isLast) {
        drawStepsLine(
            state = state,
            x = centerX,
            colors = colors,
            lineWidth = borderWidths.base.border50.toPx(),
            activeLineElapsedMillis = activeLineElapsedMillis,
        )
    }
}

private fun DrawScope.drawStepsLine(
    state: HistoryItemState,
    x: Float,
    colors: LemonadeSemanticColors,
    lineWidth: Float,
    activeLineElapsedMillis: Float?,
) {
    val lineTop = HistoryStepsDotAreaHeight.toPx() + HistoryStepsLineInset.toPx()
    val lineBottom = size.height - HistoryStepsLineInset.toPx()
    if (lineBottom <= lineTop) return
    if (state == HistoryItemState.Next) {
        drawDashedStepsLine(
            color = colors.border.borderNeutralHigh,
            x = x,
            top = lineTop,
            bottom = lineBottom,
            strokeWidth = lineWidth,
        )
    } else {
        drawLine(
            color = colors.border.borderNeutralMedium,
            start = Offset(x = x, y = lineTop),
            end = Offset(x = x, y = lineBottom),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round,
        )
        if (activeLineElapsedMillis != null) {
            drawActiveStepsLine(
                color = colors.border.borderPositive,
                start = Offset(x = x, y = lineTop),
                bottom = lineBottom,
                strokeWidth = lineWidth,
                elapsedMillis = activeLineElapsedMillis,
            )
        }
    }
}

@Composable
private fun rememberActiveLineElapsedMillis(): State<Float> =
    rememberInfiniteTransition(label = "HistoryActiveLine").animateFloat(
        initialValue = 0f,
        targetValue = HISTORY_ACTIVE_LINE_CYCLE_MILLIS.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = HISTORY_ACTIVE_LINE_CYCLE_MILLIS,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "HistoryActiveLineElapsed",
    )

private fun DrawScope.drawActiveStepsLine(
    color: Color,
    start: Offset,
    bottom: Float,
    strokeWidth: Float,
    elapsedMillis: Float,
) {
    val top = start.y
    val fillFraction = min(elapsedMillis / HISTORY_ACTIVE_LINE_FILL_MILLIS, 1f)
    val end = top + (bottom - top) * EaseInOut.transform(fillFraction)
    val fadeFraction = (elapsedMillis - HISTORY_ACTIVE_LINE_FADE_START_MILLIS) /
        (HISTORY_ACTIVE_LINE_FILL_MILLIS - HISTORY_ACTIVE_LINE_FADE_START_MILLIS)
    val alpha = 1f - fadeFraction.coerceIn(minimumValue = 0f, maximumValue = 1f)
    if (end <= top || alpha <= 0f) return
    drawLine(
        brush = Brush.verticalGradient(
            colors = listOf(color, color.copy(alpha = 0f)),
            startY = top,
            endY = end,
        ),
        start = start,
        end = Offset(x = start.x, y = end),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
        alpha = alpha,
    )
}

private fun DrawScope.drawDashedStepsLine(
    color: Color,
    x: Float,
    top: Float,
    bottom: Float,
    strokeWidth: Float,
) {
    val length = bottom - top
    val periods = max(1, (length / HistoryStepsDashPeriod.toPx()).roundToInt())
    val period = length / periods
    val halfDash = period * HISTORY_STEPS_DASH_FRACTION / 2f
    for (index in 0..periods) {
        val center = top + index * period
        drawLine(
            color = color,
            start = Offset(x = x, y = max(top, center - halfDash)),
            end = Offset(x = x, y = min(bottom, center + halfDash)),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

private val HistoryItemVoice.currentDotColor: Color
    @Composable get() {
        return when (this) {
            HistoryItemVoice.Positive -> LocalColors.current.background.bgPositive
            HistoryItemVoice.Critical -> LocalColors.current.background.bgCritical
            HistoryItemVoice.Neutral -> LocalColors.current.background.bgNeutral
        }
    }

private data class HistoryItemPreviewData(
    val voice: HistoryItemVoice,
    val isCurrent: Boolean,
    val hasDescription: Boolean,
    val hasContentSlot: Boolean,
)

private class HistoryItemPreviewProvider : PreviewParameterProvider<HistoryItemPreviewData> {
    override val values: Sequence<HistoryItemPreviewData> = buildList {
        HistoryItemVoice.entries.forEach { voice ->
            listOf(true, false)
                .forEach { current ->
                    listOf(true, false)
                        .forEach { description ->
                            listOf(true, false)
                                .forEach { contentSlot ->
                                    add(
                                        HistoryItemPreviewData(
                                            voice = voice,
                                            isCurrent = current,
                                            hasDescription = description,
                                            hasContentSlot = contentSlot,
                                        ),
                                    )
                                }
                        }
                }
        }
    }.asSequence()
}

@LemonadePreview
@Composable
private fun HistoryItemPreview(
    @PreviewParameter(HistoryItemPreviewProvider::class)
    previewData: HistoryItemPreviewData,
) {
    CoreHistoryTimelineItem(
        item = HistoryTimelineItem(
            label = "Label",
            subheading = "Subheading",
            description = if (previewData.hasDescription) {
                "Description for the timeline step providing extra context."
            } else {
                null
            },
            voice = previewData.voice,
            contentSlot = if (previewData.hasContentSlot) {
                {
                    LemonadeUi.Text(
                        text = "Custom slot content",
                        textStyle = LocalTypographies.current.bodySmallRegular,
                        color = LocalColors.current.content.contentSecondary,
                    )
                }
            } else {
                null
            },
        ),
        state = if (previewData.isCurrent) HistoryItemState.Current else HistoryItemState.Inactive,
        stepStatus = null,
        isLast = false,
    )
}

@LemonadePreview
@Composable
private fun HistoryTimelinePreview() {
    LemonadeUi.HistoryTimeline(
        items = listOf(
            HistoryTimelineItem(
                label = "Find a Visa PayPoint next to you",
                description = "PayPoint locations collect cash deposits on our behalf",
                voice = HistoryItemVoice.Positive,
            ),
            HistoryTimelineItem(
                label = "Enter the amount and generate a barcode",
                description = "Show the barcode to the shopkeeper and deposit the funds",
            ),
            HistoryTimelineItem(
                label = "After deposit, your money will be available in 10 minutes",
                description = "We'll notify you once the funds are in your account",
            ),
        ),
        currentIndex = 0,
    )
}

@LemonadePreview
@Composable
private fun HistoryTimelineStepsPreview() {
    LemonadeUi.HistoryTimeline(
        items = listOf(
            HistoryTimelineItem(
                label = "Invitation accepted",
                voice = HistoryItemVoice.Positive,
            ),
            HistoryTimelineItem(
                label = "Logs into the Teya App",
                subheading = "Sofia needs to download the Teya app and log in.",
            ),
            HistoryTimelineItem(
                label = "Card activation",
            ),
        ),
        currentIndex = 1,
        variant = HistoryTimelineVariant.Steps,
    )
}
