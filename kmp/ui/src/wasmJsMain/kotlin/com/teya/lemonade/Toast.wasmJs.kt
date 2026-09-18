package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun PlatformToastHost(
    modifier: Modifier,
    toastState: LemonadeToastState,
    content: @Composable () -> Unit,
) {
    InlineToastHost(modifier = modifier, toastState = toastState, content = content)
}
