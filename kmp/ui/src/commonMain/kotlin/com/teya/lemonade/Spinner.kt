package com.teya.lemonade

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize

/**
 * Shows that a process is ongoing.
 *
 * Animated indicator for loading or waiting states; it never blocks the interface.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Spinner(
 *     size = LemonadeAssetSize.Medium,
 *     tint = LocalColors.current.content.contentSecondary,
 * )
 * ```
 *
 * @param size [LemonadeAssetSize] applied to the spinner; defaults to [LemonadeAssetSize.Medium]
 * @param tint tint color applied to the spinner; defaults to the secondary content color of
 *  [LemonadeTheme]
 * @param modifier optional [Modifier] for styling and layout adjustments
 */
@Composable
public fun LemonadeUi.Spinner(
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    tint: Color = LocalColors.current.content.contentSecondary,
    modifier: Modifier = Modifier,
) {
    CoreSpinner(
        tint = tint,
        spinnerSize = size,
        modifier = modifier,
    )
}

@Composable
private fun CoreSpinner(
    tint: Color,
    spinnerSize: LemonadeAssetSize,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteRotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = EaseInOut,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Rotate it",
    )

    val strokeBaseWidth = LocalBorderWidths.current.base.border50
    val spinnerBaseSize = LocalSizes.current.size500

    Canvas(
        modifier = modifier
            .size(spinnerSize.dp),
    ) {
        /** Scales the stroke with the [LemonadeUi.Spinner] so its width stays proportional. */
        val strokeWidth = size.minDimension * (strokeBaseWidth / spinnerBaseSize)

        rotate(rotation) {
            drawArc(
                color = tint,
                startAngle = 0f,
                sweepAngle = 285f,
                topLeft = Offset.Zero,
                useCenter = false,
                size = Size(
                    width = size.width,
                    height = size.height,
                ),
                style = Stroke(width = strokeWidth),
            )
        }
    }
}

private val LemonadeAssetSize.dp: Dp
    @Composable get() {
        return when (this) {
            LemonadeAssetSize.XSmall -> LocalSizes.current.size300
            LemonadeAssetSize.Small -> LocalSizes.current.size400
            LemonadeAssetSize.Medium -> LocalSizes.current.size500
            LemonadeAssetSize.Large -> LocalSizes.current.size600
            LemonadeAssetSize.XLarge -> LocalSizes.current.size800
            LemonadeAssetSize.XXLarge -> LocalSizes.current.size1000
            LemonadeAssetSize.XXXLarge -> LocalSizes.current.size1200
            LemonadeAssetSize.XXXXLarge -> LocalSizes.current.size1400
        }
    }

private data class SpinnerPreviewData(
    val size: LemonadeAssetSize,
)

private class SpinnerPreviewProvider : PreviewParameterProvider<SpinnerPreviewData> {
    override val values: Sequence<SpinnerPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SpinnerPreviewData> =
        buildList {
            LemonadeAssetSize.entries.forEach { size ->
                add(
                    element = SpinnerPreviewData(
                        size = size,
                    ),
                )
            }
        }.asSequence()
}

@Composable
@LemonadePreview
private fun SpinnerPreview(
    @PreviewParameter(SpinnerPreviewProvider::class)
    previewData: SpinnerPreviewData,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 4.dp)) {
        LemonadeUi.Spinner(size = previewData.size)
    }
}
