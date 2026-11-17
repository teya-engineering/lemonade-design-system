package com.teya.lemonade

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TextAreaSizingTest {
    @Test
    fun aRangeFromOneLineIsAccepted() {
        assertEquals(1..5, TextAreaSizing.Lines(lineLimits = 1..5).lineLimits)
    }

    @Test
    fun aSingleLineRangeIsAccepted() {
        assertEquals(3..3, TextAreaSizing.Lines(lineLimits = 3..3).lineLimits)
    }

    @Test
    fun aRangeBelowOneLineIsRejected() {
        assertFailsWith<IllegalArgumentException> { TextAreaSizing.Lines(lineLimits = 0..5) }
    }

    @Test
    fun anEmptyRangeIsRejected() {
        assertFailsWith<IllegalArgumentException> { TextAreaSizing.Lines(lineLimits = 5..1) }
    }
}
