package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeSegmentedControlSize
import com.teya.lemonade.core.TabButtonProperties

private val iconTabLabels: List<String> = listOf("Home", "Profile", "Settings")

@Suppress("LongMethod")
@Composable
internal fun SegmentedControlDisplay() {
    var selectedLarge by remember { mutableIntStateOf(0) }
    var selectedMedium by remember { mutableIntStateOf(0) }
    var selectedSmall by remember { mutableIntStateOf(0) }
    var selectedIcons by remember { mutableIntStateOf(1) }
    var selectedIconOnly by remember { mutableIntStateOf(0) }

    SampleScreenDisplayColumn(title = "SegmentedControl") {
        SegmentedControlSection(title = "Large (Default)") {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                ),
            ) {
                LemonadeUi.SegmentedControl(
                    onTabSelected = { index ->
                        selectedLarge = index
                    },
                    selectedTab = selectedLarge,
                    properties = listOf(
                        TabButtonProperties.label(label = "Tab 1"),
                        TabButtonProperties.label(label = "Tab 2"),
                        TabButtonProperties.label(label = "Tab 3"),
                    ),
                )
                LemonadeUi.Text(
                    text = "Selected: Tab ${selectedLarge + 1}",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                )
            }
        }

        SegmentedControlSection(title = "Medium") {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                ),
            ) {
                LemonadeUi.SegmentedControl(
                    onTabSelected = { index ->
                        selectedMedium = index
                    },
                    selectedTab = selectedMedium,
                    size = LemonadeSegmentedControlSize.Medium,
                    properties = listOf(
                        TabButtonProperties.label(label = "Tab 1"),
                        TabButtonProperties.label(label = "Tab 2"),
                        TabButtonProperties.label(label = "Tab 3"),
                    ),
                )
                LemonadeUi.Text(
                    text = "Selected: Tab ${selectedMedium + 1}",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                )
            }
        }

        SegmentedControlSection(title = "Small") {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                ),
            ) {
                LemonadeUi.SegmentedControl(
                    onTabSelected = { index ->
                        selectedSmall = index
                    },
                    selectedTab = selectedSmall,
                    size = LemonadeSegmentedControlSize.Small,
                    properties = listOf(
                        TabButtonProperties.label(label = "Tab 1"),
                        TabButtonProperties.label(label = "Tab 2"),
                        TabButtonProperties.label(label = "Tab 3"),
                    ),
                )
                LemonadeUi.Text(
                    text = "Selected: Tab ${selectedSmall + 1}",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                )
            }
        }

        SegmentedControlSection(title = "With Icons") {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = LemonadeTheme.spaces.spacing200,
                ),
            ) {
                LemonadeUi.SegmentedControl(
                    onTabSelected = { index ->
                        selectedIcons = index
                    },
                    selectedTab = selectedIcons,
                    properties = listOf(
                        TabButtonProperties.labelAndIcon(
                            label = "Home",
                            icon = LemonadeIcons.Home,
                        ),
                        TabButtonProperties.labelAndIcon(
                            label = "Profile",
                            icon = LemonadeIcons.User,
                        ),
                        TabButtonProperties.labelAndIcon(
                            label = "Settings",
                            icon = LemonadeIcons.Gear,
                        ),
                    ),
                )
                LemonadeUi.Text(
                    text = "Selected: ${iconTabLabels[selectedIcons]}",
                    textStyle = LemonadeTheme.typography.bodySmallRegular,
                )
            }
        }

        SegmentedControlSection(title = "Icon Only (Small)") {
            LemonadeUi.SegmentedControl(
                onTabSelected = { index ->
                    selectedIconOnly = index
                },
                selectedTab = selectedIconOnly,
                size = LemonadeSegmentedControlSize.Small,
                modifier = Modifier.width(IntrinsicSize.Min),
                properties = listOf(
                    TabButtonProperties.icon(icon = LemonadeIcons.List),
                    TabButtonProperties.icon(icon = LemonadeIcons.StackThree),
                ),
            )
        }
    }
}

@Composable
private fun SegmentedControlSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = LemonadeTheme.spaces.spacing300,
        ),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}
