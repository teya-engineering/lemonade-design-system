package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ThemedColorsTest {
    @Test
    fun aVariantCanBeHeldAsOneValueAndStyledFrom() {
        // The reason subtle is nested rather than a sibling slot: either palette is a
        // ThemedColor, so a component picks one up front and reads the same slots off it.
        val solid: ThemedColor = LemonadeLightThemedColors.amber
        val subtle: ThemedColor = LemonadeLightThemedColors.amber.subtle

        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber600, solid.background)
        assertEquals(LemonadePrimitiveColors.Alpha.Amber.alpha100, subtle.background)
        assertNotEquals(solid.border, subtle.border)
    }

    @Test
    fun themedColoursCanBePassedAroundAsValues() {
        // Only compiles because every hue shares ThemedPrimaryColor; without it the list
        // infers as List<Any> and `it.background` does not resolve.
        val series: List<ThemedPrimaryColor> = listOf(
            LemonadeLightThemedColors.blue,
            LemonadeLightThemedColors.amber,
            LemonadeLightThemedColors.neutral,
        )
        assertEquals(3, series.map { it.background }.size)
        assertEquals(3, series.map { it.subtle.background }.size)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue600, series[0].background)
    }

    @Test
    fun solidBackgroundsStepDownInDarkAndBorderTracksThem() {
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue600, LemonadeLightThemedColors.blue.background)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue400, LemonadeDarkThemedColors.blue.background)
        assertEquals(LemonadeLightThemedColors.blue.background, LemonadeLightThemedColors.blue.border)
        assertEquals(LemonadeDarkThemedColors.blue.background, LemonadeDarkThemedColors.blue.border)
    }

    @Test
    fun subtlePalettesAreThemeInvariantForChromaticHues() {
        assertEquals(
            LemonadeLightThemedColors.blue.subtle.background,
            LemonadeDarkThemedColors.blue.subtle.background,
        )
        assertEquals(
            LemonadeLightThemedColors.blue.subtle.border,
            LemonadeDarkThemedColors.blue.subtle.border,
        )
    }

    @Test
    fun theLabelOnASubtleFillIsTheHuesOwnContent() {
        // A pale fill is light enough for hue-coloured text, so subtle reuses content
        // rather than needing a neutral. Same value, different role.
        assertEquals(LemonadeLightThemedColors.blue.content, LemonadeLightThemedColors.blue.subtle.onBackground)
        assertEquals(LemonadeDarkThemedColors.teal.content, LemonadeDarkThemedColors.teal.subtle.onBackground)
    }

    @Test
    fun contentOnColorIsContentWithModesSwapped() {
        assertEquals(LemonadeLightThemedColors.blue.content, LemonadeDarkThemedColors.blue.contentOnColor)
        assertEquals(LemonadeDarkThemedColors.blue.content, LemonadeLightThemedColors.blue.contentOnColor)
    }

    @Test
    fun labelOnTheSolidFillUsesTheHuesOwnExtremeWhereContrastAllows() {
        // Dark backgrounds are the pale <hue>/400, so the hue's darkest step clears AA
        // for all seventeen and every label stays in the family.
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue950, LemonadeDarkThemedColors.blue.onBackground)
        assertEquals(LemonadePrimitiveColors.Solid.Red.red950, LemonadeDarkThemedColors.red.onBackground)

        // Light backgrounds are the saturated <hue>/600, so which end works varies:
        // light end for the cool hues, dark end for the warm.
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue50, LemonadeLightThemedColors.blue.onBackground)
        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber950, LemonadeLightThemedColors.amber.onBackground)
    }

    @Test
    fun labelFallsBackToWhiteOrInkWhereTheFamilyCannotReachAA() {
        // Seven hues sit too near mid-luminance for either end of their own ramp to reach
        // 4.5:1 on <hue>/600 — best in-family is 3.71-4.34. They fall back, staying on the
        // side their family came closest to.
        assertEquals(LemonadePrimitiveColors.Solid.White.white950, LemonadeLightThemedColors.red.onBackground)
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeLightThemedColors.cyan.onBackground)
        // A light-mode concern only; dark stays in-family throughout.
        assertEquals(LemonadePrimitiveColors.Solid.Cyan.cyan950, LemonadeDarkThemedColors.cyan.onBackground)
    }

    @Test
    fun neutralFollowsTheMirrorRuleRatherThanTheChromaticStepMap() {
        // Neutral is the one themed hue that does not follow the chromatic step map:
        // light draws from neutral/alpha, dark from white, at the same rung.
        assertEquals(
            LemonadePrimitiveColors.Alpha.Neutral.alpha100,
            LemonadeLightThemedColors.neutral.subtle.background,
        )
        assertEquals(
            LemonadePrimitiveColors.Solid.White.white100,
            LemonadeDarkThemedColors.neutral.subtle.background,
        )
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeLightThemedColors.neutral.content)
        assertEquals(LemonadePrimitiveColors.Solid.White.white900, LemonadeDarkThemedColors.neutral.content)

        // So its subtle palette is NOT theme-invariant, unlike every chromatic hue's.
        assertNotEquals(
            LemonadeLightThemedColors.neutral.subtle.background,
            LemonadeDarkThemedColors.neutral.subtle.background,
        )
    }

    @Test
    fun hyphenatedHuesResolveOnTheirOwnAccessor() {
        assertEquals(
            LemonadePrimitiveColors.Solid.GreenLime.greenLime600,
            LemonadeLightThemedColors.greenLime.background,
        )
        assertEquals(
            LemonadePrimitiveColors.Solid.YellowLime.yellowLime400,
            LemonadeDarkThemedColors.yellowLime.background,
        )
    }
}
