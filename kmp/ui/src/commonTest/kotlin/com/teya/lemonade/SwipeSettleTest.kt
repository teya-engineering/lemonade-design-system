package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers [resolveSwipeSettle] — where a released drag lands. Geometry matches one trailing action
 * on a 361dp-wide row: a 60dp reveal, and a full swipe that has to cross half of 361.
 */
class SwipeSettleTest {
    private val revealWidth = 60f
    private val rowWidth = 361f

    private fun settle(
        travel: Float,
        velocity: Float = 0f,
        allowsFullSwipe: Boolean = true,
    ): SwipeSettleTarget = resolveSwipeSettle(
        travel = travel,
        velocity = velocity,
        revealWidth = revealWidth,
        rowWidth = rowWidth,
        allowsFullSwipe = allowsFullSwipe,
    )

    @Test
    fun `a drag short of half the reveal settles closed`() {
        assertEquals(expected = SwipeSettleTarget.Closed, actual = settle(travel = 20f))
    }

    @Test
    fun `a drag past half the reveal settles open`() {
        assertEquals(expected = SwipeSettleTarget.Open, actual = settle(travel = 40f))
    }

    @Test
    fun `a flick opens before travelling half the reveal`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 12f, velocity = 900f),
        )
    }

    @Test
    fun `a flick back closes an open row`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 55f, velocity = -900f),
        )
    }

    @Test
    fun `crossing half the row commits`() {
        assertEquals(expected = SwipeSettleTarget.Committed, actual = settle(travel = 200f))
    }

    @Test
    fun `a long drag only opens when full swipe is off`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 200f, allowsFullSwipe = false),
        )
    }

    @Test
    fun `a commit beats a flick back`() {
        assertEquals(
            expected = SwipeSettleTarget.Committed,
            actual = settle(travel = 200f, velocity = -900f),
        )
    }
}
