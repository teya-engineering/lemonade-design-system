@file:OptIn(ExperimentalLemonadeApi::class)

package com.teya.lemonade

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

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
            ColorSwatchSection(
                title = hue.title,
                swatches = hue.swatches,
            )
        }
    }
}

private fun themedHues(themed: LemonadeThemedColors): List<ColorSwatchGroup> =
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
        ColorSwatchGroup(
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
): List<ColorSwatch> =
    listOf(
        ColorSwatch(
            path = name,
            name = "background",
            fill = color.background,
            label = color.onBackground,
        ),
        ColorSwatch(
            path = name,
            name = "border",
            fill = color.border,
            label = color.onBackground,
        ),
        ColorSwatch(
            path = name,
            name = "content",
            fill = color.content,
            label = color.contentInverse,
        ),
        ColorSwatch(
            path = name,
            name = "contentInverse",
            fill = color.contentInverse,
            label = color.onBackground,
        ),
        ColorSwatch(
            path = name,
            name = "onBackground",
            fill = color.onBackground,
            label = color.content,
        ),
        ColorSwatch(
            path = name,
            name = "backgroundHigh",
            fill = color.backgroundHigh,
            label = color.contentInverse,
        ),
        ColorSwatch(
            path = name,
            name = "onBackgroundHigh",
            fill = color.onBackgroundHigh,
            label = color.content,
        ),
    )

private fun subtleSwatches(
    name: String,
    color: ThemedPrimaryColor,
): List<ColorSwatch> =
    listOf(
        ColorSwatch(
            path = "$name.subtle",
            name = "background",
            fill = color.subtle.background,
            label = color.subtle.onBackground,
        ),
        ColorSwatch(
            path = "$name.subtle",
            name = "border",
            fill = color.subtle.border,
            label = color.subtle.onBackground,
        ),
        ColorSwatch(
            path = "$name.subtle",
            name = "onBackground",
            fill = color.subtle.onBackground,
            label = color.contentInverse,
        ),
    )
