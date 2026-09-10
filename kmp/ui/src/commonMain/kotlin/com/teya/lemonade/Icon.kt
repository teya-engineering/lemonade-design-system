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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

/**
 * Shows status and possible actions through iconography.
 *
 * Icons come in predefined sizes and can be made clickable by the caller.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Icon(
 *     icon = LemonadeIcons.Close,
 *     contentDescription = "Close icon",
 *     size = LemonadeAssetSize.Medium,
 *     tint = LocalColors.current.content.contentPrimary,
 * )
 * ```
 *
 * @param icon [LemonadeIcons] to display
 * @param contentDescription a **localized** text that describes the icon or its action.
 *  Optional, but strongly recommended
 * @param size [LemonadeAssetSize] applied to the icon, defaults to [LemonadeAssetSize.Medium]
 * @param tint tint color applied to the icon, defaults to the primary content color of [LemonadeTheme]
 * @param modifier optional [Modifier] for additional styling and layout adjustments
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
        size = size,
        modifier = modifier,
    )
}

@Composable
private fun CoreIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color,
    size: LemonadeAssetSize,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = ColorFilter
            .tint(color = tint)
            .takeIf { tint != Color.Unspecified },
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
