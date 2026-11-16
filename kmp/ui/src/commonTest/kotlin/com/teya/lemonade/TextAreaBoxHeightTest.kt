package com.teya.lemonade

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TextAreaBoxHeightTest {
    @Test
    fun withoutLineLimitsTheBoxKeepsTheDefaultHeight() {
        assertEquals(180.dp, textAreaBoxHeight(height = Dp.Unspecified, lineLimits = null))
    }

    @Test
    fun withoutLineLimitsTheBoxKeepsTheHeightPassed() {
        assertEquals(120.dp, textAreaBoxHeight(height = 120.dp, lineLimits = null))
    }

    @Test
    fun withLineLimitsTheBoxSizesToItsText() {
        assertNull(textAreaBoxHeight(height = Dp.Unspecified, lineLimits = 1..5))
    }

    @Test
    fun aSingleLineRangeIsAccepted() {
        assertNull(textAreaBoxHeight(height = Dp.Unspecified, lineLimits = 3..3))
    }

    @Test
    fun heightAndLineLimitsTogetherAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            textAreaBoxHeight(height = 120.dp, lineLimits = 1..5)
        }
    }

    @Test
    fun lineLimitsBelowOneLineAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            textAreaBoxHeight(height = Dp.Unspecified, lineLimits = 0..5)
        }
    }

    @Test
    fun anEmptyRangeIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            textAreaBoxHeight(height = Dp.Unspecified, lineLimits = 5..1)
        }
    }
}
