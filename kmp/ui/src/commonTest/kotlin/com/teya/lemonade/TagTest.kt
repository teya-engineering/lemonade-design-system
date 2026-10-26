package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {
    @Test
    fun aHueStylesTheTagFromItsSolidPalette() {
        val solid = LemonadeLightThemedColors.violet
        assertEquals(
            TagColors(
                background = solid.background,
                border = solid.border,
                content = solid.onBackground,
            ),
            LemonadeLightThemedColors.tagColors(theme = ThemedHue.Violet),
        )
    }

    @Test
    fun aSubtleHueStylesTheTagFromItsSubtlePalette() {
        val subtle = LemonadeLightThemedColors.violet.subtle
        assertEquals(
            TagColors(
                background = subtle.background,
                border = subtle.border,
                content = subtle.onBackground,
            ),
            LemonadeLightThemedColors.tagColors(theme = ThemedHue.Violet.subtle),
        )
    }
}
