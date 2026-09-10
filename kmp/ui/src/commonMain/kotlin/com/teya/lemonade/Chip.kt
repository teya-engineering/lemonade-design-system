package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Shows information, triggers an action, or marks a selection in a compact element.
 *
 * Fits tags, filters and interactive choices in dense interfaces.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Chip(
 *     label = "Label",
 *     selected = true,
 *     leadingPainter = painterResource(resource = LemonadeIcons.Airplane.drawableResource),
 * )
 * ```
 *
 * @param label text shown in the chip
 * @param selected whether the chip is in the selected state
 * @param modifier [Modifier] applied to the chip's root container
 * @param leadingPainter optional [Painter] shown in the leading position of the chip
 * @param trailingIcon optional [LemonadeIcons] shown in the trailing position of the chip
 * @param counter optional number shown in the chip when it counts a subject
 * @param enabled whether the chip responds to interaction, styled as disabled when false
 * @param error shows the chip in an error state with a critical border and background, taking
 *  precedence over the [selected] styling when both are true
 * @param onChipClicked called when the chip is clicked. When null the chip is not clickable
 * @param onTrailingIconClick called when the [trailingIcon] is clicked, needs [trailingIcon] to
 *  be non-null
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *  the visual feedback
 */
@Composable
public fun LemonadeUi.Chip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    leadingPainter: Painter? = null,
    trailingIcon: LemonadeIcons? = null,
    counter: Int? = null,
    enabled: Boolean = true,
    error: Boolean = false,
    onChipClicked: (() -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    CoreChip(
        label = label,
        selected = selected,
        enabled = enabled,
        error = error,
        counter = counter,
        leadingSlot = if (leadingPainter != null) {
            {
                Image(
                    painter = leadingPainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(shape = LocalShapes.current.radiusFull)
                        .border(
                            width = LocalBorderWidths.current.base.border25,
                            shape = LocalShapes.current.radiusFull,
                            color = LocalColors.current.border.borderNeutralMedium,
                        ).requiredSize(size = 20.dp),
                )
            }
        } else {
            null
        },
        trailingSlot = if (trailingIcon != null) {
            {
                LemonadeUi.Icon(
                    icon = trailingIcon,
                    tint = LocalChipContentColor.current.invoke(),
                    size = LemonadeAssetSize.Small,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        onChipClicked = onChipClicked,
        onTrailingIconClick = onTrailingIconClick,
        modifier = modifier,
        interactionSource = interactionSource,
    )
}

/**
 * Shows information, triggers an action, or marks a selection in a compact element.
 *
 * Fits tags, filters and interactive choices in dense interfaces.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Chip(
 *     label = "Label",
 *     selected = true,
 *     leadingIcon = LemonadeIcons.Airplane,
 * )
 * ```
 *
 * @param label text shown in the chip
 * @param selected whether the chip is in the selected state
 * @param modifier [Modifier] applied to the chip's root container
 * @param leadingIcon optional [LemonadeIcons] shown in the leading position of the chip
 * @param trailingIcon optional [LemonadeIcons] shown in the trailing position of the chip
 * @param counter optional number shown in the chip when it counts a subject
 * @param enabled whether the chip responds to interaction, styled as disabled when false
 * @param error shows the chip in an error state with a critical border and background, taking
 *  precedence over the [selected] styling when both are true
 * @param onChipClicked called when the chip is clicked. When null the chip is not clickable
 * @param onTrailingIconClick called when the [trailingIcon] is clicked, needs [trailingIcon] to
 *  be non-null
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *  the visual feedback
 */
@Composable
public fun LemonadeUi.Chip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: LemonadeIcons? = null,
    trailingIcon: LemonadeIcons? = null,
    counter: Int? = null,
    enabled: Boolean = true,
    error: Boolean = false,
    onChipClicked: (() -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    CoreChip(
        label = label,
        selected = selected,
        enabled = enabled,
        error = error,
        counter = counter,
        leadingSlot = if (leadingIcon != null) {
            {
                LemonadeUi.Icon(
                    icon = leadingIcon,
                    tint = LocalChipLeadingIconColor.current.invoke(),
                    size = LemonadeAssetSize.Small,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        trailingSlot = if (trailingIcon != null) {
            {
                LemonadeUi.Icon(
                    icon = trailingIcon,
                    tint = LocalChipContentColor.current.invoke(),
                    size = LemonadeAssetSize.Small,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        onChipClicked = onChipClicked,
        onTrailingIconClick = onTrailingIconClick,
        modifier = modifier,
        interactionSource = interactionSource,
    )
}

/**
 * Shows information, triggers an action, or marks a selection in a compact element.
 *
 * Fits tags, filters and interactive choices in dense interfaces.
 *
 * Exposes generic [leadingContent] and [trailingContent] slots, so callers can place any
 * composable in either position — for example a [LemonadeUi.SymbolContainer] avatar as the
 * leading content and a close icon as the trailing content.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Chip(
 *     label = "joe@teya.com",
 *     selected = false,
 *     onTrailingIconClick = { /* remove */ },
 *     leadingContent = {
 *         LemonadeUi.SymbolContainer(
 *             text = "J",
 *             voice = SymbolContainerVoice.Neutral,
 *             size = SymbolContainerSize.XSmall,
 *             shape = SymbolContainerShape.Circle,
 *         )
 *     },
 *     trailingContent = {
 *         LemonadeUi.Icon(icon = LemonadeIcons.Times, contentDescription = null)
 *     },
 * )
 * ```
 *
 * @param label text shown in the chip
 * @param selected whether the chip is in the selected state
 * @param leadingContent content rendered in the leading position of the chip
 * @param trailingContent content rendered in the trailing position of the chip
 * @param modifier [Modifier] applied to the chip's root container
 * @param counter optional number shown in the chip
 * @param enabled whether the chip responds to interaction
 * @param error shows the chip in an error state
 * @param onChipClicked called when the chip is clicked
 * @param onTrailingIconClick called when [trailingContent] is clicked
 * @param interactionSource optional [MutableInteractionSource] observing hover and press to drive
 *  the visual feedback
 */
@Composable
public fun LemonadeUi.Chip(
    label: String,
    selected: Boolean,
    leadingContent: @Composable BoxScope.() -> Unit,
    trailingContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    counter: Int? = null,
    enabled: Boolean = true,
    error: Boolean = false,
    onChipClicked: (() -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    CoreChip(
        label = label,
        selected = selected,
        enabled = enabled,
        error = error,
        counter = counter,
        leadingSlot = leadingContent,
        trailingSlot = trailingContent,
        onChipClicked = onChipClicked,
        onTrailingIconClick = onTrailingIconClick,
        modifier = modifier,
        // Custom leading content (e.g. a SymbolContainer avatar) is larger than the icon-sized
        // actions box, so let it size to its content instead of being clipped into it.
        leadingSlotConstrained = false,
        interactionSource = interactionSource,
    )
}

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun CoreChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    error: Boolean,
    counter: Int?,
    leadingSlot: (@Composable BoxScope.() -> Unit)?,
    trailingSlot: (@Composable BoxScope.() -> Unit)?,
    onChipClicked: (() -> Unit)?,
    onTrailingIconClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    leadingSlotConstrained: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val platformDimensions = defaultChipDimensions()
    val props = getChipProps(
        selected = selected,
        error = error,
    )

    val isHover by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedContentColor by animateColorAsState(targetValue = props.contentColor)
    val animatedLeadingIconColor by animateColorAsState(targetValue = props.leadingIconColor)
    val animatedBorderColor by animateColorAsState(targetValue = props.borderColor)
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isHover || isPressed) {
            props.pressedBackgroundColor
        } else {
            props.backgroundColor
        },
    )
    CompositionLocalProvider(
        LocalChipContentColor provides { animatedContentColor },
        LocalChipLeadingIconColor provides { animatedLeadingIconColor },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .then(
                    other = if (!enabled) {
                        Modifier.alpha(alpha = LocalOpacities.current.state.opacityDisabled)
                    } else {
                        Modifier
                    },
                ).clip(shape = LocalShapes.current.radiusFull)
                .defaultMinSize(
                    minWidth = platformDimensions.minSize.width,
                    minHeight = platformDimensions.minSize.height,
                ).clickable(
                    role = Role.Button,
                    enabled = onChipClicked != null && enabled,
                    onClick = { onChipClicked?.invoke() },
                    interactionSource = interactionSource,
                    indication = LocalEffects.current.interactionIndication,
                ).background(
                    color = animatedBackgroundColor,
                    shape = LocalShapes.current.radiusFull,
                ).border(
                    width = LocalBorderWidths.current.base.border40,
                    color = animatedBorderColor,
                    shape = LocalShapes.current.radiusFull,
                ).padding(all = LocalSpaces.current.spacing200),
        ) {
            if (leadingSlot != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    content = leadingSlot,
                    modifier = if (leadingSlotConstrained) {
                        Modifier.requiredSize(size = platformDimensions.actionsSize)
                    } else {
                        Modifier
                    },
                )
            }

            LemonadeUi.Text(
                text = label,
                color = animatedContentColor,
                textStyle = platformDimensions.labelFontStyle,
                modifier = Modifier.padding(horizontal = LocalSpaces.current.spacing100),
            )

            if (counter != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = LocalSpaces.current.spacing100)
                        .defaultMinSize(
                            minWidth = LocalSizes.current.size450,
                            minHeight = LocalSizes.current.size400,
                        ).background(
                            color = LocalColors.current.background.bgBrand,
                            shape = LocalShapes.current.radiusFull,
                        ).padding(horizontal = LocalSpaces.current.spacing50),
                ) {
                    LemonadeUi.Text(
                        text = counter.toString(),
                        maxLines = 1,
                        textStyle = platformDimensions.counterFontStyle.textStyle.copy(
                            textAlign = TextAlign.Center,
                            color = LocalColors.current.content.contentOnBrandHigh,
                        ),
                    )
                }
            }

            if (trailingSlot != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    content = trailingSlot,
                    modifier = Modifier
                        .then(
                            other = if (onTrailingIconClick != null) {
                                Modifier.clickable(
                                    onClick = onTrailingIconClick,
                                    role = Role.Button,
                                    interactionSource = interactionSource,
                                    indication = LocalEffects.current.interactionIndication,
                                )
                            } else {
                                Modifier
                            },
                        ).requiredSize(size = platformDimensions.actionsSize),
                )
            }
        }
    }
}

private val LocalChipContentColor: ProvidableCompositionLocal<@Composable () -> Color> =
    staticCompositionLocalOf {
        { LocalColors.current.content.contentPrimary }
    }

private val LocalChipLeadingIconColor: ProvidableCompositionLocal<@Composable () -> Color> =
    staticCompositionLocalOf {
        { LocalColors.current.content.contentPrimary }
    }

internal data class ChipPlatformDimensions(
    val labelFontStyle: LemonadeTextStyle,
    val counterFontStyle: LemonadeTextStyle,
    val actionsSize: Dp,
    val minSize: DpSize,
)

@Composable
internal fun defaultChipDimensions(): ChipPlatformDimensions =
    ChipPlatformDimensions(
        labelFontStyle = LocalTypographies.current.bodySmallMedium,
        counterFontStyle = LocalTypographies.current.bodyXSmallSemiBold,
        actionsSize = 16.dp,
        minSize = DpSize(
            width = 64.dp,
            height = 32.dp,
        ),
    )

private data class ChipProps(
    val backgroundColor: Color,
    val pressedBackgroundColor: Color,
    val contentColor: Color,
    val leadingIconColor: Color,
    val borderColor: Color,
)

@Composable
private fun getChipProps(
    selected: Boolean,
    error: Boolean,
): ChipProps =
    when {
        error -> ChipProps(
            backgroundColor = LocalColors.current.background.bgCriticalSubtle,
            pressedBackgroundColor = LocalColors.current.interaction.bgCriticalSubtleInteractive,
            contentColor = LocalColors.current.content.contentPrimary,
            leadingIconColor = LocalColors.current.content.contentCritical,
            borderColor = LocalColors.current.border.borderCritical,
        )
        selected -> ChipProps(
            backgroundColor = LocalColors.current.background.bgBrandHigh,
            pressedBackgroundColor = LocalColors.current.interaction.bgBrandHighInteractive,
            contentColor = LocalColors.current.content.contentBrandInverse,
            leadingIconColor = LocalColors.current.content.contentBrandInverse,
            borderColor = Color.Transparent,
        )
        else -> ChipProps(
            backgroundColor = LocalColors.current.background.bgElevated,
            pressedBackgroundColor = LocalColors.current.interaction.bgSubtleInteractive,
            contentColor = LocalColors.current.content.contentPrimary,
            leadingIconColor = LocalColors.current.content.contentPrimary,
            borderColor = Color.Transparent,
        )
    }

private data class ChipPreviewData(
    val counter: Int?,
    val isSelected: Boolean,
    val enabled: Boolean,
    val error: Boolean,
    val leadingIcon: LemonadeIcons?,
    val trailingIcon: LemonadeIcons?,
)

private class ChipPreviewProvider : PreviewParameterProvider<ChipPreviewData> {
    override val values: Sequence<ChipPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<ChipPreviewData> =
        buildList {
            listOf(true, false)
                .forEach { enabled ->
                    listOf(true, false)
                        .forEach { withCounter ->
                            listOf(true, false)
                                .forEach { selected ->
                                    addIconVariants(
                                        enabled = enabled,
                                        withCounter = withCounter,
                                        selected = selected,
                                    )
                                }
                        }
                }
        }.asSequence()

    private fun MutableList<ChipPreviewData>.addIconVariants(
        enabled: Boolean,
        withCounter: Boolean,
        selected: Boolean,
    ) {
        listOf(true, false)
            .forEach { withLeadingIcon ->
                listOf(true, false)
                    .forEach { withTrailingIcon ->
                        add(
                            ChipPreviewData(
                                isSelected = selected,
                                enabled = enabled,
                                error = false,
                                counter = 5.takeIf { withCounter },
                                leadingIcon = LemonadeIcons.Airplane.takeIf { withLeadingIcon },
                                trailingIcon = LemonadeIcons.Airplane.takeIf { withTrailingIcon },
                            ),
                        )
                    }
            }
    }
}

@LemonadePreview
@Composable
private fun ChipPreview(
    @PreviewParameter(ChipPreviewProvider::class)
    previewData: ChipPreviewData,
) {
    LemonadeUi.Chip(
        label = "Label",
        selected = previewData.isSelected,
        enabled = previewData.enabled,
        error = previewData.error,
        counter = previewData.counter,
        leadingIcon = previewData.leadingIcon,
        trailingIcon = previewData.trailingIcon,
    )
}

@LemonadePreview
@Composable
private fun ChipErrorPreview() {
    LemonadeUi.Chip(
        label = "Error",
        selected = false,
        leadingIcon = null,
        error = true,
    )
}

@LemonadePreview
@Composable
private fun ChipErrorWithIconPreview() {
    LemonadeUi.Chip(
        label = "Error",
        selected = false,
        leadingIcon = LemonadeIcons.CircleAlert,
        error = true,
    )
}

@LemonadePreview
@Composable
private fun ChipErrorDisabledPreview() {
    LemonadeUi.Chip(
        label = "Error",
        selected = false,
        leadingIcon = null,
        enabled = false,
        error = true,
    )
}
