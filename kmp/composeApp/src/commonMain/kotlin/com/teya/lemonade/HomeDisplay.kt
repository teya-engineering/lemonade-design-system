package com.teya.lemonade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.SelectListItemType
import com.teya.lemonade.core.TabButtonProperties

private val themeVariants: List<LemonadeThemeVariant> = LemonadeThemeVariant.entries

private val themeVariantTabs: List<TabButtonProperties> = themeVariants.map { variant ->
    TabButtonProperties.label(label = variant.label)
}

@Composable
internal fun HomeDisplay(onNavigate: (Displays) -> Unit) {
    val styleHandler = LocalLemonadeStyleHandler.current
    val selectedVariantIndex = themeVariants.indexOf(styleHandler.currentVariant)

    var showSettings by remember { mutableStateOf(value = false) }

    SampleScreenDisplayLazyColumn(
        title = "Lemonade Design System",
        action = {
            LemonadeUi.IconButton(
                icon = LemonadeIcons.Gear,
                contentDescription = "Settings",
                type = LemonadeButtonType.Ghost,
                onClick = { showSettings = true },
            )
        },
    ) {
        item(key = "theme-variants") {
            LemonadeUi.SegmentedControl(
                selectedTab = selectedVariantIndex,
                onTabSelected = { index -> styleHandler.currentVariant = themeVariants[index] },
                properties = themeVariantTabs,
                modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing400),
            )
        }
        items(
            items = DisplayRegistry.homeItems,
            key = { display -> display.title },
        ) { display ->
            CardSection(
                display = display,
                onNavigate = onNavigate,
            )
        }
    }

    SettingsSheet(
        expanded = showSettings,
        onDismissRequest = { showSettings = false },
    )
}

@Composable
private fun CardSection(
    display: DisplayRegistry.DisplayData,
    onNavigate: (Displays) -> Unit,
) {
    LemonadeUi.Card(
        header = CardHeaderConfig(title = display.title),
        modifier = Modifier.padding(top = LemonadeTheme.spaces.spacing400),
    ) {
        display.items.forEachIndexed { index, item ->
            LemonadeUi.ActionListItem(
                label = item.label,
                onItemClicked = { onNavigate(item) },
                showDivider = display.items.lastIndex != index,
                trailingSlot = {
                    LemonadeUi.Icon(
                        icon = LemonadeIcons.ChevronRight,
                        size = LemonadeAssetSize.Medium,
                        contentDescription = "Navigation indicator",
                        tint = LemonadeTheme.colors.content.contentTertiary,
                    )
                },
            )
        }
    }
}

@Composable
private fun SettingsSheet(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
) {
    val styleHandler = LocalLemonadeStyleHandler.current
    LemonadeUi.BottomSheet(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing400),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = LemonadeTheme.spaces.spacing400),
        ) {
            LemonadeUi.Text(
                text = "Settings",
                textStyle = LemonadeTheme.typography.headingSmall,
            )

            LemonadeUi.Card(
                header = CardHeaderConfig(title = "Appearance"),
            ) {
                val styles = LemonadeStyle.entries
                styles.forEachIndexed { index, style ->
                    LemonadeUi.SelectListItem(
                        label = style.label,
                        type = SelectListItemType.Single,
                        checked = styleHandler.currentStyle == style,
                        onItemClicked = { styleHandler.currentStyle = style },
                        showDivider = index != styles.lastIndex,
                    )
                }
            }

            Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing400))
        }
    }
}

internal object DisplayRegistry {
    val homeItems: List<DisplayData> = listOfNotNull(
        DisplayData(
            title = "Foundations",
            items = listOf(
                Displays.Colors,
                Displays.Spacing,
                Displays.Radius,
                Displays.Shadows,
                Displays.Sizes,
                Displays.Opacity,
                Displays.BorderWidth,
            ),
        ),
        DisplayData(
            title = "Assets",
            items = listOf(
                Displays.Icons,
                Displays.CountryFlag,
                Displays.BrandLogo,
            ),
        ),
        DisplayData(
            title = "Typography",
            items = listOf(
                Displays.Text,
                Displays.Markdown,
            ),
        ),
        DisplayData(
            title = "Form Controls",
            items = listOf(
                Displays.Button,
                Displays.IconButton,
                Displays.Checkbox,
                Displays.RadioButton,
                Displays.Switch,
                Displays.DatePicker,
                Displays.InlineCalendar,
                Displays.TimePicker,
            ),
        ),
        DisplayData(
            title = "Input Fields",
            items = listOf(
                Displays.TextField,
                Displays.SearchField,
                Displays.SelectField,
                Displays.PinCode,
            ),
        ),
        DisplayData(
            title = "Display Components",
            items = listOf(
                Displays.Tag,
                Displays.Badge,
                Displays.SymbolContainer,
                Displays.Card,
                Displays.Spinner,
                Displays.Skeleton,
                Displays.Divider,
                Displays.Notice,
                Displays.Toast,
                Displays.Tooltip,
                Displays.HistoryTimeline,
            ),
        ),
        DisplayData(
            title = "Selection & Lists",
            items = listOf(
                Displays.Chip,
                Displays.SelectListItem,
                Displays.ResourceListItem,
                Displays.ContentListItem,
                Displays.SegmentedControl,
                Displays.ActionListItem,
                Displays.BoxSelection,
            ),
        ),
        DisplayData(
            title = "Navigation",
            items = listOf(
                Displays.Link,
                Displays.Tabs,
                Displays.BottomTabBar,
                Displays.Tile,
            ),
        ),
        DisplayData(
            title = "Platform Specific",
            items = platformSpecificEntries,
        ).takeIf { platformSpecificEntries.isNotEmpty() },
    )

    data class DisplayData(
        val title: String,
        val items: List<Displays>,
    )
}
