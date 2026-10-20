package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.HistoryItemVoice
import com.teya.lemonade.core.HistoryTimelineVariant

private val HistoryItemIndicatorWidth: Dp = 20.dp
private val HistoryItemDotSize: Dp = 10.dp
private val HistoryItemDotTopOffset: Dp = 7.dp
private val HistoryItemDotLineGap: Dp = 6.dp

/**
 * Describes a single row inside a [LemonadeUi.HistoryTimeline].
 *
 * @param label primary row text
 * @param subheading optional secondary text rendered immediately below [label]
 * @param description optional tertiary paragraph text rendered below the subheading
 * @param voice semantic color of the indicator dot when this row is the current step, or a
 *   completed step in a [HistoryTimelineVariant.Progress] timeline
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
 * Set [variant] to [HistoryTimelineVariant.Progress] for a stepper: completed rows keep their
 * voice colour (typically [HistoryItemVoice.Positive]) and upcoming rows are muted.
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
 *         HistoryTimelineItem(label = "Invitation accepted", voice = HistoryItemVoice.Positive),
 *         HistoryTimelineItem(label = "Logs into the app"),
 *         HistoryTimelineItem(label = "Card activation"),
 *     ),
 *     currentIndex = 1,
 *     variant = HistoryTimelineVariant.Progress,
 * )
 * ```
 *
 * @param items ordered list of [HistoryTimelineItem]s to display
 * @param modifier [Modifier] applied to the root column
 * @param currentIndex index of the row to render as the current step. Pass `null` to render
 *   every row in its non-current (muted) state. Defaults to `0` (first row is current). In a
 *   [HistoryTimelineVariant.Progress] timeline, an index past the last row shows every step completed
 * @param variant how rows around the current step are styled
 */
@Composable
public fun LemonadeUi.HistoryTimeline(
    items: List<HistoryTimelineItem>,
    modifier: Modifier = Modifier,
    currentIndex: Int? = 0,
    variant: HistoryTimelineVariant = HistoryTimelineVariant.Default,
) {
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            CoreHistoryTimelineItem(
                item = item,
                state = historyItemState(
                    index = index,
                    currentIndex = currentIndex,
                    variant = variant,
                ),
                isLast = index == items.lastIndex,
            )
        }
    }
}

@Deprecated(
    message = "Use the overload with a variant parameter.",
    replaceWith = ReplaceWith(
        expression = "HistoryTimeline(items, modifier, currentIndex, HistoryTimelineVariant.Default)",
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
        variant = HistoryTimelineVariant.Default,
    )
}

private enum class HistoryItemState {
    Current,
    Inactive,
    Reached,
    Upcoming,
}

private fun historyItemState(
    index: Int,
    currentIndex: Int?,
    variant: HistoryTimelineVariant,
): HistoryItemState =
    when (variant) {
        HistoryTimelineVariant.Default -> if (index == currentIndex) {
            HistoryItemState.Current
        } else {
            HistoryItemState.Inactive
        }

        HistoryTimelineVariant.Progress -> if (currentIndex != null && index <= currentIndex) {
            HistoryItemState.Reached
        } else {
            HistoryItemState.Upcoming
        }
    }

@Composable
private fun CoreHistoryTimelineItem(
    item: HistoryTimelineItem,
    state: HistoryItemState,
    isLast: Boolean,
) {
    val spaces = LocalSpaces.current
    val borderWidths = LocalBorderWidths.current
    val typographies = LocalTypographies.current
    val contentColors = LocalColors.current.content
    val mutedColor = LocalColors.current.border.borderNeutralMedium
    val dotColor: Color = when (state) {
        HistoryItemState.Current, HistoryItemState.Reached -> item.voice.currentDotColor
        HistoryItemState.Inactive, HistoryItemState.Upcoming -> mutedColor
    }
    val lineColor: Color = when (state) {
        HistoryItemState.Reached -> LocalColors.current.border.borderSelected
        HistoryItemState.Current, HistoryItemState.Inactive, HistoryItemState.Upcoming -> mutedColor
    }
    val labelColor: Color = when (state) {
        HistoryItemState.Upcoming -> contentColors.contentTertiary
        HistoryItemState.Current, HistoryItemState.Inactive, HistoryItemState.Reached -> contentColors.contentPrimary
    }

    Row(
        modifier = Modifier.drawBehind {
            val centerX = HistoryItemIndicatorWidth.toPx() / 2f
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
                    strokeWidth = borderWidths.base.border40.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        },
        horizontalArrangement = Arrangement.spacedBy(space = spaces.spacing300),
    ) {
        Spacer(modifier = Modifier.width(width = HistoryItemIndicatorWidth))

        Column(
            modifier = Modifier.padding(bottom = if (isLast) 0.dp else spaces.spacing600),
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

            val contentSlot = item.contentSlot
            if (contentSlot != null) {
                contentSlot()
            }
        }
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
private fun HistoryTimelineProgressPreview() {
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
        variant = HistoryTimelineVariant.Progress,
    )
}
