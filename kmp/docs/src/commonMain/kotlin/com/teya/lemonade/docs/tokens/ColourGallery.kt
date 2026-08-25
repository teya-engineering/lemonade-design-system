package com.teya.lemonade.docs.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teya.lemonade.LemonadeDarkTheme
import com.teya.lemonade.LemonadeLightTheme
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Tag
import com.teya.lemonade.Text
import com.teya.lemonade.core.TagVoice

private val SwatchHeight = 72.dp
private val CheckerSquare = 8.dp

/**
 * Every published colour in one group, as a light/dark pair.
 *
 * The swatches sit on a checkerboard so a translucent token reads as translucent rather than as a
 * slightly different flat colour, and both columns are drawn at once regardless of the active
 * theme — the page is a reference, not a preview.
 */
@Composable
internal fun ColourGallery(
    group: ColorTokenGroup,
    modifier: Modifier = Modifier,
) {
    val tokens = LemonadeColorTokenDocs.filter { doc ->
        doc.group == group
    }
    val subgroups = tokens
        .groupBy { doc -> doc.subgroup }
        .toList()
        .sortedWith(compareBy(nullsFirst()) { entry -> entry.first })

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
    ) {
        subgroups.forEach { (subgroup, docs) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            ) {
                if (subgroup != null) {
                    LemonadeUi.Text(
                        text = subgroup,
                        textStyle = LemonadeTheme.typography.headingXSmall,
                        color = LemonadeTheme.colors.content.contentPrimary,
                    )
                }
                docs.forEach { doc ->
                    ColourCard(doc = doc)
                }
            }
        }
    }
}

@Composable
private fun ColourCard(doc: ColorTokenDoc) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LemonadeTheme.shapes.radius300)
            .background(LemonadeTheme.colors.background.bgDefault)
            .border(
                width = LemonadeTheme.borderWidths.base.border25,
                color = LemonadeTheme.colors.border.borderNeutralLow,
                shape = LemonadeTheme.shapes.radius300,
            ).padding(LemonadeTheme.spaces.spacing300),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SwatchHeight)
                .clip(LemonadeTheme.shapes.radius200),
        ) {
            Swatch(
                color = doc.light,
                label = "Light",
                labelColor = LemonadeLightTheme.content.contentSecondary,
                modifier = Modifier.weight(1f),
            )
            Swatch(
                color = doc.dark,
                label = "Dark",
                labelColor = LemonadeDarkTheme.content.contentSecondary,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        ) {
            LemonadeUi.Text(
                text = doc.name,
                textStyle = LemonadeTheme.typography.bodyMediumSemiBold,
                color = LemonadeTheme.colors.content.contentPrimary,
            )
            if (doc.fixed) {
                LemonadeUi.Tag(label = "Fixed", voice = TagVoice.Neutral)
            }
        }

        if (doc.description != null) {
            LemonadeUi.Text(
                text = doc.description,
                textStyle = LemonadeTheme.typography.bodySmallRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        ) {
            HexLabel(label = "Light", color = doc.light)
            HexLabel(label = "Dark", color = doc.dark)
        }

        if (doc.androidSymbol != null) {
            SymbolLabel(platform = "KMP", symbol = doc.androidSymbol)
        }
        if (doc.iosSymbol != null) {
            SymbolLabel(platform = "SwiftUI", symbol = doc.iosSymbol)
        }
    }
}

/**
 * [labelColor] comes from the theme the swatch is showing, not from the active one. A "Light" label
 * tinted for dark mode disappears against a white token.
 */
@Composable
private fun Swatch(
    color: Color,
    label: String,
    labelColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .checkerboard(
                light = LemonadeLightTheme.background.bgSubtle,
                dark = LemonadeLightTheme.background.bgElevatedHigh,
            ).background(color),
    ) {
        LemonadeUi.Text(
            text = label,
            textStyle = LemonadeTheme.typography.bodyXSmallOverline,
            color = labelColor,
            modifier = Modifier.padding(LemonadeTheme.spaces.spacing100),
        )
    }
}

private fun Modifier.checkerboard(
    light: Color,
    dark: Color,
): Modifier =
    drawBehind {
        val square = CheckerSquare.toPx()
        drawRect(color = light)
        var y = 0f
        var row = 0
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else square
            while (x < size.width) {
                drawRect(
                    color = dark,
                    topLeft = Offset(x, y),
                    size = Size(
                        width = minOf(square, size.width - x),
                        height = minOf(square, size.height - y),
                    ),
                )
                x += square * 2
            }
            y += square
            row += 1
        }
    }

@Composable
private fun HexLabel(
    label: String,
    color: Color,
) {
    LemonadeUi.Text(
        text = "$label ${color.toHexString()}",
        textStyle = LemonadeTheme.typography.bodyXSmallRegular,
        color = LemonadeTheme.colors.content.contentSecondary,
    )
}

@Composable
private fun SymbolLabel(
    platform: String,
    symbol: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = platform,
            textStyle = LemonadeTheme.typography.bodyXSmallOverline,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        LemonadeUi.Text(
            text = symbol,
            textStyle = LemonadeTheme.typography.bodyXSmallRegular,
            color = LemonadeTheme.colors.content.contentPrimary,
        )
    }
}

/** `#rrggbb`, with an alpha byte only when the token is actually translucent. */
internal fun Color.toHexString(): String {
    fun channel(value: Float): String =
        (value * 255)
            .toInt()
            .toString(16)
            .padStart(2, '0')
            .uppercase()
    val base = "#${channel(red)}${channel(green)}${channel(blue)}"
    return if (alpha < 0.999f) {
        "$base${channel(alpha)}"
    } else {
        base
    }
}
