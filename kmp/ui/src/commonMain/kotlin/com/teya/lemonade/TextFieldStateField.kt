package com.teya.lemonade

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Shows a single-line text input with optional label, support text, and error state.
 *
 * This overload takes a [TextFieldState], which owns the text and the selection itself. Prefer it
 * over the [String] and [TextFieldValue] overloads for anything that filters or formats input.
 *
 * [inputTransformation] runs synchronously on every edit — keyboard, IME, paste, accessibility —
 * before the state commits, so a rejected character never reaches the buffer and the field has no
 * intermediate state to re-sync. [outputTransformation] decorates the text for display without
 * putting the decoration in the buffer, so the value stays canonical, the caret maps itself, and
 * the IME session is never restarted (rewriting a [TextFieldValue] on each keystroke restarts the
 * input connection, which is what makes a numeric keyboard flicker back to its letters layer).
 *
 * ## Usage
 * ```kotlin
 * val state = rememberTextFieldState()
 *
 * LemonadeUi.TextField(
 *     state = state,
 *     inputTransformation = InputTransformation.maxLength(34),
 *     outputTransformation = { /* replace(4, 4, " ") */ },
 *     label = "IBAN",
 * )
 * ```
 *
 * Pass stable [inputTransformation] and [outputTransformation] instances — a new instance on each
 * recomposition rebuilds the field's internal transformed state and can strand the input session.
 *
 * @param state [TextFieldState] holding the text and its selection
 * @param label text shown above the field, on the left
 * @param optionalIndicator text shown above the field, on the right
 * @param supportText text shown below the field
 * @param placeholderText text shown inside the field while [state] is empty
 * @param errorMessage text shown below the field while [error] is `true`
 * @param interactionSource [MutableInteractionSource] applied to the field
 * @param keyboardOptions [KeyboardOptions] applied to the field
 * @param onKeyboardAction handler for the keyboard's action key
 * @param inputTransformation filters or rewrites each edit before it reaches the buffer
 * @param outputTransformation decorates the text for display only
 * @param error `true` when the field shows its error state
 * @param enabled `false` dims the field and blocks input
 * @param leadingContent content shown before the input
 * @param trailingContent content shown after the input
 * @param modifier [Modifier] applied to the root container of the text field
 */
@Composable
public fun LemonadeUi.TextField(
    state: TextFieldState,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    placeholderText: String? = null,
    errorMessage: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) {
            LocalOpacities.current.base.opacity100
        } else {
            LocalOpacities.current.state.opacityDisabled
        },
    )
    CoreTextField(
        state = state,
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        errorMessage = errorMessage,
        size = null,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        error = error,
        enabled = enabled,
        modifier = modifier,
        textBoxContent = { innerTextField ->
            DefaultTextBox(
                placeholderText = placeholderText,
                size = null,
                showPlaceholder = state.text.isEmpty(),
                leadingContent = leadingContent,
                trailingContent = trailingContent,
                innerTextField = innerTextField,
                modifier = Modifier.alpha(alpha = animatedAlpha),
            )
        },
    )
}

/**
 * Wraps the [BasicTextField] [TextFieldState] API.
 *
 * The decoration is the same tree the other two overloads use — [BasicTextField]'s `decorationBox`
 * and [androidx.compose.foundation.text.input.TextFieldDecorator] are the same inverted slot, so
 * only the wrapper type differs.
 */
@Composable
internal fun CoreTextField(
    state: TextFieldState,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    errorMessage: String? = null,
    size: TextFieldSize? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    textBoxContent: @Composable BoxScope.(@Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        lineLimits = TextFieldLineLimits.SingleLine,
        cursorBrush = SolidColor(contentColor),
        textStyle = size.data.contentStyle.textStyle.copy(
            color = contentColor,
        ),
        modifier = modifier,
        decorator = { innerTextField ->
            CoreTextFieldDecorator(
                label = label,
                optionalIndicator = optionalIndicator,
                supportText = supportText,
                errorMessage = errorMessage,
                size = size,
                interactionSource = interactionSource,
                error = error,
                enabled = enabled,
                textBoxContent = { textBoxContent(innerTextField) },
            )
        },
    )
}
