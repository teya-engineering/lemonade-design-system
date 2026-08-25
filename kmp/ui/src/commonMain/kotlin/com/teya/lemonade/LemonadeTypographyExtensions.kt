package com.teya.lemonade

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
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
 *
 * See [Lemonade  typography](https://www.figma.com/design/mmSKfenwtw1xujWwXvs9wJ/Lemonade-DS---Foundations?node-id=207-17406&t=OWFace9zKbJJOo1E-4)
 *
 * See [Figtree font](https://fonts.google.com/specimen/Figtree)
 */
public val lemonadeFontFamily: FontFamily
    @Composable get() {
        return FontFamily(
            Font(LemonadeRes.font.Figtree_Regular, FontWeight.Normal),
            Font(LemonadeRes.font.Figtree_Medium, FontWeight.Medium),
            Font(LemonadeRes.font.Figtree_SemiBold, FontWeight.SemiBold),
        )
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
 * Converts a [LemonadeTextStyle] to a Compose [TextStyle].
 */
public val LemonadeTextStyle.textStyle: TextStyle
    @Composable get() {
        val spacing = letterSpacing
            ?: 0f
        return TextStyle(
            fontFamily = LocalFontFamily.current
                ?: lemonadeFontFamily,
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
 * Convenience extension to convert a [LemonadeTypography] enum value directly to a Compose [TextStyle].
 */
public val LemonadeTypography.textStyle: TextStyle
    @Composable get() = style.textStyle
