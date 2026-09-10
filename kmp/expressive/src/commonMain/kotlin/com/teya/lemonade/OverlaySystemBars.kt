package com.teya.lemonade

import androidx.compose.runtime.Composable

/** The mirror returned by platforms that have no per-window system bars. */
internal val NoOpSystemBarsMirror: @Composable () -> Unit = {}

/**
 * Mirrors the system bars the host window hides onto an overlay's own window.
 *
 * Call it from the composition hosting the overlay, then invoke the returned composable as the first
 * thing inside the overlay's content. Bars are only ever mirrored hidden, never shown.
 *
 * Inert below API 30, where the platform drops the host's hide flags on every window focus change:
 * there an immersive host has to re-assert them from `onWindowFocusChanged` instead.
 *
 * @param forceHideNavigationBar hide the navigation bar even when the host window shows it
 */
@Composable
internal expect fun systemBarsMirror(forceHideNavigationBar: Boolean = false): @Composable () -> Unit
