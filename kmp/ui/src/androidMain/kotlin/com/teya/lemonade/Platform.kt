package com.teya.lemonade

import android.animation.ValueAnimator
import android.os.Build

// The compat minimum: androidx.core emulates the IME inset type from API 23. Where a window never
// reports it the value stays 0 and clearFocusOnKeyboardDismiss degrades to a no-op.
internal actual fun supportsImeInsets(): Boolean = Build.VERSION.SDK_INT >= 23

// "Remove animations" sets the animator duration scale to 0. Compose honours that for finite
// animations, but an infinite one jumps straight to its end state, so callers need to know.
// The check needs API 26; below that the setting is not reported.
internal actual fun prefersReducedMotion(): Boolean {
    val canReport = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    return canReport && !ValueAnimator.areAnimatorsEnabled()
}
