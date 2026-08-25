package com.teya.lemonade.docs

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val router = DocRouter(initial = DocRoute.parse(fragment = window.location.hash))

    router.onNavigate = { route ->
        window.location.hash = route.path
    }
    window.addEventListener(type = "hashchange") {
        router.syncFromBrowser(route = DocRoute.parse(fragment = window.location.hash))
    }

    ComposeViewport(document.body!!) {
        DocsApp(router = router)
    }
    document.getElementById("splash")?.remove()
}
