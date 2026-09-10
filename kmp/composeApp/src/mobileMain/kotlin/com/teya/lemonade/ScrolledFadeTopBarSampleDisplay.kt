package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.teya.lemonade.core.TopBarAction

@Composable
internal fun ScrolledFadeTopBarSampleDisplay() {
    val topBarState = rememberTopBarState(
        startCollapsed = true,
        lockGestureAnimation = true,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LemonadeTheme.colors.background.bgPositiveSubtle,
                        LemonadeTheme.colors.background.bgDefault,
                    ),
                ),
            ).navigationBarsPadding(),
    ) {
        LemonadeUi.TopBar(
            label = "Scrolled Fade",
            state = topBarState,
            backgroundColor = Color.Transparent,
            scrolledBackgroundColor = LemonadeTheme.colors.background.bgDefault,
            navigationAction = NavigationAction(
                navigationAction = TopBarAction.Close,
                onNavigationActionClicked = { },
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(topBarState.nestedScrollConnection),
        ) {
            items(count = 30) { index ->
                LemonadeUi.Text(
                    text = "Item ${index + 1}",
                    textStyle = LemonadeTheme.typography.bodyMediumRegular,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = LemonadeTheme.spaces.spacing300,
                            vertical = LemonadeTheme.spaces.spacing200,
                        ),
                )
            }
        }
    }
}
