package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.LemonadeTypography
import org.jetbrains.compose.resources.Font

/**
 * Figtree font family every Lemonade text style renders with.
 *
 * See [Figtree font](https://fonts.google.com/specimen/Figtree)
 */
public val lemonadeFontFamily: FontFamily
    @Composable get() {
        val regular = Font(
            resource = LemonadeRes.font.Figtree_Regular,
            weight = FontWeight.Normal,
        )
        val medium = Font(
            resource = LemonadeRes.font.Figtree_Medium,
            weight = FontWeight.Medium,
        )
        val semiBold = Font(
            resource = LemonadeRes.font.Figtree_SemiBold,
            weight = FontWeight.SemiBold,
        )
        return remember(regular, medium, semiBold) {
            FontFamily(regular, medium, semiBold)
        }
    }

/**
 * Converts a [LemonadeTextStyle] to a Compose [TextStyle] using an already-resolved [fontFamily].
 */
@InternalLemonadeApi
public fun LemonadeTextStyle.toTextStyle(fontFamily: FontFamily): TextStyle {
    val spacing = letterSpacing
        ?: 0f
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = when (fontWeight) {
            400 -> FontWeight.Normal
            500 -> FontWeight.Medium
            600 -> FontWeight.SemiBold
            700 -> FontWeight.Bold
            else -> FontWeight.Normal
        },
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = spacing.sp,
        fontFeatureSettings = "psum",
    )
}

/**
 * Converts a [LemonadeTextStyle] to a Compose [TextStyle].
 */
@OptIn(InternalLemonadeApi::class)
public val LemonadeTextStyle.textStyle: TextStyle
    @Composable get() {
        val fontFamily = lemonadeFontFamily
        return remember(this, fontFamily) {
            toTextStyle(fontFamily)
        }
    }

/**
 * Converts [LemonadeTypography] to a Compose [TextStyle].
 */
public val LemonadeTypography.textStyle: TextStyle
    @Composable get() = style.textStyle
