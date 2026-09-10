package com.teya.lemonade

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
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice

@Composable
private fun RemovalConfirmation(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    LemonadeUi.Dialog(
        expanded = true,
        onDismissRequest = onCancel,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "This will be deleted and you will not be able to recover it.",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                    alignment = Alignment.End,
                ),
            ) {
                LemonadeUi.Button(
                    label = "Cancel",
                    onClick = onCancel,
                    variant = LemonadeButtonVariant.Neutral,
                    size = LemonadeButtonSize.Medium,
                )
                LemonadeUi.Button(
                    label = "Delete",
                    onClick = onDelete,
                    variant = LemonadeButtonVariant.Critical,
                    size = LemonadeButtonSize.Medium,
                )
            }
        }
    }
}

private data class SampleAccount(
    val id: String,
    val name: String,
    val email: String,
    val initials: String,
)

private val sampleAccounts = listOf(
    SampleAccount(
        id = "1",
        name = "Kathryn Murphy",
        email = "kathryn.murphy@mail.com",
        initials = "KM",
    ),
    SampleAccount(
        id = "2",
        name = "Marvin McKinney",
        email = "marvin.mckinney@mail.com",
        initials = "MM",
    ),
    SampleAccount(
        id = "3",
        name = "Jenny Wilson",
        email = "jenny.wilson@mail.com",
        initials = "JW",
    ),
)

@Composable
internal fun SwipeActionRowDisplay() {
    LemonadeUi.SwipeActionGroup {
        SwipeActionRowDisplayContent()
    }
}

@Composable
private fun SwipeActionRowDisplayContent() {
    SampleScreenDisplayLazyColumn(title = "SwipeActionRow") {
        item(key = "single-open") {
            var openId by remember { mutableStateOf<Any?>(null) }
            var removed by remember { mutableStateOf(emptySet<String>()) }
            // A destructive action fired by a gesture is the one most easily fired by accident,
            // so the row raises a confirmation instead of deleting.
            var pendingRemoval by remember { mutableStateOf<SampleAccount?>(null) }
            pendingRemoval?.let { account ->
                RemovalConfirmation(
                    onDelete = {
                        removed = removed + account.id
                        pendingRemoval = null
                    },
                    // The row held itself open for this, so closing it again is the caller's to
                    // do: the row cannot see the confirmation go.
                    onCancel = {
                        pendingRemoval = null
                        openId = null
                    },
                )
            }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "One open row at a time",
                    subtitle = "Drag a row left. Dragging across it fires the first action.",
                ),
            ) {
                val visible = sampleAccounts.filterNot { account -> account.id in removed }
                visible.forEachIndexed { index, account ->
                    LemonadeUi.SwipeActionRow(
                        id = account.id,
                        openId = openId,
                        onOpenIdChange = { id -> openId = id },
                        trailingActions = listOf(
                            SwipeAction(
                                icon = LemonadeIcons.Trash,
                                contentDescription = "Remove ${account.name}",
                                onClick = { pendingRemoval = account },
                                keepsRowOpen = true,
                            ),
                        ),
                        showDivider = index != visible.lastIndex,
                    ) {
                        LemonadeUi.ActionListItem(
                            label = account.name,
                            supportText = account.email,
                            showNavigationIndicator = true,
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

        item(key = "starts-open") {
            // This row is open before it has ever been measured, so an unmeasured position must
            // not be mistaken for the row having been scrolled away.
            var openId by remember { mutableStateOf<Any?>("unread") }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Opened by the caller",
                    subtitle = "This row starts open, and closes on a tap away like any other.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    id = "unread",
                    openId = openId,
                    onOpenIdChange = { id -> openId = id },
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Envelope,
                            contentDescription = "Mark unread",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                    allowsFullSwipe = false,
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Already open",
                        supportText = if (openId == null) "Closed" else "Open",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "two-actions-no-full-swipe") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Two actions, allowsFullSwipe = false",
                    subtitle = "Travel past the reveal is capped, and nothing commits.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
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

        item(key = "two-actions-full-swipe") {
            var fired by remember { mutableStateOf(0) }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Two actions, allowsFullSwipe = true",
                    subtitle = "Dragging across the row fires the first action, which takes over the second.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { fired += 1 },
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.PencilLine,
                            contentDescription = "Edit",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Two actions",
                        // Counted rather than removed, so the swipe can be tried again.
                        supportText = if (fired == 0) {
                            "Drag across to fire Delete"
                        } else {
                            "Delete fired ${fired}x"
                        },
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "leading-actions") {
            var read by remember { mutableStateOf(false) }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Leading actions",
                    subtitle = "Drag right. Nothing sits on the trailing edge, so dragging left does nothing.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    leadingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Check,
                            contentDescription = "Mark as read",
                            onClick = { read = !read },
                            variant = LemonadeButtonVariant.Primary,
                        ),
                    ),
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Leading only",
                        supportText = if (read) "Read" else "Unread",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "both-edges") {
            var read by remember { mutableStateOf(false) }
            var deleted by remember { mutableStateOf(0) }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Both edges",
                    subtitle = "Each edge has its own actions. One drag reveals one edge, and can fire it.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    leadingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Check,
                            contentDescription = "Mark as read",
                            onClick = { read = !read },
                            variant = LemonadeButtonVariant.Primary,
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.Star,
                            contentDescription = "Star",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { deleted += 1 },
                        ),
                    ),
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Both edges",
                        // Counted rather than removed, so either swipe can be tried again.
                        supportText = when {
                            deleted > 0 -> "Delete fired ${deleted}x"
                            read -> "Read"
                            else -> "Unread"
                        },
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "any-variant") {
            var pinned by remember { mutableStateOf(false) }
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Any icon, any variant",
                    subtitle = "An action is an icon, a description and a lambda. Nothing here removes the row.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Pin,
                            contentDescription = "Pin",
                            onClick = { pinned = !pinned },
                            variant = LemonadeButtonVariant.Primary,
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.Envelope,
                            contentDescription = "Mark unread",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                    allowsFullSwipe = false,
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Pin or mark unread",
                        supportText = if (pinned) "Pinned" else "Not pinned",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "three-actions") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Three actions",
                    subtitle = "Each arrives as the row clears it, outermost first.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { },
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.Pin,
                            contentDescription = "Pin",
                            onClick = { },
                            variant = LemonadeButtonVariant.Primary,
                        ),
                        SwipeAction(
                            icon = LemonadeIcons.Envelope,
                            contentDescription = "Mark unread",
                            onClick = { },
                            variant = LemonadeButtonVariant.Neutral,
                        ),
                    ),
                    allowsFullSwipe = false,
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Three actions",
                        supportText = "The row opens far enough for all of them",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }

        item(key = "any-row") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "Wrapping any row",
                    subtitle = "The content is whatever you pass, not only an ActionListItem.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { },
                        ),
                    ),
                    allowsFullSwipe = false,
                ) {
                    LemonadeUi.ContentListItem(
                        label = "Balance",
                        value = "£1,204.00",
                    )
                }
            }
        }

        item(key = "disabled") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "enabled = false",
                    subtitle = "The drag is left to whatever is scrolling underneath.",
                ),
            ) {
                LemonadeUi.SwipeActionRow(
                    trailingActions = listOf(
                        SwipeAction(
                            icon = LemonadeIcons.Trash,
                            contentDescription = "Delete",
                            onClick = { },
                        ),
                    ),
                    enabled = false,
                ) {
                    LemonadeUi.ActionListItem(
                        label = "Disabled",
                        supportText = "This row does not open",
                        showDivider = false,
                        onItemClicked = { },
                    )
                }
            }
        }
    }
}
