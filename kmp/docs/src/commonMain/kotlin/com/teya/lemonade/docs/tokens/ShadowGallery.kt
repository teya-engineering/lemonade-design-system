package com.teya.lemonade.docs.tokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.LemonadeUi
import com.teya.lemonade.Text
import com.teya.lemonade.core.LemonadeShadow
import com.teya.lemonade.lemonadeShadow

private val SampleHeight = 80.dp

/**
 * Elevation, rendered by the same code a product screen would use.
 *
 * A CSS approximation of these tokens would need a caution saying a Compose screen will not match;
 * here the preview is the Compose renderer, so if the platform draws shadows differently from what
 * the tokens describe, this page shows it rather than disclaiming it.
 */
@Composable
internal fun ShadowGallery(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(LemonadeTheme.shapes.radius300)
            .background(LemonadeTheme.colors.background.bgAlwaysLight)
            .padding(LemonadeTheme.spaces.spacing600),
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeShadow.entries.forEach { shadow ->
            Column(
                verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SampleHeight)
                        .lemonadeShadow(
                            shadow = shadow,
                            shape = LemonadeTheme.shapes.semantic.radiusContainerDefault,
                        ).clip(LemonadeTheme.shapes.semantic.radiusContainerDefault)
                        .background(LemonadeTheme.colors.background.bgAlwaysLight),
                )
                LemonadeUi.Text(
                    text = shadow.name,
                    textStyle = LemonadeTheme.typography.bodySmallSemiBold,
                    color = LemonadeTheme.colors.content.contentAlwaysDark,
                )
            }
        }
    }
}
