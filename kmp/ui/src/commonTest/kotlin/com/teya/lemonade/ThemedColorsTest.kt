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
    fun themedColoursCanBePassedAroundAsValues() {
        // The point of grouping by hue: one colour is a value, so a chart series or a
        // per-role mapping can hold them in a collection and read slots off them. This
        // only compiles because every group shares the ThemedColor supertype — without
        // it the list infers as List<Any> and `it.background` does not resolve.
        val series: List<ThemedColor> = listOf(
            LemonadeLightThemedColors.blue,
            LemonadeLightThemedColors.amber,
            LemonadeLightThemedColors.neutral,
        )
        val fills = series.map { it.background }

        assertEquals(3, fills.size)
        assertEquals(LemonadePrimitiveColors.Solid.Blue.blue600, fills[0])
        assertEquals(LemonadePrimitiveColors.Solid.Amber.amber600, fills[1])
        assertEquals(LemonadePrimitiveColors.Solid.Neutral.neutral600, fills[2])
    }

    @Test
    fun aThemedColourSuppliesEverySlotThroughTheSharedType() {
        // A component can take a ThemedColor and style itself entirely from it.
        fun style(colour: ThemedColor) =
            listOf(
                colour.background,
                colour.backgroundSubtle,
                colour.border,
                colour.borderSubtle,
                colour.content,
                colour.contentOnColor,
                colour.onBackground,
            )

        assertEquals(7, style(LemonadeLightThemedColors.violet).size)
        assertEquals(7, style(LemonadeDarkThemedColors.greenLime).size)
    }

    @Test
    fun neutralFollowsTheMirrorRuleRatherThanTheChromaticStepMap() {
        // Neutral is the one themed hue that does not follow the chromatic step map:
        // light draws from neutral/alpha, dark from white, at the same rung.
        assertEquals(
            LemonadePrimitiveColors.Alpha.Neutral.alpha100,
            LemonadeLightThemedColors.neutral.backgroundSubtle,
        )
        assertEquals(
            LemonadePrimitiveColors.Solid.White.white100,
            LemonadeDarkThemedColors.neutral.backgroundSubtle,
        )
        assertEquals(LemonadePrimitiveColors.Alpha.Neutral.alpha900, LemonadeLightThemedColors.neutral.content)
        assertEquals(LemonadePrimitiveColors.Solid.White.white900, LemonadeDarkThemedColors.neutral.content)

        // Its subtle slots are therefore NOT theme-invariant, unlike every chromatic hue's.
        assertNotEquals(
            LemonadeLightThemedColors.neutral.backgroundSubtle,
            LemonadeDarkThemedColors.neutral.backgroundSubtle,
        )
        assertEquals(
            LemonadeLightThemedColors.blue.backgroundSubtle,
            LemonadeDarkThemedColors.blue.backgroundSubtle,
        )
    }

    @Test
    fun neutralStillTracksBackgroundWithItsBorder() {
        // The one chromatic rule neutral does keep.
        assertEquals(LemonadeLightThemedColors.neutral.background, LemonadeLightThemedColors.neutral.border)
        assertEquals(LemonadeDarkThemedColors.neutral.background, LemonadeDarkThemedColors.neutral.border)
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
