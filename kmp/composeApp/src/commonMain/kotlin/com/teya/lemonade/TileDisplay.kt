package com.teya.lemonade

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeTileOrientation
import com.teya.lemonade.core.LemonadeTileVariant

@Suppress("LongMethod")
@Composable
internal fun TileDisplay() {
    val toasts = LocalLemonadeToastState.current

    var isFilledSelected by rememberSaveable { mutableStateOf(value = true) }
    var isOutlinedSelected by rememberSaveable { mutableStateOf(value = true) }
    var isHorizontalFilledSelected by rememberSaveable { mutableStateOf(value = true) }
    var isHorizontalOutlinedSelected by rememberSaveable { mutableStateOf(value = false) }

    SampleScreenDisplayLazyColumn(title = "Tile") {
        item(key = "variants") {
            TileSection(title = "Variants") {
                TileRow {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Tile(
                            label = "Filled",
                            icon = LemonadeIcons.Heart,
                            variant = LemonadeTileVariant.Filled,
                        )
                        LemonadeUi.Text(
                            text = "Filled",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
                    ) {
                        LemonadeUi.Tile(
                            label = "Outlined",
                            icon = LemonadeIcons.Star,
                            variant = LemonadeTileVariant.Outlined,
                        )
                        LemonadeUi.Text(
                            text = "Outlined",
                            textStyle = LemonadeTheme.typography.bodySmallRegular,
                        )
                    }
                }
            }
        }

        item(key = "selected") {
            TileSection(title = "Selected") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Filled",
                        icon = LemonadeIcons.CircleCheck,
                        variant = LemonadeTileVariant.Filled,
                        isSelected = isFilledSelected,
                        onClick = { isFilledSelected = !isFilledSelected },
                    )
                    LemonadeUi.Tile(
                        label = "Outlined",
                        icon = LemonadeIcons.CircleCheck,
                        variant = LemonadeTileVariant.Outlined,
                        isSelected = isOutlinedSelected,
                        onClick = { isOutlinedSelected = !isOutlinedSelected },
                    )
                }
            }
        }

        item(key = "support-text") {
            TileSection(title = "Support Text") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Filled",
                        icon = LemonadeIcons.Heart,
                        variant = LemonadeTileVariant.Filled,
                        supportText = "Support text",
                    )

                    LemonadeUi.Tile(
                        label = "Outlined",
                        icon = LemonadeIcons.Star,
                        variant = LemonadeTileVariant.Outlined,
                        supportText = "Support text",
                    )
                }
            }
        }

        item(key = "top-accessory") {
            TileSection(title = "Top Accessory") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Accessory",
                        icon = LemonadeIcons.Heart,
                        variant = LemonadeTileVariant.Filled,
                        topAccessory = {
                            LemonadeUi.Icon(
                                icon = LemonadeIcons.CircleInfo,
                                size = LemonadeAssetSize.Small,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }

        item(key = "leading-slot") {
            TileSection(title = "Leading Slot") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Custom",
                        variant = LemonadeTileVariant.Filled,
                        leadingSlot = {
                            LemonadeUi.Icon(
                                icon = LemonadeIcons.ShoppingBag,
                                size = LemonadeAssetSize.Medium,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }

        item(key = "interactive") {
            TileSection(title = "Interactive") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Tap me",
                        icon = LemonadeIcons.HandCoins,
                        onClick = { toasts.show(label = "Tile tapped!") },
                        variant = LemonadeTileVariant.Filled,
                    )

                    LemonadeUi.Tile(
                        label = "Click",
                        icon = LemonadeIcons.FingerPrint,
                        onClick = { toasts.show(label = "Click!") },
                        variant = LemonadeTileVariant.Outlined,
                    )
                }
            }
        }

        item(key = "disabled") {
            TileSection(title = "Disabled") {
                TileRow {
                    LemonadeUi.Tile(
                        label = "Disabled",
                        icon = LemonadeIcons.Padlock,
                        enabled = false,
                        variant = LemonadeTileVariant.Filled,
                    )

                    LemonadeUi.Tile(
                        label = "Disabled",
                        icon = LemonadeIcons.Padlock,
                        enabled = false,
                        variant = LemonadeTileVariant.Outlined,
                    )
                }
            }
        }

        item(key = "horizontal-variants") {
            TileSection(title = "Horizontal / Variants") {
                TileColumn {
                    LemonadeUi.Tile(
                        label = "Filled",
                        icon = LemonadeIcons.Heart,
                        variant = LemonadeTileVariant.Filled,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                    LemonadeUi.Tile(
                        label = "Outlined",
                        icon = LemonadeIcons.Star,
                        variant = LemonadeTileVariant.Outlined,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                }
            }
        }

        item(key = "horizontal-selected") {
            TileSection(title = "Horizontal / Selected") {
                TileColumn {
                    LemonadeUi.Tile(
                        label = "Filled",
                        icon = LemonadeIcons.CircleCheck,
                        variant = LemonadeTileVariant.Filled,
                        isSelected = isHorizontalFilledSelected,
                        onClick = { isHorizontalFilledSelected = !isHorizontalFilledSelected },
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                    LemonadeUi.Tile(
                        label = "Outlined",
                        icon = LemonadeIcons.CircleCheck,
                        variant = LemonadeTileVariant.Outlined,
                        isSelected = isHorizontalOutlinedSelected,
                        onClick = { isHorizontalOutlinedSelected = !isHorizontalOutlinedSelected },
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                }
            }
        }

        item(key = "horizontal-support-text") {
            TileSection(title = "Horizontal / Support Text") {
                TileColumn {
                    LemonadeUi.Tile(
                        label = "Transfer",
                        icon = LemonadeIcons.ArrowLeftRight,
                        supportText = "Send money instantly",
                        variant = LemonadeTileVariant.Filled,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                    LemonadeUi.Tile(
                        label = "Pay",
                        icon = LemonadeIcons.Card,
                        supportText = "Pay with card",
                        variant = LemonadeTileVariant.Outlined,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                }
            }
        }

        item(key = "horizontal-leading-slot") {
            TileSection(title = "Horizontal / Leading Slot") {
                LemonadeUi.Tile(
                    label = "Custom slot",
                    variant = LemonadeTileVariant.Filled,
                    orientation = LemonadeTileOrientation.Horizontal,
                    leadingSlot = {
                        LemonadeUi.Icon(
                            icon = LemonadeIcons.ShoppingBag,
                            size = LemonadeAssetSize.Medium,
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        item(key = "horizontal-disabled") {
            TileSection(title = "Horizontal / Disabled") {
                TileColumn {
                    LemonadeUi.Tile(
                        label = "Disabled",
                        icon = LemonadeIcons.Padlock,
                        enabled = false,
                        variant = LemonadeTileVariant.Filled,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                    LemonadeUi.Tile(
                        label = "Disabled",
                        icon = LemonadeIcons.Padlock,
                        enabled = false,
                        variant = LemonadeTileVariant.Outlined,
                        orientation = LemonadeTileOrientation.Horizontal,
                    )
                }
            }
        }

        item(key = "use-case-menu-list") {
            TileSection(title = "Use Case: Menu List") {
                TileColumn {
                    menuListActions.forEach { action ->
                        LemonadeUi.Tile(
                            label = action.label,
                            icon = action.icon,
                            supportText = action.supportText,
                            onClick = { toasts.show(label = "${action.label} tapped") },
                            variant = LemonadeTileVariant.Filled,
                            orientation = LemonadeTileOrientation.Horizontal,
                        )
                    }
                }
            }
        }

        item(key = "use-case-quick-actions") {
            TileSection(title = "Use Case: Quick Actions") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    quickActions
                        .chunked(size = QUICK_ACTIONS_PER_ROW)
                        .forEach { rowActions ->
                            TileRow {
                                rowActions.forEach { action ->
                                    LemonadeUi.Tile(
                                        label = action.label,
                                        icon = action.icon,
                                        onClick = { toasts.show(label = "${action.label} tapped") },
                                        variant = LemonadeTileVariant.Filled,
                                    )
                                }
                            }
                        }
                }
            }
        }

        item(key = "use-case-dashboard") {
            TileSection(title = "Use Case: Dashboard") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
                ) {
                    dashboardActions
                        .chunked(size = DASHBOARD_ACTIONS_PER_ROW)
                        .forEach { rowActions ->
                            TileRow {
                                rowActions.forEach { action ->
                                    LemonadeUi.Tile(
                                        label = action.label,
                                        icon = action.icon,
                                        onClick = { toasts.show(label = "${action.label} tapped") },
                                        variant = LemonadeTileVariant.Outlined,
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

private const val QUICK_ACTIONS_PER_ROW = 3
private const val DASHBOARD_ACTIONS_PER_ROW = 2

private data class TileAction(
    val label: String,
    val icon: LemonadeIcons,
    val supportText: String? = null,
)

private val menuListActions: List<TileAction> = listOf(
    TileAction(
        label = "Transfer",
        icon = LemonadeIcons.ArrowLeftRight,
        supportText = "Send money instantly",
    ),
    TileAction(
        label = "Pay",
        icon = LemonadeIcons.Card,
        supportText = "Pay with card",
    ),
    TileAction(
        label = "Top Up",
        icon = LemonadeIcons.Plus,
        supportText = "Add funds to your account",
    ),
    TileAction(
        label = "Statements",
        icon = LemonadeIcons.Chart,
        supportText = "View your transactions",
    ),
)

private val quickActions: List<TileAction> = listOf(
    TileAction(
        label = "Transfer",
        icon = LemonadeIcons.ArrowLeftRight,
    ),
    TileAction(
        label = "Pay",
        icon = LemonadeIcons.Card,
    ),
    TileAction(
        label = "Request",
        icon = LemonadeIcons.Download,
    ),
    TileAction(
        label = "Scan",
        icon = LemonadeIcons.QrCode,
    ),
    TileAction(
        label = "Top Up",
        icon = LemonadeIcons.Plus,
    ),
    TileAction(
        label = "More",
        icon = LemonadeIcons.EllipsisHorizontal,
    ),
)

private val dashboardActions: List<TileAction> = listOf(
    TileAction(
        label = "Orders",
        icon = LemonadeIcons.ShoppingBag,
    ),
    TileAction(
        label = "Inventory",
        icon = LemonadeIcons.Package,
    ),
    TileAction(
        label = "Reports",
        icon = LemonadeIcons.Chart,
    ),
    TileAction(
        label = "Settings",
        icon = LemonadeIcons.Gear,
    ),
)

@Composable
private fun TileRow(content: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing400),
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        content()
    }
}

@Composable
private fun TileColumn(content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
    ) {
        content()
    }
}

@Composable
private fun TileSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing300),
        modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
    ) {
        LemonadeUi.Text(
            text = title,
            textStyle = LemonadeTheme.typography.headingXSmall,
            color = LemonadeTheme.colors.content.contentSecondary,
        )
        content()
    }
}
