package com.teya.lemonade

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun Modifier.secureFieldModifier(enabled: Boolean): Modifier {
    val window =
        LocalView.current.context
            .findActivity()
            ?.window
    DisposableEffect(window, enabled) {
        if (enabled && window != null) {
            SecureFlag.acquire(window)
        }
        onDispose {
            if (enabled && window != null) {
                SecureFlag.release(window)
            }
        }
    }
    return this
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

/**
 * Reference-counts `FLAG_SECURE` per window.
 *
 * With several secure regions on screen, disposing one does not clear the flag while others remain.
 * Only touched from the main (composition) thread, so no synchronisation is needed.
 */
private object SecureFlag {
    private val counts = mutableMapOf<Window, Int>()

    fun acquire(window: Window) {
        val previous = counts[window]
            ?: 0
        val count = previous + 1
        counts[window] = count
        if (count == 1) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun release(window: Window) {
        val count = counts[window]
            ?: return // never acquired here — leave the host's flag untouched
        if (count <= 1) {
            counts.remove(window)
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            counts[window] = count - 1
        }
    }
}
