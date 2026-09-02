package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIconButtonShape
import com.teya.lemonade.core.LemonadeIcons

/**
 * Lemonade icon button component. Used for simple click actions with only an icon.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.IconButton(
 *   icon = LemonadeIcons.Heart,
 *   contentDescription = "Favorite",
 *   onClick = { println("icon button clicked!") },
 * )
 * ```
 *
 * @param icon - [LemonadeIcons] to be displayed as the Button's icon.
 * @param contentDescription - [String] content description for accessibility.
 * @param onClick - Callback to be invoked when the Button is clicked.
 * @param modifier - [Modifier] to be applied to the Button.
 * @param interactionSource - [MutableInteractionSource] to be applied to the Button.
 * @param enabled - [Boolean] flag to enable or disable the Button.
 * @param variant - [LemonadeButtonVariant] for the color palette (Primary, Secondary, Neutral, Critical).
 * @param type - [LemonadeButtonType] for the fill treatment (Solid, Subtle, Ghost).
 * @param size - [LemonadeButtonSize] to size the Button accordingly.
 * @param loading - [Boolean] flag to show a loading spinner.
 * @param shape - [LemonadeIconButtonShape] for the button shape (Rounded, Circular).
 */
@Composable
public fun LemonadeUi.IconButton(
    icon: LemonadeIcons,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    variant: LemonadeButtonVariant = LemonadeButtonVariant.Neutral,
    type: LemonadeButtonType = LemonadeButtonType.Subtle,
    size: LemonadeButtonSize = LemonadeButtonSize.Medium,
    loading: Boolean = false,
    shape: LemonadeIconButtonShape = LemonadeIconButtonShape.Rounded,
) {
    CoreIconButton(
        icon = icon,
        contentDescription = contentDescription,
        onClick = onClick,
        enabled = enabled,
        variant = variant,
        type = type,
        size = size,
        loading = loading,
        shape = shape,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

@Suppress("LongParameterList")
@Composable
private fun CoreIconButton(
    icon: LemonadeIcons,
    contentDescription: String?,
    onClick: () -> Unit,
    enabled: Boolean,
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType,
    size: LemonadeButtonSize,
    loading: Boolean,
    shape: LemonadeIconButtonShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    val colors = resolveColors(
        variant = variant,
        type = type,
    ).adjustedForDisabledFill(dimmed = !enabled || loading, variant = variant, type = type)
    val animatedBackgroundColor by colors.animatedBackground(interactionSource = interactionSource)
    val sizeData = size.toSizeData(shape = shape)

    // When disabled or loading, wrap the fill and content in a single alpha graphics layer so the
    // whole button — container and content together — dims to 50% as one group, matching the Figma
    // disabled treatment (group opacity, letting the underlying surface show through).
    val disabledModifier = if (!enabled || loading) {
        Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
    } else {
        Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .requiredSize(size = sizeData.size)
            .clip(shape = sizeData.shape)
            .then(other = disabledModifier)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = LocalEffects.current.interactionIndication,
                enabled = enabled && !loading,
            ).background(color = animatedBackgroundColor),
    ) {
        if (loading) {
            LemonadeUi.Spinner(
                size = sizeData.spinnerSize,
                tint = colors.contentColor,
            )
        } else {
            LemonadeUi.Icon(
                icon = icon,
                contentDescription = contentDescription,
                size = sizeData.iconSize,
                tint = colors.contentColor,
            )
        }
    }
}

// MARK: - Color Resolution

internal data class IconButtonColorData(
    val backgroundColor: Color,
    val backgroundHoverColor: Color,
    val backgroundPressedColor: Color,
    val contentColor: Color,
)

/**
 * The fill a button of these [IconButtonColorData] draws under a finger, a pointer, or neither.
 *
 * Shared so that anything standing in for an icon button — the capsule a [LemonadeUi.SwipeActionRow]
 * reveals, for one — is pressed and hovered the same way rather than restating the rule.
 */
@Composable
internal fun IconButtonColorData.animatedBackground(interactionSource: MutableInteractionSource): State<Color> {
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    return animateColorAsState(
        targetValue = when {
            pressed -> backgroundPressedColor
            hovered -> backgroundHoverColor
            else -> backgroundColor
        },
    )
}

/**
 * Shared with [LemonadeUi.SwipeActionRow], which draws its own stretchable capsule but has to stay
 * in step with the Solid icon button it stands in for.
 */
@Composable
internal fun resolveColors(
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType,
): IconButtonColorData =
    when (variant) {
        LemonadeButtonVariant.Primary -> resolvePrimaryColors(type = type)
        LemonadeButtonVariant.Secondary -> resolveSecondaryColors(type = type)
        LemonadeButtonVariant.Neutral -> resolveNeutralColors(type = type)
        LemonadeButtonVariant.Critical -> resolveCriticalColors(type = type)
        LemonadeButtonVariant.OnBrand -> resolveOnBrandColors()
        LemonadeButtonVariant.OnColor -> resolveOnColorColors()
    }

// Secondary Solid's fill is an opaque dark inverse. Figma dims it to `opacity40` when dimmed
// (disabled or loading), while every other variant — and all content — dims to `opacityDisabled`.
// The dimming [Modifier.alpha] in [CoreIconButton] already multiplies the whole button by
// `opacityDisabled`, so pre-scale just this fill by the ratio of the two, letting them multiply out
// to `opacity40`.
@Composable
private fun IconButtonColorData.adjustedForDisabledFill(
    dimmed: Boolean,
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType,
): IconButtonColorData {
    val isSecondarySolid = variant == LemonadeButtonVariant.Secondary &&
        type == LemonadeButtonType.Solid
    if (!dimmed || !isSecondarySolid) return this
    val opacities = LocalOpacities.current
    val fillScale = opacities.base.opacity40 / opacities.state.opacityDisabled
    return copy(
        backgroundColor = backgroundColor.copy(alpha = backgroundColor.alpha * fillScale),
    )
}

@Composable
private fun resolvePrimaryColors(type: LemonadeButtonType): IconButtonColorData =
    when (type) {
        LemonadeButtonType.Solid -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgBrand,
            backgroundHoverColor = LocalColors.current.interaction.bgBrandInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgBrandPressed,
            contentColor = LocalColors.current.content.contentOnBrandHigh,
        )
        LemonadeButtonType.Subtle -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgBrandSubtle,
            backgroundHoverColor = LocalColors.current.interaction.bgSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgSubtlePressed,
            contentColor = LocalColors.current.content.contentBrandHigh,
        )
        LemonadeButtonType.Ghost -> IconButtonColorData(
            backgroundColor = Color.Transparent,
            backgroundHoverColor = LocalColors.current.interaction.bgSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgSubtlePressed,
            contentColor = LocalColors.current.content.contentBrandHigh,
        )
    }

@Composable
private fun resolveSecondaryColors(type: LemonadeButtonType): IconButtonColorData =
    when (type) {
        LemonadeButtonType.Solid -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgSubtleInverse,
            backgroundHoverColor = LocalColors.current.interaction.bgNeutralInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgNeutralPressed,
            contentColor = LocalColors.current.content.contentPrimaryInverse,
        )
        LemonadeButtonType.Subtle -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgNeutralSubtle,
            backgroundHoverColor = LocalColors.current.interaction.bgNeutralSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
            contentColor = LocalColors.current.content.contentPrimary,
        )
        LemonadeButtonType.Ghost -> IconButtonColorData(
            backgroundColor = Color.Transparent,
            backgroundHoverColor = LocalColors.current.interaction.bgSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
            contentColor = LocalColors.current.content.contentPrimary,
        )
    }

@Composable
private fun resolveNeutralColors(type: LemonadeButtonType): IconButtonColorData =
    when (type) {
        LemonadeButtonType.Solid -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgElevated,
            backgroundHoverColor = LocalColors.current.interaction.bgElevatedInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgElevatedPressed,
            contentColor = LocalColors.current.content.contentPrimary,
        )
        LemonadeButtonType.Subtle -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgNeutralSubtle,
            backgroundHoverColor = LocalColors.current.interaction.bgNeutralSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
            contentColor = LocalColors.current.content.contentPrimary,
        )
        LemonadeButtonType.Ghost -> IconButtonColorData(
            backgroundColor = Color.Transparent,
            backgroundHoverColor = LocalColors.current.interaction.bgSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgNeutralSubtlePressed,
            contentColor = LocalColors.current.content.contentPrimary,
        )
    }

@Composable
private fun resolveCriticalColors(type: LemonadeButtonType): IconButtonColorData =
    when (type) {
        LemonadeButtonType.Solid -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgCritical,
            backgroundHoverColor = LocalColors.current.interaction.bgCriticalInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgCriticalPressed,
            contentColor = LocalColors.current.content.contentAlwaysLight,
        )
        LemonadeButtonType.Subtle -> IconButtonColorData(
            backgroundColor = LocalColors.current.background.bgCriticalSubtle,
            backgroundHoverColor = LocalColors.current.interaction.bgCriticalSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgCriticalSubtlePressed,
            contentColor = LocalColors.current.content.contentCritical,
        )
        LemonadeButtonType.Ghost -> IconButtonColorData(
            backgroundColor = Color.Transparent,
            backgroundHoverColor = LocalColors.current.interaction.bgSubtleInteractive,
            backgroundPressedColor = LocalColors.current.interaction.bgCriticalSubtlePressed,
            contentColor = LocalColors.current.content.contentCritical,
        )
    }

// On Brand / On Color are designed as a single Subtle treatment, meant to sit on top of a
// brand- or color-filled surface. They don't vary by [LemonadeButtonType], so the type is
// ignored and every type resolves to the same colors. Their pressed state mirrors the labeled
// [LemonadeUi.Button] (the base), which uses the interactive token rather than a dedicated pressed
// one.
@Composable
private fun resolveOnBrandColors(): IconButtonColorData =
    IconButtonColorData(
        backgroundColor = LocalColors.current.background.bgBrandElevated,
        backgroundHoverColor = LocalColors.current.interaction.bgBrandElevatedInteractive,
        backgroundPressedColor = LocalColors.current.interaction.bgBrandElevatedInteractive,
        contentColor = LocalColors.current.content.contentOnBrandHigh,
    )

@Composable
private fun resolveOnColorColors(): IconButtonColorData =
    IconButtonColorData(
        backgroundColor = LocalColors.current.background.bgAlwaysLightMedium,
        backgroundHoverColor = LocalColors.current.interaction.bgAlwaysLightMediumInteractive,
        backgroundPressedColor = LocalColors.current.interaction.bgAlwaysLightMediumInteractive,
        contentColor = LocalColors.current.content.contentAlwaysLight,
    )

// MARK: - Size Data

private data class IconButtonSizeData(
    val iconSize: LemonadeAssetSize,
    val spinnerSize: LemonadeAssetSize,
    val size: Dp,
    val shape: Shape,
)

@Composable
private fun LemonadeButtonSize.toSizeData(shape: LemonadeIconButtonShape): IconButtonSizeData =
    when (this) {
        LemonadeButtonSize.Large -> IconButtonSizeData(
            iconSize = LemonadeAssetSize.Large,
            spinnerSize = LemonadeAssetSize.Small,
            size = LocalSizes.current.size1400,
            shape = shape.resolveShape(roundedShape = LocalShapes.current.radius400),
        )

        LemonadeButtonSize.Medium -> IconButtonSizeData(
            iconSize = LemonadeAssetSize.Large,
            spinnerSize = LemonadeAssetSize.Small,
            size = LocalSizes.current.size1200,
            shape = shape.resolveShape(roundedShape = LocalShapes.current.radius350),
        )

        LemonadeButtonSize.Small -> IconButtonSizeData(
            iconSize = LemonadeAssetSize.Medium,
            spinnerSize = LemonadeAssetSize.XSmall,
            size = LocalSizes.current.size1000,
            shape = shape.resolveShape(roundedShape = LocalShapes.current.radius300),
        )

        LemonadeButtonSize.XSmall -> IconButtonSizeData(
            iconSize = LemonadeAssetSize.Small,
            spinnerSize = LemonadeAssetSize.XSmall,
            size = LocalSizes.current.size800,
            shape = shape.resolveShape(roundedShape = LocalShapes.current.radius250),
        )
    }

@Composable
private fun LemonadeIconButtonShape.resolveShape(roundedShape: Shape): Shape =
    when (this) {
        LemonadeIconButtonShape.Rounded -> roundedShape
        LemonadeIconButtonShape.Circular -> LocalShapes.current.radiusFull
    }

// MARK: - Previews

private data class IconButtonPreviewData(
    val size: LemonadeButtonSize,
    val variant: LemonadeButtonVariant,
    val type: LemonadeButtonType,
    val enabled: Boolean,
)

private class IconButtonPreviewProvider : PreviewParameterProvider<IconButtonPreviewData> {
    override val values: Sequence<IconButtonPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<IconButtonPreviewData> =
        buildList {
            listOf(true, false).forEach { enabled ->
                LemonadeButtonSize.entries.forEach { size ->
                    LemonadeButtonVariant.entries.forEach { variant ->
                        LemonadeButtonType.entries.forEach { type ->
                            add(
                                element = IconButtonPreviewData(
                                    size = size,
                                    variant = variant,
                                    type = type,
                                    enabled = enabled,
                                ),
                            )
                        }
                    }
                }
            }
        }.asSequence()
}

@Composable
@LemonadePreview
private fun IconButtonPreview(
    @PreviewParameter(IconButtonPreviewProvider::class)
    previewData: IconButtonPreviewData,
) {
    LemonadeUi.IconButton(
        icon = LemonadeIcons.Heart,
        size = previewData.size,
        variant = previewData.variant,
        type = previewData.type,
        enabled = previewData.enabled,
        contentDescription = null,
        onClick = {},
    )
}
