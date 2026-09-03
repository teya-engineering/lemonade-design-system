package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Covers [resolveSwipeSettle] — where a released drag lands. Geometry matches a trailing action on a
 * 361dp-wide row: 76dp of travel brings it fully out, and a full swipe commits at
 * [swipeCommitThreshold], which is 198.55dp of it.
 */
class SwipeSettleTest {
    private val firstActionReveal = 76f
    private val rowWidth = 361f

    private fun settle(
        travel: Float,
        velocity: Float = 0f,
        allowsFullSwipe: Boolean = true,
        firstActionReveal: Float = this.firstActionReveal,
        rowWidth: Float = this.rowWidth,
    ): SwipeSettleTarget =
        resolveSwipeSettle(
            travel = travel,
            velocity = velocity,
            firstActionReveal = firstActionReveal,
            rowWidth = rowWidth,
            allowsFullSwipe = allowsFullSwipe,
        )

    /** A drag that showed most of the action but not all of it still belongs back where it was. */
    @Test
    fun `a drag short of the whole action settles closed`() {
        assertEquals(expected = SwipeSettleTarget.Closed, actual = settle(travel = 70f))
    }

    @Test
    fun `a drag that brings the action fully out settles open`() {
        assertEquals(expected = SwipeSettleTarget.Open, actual = settle(travel = 76f))
    }

    /** Momentum settles the row where it was going, not where the finger let go. */
    @Test
    fun `a flick opens a row the finger did not carry all the way`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 20f, velocity = 900f),
        )
    }

    /**
     * The same distance without the speed behind it does not, which is the whole point of settling
     * on the projection rather than on a speed rule of its own.
     */
    @Test
    fun `the same drag without the speed does not`() {
        assertEquals(expected = SwipeSettleTarget.Closed, actual = settle(travel = 20f))
    }

    /**
     * A destructive action must not fire off momentum alone: the commit reads the travel itself, so
     * a flick that projects across the row still only opens it.
     */
    @Test
    fun `momentum does not commit a swipe the finger never carried`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 100f, velocity = 2000f),
        )
    }

    @Test
    fun `a flick back closes an open row`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 100f, velocity = -900f),
        )
    }

    @Test
    fun `crossing the commit threshold commits`() {
        assertEquals(expected = SwipeSettleTarget.Committed, actual = settle(travel = 240f))
    }

    @Test
    fun `a long drag only opens when full swipe is off`() {
        assertEquals(
            expected = SwipeSettleTarget.Open,
            actual = settle(travel = 240f, allowsFullSwipe = false),
        )
    }

    @Test
    fun `a commit beats a flick back`() {
        assertEquals(
            expected = SwipeSettleTarget.Committed,
            actual = settle(travel = 240f, velocity = -900f),
        )
    }

    @Test
    fun `a drag exactly on the action's reveal opens`() {
        assertEquals(expected = SwipeSettleTarget.Open, actual = settle(travel = firstActionReveal))
    }

    /** A drag back carries its own momentum too, so the projection is what closes the row. */
    @Test
    fun `a flick back closes a row the finger left open`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 100f, velocity = -400f),
        )
    }

    @Test
    fun `travel exactly on the commit threshold commits`() {
        assertEquals(
            expected = SwipeSettleTarget.Committed,
            actual = settle(travel = rowWidth * 0.55f),
        )
    }

    /** Halfway is no longer enough: iOS asks for a little more than half the row. */
    @Test
    fun `travel on half the row does not commit`() {
        assertNotEquals(
            illegal = SwipeSettleTarget.Committed,
            actual = settle(travel = rowWidth / 2f),
        )
    }

    @Test
    fun `a row with no actions settles closed`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 0f, firstActionReveal = 0f),
        )
    }

    /**
     * Coming back from a commit, the row gives up its lead in proportion to the finger: no step at
     * the crossing, and home exactly as the finger gets there.
     */
    @Test
    fun `a released commit hands the row back in proportion to the finger`() {
        val threshold = rowWidth * 0.55f
        val commitTravel = rowWidth - 20f
        val released = { travel: Float ->
            resolveSwipeReleasedTravel(
                travel = travel,
                commitTravel = commitTravel,
                threshold = threshold,
            )
        }
        // Continuous at the crossing: a drag can only leave a commit here.
        assertEquals(commitTravel, released(threshold), 0.001f)
        assertEquals(commitTravel / 2f, released(threshold / 2f), 0.001f)
        assertEquals(0f, released(0f), 0.001f)
    }

    /** Never past where the commit had it, however far the finger is. */
    @Test
    fun `a released commit never draws the row further than the commit did`() {
        assertEquals(
            expected = 400f,
            actual = resolveSwipeReleasedTravel(travel = 300f, commitTravel = 400f, threshold = 220f),
            absoluteTolerance = 0.001f,
        )
    }

    /** A row with no width to cross has no lead to give back. */
    @Test
    fun `a released commit with no threshold draws the finger`() {
        assertEquals(
            expected = 40f,
            actual = resolveSwipeReleasedTravel(travel = 40f, commitTravel = 0f, threshold = 0f),
            absoluteTolerance = 0.001f,
        )
    }

    /**
     * A drag that committed and then came back below the threshold must not fire on release.
     *
     * The two resolvers compose: the row is drawn at [resolveSwipeReleasedTravel] of the finger,
     * which is up to 1.7x further out, and settling on *that* fires the action from a third of the
     * way across — after the crossing back has already told the reader the gesture is no longer
     * its. Each function alone is right; only together do they say so.
     */
    @Test
    fun `a drag handed back from a commit does not fire on release`() {
        val commitTravel = rowWidth - 20f
        val threshold = swipeCommitThreshold(rowWidth = rowWidth)
        // Just below the threshold, where crossing back has just happened.
        val reached = threshold - 1f
        val drawn = resolveSwipeReleasedTravel(
            travel = reached,
            commitTravel = commitTravel,
            threshold = threshold,
        )
        assertTrue(drawn > reached, "the row is drawn ahead of the finger on the way back")
        assertEquals(expected = SwipeSettleTarget.Committed, actual = settle(travel = drawn))
        assertNotEquals(illegal = SwipeSettleTarget.Committed, actual = settle(travel = reached))
    }

    /**
     * A row that has not been measured yet must not commit.
     *
     * The threshold is a fraction of the row's width, so at width zero it is zero and every
     * release clears it — including one that never moved. Unreachable through the component,
     * which cannot be dragged before it is laid out, but this is the half that is meant to be
     * right on its own.
     */
    @Test
    fun `an unmeasured row does not commit`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 0f, rowWidth = 0f),
        )
    }

    /** Nor does a row with nothing behind it, however far it is dragged. */
    @Test
    fun `a row with no actions does not commit`() {
        assertEquals(
            expected = SwipeSettleTarget.Closed,
            actual = settle(travel = 300f, firstActionReveal = 0f),
        )
    }
}
