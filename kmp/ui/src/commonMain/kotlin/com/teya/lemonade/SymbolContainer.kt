@file:Suppress("TooManyFunctions")

package com.teya.lemonade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
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
 * Shows an icon inside a sized container styled by its voice.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     icon = LemonadeIcons.Heart,
 *     contentDescription = "Content Description",
 *     voice = SymbolContainerVoice.Info,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 *
 * @param icon [LemonadeIcons] shown inside the container
 * @param contentDescription **localized** content description for the [icon]
 * @param modifier [Modifier] applied to the base component
 * @param voice [SymbolContainerVoice] driving the background color and the [icon] tint
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
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
    IconSymbolContainer(
        icon = icon,
        contentDescription = contentDescription,
        colors = voice.symbolContainerColors,
        modifier = modifier,
        size = size,
        shape = shape,
        badgeSlot = badgeSlot,
    )
}

/**
 * Shows text inside a sized container styled by its voice.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     text = "W",
 *     voice = SymbolContainerVoice.Info,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 *
 * @param text text shown inside the container
 * @param modifier [Modifier] applied to the base component
 * @param voice [SymbolContainerVoice] driving the background color and the [text] color
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
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
    TextSymbolContainer(
        text = text,
        colors = voice.symbolContainerColors,
        modifier = modifier,
        size = size,
        shape = shape,
        badgeSlot = badgeSlot,
    )
}

/**
 * Shows an icon inside a sized container styled by a themed hue, for colour that carries
 * application meaning the voices do not model - categories, per-role accents.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     icon = LemonadeIcons.Heart,
 *     contentDescription = "Content Description",
 *     theme = ThemedHue.Violet.subtle,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 *
 * @param icon [LemonadeIcons] shown inside the container
 * @param contentDescription **localized** content description for the [icon]
 * @param theme [ThemedStyle] driving the background, border and [icon] tint: a [ThemedHue] for its
 *  solid palette, or [ThemedHue.subtle] for its subtle one
 * @param modifier [Modifier] applied to the base component
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
 */
@ExperimentalLemonadeApi
@Composable
public fun LemonadeUi.SymbolContainer(
    icon: LemonadeIcons,
    contentDescription: String?,
    theme: ThemedStyle,
    modifier: Modifier = Modifier,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    IconSymbolContainer(
        icon = icon,
        contentDescription = contentDescription,
        colors = LocalThemedColors.current.symbolContainerColors(theme = theme),
        modifier = modifier,
        size = size,
        shape = shape,
        badgeSlot = badgeSlot,
    )
}

/**
 * Shows text inside a sized container styled by a themed hue, for colour that carries
 * application meaning the voices do not model - categories, per-role accents.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SymbolContainer(
 *     text = "W",
 *     theme = ThemedHue.Violet.subtle,
 *     size = SymbolContainerSize.Small,
 * )
 * ```
 *
 * @param text text shown inside the container
 * @param theme [ThemedStyle] driving the background, border and [text] color: a [ThemedHue] for its
 *  solid palette, or [ThemedHue.subtle] for its subtle one
 * @param modifier [Modifier] applied to the base component
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
 */
@ExperimentalLemonadeApi
@Composable
public fun LemonadeUi.SymbolContainer(
    text: String,
    theme: ThemedStyle,
    modifier: Modifier = Modifier,
    size: SymbolContainerSize = SymbolContainerSize.Medium,
    shape: SymbolContainerShape = SymbolContainerShape.Circle,
    badgeSlot: (@Composable BoxScope.() -> Unit)? = null,
) {
    TextSymbolContainer(
        text = text,
        colors = LocalThemedColors.current.symbolContainerColors(theme = theme),
        modifier = modifier,
        size = size,
        shape = shape,
        badgeSlot = badgeSlot,
    )
}

/**
 * Shows a [Painter] image, such as a brand logo or avatar, inside a sized container.
 *
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
 *
 * @param painter [Painter] shown inside the container, rendered with its original colors
 * @param contentDescription **localized** content description for the [painter]
 * @param fill when `true` the [painter] fills the container and is clipped by [shape]; when
 *  `false` it is sized to the content area and centered
 * @param modifier [Modifier] applied to the base component
 * @param voice [SymbolContainerVoice] driving the background color
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
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
        colors = voice.symbolContainerColors,
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
 * Shows custom content inside a sized container styled by its voice.
 *
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
 *
 * @param contentSlot composable slot for the container's content
 * @param modifier [Modifier] applied to the base component
 * @param voice [SymbolContainerVoice] driving the background color
 * @param size [SymbolContainerSize] driving the container's size
 * @param shape [SymbolContainerShape] driving the container's shape
 * @param badgeSlot composable slot for a badge overlay at the bottom-right corner
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
        colors = voice.symbolContainerColors,
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
private fun IconSymbolContainer(
    icon: LemonadeIcons,
    contentDescription: String?,
    colors: SymbolContainerColors,
    modifier: Modifier,
    size: SymbolContainerSize,
    shape: SymbolContainerShape,
    badgeSlot: (@Composable BoxScope.() -> Unit)?,
) {
    CoreSymbolContainer(
        colors = colors,
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
                tint = colors.content,
            )
        },
    )
}

@Composable
private fun TextSymbolContainer(
    text: String,
    colors: SymbolContainerColors,
    modifier: Modifier,
    size: SymbolContainerSize,
    shape: SymbolContainerShape,
    badgeSlot: (@Composable BoxScope.() -> Unit)?,
) {
    CoreSymbolContainer(
        colors = colors,
        size = size,
        shape = shape,
        clipContent = false,
        modifier = modifier,
        badgeSlot = badgeSlot,
        contentSlot = { dimensions ->
            LemonadeUi.Text(
                text = text,
                color = colors.content,
                textStyle = dimensions.textStyle,
            )
        },
    )
}

@Composable
private fun CoreSymbolContainer(
    contentSlot: @Composable BoxScope.(dimensions: SymbolContainerPlatformDimensions) -> Unit,
    colors: SymbolContainerColors,
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
                        colors = colors,
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
                colors = colors,
                resolvedShape = resolvedShape,
                containerSize = dimensions.containerSize,
                clipContent = clipContent,
            ),
        ) {
            contentSlot(dimensions)
        }
    }
}

@Composable
private fun Modifier.symbolContainerSurface(
    colors: SymbolContainerColors,
    resolvedShape: Shape,
    containerSize: Dp,
    clipContent: Boolean,
): Modifier {
    val surface = if (clipContent) {
        this
            .clip(shape = resolvedShape)
            .background(color = colors.background)
    } else {
        this.background(
            color = colors.background,
            shape = resolvedShape,
        )
    }
    return surface
        .border(
            width = LocalBorderWidths.current.base.border25,
            color = colors.border,
            shape = resolvedShape,
        ).requiredSize(size = containerSize)
}

internal data class SymbolContainerColors(
    val background: Color,
    val border: Color,
    val content: Color,
)

internal fun LemonadeThemedColors.symbolContainerColors(theme: ThemedStyle): SymbolContainerColors {
    val palette = this[theme]
    return SymbolContainerColors(
        background = palette.background,
        border = palette.border,
        content = palette.onBackground,
    )
}

private val SymbolContainerVoice.symbolContainerColors: SymbolContainerColors
    @Composable get() = SymbolContainerColors(
        background = containerColor,
        border = borderColor,
        content = tintColor,
    )

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
                listOf("A", LemonadeIcons.Heart)
                    .forEach { content ->
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

private class SymbolContainerThemedPreviewProvider : PreviewParameterProvider<ThemedHue> {
    override val values: Sequence<ThemedHue> = ThemedHue.entries.asSequence()
}

@LemonadePreview
@Composable
private fun SymbolContainerThemedPreview(
    @PreviewParameter(SymbolContainerThemedPreviewProvider::class)
    theme: ThemedHue,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200)) {
        LemonadeUi.SymbolContainer(
            icon = LemonadeIcons.Heart,
            contentDescription = null,
            theme = theme,
        )
        LemonadeUi.SymbolContainer(
            icon = LemonadeIcons.Heart,
            contentDescription = null,
            theme = theme.subtle,
        )
        LemonadeUi.SymbolContainer(
            text = "A",
            theme = theme.subtle,
        )
    }
}
