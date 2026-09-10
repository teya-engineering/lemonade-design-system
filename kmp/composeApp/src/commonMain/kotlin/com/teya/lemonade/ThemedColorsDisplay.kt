@file:OptIn(ExperimentalLemonadeApi::class)

package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val SwatchHeight: Dp = 162.dp

private val UppercaseLetter = Regex(pattern = "[A-Z]")

@Composable
internal fun ThemedColorsDisplay() {
    val themed = LemonadeTheme.themed
    val hues = remember(themed) { themedHues(themed = themed) }

    SampleScreenDisplayLazyColumn(
        title = "Themed Colors",
        background = LemonadeTheme.colors.background.bgDefault,
    ) {
        items(
            items = hues,
            key = { hue -> hue.title },
        ) { hue ->
            ThemedHueSection(hue = hue)
        }
    }
}

@Composable
private fun ThemedHueSection(hue: ThemedHue) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
        modifier = Modifier.padding(vertical = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = hue.title,
            textStyle = LemonadeTheme.typography.headingXXSmall,
            modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing100),
        )
        hue.swatches
            .chunked(size = 2)
            .forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
                ) {
                    row.forEach { swatch ->
                        ThemedSwatchBlock(
                            swatch = swatch,
                            modifier = Modifier.weight(weight = 1f),
                        )
                    }
                }
            }
    }
}

@Composable
private fun ThemedSwatchBlock(
    swatch: ThemedSwatch,
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
            text = swatch.slot,
            textStyle = LemonadeTheme.typography.bodyXSmallMedium,
            color = swatch.label,
        )
    }
}

private data class ThemedSwatch(
    val path: String,
    val slot: String,
    val fill: Color,
    val label: Color,
)

private data class ThemedHue(
    val title: String,
    val swatches: List<ThemedSwatch>,
)

private fun themedHues(themed: LemonadeThemedColors): List<ThemedHue> =
    listOf(
        "amber" to themed.amber,
        "blue" to themed.blue,
        "cyan" to themed.cyan,
        "fuchsia" to themed.fuchsia,
        "green" to themed.green,
        "greenLime" to themed.greenLime,
        "indigo" to themed.indigo,
        "neutral" to themed.neutral,
        "orange" to themed.orange,
        "pink" to themed.pink,
        "purple" to themed.purple,
        "red" to themed.red,
        "rose" to themed.rose,
        "teal" to themed.teal,
        "violet" to themed.violet,
        "yellow" to themed.yellow,
        "yellowLime" to themed.yellowLime,
    ).map { (name, color) ->
        ThemedHue(
            title = name
                .replace(
                    regex = UppercaseLetter,
                    replacement = " \$0",
                ).replaceFirstChar { char -> char.uppercase() },
            swatches = primarySwatches(
                name = name,
                color = color,
            ) + subtleSwatches(
                name = name,
                color = color,
            ),
        )
    }

private fun primarySwatches(
    name: String,
    color: ThemedPrimaryColor,
): List<ThemedSwatch> =
    listOf(
        ThemedSwatch(
            path = name,
            slot = "background",
            fill = color.background,
            label = color.onBackground,
        ),
        ThemedSwatch(
            path = name,
            slot = "border",
            fill = color.border,
            label = color.onBackground,
        ),
        ThemedSwatch(
            path = name,
            slot = "content",
            fill = color.content,
            label = color.contentInverse,
        ),
        ThemedSwatch(
            path = name,
            slot = "contentInverse",
            fill = color.contentInverse,
            label = color.onBackground,
        ),
        ThemedSwatch(
            path = name,
            slot = "onBackground",
            fill = color.onBackground,
            label = color.content,
        ),
        ThemedSwatch(
            path = name,
            slot = "backgroundHigh",
            fill = color.backgroundHigh,
            label = color.contentInverse,
        ),
        ThemedSwatch(
            path = name,
            slot = "onBackgroundHigh",
            fill = color.onBackgroundHigh,
            label = color.content,
        ),
    )

private fun subtleSwatches(
    name: String,
    color: ThemedPrimaryColor,
): List<ThemedSwatch> =
    listOf(
        ThemedSwatch(
            path = "$name.subtle",
            slot = "background",
            fill = color.subtle.background,
            label = color.subtle.onBackground,
        ),
        ThemedSwatch(
            path = "$name.subtle",
            slot = "border",
            fill = color.subtle.border,
            label = color.subtle.onBackground,
        ),
        ThemedSwatch(
            path = "$name.subtle",
            slot = "onBackground",
            fill = color.subtle.onBackground,
            label = color.contentInverse,
        ),
    )
