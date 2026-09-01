package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice

private data class SampleAccount(val id: String, val name: String, val email: String, val initials: String)

private val sampleAccounts = listOf(
    SampleAccount("1", "Kathryn Murphy", "kathryn.murphy@mail.com", "KM"),
    SampleAccount("2", "Marvin McKinney", "marvin.mckinney@mail.com", "MM"),
    SampleAccount("3", "Jenny Wilson", "jenny.wilson@mail.com", "JW"),
)

@Composable
internal fun SwipeActionRowDisplay() {
    SampleScreenDisplayLazyColumn(title = "SwipeActionRow") {
        item(key = "single-open") {
            var openId by remember { mutableStateOf<Any?>(null) }
            var removed by remember { mutableStateOf(emptySet<String>()) }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "One open row at a time",
                    subtitle = "Drag a row left. Dragging across it fires the first action.",
                ),
            ) {
                val visible = sampleAccounts.filterNot { it.id in removed }
                visible.forEachIndexed { index, account ->
                    LemonadeUi.SwipeActionRow(
                        id = account.id,
                        openId = openId,
                        onOpenIdChange = { openId = it },
                        actions = listOf(
                            SwipeAction(
                                icon = LemonadeIcons.Trash,
                                contentDescription = "Remove ${account.name}",
                                onClick = { removed = removed + account.id },
                            ),
                        ),
                        showDivider = index != visible.lastIndex,
                    ) {
                        LemonadeUi.ActionListItem(
                            label = account.name,
                            supportText = account.email,
                            showNavigationIndicator = true,
                            // The container draws the divider: an item's own would travel with it.
                            showDivider = false,
                            onItemClicked = { },
                            leadingSlot = {
                                LemonadeUi.SymbolContainer(
                                    text = account.initials,
                                    voice = SymbolContainerVoice.Neutral,
                                    size = SymbolContainerSize.Medium,
                                    shape = SymbolContainerShape.Circle,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "two-actions-no-full-swipe") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Two actions, allowsFullSwipe = false",
                    subtitle = "Travel past the reveal rubber-bands, and nothing commits.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    actions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { },
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.PencilLine,
                            contentDescription = "Edit",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                    allowsFullSwipe = false,
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Two actions",
                        supportText = "Outermost action first",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }
    }
}
