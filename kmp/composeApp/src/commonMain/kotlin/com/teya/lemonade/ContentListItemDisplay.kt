package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeContentListItemDensity
import com.teya.lemonade.core.LemonadeContentListItemLayout
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice
import com.teya.lemonade.core.TagVoice

@Composable
internal fun ContentListItemDisplay() {
    SampleScreenDisplayLazyColumn(title = "ContentListItem") {
        item(key = "Horizontal — Simple") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal — Simple"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Account holder",
                    value = "John Doe",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Account number",
                    value = "123-456-789",
                    layout = LemonadeContentListItemLayout.Horizontal,
                )
            }
        }

        item(key = "Horizontal — Label Only") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal — Label Only"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Account holder",
                    value = "",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Terms and conditions agreement for the account holder " +
                        "regarding international transfers and currency exchange policies",
                    value = "",
                    layout = LemonadeContentListItemLayout.Horizontal,
                )
            }
        }

        item(key = "Horizontal — Label Only & Leading") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal — Label Only & Leading"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Favourite",
                    value = "",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Heart,
                            voice = SymbolContainerVoice.Neutral,
                            size = SymbolContainerSize.Medium,
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        item(key = "Vertical — Label Only") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Vertical — Label Only"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Balance",
                    value = "",
                    layout = LemonadeContentListItemLayout.Vertical,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "A much longer label that should fill the available width " +
                        "when there is no value or trailing element to share the row with",
                    value = "",
                    layout = LemonadeContentListItemLayout.Vertical,
                )
            }
        }

        item(key = "Horizontal Simple — Long Text") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal Simple — Long Text"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Terms and conditions agreement for the account holder " +
                        "regarding international transfers and currency exchange policies",
                    value = "This value is intentionally very long to demonstrate how " +
                        "the horizontal simple layout handles multi-line text wrapping " +
                        "across several lines in a constrained width",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Short label",
                    value = "A much longer value that should wrap onto multiple lines " +
                        "to test alignment behavior when only one side is long",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    showDivider = true,
                    verticalAlignment = Alignment.Top,
                )
                LemonadeUi.ContentListItem(
                    label = "Address",
                    value = "Westminster, London SW1A 2HQ, United Kingdom",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    verticalAlignment = Alignment.Top,
                )
            }
        }

        item(key = "Horizontal — Leading & Trailing") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal — Leading & Trailing"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Favourite",
                    value = "Enabled",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Heart,
                            voice = SymbolContainerVoice.Neutral,
                            size = SymbolContainerSize.Medium,
                            contentDescription = null,
                        )
                    },
                    trailingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.PencilLine,
                            tint = LemonadeTheme.colors.content.contentBrand,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = "Edit",
                        )
                    },
                )
            }
        }

        item(key = "Horizontal — Content Slot") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Horizontal — Content Slot"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Status",
                    value = "Active",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    contentSlot = {
                        LemonadeUi.Tag(
                            label = "Available",
                            voice = TagVoice.Positive,
                        )
                    },
                )
            }
        }

        item(key = "Vertical Small — Simple") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Vertical Small — Simple"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Balance",
                    value = "$1,234.56",
                    layout = LemonadeContentListItemLayout.Vertical,
                )
            }
        }

        item(key = "Vertical Small — Leading & Trailing") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Vertical Small — Leading & Trailing"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Savings",
                    value = "$5,678.90",
                    layout = LemonadeContentListItemLayout.Vertical,
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Heart,
                            voice = SymbolContainerVoice.Neutral,
                            size = SymbolContainerSize.Medium,
                            contentDescription = null,
                        )
                    },
                    trailingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.PencilLine,
                            tint = LemonadeTheme.colors.content.contentBrand,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = "Edit",
                        )
                    },
                )
            }
        }

        item(key = "Vertical Large — Content Slot") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Vertical Large — Content Slot"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Total balance",
                    value = "$12,345.67",
                    layout = LemonadeContentListItemLayout.Vertical,
                    contentSlot = {
                        LemonadeUi.Tag(
                            label = "Available",
                            voice = TagVoice.Positive,
                        )
                    },
                )
            }
        }

        item(key = "Vertical Large — Full") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Vertical Large — Full"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Investment portfolio",
                    value = "$98,765.43",
                    layout = LemonadeContentListItemLayout.Vertical,
                    leadingSlot = {
                        LemonadeUi.SymbolContainer(
                            icon = LemonadeIcons.Heart,
                            voice = SymbolContainerVoice.Neutral,
                            size = SymbolContainerSize.Medium,
                            contentDescription = null,
                        )
                    },
                    trailingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.PencilLine,
                            tint = LemonadeTheme.colors.content.contentBrand,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = "Edit",
                        )
                    },
                    contentSlot = {
                        LemonadeUi.Tag(
                            label = "Available",
                            voice = TagVoice.Positive,
                        )
                    },
                )
            }
        }

        item(key = "Density — Comfortable (default)") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Density — Comfortable (default)"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Account holder",
                    value = "John Doe",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    density = LemonadeContentListItemDensity.Comfortable,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Balance",
                    value = "$1,234.56",
                    layout = LemonadeContentListItemLayout.Vertical,
                    density = LemonadeContentListItemDensity.Comfortable,
                )
            }
        }

        item(key = "Density — Compact") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Density — Compact"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Account holder",
                    value = "John Doe",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    density = LemonadeContentListItemDensity.Compact,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Balance",
                    value = "$1,234.56",
                    layout = LemonadeContentListItemLayout.Vertical,
                    density = LemonadeContentListItemDensity.Compact,
                )
            }
        }

        item(key = "Mixed List with Dividers") {
            LemonadeUi.Card(
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                header = CardHeaderConfig(title = "Mixed List with Dividers"),
            ) {
                LemonadeUi.ContentListItem(
                    label = "Label",
                    value = "Value",
                    layout = LemonadeContentListItemLayout.Horizontal,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Label",
                    value = "Value",
                    layout = LemonadeContentListItemLayout.Vertical,
                    showDivider = true,
                )
                LemonadeUi.ContentListItem(
                    label = "Label",
                    value = "Value",
                    layout = LemonadeContentListItemLayout.Vertical,
                    contentSlot = {
                        LemonadeUi.Tag(
                            label = "Available",
                            voice = TagVoice.Positive,
                        )
                    },
                )
            }
        }
    }
}
