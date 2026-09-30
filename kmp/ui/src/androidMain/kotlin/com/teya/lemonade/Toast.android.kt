package com.teya.lemonade

import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.delay
import android.app.Dialog as AndroidDialog
import android.graphics.Color as AndroidColor
import android.view.WindowInsets as AndroidWindowInsets

/** Lets the window's size and offset settle before animating, else the enter flicks. */
private const val SHOW_DELAY_MS = 100L

/** Room around the pill for its shadow, which the window would otherwise clip. */
private val SHADOW_ELEVATION = 8.dp

@Composable
internal actual fun PlatformToastHost(
    modifier: Modifier,
    toastState: LemonadeToastState,
    hideNavigationBar: Boolean,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) { content() }
    ToastOverlayWindow(
        toastState = toastState,
        hideNavigationBar = hideNavigationBar,
    )
}

/**
 * Renders toast overlays at the bottom of the screen, keeping the navigation bar hidden when
 * [hideNavigationBar].
 *
 * The toast draws in a window of its own, above any open modal. With [hideNavigationBar] that window
 * hides the navigation bar too, so a host that hides the bar keeps it hidden for as long as a toast is
 * up. Without it, the toast is the same as the common [LemonadeToastHost]'s.
 *
 * ## Usage
 * ```kotlin
 * LemonadeToastHost(hideNavigationBar = true) {
 *     // Your app content
 * }
 * ```
 *
 * @param hideNavigationBar whether the toast's window hides the navigation bar
 * @param modifier [Modifier] applied to the box wrapping [content]
 * @param content the app content the toasts are shown over
 */
@Composable
public fun LemonadeToastHost(
    hideNavigationBar: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CoreToastHost(
        modifier = modifier,
        hideNavigationBar = hideNavigationBar,
        content = content,
    )
}

/**
 * Draws the toast in its own [ToastWindow], above any open modal.
 *
 * It z-orders above an [androidx.compose.material3.ModalBottomSheet] or a
 * [androidx.compose.ui.window.Dialog]. `FLAG_NOT_FOCUSABLE` and `FLAG_NOT_TOUCH_MODAL` pass touches
 * outside the window through to the content beneath and never take input focus.
 *
 * Pass-through is bounded by the *window*, not the pill. The window spans the full width, so while a
 * toast is visible, taps in the horizontal band it occupies are swallowed even beside a short, narrow
 * pill — they don't reach the content behind. The band is the height of the toast at the bottom of the
 * screen and lasts only as long as the toast is on screen. Sizing the window to the pill instead would
 * restore that pass-through, but re-applies the platform's 320dp dialog width cap and stops a wrapped
 * label from ever filling the width.
 */
@Composable
private fun ToastOverlayWindow(
    toastState: LemonadeToastState,
    hideNavigationBar: Boolean,
) {
    val toast = toastState.currentToast

    // Outlive `currentToast` clearing so the exit animation can play before the window unmounts.
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

    ToastWindow(hideNavigationBar = hideNavigationBar) {
        val layoutDirection = LocalLayoutDirection.current
        val margins = rememberToastPadding(
            override = displayToast.paddingValues,
            layoutDirection = layoutDirection,
        )
        val startInset = margins.calculateStartPadding(layoutDirection)
        val endInset = margins.calculateEndPadding(layoutDirection)
        OffsetToastWindow(
            bottomMargin = margins.calculateBottomPadding(),
            hideNavigationBar = hideNavigationBar,
        )

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
                // `MATCH_PARENT` wide, see [ToastOverlayWindow].
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
 * Lifts the toast's window [bottomMargin] above the bottom of the screen, plus the navigation bar's
 * inset unless [hideNavigationBar].
 *
 * The lift is a window attribute, not padding, so the frame hugs the pill vertically and taps above and
 * below it fall through. The navigation-bar inset resolves to zero when the window already sits above
 * the bars and to the bar height on edge-to-edge screens, so the toast never lands under the navigation
 * bar. It reaches the window's content only once the window is shown, so the offset is set here rather
 * than with the rest of the window's layout.
 */
@Composable
private fun OffsetToastWindow(
    bottomMargin: Dp,
    hideNavigationBar: Boolean,
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val navigationBarInsets = WindowInsets.navigationBars
    val bottomOffsetPx = toastWindowBottomOffsetPx(
        bottomMarginPx = with(density) { bottomMargin.roundToPx() },
        hideNavigationBar = hideNavigationBar,
        navigationBarInsetPx = { navigationBarInsets.getBottom(density) },
    )
    DisposableEffect(bottomOffsetPx) {
        (view.parent as? DialogWindowProvider)?.window?.apply {
            attributes = attributes.apply { y = bottomOffsetPx }
        }
        onDispose { }
    }
}

/**
 * Shows [content] in a window built and laid out as a toast before it is shown.
 *
 * A window that is focusable for even one frame makes Android before 11 show the navigation bar again
 * on every window, and Compose's [androidx.compose.ui.window.Dialog] can only be adjusted after it is
 * shown. So this window is non-focusable, sized and placed from the start.
 */
@Composable
private fun ToastWindow(
    hideNavigationBar: Boolean,
    content: @Composable () -> Unit,
) {
    val hostView = LocalView.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val dialog = remember(hostView, hideNavigationBar) {
        ToastDialog(
            hostView = hostView,
            parentComposition = parentComposition,
            hideNavigationBar = hideNavigationBar,
            content = { currentContent() },
        )
    }
    DisposableEffect(dialog) {
        dialog.show()
        onDispose { dialog.dismissAndDispose() }
    }
}

private class ToastDialog(
    hostView: View,
    parentComposition: CompositionContext,
    hideNavigationBar: Boolean,
    content: @Composable () -> Unit,
) : AndroidDialog(hostView.context) {
    private val layout: ToastWindowLayout

    init {
        val window = checkNotNull(window)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        )
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        window.attributes = window.attributes.apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            // Compose drives the enter/exit; suppress the platform window animation so it doesn't
            // run on top of it.
            windowAnimations = 0
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        layout = ToastWindowLayout(
            window = window,
            parentComposition = parentComposition,
            content = content,
        )
        (window.decorView as? ViewGroup)?.disableClippingDownTo(layout)
        setContentView(layout)
        // After setContentView: from API 30 the window's insets controller lives on its decor view.
        if (hideNavigationBar) window.hideNavigationBar()
        window.decorView.apply {
            setViewTreeLifecycleOwner(hostView.findViewTreeLifecycleOwner())
            setViewTreeViewModelStoreOwner(hostView.findViewTreeViewModelStoreOwner())
            setViewTreeSavedStateRegistryOwner(hostView.findViewTreeSavedStateRegistryOwner())
        }
    }

    fun dismissAndDispose() {
        dismiss()
        layout.disposeComposition()
    }
}

/** Exposes its [window] the way Compose's own dialog layout does, so [OffsetToastWindow] finds it. */
private class ToastWindowLayout(
    override val window: Window,
    parentComposition: CompositionContext,
    private val content: @Composable () -> Unit,
) : AbstractComposeView(window.context),
    DialogWindowProvider {
    init {
        setParentCompositionContext(parentComposition)
        clipChildren = false
        // An elevation with an invisible outline makes the window manager allocate room for the
        // pill's shadow without drawing a shadow of its own.
        elevation = SHADOW_ELEVATION.value * resources.displayMetrics.density
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(
                view: View,
                outline: Outline,
            ) {
                outline.setRect(0, 0, view.width, view.height)
                outline.alpha = 0f
            }
        }
    }

    @Composable
    override fun Content() {
        content()
    }
}

private fun ViewGroup.disableClippingDownTo(layout: View) {
    clipChildren = false
    if (this === layout) return
    for (index in 0 until childCount) {
        (getChildAt(index) as? ViewGroup)?.disableClippingDownTo(layout)
    }
}

/**
 * Below API 30 the sticky immersive flags, which survive focus changes; from API 30 the insets controller.
 */
private fun Window.hideNavigationBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    } else {
        hideNavigationBarFromInsetsController()
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Window.hideNavigationBarFromInsetsController() {
    insetsController?.apply {
        systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(AndroidWindowInsets.Type.navigationBars())
    }
}
