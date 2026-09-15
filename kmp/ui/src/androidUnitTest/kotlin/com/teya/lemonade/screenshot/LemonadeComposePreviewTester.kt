package com.teya.lemonade.screenshot

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import com.github.takahirom.roborazzi.AndroidComposePreviewTester
import com.github.takahirom.roborazzi.AndroidComposePreviewTester.CaptureParameter
import com.github.takahirom.roborazzi.AndroidComposePreviewTester.Capturer
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter.AndroidPreviewJUnit4TestParameter
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.teya.lemonade.LemonadeDarkTheme
import com.teya.lemonade.LemonadeLightTheme
import com.teya.lemonade.LemonadeSemanticColors
import com.teya.lemonade.LemonadeTheme
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo

/**
 * Roborazzi preview tester that wraps every discovered `@Preview` in
 * [LemonadeTheme], picking the light or dark colour set from the preview's
 * `uiMode`, and paints the theme's `background.bgDefault` behind it - previews
 * do not set `showBackground`, so the capture surface would otherwise be
 * transparent and dark-theme content (light text) would be invisible once the
 * PNG is flattened.
 *
 * Preview discovery, device/locale/font-scale configuration, manual-clock
 * handling and golden-file naming are delegated to Roborazzi's
 * [AndroidComposePreviewTester], so goldens stay consistent with upstream.
 *
 * Wired in via `roborazzi.generateComposePreviewRobolectricTests` in
 * `LemonadeScreenshotPlugin`.
 */
@OptIn(ExperimentalRoborazziApi::class)
public class LemonadeComposePreviewTester :
    ComposePreviewTester<AndroidPreviewJUnit4TestParameter> by AndroidComposePreviewTester(
        capturer = Capturer(::captureThemedPreview),
    )

@OptIn(ExperimentalRoborazziApi::class)
private fun captureThemedPreview(parameter: CaptureParameter) {
    val colors: LemonadeSemanticColors = if (parameter.preview.previewInfo.isNightMode) {
        LemonadeDarkTheme
    } else {
        LemonadeLightTheme
    }
    captureRoboImage(
        filePath = parameter.filePath,
        roborazziOptions = parameter.roborazziOptions,
        roborazziComposeOptions = parameter.roborazziComposeOptions,
    ) {
        LemonadeTheme(colors = colors) {
            Box(modifier = Modifier.background(color = colors.background.bgDefault)) {
                parameter.preview()
            }
        }
    }
}

private val AndroidPreviewInfo.isNightMode: Boolean
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
