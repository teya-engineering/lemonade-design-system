package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Lemonade labeled button component. Used for simple click actions with a text and optional icons.
 * ## Usage
 * ```kotlin
 * LemonadeUi.Button(
 *   label = "click me!",
 *   onClick = { println("button clicked!") },
 * )
 * ```
 * @param label - [String] to be displayed as the Button's label.
 * @param onClick - Callback to be invoked when the Button is clicked.
 * @param leadingIcon - [LemonadeIcons] shown before the label.
 * @param trailingIcon - [LemonadeIcons] shown after the label.
 * @param variant - [LemonadeButtonVariant] for the color palette (Primary, Secondary, Neutral,
 * Critical, OnBrand, OnColor). OnBrand and OnColor are single Subtle treatments for placing a
 * button on top of a brand- or color-filled surface; they ignore [type].
 * @param type - [LemonadeButtonType] for the fill treatment (Solid, Subtle, Ghost).
 * @param size - [LemonadeButtonSize] to size the Button accordingly.
 * @param modifier - [Modifier] to be applied to the Button.
 * @param enabled - [Boolean] flag to enable or disable the Button.
 * @param loading - [Boolean] flag to enable the loading state.
 * @param interactionSource - [MutableInteractionSource] to be applied to the Button.
 */
@Composable
public fun LemonadeUi.Button(
    label: String,
    onClick: () -> Unit,
    leadingIcon: LemonadeIcons? = null,
    trailingIcon: LemonadeIcons? = null,
    variant: LemonadeButtonVariant = LemonadeButtonVariant.Primary,
    type: LemonadeButtonType = LemonadeButtonType.Solid,
    size: LemonadeButtonSize = LemonadeButtonSize.Large,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = resolveButtonColors(
        variant = variant,
        type = type,
    ).adjustedForDisabledFill(dimmed = !enabled || loading, variant = variant, type = type)
    CoreButton(
        colors = colors,
        size = size,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        loading = loading,
        leadingSlot = null,
        trailingSlot = null,
        expandContents = false,
        contentSlot = {
            ButtonAdaptiveContent(
                label = label,
                textStyle = size.contentData.textStyle,
                contentColor = colors.contentColor,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            )
        },
    )
}

/**
 * Lemonade labeled button component with slot-based leading and trailing content.
 *
 * @param label - [String] to be displayed as the Button's label.
 * @param onClick - Callback to be invoked when the Button is clicked.
 * @param modifier - [Modifier] to be applied to the Button.
 * @param variant - [LemonadeButtonVariant] for the color palette (Primary, Secondary, Neutral,
 * Critical, OnBrand, OnColor). OnBrand and OnColor are single Subtle treatments for placing a
 * button on top of a brand- or color-filled surface; they ignore [type].
 * @param type - [LemonadeButtonType] for the fill treatment (Solid, Subtle, Ghost).
 * @param size - [LemonadeButtonSize] to size the Button accordingly.
 * @param leadingSlot - Optional composable slot shown before the label.
 * @param trailingSlot - Optional composable slot shown after the label.
 * @param expandContents - [Boolean] flag to expand the content area.
 * @param enabled - [Boolean] flag to enable or disable the Button.
 * @param loading - [Boolean] flag to enable the loading state.
 * @param interactionSource - [MutableInteractionSource] to be applied to the Button.
 */
@Composable
public fun LemonadeUi.Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LemonadeButtonVariant = LemonadeButtonVariant.Primary,
    type: LemonadeButtonType = LemonadeButtonType.Solid,
    size: LemonadeButtonSize = LemonadeButtonSize.Large,
    leadingSlot: (@Composable RowScope.(colors: LemonadeButtonColors) -> Unit)? = null,
    trailingSlot: (@Composable RowScope.(colors: LemonadeButtonColors) -> Unit)? = null,
    expandContents: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = resolveButtonColors(
        variant = variant,
        type = type,
    ).adjustedForDisabledFill(dimmed = !enabled || loading, variant = variant, type = type)
    CoreButton(
        colors = colors,
        size = size,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick,
        loading = loading,
        modifier = modifier,
        leadingSlot = leadingSlot.takeIf { !loading },
        trailingSlot = trailingSlot.takeIf { !loading },
        expandContents = expandContents,
        contentSlot = {
            LemonadeUi.Text(
                text = label,
                textStyle = size.contentData.textStyle,
                color = colors.contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing200),
            )
        },
    )
}

@Stable
public class LemonadeButtonColors internal constructor(
    public val contentColor: Color,
    public val solidBackgroundColor: Color,
    public val pressedBackgroundColor: Color,
)

@Stable
private data class LemonadeButtonContentData(
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val requiredHeight: Dp,
    val minWidth: Dp,
    val shape: Shape,
    val textStyle: LemonadeTextStyle,
)

private val LemonadeButtonSize.contentData: LemonadeButtonContentData
    @Composable get() {
        return when (this) {
            LemonadeButtonSize.XSmall -> LemonadeButtonContentData(
                verticalPadding = LocalSpaces.current.spacing100,
                horizontalPadding = LocalSpaces.current.spacing200,
                requiredHeight = LocalSizes.current.size800,
                minWidth = LocalSizes.current.size1600,
                shape = LocalShapes.current.radius250,
                textStyle = LocalTypographies.current.bodySmallSemiBold,
            )

            LemonadeButtonSize.Small -> LemonadeButtonContentData(
                verticalPadding = LocalSpaces.current.spacing200,
                horizontalPadding = LocalSpaces.current.spacing300,
                requiredHeight = LocalSizes.current.size1000,
                minWidth = LocalSizes.current.size1600,
                shape = LocalShapes.current.radius300,
                textStyle = LocalTypographies.current.bodySmallSemiBold,
            )

            LemonadeButtonSize.Medium -> LemonadeButtonContentData(
                verticalPadding = LocalSpaces.current.spacing300,
                horizontalPadding = LocalSpaces.current.spacing400,
                requiredHeight = LocalSizes.current.size1200,
                minWidth = LocalSizes.current.size1600,
                shape = LocalShapes.current.radius350,
                textStyle = LocalTypographies.current.bodyMediumSemiBold,
            )

            LemonadeButtonSize.Large -> LemonadeButtonContentData(
                verticalPadding = LocalSpaces.current.spacing300,
                horizontalPadding = LocalSpaces.current.spacing400,
                requiredHeight = LocalSizes.current.size1400,
                minWidth = LocalSizes.current.size1600,
                shape = LocalShapes.current.radius400,
                textStyle = LocalTypographies.current.bodyMediumSemiBold,
            )
        }
    }

// MARK: - Color Resolution

@Composable
private fun resolveButtonColors(
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType,
): LemonadeButtonColors =
    when (variant) {
        LemonadeButtonVariant.Primary -> resolvePrimaryButtonColors(type = type)
        LemonadeButtonVariant.Secondary -> resolveSecondaryButtonColors(type = type)
        LemonadeButtonVariant.Neutral -> resolveNeutralButtonColors(type = type)
        LemonadeButtonVariant.Critical -> resolveCriticalButtonColors(type = type)
        LemonadeButtonVariant.OnBrand -> resolveOnBrandButtonColors()
        LemonadeButtonVariant.OnColor -> resolveOnColorButtonColors()
    }

// Secondary Solid's fill is an opaque dark inverse. Figma dims it to `opacity40` when dimmed
// (disabled or loading), while every other variant — and all content — dims to `opacityDisabled`.
// The dimming [Modifier.alpha] in [CoreButton] already multiplies the whole button by
// `opacityDisabled`, so pre-scale just this fill by the ratio of the two, letting them multiply out
// to `opacity40`.
@Composable
private fun LemonadeButtonColors.adjustedForDisabledFill(
    dimmed: Boolean,
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType,
): LemonadeButtonColors {
    val isSecondarySolid = variant == LemonadeButtonVariant.Secondary &&
        type == LemonadeButtonType.Solid
    if (!dimmed || !isSecondarySolid) return this
    val opacities = LocalOpacities.current
    val fillScale = opacities.base.opacity40 / opacities.state.opacityDisabled
    return LemonadeButtonColors(
        contentColor = contentColor,
        solidBackgroundColor = solidBackgroundColor.copy(
            alpha = solidBackgroundColor.alpha * fillScale,
        ),
        pressedBackgroundColor = pressedBackgroundColor,
    )
}

@Composable
private fun resolvePrimaryButtonColors(type: LemonadeButtonType): LemonadeButtonColors =
    when (type) {
        LemonadeButtonType.Solid -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentOnBrandHigh,
            solidBackgroundColor = LocalColors.current.background.bgBrand,
            pressedBackgroundColor = LocalColors.current.interaction.bgBrandInteractive,
        )

        LemonadeButtonType.Subtle -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentBrandHigh,
            solidBackgroundColor = LocalColors.current.background.bgBrandSubtle,
            pressedBackgroundColor = LocalColors.current.interaction.bgSubtlePressed,
        )

        LemonadeButtonType.Ghost -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentBrandHigh,
            solidBackgroundColor = Color.Transparent,
            pressedBackgroundColor = LocalColors.current.interaction.bgSubtlePressed,
        )
    }

@Composable
private fun resolveSecondaryButtonColors(type: LemonadeButtonType): LemonadeButtonColors =
    when (type) {
        LemonadeButtonType.Solid -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimaryInverse,
            solidBackgroundColor = LocalColors.current.background.bgSubtleInverse,
            pressedBackgroundColor = LocalColors.current.interaction.bgNeutralPressed,
        )

        LemonadeButtonType.Subtle -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimary,
            solidBackgroundColor = LocalColors.current.background.bgNeutralSubtle,
            pressedBackgroundColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
        )

        LemonadeButtonType.Ghost -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimary,
            solidBackgroundColor = Color.Transparent,
            pressedBackgroundColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
        )
    }

@Composable
private fun resolveNeutralButtonColors(type: LemonadeButtonType): LemonadeButtonColors =
    when (type) {
        LemonadeButtonType.Solid -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimary,
            solidBackgroundColor = LocalColors.current.background.bgElevated,
            pressedBackgroundColor = LocalColors.current.interaction.bgElevatedPressed,
        )

        LemonadeButtonType.Subtle -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimary,
            solidBackgroundColor = LocalColors.current.background.bgNeutralSubtle,
            pressedBackgroundColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
        )

        LemonadeButtonType.Ghost -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentPrimary,
            solidBackgroundColor = Color.Transparent,
            pressedBackgroundColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
        )
    }

@Composable
private fun resolveCriticalButtonColors(type: LemonadeButtonType): LemonadeButtonColors =
    when (type) {
        LemonadeButtonType.Solid -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentAlwaysLight,
            solidBackgroundColor = LocalColors.current.background.bgCritical,
            pressedBackgroundColor = LocalColors.current.interaction.bgCriticalInteractive,
        )

        LemonadeButtonType.Subtle -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentCritical,
            solidBackgroundColor = LocalColors.current.background.bgCriticalSubtle,
            pressedBackgroundColor = LocalColors.current.interaction.bgCriticalSubtleInteractive,
        )

        LemonadeButtonType.Ghost -> LemonadeButtonColors(
            contentColor = LocalColors.current.content.contentCritical,
            solidBackgroundColor = Color.Transparent,
            pressedBackgroundColor = LocalColors.current.interaction.bgCriticalSubtlePressed,
        )
    }

// On Brand / On Color are designed as a single Subtle treatment, meant to sit on top of a
// brand- or color-filled surface. They don't vary by [LemonadeButtonType], so the type is
// ignored and every type resolves to the same colors.
@Composable
private fun resolveOnBrandButtonColors(): LemonadeButtonColors =
    LemonadeButtonColors(
        contentColor = LocalColors.current.content.contentOnBrandHigh,
        solidBackgroundColor = LocalColors.current.background.bgBrandElevated,
        pressedBackgroundColor = LocalColors.current.interaction.bgBrandElevatedInteractive,
    )

@Composable
private fun resolveOnColorButtonColors(): LemonadeButtonColors =
    LemonadeButtonColors(
        contentColor = LocalColors.current.content.contentAlwaysLight,
        solidBackgroundColor = LocalColors.current.background.bgAlwaysLightMedium,
        pressedBackgroundColor = LocalColors.current.interaction.bgAlwaysLightMediumInteractive,
    )

@Composable
private fun CoreButton(
    contentSlot: @Composable RowScope.() -> Unit,
    leadingSlot: (@Composable RowScope.(LemonadeButtonColors) -> Unit)?,
    trailingSlot: (@Composable RowScope.(LemonadeButtonColors) -> Unit)?,
    onClick: () -> Unit,
    colors: LemonadeButtonColors,
    size: LemonadeButtonSize,
    expandContents: Boolean,
    enabled: Boolean,
    loading: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isPressed) {
            colors.pressedBackgroundColor
        } else {
            colors.solidBackgroundColor
        },
    )
    // When disabled or loading, wrap the fill and content in a single alpha graphics layer so the
    // whole button — container and content together — dims to 50% as one group, matching the Figma
    // disabled treatment (group opacity, letting the underlying surface show through).
    val disabledModifier = if (!enabled || loading) {
        Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
    } else {
        Modifier
    }
    val slotHeightModifier = if (leadingSlot != null || trailingSlot != null) {
        Modifier.height(intrinsicSize = IntrinsicSize.Min)
    } else {
        Modifier
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .defaultMinSize(minWidth = size.contentData.minWidth)
            .requiredHeightIn(min = size.contentData.requiredHeight)
            .then(other = slotHeightModifier)
            .clip(shape = size.contentData.shape)
            .then(other = disabledModifier)
            .clickable(
                enabled = enabled && !loading,
                onClick = onClick,
                interactionSource = interactionSource,
                role = Role.Button,
                indication = LocalEffects.current.interactionIndication,
            ).background(color = animatedBackgroundColor),
        content = {
            leadingSlot?.invoke(this, colors)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .then(
                        other = if (expandContents) {
                            Modifier.weight(1f)
                        } else {
                            Modifier
                        },
                    ).padding(
                        vertical = size.contentData.verticalPadding,
                        horizontal = size.contentData.horizontalPadding,
                    ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = contentSlot,
                    modifier = Modifier.alpha(alpha = if (loading) 0f else 1f),
                )
                if (loading) {
                    LemonadeUi.Spinner(
                        tint = colors.contentColor,
                    )
                }
            }
            trailingSlot?.invoke(this, colors)
        },
    )
}

// MARK: - Previews

private data class ButtonPreviewData(
    val leadingIcon: Boolean,
    val trailingIcon: Boolean,
    val enabled: Boolean,
    val loading: Boolean,
    val size: LemonadeButtonSize,
    val variant: LemonadeButtonVariant,
    val type: LemonadeButtonType,
)

private class ButtonPreviewProvider : PreviewParameterProvider<ButtonPreviewData> {
    override val values: Sequence<ButtonPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<ButtonPreviewData> =
        buildList {
            LemonadeButtonSize.entries.forEach { size ->
                LemonadeButtonVariant.entries.forEach { variant ->
                    LemonadeButtonType.entries.forEach { type ->
                        listOf(true, false).forEach { leadingIcon ->
                            listOf(true, false).forEach { trailingIcon ->
                                listOf(true, false).forEach { loading ->
                                    listOf(true, false).forEach { enabled ->
                                        add(
                                            element = ButtonPreviewData(
                                                leadingIcon = leadingIcon,
                                                trailingIcon = trailingIcon,
                                                enabled = enabled,
                                                loading = loading,
                                                size = size,
                                                variant = variant,
                                                type = type,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun LemonadeLabeledRadioButtonPreview(
    @PreviewParameter(ButtonPreviewProvider::class)
    previewData: ButtonPreviewData,
) {
    LemonadeUi.Button(
        label = "Label",
        onClick = { /* Nothing */ },
        leadingIcon = LemonadeIcons.Heart.takeIf { previewData.leadingIcon },
        trailingIcon = LemonadeIcons.Heart.takeIf { previewData.trailingIcon },
        enabled = previewData.enabled,
        loading = previewData.loading,
        size = previewData.size,
        variant = previewData.variant,
        type = previewData.type,
    )
}
