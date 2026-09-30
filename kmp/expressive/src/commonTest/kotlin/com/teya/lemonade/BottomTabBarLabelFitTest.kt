package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers when the bar keeps its labels and when it falls back to icons. Geometry is measured off a
 * 1080x2400 phone at density 2.625: the item row spans 890px, so four items get 222px each and five
 * get 178px, and the 4dp gutter rounds to 11px a side. That leaves a label 200px and 156px.
 */
class BottomTabBarLabelFitTest {
    private val gutter = 11
    private val fourItemSlot = 890f / 4
    private val fiveItemSlot = 890f / 5

    @Test
    fun `labels a slot has room for are kept`() {
        assertTrue(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(70, 78, 92, 103),
                slotWidthPx = fourItemSlot,
                gutterPx = gutter,
            ),
        )
    }

    @Test
    fun `a label filling its slot to the last pixel still counts as fitting`() {
        assertTrue(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(200),
                slotWidthPx = fourItemSlot,
                gutterPx = gutter,
            ),
        )
    }

    @Test
    fun `one label too wide drops every label`() {
        assertFalse(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(70, 78, 92, 201),
                slotWidthPx = fourItemSlot,
                gutterPx = gutter,
            ),
        )
    }

    @Test
    fun `the same labels survive a wider bar and not a narrower one`() {
        val labels = listOf(70, 78, 92, 190)
        assertTrue(
            actual = labelsFitSlot(
                labelWidthsPx = labels,
                slotWidthPx = fourItemSlot,
                gutterPx = gutter,
            ),
        )
        assertFalse(
            actual = labelsFitSlot(
                labelWidthsPx = labels,
                slotWidthPx = fiveItemSlot,
                gutterPx = gutter,
            ),
        )
    }

    /**
     * A Row hands the remainder of an inexact division to its first children, so the narrowest slot
     * is the floor of the share. Rounding 222.5 up would promise a pixel the last slot never gets.
     */
    @Test
    fun `an inexact slot share is floored, not rounded`() {
        assertFalse(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(201),
                slotWidthPx = 222.5f,
                gutterPx = gutter,
            ),
        )
    }

    /**
     * The gutter is one side, so it comes off twice. Taking it off once would leave the label
     * ellipsising inside the padding the bar promised it.
     */
    @Test
    fun `the gutter is taken off both sides of a slot`() {
        assertFalse(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(211),
                slotWidthPx = fourItemSlot,
                gutterPx = gutter,
            ),
        )
    }

    /**
     * An unmeasured bar has no slot to have overflowed. Without the guard the budget is negative,
     * every label reads as too wide, and a bar whose labels fit flashes label-less on its first
     * frame.
     */
    @Test
    fun `an unmeasured bar keeps its labels`() {
        assertTrue(
            actual = labelsFitSlot(
                labelWidthsPx = listOf(70, 78, 92, 103),
                slotWidthPx = 0f,
                gutterPx = gutter,
            ),
        )
    }
}
