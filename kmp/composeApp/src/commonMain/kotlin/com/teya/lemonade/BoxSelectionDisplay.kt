package com.teya.lemonade

import androidx.compose.foundation.background
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
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeBoxSelectionBackground
import com.teya.lemonade.core.LemonadeBoxSelectionVariant
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeRadius
import com.teya.lemonade.core.LemonadeSpaces
import com.teya.lemonade.core.SymbolContainerShape
import com.teya.lemonade.core.SymbolContainerSize
import com.teya.lemonade.core.TagVoice

@Composable
internal fun BoxSelectionDisplay() {
    SampleScreenDisplayLazyColumn(title = "BoxSelection") {
        item(key = "Variants") {
            BoxSelectionSection(title = "Variants") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Filled,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Filled")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Outlined")
                    }
                }
            }
        }

        item(key = "Background") {
            BoxSelectionSection(title = "Background") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        background = LemonadeBoxSelectionBackground.Default,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Default")
                    }

                    LemonadeUi.BoxSelection(
                        background = LemonadeBoxSelectionBackground.Elevated,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Elevated")
                    }
                }
            }
        }

        item(key = "Selected") {
            BoxSelectionSection(title = "Selected") {
                var isFilledSelected by remember { mutableStateOf(value = true) }
                var isOutlinedSelected by remember { mutableStateOf(value = true) }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Filled,
                        isSelected = isFilledSelected,
                        onClick = { isFilledSelected = !isFilledSelected },
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Filled")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        isSelected = isOutlinedSelected,
                        onClick = { isOutlinedSelected = !isOutlinedSelected },
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Outlined")
                    }
                }
            }
        }

        item(key = "Disabled") {
            BoxSelectionSection(title = "Disabled") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Filled,
                        enabled = false,
                        onClick = {},
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Filled")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        enabled = false,
                        onClick = {},
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Outlined")
                    }
                }
            }
        }

        item(key = "Content Padding") {
            BoxSelectionSection(title = "Content Padding") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        contentPadding = LemonadeSpaces.Spacing100,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Spacing100")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Spacing300")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        contentPadding = LemonadeSpaces.Spacing600,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Spacing600")
                    }
                }
            }
        }

        item(key = "Radius") {
            BoxSelectionSection(title = "Radius") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        radius = LemonadeRadius.Radius0,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Radius0")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Radius500")
                    }

                    LemonadeUi.BoxSelection(
                        variant = LemonadeBoxSelectionVariant.Outlined,
                        radius = LemonadeRadius.Radius800,
                        modifier = Modifier.weight(weight = 1f),
                    ) {
                        BoxSelectionSampleContent(label = "Radius800")
                    }
                }
            }
        }

        // The slot takes a whole card layout, not just the icon-and-label content a Tile carries.
        item(key = "Plan Cards") {
            BoxSelectionSection(title = "Use Case: Plan Cards") {
                var selectedPlan by remember { mutableStateOf(value = plans[1].name) }

                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    plans.forEach { plan ->
                        val isPlanSelected = selectedPlan == plan.name
                        val selectPlan = { selectedPlan = plan.name }

                        LemonadeUi.BoxSelection(
                            isSelected = isPlanSelected,
                            onClick = selectPlan,
                            contentPadding = LemonadeSpaces.Spacing400,
                            radius = LemonadeRadius.Radius600,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            PlanCardContent(
                                plan = plan,
                                isSelected = isPlanSelected,
                                onSelect = selectPlan,
                            )
                        }
                    }
                }
            }
        }

        item(key = "Single Selection") {
            BoxSelectionSection(title = "Use Case: Single Selection") {
                var selectedOption by remember { mutableStateOf(value = frequencyOptions.first()) }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                ) {
                    frequencyOptions.forEach { option ->
                        LemonadeUi.BoxSelection(
                            variant = LemonadeBoxSelectionVariant.Outlined,
                            isSelected = selectedOption == option,
                            onClick = { selectedOption = option },
                            modifier = Modifier.weight(weight = 1f),
                        ) {
                            LemonadeUi.Text(
                                text = option,
                                textStyle = LemonadeTheme.typography.bodySmallMedium,
                                modifier = Modifier.align(alignment = Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

private val frequencyOptions = listOf("Weekly", "Monthly", "Yearly")

private data class PlanOption(
    val name: String,
    val price: String,
    val description: String,
    val icon: LemonadeIcons,
    val tag: String? = null,
)

private val plans = listOf(
    PlanOption(
        name = "Starter",
        price = "Free",
        description = "Take card payments with a Teya reader and get paid the next working day.",
        icon = LemonadeIcons.Card,
    ),
    PlanOption(
        name = "Business",
        price = "£12.99 / month",
        description = "Everything in Starter, plus invoicing, expense tracking and same-day payouts.",
        icon = LemonadeIcons.Chart,
        tag = "Most popular",
    ),
    PlanOption(
        name = "Enterprise",
        price = "Custom pricing",
        description = "Multi-site reporting, custom payout schedules and a dedicated account manager.",
        icon = LemonadeIcons.HandCoins,
    ),
)

@Composable
private fun PlanCardContent(
    plan: PlanOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing300),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LemonadeUi.SymbolContainer(
                icon = plan.icon,
                contentDescription = null,
                shape = SymbolContainerShape.Rounded,
                size = SymbolContainerSize.Large,
            )

            Column(modifier = Modifier.weight(weight = 1f)) {
                LemonadeUi.Text(
                    text = plan.name,
                    textStyle = LemonadeTheme.typography.headingXSmall,
                )
                LemonadeUi.Text(
                    text = plan.price,
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                    color = LemonadeTheme.colors.content.contentSecondary,
                )
            }

            LemonadeUi.RadioButton(
                checked = isSelected,
                onRadioButtonClicked = onSelect,
            )
        }

        LemonadeUi.HorizontalDivider()

        LemonadeUi.Text(
            text = plan.description,
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )

        if (plan.tag != null) {
            LemonadeUi.Tag(
                label = plan.tag,
                voice = TagVoice.Positive,
            )
        }
    }
}

@Composable
private fun BoxSelectionSampleContent(label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
        modifier = Modifier.fillMaxWidth(),
    ) {
        LemonadeUi.Icon(
            icon = LemonadeIcons.Heart,
            contentDescription = null,
            size = LemonadeAssetSize.Medium,
        )

        LemonadeUi.Text(
            text = label,
            textStyle = LemonadeTheme.typography.bodySmallMedium,
        )
    }
}

@Composable
private fun BoxSelectionSection(
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
