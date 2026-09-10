package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun RadioButtonDisplay() {
    var selectedOption by remember { mutableIntStateOf(0) }

    SampleScreenDisplayLazyColumn(title = "RadioButton") {
        item(key = "States") {
            RadioButtonSection(title = "States") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing600),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.RadioButton(
                            checked = false,
                            onRadioButtonClicked = {},
                        )
                        LemonadeUi.Text(
                            text = "Unchecked",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.RadioButton(
                            checked = true,
                            onRadioButtonClicked = {},
                        )
                        LemonadeUi.Text(
                            text = "Checked",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }
                }
            }
        }

        item(key = "Interactive Group") {
            RadioButtonSection(title = "Interactive Group") {
                for (index in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LemonadeUi.RadioButton(
                            checked = selectedOption == index,
                            onRadioButtonClicked = { selectedOption = index },
                        )
                        LemonadeUi.Text(
                            text = "Option ${index + 1}",
                            textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        )
                    }
                }
            }
        }

        item(key = "With Label") {
            RadioButtonSection(title = "With Label") {
                LemonadeUi.RadioButton(
                    checked = selectedOption == 0,
                    onRadioButtonClicked = { selectedOption = 0 },
                    label = "Free shipping",
                )

                LemonadeUi.RadioButton(
                    checked = selectedOption == 1,
                    onRadioButtonClicked = { selectedOption = 1 },
                    label = "Express delivery",
                )

                LemonadeUi.RadioButton(
                    checked = selectedOption == 2,
                    onRadioButtonClicked = { selectedOption = 2 },
                    label = "Same day delivery",
                )
            }
        }

        item(key = "With Support Text") {
            RadioButtonSection(title = "With Support Text") {
                LemonadeUi.RadioButton(
                    checked = true,
                    onRadioButtonClicked = {},
                    label = "Standard Plan",
                    supportText = "\$9.99/month - Basic features",
                )

                LemonadeUi.RadioButton(
                    checked = false,
                    onRadioButtonClicked = {},
                    label = "Premium Plan",
                    supportText = "\$19.99/month - All features included",
                )
            }
        }

        item(key = "Disabled") {
            RadioButtonSection(title = "Disabled") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LemonadeUi.RadioButton(
                        checked = false,
                        onRadioButtonClicked = {},
                        enabled = false,
                    )
                    LemonadeUi.Text(
                        text = "Disabled unchecked",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LemonadeUi.RadioButton(
                        checked = true,
                        onRadioButtonClicked = {},
                        enabled = false,
                    )
                    LemonadeUi.Text(
                        text = "Disabled checked",
                        textStyle = LemonadeTheme.typography.bodyMediumRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                    )
                }

                LemonadeUi.RadioButton(
                    checked = true,
                    onRadioButtonClicked = {},
                    label = "Disabled with label",
                    enabled = false,
                )
            }
        }
    }
}

@Composable
private fun RadioButtonSection(
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
