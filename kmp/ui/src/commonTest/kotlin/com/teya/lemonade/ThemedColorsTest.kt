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
    fun contentInverseIsContentWithModesSwapped() {
        // For a surface whose brightness is inverted relative to the theme: a dark one in
        // light mode, a light one in dark. `content` is tuned for the default surface and
        // drops to 2.53:1 on an inverted one.
        assertEquals(LemonadeLightThemedColors.blue.content, LemonadeDarkThemedColors.blue.contentInverse)
        assertEquals(LemonadeDarkThemedColors.blue.content, LemonadeLightThemedColors.blue.contentInverse)
    }

    @Test
    fun backgroundHighGoesDeeperInLightAndLighterInDark() {
        // "High" means further from the page, so the direction flips with the theme.
        // A 900 fill in dark would be invisible: the dark page is neutral/900 itself.
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue900, LemonadeLightThemedColors.blue.backgroundHigh)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue200, LemonadeDarkThemedColors.blue.backgroundHigh)
        assertNotEquals(LemonadeLightThemedColors.blue.background, LemonadeLightThemedColors.blue.backgroundHigh)
    }

    @Test
    fun yellowLimeTakesTheBrandStepForItsHighFillInDark() {
        // Sixteen hues use <hue>/200 for the dark high fill. yellow-lime reads washed out
        // there, so it uses 500 — the same step bg-brand uses — which still clears its
        // label comfortably at 9.91:1. A deliberate exception, not drift.
        assertEquals(
            LemonadePrimitiveColors.Solid.YellowLime.yellowLime500,
            LemonadeDarkThemedColors.yellowLime.backgroundHigh,
        )
        assertEquals(
            LemonadePrimitiveColors.Solid.Blue.blue200,
            LemonadeDarkThemedColors.blue.backgroundHigh,
        )
        // Light is unaffected — it follows the 900 rule like every other hue.
        assertEquals(
            LemonadePrimitiveColors.Solid.YellowLime.yellowLime900,
            LemonadeLightThemedColors.yellowLime.backgroundHigh,
        )
    }

    @Test
    fun backgroundHighNeedsItsOwnLabelBecauseOnBackgroundDoesNotCarry() {
        // onBackground is tuned for background. On a 900 fill it fails for the eight hues
        // whose label is itself dark — amber's is amber/950, which scores 1.66:1 there.
        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber950, LemonadeLightThemedColors.amber.onBackground)
        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber50, LemonadeLightThemedColors.amber.onBackgroundHigh)
        assertNotEquals(
            LemonadeLightThemedColors.amber.onBackground,
            LemonadeLightThemedColors.amber.onBackgroundHigh,
        )
        // In dark the high fill is pale, so its label is the hue's dark end.
        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber900, LemonadeDarkThemedColors.amber.onBackgroundHigh)
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
