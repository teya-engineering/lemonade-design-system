package com.teya.lemonade

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areNavigationBarsVisible
import androidx.compose.foundation.layout.areStatusBarsVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.view.WindowInsets as AndroidWindowInsets

private data class HiddenSystemBars(
    val statusBar: Boolean,
    val navigationBar: Boolean,
) {
    val any: Boolean
        get() = statusBar || navigationBar
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal actual fun systemBarsMirror(forceHideNavigationBar: Boolean): @Composable () -> Unit {
    val hidden = HiddenSystemBars(
        statusBar = !WindowInsets.areStatusBarsVisible,
        navigationBar = forceHideNavigationBar || !WindowInsets.areNavigationBarsVisible,
    )
    return { MirrorSystemBars(hidden = hidden) }
}

@Composable
private fun MirrorSystemBars(hidden: HiddenSystemBars) {
    val view = LocalView.current
    // Keyed on Unit: a transient swipe-to-reveal underneath must not bring an open overlay's bars back.
    DisposableEffect(Unit) {
        if (hidden.any) {
            view.hideOverlaySystemBars(hidden)
        }
        onDispose { }
    }
}

private fun View.hideOverlaySystemBars(hidden: HiddenSystemBars) {
    val dialogWindow = (parent as? DialogWindowProvider)?.window
    if (dialogWindow != null) {
        dialogWindow.hideSystemBars(hidden)
    } else {
        hidePopupSystemBars(hidden)
    }
}

/**
 * [WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE] leaves this window out of the system-bar policy,
 * so the insets request lands before the window gets to decide what the bars do.
 */
private fun Window.hideSystemBars(hidden: HiddenSystemBars) {
    val wasFocusable =
        (attributes.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) == 0
    setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
    )
    WindowCompat.setDecorFitsSystemWindows(this, false)
    WindowCompat
        .getInsetsController(this, decorView)
        .hideBars(hidden)
    if (wasFocusable) {
        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }
}

/**
 * A Compose [androidx.compose.ui.window.Popup] goes straight to the [WindowManager], with no
 * [Window] of its own.
 */
private fun View.hidePopupSystemBars(hidden: HiddenSystemBars) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        hidePopupSystemBarsFromView(hidden)
    } else {
        @Suppress("DEPRECATION")
        rootView.systemUiVisibility = rootView.systemUiVisibility or hidden.legacySystemUiFlags()
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun View.hidePopupSystemBarsFromView(hidden: HiddenSystemBars) {
    val controller = windowInsetsController
        ?: return
    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(hidden.insetTypes())
}

private fun WindowInsetsControllerCompat.hideBars(hidden: HiddenSystemBars) {
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    if (hidden.statusBar) {
        hide(WindowInsetsCompat.Type.statusBars())
    }
    if (hidden.navigationBar) {
        hide(WindowInsetsCompat.Type.navigationBars())
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun HiddenSystemBars.insetTypes(): Int {
    var types = 0
    if (statusBar) {
        types = types or AndroidWindowInsets.Type.statusBars()
    }
    if (navigationBar) {
        types = types or AndroidWindowInsets.Type.navigationBars()
    }
    return types
}

@Suppress("DEPRECATION")
private fun HiddenSystemBars.legacySystemUiFlags(): Int {
    var flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (statusBar) {
        flags = flags or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
    if (navigationBar) {
        flags = flags or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    return flags
}
