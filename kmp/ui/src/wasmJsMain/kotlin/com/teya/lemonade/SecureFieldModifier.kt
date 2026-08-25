package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// The browser has no OS-level screenshot/recording protection equivalent.
@Composable
internal actual fun Modifier.secureFieldModifier(enabled: Boolean): Modifier = this
