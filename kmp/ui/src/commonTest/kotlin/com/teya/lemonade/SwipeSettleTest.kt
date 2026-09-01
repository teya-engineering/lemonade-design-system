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
        revealWidth: Float = this.revealWidth,
    ): SwipeSettleTarget =
        resolveSwipeSettle(
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

    @Test
    fun `a drag exactly on half the reveal opens`() {
        assertEquals(expected = SwipeSettleTarget.Open, actual = settle(travel = revealWidth / 2f))
    }

    @Test
    fun `a flick exactly on the fling threshold opens`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 0f, velocity = 400f),
        )
    }

    @Test
    fun `a flick back exactly on the fling threshold closes`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 55f, velocity = -400f),
        )
    }

    @Test
    fun `travel exactly on half the row commits`() {
        assertEquals(
            expected = SwipeSettleTarget.Committed,
            actual = settle(travel = rowWidth / 2f),
        )
    }

    @Test
    fun `the first frame with nothing measured settles closed`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 0f, revealWidth = 0f),
        )
    }
}
