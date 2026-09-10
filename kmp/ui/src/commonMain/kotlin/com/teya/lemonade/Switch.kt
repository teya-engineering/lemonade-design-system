package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeShadow

/**
 * Shows a toggle switch with an animated track and thumb.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Switch(
 *  checked = false,
 *  onCheckedChange = { setTo -> ... },
 * )
 * ```
 *
 * @param checked `true` when the switch is on
 * @param onCheckedChange callback run with the new state when the user toggles the switch
 * @param enabled when `false` the switch ignores input and renders as disabled
 * @param interactionSource [MutableInteractionSource] observed for the hover and press states
 *  that drive the visual feedback
 * @param modifier [Modifier] applied to the root container of the switch
 */
@Composable
public fun LemonadeUi.Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    CoreSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

/**
 * Shows a toggle switch with a label and optional support text.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Switch(
 *  checked = false,
 *  label = "Instant Settlements",
 *  supportText = "This is a feature that instantiate the settlements? idk",
 *  onCheckedChange = { setTo -> ... },
 * )
 * ```
 *
 * @param checked `true` when the switch is on
 * @param onCheckedChange callback run with the new state when the user toggles the switch
 * @param label text shown next to the switch
 * @param supportText secondary text shown under [label]
 * @param enabled when `false` the switch ignores input and renders as disabled
 * @param interactionSource [MutableInteractionSource] observed for the hover and press states
 *  that drive the visual feedback
 * @param modifier [Modifier] applied to the root container of the switch
 */
@Composable
public fun LemonadeUi.Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    supportText: String? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        modifier = modifier
            .toggleable(
                indication = null,
                enabled = enabled,
                value = checked,
                interactionSource = interactionSource,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ).hoverable(interactionSource = interactionSource),
    ) {
        CoreSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            interactionSource = interactionSource,
        )

        Column(
            modifier = Modifier.then(
                other = if (enabled) {
                    Modifier
                } else {
                    Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                },
            ),
        ) {
            LemonadeUi.Text(
                text = label,
                textStyle = LocalTypographies.current.bodyMediumMedium,
            )

            if (supportText != null) {
                LemonadeUi.Text(
                    text = supportText,
                    textStyle = LocalTypographies.current.bodySmallRegular,
                    color = LocalColors.current.content.contentSecondary,
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun CoreSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    val isHover by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val switchState = when {
        isPressed -> SwitchState.Pressed
        isHover -> SwitchState.Hover
        else -> SwitchState.Default
    }
    val switchProps = switchState.getSwitchProps()

    val animatedBallWidth by animateDpAsState(targetValue = switchProps.minIndicatorWidth)
    val horizontalBias by animateFloatAsState(
        targetValue = if (checked) {
            1f
        } else {
            -1f
        },
    )
    val animatedBaseColor by animateColorAsState(
        targetValue = when {
            !enabled -> LocalColors.current.background.bgElevatedHigh
            checked && isPressed -> LocalColors.current.interaction.bgBrandHighPressed
            checked -> LocalColors.current.background.bgBrandHigh
            isPressed -> LocalColors.current.interaction.bgElevatedPressed
            else -> LocalColors.current.background.bgElevatedHigh
        },
    )
    val animatedKnobColor by animateColorAsState(
        targetValue = if (!enabled) {
            LocalColors.current.background.bgElevatedHigh
        } else {
            LocalColors.current.background.bgDefault
        },
    )

    Box(
        contentAlignment = BiasAlignment(
            horizontalBias = horizontalBias,
            verticalBias = 0f,
        ),
        modifier = modifier
            .hoverable(
                interactionSource = interactionSource,
                enabled = enabled,
            ).size(
                size = DpSize(
                    height = switchProps.minHeight,
                    width = switchProps.minWidth,
                ),
            ).aspectRatio(ratio = switchProps.minWidth / switchProps.minHeight)
            .clip(shape = LocalShapes.current.radiusFull)
            .clickable(
                role = Role.Switch,
                interactionSource = interactionSource,
                enabled = enabled,
                indication = null,
                onClick = { onCheckedChange(!checked) },
            ).background(color = animatedBaseColor)
            .padding(all = LocalSpaces.current.spacing50),
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(ratio = animatedBallWidth / switchProps.minIndicatorHeight)
                .then(
                    other = if (enabled) {
                        Modifier.lemonadeShadow(
                            shadow = LemonadeShadow.Small,
                            shape = LocalShapes.current.radiusFull,
                        )
                    } else {
                        Modifier
                    },
                ).border(
                    width = LocalBorderWidths.current.base.border25,
                    color = LocalColors.current.background.bgElevated,
                    shape = LocalShapes.current.radiusFull,
                ).padding(all = 1.dp)
                .background(
                    shape = LocalShapes.current.radiusFull,
                    color = animatedKnobColor,
                ),
        )
    }
}

internal data class SwitchSizeProps(
    val minHeight: Dp,
    val minWidth: Dp,
    val minIndicatorHeight: Dp,
    val minIndicatorWidth: Dp,
)

internal enum class SwitchState {
    Default,
    Hover,
    Pressed,
}

internal fun SwitchState.getSwitchProps(): SwitchSizeProps =
    when (this) {
        SwitchState.Default -> SwitchSizeProps(
            minHeight = 28.dp,
            minWidth = 48.dp,
            minIndicatorHeight = 22.dp,
            minIndicatorWidth = 22.dp,
        )

        SwitchState.Hover -> SwitchSizeProps(
            minHeight = 28.dp,
            minWidth = 48.dp,
            minIndicatorHeight = 22.dp,
            minIndicatorWidth = 24.dp,
        )

        SwitchState.Pressed -> SwitchSizeProps(
            minHeight = 28.dp,
            minWidth = 48.dp,
            minIndicatorHeight = 22.dp,
            minIndicatorWidth = 26.dp,
        )
    }

private data class SwitchPreviewData(
    val checked: Boolean,
    val label: String?,
    val supportText: String?,
    val enabled: Boolean,
)

private class SwitchPreviewProvider : PreviewParameterProvider<SwitchPreviewData> {
    override val values: Sequence<SwitchPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SwitchPreviewData> =
        buildList {
            listOf(true, false)
                .forEach { checked ->
                    listOf(true, false)
                        .forEach { enabled ->
                            listOf(true, false)
                                .forEach { withLabel ->
                                    listOf(true, false)
                                        .forEach { withSupportText ->
                                            add(
                                                SwitchPreviewData(
                                                    checked = checked,
                                                    label = "Label".takeIf { withLabel },
                                                    supportText = "Support Text".takeIf { withSupportText },
                                                    enabled = enabled,
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
private fun LemonadeSwitchPreview(
    @PreviewParameter(SwitchPreviewProvider::class)
    previewData: SwitchPreviewData,
) {
    if (previewData.label != null) {
        LemonadeUi.Switch(
            label = previewData.label,
            supportText = previewData.supportText,
            checked = previewData.checked,
            enabled = previewData.enabled,
            onCheckedChange = { },
        )
    } else {
        LemonadeUi.Switch(
            checked = previewData.checked,
            enabled = previewData.enabled,
            onCheckedChange = { },
        )
    }
}
