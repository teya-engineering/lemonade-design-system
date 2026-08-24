package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeBadgeSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeListItemPriority
import com.teya.lemonade.core.LemonadeListItemVoice
import com.teya.lemonade.core.TagVoice

@Composable
internal fun ActionListItemDisplay() {
    SampleScreenDisplayLazyColumn(title = "ActionListItem") {
        // ListItem - Layout Priority (priority = Trailing, default)
        item(key = "priority-trailing") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "priority: Trailing (default)",
                    subtitle = "Trailing keeps its full width; the label truncates to fit.",
                ),
            ) {
                LemonadeUi.ListItem(
                    label = "Beneficiary account holder",
                    labelMaxLines = 1,
                    labelOverflow = TextOverflow.Ellipsis,
                    trailingSlot = {
                        LemonadeUi.Text(
                            text = "International Holdings Ltd Partnership",
                            textStyle = LemonadeTheme.typography.bodyMediumMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }

        // ListItem - Layout Priority (priority = Label)
        item(key = "priority-label") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(
                    title = "priority: Label",
                    subtitle = "Label keeps its full width; the trailing content truncates to fit.",
                ),
            ) {
                LemonadeUi.ListItem(
                    label = "Beneficiary account holder",
                    showDivider = true,
                    priority = LemonadeListItemPriority.Label,
                    labelMaxLines = 1,
                    labelOverflow = TextOverflow.Ellipsis,
                    trailingSlot = {
                        LemonadeUi.Text(
                            text = "International Holdings Ltd Partnership",
                            textStyle = LemonadeTheme.typography.bodyMediumMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )

                // Edge case: a label long enough to fill the row keeps the trailing at its
                // readable floor instead of letting it vanish.
                LemonadeUi.ListItem(
                    label = "Beneficiary account holder full legal registered name",
                    priority = LemonadeListItemPriority.Label,
                    labelMaxLines = 1,
                    labelOverflow = TextOverflow.Ellipsis,
                    trailingSlot = {
                        LemonadeUi.Text(
                            text = "International Holdings Ltd Partnership",
                            textStyle = LemonadeTheme.typography.bodyMediumMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }

        // ListItem - Layout Priority (priority = Both)
        LemonadeUi.Card(
            header = CardHeaderConfig(
                title = "priority: Both",
                subtitle = "Neither side wins — the width splits 50/50 and both truncate together.",
            ),
        ) {
            LemonadeUi.ListItem(
                label = "Beneficiary account holder",
                priority = LemonadeListItemPriority.Both,
                labelMaxLines = 1,
                labelOverflow = TextOverflow.Ellipsis,
                trailingSlot = {
                    LemonadeUi.Text(
                        text = "International Holdings Ltd Partnership",
                        textStyle = LemonadeTheme.typography.bodyMediumMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }

        // ActionListItem - Truncation
        item(key = "truncation") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Truncation"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "This is a very long label that should be truncated to a single line with an ellipsis",
                    supportText = "And this is an even longer support text that wraps across multiple lines " +
                        "before it gets truncated, so we can clearly see the maxLines and overflow props " +
                        "doing their job on the second line of the row",
                    labelMaxLines = 1,
                    labelOverflow = TextOverflow.Ellipsis,
                    supportTextMaxLines = 2,
                    supportTextOverflow = TextOverflow.Ellipsis,
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bell,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Same long label without truncation wraps onto as many lines as it needs",
                    supportText = "Default behaviour: no maxLines set, so this support text wraps freely " +
                        "instead of being clipped",
                    onItemClicked = {},
                    showDivider = false,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bell,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )
            }
        }

        // ActionListItem
        item(key = "action-list-item") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Settings",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Gear,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Notifications",
                    supportText = "Manage your notifications",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bell,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Privacy",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Padlock,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )
            }
        }

        // ActionListItem - Top Label
        item(key = "top-label") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Top Label"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Notifications",
                    topLabel = "Account",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bell,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Two-factor auth",
                    topLabel = "Security",
                    supportText = "Recommended",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Padlock,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "What's new",
                    topLabel = "Release notes",
                    onItemClicked = {},
                    showDivider = false,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Sparkles,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    trailingSlot = {
                        LemonadeUi.Tag(label = "New", voice = TagVoice.Positive)
                    },
                )
            }
        }

        // ActionListItem - Trailing Alignment
        item(key = "trailing-alignment") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Trailing Alignment"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Top aligned",
                    supportText = "trailingVerticalAlignment: Top\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = true,
                    trailingVerticalAlignment = Alignment.Top,
                    onItemClicked = {},
                    trailingSlot = {
                        LemonadeUi.Tag(label = "Top", voice = TagVoice.Info)
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Center aligned",
                    supportText = "trailingVerticalAlignment: CenterVertically\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = true,
                    trailingVerticalAlignment = Alignment.CenterVertically,
                    onItemClicked = {},
                    trailingSlot = {
                        LemonadeUi.Tag(label = "Center", voice = TagVoice.Positive)
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Bottom aligned",
                    supportText = "trailingVerticalAlignment: Bottom\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = false,
                    trailingVerticalAlignment = Alignment.Bottom,
                    onItemClicked = {},
                    trailingSlot = {
                        LemonadeUi.Tag(label = "Bottom", voice = TagVoice.Warning)
                    },
                )
            }
        }

        // ActionListItem - Leading Alignment
        item(key = "leading-alignment") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Leading Alignment"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Top aligned (default)",
                    supportText = "leadingVerticalAlignment: Top\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = true,
                    leadingVerticalAlignment = Alignment.Top,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Gear,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Center aligned",
                    supportText = "leadingVerticalAlignment: CenterVertically\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = true,
                    leadingVerticalAlignment = Alignment.CenterVertically,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bell,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Bottom aligned",
                    supportText = "leadingVerticalAlignment: Bottom\nSecond line",
                    showNavigationIndicator = true,
                    showDivider = false,
                    leadingVerticalAlignment = Alignment.Bottom,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Padlock,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )
            }
        }

        // ActionListItem - Slot Content (below support text)
        item(key = "slot-content") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Slot Content"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Account ***4236",
                    supportText = "PT50 0002 0123 1234…",
                    showNavigationIndicator = true,
                    showDivider = true,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Bank,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    slotContent = {
                        LemonadeUi.Tag(
                            label = "Settlements",
                            voice = TagVoice.Positive,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Payout",
                    supportText = "Arrives tomorrow",
                    showNavigationIndicator = true,
                    showDivider = true,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Coins,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    slotContent = {
                        LemonadeUi.Text(
                            text = "Reference: PYT-00123 • EUR account",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Updates Available",
                    supportText = "3 new features",
                    showNavigationIndicator = true,
                    showDivider = false,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Download,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    slotContent = {
                        LemonadeUi.Badge(
                            text = "3",
                            size = LemonadeBadgeSize.Small,
                        )
                    },
                )
            }
        }

        // ActionListItem with Trailing Slot
        item(key = "with-trailing") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem with Trailing"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Updates Available",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Download,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    showDivider = true,
                    trailingSlot = {
                        LemonadeUi.Badge(text = "3", size = LemonadeBadgeSize.Small)
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "New Features",
                    showNavigationIndicator = true,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Sparkles,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                    showDivider = true,
                    trailingSlot = {
                        LemonadeUi.Tag(label = "New", voice = TagVoice.Positive)
                    },
                )
            }
        }

        item(key = "critical-voice") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "ActionListItem - Critical Voice"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Delete Account",
                    voice = LemonadeListItemVoice.Critical,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Trash,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                            tint = LemonadeTheme.colors.content.contentCritical,
                        )
                    },
                )

                LemonadeUi.ActionListItem(
                    label = "Log Out",
                    supportText = "You will need to sign in again",
                    voice = LemonadeListItemVoice.Critical,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.LogOut,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                            tint = LemonadeTheme.colors.content.contentCritical,
                        )
                    },
                )
            }
        }

        item(key = "disabled-states") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Disabled States"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "Disabled Action",
                    enabled = false,
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Gear,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )
            }
        }

        item(key = "loading-state") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Loading State"),
            ) {
                LemonadeUi.ActionListItem(
                    label = "",
                    isLoading = true,
                    showDivider = true,
                )

                LemonadeUi.ActionListItem(
                    label = "",
                    isLoading = true,
                    showDivider = true,
                )

                LemonadeUi.ActionListItem(
                    label = "",
                    isLoading = true,
                )
            }
        }
    }
}
