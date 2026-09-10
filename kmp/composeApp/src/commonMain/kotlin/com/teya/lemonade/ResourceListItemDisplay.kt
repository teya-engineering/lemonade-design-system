package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice
import com.teya.lemonade.core.TagVoice

@Composable
internal fun ResourceListItemDisplay() {
    SampleScreenDisplayLazyColumn(title = "ResourceListItem") {
        item(key = "ResourceListItem") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
                header = CardHeaderConfig(title = "ResourceListItem"),
            ) {
                LemonadeUi.ResourceListItem(
                    label = "Account Balance",
                    value = "$1,234.56",
                    supportText = "Updated today",
                    showDivider = true,
                    onItemClicked = {},
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Money,
                            contentDescription = null,
                            voice = SymbolContainerVoice.Info,
                            size = SymbolContainerSize.Medium,
                        )
                    },
                )

                LemonadeUi.ResourceListItem(
                    label = "Savings",
                    value = "$5,000.00",
                    onItemClicked = {},
                    showDivider = true,
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Coins,
                            contentDescription = null,
                            voice = SymbolContainerVoice.Positive,
                            size = SymbolContainerSize.Medium,
                        )
                    },
                )
            }
        }

        item(key = "ResourceListItem with Addon") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
                header = CardHeaderConfig(title = "ResourceListItem with Addon"),
            ) {
                LemonadeUi.ResourceListItem(
                    label = "Last Transaction",
                    value = "-$50.00",
                    onItemClicked = {},
                    showDivider = false,
                    supportText = "Yesterday",
                    addonSlot = {
                        LemonadeUi.Tag(
                            label = "Pending",
                            voice = TagVoice.Warning,
                        )
                    },
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.ArrowUpRight,
                            contentDescription = null,
                            voice = SymbolContainerVoice.Critical,
                            size = SymbolContainerSize.Medium,
                        )
                    },
                )
            }
        }
    }
}
