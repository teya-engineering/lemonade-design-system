package com.teya.lemonade

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TextAreaLineLimitsUiTest {
    @Test
    fun theBoxGrowsLineByLineUpToTheLastLimitThenStops() =
        runComposeUiTest {
            var input by mutableStateOf("")
            setContent {
                LemonadeTheme {
                    LemonadeUi.TextArea(
                        input = input,
                        onInputChanged = { value -> input = value },
                        lineLimits = 1..3,
                        modifier = Modifier.testTag(AREA),
                    )
                }
            }

            val heights = listOf("", "one\ntwo", "one\ntwo\nthree", "one\ntwo\nthree\nfour\nfive\nsix").map { text ->
                input = text
                waitForIdle()
                onNodeWithTag(AREA).getUnclippedBoundsInRoot().height
            }

            assertTrue(heights[1] > heights[0], "two lines should be taller than one: $heights")
            assertTrue(heights[2] > heights[1], "three lines should be taller than two: $heights")
            assertEquals(heights[2], heights[3], "past the last limit the box should keep its height: $heights")
        }

    @Test
    fun theStateOverloadStartsAtTheFirstLimit() =
        runComposeUiTest {
            setContent {
                LemonadeTheme {
                    Box {
                        LemonadeUi.TextArea(
                            state = TextFieldState(),
                            lineLimits = 1..4,
                            modifier = Modifier.testTag(AREA),
                        )
                    }
                    Box {
                        LemonadeUi.TextArea(
                            state = TextFieldState(),
                            lineLimits = 2..4,
                            modifier = Modifier.testTag(OTHER_AREA),
                        )
                    }
                }
            }

            val oneLine = onNodeWithTag(AREA).getUnclippedBoundsInRoot().height
            val twoLines = onNodeWithTag(OTHER_AREA).getUnclippedBoundsInRoot().height
            assertTrue(twoLines > oneLine, "a field starting at two lines should be taller: $oneLine vs $twoLines")
        }

    @Test
    fun withLineLimitsTheBoxFillsTheWidthButNotTheHeight() =
        runComposeUiTest {
            setContent {
                LemonadeTheme {
                    Box(modifier = Modifier.size(width = 300.dp, height = 600.dp)) {
                        LemonadeUi.TextArea(
                            input = "",
                            onInputChanged = { },
                            lineLimits = 1..5,
                            modifier = Modifier.testTag(AREA),
                        )
                    }
                }
            }

            val bounds = onNodeWithTag(AREA).getUnclippedBoundsInRoot()
            assertEquals(300.dp, bounds.width)
            assertTrue(bounds.height < 100.dp, "a one-line box should not fill a tall parent: ${bounds.height}")
        }

    @Test
    fun withoutLineLimitsTheBoxKeepsItsFixedHeight() =
        runComposeUiTest {
            setContent {
                LemonadeTheme {
                    Box(modifier = Modifier.size(width = 300.dp, height = 600.dp)) {
                        LemonadeUi.TextArea(
                            input = "",
                            onInputChanged = { },
                            modifier = Modifier.testTag(AREA),
                        )
                    }
                }
            }

            assertTrue(onNodeWithTag(AREA).getUnclippedBoundsInRoot().height >= 180.dp)
        }

    @Test
    fun aLongPlaceholderDoesNotGrowTheEmptyBox() =
        runComposeUiTest {
            var placeholder: String? by mutableStateOf(null)
            setContent {
                LemonadeTheme {
                    Box(modifier = Modifier.size(width = 300.dp, height = 600.dp)) {
                        LemonadeUi.TextArea(
                            input = "",
                            onInputChanged = { },
                            lineLimits = 1..5,
                            placeholderText = placeholder,
                            modifier = Modifier.testTag(AREA),
                        )
                    }
                }
            }

            val withoutPlaceholder = onNodeWithTag(AREA).getUnclippedBoundsInRoot().height
            placeholder = List(size = 40) { "placeholder" }.joinToString(separator = " ")
            waitForIdle()
            val withPlaceholder: Dp = onNodeWithTag(AREA).getUnclippedBoundsInRoot().height

            assertEquals(withoutPlaceholder, withPlaceholder)
        }

    private companion object {
        const val AREA = "area"
        const val OTHER_AREA = "other-area"
    }
}
