package com.teya.lemonade

import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

internal actual fun supportsImeInsets(): Boolean = true

internal actual fun prefersReducedMotion(): Boolean = UIAccessibilityIsReduceMotionEnabled()
