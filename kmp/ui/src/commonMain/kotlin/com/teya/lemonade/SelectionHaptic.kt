package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Plays the selection haptic whenever [selected] transitions.
 *
 * Firing on the value change rather than on click dispatch means a programmatic selection
 * change buzzes too, and a click that does not change the selection stays silent. The first
 * emission seeds the initial state without buzzing.
 *
 * Shared by every component that presents a selectable surface, so the feel of "this was
 * selected" is defined in one place.
 */
@Composable
internal fun SelectionHapticEffect(selected: Boolean) {
    val haptic = LocalHapticFeedback.current
    // A plain holder rather than snapshot state: only the effect below ever reads it, so there
    // is nothing to observe and no reason to route the write through the snapshot system.
    val hasEmittedInitial = remember { booleanArrayOf(false) }

    LaunchedEffect(selected) {
        if (hasEmittedInitial[0]) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        } else {
            hasEmittedInitial[0] = true
        }
    }
}
