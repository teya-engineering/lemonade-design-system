package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeIcons

@Composable
internal fun SelectFieldDisplay() {
    SampleScreenDisplayLazyColumn(title = "SelectField") {
        item(key = "Basic") {
            SelectFieldSection(title = "Basic") {
                LemonadeUi.SelectField(
                    onClick = { },
                    selectedValue = "ABC",
                    placeholderText = "Select an option",
                )
            }
        }

        item(key = "With Label") {
            SelectFieldSection(title = "With Label") {
                LemonadeUi.SelectField(
                    onClick = { },
                    placeholderText = "Select a category",
                    label = "Category",
                    selectedValue = null,
                )
            }
        }

        item(key = "Filled") {
            SelectFieldSection(title = "Filled") {
                LemonadeUi.SelectField(
                    onClick = { },
                    selectedValue = "English",
                    label = "Language",
                )
            }
        }

        item(key = "With Leading Icon") {
            SelectFieldSection(title = "With Leading Icon") {
                LemonadeUi.SelectField(
                    onClick = { },
                    selectedValue = "Favourites",
                    label = "Collection",
                    leadingContent = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Heart,
                            contentDescription = null,
                            tint = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                )
            }
        }

        item(key = "With Error") {
            SelectFieldSection(title = "With Error") {
                LemonadeUi.SelectField(
                    onClick = { },
                    placeholderText = "Select an option",
                    label = "Required Field",
                    errorMessage = "Please select an option",
                    error = true,
                    selectedValue = null,
                )
            }
        }

        item(key = "With Support Text") {
            SelectFieldSection(title = "With Support Text") {
                LemonadeUi.SelectField(
                    onClick = { },
                    placeholderText = "Select a country",
                    label = "Country",
                    supportText = "Choose your country of residence",
                    optionalIndicator = "Optional",
                    selectedValue = null,
                )
            }
        }

        item(key = "Disabled") {
            SelectFieldSection(title = "Disabled") {
                LemonadeUi.SelectField(
                    onClick = { },
                    selectedValue = "Locked value",
                    label = "Disabled Field",
                    enabled = false,
                )
            }
        }
    }
}

@Composable
private fun SelectFieldSection(
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
