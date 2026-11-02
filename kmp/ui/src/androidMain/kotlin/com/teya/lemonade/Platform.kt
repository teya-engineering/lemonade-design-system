package com.teya.lemonade

import android.os.Build

// The compat minimum: androidx.core emulates the IME inset type from API 23. Where a window never
// reports it the value stays 0 and clearFocusOnKeyboardDismiss degrades to a no-op.
internal actual fun supportsImeInsets(): Boolean = Build.VERSION.SDK_INT >= 23

// Compose already scales its animations by the system's animator duration scale, which is 0 when
// "Remove animations" is on.
internal actual fun prefersReducedMotion(): Boolean = false
