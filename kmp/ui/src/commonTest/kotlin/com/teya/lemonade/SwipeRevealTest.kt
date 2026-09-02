package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers [resolveSwipeStripReveal] — how far one action has been revealed. Geometry matches a 40dp
 * trailing action in a 60dp reveal: 12dp of padding ahead of it and 8dp behind.
 */
class SwipeRevealTest {
    private val revealWidth = 76f
    private val actionWidth = 48f

    private fun reveal(
        travel: Float,
        actionWidth: Float = this.actionWidth,
        revealWidth: Float = this.revealWidth,
        stripReveal: Float = revealWidth,
    ): SwipeStripReveal =
        resolveSwipeStripReveal(
            travel = travel,
            actionReveal = revealWidth,
            stripReveal = stripReveal,
            actionWidth = actionWidth,
        )

    /** An action grows over the last half of its own width — the last 24dp of a 48dp action. */
    @Test
    fun `nothing is drawn until the last half of the action`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 0f, stretch = 0f),
            actual = reveal(travel = 51f),
        )
        assertEquals(
            expected = SwipeStripReveal(scale = 0f, stretch = 0f),
            actual = reveal(travel = 52f),
        )
    }

    @Test
    fun `the action grows with the row over that last half`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 0.5f, stretch = 0f),
            actual = reveal(travel = 64f),
        )
    }

    @Test
    fun `the action is at full size when the row rests open`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 1f, stretch = 0f),
            actual = reveal(travel = 76f),
        )
    }

    @Test
    fun `travel past the reveal stretches the action instead of scaling it`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 1f, stretch = 40f),
            actual = reveal(travel = 116f),
        )
    }

    /**
     * The invariant the whole model rests on: whatever the row has opened, the action's leading edge
     * sits one leading gap — 12dp — ahead of it, scaling or stretched.
     */
    @Test
    fun `the action is always exactly the gap the row has opened`() {
        val trailingPad = 16f
        var travel = 52f
        while (travel <= 200f) {
            val revealed = reveal(travel = travel)
            val leadingEdge = trailingPad + actionWidth / 2f +
                actionWidth / 2f * revealed.scale + revealed.stretch
            assertEquals(expected = travel - 12f, actual = leadingEdge, absoluteTolerance = 0.001f)
            travel += 5f
        }
    }

    /**
     * The outermost of a pair is out at 76dp but must not start filling the space behind it: that
     * space is where the second action arrives, and a stretch would cover it before it ever did.
     */
    @Test
    fun `the outermost action waits for the whole strip before stretching`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 1f, stretch = 0f),
            actual = reveal(travel = 132f, revealWidth = 76f, stripReveal = 132f),
        )
        assertEquals(
            expected = SwipeStripReveal(scale = 1f, stretch = 40f),
            actual = reveal(travel = 172f, revealWidth = 76f, stripReveal = 132f),
        )
    }

    /**
     * Actions being pushed along by a stretching one recede as it takes the row over: unchanged
     * through the reveal and up to the commit threshold, down to a fifth by the time the row has
     * gone.
     */
    @Test
    fun `displaced actions dim as the swipe takes the row over`() {
        val rowWidth = 400f
        val tolerance = 0.001f
        assertEquals(1f, resolveSwipeDisplacedOpacity(travel = 0f, rowWidth = rowWidth), tolerance)
        assertEquals(1f, resolveSwipeDisplacedOpacity(travel = 200f, rowWidth = rowWidth), tolerance)
        assertEquals(0.6f, resolveSwipeDisplacedOpacity(travel = 300f, rowWidth = rowWidth), tolerance)
        assertEquals(0.2f, resolveSwipeDisplacedOpacity(travel = 400f, rowWidth = rowWidth), tolerance)
    }

    /** A row that has not measured yet must not dim anything. */
    @Test
    fun `nothing dims before the row has a width`() {
        assertEquals(1f, resolveSwipeDisplacedOpacity(travel = 0f, rowWidth = 0f), 0.001f)
    }

    @Test
    fun `an empty strip is never drawn`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 0f, stretch = 0f),
            actual = reveal(travel = 200f, actionWidth = 0f, revealWidth = 0f),
        )
    }

    /**
     * The second of two actions sits behind the first, so the row has to travel 132dp to rest on it
     * rather than the 76dp that rests it on the first. Each action waits for its own share.
     */
    @Test
    fun `an action behind another is revealed against its own share`() {
        assertEquals(
            expected = SwipeStripReveal(scale = 0f, stretch = 0f),
            actual = reveal(travel = 108f, revealWidth = 132f),
        )
        assertEquals(
            expected = SwipeStripReveal(scale = 0.5f, stretch = 0f),
            actual = reveal(travel = 120f, revealWidth = 132f),
        )
        assertEquals(
            expected = SwipeStripReveal(scale = 1f, stretch = 0f),
            actual = reveal(travel = 132f, revealWidth = 132f),
        )
    }
}
