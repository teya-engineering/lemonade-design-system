package com.teya.lemonade

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Covers where [TopBarState] sits across the layout's measurement, which lands after the first
 * composition and lands again whenever the collapsable slot is remeasured.
 */
class TopBarStateTest {
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    private fun state(
        startCollapsed: Boolean,
        lockGestureAnimation: Boolean = true,
    ): TopBarState =
        TopBarState(
            coroutineScope = scope,
            startCollapsed = startCollapsed,
            lockGestureAnimation = lockGestureAnimation,
        )

    /** The composition reads the bar before the layout has measured the collapsable slot. */
    @Test
    fun `a bar that starts collapsed reads as collapsed before the first measurement`() {
        val state = state(startCollapsed = true)

        assertEquals(expected = 1f, actual = state.collapseProgress)
        assertTrue(state.isCollapsed)
    }

    @Test
    fun `a bar that starts expanded reads as expanded before the first measurement`() {
        val state = state(startCollapsed = false)

        assertEquals(expected = 0f, actual = state.collapseProgress)
        assertFalse(state.isCollapsed)
    }

    @Test
    fun `the first measurement does not move a bar that starts collapsed`() {
        val state = state(startCollapsed = true)

        state.maxScrollOffset = 120f

        assertEquals(expected = 1f, actual = state.collapseProgress)
        assertEquals(expected = 120f, actual = state.scrollOffset)
        assertEquals(expected = -120f, actual = state.heightOffset)
    }

    @Test
    fun `the first measurement does not move a bar that starts expanded`() {
        val state = state(startCollapsed = false)

        state.maxScrollOffset = 120f

        assertEquals(expected = 0f, actual = state.collapseProgress)
        assertEquals(expected = 0f, actual = state.scrollOffset)
    }

    @Test
    fun `a remeasure keeps the bar where the scroll left it`() {
        val state = state(startCollapsed = false, lockGestureAnimation = false)
        state.maxScrollOffset = 120f
        state.scrollBy(deltaY = -60f)
        assertEquals(expected = 0.5f, actual = state.collapseProgress)

        state.maxScrollOffset = 80f

        assertEquals(expected = 0.5f, actual = state.collapseProgress)
        assertEquals(expected = 40f, actual = state.scrollOffset)
    }

    @Test
    fun `scrolling past the collapsable height stops at fully collapsed`() {
        val state = state(startCollapsed = false, lockGestureAnimation = false)
        state.maxScrollOffset = 120f

        state.scrollBy(deltaY = -400f)

        assertEquals(expected = 1f, actual = state.collapseProgress)
        assertEquals(expected = 120f, actual = state.scrollOffset)
    }

    private fun TopBarState.scrollBy(deltaY: Float) {
        nestedScrollConnection.onPreScroll(
            available = Offset(x = 0f, y = deltaY),
            source = NestedScrollSource.UserInput,
        )
    }
}
