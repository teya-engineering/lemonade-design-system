package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Covers the rules that give a swipe a side: which one a gesture owns, how far the row may travel
 * onto it, and where a delta leaves the row. Geometry matches one action a side on a 361dp-wide row,
 * where 76dp of travel brings an action fully out.
 */
class SwipeSideTest {
    private val revealWidth = 76f
    private val rowWidth = 361f

    // resolveSwipeGestureSide

    @Test
    fun `a closed row takes the side the finger sets off towards`() {
        assertEquals(
            expected = SwipeActionSide.Trailing,
            actual = resolveSwipeGestureSide(travel = 0f, delta = 12f),
        )
        assertEquals(
            expected = SwipeActionSide.Leading,
            actual = resolveSwipeGestureSide(travel = 0f, delta = -12f),
        )
    }

    /**
     * One gesture, one side. A finger dragging an open row back is closing it, not setting off
     * towards the actions on the other edge.
     */
    @Test
    fun `an open row keeps its own side whichever way the finger goes`() {
        assertEquals(
            expected = SwipeActionSide.Trailing,
            actual = resolveSwipeGestureSide(travel = 76f, delta = -400f),
        )
        assertEquals(
            expected = SwipeActionSide.Leading,
            actual = resolveSwipeGestureSide(travel = -76f, delta = 400f),
        )
    }

    @Test
    fun `nothing is decided while nothing has moved`() {
        assertNull(actual = resolveSwipeGestureSide(travel = 0f, delta = 0f))
    }

    // resolveSwipeCeiling

    /**
     * The one case the trailing-only row never had: a row with actions on one edge only is still
     * draggable, and the empty edge has to hold it at rest rather than let it be carried across.
     */
    @Test
    fun `a side with no actions has nowhere to go`() {
        assertEquals(
            expected = 0f,
            actual = resolveSwipeCeiling(
                revealWidth = 0f,
                rowWidth = rowWidth,
                allowsFullSwipe = true,
            ),
        )
    }

    @Test
    fun `a full swipe may carry the row across`() {
        assertEquals(
            expected = rowWidth,
            actual = resolveSwipeCeiling(
                revealWidth = revealWidth,
                rowWidth = rowWidth,
                allowsFullSwipe = true,
            ),
        )
    }

    @Test
    fun `without one the row caps at the reveal`() {
        assertEquals(
            expected = revealWidth,
            actual = resolveSwipeCeiling(
                revealWidth = revealWidth,
                rowWidth = rowWidth,
                allowsFullSwipe = false,
            ),
        )
    }

    // resolveSwipeTravel

    private fun travel(
        travel: Float,
        delta: Float,
        side: SwipeActionSide?,
        leadingCeiling: Float = rowWidth,
        trailingCeiling: Float = rowWidth,
    ): Float =
        resolveSwipeTravel(
            travel = travel,
            delta = delta,
            side = side,
            leadingCeiling = leadingCeiling,
            trailingCeiling = trailingCeiling,
        )

    /** Leading travel is negative, which is the whole of what tells the row which strip to draw. */
    @Test
    fun `travel carries the side in its sign`() {
        assertEquals(
            expected = 40f,
            actual = travel(travel = 0f, delta = 40f, side = SwipeActionSide.Trailing),
        )
        assertEquals(
            expected = -40f,
            actual = travel(travel = 0f, delta = -40f, side = SwipeActionSide.Leading),
        )
    }

    /**
     * A gesture that has closed the row stops there. Carrying on into the other side's actions
     * would turn one drag back into a commit on the opposite edge.
     */
    @Test
    fun `a gesture cannot cross into the other side`() {
        assertEquals(
            expected = 0f,
            actual = travel(travel = 10f, delta = -50f, side = SwipeActionSide.Trailing),
        )
        assertEquals(
            expected = 0f,
            actual = travel(travel = -10f, delta = 50f, side = SwipeActionSide.Leading),
        )
    }

    @Test
    fun `travel caps at its own side's ceiling`() {
        assertEquals(
            expected = revealWidth,
            actual = travel(
                travel = 0f,
                delta = 400f,
                side = SwipeActionSide.Trailing,
                trailingCeiling = revealWidth,
            ),
        )
        assertEquals(
            expected = -revealWidth,
            actual = travel(
                travel = 0f,
                delta = -400f,
                side = SwipeActionSide.Leading,
                leadingCeiling = revealWidth,
            ),
        )
    }

    /** A side with nothing behind it holds the row at rest however hard it is dragged. */
    @Test
    fun `an empty side does not move`() {
        assertEquals(
            expected = 0f,
            actual = travel(
                travel = 0f,
                delta = -400f,
                side = SwipeActionSide.Leading,
                leadingCeiling = 0f,
            ),
        )
    }

    /** Undecided only while the row is at rest and the finger has not moved, so it may go either way. */
    @Test
    fun `an undecided gesture is held to both ceilings`() {
        assertEquals(
            expected = 0f,
            actual = travel(travel = 0f, delta = 0f, side = null),
        )
    }
}
