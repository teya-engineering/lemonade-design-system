package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TopBarAction
import kotlinx.coroutines.launch

private val sampleItems = listOf(
    "Argentina",
    "Australia",
    "Brazil",
    "Canada",
    "China",
    "Denmark",
    "Egypt",
    "France",
    "Germany",
    "India",
    "Italy",
    "Japan",
    "Mexico",
    "Netherlands",
    "Norway",
    "Portugal",
    "South Korea",
    "Spain",
    "Sweden",
    "United Kingdom",
)

@Composable
internal fun SearchTopBarSampleDisplay() {
    var searchInput by remember { mutableStateOf("") }
    val topBarState = rememberTopBarState()
    val coroutineScope = rememberCoroutineScope()
    val filteredItems = remember(searchInput) {
        if (searchInput.isBlank()) {
            sampleItems
        } else {
            sampleItems.filter { item ->
                item.contains(
                    other = searchInput,
                    ignoreCase = true,
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LemonadeTheme.colors.background.bgSubtle)
            .navigationBarsPadding(),
    ) {
        LemonadeUi.TopBar(
            label = "Top Bar",
            state = topBarState,
            searchInput = searchInput,
            searchPlaceholder = "Search",
            onSearchChanged = { input -> searchInput = input },
            navigationAction = NavigationAction(
                navigationAction = TopBarAction.Close,
                onNavigationActionClicked = { },
            ),
            trailingSlot = {
                LemonadeUi.IconButton(
                    icon = LemonadeIcons.ChevronDown,
                    type = LemonadeButtonType.Ghost,
                    contentDescription = "Expand",
                    size = LemonadeButtonSize.Medium,
                    onClick = {
                        coroutineScope.launch {
                            topBarState.expand()
                        }
                    },
                )
                LemonadeUi.IconButton(
                    icon = LemonadeIcons.ChevronTop,
                    type = LemonadeButtonType.Ghost,
                    contentDescription = "Collapse",
                    size = LemonadeButtonSize.Medium,
                    onClick = {
                        coroutineScope.launch {
                            topBarState.collapse()
                        }
                    },
                )
            },
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(topBarState.nestedScrollConnection)
                .background(LemonadeTheme.colors.background.bgSubtle),
        ) {
            if (filteredItems.isEmpty()) {
                item {
                    LemonadeUi.Text(
                        text = "No results found",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        modifier = Modifier.padding(LemonadeTheme.spaces.spacing300),
                    )
                }
            } else {
                items(
                    items = filteredItems,
                    key = { item -> item },
                ) { item ->
                    LemonadeUi.Text(
                        text = item,
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
}
