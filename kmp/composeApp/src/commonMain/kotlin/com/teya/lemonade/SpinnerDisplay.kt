package com.teya.lemonade

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.teya.lemonade.core.LemonadeAssetSize

@Composable
internal fun SpinnerDisplay() {
    SampleScreenDisplayLazyGrid(title = "Spinner") {
        items(
            items = LemonadeAssetSize.entries,
            key = { size -> size.ordinal },
        ) { size ->
            LemonadeAssetBox(
                asset = {
                    LemonadeUi.Spinner(size = size)
                },
                label = size.name,
            )
        }
    }
}
