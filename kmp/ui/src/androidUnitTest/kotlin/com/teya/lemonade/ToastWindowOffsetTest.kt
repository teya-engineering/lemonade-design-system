package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ToastWindowOffsetTest {
    @Test
    fun `a shown navigation bar lifts the toast by its inset`() {
        assertEquals(
            expected = 72 + 48,
            actual = toastWindowBottomOffsetPx(
                bottomMarginPx = 72,
                hideNavigationBar = false,
                navigationBarInsetPx = { 48 },
            ),
        )
    }

    @Test
    fun `a hidden navigation bar leaves the toast at its own margin`() {
        assertEquals(
            expected = 72,
            actual = toastWindowBottomOffsetPx(
                bottomMarginPx = 72,
                hideNavigationBar = true,
                navigationBarInsetPx = { 48 },
            ),
        )
    }

    @Test
    fun `a hidden navigation bar never reads the inset`() {
        toastWindowBottomOffsetPx(
            bottomMarginPx = 72,
            hideNavigationBar = true,
            navigationBarInsetPx = { fail("The inset of a hidden navigation bar was read") },
        )
    }
}
