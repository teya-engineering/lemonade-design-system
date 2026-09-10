package com.teya.lemonade

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.CountryFlagShape
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeCountryFlags

/**
 * Shows a country flag at a standard size.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.CountryFlag(
 *     flag = LemonadeCountryFlags.BRBrazil,
 *     size = LemonadeAssetSize.Medium,
 *     shape = CountryFlagShape.Rounded,
 *     modifier = Modifier.clickable { ... },
 * )
 * ```
 *
 * @param flag [LemonadeCountryFlags] to show
 * @param contentDescription localizable content description for the [flag], defaults to
 *  [LemonadeCountryFlags.name]
 * @param size [LemonadeAssetSize] applied to the flag, defaults to [LemonadeAssetSize.Medium]
 * @param modifier optional [Modifier] for styling and layout
 * @param shape [CountryFlagShape] applied to the flag, defaults to [CountryFlagShape.Circular]
 */
@Composable
public fun LemonadeUi.CountryFlag(
    flag: LemonadeCountryFlags,
    contentDescription: String = flag.name,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    modifier: Modifier = Modifier,
    shape: CountryFlagShape = CountryFlagShape.Circular,
) {
    CoreCountryFlag(
        flag = flag,
        contentDescription = contentDescription,
        size = size,
        shape = shape,
        modifier = modifier,
    )
}

@Deprecated(
    message = "Use the overload with a shape parameter.",
    replaceWith = ReplaceWith(
        expression = "CountryFlag(flag, contentDescription, size, modifier, CountryFlagShape.Circular)",
    ),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.CountryFlag(
    flag: LemonadeCountryFlags,
    contentDescription: String = flag.name,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    modifier: Modifier = Modifier,
) {
    CountryFlag(
        flag = flag,
        contentDescription = contentDescription,
        size = size,
        modifier = modifier,
        shape = CountryFlagShape.Circular,
    )
}

@Composable
private fun CoreCountryFlag(
    flag: LemonadeCountryFlags,
    size: LemonadeAssetSize,
    shape: CountryFlagShape,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val resolvedShape = shape.resolveShape(size = size)
    Image(
        painter = rememberAssetPainter(resource = flag.drawableResource),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(shape = resolvedShape)
            .border(
                width = LocalBorderWidths.current.base.border25,
                color = LocalColors.current.border.borderNeutralMedium,
                shape = resolvedShape,
            ).requiredSize(size = size.dp),
    )
}

@Composable
private fun CountryFlagShape.resolveShape(size: LemonadeAssetSize): Shape =
    when (this) {
        CountryFlagShape.Circular -> LocalShapes.current.radiusFull
        CountryFlagShape.Rounded -> when (size) {
            LemonadeAssetSize.XSmall -> LocalShapes.current.radius100
            LemonadeAssetSize.Small -> LocalShapes.current.radius100
            LemonadeAssetSize.Medium -> LocalShapes.current.radius150
            LemonadeAssetSize.Large -> LocalShapes.current.radius200
            LemonadeAssetSize.XLarge -> LocalShapes.current.radius250
            LemonadeAssetSize.XXLarge -> LocalShapes.current.radius300
            LemonadeAssetSize.XXXLarge -> LocalShapes.current.radius400
            LemonadeAssetSize.XXXXLarge -> LocalShapes.current.radius500
        }
    }

private val LemonadeAssetSize.dp: Dp
    @Composable get() {
        return when (this) {
            LemonadeAssetSize.XSmall -> LocalSizes.current.size400
            LemonadeAssetSize.Small -> LocalSizes.current.size400
            LemonadeAssetSize.Medium -> LocalSizes.current.size500
            LemonadeAssetSize.Large -> LocalSizes.current.size600
            LemonadeAssetSize.XLarge -> LocalSizes.current.size800
            LemonadeAssetSize.XXLarge -> LocalSizes.current.size1000
            LemonadeAssetSize.XXXLarge -> LocalSizes.current.size1200
            LemonadeAssetSize.XXXXLarge -> LocalSizes.current.size1400
        }
    }

private data class CountryFlagPreviewData(
    val flag: LemonadeCountryFlags,
    val size: LemonadeAssetSize,
    val shape: CountryFlagShape,
)

private class CountryFlagPreviewProvider : PreviewParameterProvider<CountryFlagPreviewData> {
    override val values: Sequence<CountryFlagPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<CountryFlagPreviewData> =
        buildList {
            LemonadeCountryFlags.entries
                .take(5)
                .forEach { flag ->
                    LemonadeAssetSize.entries.forEach { size ->
                        CountryFlagShape.entries.forEach { shape ->
                            add(
                                CountryFlagPreviewData(
                                    flag = flag,
                                    size = size,
                                    shape = shape,
                                ),
                            )
                        }
                    }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun CountryFlagPreview(
    @PreviewParameter(CountryFlagPreviewProvider::class)
    previewData: CountryFlagPreviewData,
) {
    LemonadeUi.CountryFlag(
        flag = previewData.flag,
        size = previewData.size,
        shape = previewData.shape,
    )
}
