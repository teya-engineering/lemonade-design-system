package com.teya.lemonade

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Preview(
    name = "RTL",
    showBackground = true,
    locale = "ar",
)
@Preview(
    name = "LTR",
    showBackground = true,
    locale = "en",
)
internal annotation class LemonadePreview

internal class ThemedHuePreviewProvider : PreviewParameterProvider<ThemedHue> {
    override val values: Sequence<ThemedHue> = ThemedHue.entries.asSequence()
}
