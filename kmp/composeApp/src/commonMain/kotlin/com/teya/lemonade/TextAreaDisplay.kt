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
private const val LONG_TEXT = "This text is longer than five lines, so the box opens at its tallest and " +
    "scrolls the rest. Type or delete a few lines to see it grow and shrink between one line and " +
    "five. Every line past the fifth stays inside the box, which keeps the same height while the " +
    "content scrolls under it."

@Suppress("LongMethod")
@Composable
internal fun TextAreaDisplay() {
    var basicText by remember { mutableStateOf("") }
    var counterText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("Not enough detail") }
    val stateText = rememberTextFieldState()
    var growingText by remember { mutableStateOf("") }
    var longText by remember { mutableStateOf(LONG_TEXT) }
    val growingState = rememberTextFieldState()

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
                    errorMessage = "Shorten this to $MAX_LENGTH characters",
                    maxLength = MAX_LENGTH,
                    error = counterText.length > MAX_LENGTH,
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

        item(key = "line-limits") {
            TextAreaSection(title = "Grows From 1 to 5 Lines") {
                LemonadeUi.TextArea(
                    input = growingText,
                    onInputChanged = { value -> growingText = value },
                    label = "Message",
                    placeholderText = "Type a few lines...",
                    lineLimits = 1..5,
                )
            }
        }

        item(key = "line-limits-long-text") {
            TextAreaSection(title = "Grows From 1 to 5 Lines, Opened With Long Text") {
                LemonadeUi.TextArea(
                    input = longText,
                    onInputChanged = { value -> longText = value },
                    label = "Message",
                    lineLimits = 1..5,
                )
            }
        }

        item(key = "line-limits-text-field-state") {
            TextAreaSection(title = "Grows From 2 to 4 Lines, TextFieldState") {
                LemonadeUi.TextArea(
                    state = growingState,
                    label = "Message",
                    placeholderText = "Type a few lines...",
                    lineLimits = 2..4,
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
