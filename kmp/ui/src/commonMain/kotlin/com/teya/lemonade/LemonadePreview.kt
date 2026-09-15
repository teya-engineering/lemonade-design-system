package com.teya.lemonade

import androidx.compose.ui.tooling.preview.Preview

// android.content.res.Configuration.UI_MODE_NIGHT_{NO,YES}; inlined because
// commonMain cannot reference the Android Configuration constants.
private const val UI_MODE_NIGHT_NO = 0x10
private const val UI_MODE_NIGHT_YES = 0x20

@Preview(
    name = "Light",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    uiMode = UI_MODE_NIGHT_YES,
)
internal annotation class LemonadePreview
