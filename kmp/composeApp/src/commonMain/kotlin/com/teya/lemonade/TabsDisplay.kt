package com.teya.lemonade

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeIcons

// Hoisted so the lists keep the same identity across recompositions. `List<T>` is an unstable
// parameter type, so an inline `listOf(...)` would make Tabs re-compose on every tap.
private val basicTabs: List<TabItem> = listOf(
    TabItem(label = "Overview"),
    TabItem(label = "Details"),
    TabItem(label = "Reviews"),
)

private val iconTabs: List<TabItem> = listOf(
    TabItem(
        label = "Home",
        icon = LemonadeIcons.Home,
    ),
    TabItem(
        label = "Analytics",
        icon = LemonadeIcons.Chart,
    ),
    TabItem(
        label = "Settings",
        icon = LemonadeIcons.Gear,
    ),
)

private val stretchTabs: List<TabItem> = listOf(
    TabItem(label = "Tab A"),
    TabItem(label = "Tab B"),
    TabItem(label = "Tab C"),
)

private val disabledTabs: List<TabItem> = listOf(
    TabItem(label = "Active"),
    TabItem(label = "Also Active"),
    TabItem(
        label = "Disabled",
        isDisabled = true,
    ),
)

private val manyTabs: List<TabItem> = listOf(
    TabItem(label = "Dashboard"),
    TabItem(label = "Analytics"),
    TabItem(label = "Reports"),
    TabItem(label = "Settings"),
    TabItem(label = "Users"),
    TabItem(label = "Activity"),
    TabItem(label = "Notifications"),
)

private val interactiveTabs: List<TabItem> = listOf(
    TabItem(label = "Account"),
    TabItem(label = "Privacy"),
    TabItem(label = "Notifications"),
)

private val interactiveTabsContent: List<String> = listOf(
    "Manage your account settings and preferences.",
    "Control your privacy settings and data.",
    "Configure notification preferences and alerts.",
)

private val twoTabs: List<TabItem> = listOf(
    TabItem(label = "Login"),
    TabItem(label = "Sign Up"),
)

@Composable
internal fun TabsDisplay() {
    SampleScreenDisplayLazyColumn(
        title = "Tabs",
    ) {
        tabsSection(
            key = "basic",
            title = "Basic Tabs",
            tabs = basicTabs,
            isFirst = true,
        )
        tabsSection(
            key = "with-icons",
            title = "Tabs with Icons",
            tabs = iconTabs,
        )
        tabsSection(
            key = "stretch",
            title = "Stretch Mode",
            tabs = stretchTabs,
            itemsSize = TabsItemSize.Stretch,
        )
        tabsSection(
            key = "disabled",
            title = "Disabled Tab",
            tabs = disabledTabs,
        )
        tabsSection(
            key = "many",
            title = "Many Tabs (Scrollable)",
            tabs = manyTabs,
        )
        interactiveTabsSection()
        tabsSection(
            key = "two",
            title = "Two Tabs",
            tabs = twoTabs,
        )
        item(key = "bottom-spacer") {
            Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing500))
        }
    }
}

private fun LazyListScope.tabsSection(
    key: String,
    title: String,
    tabs: List<TabItem>,
    itemsSize: TabsItemSize = TabsItemSize.Hug,
    isFirst: Boolean = false,
) {
    sectionTitle(
        key = key,
        title = title,
        isFirst = isFirst,
    )
    item(key = key) {
        var selectedTab by rememberSaveable { mutableIntStateOf(value = 0) }

        LemonadeUi.Tabs(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { index -> selectedTab = index },
            itemsSize = itemsSize,
        )
    }
}

private fun LazyListScope.interactiveTabsSection() {
    sectionTitle(
        key = "interactive",
        title = "Interactive with Content",
    )
    item(key = "interactive") {
        var selectedTab by rememberSaveable { mutableIntStateOf(value = 0) }

        LemonadeUi.Tabs(
            tabs = interactiveTabs,
            selectedIndex = selectedTab,
            onTabSelected = { index -> selectedTab = index },
        )

        Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing400))

        LemonadeUi.Card {
            LemonadeUi.Text(
                text = interactiveTabsContent[selectedTab],
                textStyle = LemonadeTheme.typography.bodyMediumRegular,
                color = LemonadeTheme.colors.content.contentSecondary,
                modifier = Modifier.padding(all = LemonadeTheme.spaces.spacing400),
            )
        }
    }
}

private fun LazyListScope.sectionTitle(
    key: String,
    title: String,
    isFirst: Boolean = false,
) {
    item(key = "$key-title") {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            modifier = Modifier.padding(
                top = if (isFirst) {
                    LemonadeTheme.spaces.spacing400
                } else {
                    LemonadeTheme.spaces.spacing500
                },
                bottom = LemonadeTheme.spaces.spacing200,
            ),
        )
    }
}
