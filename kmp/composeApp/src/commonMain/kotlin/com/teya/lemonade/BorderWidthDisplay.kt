package com.teya.lemonade

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private data class BorderWidthItem(
    val name: String,
    val value: Dp,
)

@Composable
private fun rememberBorderWidthItems(): List<BorderWidthItem> {
    val borderWidths = LemonadeTheme.borderWidths.base
    return remember(borderWidths) {
        listOf(
            BorderWidthItem(
                name = "border0",
                value = borderWidths.border0,
            ),
            BorderWidthItem(
                name = "border25",
                value = borderWidths.border25,
            ),
            BorderWidthItem(
                name = "border40",
                value = borderWidths.border40,
            ),
            BorderWidthItem(
                name = "border50",
                value = borderWidths.border50,
            ),
            BorderWidthItem(
                name = "border75",
                value = borderWidths.border75,
            ),
            BorderWidthItem(
                name = "border100",
                value = borderWidths.border100,
            ),
        )
    }
}

@Composable
internal fun BorderWidthDisplay() {
    val borderWidthItems = rememberBorderWidthItems()

    SampleScreenDisplayLazyColumn(title = "Border Width") {
        items(
            items = borderWidthItems,
            key = { border -> border.name },
        ) { border ->
            BorderWidthRow(border = border)
        }
    }
}

@Composable
private fun BorderWidthRow(border: BorderWidthItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
    ) {
        LemonadeUi.Text(
            text = border.name,
            textStyle = LemonadeTheme.typography.bodySmallMedium,
            modifier = Modifier.width(120.dp),
        )

        LemonadeUi.Text(
            text = "${border.value.value}dp",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
            modifier = Modifier.width(50.dp),
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(60.dp)
                .then(
                    if (border.value > 0.dp) {
                        Modifier.border(
                            width = border.value,
                            color = LemonadeTheme.colors.background.bgInfo,
                            shape = RoundedCornerShape(8.dp),
                        )
                    } else {
                        Modifier
                    },
                ),
        )
    }
}
