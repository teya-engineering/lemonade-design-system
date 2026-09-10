package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SelectListItemType
import com.teya.lemonade.core.SelectListItemVariant
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.SymbolContainerVoice
import com.teya.lemonade.core.TagVoice

private data class OutlinedOption(
    val label: String,
    val icon: LemonadeIcons,
)

private data class TrailingPreset(
    val label: String,
    val voice: TagVoice,
)

private val outlinedOptions: List<OutlinedOption> = listOf(
    OutlinedOption(
        label = "Option A",
        icon = LemonadeIcons.Heart,
    ),
    OutlinedOption(
        label = "Option B",
        icon = LemonadeIcons.Star,
    ),
    OutlinedOption(
        label = "Option C",
        icon = LemonadeIcons.Sparkles,
    ),
    OutlinedOption(
        label = "Option D",
        icon = LemonadeIcons.Gift,
    ),
)

private val outlinedOptionsFirstThree: List<OutlinedOption> = outlinedOptions.take(n = 3)

private val trailingPresets: List<TrailingPreset> = listOf(
    TrailingPreset(
        label = "New",
        voice = TagVoice.Info,
    ),
    TrailingPreset(
        label = "Recommended",
        voice = TagVoice.Positive,
    ),
    TrailingPreset(
        label = "Popular",
        voice = TagVoice.Neutral,
    ),
)

private fun Set<Int>.toggle(index: Int): Set<Int> =
    if (contains(element = index)) {
        this - index
    } else {
        this + index
    }

@Suppress("LongMethod")
@Composable
internal fun SelectListItemDisplay() {
    var plainSingle by rememberSaveable { mutableIntStateOf(value = 0) }
    // Set-typed selections stay on `remember`: they are not covered by the default saveable savers.
    var plainMultiple by remember { mutableStateOf(value = setOf(0)) }
    var plainToggles by remember { mutableStateOf(value = setOf(0)) }
    var plainLeadingIcon by rememberSaveable { mutableStateOf(value = true) }
    var emailDigest by rememberSaveable { mutableStateOf(value = true) }
    var marketingEmails by rememberSaveable { mutableStateOf(value = false) }
    var outlinedWithLeading by rememberSaveable { mutableIntStateOf(value = 0) }
    var outlinedWithTrailing by rememberSaveable { mutableIntStateOf(value = 1) }
    var outlinedLabelOnly by rememberSaveable { mutableIntStateOf(value = 0) }
    var outlinedWithSupport by rememberSaveable { mutableIntStateOf(value = 0) }
    var outlinedMultiple by remember { mutableStateOf(value = setOf(0)) }
    var outlinedSlotContent by rememberSaveable { mutableIntStateOf(value = 0) }

    SampleScreenDisplayLazyColumn(title = "SelectListItem") {
        item(key = "plain-single") {
            SelectListItemCard(title = "Plain — Single") {
                for (index in 0..2) {
                    LemonadeUi.SelectListItem(
                        label = "Option ${index + 1}",
                        type = SelectListItemType.Single,
                        checked = plainSingle == index,
                        onItemClicked = { plainSingle = index },
                        supportText = if (index == 0) "With support text" else null,
                        showDivider = true,
                    )
                }
            }
        }

        item(key = "plain-multiple") {
            SelectListItemCard(title = "Plain — Multiple") {
                for (index in 0..2) {
                    LemonadeUi.SelectListItem(
                        label = "Item ${index + 1}",
                        type = SelectListItemType.Multiple,
                        checked = plainMultiple.contains(index),
                        onItemClicked = { plainMultiple = plainMultiple.toggle(index = index) },
                    )
                }
            }
        }

        item(key = "plain-toggle") {
            SelectListItemCard(title = "Plain — Toggle") {
                for (index in 0..2) {
                    LemonadeUi.SelectListItem(
                        label = "Setting ${index + 1}",
                        type = SelectListItemType.Toggle,
                        checked = plainToggles.contains(index),
                        onItemClicked = { plainToggles = plainToggles.toggle(index = index) },
                    )
                }
            }
        }

        item(key = "plain-leading-icon") {
            SelectListItemCard(title = "Plain — With leading icon") {
                LemonadeUi.SelectListItem(
                    label = "With icon",
                    type = SelectListItemType.Single,
                    checked = plainLeadingIcon,
                    onItemClicked = { plainLeadingIcon = !plainLeadingIcon },
                    supportText = "Leading icon example",
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Star,
                            contentDescription = null,
                            size = LemonadeAssetSize.Medium,
                        )
                    },
                )
            }
        }

        item(key = "plain-slot-content") {
            SelectListItemCard(title = "Plain — Slot Content") {
                LemonadeUi.SelectListItem(
                    label = "Email digest",
                    type = SelectListItemType.Toggle,
                    checked = emailDigest,
                    onItemClicked = { emailDigest = !emailDigest },
                    supportText = "Weekly summary of your account",
                    showDivider = true,
                    slotContent = {
                        LemonadeUi.Tag(
                            label = "Recommended",
                            voice = TagVoice.Positive,
                        )
                    },
                )

                LemonadeUi.SelectListItem(
                    label = "Marketing emails",
                    type = SelectListItemType.Toggle,
                    checked = marketingEmails,
                    onItemClicked = { marketingEmails = !marketingEmails },
                    supportText = "Offers from partners",
                    slotContent = {
                        LemonadeUi.Text(
                            text = "You can opt out at any time",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                            color = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                )
            }
        }

        item(key = "plain-disabled") {
            SelectListItemCard(title = "Plain — Disabled") {
                LemonadeUi.SelectListItem(
                    label = "Disabled option",
                    type = SelectListItemType.Single,
                    checked = false,
                    onItemClicked = { },
                    enabled = false,
                )

                LemonadeUi.SelectListItem(
                    label = "Disabled toggle",
                    type = SelectListItemType.Toggle,
                    checked = true,
                    onItemClicked = { },
                    enabled = false,
                )
            }
        }

        item(key = "outlined-leading-icon") {
            SelectListItemCard(title = "Outlined — Leading icon only") {
                OutlinedGroup {
                    outlinedOptions.forEachIndexed { index, option ->
                        val isChecked = outlinedWithLeading == index
                        LemonadeUi.SelectListItem(
                            label = option.label,
                            type = SelectListItemType.Single,
                            variant = SelectListItemVariant.Outlined,
                            checked = isChecked,
                            onItemClicked = { outlinedWithLeading = index },
                            leadingSlot = {
                                OptionSymbol(
                                    icon = option.icon,
                                    isChecked = isChecked,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "outlined-leading-trailing") {
            SelectListItemCard(title = "Outlined — Leading + trailing tag") {
                OutlinedGroup {
                    outlinedOptionsFirstThree.forEachIndexed { index, option ->
                        val isChecked = outlinedWithTrailing == index
                        val preset = trailingPresets[index]
                        LemonadeUi.SelectListItem(
                            label = option.label,
                            type = SelectListItemType.Single,
                            variant = SelectListItemVariant.Outlined,
                            checked = isChecked,
                            onItemClicked = { outlinedWithTrailing = index },
                            leadingSlot = {
                                OptionSymbol(
                                    icon = option.icon,
                                    isChecked = isChecked,
                                )
                            },
                            trailingSlot = {
                                LemonadeUi.Tag(
                                    label = preset.label,
                                    voice = preset.voice,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "outlined-label-only") {
            SelectListItemCard(title = "Outlined — Label only (no leading, no trailing)") {
                OutlinedGroup {
                    for (index in 0..2) {
                        LemonadeUi.SelectListItem(
                            label = "Option ${index + 1}",
                            type = SelectListItemType.Single,
                            variant = SelectListItemVariant.Outlined,
                            checked = outlinedLabelOnly == index,
                            onItemClicked = { outlinedLabelOnly = index },
                        )
                    }
                }
            }
        }

        item(key = "outlined-support-text") {
            SelectListItemCard(title = "Outlined — With support text") {
                OutlinedGroup {
                    outlinedOptionsFirstThree.forEachIndexed { index, option ->
                        LemonadeUi.SelectListItem(
                            label = option.label,
                            type = SelectListItemType.Single,
                            variant = SelectListItemVariant.Outlined,
                            checked = outlinedWithSupport == index,
                            onItemClicked = { outlinedWithSupport = index },
                            supportText = "Short description for ${option.label.lowercase()}",
                            leadingSlot = {
                                OptionSymbol(
                                    icon = option.icon,
                                    isChecked = false,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "outlined-multiple") {
            SelectListItemCard(title = "Outlined — Multiple") {
                OutlinedGroup {
                    outlinedOptions.forEachIndexed { index, option ->
                        val isChecked = outlinedMultiple.contains(index)
                        LemonadeUi.SelectListItem(
                            label = option.label,
                            type = SelectListItemType.Multiple,
                            variant = SelectListItemVariant.Outlined,
                            checked = isChecked,
                            onItemClicked = { outlinedMultiple = outlinedMultiple.toggle(index = index) },
                            supportText = "Tap to toggle".takeIf { index == 0 },
                            leadingSlot = {
                                OptionSymbol(
                                    icon = option.icon,
                                    isChecked = isChecked,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "outlined-slot-content") {
            SelectListItemCard(title = "Outlined — Slot Content") {
                OutlinedGroup {
                    outlinedOptionsFirstThree.forEachIndexed { index, option ->
                        val isChecked = outlinedSlotContent == index
                        val preset = trailingPresets[index]
                        LemonadeUi.SelectListItem(
                            label = option.label,
                            type = SelectListItemType.Single,
                            variant = SelectListItemVariant.Outlined,
                            checked = isChecked,
                            onItemClicked = { outlinedSlotContent = index },
                            supportText = "Short description for ${option.label.lowercase()}",
                            leadingSlot = {
                                OptionSymbol(
                                    icon = option.icon,
                                    isChecked = isChecked,
                                )
                            },
                            slotContent = {
                                LemonadeUi.Tag(
                                    label = preset.label,
                                    voice = preset.voice,
                                )
                            },
                        )
                    }
                }
            }
        }

        item(key = "outlined-disabled") {
            SelectListItemCard(title = "Outlined — Disabled states") {
                OutlinedGroup {
                    LemonadeUi.SelectListItem(
                        label = "Disabled, no leading",
                        type = SelectListItemType.Single,
                        variant = SelectListItemVariant.Outlined,
                        checked = false,
                        enabled = false,
                        onItemClicked = { },
                    )

                    LemonadeUi.SelectListItem(
                        label = "Disabled, with leading",
                        type = SelectListItemType.Single,
                        variant = SelectListItemVariant.Outlined,
                        checked = false,
                        enabled = false,
                        onItemClicked = { },
                        leadingSlot = {
                            OptionSymbol(
                                icon = LemonadeIcons.Padlock,
                                isChecked = false,
                            )
                        },
                    )

                    LemonadeUi.SelectListItem(
                        label = "Disabled, with trailing tag",
                        type = SelectListItemType.Single,
                        variant = SelectListItemVariant.Outlined,
                        checked = false,
                        enabled = false,
                        onItemClicked = { },
                        leadingSlot = {
                            OptionSymbol(
                                icon = LemonadeIcons.Bell,
                                isChecked = false,
                            )
                        },
                        trailingSlot = {
                            LemonadeUi.Tag(
                                label = "Coming Soon",
                                voice = TagVoice.Neutral,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionSymbol(
    icon: LemonadeIcons,
    isChecked: Boolean,
) {
    LemonadeUi.SymbolContainer(
        icon = icon,
        contentDescription = null,
        size = SymbolContainerSize.Medium,
        shape = SymbolContainerShape.Rounded,
        voice = if (isChecked) {
            SymbolContainerVoice.Positive
        } else {
            SymbolContainerVoice.Neutral
        },
    )
}

@Composable
private fun OutlinedGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
        content = content,
    )
}

@Composable
private fun SelectListItemCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    LemonadeUi.Card(
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
        header = CardHeaderConfig(title = title),
        content = content,
    )
}
