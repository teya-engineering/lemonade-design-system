package com.teya.lemonade.docs.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.teya.lemonade.LemonadeExpressiveTheme
import com.teya.lemonade.LemonadeTheme

internal class DocStyleHandler(
    initialStyle: DocStyle = DocStyle.Default,
    initialVariant: DocThemeVariant = DocThemeVariant.Default,
) {
    var currentStyle: DocStyle by mutableStateOf(initialStyle)
    var currentVariant: DocThemeVariant by mutableStateOf(initialVariant)
}

internal val LocalDocStyleHandler = staticCompositionLocalOf {
    DocStyleHandler()
}

@Composable
internal fun rememberDocStyleHandler(
    initialStyle: DocStyle = DocStyle.Default,
    initialVariant: DocThemeVariant = DocThemeVariant.Default,
): DocStyleHandler = remember { DocStyleHandler(initialStyle, initialVariant) }

@Composable
internal fun DocStyledTheme(
    handler: DocStyleHandler = rememberDocStyleHandler(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDocStyleHandler provides handler) {
        val colors = handler.currentStyle.resolveColors()
        when (handler.currentVariant) {
            DocThemeVariant.Standard -> LemonadeTheme(colors = colors) {
                content()
            }
            DocThemeVariant.Expressive -> LemonadeExpressiveTheme(colors = colors) {
                content()
            }
        }
    }
}
