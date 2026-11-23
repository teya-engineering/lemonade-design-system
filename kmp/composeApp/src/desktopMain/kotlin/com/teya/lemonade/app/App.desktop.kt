package com.teya.lemonade.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.Displays
import com.teya.lemonade.LemonadeTheme

@Composable
internal actual fun App() {
    // The desktop layout is a fixed master/detail pair: the list is always Home and the detail pane
    // holds at most one screen, so a nullable slot models it exactly.
    var detailScreen: Displays? by remember { mutableStateOf(null) }

    // Remembered so the lambda keeps its identity — otherwise every recomposition hands HomeDisplay
    // a new onNavigate and it can never skip.
    val onNavigate = remember {
        { screen: Displays ->
            detailScreen = screen
        }
    }

    Row(modifier = Modifier.background(color = LemonadeTheme.colors.background.bgSubtle)) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 1f),
        ) {
            screens[Displays.Home]?.invoke(onNavigate)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 4f),
        ) {
            detailScreen?.let { focusScreen ->
                screens[focusScreen]?.invoke(onNavigate)
            }
        }
    }
}

internal actual val platformScreens: Map<Displays, @Composable ((onNavigate: (Displays) -> Unit) -> Unit)> =
    emptyMap()
