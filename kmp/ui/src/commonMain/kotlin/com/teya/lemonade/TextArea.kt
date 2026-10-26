package com.teya.lemonade

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shows a multi-line text input with optional label, support text, character counter, and error
 * state. The box keeps a fixed [height] and scrolls its content once the text outgrows it.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.TextArea(
 *     input = "Sample text",
 *     onInputChanged = { /* Nothing */ },
 *     label = "What happened",
 *     optionalIndicator = "Optional",
 *     placeholderText = "For example: I didn't make this payment",
 *     maxLength = 1000,
 * )
 * ```
 *
 * @param input current text
 * @param onInputChanged callback run with the new text whenever the user types
 * @param label text shown above the field, on the left
 * @param optionalIndicator text shown above the field, on the right
 * @param supportText text shown below the field
 * @param placeholderText text shown inside the field while [input] is empty
 * @param errorMessage text shown below the field while [error] is `true`
 * @param maxLength rejects any edit that would take the text past this many UTF-16 code units (an
 *   emoji can count as 2), and shows a `length/maxLength` counter below the field. Text already
 *   past the limit can still be shortened
 * @param height height of the input box, excluding the label and the text below it
 * @param interactionSource [MutableInteractionSource] applied to the field
 * @param keyboardOptions [KeyboardOptions] applied to the field
 * @param error `true` when the field shows its error state
 * @param enabled `false` dims the field and blocks input
 * @param modifier [Modifier] applied to the root container of the text area
 */
@Composable
public fun LemonadeUi.TextArea(
    input: String,
    onInputChanged: (String) -> Unit,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    placeholderText: String? = null,
    errorMessage: String? = null,
    maxLength: Int? = null,
    height: Dp = TextAreaDefaultHeight,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = TextAreaKeyboardOptions,
    error: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    BasicTextField(
        value = input,
        onValueChange = { value ->
            if (maxLength == null || value.length <= maxOf(maxLength, input.length)) onInputChanged(value)
        },
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(contentColor),
        textStyle = textAreaTextStyle(color = contentColor),
        singleLine = false,
        modifier = modifier,
        decorationBox = { innerTextField ->
            TextAreaDecorator(
                text = input,
                label = label,
                optionalIndicator = optionalIndicator,
                supportText = supportText,
                placeholderText = placeholderText,
                errorMessage = errorMessage,
                maxLength = maxLength,
                height = height,
                interactionSource = interactionSource,
                error = error,
                enabled = enabled,
                innerTextField = innerTextField,
            )
        },
    )
}

/**
 * Shows a multi-line text input backed by a [TextFieldState]. Pass a stable [inputTransformation]
 * instance — a new one on each recomposition can strand the input session.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberTextFieldState()
 *
 * LemonadeUi.TextArea(
 *     state = state,
 *     label = "What happened",
 *     placeholderText = "For example: I didn't make this payment",
 *     maxLength = 1000,
 * )
 * ```
 *
 * @param state [TextFieldState] holding the text and its selection
 * @param label text shown above the field, on the left
 * @param optionalIndicator text shown above the field, on the right
 * @param supportText text shown below the field
 * @param placeholderText text shown inside the field while [state] is empty
 * @param errorMessage text shown below the field while [error] is `true`
 * @param maxLength rejects any edit that would take the text past this many UTF-16 code units (an
 *   emoji can count as 2), and shows a `length/maxLength` counter below the field. Text already
 *   past the limit can still be shortened
 * @param height height of the input box, excluding the label and the text below it
 * @param interactionSource [MutableInteractionSource] applied to the field
 * @param keyboardOptions [KeyboardOptions] applied to the field
 * @param inputTransformation filters or rewrites each edit before the [maxLength] check
 * @param error `true` when the field shows its error state
 * @param enabled `false` dims the field and blocks input
 * @param modifier [Modifier] applied to the root container of the text area
 */
@Composable
public fun LemonadeUi.TextArea(
    state: TextFieldState,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    placeholderText: String? = null,
    errorMessage: String? = null,
    maxLength: Int? = null,
    height: Dp = TextAreaDefaultHeight,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = TextAreaKeyboardOptions,
    inputTransformation: InputTransformation? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    val combinedTransformation = remember(inputTransformation, maxLength) {
        val limit = maxLength?.let { length -> MaxLengthTransformation(maxLength = length) }
        when {
            limit == null -> inputTransformation
            inputTransformation == null -> limit
            else -> inputTransformation.then(next = limit)
        }
    }
    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        inputTransformation = combinedTransformation,
        lineLimits = TextFieldLineLimits.MultiLine(),
        cursorBrush = SolidColor(contentColor),
        textStyle = textAreaTextStyle(color = contentColor),
        modifier = modifier,
        decorator = { innerTextField ->
            TextAreaDecorator(
                text = state.text,
                label = label,
                optionalIndicator = optionalIndicator,
                supportText = supportText,
                placeholderText = placeholderText,
                errorMessage = errorMessage,
                maxLength = maxLength,
                height = height,
                interactionSource = interactionSource,
                error = error,
                enabled = enabled,
                innerTextField = innerTextField,
            )
        },
    )
}

private val TextAreaDefaultHeight = 180.dp

/**
 * Unlike [InputTransformation.Companion.maxLength], lets text that is already past the limit be
 * shortened, so a pre-filled value or a lowered limit never locks the field.
 */
private class MaxLengthTransformation(private val maxLength: Int) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (length > maxOf(maxLength, originalText.length)) revertAllChanges()
    }
}

private val TextAreaKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

@Composable
private fun textAreaTextStyle(color: Color): TextStyle =
    LocalTypographies.current.bodyMediumRegular.textStyle
        .copy(color = color)

@Suppress("LongParameterList")
@Composable
private fun TextAreaDecorator(
    text: CharSequence,
    label: String?,
    optionalIndicator: String?,
    supportText: String?,
    placeholderText: String?,
    errorMessage: String?,
    maxLength: Int?,
    height: Dp,
    interactionSource: MutableInteractionSource,
    error: Boolean,
    enabled: Boolean,
    innerTextField: @Composable () -> Unit,
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) {
            LocalOpacities.current.base.opacity100
        } else {
            LocalOpacities.current.state.opacityDisabled
        },
    )
    CoreTextFieldDecorator(
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        errorMessage = errorMessage,
        size = null,
        interactionSource = interactionSource,
        error = error,
        enabled = enabled,
        containerHeight = height,
        counter = maxLength?.let { limit -> "${text.length}/$limit" },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha = animatedAlpha)
                .padding(all = LocalSpaces.current.spacing400),
        ) {
            if (text.isEmpty() && placeholderText != null) {
                LemonadeUi.Text(
                    text = placeholderText,
                    textStyle = LocalTypographies.current.bodyMediumRegular,
                    color = LocalColors.current.content.contentSecondary,
                )
            }
            innerTextField()
        }
    }
}

@LemonadePreview
@Composable
private fun TextAreaPreview() {
    var input by remember { mutableStateOf("") }
    LemonadeUi.TextArea(
        input = input,
        onInputChanged = { value -> input = value },
        label = "What happened",
        optionalIndicator = "Optional",
        supportText = "Anything you add helps us look into it.",
        placeholderText = "For example: I didn't make this payment",
        maxLength = 1000,
    )
}

@LemonadePreview
@Composable
private fun TextAreaWithTextFieldStatePreview() {
    val state = rememberTextFieldState(initialText = "I didn't make this payment.")
    LemonadeUi.TextArea(
        state = state,
        label = "What happened",
        errorMessage = "Tell us a bit more",
        error = true,
        maxLength = 1000,
    )
}
