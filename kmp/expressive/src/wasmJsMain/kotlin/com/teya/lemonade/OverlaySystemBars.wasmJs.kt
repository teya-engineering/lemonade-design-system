package com.teya.lemonade

import androidx.compose.runtime.Composable

@Composable
internal actual fun systemBarsMirror(forceHideNavigationBar: Boolean): @Composable () -> Unit = NoOpSystemBarsMirror
