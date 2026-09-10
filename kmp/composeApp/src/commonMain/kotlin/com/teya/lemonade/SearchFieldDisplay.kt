package com.teya.lemonade

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

private val productList: List<String> = listOf("iPhone 15", "MacBook Pro", "iPad Air", "Apple Watch")

@Suppress("LongMethod")
@Composable
internal fun SearchFieldDisplay() {
    val focusManager = LocalFocusManager.current
    val toasts = LocalLemonadeToastState.current

    var searchText1 by remember { mutableStateOf("") }
    var searchText2 by remember { mutableStateOf("Sample search") }
    var searchText3 by remember { mutableStateOf("") }
    var searchText4 by remember { mutableStateOf("") }
    var searchText5 by remember { mutableStateOf("") }
    var searchText6 by remember { mutableStateOf("") }

    SampleScreenDisplayLazyColumn(
        title = "SearchField",
        modifier = Modifier.pointerInput(focusManager) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                },
            )
        },
    ) {
        item(key = "basic") {
            SearchFieldSection(title = "Basic") {
                @OptIn(ExperimentalLemonadeComponent::class)
                LemonadeUi.SearchField(
                    input = searchText1,
                    onInputChanged = { value -> searchText1 = value },
                    placeholder = "Search...",
                )
            }
        }

        item(key = "with-content") {
            SearchFieldSection(title = "With Content") {
                @OptIn(ExperimentalLemonadeComponent::class)
                LemonadeUi.SearchField(
                    input = searchText2,
                    onInputChanged = { value -> searchText2 = value },
                    placeholder = "Search...",
                )
            }
        }

        item(key = "with-callbacks") {
            SearchFieldSection(title = "With Callbacks") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                ) {
                    @OptIn(ExperimentalLemonadeComponent::class)
                    LemonadeUi.SearchField(
                        input = searchText3,
                        onInputChanged = { newValue ->
                            searchText3 = newValue
                        },
                        placeholder = "Type to search...",
                        onInputClear = {
                            toasts.show(label = "Search cleared")
                        },
                    )

                    if (searchText3.isNotEmpty()) {
                        LemonadeUi.Text(
                            text = "Searching for: $searchText3",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                    }
                }
            }
        }

        item(key = "cancel-callback") {
            SearchFieldSection(title = "Cancel Callback") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                ) {
                    @OptIn(ExperimentalLemonadeComponent::class)
                    LemonadeUi.SearchField(
                        input = searchText4,
                        onInputChanged = { value -> searchText4 = value },
                        placeholder = "Search and cancel...",
                        onCancel = { toasts.show(label = "Search dismissed") },
                        cancelContentDescription = "Cancel search",
                    )

                    LemonadeUi.Text(
                        text = "Cancelling drops the focus and hides the keyboard; the input stays " +
                            "as typed. onCancel then runs for whatever the query was driving.",
                        textStyle = LemonadeTheme.typography.bodySmallRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                    )
                }
            }
        }

        item(key = "not-dismissible") {
            SearchFieldSection(title = "Not Dismissible") {
                @OptIn(ExperimentalLemonadeComponent::class)
                LemonadeUi.SearchField(
                    input = searchText5,
                    onInputChanged = { value -> searchText5 = value },
                    placeholder = "No cancel button...",
                    dismissible = false,
                )
            }
        }

        item(key = "disabled") {
            SearchFieldSection(title = "Disabled") {
                @OptIn(ExperimentalLemonadeComponent::class)
                LemonadeUi.SearchField(
                    input = "",
                    onInputChanged = {},
                    placeholder = "Search disabled...",
                    enabled = false,
                )
            }
        }

        item(key = "usage-example") {
            SearchFieldSection(title = "Usage Example") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    @OptIn(ExperimentalLemonadeComponent::class)
                    LemonadeUi.SearchField(
                        input = searchText6,
                        onInputChanged = { value -> searchText6 = value },
                        placeholder = "Search products...",
                    )

                    val displayList = remember(searchText6) {
                        if (searchText6.isEmpty()) {
                            productList
                        } else {
                            productList.filter { product ->
                                product.contains(
                                    other = searchText6,
                                    ignoreCase = true,
                                )
                            }
                        }
                    }

                    SearchResults(results = displayList)
                }
            }
        }
    }
}

@Composable
private fun SearchResults(results: List<String>) {
    if (results.isEmpty()) {
        LemonadeUi.Text(
            text = "No results found",
            textStyle = LemonadeTheme.typography.bodyMediumRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
            modifier = Modifier.padding(LemonadeTheme.spaces.spacing400),
        )
        return
    }

    Column {
        results.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LemonadeTheme.spaces.spacing200),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LemonadeUi.Text(
                    text = item,
                    textStyle = LemonadeTheme.typography.bodyMediumRegular,
                )
                LemonadeUi.Icon(
                    icon = LemonadeIcons.ChevronRight,
                    contentDescription = null,
                    size = LemonadeAssetSize.Small,
                    tint = LemonadeTheme.colors.content.contentTertiary,
                )
            }
        }
    }
}

@Composable
private fun SearchFieldSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}
