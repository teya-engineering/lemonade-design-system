package com.teya.lemonade

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
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
 * Shows a multi-line text input with label, support text, counter and error state.
 *
 * The box keeps a fixed [height] and scrolls its content once the text outgrows it. To size the
 * box to its text instead, use the overload that takes `lineLimits`.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.TextArea(
 *     input = "Sample text",
 *     onInputChanged = { },
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
 * @param maxLength shows a `length/maxLength` counter below the field, in UTF-16 code units (an
 *   emoji can count as 2). Input is never blocked: past the limit the counter turns critical, and
 *   the caller should show an [errorMessage] and hold back submission until the text is shortened
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
    InputTextArea(
        input = input,
        onInputChanged = onInputChanged,
        sizing = TextAreaSizing.Fixed(height = height),
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        placeholderText = placeholderText,
        errorMessage = errorMessage,
        maxLength = maxLength,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        error = error,
        enabled = enabled,
        modifier = modifier,
    )
}

/**
 * Shows a multi-line text input that grows with its text.
 *
 * The box is never shorter than the first value of [lineLimits] or taller than the last. Past the
 * last line its content scrolls.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.TextArea(
 *     input = "Sample text",
 *     onInputChanged = { },
 *     lineLimits = 1..5,
 *     placeholderText = "Write a message",
 * )
 * ```
 *
 * @param input current text
 * @param onInputChanged callback run with the new text whenever the user types
 * @param lineLimits the fewest and most lines of text the box shows. The range must start at 1 or
 *   more and must not be empty
 * @param label text shown above the field, on the left
 * @param optionalIndicator text shown above the field, on the right
 * @param supportText text shown below the field
 * @param placeholderText text shown inside the field while [input] is empty
 * @param errorMessage text shown below the field while [error] is `true`
 * @param maxLength shows a `length/maxLength` counter below the field, in UTF-16 code units (an
 *   emoji can count as 2). Input is never blocked: past the limit the counter turns critical, and
 *   the caller should show an [errorMessage] and hold back submission until the text is shortened
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
    lineLimits: IntRange,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    placeholderText: String? = null,
    errorMessage: String? = null,
    maxLength: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = TextAreaKeyboardOptions,
    error: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    InputTextArea(
        input = input,
        onInputChanged = onInputChanged,
        sizing = TextAreaSizing.Lines(lineLimits = lineLimits),
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        placeholderText = placeholderText,
        errorMessage = errorMessage,
        maxLength = maxLength,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        error = error,
        enabled = enabled,
        modifier = modifier,
    )
}

/**
 * Shows a multi-line text input backed by a [TextFieldState]. Pass a stable [inputTransformation]
 * instance — a new one on each recomposition can strand the input session.
 *
 * The box keeps a fixed [height] and scrolls its content once the text outgrows it. To size the
 * box to its text instead, use the overload that takes `lineLimits`.
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
 * @param maxLength shows a `length/maxLength` counter below the field, in UTF-16 code units (an
 *   emoji can count as 2). Input is never blocked: past the limit the counter turns critical, and
 *   the caller should show an [errorMessage] and hold back submission until the text is shortened
 * @param height height of the input box, excluding the label and the text below it
 * @param interactionSource [MutableInteractionSource] applied to the field
 * @param keyboardOptions [KeyboardOptions] applied to the field
 * @param inputTransformation filters or rewrites each edit before it reaches [state]
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
    StateTextArea(
        state = state,
        sizing = TextAreaSizing.Fixed(height = height),
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        placeholderText = placeholderText,
        errorMessage = errorMessage,
        maxLength = maxLength,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        inputTransformation = inputTransformation,
        error = error,
        enabled = enabled,
        modifier = modifier,
    )
}

/**
 * Shows a multi-line text input backed by a [TextFieldState] that grows with its text. Pass a
 * stable [inputTransformation] instance — a new one on each recomposition can strand the input
 * session.
 *
 * The box is never shorter than the first value of [lineLimits] or taller than the last. Past the
 * last line its content scrolls.
 *
 * ## Usage
 * ```kotlin
 * val state = rememberTextFieldState()
 *
 * LemonadeUi.TextArea(
 *     state = state,
 *     lineLimits = 2..4,
 *     placeholderText = "Write a message",
 * )
 * ```
 *
 * @param state [TextFieldState] holding the text and its selection
 * @param lineLimits the fewest and most lines of text the box shows. The range must start at 1 or
 *   more and must not be empty
 * @param label text shown above the field, on the left
 * @param optionalIndicator text shown above the field, on the right
 * @param supportText text shown below the field
 * @param placeholderText text shown inside the field while [state] is empty
 * @param errorMessage text shown below the field while [error] is `true`
 * @param maxLength shows a `length/maxLength` counter below the field, in UTF-16 code units (an
 *   emoji can count as 2). Input is never blocked: past the limit the counter turns critical, and
 *   the caller should show an [errorMessage] and hold back submission until the text is shortened
 * @param interactionSource [MutableInteractionSource] applied to the field
 * @param keyboardOptions [KeyboardOptions] applied to the field
 * @param inputTransformation filters or rewrites each edit before it reaches [state]
 * @param error `true` when the field shows its error state
 * @param enabled `false` dims the field and blocks input
 * @param modifier [Modifier] applied to the root container of the text area
 */
@Composable
public fun LemonadeUi.TextArea(
    state: TextFieldState,
    lineLimits: IntRange,
    label: String? = null,
    optionalIndicator: String? = null,
    supportText: String? = null,
    placeholderText: String? = null,
    errorMessage: String? = null,
    maxLength: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardOptions: KeyboardOptions = TextAreaKeyboardOptions,
    inputTransformation: InputTransformation? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    StateTextArea(
        state = state,
        sizing = TextAreaSizing.Lines(lineLimits = lineLimits),
        label = label,
        optionalIndicator = optionalIndicator,
        supportText = supportText,
        placeholderText = placeholderText,
        errorMessage = errorMessage,
        maxLength = maxLength,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        inputTransformation = inputTransformation,
        error = error,
        enabled = enabled,
        modifier = modifier,
    )
}

internal sealed interface TextAreaSizing {
    data class Fixed(val height: Dp) : TextAreaSizing

    data class Lines(val lineLimits: IntRange) : TextAreaSizing {
        init {
            require(lineLimits.first >= 1) { "TextArea lineLimits must start at 1 or more, was $lineLimits" }
            require(!lineLimits.isEmpty()) { "TextArea lineLimits must not be empty, was $lineLimits" }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun InputTextArea(
    input: String,
    onInputChanged: (String) -> Unit,
    sizing: TextAreaSizing,
    label: String?,
    optionalIndicator: String?,
    supportText: String?,
    placeholderText: String?,
    errorMessage: String?,
    maxLength: Int?,
    interactionSource: MutableInteractionSource,
    keyboardOptions: KeyboardOptions,
    error: Boolean,
    enabled: Boolean,
    modifier: Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    val lineLimits = (sizing as? TextAreaSizing.Lines)?.lineLimits
    BasicTextField(
        value = input,
        onValueChange = onInputChanged,
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(contentColor),
        textStyle = textAreaTextStyle(color = contentColor),
        singleLine = false,
        minLines = lineLimits?.first ?: 1,
        maxLines = lineLimits?.last ?: Int.MAX_VALUE,
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
                sizing = sizing,
                interactionSource = interactionSource,
                error = error,
                enabled = enabled,
                innerTextField = innerTextField,
            )
        },
    )
}

@Suppress("LongParameterList")
@Composable
private fun StateTextArea(
    state: TextFieldState,
    sizing: TextAreaSizing,
    label: String?,
    optionalIndicator: String?,
    supportText: String?,
    placeholderText: String?,
    errorMessage: String?,
    maxLength: Int?,
    interactionSource: MutableInteractionSource,
    keyboardOptions: KeyboardOptions,
    inputTransformation: InputTransformation?,
    error: Boolean,
    enabled: Boolean,
    modifier: Modifier,
) {
    val contentColor = LocalColors.current.content.contentPrimary
    BasicTextField(
        state = state,
        interactionSource = interactionSource,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        inputTransformation = inputTransformation,
        lineLimits = when (sizing) {
            is TextAreaSizing.Fixed -> TextFieldLineLimits.MultiLine()
            is TextAreaSizing.Lines -> TextFieldLineLimits.MultiLine(
                minHeightInLines = sizing.lineLimits.first,
                maxHeightInLines = sizing.lineLimits.last,
            )
        },
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
                sizing = sizing,
                interactionSource = interactionSource,
                error = error,
                enabled = enabled,
                innerTextField = innerTextField,
            )
        },
    )
}

private val TextAreaDefaultHeight = 180.dp

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
    sizing: TextAreaSizing,
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
        containerHeight = (sizing as? TextAreaSizing.Fixed)?.height,
        counter = maxLength?.let { limit -> "${text.length}/$limit" },
        counterOverLimit = maxLength != null && text.length > maxLength,
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (sizing is TextAreaSizing.Fixed) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier.fillMaxWidth()
                    },
                ).alpha(alpha = animatedAlpha)
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
private fun TextAreaWithLineLimitsPreview() {
    var input by remember { mutableStateOf("") }
    LemonadeUi.TextArea(
        input = input,
        onInputChanged = { value -> input = value },
        lineLimits = 1..5,
        label = "Message",
        placeholderText = "Starts at one line and grows up to five",
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
