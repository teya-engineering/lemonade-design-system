package com.teya.lemonade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice

/**
 * A versatile container used to display an icon, brand logo, or image.
 *  Supports consistent sizing and different tone of voice.
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     icon = LemonadeIcons.Heart,
 *     contentDescription = "Content Description",
 *     voice = SymbolContainerVoice.Info,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 * @param icon - [LemonadeIcons] to be displayed inside the container.
 * @param contentDescription - the **localized** content description for the [icon].
 * @param modifier - Optional, the [Modifier] to be applied to the base component.
 * @param voice - [SymbolContainerVoice] to define the tone of voice. This will effectively define
 *  color of the background and the tint for the [icon]. Defaults to [SymbolContainerVoice.Neutral].
 * @param size - [SymbolContainerSize] to define the container's size. Defaults to [SymbolContainerSize.Medium].
 * @param shape - [SymbolContainerShape] to define the container's shape. Defaults to [SymbolContainerShape.Circle].
 * @param badgeSlot - Optional composable slot for a badge overlay positioned at the bottom-right corner.
 */
@Composable
public fun LemonadeUi.SymbolContainer(
    icon: LemonadeIcons,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    voice: SymbolContainerVoice = SymbolContainerVoice.Neutral,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    CoreSymbolContainer(
        voice = voice,
        size = size,
        shape = shape,
        clipContent = false,
        modifier = modifier,
        badgeSlot = badgeSlot,
        contentSlot = { dimensions ->
            LemonadeUi.Icon(
                icon = icon,
                size = dimensions.lemonadeIconSize,
                contentDescription = contentDescription,
                tint = voice.tintColor,
            )
        },
    )
}

/**
 * A versatile container used to display an icon, brand logo, or image.
 *  Supports consistent sizing and different tone of voice.
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     text = "W",
 *     voice = SymbolContainerVoice.Info,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 * @param text - [Text] to be displayed inside the container.
 * @param modifier - Optional, the [Modifier] to be applied to the base component.
 * @param voice - [SymbolContainerVoice] to define the tone of voice. This will effectively define
 *  color of the background and the color for the [text]. Defaults to [SymbolContainerVoice.Neutral].
 * @param size - [SymbolContainerSize] to define the container's size. Defaults to [SymbolContainerSize.Medium].
 * @param shape - [SymbolContainerShape] to define the container's shape. Defaults to [SymbolContainerShape.Circle].
 * @param badgeSlot - Optional composable slot for a badge overlay positioned at the bottom-right corner.
 */
@Composable
public fun LemonadeUi.SymbolContainer(
    text: String,
    modifier: Modifier = Modifier,
    voice: SymbolContainerVoice = SymbolContainerVoice.Neutral,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    CoreSymbolContainer(
        voice = voice,
        size = size,
        shape = shape,
        clipContent = false,
        modifier = modifier,
        badgeSlot = badgeSlot,
        contentSlot = { dimensions ->
            LemonadeUi.Text(
                text = text,
                color = voice.tintColor,
                textStyle = dimensions.textStyle,
            )
        },
    )
}

/**
 * A versatile container used to display a [Painter] image, such as a brand logo or user avatar.
 *  Supports consistent sizing and different tone of voice.
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     painter = painterResource(Res.drawable.logo),
 *     contentDescription = "Brand Logo",
 *     fill = true,
 *     voice = SymbolContainerVoice.Neutral,
 *     size = SymbolContainerSize.Large,
 * )
 * ```
 * @param painter - [Painter] to be displayed inside the container. Rendered with its original colors (no tint).
 * @param contentDescription - the **localized** content description for the [painter].
 * @param fill - When `true`, the [painter] fills the entire container and is clipped by the [shape].
 *  When `false`, the [painter] is sized to the content area (same as icon/text overloads) and centered.
 * @param modifier - Optional, the [Modifier] to be applied to the base component.
 * @param voice - [SymbolContainerVoice] to define the tone of voice. This will effectively define
 *  color of the background. Defaults to [SymbolContainerVoice.Neutral].
 * @param size - [SymbolContainerSize] to define the container's size. Defaults to [SymbolContainerSize.Medium].
 * @param shape - [SymbolContainerShape] to define the container's shape. Defaults to [SymbolContainerShape.Circle].
 * @param badgeSlot - Optional composable slot for a badge overlay positioned at the bottom-right corner.
 */
@Composable
public fun LemonadeUi.SymbolContainer(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fill: Boolean = true,
    voice: SymbolContainerVoice = SymbolContainerVoice.Neutral,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    CoreSymbolContainer(
        voice = voice,
        size = size,
        shape = shape,
        clipContent = fill,
        modifier = modifier,
        badgeSlot = badgeSlot,
        contentSlot = { dimensions ->
            Image(
                painter = painter,
                contentDescription = contentDescription,
                alignment = Alignment.Center,
                contentScale = if (fill) {
                    ContentScale.Crop
                } else {
                    ContentScale.Fit
                },
                modifier = Modifier.then(
                    if (fill) {
                        Modifier.matchParentSize()
                    } else {
                        Modifier.requiredSize(size = dimensions.contentSize)
                    },
                ),
            )
        },
    )
}

/**
 * A versatile container used to display an icon, brand logo, or image.
 *  Supports consistent sizing and different tone of voice.
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     voice = SymbolContainerVoice.Info,
 *     size = SymbolContainerSize.Small,
 *     contentSlot = {
 *          Image(
 *              painter = ...,
 *              ...
 *          )
 *     },
 * )
 * ```
 * @param contentSlot - A Composable slot for generic content to be added as the content of the container.
 * @param modifier - Optional, the [Modifier] to be applied to the base component.
 * @param voice - [SymbolContainerVoice] to define the tone of voice. This will effectively define
 *  color of the background. Defaults to [SymbolContainerVoice.Neutral].
 * @param size - [SymbolContainerSize] to define the container's size. Defaults to [SymbolContainerSize.Medium].
 * @param shape - [SymbolContainerShape] to define the container's shape. Defaults to [SymbolContainerShape.Circle].
 * @param badgeSlot - Optional composable slot for a badge overlay positioned at the bottom-right corner.
 */
@Composable
public fun LemonadeUi.SymbolContainer(
    contentSlot: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    voice: SymbolContainerVoice = SymbolContainerVoice.Neutral,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    CoreSymbolContainer(
        voice = voice,
        size = size,
        shape = shape,
        clipContent = true,
        modifier = modifier,
        badgeSlot = badgeSlot,
        contentSlot = { dimensions ->
            Box(
                content = contentSlot,
                contentAlignment = Alignment.Center,
                modifier = Modifier.requiredSize(size = dimensions.contentSize),
            )
        },
    )
}

@Composable
private fun CoreSymbolContainer(
    contentSlot: @Composable BoxScope.(dimensions: SymbolContainerPlatformDimensions) -> Unit,
    voice: SymbolContainerVoice,
    size: SymbolContainerSize,
    shape: SymbolContainerShape,
    clipContent: Boolean,
    modifier: Modifier = Modifier,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    val dimensions = size.defaultSymbolContainerPlatformDimensions()
    val resolvedShape = shape.resolveShape(size)
    if (badgeSlot != null) {
        val density = LocalDensity.current
        val spaces = LocalSpaces.current
        LemonadeBadgeBox(
            modifier = modifier,
            badgeOffset = { badgeSize ->
                val startingHeight = with(density) {
                    badgeSize.height.toDp() - dimensions.containerSize
                }
                DpOffset(
                    x = spaces.spacing100,
                    y = startingHeight - spaces.spacing100,
                )
            },
            badge = badgeSlot,
            content = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.symbolContainerSurface(
                        voice = voice,
                        resolvedShape = resolvedShape,
                        containerSize = dimensions.containerSize,
                        clipContent = clipContent,
                    ),
                ) {
                    contentSlot(dimensions)
                }
            },
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.symbolContainerSurface(
                voice = voice,
                resolvedShape = resolvedShape,
                containerSize = dimensions.containerSize,
                clipContent = clipContent,
            ),
        ) {
            contentSlot(dimensions)
        }
    }
}

/** The container's fill, border and size; [clipContent] keeps the clip for content that overflows. */
@Composable
private fun Modifier.symbolContainerSurface(
    voice: SymbolContainerVoice,
    resolvedShape: Shape,
    containerSize: Dp,
    clipContent: Boolean,
): Modifier {
    val surface = if (clipContent) {
        this
            .clip(shape = resolvedShape)
            .background(color = voice.containerColor)
    } else {
        this.background(
            color = voice.containerColor,
            shape = resolvedShape,
        )
    }
    return surface
        .border(
            width = LocalBorderWidths.current.base.border25,
            color = voice.borderColor,
            shape = resolvedShape,
        ).requiredSize(size = containerSize)
}

private val SymbolContainerVoice.tintColor: Color
    @Composable get() {
        return when (this) {
            SymbolContainerVoice.Neutral -> LocalColors.current.content.contentPrimary
            SymbolContainerVoice.Critical -> LocalColors.current.content.contentCritical
            SymbolContainerVoice.Warning -> LocalColors.current.content.contentCaution
            SymbolContainerVoice.Info -> LocalColors.current.content.contentInfo
            SymbolContainerVoice.Positive -> LocalColors.current.content.contentPositive
            SymbolContainerVoice.Brand -> LocalColors.current.content.contentOnBrandHigh
            SymbolContainerVoice.BrandSubtle -> LocalColors.current.content.contentOnBrandHigh
        }
    }

private val SymbolContainerVoice.containerColor: Color
    @Composable get() {
        return when (this) {
            SymbolContainerVoice.Neutral -> LocalColors.current.background.bgNeutralSubtle
            SymbolContainerVoice.Critical -> LocalColors.current.background.bgCriticalSubtle
            SymbolContainerVoice.Warning -> LocalColors.current.background.bgCautionSubtle
            SymbolContainerVoice.Info -> LocalColors.current.background.bgInfoSubtle
            SymbolContainerVoice.Positive -> LocalColors.current.background.bgPositiveSubtle
            SymbolContainerVoice.Brand -> LocalColors.current.background.bgBrand
            SymbolContainerVoice.BrandSubtle -> LocalColors.current.background.bgBrandSubtle
        }
    }

private val SymbolContainerVoice.borderColor: Color
    @Composable get() {
        return when (this) {
            SymbolContainerVoice.Neutral -> LocalColors.current.border.borderNeutralLow
            SymbolContainerVoice.Critical -> LocalColors.current.border.borderCriticalSubtle
            SymbolContainerVoice.Warning -> LocalColors.current.border.borderCautionSubtle
            SymbolContainerVoice.Info -> LocalColors.current.border.borderInfoSubtle
            SymbolContainerVoice.Positive -> LocalColors.current.border.borderPositiveSubtle
            SymbolContainerVoice.Brand -> LocalColors.current.border.borderBrand
            SymbolContainerVoice.BrandSubtle -> LocalColors.current.border.borderOnBrandLow
        }
    }

@Composable
private fun SymbolContainerShape.resolveShape(size: SymbolContainerSize): Shape =
    when (this) {
        SymbolContainerShape.Circle -> LocalShapes.current.radiusFull
        SymbolContainerShape.Rounded -> when (size) {
            SymbolContainerSize.XSmall -> LocalShapes.current.radius200
            SymbolContainerSize.Small -> LocalShapes.current.radius250
            SymbolContainerSize.Medium -> LocalShapes.current.radius300
            SymbolContainerSize.Large -> LocalShapes.current.radius400
            SymbolContainerSize.XLarge -> LocalShapes.current.radius500
            SymbolContainerSize.XXLarge -> LocalShapes.current.radius500
        }
    }

private data class SymbolContainerPlatformDimensions(
    val containerSize: Dp,
    val contentSize: Dp,
    val lemonadeIconSize: LemonadeAssetSize,
    val textStyle: LemonadeTextStyle,
)

@Composable
private fun SymbolContainerSize.defaultSymbolContainerPlatformDimensions(): SymbolContainerPlatformDimensions =
    when (this) {
        SymbolContainerSize.XSmall -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size600,
            contentSize = LocalSizes.current.size300,
            lemonadeIconSize = LemonadeAssetSize.XSmall,
            textStyle = LocalTypographies.current.bodyXSmallSemiBold,
        )

        SymbolContainerSize.Small -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size800,
            contentSize = LocalSizes.current.size400,
            lemonadeIconSize = LemonadeAssetSize.Small,
            textStyle = LocalTypographies.current.bodySmallSemiBold,
        )

        SymbolContainerSize.Medium -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size1000,
            contentSize = LocalSizes.current.size500,
            lemonadeIconSize = LemonadeAssetSize.Medium,
            textStyle = LocalTypographies.current.bodySmallSemiBold,
        )

        SymbolContainerSize.Large -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size1200,
            contentSize = LocalSizes.current.size600,
            lemonadeIconSize = LemonadeAssetSize.Large,
            textStyle = LocalTypographies.current.bodyLargeSemiBold,
        )

        SymbolContainerSize.XLarge -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size1600,
            contentSize = LocalSizes.current.size800,
            lemonadeIconSize = LemonadeAssetSize.XLarge,
            textStyle = LocalTypographies.current.bodyXLargeSemiBold,
        )

        SymbolContainerSize.XXLarge -> SymbolContainerPlatformDimensions(
            containerSize = LocalSizes.current.size1800,
            contentSize = LocalSizes.current.size1000,
            lemonadeIconSize = LemonadeAssetSize.XXLarge,
            textStyle = LocalTypographies.current.headingSmall,
        )
    }

private data class SymbolContainerPreviewData(
    val content: Any,
    val size: SymbolContainerSize,
    val voice: SymbolContainerVoice,
    val shape: SymbolContainerShape,
)

private class SymbolContainerPreviewProvider :
    PreviewParameterProvider<SymbolContainerPreviewData> {
    override val values: Sequence<SymbolContainerPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SymbolContainerPreviewData> =
        buildList {
            SymbolContainerVoice.entries.forEach { voice ->
                listOf("A", LemonadeIcons.Heart).forEach { content ->
                    SymbolContainerSize.entries.forEach { size ->
                        SymbolContainerShape.entries.forEach { shape ->
                            add(
                                SymbolContainerPreviewData(
                                    content = content,
                                    size = size,
                                    voice = voice,
                                    shape = shape,
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
private fun SymbolContainerPreview(
    @PreviewParameter(SymbolContainerPreviewProvider::class)
    previewData: SymbolContainerPreviewData,
) {
    when (previewData.content) {
        is LemonadeIcons -> {
            LemonadeUi.SymbolContainer(
                icon = previewData.content,
                size = previewData.size,
                voice = previewData.voice,
                shape = previewData.shape,
                contentDescription = "Content Description",
            )
        }

        is String -> {
            LemonadeUi.SymbolContainer(
                text = previewData.content,
                size = previewData.size,
                voice = previewData.voice,
                shape = previewData.shape,
            )
        }
    }
}
