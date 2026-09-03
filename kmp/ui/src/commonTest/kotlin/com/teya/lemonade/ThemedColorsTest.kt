package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ThemedColorsTest {
    @Test
    fun solidBackgroundsStepDownInDark() {
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue600, LemonadeLightThemedColors.background.bgBlue)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue400, LemonadeDarkThemedColors.background.bgBlue)
        assertNotEquals(LemonadeLightThemedColors.background.bgBlue, LemonadeDarkThemedColors.background.bgBlue)
    }

    @Test
    fun subtleBackgroundsAreThemeInvariant() {
        assertEquals(
            LemonadeLightThemedColors.background.bgBlueSubtle,
            LemonadeDarkThemedColors.background.bgBlueSubtle,
        )
    }

    @Test
    fun borderMatchesBackgroundStepForEveryTheme() {
        assertEquals(LemonadeLightThemedColors.background.bgBlue, LemonadeLightThemedColors.border.borderBlue)
        assertEquals(LemonadeDarkThemedColors.background.bgBlue, LemonadeDarkThemedColors.border.borderBlue)
    }

    @Test
    fun contentOnColorIsContentWithModesSwapped() {
        assertEquals(
            LemonadeLightThemedColors.content.contentBlue,
            LemonadeDarkThemedColors.content.contentBlueOnColor,
        )
        assertEquals(
            LemonadeDarkThemedColors.content.contentBlue,
            LemonadeLightThemedColors.content.contentBlueOnColor,
        )
    }

    @Test
    fun labelColourIsChosenPerHue() {
        // Cool hues take a white label in light; warm hues take ink.
        assertEquals(LemonadePrimitiveColors.Solid.White.white950, LemonadeLightThemedColors.content.contentOnBlue)
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeLightThemedColors.content.contentOnAmber)
        // Dark backgrounds are light enough that ink wins for every hue.
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeDarkThemedColors.content.contentOnBlue)
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeDarkThemedColors.content.contentOnAmber)
    }
}
