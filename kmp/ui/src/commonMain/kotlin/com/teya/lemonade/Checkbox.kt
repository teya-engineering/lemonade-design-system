package com.teya.lemonade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.CheckboxStatus
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Selects one or more options from a set.
 *
 * Supports checked, unchecked and indeterminate states.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Checkbox(
 *     status = CheckboxStatus.Checked,
 *     onCheckboxClicked = { /* on box toggle behaviour */ },
 *     label = "Label",
 *     supportText = "Support Text",
 *     enabled = false,
 * )
 * ```
 *
 * @param status current [CheckboxStatus] of the checkbox
 * @param onCheckboxClicked called when the user clicks the checkbox
 * @param label primary text shown next to the checkbox
 * @param modifier [Modifier] applied to the whole component
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *  the visual feedback
 * @param supportText optional secondary text shown below the label, hidden when null
 * @param enabled whether the checkbox responds to clicks
 */
@Composable
public fun LemonadeUi.Checkbox(
    status: CheckboxStatus,
    onCheckboxClicked: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    supportText: String? = null,
    enabled: Boolean = true,
) {
    val props = defaultPlatformCheckboxProps()
        .copy(focusVisible = true)
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        modifier = modifier
            .clickable(
                role = Role.Checkbox,
                enabled = enabled,
                onClick = onCheckboxClicked,
                interactionSource = interactionSource,
                indication = null,
            ).hoverable(interactionSource = interactionSource),
    ) {
        CoreCheckbox(
            status = status,
            interactionSource = interactionSource,
            onCheckboxClicked = onCheckboxClicked,
            enabled = enabled,
            checkboxPlatformProps = props,
        )

        Column(
            modifier = if (enabled) {
                Modifier
            } else {
                Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
            },
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
 * Shows the checkbox box on its own, without a label.
 *
 * Use it in custom layouts that place the label separately.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Checkbox(
 *     status = CheckboxStatus.Indeterminate,
 *     onCheckboxClicked = { /* on box toggle behaviour */ },
 *     enabled = false,
 * )
 * ```
 *
 * @param status current [CheckboxStatus] of the checkbox
 * @param onCheckboxClicked called when the user clicks the checkbox
 * @param modifier [Modifier] applied to the checkbox
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *  the visual feedback
 * @param enabled whether the checkbox responds to clicks
 */
@Suppress("UnusedParameter")
@Composable
public fun LemonadeUi.Checkbox(
    status: CheckboxStatus,
    onCheckboxClicked: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    CoreCheckbox(
        status = status,
        onCheckboxClicked = onCheckboxClicked,
        enabled = enabled,
        modifier = modifier,
    )
}

@Suppress("LongMethod", "CyclomaticComplexMethod", "UnusedParameter")
@Composable
private fun CoreCheckbox(
    status: CheckboxStatus,
    onCheckboxClicked: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    checkboxPlatformProps: CheckboxPlatformProps = defaultPlatformCheckboxProps(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val platformProps = checkboxPlatformProps
    val isHovering by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val isChecked = remember(status) {
        status in setOf(CheckboxStatus.Checked, CheckboxStatus.Indeterminate)
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            !enabled && status == CheckboxStatus.Unchecked -> LocalColors.current.border.borderNeutralMedium
            enabled && status == CheckboxStatus.Unchecked -> LocalColors.current.border.borderNeutralHigh
            else -> LocalColors.current.border.borderNeutralHigh.copy(
                alpha = LocalOpacities.current.base.opacity0,
            )
        },
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> LocalColors.current.background.bgElevatedHigh
            !isChecked && isHovering -> LocalColors.current.interaction.bgSubtleInteractive
            !isChecked -> LocalColors.current.background.bgDefault
            isChecked && isHovering -> LocalColors.current.interaction.bgBrandHighInteractive
            isHovering -> LocalColors.current.content.contentPrimaryInverse
            else -> LocalColors.current.background.bgBrandHigh
        },
    )
    val animatedIconTint by animateColorAsState(
        targetValue = if (enabled) {
            LocalColors.current.content.contentPrimaryInverse
        } else {
            LocalColors.current.content.contentTertiary
        },
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .focusable(
                enabled = enabled,
                interactionSource = interactionSource,
            ).clip(shape = LocalShapes.current.radius150)
            .clickable(
                role = Role.Checkbox,
                enabled = enabled,
                onClick = onCheckboxClicked,
                interactionSource = interactionSource,
                indication = null,
            ).then(
                other = if (isFocused && platformProps.focusVisible) {
                    Modifier
                        .border(
                            width = LocalBorderWidths.current.state.focusRing,
                            shape = LocalShapes.current.radius150,
                            color = LocalColors.current.border.borderSelected,
                        ).padding(all = LocalBorderWidths.current.state.focusRing + 1.dp)
                        .clip(shape = LocalShapes.current.radius150)
                } else {
                    Modifier
                },
            ).border(
                width = LocalBorderWidths.current.base.border50,
                color = animatedBorderColor,
                shape = LocalShapes.current.radius150,
            ).background(color = animatedBackgroundColor)
            .requiredSize(size = platformProps.checkboxSize),
    ) {
        AnimatedContent(
            targetState = status.icon,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
            content = { icon ->
                if (icon != null) {
                    LemonadeUi.Icon(
                        icon = icon,
                        contentDescription = status.name,
                        size = LemonadeAssetSize.Medium,
                        tint = animatedIconTint,
                    )
                } else {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            },
        )
    }
}

private val CheckboxStatus.icon: LemonadeIcons?
    get() {
        return when (this) {
            CheckboxStatus.Checked -> LemonadeIcons.CheckSmall
            CheckboxStatus.Unchecked -> null
            CheckboxStatus.Indeterminate -> LemonadeIcons.Minus
        }
    }

@Stable
internal data class CheckboxPlatformProps(
    val checkboxSize: Dp,
    val labelStyle: LemonadeTextStyle,
    val supportTextStyle: LemonadeTextStyle,
    val focusVisible: Boolean,
)

@Composable
internal fun defaultPlatformCheckboxProps(): CheckboxPlatformProps =
    CheckboxPlatformProps(
        checkboxSize = 22.dp,
        labelStyle = LocalTypographies.current.bodyMediumMedium,
        supportTextStyle = LocalTypographies.current.bodySmallRegular,
        focusVisible = false,
    )

@LemonadePreview
@Composable
internal fun LemonadeCheckboxPreview() {
    Column(
        modifier = Modifier.background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 24.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        CheckboxStatus.entries.forEach { status ->
            LemonadeUi.Checkbox(
                status = status,
                onCheckboxClicked = { },
                enabled = true,
            )
            LemonadeUi.Checkbox(
                status = status,
                onCheckboxClicked = { },
                enabled = false,
            )
        }
    }
}

@LemonadePreview
@Composable
internal fun LemonadeLabeledCheckboxPreview() {
    Column(
        modifier = Modifier.background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 24.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        CheckboxStatus.entries.forEach { status ->
            LemonadeUi.Checkbox(
                label = "Label",
                supportText = "Support text",
                status = status,
                onCheckboxClicked = { },
                enabled = true,
            )
            LemonadeUi.Checkbox(
                label = "Label",
                supportText = "Support text",
                status = status,
                onCheckboxClicked = { },
                enabled = false,
            )
        }
    }
}
