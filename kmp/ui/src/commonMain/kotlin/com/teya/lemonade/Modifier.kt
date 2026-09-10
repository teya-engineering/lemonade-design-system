package com.teya.lemonade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role

internal fun Modifier.modifyIf(
    predicate: Boolean,
    modify: Modifier.() -> Modifier,
): Modifier =
    then(
        other = if (predicate) {
            modify()
        } else {
            this
        },
    )

internal fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = clearFocusOnKeyboardDismiss(onKeyboardDismissed = null)

/**
 * Replaces the default [androidx.compose.ui.focus.FocusManager.clearFocus] reaction with
 * [onKeyboardDismissed].
 *
 * Hosts that must unfocus reliably on legacy Android hand focus to a [SearchFocusDecoy] instead.
 */
internal fun Modifier.clearFocusOnKeyboardDismiss(onKeyboardDismissed: (() -> Unit)?): Modifier {
    if (!supportsImeInsets()) {
        return this
    }
    return composed {
        var isFocused by remember { mutableStateOf(false) }
        var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

        if (isFocused) {
            val imeIsVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
            val focusManager = LocalFocusManager.current

            LaunchedEffect(imeIsVisible) {
                when {
                    keyboardAppearedSinceLastFocused ->
                        onKeyboardDismissed?.invoke()
                            ?: focusManager.clearFocus()
                    imeIsVisible -> keyboardAppearedSinceLastFocused = true
                }
            }
        }

        onFocusEvent { focusEvent ->
            isFocused = focusEvent.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Composable
public fun Modifier.clickable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    role: Role? = null,
): Modifier =
    clickable(
        enabled = enabled,
        onClick = onClick,
        interactionSource = interactionSource,
        role = role,
        indication = LocalEffects.current.interactionIndication,
    )
