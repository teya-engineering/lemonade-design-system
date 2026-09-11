package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolContainerTest {
    @Test
    fun aHueStylesTheContainerFromItsSolidPalette() {
        val solid = LemonadeLightThemedColors.violet
        assertEquals(
            SymbolContainerColors(
                background = solid.background,
                border = solid.border,
                content = solid.onBackground,
            ),
            LemonadeLightThemedColors.symbolContainerColors(theme = ThemedHue.Violet),
        )
    }

    @Test
    fun aSubtleHueStylesTheContainerFromItsSubtlePalette() {
        val subtle = LemonadeLightThemedColors.violet.subtle
        assertEquals(
            SymbolContainerColors(
                background = subtle.background,
                border = subtle.border,
                content = subtle.onBackground,
            ),
            LemonadeLightThemedColors.symbolContainerColors(theme = ThemedHue.Violet.subtle),
        )
    }
}
