package com.teya.lemonade.docs.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.teya.lemonade.HorizontalDivider
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text

private val NameColumn = 180.dp
private val ValueColumn = 80.dp
private val PreviewColumn = 140.dp
private val SymbolColumn = 300.dp
private val PreviewBox = 40.dp
private const val MAX_PREVIEW_RADIUS = 32f

@Composable
internal fun ScaleGallery(
    tokens: List<ScaleTokenDoc>,
    preview: ScalePreview,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(LemonadeTheme.shapes.radius300)
            .background(LemonadeTheme.colors.background.bgDefault)
            .border(
                width = LemonadeTheme.borderWidths.base.border25,
                color = LemonadeTheme.colors.border.borderNeutralLow,
                shape = LemonadeTheme.shapes.radius300,
            ).horizontalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .background(LemonadeTheme.colors.background.bgSubtle)
                .padding(
                    horizontal = LemonadeTheme.spaces.spacing300,
                    vertical = LemonadeTheme.spaces.spacing200,
                ),
        ) {
            HeaderCell(text = "Token", width = NameColumn)
            HeaderCell(text = "Value", width = ValueColumn)
            if (preview != ScalePreview.None) {
                HeaderCell(text = "Preview", width = PreviewColumn)
            }
            HeaderCell(text = "KMP", width = SymbolColumn)
            HeaderCell(text = "SwiftUI", width = SymbolColumn)
        }
        tokens.forEachIndexed { index, token ->
            if (index > 0) {
                LemonadeUi.HorizontalDivider()
            }
            Row(
                modifier = Modifier.padding(
                    horizontal = LemonadeTheme.spaces.spacing300,
                    vertical = LemonadeTheme.spaces.spacing300,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BodyCell(text = token.name, width = NameColumn, emphasised = true)
                BodyCell(text = formatValue(token.value), width = ValueColumn)
                if (preview != ScalePreview.None) {
                    Box(modifier = Modifier.width(PreviewColumn)) {
                        ScalePreviewCell(value = token.value, preview = preview)
                    }
                }
                BodyCell(text = token.androidSymbol ?: "—", width = SymbolColumn)
                BodyCell(text = token.iosSymbol ?: "—", width = SymbolColumn)
            }
        }
    }
}

@Composable
private fun HeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
) {
    LemonadeUi.Text(
        text = text,
        textStyle = LemonadeTheme.typography.bodySmallSemiBold,
        color = LemonadeTheme.colors.content.contentPrimary,
        modifier = Modifier.width(width),
    )
}

@Composable
private fun BodyCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    emphasised: Boolean = false,
) {
    LemonadeUi.Text(
        text = text,
        textStyle = if (emphasised) {
            LemonadeTheme.typography.bodySmallSemiBold
        } else {
            LemonadeTheme.typography.bodySmallRegular
        },
        color = if (emphasised) {
            LemonadeTheme.colors.content.contentPrimary
        } else {
            LemonadeTheme.colors.content.contentSecondary
        },
        modifier = Modifier.width(width),
    )
}

@Composable
private fun ScalePreviewCell(
    value: Float,
    preview: ScalePreview,
) {
    when (preview) {
        ScalePreview.Length -> Box(
            modifier = Modifier
                .width(value.dp)
                .height(LemonadeTheme.spaces.spacing300)
                .clip(LemonadeTheme.shapes.radius50)
                .background(LemonadeTheme.colors.background.bgBrand),
        )

        ScalePreview.Radius -> Box(
            modifier = Modifier
                .size(PreviewBox)
                .clip(RoundedCornerShape(minOf(value, MAX_PREVIEW_RADIUS).dp))
                .border(
                    width = LemonadeTheme.borderWidths.base.border50,
                    color = LemonadeTheme.colors.border.borderNeutralHigh,
                    shape = RoundedCornerShape(minOf(value, MAX_PREVIEW_RADIUS).dp),
                ),
        )

        ScalePreview.Percent -> Box(
            modifier = Modifier
                .size(PreviewBox)
                .clip(LemonadeTheme.shapes.radius100)
                .background(LemonadeTheme.colors.background.bgSubtle),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(PreviewBox)
                    .alpha(value / 100f)
                    .background(LemonadeTheme.colors.background.bgBrand),
            )
        }

        ScalePreview.None -> Unit
    }
}

private fun formatValue(value: Float): String =
    if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        value.toString()
    }
