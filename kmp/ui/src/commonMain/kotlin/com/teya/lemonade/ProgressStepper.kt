package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.LayoutDirection

private const val PROGRESS_STEPPER_SWEEP_CYCLE_MILLIS: Int = 2000

/**
 * A horizontal row of segments that shows how far a known, linear process has progressed, such
 * as an order moving from ordered to dispatched to delivered.
 *
 * The first [completedSteps] segments render a positive bar and full-contrast text; the rest
 * render muted. The segment right after the completed ones is the step in progress, and a
 * positive gradient sweeps along its bar on a loop. Set [isFailed] when that step has failed: its
 * bar turns critical and the sweep stops. Text under the first segment aligns to the start, under
 * the last to the end, and under any segment in between to the centre. Leave
 * [ProgressStepperItem.label] and [ProgressStepperItem.subheading] `null` for a bars-only stepper.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.ProgressStepper(
 *     items = listOf(
 *         ProgressStepperItem(label = "Ordered", subheading = "12 Jun"),
 *         ProgressStepperItem(label = "Dispatched", subheading = "14 Jun"),
 *         ProgressStepperItem(label = "Delivered"),
 *     ),
 *     completedSteps = 2,
 * )
 *
 * LemonadeUi.ProgressStepper(
 *     items = listOf(
 *         ProgressStepperItem(label = "Ordered", subheading = "12 Jun"),
 *         ProgressStepperItem(label = "Delivery failed", subheading = "14 Jun"),
 *         ProgressStepperItem(label = "Delivered"),
 *     ),
 *     completedSteps = 1,
 *     isFailed = true,
 * )
 *
 * LemonadeUi.ProgressStepper(
 *     items = List(3) { ProgressStepperItem() },
 *     completedSteps = 1,
 * )
 * ```
 *
 * @param items ordered list of [ProgressStepperItem]s, one per segment
 * @param completedSteps how many segments, counted from the start, render as completed. Values
 *   outside `0..items.size` are clamped
 * @param modifier [Modifier] applied to the root row
 * @param isFailed whether the step in progress, the one at index [completedSteps], has failed.
 *   Has no effect once every step is completed
 */
@Composable
public fun LemonadeUi.ProgressStepper(
    items: List<ProgressStepperItem>,
    completedSteps: Int,
    modifier: Modifier = Modifier,
    isFailed: Boolean = false,
) {
    if (items.isEmpty()) return
    val completedCount = completedSteps.coerceIn(minimumValue = 0, maximumValue = items.size)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing100),
        verticalAlignment = Alignment.Top,
    ) {
        items.forEachIndexed { index, item ->
            val state = ProgressStepperState.of(
                index = index,
                completedCount = completedCount,
                isFailed = isFailed,
            )
            ProgressStepperSegment(
                item = item,
                state = state,
                textAlign = stepTextAlign(index = index, lastIndex = items.lastIndex),
                stepNumber = index + 1,
                stepCount = items.size,
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
}

private enum class ProgressStepperState(
    val status: String,
    val isReached: Boolean,
) {
    Completed(status = "Completed", isReached = true),
    Current(status = "Current", isReached = false),
    Failed(status = "Failed", isReached = true),
    Upcoming(status = "Upcoming", isReached = false),
    ;

    companion object {
        fun of(
            index: Int,
            completedCount: Int,
            isFailed: Boolean,
        ): ProgressStepperState =
            when {
                index < completedCount -> Completed
                index > completedCount -> Upcoming
                isFailed -> Failed
                else -> Current
            }
    }
}

private fun stepTextAlign(
    index: Int,
    lastIndex: Int,
): TextAlign =
    when (index) {
        0 -> TextAlign.Start
        lastIndex -> TextAlign.End
        else -> TextAlign.Center
    }

@Composable
private fun ProgressStepperSegment(
    item: ProgressStepperItem,
    state: ProgressStepperState,
    textAlign: TextAlign,
    stepNumber: Int,
    stepCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalColors.current
    val hasText = item.label != null || item.subheading != null
    val barColor = when (state) {
        ProgressStepperState.Completed -> colors.background.bgPositive
        ProgressStepperState.Failed -> colors.background.bgCritical
        ProgressStepperState.Current, ProgressStepperState.Upcoming -> colors.background.bgElevatedHigh
    }
    val sweep = if (state == ProgressStepperState.Current) {
        rememberProgressSweep(
            cycleMillis = PROGRESS_STEPPER_SWEEP_CYCLE_MILLIS,
            label = "ProgressStepperSweep",
        )
    } else {
        null
    }
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {
            if (hasText) {
                stateDescription = "${state.status}, step $stepNumber of $stepCount"
            } else {
                contentDescription = "Step $stepNumber of $stepCount"
                stateDescription = state.status
            }
        },
        verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = LocalSizes.current.size100)
                .clip(shape = LocalShapes.current.radiusFull)
                .background(color = barColor)
                .progressSweep(
                    sweep = sweep,
                    color = colors.background.bgPositive,
                ),
        )
        if (hasText) {
            ProgressStepperText(
                item = item,
                isReached = state.isReached,
                textAlign = textAlign,
            )
        }
    }
}

@Composable
private fun ProgressStepperText(
    item: ProgressStepperItem,
    isReached: Boolean,
    textAlign: TextAlign,
) {
    val content = LocalColors.current.content
    val typographies = LocalTypographies.current
    Column(modifier = Modifier.fillMaxWidth()) {
        item.label?.let { label ->
            LemonadeUi.Text(
                text = label,
                textStyle = typographies.bodySmallMedium,
                textAlign = textAlign,
                color = if (isReached) content.contentPrimary else content.contentTertiary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item.subheading?.let { subheading ->
            LemonadeUi.Text(
                text = subheading,
                textStyle = typographies.bodySmallRegular,
                textAlign = textAlign,
                color = if (isReached) content.contentSecondary else content.contentTertiary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun Modifier.progressSweep(
    sweep: ProgressSweepState?,
    color: Color,
): Modifier =
    drawWithContent {
        drawContent()
        if (sweep == null) return@drawWithContent
        val end = size.width * sweep.fill
        val alpha = sweep.alpha
        if (end <= 0f || alpha <= 0f) return@drawWithContent
        scale(
            scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f,
            scaleY = 1f,
        ) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(color, color.copy(alpha = 0f)),
                    startX = 0f,
                    endX = end,
                ),
                size = size.copy(width = end),
                alpha = alpha,
            )
        }
    }

private data class ProgressStepperPreviewData(
    val completedSteps: Int,
    val hasText: Boolean,
    val isFailed: Boolean = false,
)

private class ProgressStepperPreviewProvider : PreviewParameterProvider<ProgressStepperPreviewData> {
    override val values: Sequence<ProgressStepperPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<ProgressStepperPreviewData> =
        buildList {
            listOf(true, false).forEach { hasText ->
                for (completedSteps in 1..3) {
                    add(
                        element = ProgressStepperPreviewData(
                            completedSteps = completedSteps,
                            hasText = hasText,
                        ),
                    )
                }
                add(
                    element = ProgressStepperPreviewData(
                        completedSteps = 1,
                        hasText = hasText,
                        isFailed = true,
                    ),
                )
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun ProgressStepperPreview(
    @PreviewParameter(ProgressStepperPreviewProvider::class)
    previewData: ProgressStepperPreviewData,
) {
    LemonadeUi.ProgressStepper(
        items = List(size = 3) {
            if (previewData.hasText) {
                ProgressStepperItem(label = "Step title", subheading = "Subheading")
            } else {
                ProgressStepperItem()
            }
        },
        completedSteps = previewData.completedSteps,
        isFailed = previewData.isFailed,
    )
}
