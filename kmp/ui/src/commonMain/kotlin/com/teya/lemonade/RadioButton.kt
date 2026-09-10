package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Selects a single option from a group.
 *
 * Selecting one option deselects any previously selected option.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.RadioButton(
 *     checked = true,
 *     onRadioButtonClicked = { /* some action to be triggered if not already checked */ },
 *     label = "Label",
 *     supportText = "Support Text",
 *     enabled = false,
 * )
 * ```
 *
 * @param checked whether this option is selected
 * @param onRadioButtonClicked called when the user clicks the radio button
 * @param label primary text shown next to the radio button
 * @param modifier optional [Modifier] applied to the entire component
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *   visual feedback
 * @param supportText optional secondary text shown below [label]; hidden when null
 * @param enabled whether the radio button responds to input
 */
@Composable
public fun LemonadeUi.RadioButton(
    checked: Boolean,
    onRadioButtonClicked: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    supportText: String? = null,
    enabled: Boolean = true,
) {
    val props = defaultPlatformRadioButtonProps()
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        modifier = modifier
            .toggleable(
                indication = null,
                enabled = enabled,
                value = checked,
                interactionSource = interactionSource,
                role = Role.RadioButton,
                onValueChange = { selected ->
                    if (selected) {
                        onRadioButtonClicked()
                    }
                },
            ).hoverable(interactionSource = interactionSource),
    ) {
        CoreRadioButton(
            checked = checked,
            onRadioButtonClicked = onRadioButtonClicked,
            enabled = enabled,
            platformProperties = props,
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
                textStyle = props.labelStyle,
            )

            if (supportText != null) {
                LemonadeUi.Text(
                    text = supportText,
                    textStyle = props.supportTextStyle,
                    color = LocalColors.current.content.contentSecondary,
                )
            }
        }
    }
}

/**
 * Selects a single option from a group.
 *
 * Selecting one option deselects any previously selected option.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.RadioButton(
 *     checked = true,
 *     onRadioButtonClicked = { /* some action to be triggered if not already checked */ },
 *     enabled = true,
 * )
 * ```
 *
 * @param checked whether this option is selected
 * @param onRadioButtonClicked called when the user clicks the radio button
 * @param modifier optional [Modifier] applied to the radio button
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *   visual feedback
 * @param enabled whether the radio button responds to input
 */
@Composable
public fun LemonadeUi.RadioButton(
    checked: Boolean,
    onRadioButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    CoreRadioButton(
        checked = checked,
        onRadioButtonClicked = onRadioButtonClicked,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
private fun CoreRadioButton(
    checked: Boolean,
    onRadioButtonClicked: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    platformProperties: RadioButtonPlatformProps = defaultPlatformRadioButtonProps(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> LocalColors.current.background.bgElevatedHigh
            checked -> LocalColors.current.background.bgBrandHigh
            isHovered -> LocalColors.current.interaction.bgSubtleInteractive
            else -> LocalColors.current.background.bgDefault
        },
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (checked && enabled) {
            LocalColors.current.background.bgBrandHigh
        } else {
            LocalColors.current.border.borderNeutralMedium
        },
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape = LocalShapes.current.radiusFull)
            .toggleable(
                indication = null,
                enabled = enabled,
                value = checked,
                interactionSource = interactionSource,
                role = Role.RadioButton,
                onValueChange = { selected ->
                    if (selected) {
                        onRadioButtonClicked()
                    }
                },
            ).hoverable(interactionSource = interactionSource)
            .focusable(
                enabled = enabled,
                interactionSource = interactionSource,
            ).then(
                other = if (isFocused && platformProperties.focusVisible) {
                    Modifier
                        .border(
                            width = LocalBorderWidths.current.state.focusRing,
                            shape = LocalShapes.current.radiusFull,
                            color = LocalColors.current.border.borderSelected,
                        ).padding(all = LocalBorderWidths.current.state.focusRing + 1.dp)
                        .clip(shape = LocalShapes.current.radiusFull)
                } else {
                    Modifier
                },
            ).requiredSize(size = platformProperties.componentSize)
            .background(color = animatedBackgroundColor)
            .then(
                other = if (!enabled && checked) {
                    Modifier
                } else {
                    Modifier.border(
                        color = animatedBorderColor,
                        shape = LocalShapes.current.radiusFull,
                        width = LocalBorderWidths.current.base.border50,
                    )
                },
            ),
    ) {
        AnimatedContent(
            targetState = checked,
            transitionSpec = { scaleIn() togetherWith scaleOut() },
            modifier = Modifier.matchParentSize(),
            content = { isChecked ->
                if (isChecked) {
                    val animatedCenterColor by animateColorAsState(
                        targetValue = when {
                            checked && !enabled -> LocalColors.current.background.bgDefaultInverse.copy(
                                alpha = LocalOpacities.current.state.opacityDisabled,
                            )

                            checked -> LocalColors.current.background.bgDefault
                            else -> LocalColors.current.background.bgDefault.copy(
                                alpha = LocalOpacities.current.base.opacity0,
                            )
                        },
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = animatedCenterColor,
                                shape = LocalShapes.current.radiusFull,
                            ).requiredSize(size = platformProperties.checkedCircleSize),
                    )
                } else {
                    Spacer(modifier = Modifier.matchParentSize())
                }
            },
        )
    }
}

@Stable
internal data class RadioButtonPlatformProps(
    val componentSize: Dp,
    val checkedCircleSize: Dp,
    val labelStyle: LemonadeTextStyle,
    val supportTextStyle: LemonadeTextStyle,
    val focusVisible: Boolean,
)

@Composable
internal fun defaultPlatformRadioButtonProps(): RadioButtonPlatformProps =
    RadioButtonPlatformProps(
        componentSize = 20.dp,
        checkedCircleSize = 10.dp,
        labelStyle = LocalTypographies.current.bodyMediumMedium,
        supportTextStyle = LocalTypographies.current.bodySmallRegular,
        focusVisible = true,
    )

private data class RadioPreviewData(
    val checked: Boolean,
    val label: String?,
    val supportText: String?,
    val enabled: Boolean,
)

private class RadioPreviewProvider : PreviewParameterProvider<RadioPreviewData> {
    override val values: Sequence<RadioPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<RadioPreviewData> =
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
                                                element = RadioPreviewData(
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
private fun LemonadeLabeledRadioButtonPreview(
    @PreviewParameter(RadioPreviewProvider::class)
    previewData: RadioPreviewData,
) {
    if (previewData.label != null) {
        LemonadeUi.RadioButton(
            label = previewData.label,
            supportText = previewData.supportText,
            checked = previewData.checked,
            enabled = previewData.enabled,
            onRadioButtonClicked = { },
        )
    } else {
        LemonadeUi.RadioButton(
            checked = previewData.checked,
            enabled = previewData.enabled,
            onRadioButtonClicked = { },
        )
    }
}
