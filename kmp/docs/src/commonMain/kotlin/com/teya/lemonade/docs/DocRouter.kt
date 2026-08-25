package com.teya.lemonade.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Holds the page currently on screen.
 *
 * The router itself knows nothing about the browser: the wasm entry point installs [onNavigate] to
 * push onto the history, and calls [syncFromBrowser] when the user goes back or forward. That keeps
 * the whole navigation model runnable on the desktop target, where pages are authored.
 *
 * Routes are addressed as fragments (`#/standards/theming`), not paths. Compose resources resolve
 * against `window.location.pathname`, so a path-based deep link would make every icon and font
 * request 404 on any route but the root. Switching to paths requires `configureWebResources` first.
 */
internal class DocRouter(initial: DocRoute) {
    var current: DocRoute by mutableStateOf(initial)
        private set

    var onNavigate: ((DocRoute) -> Unit)? = null

    fun navigate(route: DocRoute) {
        if (route == current) {
            return
        }
        current = route
        onNavigate?.invoke(route)
    }

    fun syncFromBrowser(route: DocRoute) {
        current = route
    }
}
