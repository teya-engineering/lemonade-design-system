package com.teya.lemonade

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeSkeletonSize

/**
 * Internal skeleton shape variant used to differentiate rendering logic.
 */
private enum class SkeletonVariant {
    Line,
    Circle,
    Block,
}

/**
 * A skeleton loading placeholder displayed as a horizontal line with a shimmer animation.
 *
 * [LineSkeleton] is used to indicate that text content is loading. It displays a
 * horizontal rectangle with an animated fade effect that cycles smoothly, providing
 * a visual cue that data is being fetched or processed.
 *
 * The height of the skeleton is determined by the [size] parameter, while the width
 * can be controlled through the [modifier] parameter.
 *
 * ## Animation
 * The skeleton animates with a fade in/out effect cycling over 1000ms using an ease-in-out curve.
 * The opacity oscillates between 20% and 60% to create a subtle shimmer effect.
 *
 * ## Variants
 * For circular placeholders, use [CircleSkeleton].
 * For block/card placeholders, use [BlockSkeleton].
 *
 * ## Usage
 * ```kotlin
 * // Text line placeholder with default size
 * LemonadeUi.LineSkeleton(
 *     modifier = Modifier
 *         .fillMaxWidth()
 * )
 *
 * // Text line placeholder with custom size
 * LemonadeUi.LineSkeleton(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .padding(horizontal = LemonadeTheme.spaces.spacing400),
 *     size = LemonadeSkeletonSize.Small,
 * )
 * ```
 *
 * ## Parameters
 * @param modifier Optional [Modifier] for layout adjustments. Width can be customized here.
 * @param size The height of the skeleton line. Defaults to [LemonadeSkeletonSize.Medium].
 */
@Composable
public fun LemonadeUi.LineSkeleton(
    modifier: Modifier = Modifier,
    size: LemonadeSkeletonSize = LemonadeSkeletonSize.Medium,
) {
    CoreSkeleton(
        modifier = modifier,
        size = size,
        variant = SkeletonVariant.Line,
    )
}

/**
 * A skeleton loading placeholder displayed as a circle with a shimmer animation.
 *
 * [CircleSkeleton] is used to indicate that avatar or circular images are loading.
 * It displays a perfect circle with an animated fade effect that cycles smoothly,
 * providing a visual cue that data is being fetched or processed.
 *
 * The size of the circular skeleton is determined by the [size] parameter.
 * All dimensions (width and height) are equal to maintain a perfect circle.
 *
 * ## Animation
 * The skeleton animates with a fade in/out effect cycling over 1000ms using an ease-in-out curve.
 * The opacity oscillates between 20% and 60% to create a subtle shimmer effect.
 *
 * ## Variants
 * For text line placeholders, use [LineSkeleton].
 * For card/block placeholders, use [BlockSkeleton].
 *
 * ## Usage
 * ```kotlin
 * // Avatar placeholder with default size
 * LemonadeUi.CircleSkeleton(
 *     size = LemonadeSkeletonSize.Medium,
 * )
 *
 * // Smaller avatar placeholder
 * LemonadeUi.CircleSkeleton(
 *     size = LemonadeSkeletonSize.Small,
 * )
 * ```
 *
 * ## Parameters
 * @param modifier Optional [Modifier] for additional positioning or layout adjustments.
 * @param size The diameter of the circular skeleton. Defaults to [LemonadeSkeletonSize.Medium].
 */
@Composable
public fun LemonadeUi.CircleSkeleton(
    modifier: Modifier = Modifier,
    size: LemonadeSkeletonSize = LemonadeSkeletonSize.Medium,
) {
    CoreSkeleton(
        modifier = modifier,
        size = size,
        variant = SkeletonVariant.Circle,
    )
}

/**
 * A skeleton loading placeholder displayed as a large block/card with a shimmer animation.
 *
 * [BlockSkeleton] is used to indicate that card content, image content, or other
 * large block elements are loading. It displays a tall rectangle with an animated
 * fade effect that cycles smoothly, providing a visual cue that data is being
 * fetched or processed.
 *
 * The block skeleton has a fixed height (via the size1600 design token) suitable for
 * card or image placeholders, while the width can be controlled through the [modifier]
 * parameter. It uses a large rounded corner radius appropriate for prominent content areas.
 *
 * ## Animation
 * The skeleton animates with a fade in/out effect cycling over 1000ms using an ease-in-out curve.
 * The opacity oscillates between 20% and 60% to create a subtle shimmer effect.
 *
 * ## Variants
 * For text line placeholders, use [LineSkeleton].
 * For circular avatar placeholders, use [CircleSkeleton].
 *
 * ## Usage
 * ```kotlin
 * // Card/image placeholder with full width
 * LemonadeUi.BlockSkeleton(
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .padding(horizontal = LemonadeTheme.spaces.spacing400)
 * )
 *
 * // Customized block placeholder
 * LemonadeUi.BlockSkeleton(
 *     modifier = Modifier
 *         .fillMaxWidth(fraction = 0.9f)
 *         .padding(all = LemonadeTheme.spaces.spacing400)
 * )
 * ```
 *
 * ## Parameters
 * @param modifier Optional [Modifier] for layout adjustments. Width can be customized here.
 * @param size Currently unused for BlockSkeleton but kept for API consistency.
 *   Defaults to [LemonadeSkeletonSize.Medium].
 */
@Composable
public fun LemonadeUi.BlockSkeleton(
    modifier: Modifier = Modifier,
    size: LemonadeSkeletonSize = LemonadeSkeletonSize.Medium,
) {
    CoreSkeleton(
        modifier = modifier,
        size = size,
        variant = SkeletonVariant.Block,
    )
}

@Composable
private fun CoreSkeleton(
    size: LemonadeSkeletonSize,
    variant: SkeletonVariant,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SkeletonShimmer")

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = EaseInOut,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ShimmerOffset",
    )

    val baseColor = LocalColors.current.background.bgElevated
    val highlightColor = LocalColors.current.background.bgElevatedHigh

    val skeletonSize = size.toSkeletonSizeDimensions(variant = variant)
    val variantData = variant.variantData

    Box(
        modifier = modifier
            .height(height = skeletonSize.height)
            .width(width = skeletonSize.width)
            .padding(vertical = variantData.verticalSpacing)
            .drawWithCache {
                val drawSize = this.size
                val outline = variantData.radius.createOutline(
                    size = drawSize,
                    layoutDirection = layoutDirection,
                    density = this,
                )
                val cornerRadius = (outline as? Outline.Rounded)
                    ?.roundRect
                    ?.topLeftCornerRadius
                    ?: CornerRadius.Zero
                val brush = Brush.linearGradient(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    start = Offset(x = -drawSize.width / 2f, y = 0f),
                    end = Offset(x = drawSize.width / 2f, y = 0f),
                )
                onDrawBehind {
                    val panX = drawSize.width * shimmerOffset
                    translate(left = panX) {
                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(x = -panX, y = 0f),
                            size = drawSize,
                            cornerRadius = cornerRadius,
                        )
                    }
                }
            },
    )
}

private val LemonadeAssetSize.dp: Dp
    @Composable get() {
        return when (this) {
            LemonadeAssetSize.XSmall -> LocalSizes.current.size300
            LemonadeAssetSize.Small -> LocalSizes.current.size400
            LemonadeAssetSize.Medium -> LocalSizes.current.size500
            LemonadeAssetSize.Large -> LocalSizes.current.size600
            LemonadeAssetSize.XLarge -> LocalSizes.current.size800
            LemonadeAssetSize.XXLarge -> LocalSizes.current.size1000
            LemonadeAssetSize.XXXLarge -> LocalSizes.current.size1200
            LemonadeAssetSize.XXXXLarge -> LocalSizes.current.size1400
        }
    }

@Stable
private data class SkeletonSizeDimensions(
    val width: Dp,
    val height: Dp,
)

@Composable
private fun LemonadeSkeletonSize.toSkeletonSizeDimensions(variant: SkeletonVariant): SkeletonSizeDimensions =
    when (variant) {
        SkeletonVariant.Line -> toLineDimensions()
        SkeletonVariant.Block -> toBlockDimensions()
        SkeletonVariant.Circle -> toCircleDimensions()
    }

@Composable
private fun LemonadeSkeletonSize.toLineDimensions(): SkeletonSizeDimensions {
    val heightSize = when (this) {
        LemonadeSkeletonSize.XSmall -> LocalSizes.current.size400
        LemonadeSkeletonSize.Small -> LocalSizes.current.size500
        LemonadeSkeletonSize.Medium -> LocalSizes.current.size600
        LemonadeSkeletonSize.Large -> LocalSizes.current.size700
        LemonadeSkeletonSize.XLarge -> LocalSizes.current.size800
        LemonadeSkeletonSize.XXLarge -> LocalSizes.current.size900
        LemonadeSkeletonSize.XXXLarge -> LocalSizes.current.size1000
    }
    return SkeletonSizeDimensions(
        width = Dp.Unspecified,
        height = heightSize,
    )
}

@Composable
private fun toBlockDimensions(): SkeletonSizeDimensions =
    SkeletonSizeDimensions(
        width = Dp.Unspecified,
        height = LocalSizes.current.size1600,
    )

@Composable
private fun LemonadeSkeletonSize.toCircleDimensions(): SkeletonSizeDimensions {
    val size = when (this) {
        LemonadeSkeletonSize.XSmall -> LemonadeAssetSize.XSmall.dp
        LemonadeSkeletonSize.Small -> LemonadeAssetSize.Small.dp
        LemonadeSkeletonSize.Medium -> LemonadeAssetSize.Medium.dp
        LemonadeSkeletonSize.Large -> LemonadeAssetSize.Large.dp
        LemonadeSkeletonSize.XLarge -> LemonadeAssetSize.XLarge.dp
        LemonadeSkeletonSize.XXLarge -> LemonadeAssetSize.XXLarge.dp
        LemonadeSkeletonSize.XXXLarge -> LemonadeAssetSize.XXXLarge.dp
    }
    return SkeletonSizeDimensions(
        width = size,
        height = size,
    )
}

@Stable
private data class SkeletonVariantData(
    val verticalSpacing: Dp,
    val radius: Shape,
)

private val SkeletonVariant.variantData: SkeletonVariantData
    @Composable get() {
        return when (this) {
            SkeletonVariant.Line -> SkeletonVariantData(
                verticalSpacing = LocalSpaces.current.spacing100,
                radius = LocalShapes.current.radius100,
            )

            SkeletonVariant.Block -> SkeletonVariantData(
                verticalSpacing = LocalSpaces.current.spacing0,
                radius = LocalShapes.current.radius500,
            )

            SkeletonVariant.Circle -> SkeletonVariantData(
                verticalSpacing = LocalSpaces.current.spacing0,
                radius = LocalShapes.current.radiusFull,
            )
        }
    }

private data class SkeletonPreviewData(
    val variant: SkeletonVariant,
)

private class SkeletonPreviewProvider : PreviewParameterProvider<SkeletonPreviewData> {
    override val values: Sequence<SkeletonPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<SkeletonPreviewData> =
        SkeletonVariant.entries
            .map { entry ->
                SkeletonPreviewData(variant = entry)
            }.asSequence()
}

@Composable
@LemonadePreview
private fun SkeletonPreview(
    @PreviewParameter(SkeletonPreviewProvider::class)
    previewData: SkeletonPreviewData,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
    ) {
        when (previewData.variant) {
            SkeletonVariant.Line -> {
                LemonadeUi.LineSkeleton(
                    modifier = Modifier.width(width = 200.dp),
                    size = LemonadeSkeletonSize.Small,
                )
                LemonadeUi.LineSkeleton(
                    modifier = Modifier.width(width = 200.dp),
                    size = LemonadeSkeletonSize.Medium,
                )
                LemonadeUi.LineSkeleton(
                    modifier = Modifier.width(width = 200.dp),
                    size = LemonadeSkeletonSize.Large,
                )
            }

            SkeletonVariant.Circle -> {
                LemonadeUi.CircleSkeleton(
                    size = LemonadeSkeletonSize.Small,
                )
                LemonadeUi.CircleSkeleton(
                    size = LemonadeSkeletonSize.Medium,
                )
                LemonadeUi.CircleSkeleton(
                    size = LemonadeSkeletonSize.Large,
                )
            }

            SkeletonVariant.Block -> {
                LemonadeUi.BlockSkeleton(
                    modifier = Modifier.width(width = 200.dp),
                )
            }
        }
    }
}
