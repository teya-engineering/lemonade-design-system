package com.teya.lemonade

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

private const val PHONE_GROUP_SIZE = 3

private fun formatPhoneNumber(raw: String): String =
    raw
        .filter(Char::isDigit)
        .chunked(size = PHONE_GROUP_SIZE)
        .joinToString(separator = " ")

@Suppress("LongMethod")
@Composable
internal fun TextFieldDisplay() {
    val toasts = LocalLemonadeToastState.current

    var basicText by remember { mutableStateOf("") }
    var labeledText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("Invalid input") }
    var supportText by remember { mutableStateOf("") }
    var leadingText by remember { mutableStateOf("") }
    var trailingText by remember { mutableStateOf("") }
    var selectorText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Example of TextFieldValue-based usage for cursor control. The value is formatted synchronously
    // inside onValueChange so the cursor lands at the end of the formatted text on the same frame.
    var phoneTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    SampleScreenDisplayLazyColumn(title = "TextField") {
        item(key = "basic") {
            TextFieldSection(title = "Basic") {
                LemonadeUi.TextField(
                    input = basicText,
                    onInputChanged = { value -> basicText = value },
                    placeholderText = "Enter text...",
                )
            }
        }

        item(key = "with-label") {
            TextFieldSection(title = "With Label") {
                LemonadeUi.TextField(
                    input = labeledText,
                    onInputChanged = { value -> labeledText = value },
                    label = "Email Address",
                    placeholderText = "you@example.com",
                )
            }
        }

        item(key = "with-error") {
            TextFieldSection(title = "With Error") {
                LemonadeUi.TextField(
                    input = errorText,
                    onInputChanged = { value -> errorText = value },
                    label = "Username",
                    placeholderText = "Enter username",
                    errorMessage = "Username is already taken",
                    error = true,
                )
            }
        }

        item(key = "with-support-text") {
            TextFieldSection(title = "With Support Text") {
                LemonadeUi.TextField(
                    input = supportText,
                    onInputChanged = { value -> supportText = value },
                    label = "Password",
                    supportText = "Must be at least 8 characters",
                    placeholderText = "Enter password",
                )
            }
        }

        item(key = "with-leading-icon") {
            TextFieldSection(title = "With Leading Icon") {
                LemonadeUi.TextField(
                    input = leadingText,
                    onInputChanged = { value -> leadingText = value },
                    label = "Search",
                    placeholderText = "Search...",
                    leadingContent = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Search,
                            contentDescription = null,
                            tint = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                )
            }
        }

        item(key = "with-trailing-icon") {
            TextFieldSection(title = "With Trailing Icon") {
                LemonadeUi.TextField(
                    input = trailingText,
                    onInputChanged = { value -> trailingText = value },
                    label = "Amount",
                    placeholderText = "0.00",
                    trailingContent = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.CircleInfo,
                            contentDescription = null,
                            tint = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                )
            }
        }

        item(key = "secure") {
            TextFieldSection(title = "Secure (Password)") {
                LemonadeUi.TextField(
                    input = passwordText,
                    onInputChanged = { value -> passwordText = value },
                    label = "Password",
                    placeholderText = "Enter password",
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    leadingContent = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.Padlock,
                            contentDescription = null,
                            tint = LemonadeTheme.colors.content.contentSecondary,
                        )
                    },
                    trailingContent = {
                        LemonadeUi.Icon(
                            icon = if (passwordVisible) LemonadeIcons.EyeOpen else LemonadeIcons.EyeClosed,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = LemonadeTheme.colors.content.contentSecondary,
                            modifier = Modifier.clickable(role = Role.Button) {
                                passwordVisible = !passwordVisible
                            },
                        )
                    },
                )
            }
        }

        item(key = "with-selector") {
            TextFieldSection(title = "TextField With Selector") {
                LemonadeUi.TextFieldWithSelector(
                    input = selectorText,
                    onInputChanged = { value -> selectorText = value },
                    leadingAction = { toasts.show(label = "Show country code picker") },
                    leadingContent = {
                        CountryCodeSelectorContent(dialCode = "+1")
                    },
                    label = "Phone Number",
                    placeholderText = "Enter phone number",
                )
            }
        }

        item(key = "with-selector-cursor-control") {
            TextFieldSection(title = "TextField With Selector (Cursor Control)") {
                LemonadeUi.TextFieldWithSelector(
                    value = phoneTextFieldValue,
                    onValueChange = { newValue ->
                        val formatted = formatPhoneNumber(raw = newValue.text)
                        phoneTextFieldValue = TextFieldValue(
                            text = formatted,
                            selection = TextRange(formatted.length),
                        )
                    },
                    leadingAction = { toasts.show(label = "Show country code picker") },
                    leadingContent = {
                        CountryCodeSelectorContent(dialCode = "+351")
                    },
                    label = "Phone (with cursor control)",
                    placeholderText = "Enter phone number",
                    supportText = "Try typing - cursor stays at end after formatting",
                )
            }
        }

        item(key = "disabled") {
            TextFieldSection(title = "Disabled") {
                LemonadeUi.TextField(
                    input = "Disabled content",
                    onInputChanged = {},
                    label = "Disabled Field",
                    placeholderText = "Cannot edit",
                    enabled = false,
                )
            }
        }
    }
}

@Composable
private fun CountryCodeSelectorContent(dialCode: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing100),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LemonadeUi.Text(
            text = dialCode,
            textStyle = LemonadeTheme.typography.bodyMediumMedium,
        )
        LemonadeUi.Icon(
            icon = LemonadeIcons.ChevronDown,
            contentDescription = null,
            size = LemonadeAssetSize.Small,
        )
    }
}

@Composable
private fun TextFieldSection(
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
