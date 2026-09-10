package com.teya.lemonade

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeBrandLogos

/**
 * Shows a card scheme logo at a standard size.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.BrandLogo(
 *     logo = LemonadeBrandLogos.Visa,
 *     size = LemonadeAssetSize.Medium,
 *     modifier = Modifier.clickable { ... },
 * )
 * ```
 *
 * @param logo [LemonadeBrandLogos] to show
 * @param contentDescription localizable content description for the [logo], defaults to
 *  [LemonadeBrandLogos.name]
 * @param size [LemonadeAssetSize] applied to the logo, defaults to [LemonadeAssetSize.Medium]
 * @param modifier optional [Modifier] for styling and layout
 */
@Composable
public fun LemonadeUi.BrandLogo(
    logo: LemonadeBrandLogos,
    contentDescription: String = logo.name,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    modifier: Modifier = Modifier,
) {
    CoreBrandLogo(
        logo = logo,
        size = size,
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

@Composable
private fun CoreBrandLogo(
    logo: LemonadeBrandLogos,
    size: LemonadeAssetSize,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    // Resolved from the theme rather than the system appearance: a consumer may pass
    // LemonadeDarkTheme while the system is light, and the logo must follow the theme.
    val resource = if (LemonadeTheme.colors.isDark) {
        logo.darkDrawableResource
    } else {
        logo.drawableResource
    }
    Image(
        painter = rememberAssetPainter(resource = resource),
        contentDescription = contentDescription,
        modifier = modifier.requiredSize(size = size.dp),
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

private data class BrandLogoPreviewData(
    val logo: LemonadeBrandLogos,
    val size: LemonadeAssetSize,
)

private class BrandLogoPreviewProvider : PreviewParameterProvider<BrandLogoPreviewData> {
    override val values: Sequence<BrandLogoPreviewData> = buildAllVariants()

    private fun buildAllVariants(): Sequence<BrandLogoPreviewData> =
        buildList {
            LemonadeBrandLogos.entries
                .take(5)
                .forEach { logo ->
                    LemonadeAssetSize.entries.forEach { size ->
                        add(
                            BrandLogoPreviewData(
                                logo = logo,
                                size = size,
                            ),
                        )
                    }
                }
        }.asSequence()
}

@LemonadePreview
@Composable
private fun CountryFlagPreview(
    @PreviewParameter(BrandLogoPreviewProvider::class)
    previewData: BrandLogoPreviewData,
) {
    LemonadeUi.BrandLogo(
        logo = previewData.logo,
        size = previewData.size,
    )
}
