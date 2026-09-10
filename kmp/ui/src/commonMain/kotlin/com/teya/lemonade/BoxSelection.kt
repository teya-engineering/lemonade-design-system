package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeBoxSelectionBackground
import com.teya.lemonade.core.LemonadeBoxSelectionVariant
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeRadius
import com.teya.lemonade.core.LemonadeSpaces

/**
 * Presents an option as a selectable container and captures the user's choice.
 *
 * Supports single or multiple selection. Renders only the box; what goes inside it is entirely
 * up to the caller.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.BoxSelection(
 *   isSelected = isSelected,
 *   onClick = { isSelected = !isSelected },
 * ) {
 *   LemonadeUi.Text(text = "Option")
 * }
 * ```
 *
 * @param modifier [Modifier] applied to the box
 * @param variant [LemonadeBoxSelectionVariant] styling the box
 * @param background [LemonadeBoxSelectionBackground] behind the content. Only applied by
 *  [LemonadeBoxSelectionVariant.Filled]; a selected box always falls back to the default
 *  background
 * @param isSelected whether the box shows the selected styling. Changing it plays a selection
 *  haptic
 * @param enabled whether the box responds to clicks
 * @param contentPadding [LemonadeSpaces] token applied between the box and its content
 * @param radius [LemonadeRadius] token applied to the box corners
 * @param onClick called when the box is clicked. When null the box is not clickable, leaving the
 *  interaction to the content
 * @param interactionSource [MutableInteractionSource] applied to the box
 * @param content content rendered inside the box, laid out from the top-start corner. Use
 *  [BoxScope.align] to place it elsewhere when the box is wider than its content
 */
@Suppress("LongParameterList")
@Composable
public fun LemonadeUi.BoxSelection(
    modifier: Modifier = Modifier,
    variant: LemonadeBoxSelectionVariant = LemonadeBoxSelectionVariant.Filled,
    background: LemonadeBoxSelectionBackground = LemonadeBoxSelectionBackground.Default,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    contentPadding: LemonadeSpaces = LemonadeSpaces.Spacing300,
    radius: LemonadeRadius = LemonadeRadius.Radius500,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable BoxScope.() -> Unit,
) {
    SelectionHapticEffect(selected = isSelected)

    val shape = remember(radius) { RoundedCornerShape(size = radius.dp) }
    val style = boxSelectionStyle(
        variant = variant,
        background = background,
        isSelected = isSelected,
    )

    val isPressed by interactionSource.collectIsPressedAsState()
    val targetAlpha = when {
        !enabled -> LocalOpacities.current.state.opacityDisabled
        isPressed -> LocalOpacities.current.state.opacityPressed
        else -> LocalOpacities.current.base.opacity100
    }

    val animatedAlpha by animateFloatAsState(targetValue = targetAlpha)
    val animatedBackgroundColor by animateColorAsState(targetValue = style.backgroundColor)
    val animatedBorderColor by animateColorAsState(targetValue = style.borderColor)
    val animatedBorderWidth by animateDpAsState(targetValue = style.borderWidth)

    Box(
        content = content,
        modifier = modifier
            // One layer for both the alpha and the corner clip. Kept as a single
            // `graphicsLayer` rather than `.alpha().clip()` because `Modifier.alpha(1f)` is a
            // no-op, so the pair would add and drop a layer on every press; reading the alpha
            // in the lambda also keeps the press fade off the recomposition path.
            .graphicsLayer {
                alpha = animatedAlpha
                this.shape = shape
                clip = true
            }.background(
                color = animatedBackgroundColor,
                shape = shape,
            ).border(
                width = animatedBorderWidth,
                color = animatedBorderColor,
                shape = shape,
            )
            // The slot content is arbitrary, so it cannot be relied on to carry the selected
            // state. Publish it here or a screen reader announces the box without saying whether
            // it is the chosen one.
            .semantics { selected = isSelected }
            .then(
                other = if (onClick != null) {
                    // Role stays Button rather than RadioButton or Checkbox because the box
                    // serves both single and multiple selection.
                    Modifier.clickable(
                        onClick = onClick,
                        enabled = enabled,
                        role = Role.Button,
                        interactionSource = interactionSource,
                        indication = LocalEffects.current.interactionIndication,
                    )
                } else {
                    Modifier
                },
            ).padding(all = contentPadding.dp),
    )
}

private data class BoxSelectionStyle(
    val backgroundColor: Color,
    val borderColor: Color,
    val borderWidth: Dp,
)

@Composable
private fun boxSelectionStyle(
    variant: LemonadeBoxSelectionVariant,
    background: LemonadeBoxSelectionBackground,
    isSelected: Boolean,
): BoxSelectionStyle {
    val selectedBorderColor = LocalColors.current.border.borderSelected
    val selectedBorderWidth = LocalBorderWidths.current.base.border50

    return when (variant) {
        LemonadeBoxSelectionVariant.Filled -> BoxSelectionStyle(
            // A selected box drops back to the default background so the selected border reads
            // against it, whichever background the caller asked for.
            backgroundColor = if (isSelected) {
                LocalColors.current.background.bgDefault
            } else {
                background.color
            },
            borderColor = if (isSelected) selectedBorderColor else Color.Transparent,
            borderWidth = if (isSelected) selectedBorderWidth else 0.dp,
        )
        LemonadeBoxSelectionVariant.Outlined -> BoxSelectionStyle(
            backgroundColor = Color.Transparent,
            borderColor = if (isSelected) {
                selectedBorderColor
            } else {
                LocalColors.current.border.borderNeutralMedium
            },
            borderWidth = if (isSelected) {
                selectedBorderWidth
            } else {
                LocalBorderWidths.current.base.border40
            },
        )
    }
}

private val LemonadeBoxSelectionBackground.color: Color
    @Composable get() {
        return when (this) {
            LemonadeBoxSelectionBackground.Default -> LocalColors.current.background.bgDefault
            LemonadeBoxSelectionBackground.Elevated -> LocalColors.current.background.bgElevated
        }
    }

private data class BoxSelectionPreviewData(
    val variant: LemonadeBoxSelectionVariant,
    val background: LemonadeBoxSelectionBackground,
    val isSelected: Boolean,
    val enabled: Boolean,
)

private class BoxSelectionPreviewProvider : PreviewParameterProvider<BoxSelectionPreviewData> {
    override val values: Sequence<BoxSelectionPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<BoxSelectionPreviewData> =
        buildList {
            LemonadeBoxSelectionVariant.entries.forEach { variant ->
                LemonadeBoxSelectionBackground.entries.forEach { background ->
                    listOf(true, false)
                        .forEach { isSelected ->
                            listOf(true, false)
                                .forEach { enabled ->
                                    add(
                                        BoxSelectionPreviewData(
                                            variant = variant,
                                            background = background,
                                            isSelected = isSelected,
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
private fun BoxSelectionPreview(
    @PreviewParameter(BoxSelectionPreviewProvider::class)
    previewData: BoxSelectionPreviewData,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = LocalColors.current.background.bgSubtle)
            .padding(all = LocalSpaces.current.spacing600),
    ) {
        LemonadeUi.BoxSelection(
            variant = previewData.variant,
            background = previewData.background,
            isSelected = previewData.isSelected,
            enabled = previewData.enabled,
            onClick = {},
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
                modifier = Modifier.width(width = LocalSizes.current.size1800),
            ) {
                LemonadeUi.Icon(
                    icon = LemonadeIcons.Heart,
                    contentDescription = null,
                    size = LemonadeAssetSize.Medium,
                    tint = LocalColors.current.content.contentPrimary,
                )

                LemonadeUi.Text(
                    text = "Option",
                    textStyle = LocalTypographies.current.bodySmallMedium,
                    color = LocalColors.current.content.contentPrimary,
                )
            }
        }
    }
}
