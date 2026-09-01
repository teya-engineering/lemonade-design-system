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
 * This font family is based on the Figtree font, which is a modern sans-serif typeface.
 * It is used throughout the app for various text styles, providing a clean and readable
 * typography that aligns with the Lemonade design system.
 *
 * See [Lemonade  typography](https://www.figma.com/design/mmSKfenwtw1xujWwXvs9wJ/Lemonade-DS---Foundations?node-id=207-17406&t=OWFace9zKbJJOo1E-4)
 *
 * See [Figtree font](https://fonts.google.com/specimen/Figtree)
 */
public val lemonadeFontFamily: FontFamily
    @Composable get() {
        val regular = Font(LemonadeRes.font.Figtree_Regular, FontWeight.Normal)
        val medium = Font(LemonadeRes.font.Figtree_Medium, FontWeight.Medium)
        val semiBold = Font(LemonadeRes.font.Figtree_SemiBold, FontWeight.SemiBold)
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
 * Convenience extension to convert a [LemonadeTypography] enum value directly to a Compose [TextStyle].
 */
public val LemonadeTypography.textStyle: TextStyle
    @Composable get() = style.textStyle
