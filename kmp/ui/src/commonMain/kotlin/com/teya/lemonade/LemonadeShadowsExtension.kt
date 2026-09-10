package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeShadow

public fun Modifier.lemonadeShadow(
    shadow: LemonadeShadow,
    shape: Shape,
): Modifier {
    var modifier = this
    shadow.shadowDataSequence.forEach { shadowData ->
        modifier = modifier.dropShadow(
            lemonadeShadow = shadowData,
            shape = shape,
        )
    }
    return modifier
}

@Composable
public fun Modifier.animateLemonadeShadow(
    shadow: LemonadeShadow,
    shape: Shape,
): Modifier {
    var modifier = this
    val shadowColor = LocalColors.current.shadow.shadowDefault
    val animatedColor by animateColorAsState(
        targetValue = if (shadow == LemonadeShadow.None) {
            shadowColor.copy(alpha = LocalOpacities.current.base.opacity0)
        } else {
            shadowColor
        },
    )
    val animatedShadows = remember {
        mutableListOf<State<LemonadeShadowData>>()
    }
    shadow.normalizedShadowSequence.forEachIndexed { index, shadowData ->
        if (animatedShadows.lastIndex >= index) {
            animatedShadows[index] = animatedShadowDataAsState(targetValue = shadowData)
        } else {
            animatedShadows.add(element = animatedShadowDataAsState(targetValue = shadowData))
        }
    }
    animatedShadows.forEach { animatedShadow ->
        modifier = modifier.dropShadow(
            lemonadeShadow = animatedShadow.value,
            shape = shape,
            shadowColor = animatedColor,
        )
    }
    return modifier
}

private fun Modifier.dropShadow(
    lemonadeShadow: LemonadeShadowData,
    shape: Shape,
    shadowColor: Color? = null,
): Modifier =
    composed {
        dropShadow(
            shape = shape,
            shadow = Shadow(
                color = shadowColor
                    ?: LocalColors.current.shadow.shadowDefault,
                radius = lemonadeShadow.blur.dp,
                spread = lemonadeShadow.spread.dp,
                offset = DpOffset(
                    x = lemonadeShadow.offsetX.dp,
                    y = lemonadeShadow.offsetY.dp,
                ),
            ),
        )
    }

@Composable
private fun animatedShadowDataAsState(targetValue: LemonadeShadowData): State<LemonadeShadowData> =
    animateValueAsState(
        targetValue = targetValue,
        animationSpec = remember { tween(durationMillis = 200) },
        typeConverter = TwoWayConverter(
            convertToVector = { data ->
                AnimationVector4D(
                    v1 = data.blur,
                    v2 = data.spread,
                    v3 = data.offsetX,
                    v4 = data.offsetY,
                )
            },
            convertFromVector = { vector ->
                LemonadeShadowData(
                    blur = vector.v1,
                    spread = vector.v2,
                    offsetX = vector.v3,
                    offsetY = vector.v4,
                )
            },
        ),
    )

private val LemonadeShadow.normalizedShadowSequence: Sequence<LemonadeShadowData>
    get() {
        val biggestSequenceSize = LemonadeShadow.entries
            .maxOf { shadow -> shadow.shadowDataSequence.count() }
        val mutableList = shadowDataSequence.toMutableList()
        repeat(times = biggestSequenceSize - mutableList.size) {
            mutableList.add(
                element = LemonadeShadowData(
                    blur = 0.0f,
                    spread = 0.0f,
                    offsetX = 0.0f,
                    offsetY = 0.0f,
                ),
            )
        }
        return mutableList.asSequence()
    }

internal data class LemonadeShadowData(
    val blur: Float,
    val spread: Float,
    val offsetX: Float,
    val offsetY: Float,
)
