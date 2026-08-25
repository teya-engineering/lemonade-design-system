package com.teya.lemonade.docs.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text
import com.teya.lemonade.core.LemonadeFontSizes
import com.teya.lemonade.core.LemonadeFontWeights
import com.teya.lemonade.core.LemonadeLineHeights
import com.teya.lemonade.core.LemonadeTypography

@Composable
internal fun TypeSpecimens(
    group: TypeGroup,
    modifier: Modifier = Modifier,
) {
    val styles = LemonadeTypography.entries.filter { style ->
        styleGroup(style) == group
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
    ) {
        styles.forEach { style ->
            SpecimenCard(style = style)
        }
    }
}

@Composable
private fun SpecimenCard(style: LemonadeTypography) {
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
        LemonadeUi.Text(
            text = "The quick brown fox",
            textStyle = style.style,
            color = LemonadeTheme.colors.content.contentPrimary,
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        )
        LemonadeUi.Text(
            text = style.name,
            textStyle = LemonadeTheme.typography.bodySmallSemiBold,
            color = LemonadeTheme.colors.content.contentPrimary,
        )
        LemonadeUi.Text(
            text = specimenDetail(style = style),
            textStyle = LemonadeTheme.typography.bodyXSmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}

private fun specimenDetail(style: LemonadeTypography): String {
    val letterSpacing = style.style.letterSpacing
    val spacing = if (letterSpacing == null || letterSpacing == 0f) {
        ""
    } else {
        " · letter spacing $letterSpacing"
    }
    return "size ${style.style.fontSize.toInt()} · line height ${style.style.lineHeight.toInt()} · " +
        "weight ${style.style.fontWeight}$spacing"
}

/** Overline is filed under Body in the enum, but reads as its own role on the page. */
private fun styleGroup(style: LemonadeTypography): TypeGroup {
    val name = style.name
    return when {
        name.endsWith("Overline") -> TypeGroup.Overline
        name.startsWith("Display") -> TypeGroup.Display
        name.startsWith("Heading") -> TypeGroup.Heading
        else -> TypeGroup.Body
    }
}

/**
 * The scales the composite styles are built from.
 *
 * Deliberately not specimens: a reader picks a semantic style, so the useful thing here is the
 * name beside the value.
 */
@Composable
internal fun TypeRamp(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
    ) {
        RampTable(
            title = "Font sizes",
            rows = LemonadeFontSizes.entries.map { size ->
                size.name to size.value.toInt().toString()
            },
        )
        RampTable(
            title = "Line heights",
            rows = LemonadeLineHeights.entries.map { height ->
                height.name to height.value.toInt().toString()
            },
        )
        RampTable(
            title = "Font weights",
            rows = LemonadeFontWeights.entries.map { weight ->
                weight.name to weight.weight.toString()
            },
        )
    }
}

@Composable
private fun RampTable(
    title: String,
    rows: List<Pair<String, String>>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentPrimary,
        )
        rows.forEach { (name, value) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            ) {
                LemonadeUi.Text(
                    text = name,
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                    modifier = Modifier.weight(1f),
                )
                LemonadeUi.Text(
                    text = value,
                    textStyle = LemonadeTheme.typography.bodySmallSemiBold,
                    color = LemonadeTheme.colors.content.contentPrimary,
                )
            }
        }
    }
}
