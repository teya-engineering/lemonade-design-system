package com.teya.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.teya.lemonade.app.screens

public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            LemonadeStyledTheme {
                LemonadeToastHost {
                    LemonadeTooltipHost {
                        val backStack = remember {
                            mutableStateListOf<Displays>(Displays.Home)
                        }
                        NavDisplay(
                            modifier = Modifier.background(color = LemonadeTheme.colors.background.bgSubtle),
                            backStack = backStack,
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            entryProvider = { displayKey ->
                                NavEntry(key = displayKey) {
                                    val screen = screens[displayKey]
                                        ?: { _ -> BasicText("Invalid key") }
                                    screen.invoke { backStack.add(it) }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
