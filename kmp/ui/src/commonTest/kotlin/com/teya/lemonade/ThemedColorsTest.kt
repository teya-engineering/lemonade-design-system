package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ThemedColorsTest {
    @Test
    fun solidBackgroundsStepDownInDark() {
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue600, LemonadeLightThemedColors.blue.background)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue400, LemonadeDarkThemedColors.blue.background)
        assertNotEquals(LemonadeLightThemedColors.blue.background, LemonadeDarkThemedColors.blue.background)
    }

    @Test
    fun subtleBackgroundsAreThemeInvariant() {
        assertEquals(
            LemonadeLightThemedColors.blue.backgroundSubtle,
            LemonadeDarkThemedColors.blue.backgroundSubtle,
        )
    }

    @Test
    fun borderMatchesBackgroundStepForEveryTheme() {
        assertEquals(LemonadeLightThemedColors.blue.background, LemonadeLightThemedColors.blue.border)
        assertEquals(LemonadeDarkThemedColors.blue.background, LemonadeDarkThemedColors.blue.border)
    }

    @Test
    fun contentOnColorIsContentWithModesSwapped() {
        assertEquals(LemonadeLightThemedColors.blue.content, LemonadeDarkThemedColors.blue.contentOnColor)
        assertEquals(LemonadeDarkThemedColors.blue.content, LemonadeLightThemedColors.blue.contentOnColor)
    }

    @Test
    fun labelColourIsChosenPerHue() {
        // Cool hues take a white label in light; warm hues take ink.
        assertEquals(LemonadePrimitiveColors.Solid.White.white950, LemonadeLightThemedColors.blue.onBackground)
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeLightThemedColors.amber.onBackground)
        // Dark backgrounds are light enough that ink wins for every hue.
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeDarkThemedColors.blue.onBackground)
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeDarkThemedColors.amber.onBackground)
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
