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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.teya.lemonade.core.LemonadeSizes

private data class SizeItem(
    val name: String,
    val value: Dp,
)

private val sizeItems = listOf(
    SizeItem(
        name = "size100",
        value = LemonadeSizes.Size100.dp,
    ),
    SizeItem(
        name = "size200",
        value = LemonadeSizes.Size200.dp,
    ),
    SizeItem(
        name = "size300",
        value = LemonadeSizes.Size300.dp,
    ),
    SizeItem(
        name = "size400",
        value = LemonadeSizes.Size400.dp,
    ),
    SizeItem(
        name = "size500",
        value = LemonadeSizes.Size500.dp,
    ),
    SizeItem(
        name = "size600",
        value = LemonadeSizes.Size600.dp,
    ),
    SizeItem(
        name = "size700",
        value = LemonadeSizes.Size700.dp,
    ),
    SizeItem(
        name = "size800",
        value = LemonadeSizes.Size800.dp,
    ),
    SizeItem(
        name = "size900",
        value = LemonadeSizes.Size900.dp,
    ),
    SizeItem(
        name = "size1000",
        value = LemonadeSizes.Size1000.dp,
    ),
    SizeItem(
        name = "size1100",
        value = LemonadeSizes.Size1100.dp,
    ),
    SizeItem(
        name = "size1200",
        value = LemonadeSizes.Size1200.dp,
    ),
)

@Composable
internal fun SizesDisplay() {
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
                text = "Size Tokens",
                textStyle = LemonadeTheme.typography.headingMedium,
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing200),
            )
        }

        items(items = sizeItems) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            ) {
                LemonadeUi.Text(
                    text = item.name,
                    textStyle = LemonadeTheme.typography.bodySmallMedium,
                    modifier = Modifier.width(80.dp),
                )

                LemonadeUi.Text(
                    text = "${item.value.value.toInt()}dp",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                    modifier = Modifier.width(50.dp),
                )

                val displaySize = min(
                    a = item.value,
                    b = 100.dp,
                )
                Box(
                    modifier = Modifier
                        .size(displaySize)
                        .clip(CircleShape)
                        .background(LemonadePrimitiveColors.Solid.Purple.purple500),
                )
            }
        }
    }
}
