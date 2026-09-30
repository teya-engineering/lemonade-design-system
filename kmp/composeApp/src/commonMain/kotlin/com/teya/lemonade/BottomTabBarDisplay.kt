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
// parameter type, so an inline `listOf(...)` would make BottomTabBar re-compose on every tap.
private val fourTabItems: List<BottomTabBarItem> = listOf(
    BottomTabBarItem(
        label = "Home",
        icon = LemonadeIcons.BrandTeyaSymbol,
    ),
    BottomTabBarItem(
        label = "Sales",
        icon = LemonadeIcons.ChartStats,
        selectedIcon = LemonadeIcons.ChartStatsSolid,
    ),
    BottomTabBarItem(
        label = "Money",
        icon = LemonadeIcons.Wallet,
        selectedIcon = LemonadeIcons.WalletSolid,
    ),
    BottomTabBarItem(
        label = "Teya AI",
        icon = LemonadeIcons.SparklesSoft,
        selectedIcon = LemonadeIcons.SparklesSoftSolid,
    ),
)

private val threeTabItems: List<BottomTabBarItem> = fourTabItems.take(n = 3)

private val longLabelTabItems: List<BottomTabBarItem> = listOf(
    BottomTabBarItem(
        label = "Bank transfers",
        icon = LemonadeIcons.Wallet,
        selectedIcon = LemonadeIcons.WalletSolid,
    ),
    BottomTabBarItem(
        label = "Card payments",
        icon = LemonadeIcons.ChartStats,
        selectedIcon = LemonadeIcons.ChartStatsSolid,
    ),
    BottomTabBarItem(
        label = "Notifications",
        icon = LemonadeIcons.BrandTeyaSymbol,
    ),
    BottomTabBarItem(
        label = "Subscriptions",
        icon = LemonadeIcons.SparklesSoft,
        selectedIcon = LemonadeIcons.SparklesSoftSolid,
    ),
)

private val twoTabItems: List<BottomTabBarItem> = listOf(
    BottomTabBarItem(
        label = "Home",
        icon = LemonadeIcons.BrandTeyaSymbol,
    ),
    BottomTabBarItem(
        label = "Money",
        icon = LemonadeIcons.Wallet,
        selectedIcon = LemonadeIcons.WalletSolid,
    ),
)

@Composable
internal fun BottomTabBarDisplay() {
    SampleScreenDisplayLazyColumn(
        title = "BottomTabBar",
    ) {
        bottomTabBarSection(
            key = "default",
            title = "Default (4 items)",
            items = fourTabItems,
            initialIndex = 0,
            isFirst = true,
        )
        bottomTabBarSection(
            key = "interactive",
            title = "Interactive",
            items = fourTabItems,
            initialIndex = 1,
        )
        bottomTabBarSection(
            key = "three-items",
            title = "Three items",
            items = threeTabItems,
            initialIndex = 0,
        )
        bottomTabBarSection(
            key = "two-items",
            title = "Two items",
            items = twoTabItems,
            initialIndex = 0,
        )
        bottomTabBarSection(
            key = "long-labels",
            title = "Long labels (drops to icons)",
            items = longLabelTabItems,
            initialIndex = 0,
        )
        item(key = "bottom-spacer") {
            Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing500))
        }
    }
}

private fun LazyListScope.bottomTabBarSection(
    key: String,
    title: String,
    items: List<BottomTabBarItem>,
    initialIndex: Int,
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
    item(key = key) {
        var selectedIndex by rememberSaveable { mutableIntStateOf(value = initialIndex) }

        LemonadeUi.BottomTabBar(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = { index -> selectedIndex = index },
        )
    }
}
