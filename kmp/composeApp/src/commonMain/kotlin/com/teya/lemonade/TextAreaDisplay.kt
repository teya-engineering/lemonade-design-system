package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

private const val MAX_LENGTH = 1000

@Suppress("LongMethod")
@Composable
internal fun TextAreaDisplay() {
    var basicText by remember { mutableStateOf("") }
    var counterText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("Not enough detail") }
    val stateText = rememberTextFieldState()

    SampleScreenDisplayLazyColumn(title = "TextArea") {
        item(key = "basic") {
            TextAreaSection(title = "Basic") {
                LemonadeUi.TextArea(
                    input = basicText,
                    onInputChanged = { value -> basicText = value },
                    placeholderText = "Enter text...",
                )
            }
        }

        item(key = "with-counter") {
            TextAreaSection(title = "With Label and Counter") {
                LemonadeUi.TextArea(
                    input = counterText,
                    onInputChanged = { value -> counterText = value },
                    label = "What happened",
                    optionalIndicator = "Optional",
                    supportText = "Anything you add helps us look into it.",
                    placeholderText = "For example: I didn't make this payment and I've never heard of " +
                        "Acme Suppliers Ltd.",
                    maxLength = MAX_LENGTH,
                )
            }
        }

        item(key = "with-error") {
            TextAreaSection(title = "With Error") {
                LemonadeUi.TextArea(
                    input = errorText,
                    onInputChanged = { value -> errorText = value },
                    label = "What happened",
                    errorMessage = "Tell us a bit more",
                    error = true,
                    maxLength = MAX_LENGTH,
                )
            }
        }

        item(key = "text-field-state") {
            TextAreaSection(title = "TextFieldState") {
                LemonadeUi.TextArea(
                    state = stateText,
                    label = "Notes",
                    placeholderText = "Enter notes...",
                    maxLength = MAX_LENGTH,
                )
            }
        }

        item(key = "disabled") {
            TextAreaSection(title = "Disabled") {
                LemonadeUi.TextArea(
                    input = "This field is disabled",
                    onInputChanged = { },
                    label = "What happened",
                    supportText = "Support text",
                    maxLength = MAX_LENGTH,
                    enabled = false,
                )
            }
        }
    }
}

@Composable
private fun TextAreaSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
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
