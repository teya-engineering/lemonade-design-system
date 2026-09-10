package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeSpaces

private data class SpacingItem(
    val name: String,
    val value: Dp,
)

private val spacingItems = listOf(
    SpacingItem(
        name = "spacing0",
        value = LemonadeSpaces.Spacing0.dp,
    ),
    SpacingItem(
        name = "spacing50",
        value = LemonadeSpaces.Spacing50.dp,
    ),
    SpacingItem(
        name = "spacing100",
        value = LemonadeSpaces.Spacing100.dp,
    ),
    SpacingItem(
        name = "spacing200",
        value = LemonadeSpaces.Spacing200.dp,
    ),
    SpacingItem(
        name = "spacing300",
        value = LemonadeSpaces.Spacing300.dp,
    ),
    SpacingItem(
        name = "spacing400",
        value = LemonadeSpaces.Spacing400.dp,
    ),
    SpacingItem(
        name = "spacing500",
        value = LemonadeSpaces.Spacing500.dp,
    ),
    SpacingItem(
        name = "spacing600",
        value = LemonadeSpaces.Spacing600.dp,
    ),
    SpacingItem(
        name = "spacing800",
        value = LemonadeSpaces.Spacing800.dp,
    ),
    SpacingItem(
        name = "spacing1000",
        value = LemonadeSpaces.Spacing1000.dp,
    ),
    SpacingItem(
        name = "spacing1200",
        value = LemonadeSpaces.Spacing1200.dp,
    ),
    SpacingItem(
        name = "spacing1400",
        value = LemonadeSpaces.Spacing1400.dp,
    ),
    SpacingItem(
        name = "spacing1600",
        value = LemonadeSpaces.Spacing1600.dp,
    ),
    SpacingItem(
        name = "spacing1800",
        value = LemonadeSpaces.Spacing1800.dp,
    ),
    SpacingItem(
        name = "spacing2000",
        value = LemonadeSpaces.Spacing2000.dp,
    ),
)

@Composable
internal fun SpacingDisplay() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(LemonadeTheme.spaces.spacing300),
    ) {
        item {
            LemonadeUi.Text(
                text = "Spacing Tokens",
                textStyle = LemonadeTheme.typography.headingMedium,
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing200),
            )
        }

        items(items = spacingItems) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            ) {
                LemonadeUi.Text(
                    text = item.name,
                    textStyle = LemonadeTheme.typography.bodySmallMedium,
                    modifier = Modifier.width(100.dp),
                )

                LemonadeUi.Text(
                    text = "${item.value.value.toInt()}dp",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                    modifier = Modifier.width(50.dp),
                )

                Box(
                    modifier = Modifier
                        .width(item.value)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LemonadePrimitiveColors.Solid.Green.green500),
                )
            }
        }
    }
}
