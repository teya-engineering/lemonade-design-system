@file:OptIn(InternalResourceApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.teya.lemonade

import android.content.Context
import android.graphics.Typeface
import com.teya.lemonade.core.LemonadeTextStyle
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.getResourceItemByEnvironment
import org.jetbrains.compose.resources.getSystemResourceEnvironment

/**
 * Returns the Android [Typeface] matching this text style's font weight.
 *
 * Typefaces are cached after first load. Use this for native SDK integrations that need an
 * [android.graphics.Typeface] outside a Compose context; in Compose, prefer [lemonadeFontFamily] and
 * [textStyle].
 */
public fun LemonadeTextStyle.androidTypeface(context: Context): Typeface {
    val fontResource = fontWeight.toFontResource()
    val environment = getSystemResourceEnvironment()
    val resourceItem = fontResource.getResourceItemByEnvironment(environment)
    return Typeface.createFromAsset(context.assets, resourceItem.path)
}

private fun Int.toFontResource(): FontResource =
    when (this) {
        500 -> LemonadeRes.font.Figtree_Medium
        600, 700 -> LemonadeRes.font.Figtree_SemiBold
        else -> LemonadeRes.font.Figtree_Regular
    }
