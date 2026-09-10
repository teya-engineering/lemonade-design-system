package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val COLUMNS = 2

private val SwatchHeight: Dp = 162.dp

internal data class ColorSwatch(
    val path: String,
    val name: String,
    val fill: Color,
    val label: Color,
)

internal data class ColorSwatchGroup(
    val title: String,
    val swatches: List<ColorSwatch>,
)

@Composable
internal fun ColorSwatchSection(group: ColorSwatchGroup) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
        modifier = Modifier.padding(vertical = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = group.title,
            textStyle = LemonadeTheme.typography.headingXXSmall,
            modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing100),
        )
        group.swatches.chunked(size = COLUMNS).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
            ) {
                row.forEach { swatch ->
                    ColorSwatchBlock(
                        swatch = swatch,
                        modifier = Modifier.weight(weight = 1f),
                    )
                }
                repeat(times = COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(weight = 1f))
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchBlock(
    swatch: ColorSwatch,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .height(height = SwatchHeight)
            .clip(shape = LemonadeTheme.shapes.radius600)
            .background(color = swatch.fill)
            .padding(
                horizontal = LemonadeTheme.spaces.spacing400,
                vertical = LemonadeTheme.spaces.spacing500,
            ),
    ) {
        LemonadeUi.Text(
            text = swatch.path,
            textStyle = LemonadeTheme.typography.bodyXSmallRegular,
            color = swatch.label,
            modifier = Modifier.alpha(alpha = LemonadeTheme.opacities.base.opacity70),
        )
        LemonadeUi.Text(
            text = swatch.name,
            textStyle = LemonadeTheme.typography.bodyXSmallMedium,
            color = swatch.label,
        )
    }
}
