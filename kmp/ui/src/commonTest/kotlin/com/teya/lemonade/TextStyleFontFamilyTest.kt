package com.teya.lemonade

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.teya.lemonade.core.LemonadeTextStyle
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalLemonadeApi::class)
class TextStyleFontFamilyTest {
    @Test
    fun theFamilyIsTheOnlyThingTheCallerGetsToChange() {
        val style = LemonadeTextStyle(
            fontSize = 16f,
            lineHeight = 24f,
            fontWeight = 600,
            letterSpacing = -0.32f,
        )

        val figtree = style.toTextStyle(FontFamily.Default)
        val serif = style.toTextStyle(FontFamily.Serif)

        assertEquals(FontFamily.Serif, serif.fontFamily)
        assertEquals(figtree.fontSize, serif.fontSize)
        assertEquals(figtree.lineHeight, serif.lineHeight)
        assertEquals(figtree.fontWeight, serif.fontWeight)
        assertEquals(figtree.letterSpacing, serif.letterSpacing)
    }

    @Test
    fun theTokenWeightsMapOntoComposeWeights() {
        val weights = listOf(
            400 to FontWeight.Normal,
            500 to FontWeight.Medium,
            600 to FontWeight.SemiBold,
            700 to FontWeight.Bold,
        )

        for ((token, expected) in weights) {
            val style = LemonadeTextStyle(fontSize = 16f, lineHeight = 24f, fontWeight = token)

            assertEquals(expected, style.toTextStyle(FontFamily.Serif).fontWeight)
        }
    }

    @Test
    fun anAbsentLetterSpacingBecomesZeroRatherThanUnspecified() {
        val style = LemonadeTextStyle(fontSize = 16f, lineHeight = 24f, fontWeight = 400)

        assertEquals(0f.sp, style.toTextStyle(FontFamily.Serif).letterSpacing)
    }
}
