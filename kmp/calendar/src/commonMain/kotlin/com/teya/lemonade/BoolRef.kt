package com.teya.lemonade

/**
 * Mutable flag that survives recomposition without triggering it, unlike
 * [androidx.compose.runtime.MutableState].
 */
internal class BoolRef(var value: Boolean)
