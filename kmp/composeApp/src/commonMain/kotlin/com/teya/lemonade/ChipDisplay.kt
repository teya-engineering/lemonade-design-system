package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeCountryFlags
import com.teya.lemonade.core.LemonadeIcons

@Composable
internal fun ChipDisplay() {
    var selectedChips by remember { mutableStateOf(setOf("Option 1")) }

    SampleScreenDisplayLazyColumn(title = "Chip") {
        item(key = "States") {
            ChipSection(title = "States") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Chip(
                            label = "Unselected",
                            selected = false,
                            leadingIcon = null,
                        )
                        LemonadeUi.Text(
                            text = "Unselected",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Chip(
                            label = "Selected",
                            selected = true,
                            leadingIcon = null,
                        )
                        LemonadeUi.Text(
                            text = "Selected",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }
                }
            }
        }

        item(key = "With Counter") {
            ChipSection(title = "With Counter") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Chip(
                        label = "Messages",
                        selected = false,
                        leadingIcon = null,
                        counter = 5,
                    )
                    LemonadeUi.Chip(
                        label = "Notifications",
                        selected = true,
                        leadingIcon = null,
                        counter = 12,
                    )
                }
            }
        }

        item(key = "With Icons") {
            ChipSection(title = "With Icons") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                    ) {
                        LemonadeUi.Chip(
                            label = "Favorites",
                            selected = false,
                            leadingIcon = LemonadeIcons.Heart,
                        )
                        LemonadeUi.Chip(
                            label = "Favorites",
                            selected = true,
                            leadingIcon = LemonadeIcons.Heart,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                    ) {
                        LemonadeUi.Chip(
                            label = "Remove",
                            selected = false,
                            leadingIcon = null,
                            trailingIcon = LemonadeIcons.CircleX,
                        )
                        LemonadeUi.Chip(
                            label = "Remove",
                            selected = true,
                            leadingIcon = null,
                            trailingIcon = LemonadeIcons.CircleX,
                        )
                    }
                }
            }
        }

        item(key = "With Custom Leading") {
            ChipSection(title = "With Custom Leading") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Chip(
                        label = "GBP",
                        selected = false,
                        leadingContent = {
                            LemonadeUi.CountryFlag(flag = LemonadeCountryFlags.GBUnitedKingdom)
                        },
                        trailingContent = {
                            LemonadeUi.Icon(
                                icon = LemonadeIcons.ChevronDown,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }

        item(key = "Interactive Selection") {
            ChipSection(title = "Interactive Selection") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Text(
                        text = "Tap to select/deselect:",
                        textStyle = LemonadeTheme.typography.bodySmallRegular,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        listOf("Option 1", "Option 2", "Option 3")
                            .forEach { option ->
                                LemonadeUi.Chip(
                                    label = option,
                                    selected = selectedChips.contains(option),
                                    leadingIcon = null,
                                    onChipClicked = {
                                        selectedChips = if (selectedChips.contains(option)) {
                                            selectedChips - option
                                        } else {
                                            selectedChips + option
                                        }
                                    },
                                )
                            }
                    }

                    val selectedLabel = selectedChips
                        .sorted()
                        .joinToString(separator = ", ")

                    LemonadeUi.Text(
                        text = "Selected: $selectedLabel",
                        textStyle = LemonadeTheme.typography.bodySmallRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                    )
                }
            }
        }

        item(key = "Disabled") {
            ChipSection(title = "Disabled") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    LemonadeUi.Chip(
                        label = "Disabled",
                        selected = false,
                        leadingIcon = null,
                        enabled = false,
                    )
                    LemonadeUi.Chip(
                        label = "Disabled",
                        selected = true,
                        leadingIcon = null,
                        enabled = false,
                    )
                }
            }
        }

        item(key = "Error") {
            ChipSection(title = "Error") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                        ) {
                            LemonadeUi.Chip(
                                label = "Error",
                                selected = false,
                                leadingIcon = null,
                                error = true,
                            )
                            LemonadeUi.Text(
                                text = "Error",
                                textStyle = LemonadeTheme.typography.bodySmallRegular,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                        ) {
                            LemonadeUi.Chip(
                                label = "Error",
                                selected = false,
                                leadingIcon = null,
                                enabled = false,
                                error = true,
                            )
                            LemonadeUi.Text(
                                text = "Error Disabled",
                                textStyle = LemonadeTheme.typography.bodySmallRegular,
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                    ) {
                        LemonadeUi.Chip(
                            label = "With Icon",
                            selected = false,
                            leadingIcon = LemonadeIcons.CircleAlert,
                            error = true,
                        )
                        LemonadeUi.Chip(
                            label = "With Trailing",
                            selected = false,
                            leadingIcon = null,
                            trailingIcon = LemonadeIcons.CircleX,
                            error = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
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
