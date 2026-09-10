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
import androidx.compose.ui.unit.dp

private data class OpacityItem(
    val name: String,
    val value: Float,
)

private val baseOpacityItems = listOf(
    OpacityItem(
        name = "opacity0",
        value = 0.0f,
    ),
    OpacityItem(
        name = "opacity5",
        value = 0.05f,
    ),
    OpacityItem(
        name = "opacity10",
        value = 0.1f,
    ),
    OpacityItem(
        name = "opacity20",
        value = 0.2f,
    ),
    OpacityItem(
        name = "opacity30",
        value = 0.3f,
    ),
    OpacityItem(
        name = "opacity40",
        value = 0.4f,
    ),
    OpacityItem(
        name = "opacity50",
        value = 0.5f,
    ),
    OpacityItem(
        name = "opacity60",
        value = 0.6f,
    ),
    OpacityItem(
        name = "opacity70",
        value = 0.7f,
    ),
    OpacityItem(
        name = "opacity80",
        value = 0.8f,
    ),
    OpacityItem(
        name = "opacity90",
        value = 0.9f,
    ),
    OpacityItem(
        name = "opacity100",
        value = 1.0f,
    ),
)

private val stateOpacityItems = listOf(
    OpacityItem(
        name = "opacityPressed",
        value = 0.2f,
    ),
    OpacityItem(
        name = "opacityDisabled",
        value = 0.4f,
    ),
)

@Composable
internal fun OpacityDisplay() {
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
                text = "Opacity Tokens",
                textStyle = LemonadeTheme.typography.headingMedium,
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing200),
            )
        }

        item {
            LemonadeUi.Text(
                text = "Base Opacities",
                textStyle = LemonadeTheme.typography.headingXSmall,
                modifier = Modifier.padding(vertical = LemonadeTheme.spaces.spacing200),
            )
        }

        items(items = baseOpacityItems) { item ->
            OpacityRow(item)
        }

        item {
            LemonadeUi.Text(
                text = "State Opacities",
                textStyle = LemonadeTheme.typography.headingXSmall,
                modifier = Modifier.padding(vertical = LemonadeTheme.spaces.spacing200),
            )
        }

        items(items = stateOpacityItems) { item ->
            OpacityRow(item)
        }
    }
}

@Composable
private fun OpacityRow(item: OpacityItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
    ) {
        LemonadeUi.Text(
            text = item.name,
            textStyle = LemonadeTheme.typography.bodySmallMedium,
            modifier = Modifier.width(120.dp),
        )

        LemonadeUi.Text(
            text = "${(item.value * 100).toInt()}%",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
            modifier = Modifier.width(50.dp),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    LemonadePrimitiveColors.Solid.Green.green500
                        .copy(alpha = item.value),
                ),
        )
    }
}
