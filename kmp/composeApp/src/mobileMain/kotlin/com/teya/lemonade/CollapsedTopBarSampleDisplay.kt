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
import com.teya.lemonade.core.TopBarAction

@Composable
internal fun CollapsedTopBarSampleDisplay() {
    val topBarState = rememberTopBarState(
        startCollapsed = true,
        lockGestureAnimation = true,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LemonadeTheme.colors.background.bgSubtle)
            .navigationBarsPadding(),
    ) {
        LemonadeUi.TopBar(
            label = "Collapsed Top Bar",
            state = topBarState,
            navigationAction = NavigationAction(
                navigationAction = TopBarAction.Back,
                onNavigationActionClicked = { },
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(LemonadeTheme.colors.background.bgSubtle),
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
