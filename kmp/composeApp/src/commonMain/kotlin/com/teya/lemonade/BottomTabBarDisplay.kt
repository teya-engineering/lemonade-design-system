package com.teya.lemonade

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeIconButtonShape
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow

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

private val fiveTabItems: List<BottomTabBarItem> = fourTabItems + BottomTabBarItem(
    label = "Settings",
    icon = LemonadeIcons.Gear,
    selectedIcon = LemonadeIcons.GearSolid,
)

private val threeTabItems: List<BottomTabBarItem> = fourTabItems.take(n = 3)

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
        contentHorizontalPadding = 0.dp,
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
            key = "five-items-fab",
            title = "Five items with an action",
            items = fiveTabItems,
            initialIndex = 1,
            withAction = true,
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
    withAction: Boolean = false,
) {
    item(key = "$key-title") {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            modifier = Modifier.padding(
                start = LemonadeTheme.spaces.spacing400,
                end = LemonadeTheme.spaces.spacing400,
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

        var actionOpen by rememberSaveable { mutableStateOf(value = false) }
        val actionRotation by animateFloatAsState(targetValue = if (actionOpen) 45f else 0f)

        LemonadeUi.BottomTabBar(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = { index -> selectedIndex = index },
            floatingActionButton = if (withAction) {
                {
                    LemonadeUi.IconButton(
                        icon = LemonadeIcons.Plus,
                        contentDescription = "Add",
                        onClick = { actionOpen = !actionOpen },
                        type = LemonadeButtonType.Ghost,
                        size = LemonadeButtonSize.Large,
                        shape = LemonadeIconButtonShape.Circular,
                        modifier = Modifier
                            .lemonadeShadow(shadow = LemonadeShadow.Xlarge, shape = CircleShape)
                            .background(color = LemonadeTheme.colors.background.bgDefault, shape = CircleShape)
                            .rotate(degrees = actionRotation),
                    )
                }
            } else {
                null
            },
        )
    }
}
