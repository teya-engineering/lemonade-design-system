package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect

// The system setting is not observable, and it changes in Settings, outside the app, so it is
// read again each time the screen resumes.
@Composable
internal fun rememberPrefersReducedMotion(): Boolean {
    var reducedMotion by remember { mutableStateOf(value = prefersReducedMotion()) }
    LifecycleResumeEffect(key1 = Unit) {
        reducedMotion = prefersReducedMotion()
        onPauseOrDispose {}
    }
    return reducedMotion
}
