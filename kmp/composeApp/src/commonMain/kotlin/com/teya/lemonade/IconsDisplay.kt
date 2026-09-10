package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

@Composable
internal fun IconsDisplay() {
    var searchQuery by remember { mutableStateOf("") }
    val allIcons = remember { LemonadeIcons.entries }
    val filteredIcons by remember {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                allIcons
            } else {
                allIcons.filter { icon ->
                    icon.name.contains(
                        other = searchQuery,
                        ignoreCase = true,
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        @OptIn(ExperimentalLemonadeComponent::class)
        LemonadeUi.SearchField(
            input = searchQuery,
            onInputChanged = { input -> searchQuery = input },
            placeholder = "Search icons...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LemonadeTheme.spaces.spacing400)
                .padding(top = LemonadeTheme.spaces.spacing400),
        )

        LemonadeUi.Text(
            text = "${filteredIcons.size} icons",
            textStyle = LemonadeTheme.typography.bodySmallRegular,
            color = LemonadeTheme.colors.content.contentSecondary,
            modifier = Modifier
                .padding(horizontal = LemonadeTheme.spaces.spacing400)
                .padding(
                    top = LemonadeTheme.spaces.spacing200,
                    bottom = LemonadeTheme.spaces.spacing100,
                ),
        )

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(minSize = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
            contentPadding = PaddingValues(LemonadeTheme.spaces.spacing400),
        ) {
            items(
                items = filteredIcons,
                key = { icon -> icon.ordinal },
            ) { icon ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(LemonadeTheme.radius.radius200))
                        .background(LemonadeTheme.colors.background.bgSubtle)
                        .padding(LemonadeTheme.spaces.spacing200),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    LemonadeUi.Icon(
                        icon = icon,
                        contentDescription = "Icon: ${icon.name}",
                        size = LemonadeAssetSize.Large,
                    )

                    LemonadeUi.Text(
                        text = icon.name,
                        textStyle = LemonadeTheme.typography.bodyXSmallRegular,
                        color = LemonadeTheme.colors.content.contentSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = LemonadeTheme.spaces.spacing100),
                    )
                }
            }
        }
    }
}
