package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.teya.lemonade.core.CountryFlagShape
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeCountryFlags

@Composable
internal fun CountryFlagDisplay() {
    val isoAlpha2Example = "GB"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.Start,
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
            contentPadding = PaddingValues(LemonadeTheme.spaces.spacing400),
        ) {
            item(span = { GridItemSpan(3) }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    LemonadeUi.Text(
                        text = "Using ISO Alpha2 Format",
                        textStyle = LemonadeTheme.typography.headingXXSmall,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing100),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LemonadeCountryFlags.getOrNull(alpha2 = isoAlpha2Example)?.let { flag ->
                            FlagBox(
                                flag = flag,
                                label = isoAlpha2Example,
                            )
                        }
                    }
                }
            }

            CountryFlagShape.entries.forEach { shape ->
                item(span = { GridItemSpan(3) }) {
                    LemonadeUi.Text(
                        text = "Country Flags - ${shape.name}",
                        textStyle = LemonadeTheme.typography.headingXXSmall,
                        modifier = Modifier
                            .padding(top = LemonadeTheme.spaces.spacing400),
                    )
                }

                items(
                    items = LemonadeCountryFlags.entries,
                    key = { flag -> "${shape.name}-${flag.ordinal}" },
                ) { flag ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        FlagBox(
                            flag = flag,
                            shape = shape,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlagBox(
    flag: LemonadeCountryFlags,
    label: String? = null,
    shape: CountryFlagShape = CountryFlagShape.Circular,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(LemonadeTheme.radius.radius500))
            .background(LemonadeTheme.colors.background.bgDefault)
            .border(
                width = LemonadeTheme.borderWidths.base.border25,
                color = LemonadeTheme.colors.border.borderNeutralLow,
                shape = RoundedCornerShape(LemonadeTheme.radius.radius500),
            ).padding(
                horizontal = LemonadeTheme.spaces.spacing200,
                vertical = LemonadeTheme.spaces.spacing300,
            ),
    ) {
        LemonadeUi.CountryFlag(
            flag = flag,
            size = LemonadeAssetSize.XXLarge,
            shape = shape,
        )
        Spacer(
            modifier = Modifier
                .height(LemonadeTheme.spaces.spacing200),
        )
        LemonadeUi.Text(
            text = label
                ?: flag.name,
            overflow = TextOverflow.Clip,
            maxLines = 1,
            textStyle = LemonadeTheme.typography.bodyXSmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
    }
}
