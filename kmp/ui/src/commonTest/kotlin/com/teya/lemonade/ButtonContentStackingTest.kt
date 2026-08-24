package com.teya.lemonade

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ButtonContentStackingTest {
    @Test
    fun `a label with no icons never stacks, however tight the width`() {
        assertFalse(
            actual = shouldStackButtonContent(
                hasBoundedWidth = true,
                availableWidthPx = 10,
                iconsWidthPx = 0,
                labelMinIntrinsicWidthPx = 200,
            ),
        )
    }

    @Test
    fun `an unbounded width never stacks`() {
        assertFalse(
            actual = shouldStackButtonContent(
                hasBoundedWidth = false,
                availableWidthPx = Int.MAX_VALUE,
                iconsWidthPx = 40,
                labelMinIntrinsicWidthPx = 200,
            ),
        )
    }

    @Test
    fun `the label beside the icons stays a row while its longest word fits`() {
        assertFalse(
            actual = shouldStackButtonContent(
                hasBoundedWidth = true,
                availableWidthPx = 300,
                iconsWidthPx = 80,
                labelMinIntrinsicWidthPx = 200,
            ),
        )
    }

    @Test
    fun `the icons stack once the label's longest word no longer fits beside them`() {
        assertTrue(
            actual = shouldStackButtonContent(
                hasBoundedWidth = true,
                availableWidthPx = 300,
                iconsWidthPx = 120,
                labelMinIntrinsicWidthPx = 200,
            ),
        )
    }

    @Test
    fun `an exact fit stays a row`() {
        assertFalse(
            actual = shouldStackButtonContent(
                hasBoundedWidth = true,
                availableWidthPx = 280,
                iconsWidthPx = 80,
                labelMinIntrinsicWidthPx = 200,
            ),
        )
    }

    @Test
    fun `a text-tracked size is the base size at the default text size`() {
        val density = Density(density = 1f)
        assertEquals(
            expected = 20.dp,
            actual = density.textTrackedSize(base = 20.dp),
        )
    }

    @Test
    fun `a text-tracked size ignores the screen density`() {
        val density = Density(density = 3f)
        assertEquals(
            expected = 20.dp,
            actual = density.textTrackedSize(base = 20.dp),
        )
    }

    @Test
    fun `a text-tracked size grows with the font scale`() {
        val density = Density(density = 1f, fontScale = 2f)
        assertEquals(
            expected = 40.dp,
            actual = density.textTrackedSize(base = 20.dp),
        )
    }
}
