package com.teya.lemonade.docs

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/**
 * Authoring entry point.
 *
 * The site ships as wasm; this target exists so pages can be written against Compose Hot Reload
 * instead of a webpack rebuild, and so the content model gets a JVM test compilation.
 */
fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Lemonade docs",
            state = rememberWindowState(width = 1280.dp, height = 900.dp),
        ) {
            DocsApp(router = DocRouter(initial = DocRoute.Home))
        }
    }
