@file:OptIn(ExperimentalLemonadeComponent::class)

package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadePinCodeVariant

/**
 * Collects a PIN code, showing each entered character in its own box.
 *
 * Each box mirrors the styling of [LemonadeUi.TextField] — the same container shape, border,
 * focus ring, error and disabled states, font and height.
 *
 * Input always comes from the device's system keyboard, surfaced through a hidden field overlaid
 * on the boxes. The [variant] only selects which keyboard appears:
 * - [LemonadePinCodeVariant.Numeric] requests a numeric keyboard.
 * - [LemonadePinCodeVariant.Alphanumeric] requests the full keyboard.
 *
 * The indicator renders [length] boxes; each typed character is shown in its box. The component
 * appends to and removes from [value] internally, never letting it grow past [length]; the secret
 * stays in the caller's hoisted state.
 *
 * The hidden field uses the test tag `pin_code_field`, a stable contract for E2E tests.
 *
 * ## Usage
 * ```kotlin
 * var pin by remember { mutableStateOf("") }
 *
 * LemonadeUi.PinCode(
 *     value = pin,
 *     onValueChange = { pin = it },
 *     onComplete = { code -> /* validate */ },
 * )
 * ```
 *
 * @param value current entry, kept clamped to [length]
 * @param onValueChange called whenever the entry changes
 * @param variant which system keyboard to request for input
 * @param length number of characters to enter; defaults to 6
 * @param error when true the boxes turn critical and shake, re-triggering on each rising edge
 * @param submitting when true the boxes show the disabled style and input is disabled
 * @param autoFocus when true the field requests focus, opening the keyboard without a tap. Focus
 *   is requested on first composition and again whenever the field becomes enabled (e.g. after
 *   [submitting] clears). Use for a screen whose only purpose is entering this code
 * @param contentDescription accessibility label for the input, announced by screen readers. The
 *   boxes carry no visible label, so set this to what the code is for (e.g. "Verification code")
 * @param oneTimeCodeAutofill when true the field offers the OS one-time-code autofill suggestion.
 *   Set false to suppress it on flows where the suggestion is unwanted (the keyboard's plain
 *   numeric/character input still works)
 * @param onComplete called once when [value] reaches [length]
 * @param modifier optional [Modifier] applied to the root container
 */
@ExperimentalLemonadeComponent
@Composable
public fun LemonadeUi.PinCode(
    value: String,
    onValueChange: (String) -> Unit,
    variant: LemonadePinCodeVariant = LemonadePinCodeVariant.Numeric,
    length: Int = 6,
    error: Boolean = false,
    submitting: Boolean = false,
    autoFocus: Boolean = false,
    contentDescription: String? = null,
    oneTimeCodeAutofill: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    require(
        value = length > 0,
        lazyMessage = { "PinCode length must be greater than zero." },
    )

    val haptic = LocalHapticFeedback.current
    val shakeOffset = remember { Animatable(initialValue = 0f) }
    var focused by remember { mutableStateOf(value = false) }

    LaunchedEffect(error) {
        if (error) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            shakeOffset.snapTo(targetValue = 0f)
            ShakeKeyframes.forEach { target ->
                shakeOffset.animateTo(
                    targetValue = target,
                    animationSpec = tween(
                        durationMillis = SHAKE_STEP_MILLIS,
                        easing = LinearEasing,
                    ),
                )
            }
        }
    }

    LaunchedEffect(value) {
        clampToLengthAndReportCompletion(
            value = value,
            length = length,
            onValueChange = onValueChange,
            onComplete = onComplete,
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        PinCodeIndicator(
            value = value,
            length = length,
            error = error,
            enabled = !submitting,
            focused = focused,
            shakeOffset = shakeOffset.value,
        )
        PinCodeHiddenField(
            value = value,
            onValueChange = onValueChange,
            variant = variant,
            length = length,
            enabled = !submitting,
            autoFocus = autoFocus,
            contentDescription = contentDescription,
            oneTimeCodeAutofill = oneTimeCodeAutofill,
            onFocusChanged = { hasFocus -> focused = hasFocus },
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Composable
private fun PinCodeIndicator(
    value: String,
    length: Int,
    error: Boolean,
    enabled: Boolean,
    focused: Boolean,
    shakeOffset: Float,
    modifier: Modifier = Modifier,
) {
    val filledCount = value.length.coerceAtMost(maximumValue = length)

    Row(
        modifier = modifier
            .offset {
                IntOffset(
                    x = shakeOffset.dp.roundToPx(),
                    y = 0,
                )
            }.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(times = length) { index ->
            val isActive = enabled && focused && index == filledCount
            PinCodeBox(
                character = value.getOrNull(index = index),
                isActive = isActive,
                error = error,
                enabled = enabled,
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
}

@Composable
private fun PinCodeBox(
    character: Char?,
    isActive: Boolean,
    error: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = LocalShapes.current.radius400

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> LocalColors.current.background.bgElevated
            error && !isActive -> LocalColors.current.background.bgCriticalSubtle
            else -> LocalColors.current.background.bgSubtle.copy(
                alpha = LocalOpacities.current.base.opacity0,
            )
        },
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            // Keep a muted but visible outline when disabled — the boxes have no label/content to
            // fall back on, so a transparent border (as the text field uses) makes them vanish into
            // the page. The dimmed fill still reads as disabled.
            !enabled -> LocalColors.current.border.borderNeutralLow
            isActive -> LocalColors.current.border.borderSelected
            error && !isActive -> LocalColors.current.border.borderCritical
            else -> LocalColors.current.border.borderNeutralMedium
        },
    )
    val focusRingWidth by animateDpAsState(
        targetValue = if (isActive) {
            LocalBorderWidths.current.base.border50
        } else {
            LocalBorderWidths.current.base.border0
        },
    )

    Box(
        modifier = modifier
            .requiredHeight(height = LocalSizes.current.size1400)
            // `shadowBorder` is a `composed` modifier (opts out of skipping); only pay for it on
            // the box that actually shows the focus ring instead of on all of them every keystroke.
            .then(
                other = if (focusRingWidth > 0.dp) {
                    Modifier.shadowBorder(
                        width = focusRingWidth,
                        shape = shape,
                        color = LocalColors.current.background.bgElevated,
                    )
                } else {
                    Modifier
                },
            ).clip(shape = shape)
            .border(
                color = borderColor,
                width = LocalBorderWidths.current.base.border25,
                shape = shape,
            ).background(
                color = LocalColors.current.background.bgDefault,
                shape = shape,
            ).background(
                color = backgroundColor,
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (character != null) {
            LemonadeUi.Text(
                text = character.toString(),
                textStyle = LocalTypographies.current.bodyMediumMedium,
                color = if (enabled) {
                    LocalColors.current.content.contentPrimary
                } else {
                    LocalColors.current.content.contentTertiary
                },
            )
        }
    }
}

@Composable
private fun PinCodeHiddenField(
    value: String,
    onValueChange: (String) -> Unit,
    variant: LemonadePinCodeVariant,
    length: Int,
    enabled: Boolean,
    autoFocus: Boolean,
    contentDescription: String?,
    oneTimeCodeAutofill: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardType = when (variant) {
        LemonadePinCodeVariant.Numeric -> KeyboardType.Number
        LemonadePinCodeVariant.Alphanumeric -> KeyboardType.Ascii
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(autoFocus, enabled) {
        if (autoFocus && enabled) focusRequester.requestFocus()
    }

    // Drive the field through a [TextFieldValue] rather than a raw String so the cursor stays
    // pinned to the end of the clamped text. The String overload lets Compose's IME buffer keep
    // the characters typed past [length]: focus then drifts off the last box and backspace has to
    // chew through those invisible characters before it reaches a visible digit.
    val clamped = value.take(n = length)
    var fieldValue by remember { mutableStateOf(value = pinnedToEnd(text = clamped)) }
    // Resync from an effect — not during composition — when the entry changes underneath us: an
    // external write, or the parent clamping an oversized value. Overflow typing is already pinned
    // in onValueChange below, so this only needs to track [clamped].
    LaunchedEffect(clamped) {
        if (fieldValue.text != clamped) fieldValue = pinnedToEnd(text = clamped)
    }

    BasicTextField(
        value = fieldValue,
        onValueChange = { input ->
            val next = input.text.take(n = length)
            // Push the buffer back to the end even when [next] matches the current text (overflow
            // typing), so the IME drops the extra characters instead of parking the cursor past them.
            fieldValue = pinnedToEnd(text = next)
            if (next != clamped) onValueChange(next)
        },
        enabled = enabled,
        singleLine = true,
        cursorBrush = SolidColor(value = Color.Transparent),
        textStyle = LocalTypographies.current.bodyMediumRegular.textStyle
            .copy(color = Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .testTag(tag = FIELD_TEST_ID)
            .focusRequester(focusRequester = focusRequester)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
                if (!oneTimeCodeAutofill) contentDataType = ContentDataType.None
            }.onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) },
    )
}

private fun clampToLengthAndReportCompletion(
    value: String,
    length: Int,
    onValueChange: (String) -> Unit,
    onComplete: ((String) -> Unit)?,
) {
    val clamped = value.take(n = length)
    if (clamped != value) {
        // The write-back re-runs the effect with the clamped value and reports there;
        // returning avoids firing onComplete twice for one externally-set oversized value.
        onValueChange(clamped)
        return
    }
    if (clamped.length == length) onComplete?.invoke(clamped)
}

/** Collapses the caret to the end so rejected overflow never parks it past the digits. */
private fun pinnedToEnd(text: String): TextFieldValue =
    TextFieldValue(
        text = text,
        selection = TextRange(text.length),
    )

private const val FIELD_TEST_ID = "pin_code_field"

private val ShakeKeyframes = listOf(-10f, 10f, -8f, 8f, -5f, 5f, 0f)

private const val SHAKE_STEP_MILLIS = 35

@LemonadePreview
@Composable
private fun PinCodeNumericPreview() {
    LemonadeUi.PinCode(
        value = "123",
        onValueChange = { },
    )
}

@LemonadePreview
@Composable
private fun PinCodeNumericErrorPreview() {
    LemonadeUi.PinCode(
        value = "123",
        onValueChange = { },
        error = true,
    )
}

@LemonadePreview
@Composable
private fun PinCodeNumericSubmittingPreview() {
    LemonadeUi.PinCode(
        value = "12",
        onValueChange = { },
        submitting = true,
    )
}

@LemonadePreview
@Composable
private fun PinCodeAlphanumericPreview() {
    LemonadeUi.PinCode(
        value = "aB3",
        onValueChange = { },
        variant = LemonadePinCodeVariant.Alphanumeric,
    )
}
