package com.teya.lemonade

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

/**
 * Icon component, to indication status and possible actions via iconography
 *
 * Icons are small images with predefined sizes that can or cannot be clickable and explains
 *  behaviour to the users visually.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Icon(
 *     text = LemonadeIcons.Close,
 *     contentDescription = "Close icon",
 *     size = LemonadeIconSize.Medium,
 *     tint = LocalColors.current.content.contentPrimary,
 * )
 * ```
 *
 * ## Parameters
 * @param icon: The [LemonadeIcons] to be displayed.
 * @param contentDescription A **localized** text that describes the icon or its action.
 *  Optional, but strongly recommended.
 * @param size: The [LemonadeAssetSize] to be applied to the icon. Defaults to [LemonadeAssetSize.Medium]
 * @param tint: The tint color to be applied to the icon. Defaults to the primary color of the [LemonadeTheme]
 * @param Modifier: Optional [Modifier] for additional styling and layout adjustments.
 */
@Composable
public fun LemonadeUi.Icon(
    icon: LemonadeIcons,
    contentDescription: String?,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    tint: Color = LocalColors.current.content.contentPrimary,
    modifier: Modifier = Modifier,
) {
    CoreIcon(
        painter = rememberAssetPainter(resource = icon.drawableResource),
        contentDescription = contentDescription,
        tint = tint,
        size = size.dp,
        modifier = modifier,
    )
}

/** An icon whose rendered size tracks the reader's text size, for icons beside sp-sized text. */
@Composable
internal fun LemonadeUi.TextTrackedIcon(
    icon: LemonadeIcons,
    contentDescription: String?,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    tint: Color = LocalColors.current.content.contentPrimary,
    modifier: Modifier = Modifier,
) {
    CoreIcon(
        painter = rememberAssetPainter(resource = icon.drawableResource),
        contentDescription = contentDescription,
        tint = tint,
        size = LocalDensity.current.textTrackedSize(base = size.dp),
        modifier = modifier,
    )
}

/** The size an sp value numerically equal to [base] resolves to at the current text size. */
internal fun Density.textTrackedSize(base: Dp): Dp = base.value.sp.toDp()

@Composable
private fun CoreIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = ColorFilter
            .tint(color = tint)
            .takeIf { tint != Color.Unspecified },
        modifier = modifier.requiredSize(size = size),
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

@LemonadePreview
@Composable
internal fun LemonadeIconPreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 24.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        LemonadeAssetSize.entries.forEach { size ->
            LemonadeUi.Icon(
                icon = LemonadeIcons.Airplane,
                contentDescription = "Example icon",
                tint = LocalColors.current.background.bgBrand,
                size = size,
            )
        }
    }
}
