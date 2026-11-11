package com.teya.lemonade

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import kotlin.math.min

private const val PROGRESS_SWEEP_FILL_FRACTION: Float = 0.8f
private const val PROGRESS_SWEEP_FADE_START_FRACTION: Float = 0.6f

/**
 * The looping positive sweep that marks a step in progress: a gradient grows along the track,
 * fades out, and restarts once per cycle. Read [fill] and [alpha] in a draw phase so each frame
 * skips recomposition. With no [elapsedMillis], as under reduced motion, the gradient stays fully
 * drawn.
 */
internal class ProgressSweepState(
    private val cycleMillis: Int,
    private val elapsedMillis: State<Float>?,
) {
    /** How far along the track the gradient reaches, from 0 to 1. */
    val fill: Float
        get() {
            val elapsed = elapsedMillis?.value
                ?: return 1f
            val linearFraction = min(elapsed / (cycleMillis * PROGRESS_SWEEP_FILL_FRACTION), 1f)
            return EaseInOut.transform(linearFraction)
        }

    val alpha: Float
        get() {
            val elapsed = elapsedMillis?.value
                ?: return 1f
            val fadeStart = cycleMillis * PROGRESS_SWEEP_FADE_START_FRACTION
            val fillEnd = cycleMillis * PROGRESS_SWEEP_FILL_FRACTION
            val fadeFraction = (elapsed - fadeStart) / (fillEnd - fadeStart)
            return 1f - fadeFraction.coerceIn(minimumValue = 0f, maximumValue = 1f)
        }
}

@Composable
internal fun rememberProgressSweep(
    cycleMillis: Int,
    label: String,
): ProgressSweepState {
    if (prefersReducedMotion()) {
        return remember(cycleMillis) {
            ProgressSweepState(
                cycleMillis = cycleMillis,
                elapsedMillis = null,
            )
        }
    }
    val elapsedMillis = rememberInfiniteTransition(label = label).animateFloat(
        initialValue = 0f,
        targetValue = cycleMillis.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = cycleMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "${label}Elapsed",
    )
    return remember(cycleMillis, elapsedMillis) {
        ProgressSweepState(
            cycleMillis = cycleMillis,
            elapsedMillis = elapsedMillis,
        )
    }
}
