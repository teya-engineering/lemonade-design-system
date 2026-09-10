package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.teya.lemonade.core.LemonadeRadius

private data class RadiusItem(
    val name: String,
    val value: Dp,
)

private val radiusItems = listOf(
    RadiusItem(
        name = "radius0",
        value = LemonadeRadius.Radius0.dp,
    ),
    RadiusItem(
        name = "radius50",
        value = LemonadeRadius.Radius50.dp,
    ),
    RadiusItem(
        name = "radius100",
        value = LemonadeRadius.Radius100.dp,
    ),
    RadiusItem(
        name = "radius150",
        value = LemonadeRadius.Radius150.dp,
    ),
    RadiusItem(
        name = "radius200",
        value = LemonadeRadius.Radius200.dp,
    ),
    RadiusItem(
        name = "radius300",
        value = LemonadeRadius.Radius300.dp,
    ),
    RadiusItem(
        name = "radius400",
        value = LemonadeRadius.Radius400.dp,
    ),
    RadiusItem(
        name = "radius500",
        value = LemonadeRadius.Radius500.dp,
    ),
    RadiusItem(
        name = "radius600",
        value = LemonadeRadius.Radius600.dp,
    ),
    RadiusItem(
        name = "radius800",
        value = LemonadeRadius.Radius800.dp,
    ),
    RadiusItem(
        name = "radiusFull",
        value = LemonadeRadius.RadiusFull.dp,
    ),
)

@Composable
internal fun RadiusDisplay() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(LemonadeTheme.spaces.spacing300),
    ) {
        item {
            LemonadeUi.Text(
                text = "Radius Tokens",
                textStyle = LemonadeTheme.typography.headingMedium,
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing200),
            )
        }

        items(items = radiusItems) { item ->
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
                        .size(80.dp)
                        .clip(RoundedCornerShape(item.value))
                        .background(LemonadePrimitiveColors.Solid.Blue.blue500),
                )
            }
        }
    }
}
