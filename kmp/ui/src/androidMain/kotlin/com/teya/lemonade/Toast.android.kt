package com.teya.lemonade

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kotlinx.coroutines.delay
import android.graphics.Color as AndroidColor

/** Lets the window's flags, size and gravity settle before animating, else the enter flicks. */
private const val SHOW_DELAY_MS = 100L

@Composable
internal actual fun PlatformToastHost(
    modifier: Modifier,
    toastState: LemonadeToastState,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) { content() }
    ToastOverlayWindow(toastState = toastState)
}

/**
 * Draws the toast in its own [Dialog] window, above any open modal.
 *
 * It z-orders above an [androidx.compose.material3.ModalBottomSheet] or another [Dialog].
 * `FLAG_NOT_FOCUSABLE` (implies `FLAG_NOT_TOUCH_MODAL`) passes touches outside the window through to
 * the content beneath and never takes input focus.
 *
 * Pass-through is bounded by the *window*, not the pill. The window spans the full width (see
 * [ConfigureToastWindow]), so while a toast is visible, taps in the horizontal band it occupies are
 * swallowed even beside a short, narrow pill — they don't reach the content behind. The band is the
 * height of the toast at the bottom of the screen and lasts only as long as the toast is on screen.
 * Sizing the window to the pill instead would restore that pass-through, but re-applies the platform's
 * 320dp dialog width cap and stops a wrapped label from ever filling the width.
 */
@Composable
private fun ToastOverlayWindow(toastState: LemonadeToastState) {
    val toast = toastState.currentToast

    // Outlive `currentToast` clearing so the exit animation can play before the Dialog unmounts.
    var lastToast by remember { mutableStateOf<ToastData?>(null) }
    if (toast != null) lastToast = toast

    val animState = remember { MutableTransitionState(false) }
    val animationSettled by remember {
        derivedStateOf { !animState.currentState && !animState.targetState }
    }

    LaunchedEffect(toast, animationSettled) {
        if (toast == null && animationSettled) lastToast = null
    }

    val displayToast = lastToast
        ?: return

    Dialog(
        onDismissRequest = { toastState.dismiss() },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val margins = rememberToastPadding(
            override = displayToast.paddingValues,
            layoutDirection = layoutDirection,
        )
        val startInset = margins.calculateStartPadding(layoutDirection)
        val endInset = margins.calculateEndPadding(layoutDirection)
        ConfigureToastWindow(bottomInset = margins.calculateBottomPadding())

        LaunchedEffect(toast) {
            if (toast != null) {
                delay(SHOW_DELAY_MS)
                animState.targetState = true
            } else {
                animState.targetState = false
            }
        }

        // Keep the toast always composed so the window measures one fixed size. Enter and exit are a
        // draw-only alpha and vertical translation, which never re-measure the window.
        var toastHeightPx by remember { mutableIntStateOf(0) }
        val transition = updateTransition(
            transitionState = animState,
            label = "toast",
        )
        val alpha by transition.animateFloat(label = "alpha") { visible -> if (visible) 1f else 0f }
        val translationY by transition.animateFloat(
            transitionSpec = {
                spring(
                    dampingRatio = 0.8f,
                    stiffness = Spring.StiffnessMediumLow,
                )
            },
            label = "translationY",
        ) { visible -> if (visible) 0f else toastHeightPx.toFloat() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // The horizontal margins go on the content: the window itself has to stay
                // `MATCH_PARENT` wide, see [ConfigureToastWindow].
                .padding(
                    start = startInset,
                    end = endInset,
                ).onSizeChanged { size -> toastHeightPx = size.height }
                .graphicsLayer {
                    this.alpha = alpha
                    this.translationY = translationY
                },
            contentAlignment = Alignment.Center,
        ) {
            SwipeableToast(
                toast = displayToast,
                onDismiss = { toastState.dismiss() },
            )
        }
    }
}

/**
 * Spans the dialog window across the screen and lifts it [bottomInset] above the bottom.
 *
 * The lift is a window attribute plus the navigation-bar inset, not padding, so the frame hugs the
 * pill vertically and taps above and below it fall through (but not beside it — see
 * [ToastOverlayWindow]). `MATCH_PARENT` is deliberate: `WRAP_CONTENT` re-applies the platform's 320dp
 * dialog width cap that `usePlatformDefaultWidth = false` exists to remove, which caps the toast well
 * short of the screen. The navigation-bar inset resolves to zero when the window already sits above
 * the bars and to the bar height on edge-to-edge screens, so the toast never lands under the
 * navigation bar.
 */
@Composable
private fun ConfigureToastWindow(bottomInset: Dp) {
    val view = LocalView.current
    val density = LocalDensity.current
    val navigationBarInsetPx = WindowInsets.navigationBars.getBottom(density)
    val bottomInsetPx = with(density) { bottomInset.roundToPx() } + navigationBarInsetPx
    DisposableEffect(bottomInsetPx) {
        (view.parent as? DialogWindowProvider)?.window?.apply {
            setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            )
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            attributes = attributes.apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                y = bottomInsetPx
                // Compose drives the enter/exit; suppress the platform window animation so it doesn't
                // run on top of it.
                windowAnimations = 0
            }
        }
        onDispose { }
    }
}
