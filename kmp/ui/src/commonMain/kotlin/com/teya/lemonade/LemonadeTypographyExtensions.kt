package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
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
 * The font family every Lemonade text style resolves through.
 *
 * Defaults to [lemonadeFontFamily] (Figtree). Provide another to render the design system in a
 * different typeface — the type scale, weights and line heights are unchanged, only the faces they
 * are drawn with:
 *
 * ```kotlin
 * LemonadeTheme(fontFamily = myBrandFontFamily) {
 *     // every Lemonade component below draws in the brand face
 * }
 * ```
 *
 * The tokens carry metrics only — size, line height, weight, letter spacing — and never a family,
 * so swapping this changes the faces without touching the scale.
 *
 * Note that the design system's line heights were drawn against Figtree's metrics. A face with a
 * very different ascender/descender ratio will sit differently inside them.
 */
public val LocalFontFamily: ProvidableCompositionLocal<FontFamily?> =
    staticCompositionLocalOf { null }

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
        val fontFamily = LocalFontFamily.current
            ?: lemonadeFontFamily
        return remember(this, fontFamily) {
            toTextStyle(fontFamily)
        }
    }

/**
 * Converts [LemonadeTypography] to a Compose [TextStyle].
 */
public val LemonadeTypography.textStyle: TextStyle
    @Composable get() = style.textStyle
