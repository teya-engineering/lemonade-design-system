package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.teya.lemonade.core.LemonadeAsset
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeBrandLogos
import com.teya.lemonade.core.LemonadeCountryFlags
import com.teya.lemonade.core.LemonadeIcons

/**
 * Displays any [LemonadeAsset] at a standard size.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Asset(
 *     asset = LemonadeCountryFlags.BRBrazil,
 *     size = LemonadeAssetSize.Medium,
 *     modifier = Modifier.clickable{ ... },
 * )
 * ```
 *
 * @param asset [LemonadeAsset] to display, e.g. [LemonadeIcons], [LemonadeCountryFlags] or
 *  [LemonadeBrandLogos]
 * @param size [LemonadeAssetSize] applied to the asset
 * @param contentDescription localized description of the asset. Brand logos and country flags
 *  fall back to the asset name when this is `null`
 * @param modifier optional [Modifier] for additional styling and layout adjustments
 * @param tint optional [Color] tint, applied only when the asset allows tinting
 */
@Composable
public fun LemonadeUi.Asset(
    asset: LemonadeAsset,
    size: LemonadeAssetSize,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    when (asset) {
        is LemonadeIcons -> {
            LemonadeUi.Icon(
                icon = asset,
                contentDescription = contentDescription,
                tint = tint,
                size = size,
                modifier = modifier,
            )
        }

        is LemonadeBrandLogos -> {
            LemonadeUi.BrandLogo(
                logo = asset,
                contentDescription = contentDescription
                    ?: asset.name,
                size = size,
                modifier = modifier,
            )
        }

        is LemonadeCountryFlags -> {
            LemonadeUi.CountryFlag(
                flag = asset,
                contentDescription = contentDescription
                    ?: asset.name,
                size = size,
                modifier = modifier,
            )
        }
    }
}
