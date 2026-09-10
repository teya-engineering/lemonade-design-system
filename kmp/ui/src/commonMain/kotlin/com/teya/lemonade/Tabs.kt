package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons

/**
 * A single tab: its label, optional icon, and disabled state.
 *
 * @param label text shown on the tab
 * @param icon [LemonadeIcons] shown before the label
 * @param isDisabled `true` when the tab is dimmed and ignores input
 */
public data class TabItem(
    val label: String,
    val icon: LemonadeIcons? = null,
    val isDisabled: Boolean = false,
)

/**
 * Sizing strategy for the tab items in [LemonadeUi.Tabs].
 */
public enum class TabsItemSize {
    /** Each tab hugs its content. Scrollable when items overflow. */
    Hug,

    /** Tabs stretch to fill available width equally. */
    Stretch,
}

/**
 * A horizontal scrollable row of tab items where one is selected at a time.
 * The selected tab has a brand-colored bottom indicator line.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.Tabs(
 *     tabs = listOf(
 *         TabItem(label = "Overview"),
 *         TabItem(label = "Details", icon = LemonadeIcons.Info),
 *         TabItem(label = "Reviews"),
 *     ),
 *     selectedIndex = 0,
 *     onTabSelected = { index -> /* ... */ },
 * )
 * ```
 *
 * @param tabs [TabItem]s to show; an empty list renders nothing, so a host whose tabs are still
 *   loading can pass one straight through
 * @param selectedIndex index of the selected tab, clamped into [tabs]' indices — an index left
 *   over from a longer list selects the nearest tab instead of failing
 * @param onTabSelected callback run with the index of the tab the user selected
 * @param modifier [Modifier] applied to the root container
 * @param itemsSize sizing strategy for the tab items
 */
@Composable
public fun LemonadeUi.Tabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemsSize: TabsItemSize = TabsItemSize.Hug,
) {
    CoreTabs(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onTabSelected = onTabSelected,
        modifier = modifier,
        itemsSize = itemsSize,
    )
}

private const val INDICATOR_ANIMATION_DURATION_MS = 250
private const val FADE_WIDTH_FRACTION = 0.15f

@Composable
internal fun CoreTabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemsSize: TabsItemSize = TabsItemSize.Hug,
) {
    // Not even the separator: a bare rule under an empty strip reads as a rendering glitch.
    if (tabs.isEmpty()) {
        return
    }

    // The index and the list arrive separately, so a shrunk list can leave a stale index behind.
    val resolvedIndex = selectedIndex.coerceIn(
        minimumValue = 0,
        maximumValue = tabs.lastIndex,
    )

    val density = LocalDensity.current
    val tabWidths = remember { mutableStateMapOf<Int, Dp>() }
    val tabOffsets = remember { mutableStateMapOf<Int, Dp>() }
    val contentWidths = remember { mutableStateMapOf<Int, Dp>() }

    LaunchedEffect(key1 = tabs.size) {
        tabWidths.keys.removeAll { key -> key >= tabs.size }
        tabOffsets.keys.removeAll { key -> key >= tabs.size }
        contentWidths.keys.removeAll { key -> key >= tabs.size }
    }

    var hasInitialMeasurement by remember { mutableStateOf(value = false) }
    val animationSpec = if (hasInitialMeasurement) {
        tween<Dp>(durationMillis = INDICATOR_ANIMATION_DURATION_MS)
    } else {
        tween<Dp>(durationMillis = 0)
    }

    LaunchedEffect(key1 = contentWidths[resolvedIndex]) {
        if (contentWidths[resolvedIndex] != null && !hasInitialMeasurement) {
            hasInitialMeasurement = true
        }
    }

    val indicatorWidth by animateDpAsState(
        targetValue = contentWidths[resolvedIndex]
            ?: tabWidths[resolvedIndex]
            ?: 0.dp,
        animationSpec = animationSpec,
    )
    val selectedTabWidth = tabWidths[resolvedIndex]
        ?: 0.dp
    val selectedContentWidth = contentWidths[resolvedIndex]
        ?: selectedTabWidth
    val selectedTabOffset = tabOffsets[resolvedIndex]
        ?: 0.dp
    val indicatorOffset by animateDpAsState(
        targetValue = selectedTabOffset + (selectedTabWidth - selectedContentWidth) / 2,
        animationSpec = animationSpec,
    )

    val indicatorHeight = LocalBorderWidths.current.base.border75
    val indicatorColor = LocalColors.current.background.bgBrandHigh
    val separatorHeight = LocalBorderWidths.current.base.border25
    val separatorColor = LocalColors.current.border.borderNeutralLow

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        when (itemsSize) {
            TabsItemSize.Hug -> {
                HugModeTabs(
                    tabs = tabs,
                    selectedIndex = resolvedIndex,
                    onTabSelected = onTabSelected,
                    density = density,
                    tabWidths = tabWidths,
                    tabOffsets = tabOffsets,
                    contentWidths = contentWidths,
                    indicatorWidth = indicatorWidth,
                    indicatorOffset = indicatorOffset,
                    indicatorHeight = indicatorHeight,
                    indicatorColor = indicatorColor,
                )
            }

            TabsItemSize.Stretch -> {
                StretchModeTabs(
                    tabs = tabs,
                    selectedIndex = resolvedIndex,
                    onTabSelected = onTabSelected,
                    density = density,
                    tabWidths = tabWidths,
                    tabOffsets = tabOffsets,
                    contentWidths = contentWidths,
                    indicatorWidth = indicatorWidth,
                    indicatorOffset = indicatorOffset,
                    indicatorHeight = indicatorHeight,
                    indicatorColor = indicatorColor,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = separatorHeight)
                .background(color = separatorColor),
        )
    }
}

@Composable
private fun HugModeTabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    density: Density,
    tabWidths: SnapshotStateMap<Int, Dp>,
    tabOffsets: SnapshotStateMap<Int, Dp>,
    contentWidths: SnapshotStateMap<Int, Dp>,
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorHeight: Dp,
    indicatorColor: Color,
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = selectedIndex) {
        val offset = tabOffsets[selectedIndex]
        val width = tabWidths[selectedIndex]
        if (offset != null && width != null) {
            val offsetPx = with(density) { offset.roundToPx() }
            val widthPx = with(density) { width.roundToPx() }
            val viewportWidth = scrollState.viewportSize
            val currentScroll = scrollState.value

            val tabEnd = offsetPx + widthPx
            val visibleEnd = currentScroll + viewportWidth

            if (offsetPx < currentScroll) {
                scrollState.animateScrollTo(value = offsetPx)
            } else if (tabEnd > visibleEnd) {
                scrollState.animateScrollTo(value = tabEnd - viewportWidth)
            }
        }
    }

    val isScrollable = scrollState.maxValue > 0
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                other = if (isScrollable) {
                    Modifier
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }.drawWithContent {
                            drawContent()
                            if (scrollState.canScrollForward) {
                                val fadeWidthPx = size.width * FADE_WIDTH_FRACTION
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black,
                                            Color.Transparent,
                                        ),
                                        startX = size.width - fadeWidthPx,
                                        endX = size.width,
                                    ),
                                    blendMode = BlendMode.DstIn,
                                )
                            }
                        }
                } else {
                    Modifier
                },
            ),
    ) {
        ScrollableTabsWithIndicator(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onTabSelected = onTabSelected,
            density = density,
            scrollState = scrollState,
            tabWidths = tabWidths,
            tabOffsets = tabOffsets,
            contentWidths = contentWidths,
            indicatorWidth = indicatorWidth,
            indicatorOffset = indicatorOffset,
            indicatorHeight = indicatorHeight,
            indicatorColor = indicatorColor,
        )
    }
}

@Composable
private fun ScrollableTabsWithIndicator(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    density: Density,
    scrollState: ScrollState,
    tabWidths: SnapshotStateMap<Int, Dp>,
    tabOffsets: SnapshotStateMap<Int, Dp>,
    contentWidths: SnapshotStateMap<Int, Dp>,
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorHeight: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(state = scrollState),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            tabs.forEachIndexed { index, tab ->
                TabItemContent(
                    tab = tab,
                    isSelected = index == selectedIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            tabWidths[index] = with(density) {
                                coordinates.size.width.toDp()
                            }
                            tabOffsets[index] = with(density) {
                                coordinates.positionInParent().x.toDp()
                            }
                        },
                    onContentPositioned = { coordinates ->
                        contentWidths[index] = with(density) {
                            coordinates.size.width.toDp()
                        }
                    },
                )
            }
        }

        if (tabWidths.containsKey(selectedIndex)) {
            TabIndicator(
                offset = indicatorOffset,
                width = indicatorWidth,
                height = indicatorHeight,
                color = indicatorColor,
            )
        }
    }
}

@Composable
private fun BoxScope.TabIndicator(
    offset: Dp,
    width: Dp,
    height: Dp,
    color: Color,
) {
    Box(
        modifier = Modifier
            .align(alignment = Alignment.BottomStart)
            .offset(x = offset)
            .width(width = width)
            .height(height = height)
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topStart = 2.dp,
                    topEnd = 2.dp,
                ),
            ),
    )
}

@Composable
private fun StretchModeTabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    density: Density,
    tabWidths: SnapshotStateMap<Int, Dp>,
    tabOffsets: SnapshotStateMap<Int, Dp>,
    contentWidths: SnapshotStateMap<Int, Dp>,
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorHeight: Dp,
    indicatorColor: Color,
) {
    Box {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, tab ->
                Box(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .onGloballyPositioned { coordinates ->
                            tabWidths[index] = with(density) {
                                coordinates.size.width.toDp()
                            }
                            tabOffsets[index] = with(density) {
                                coordinates.positionInParent().x.toDp()
                            }
                        },
                ) {
                    TabItemContent(
                        tab = tab,
                        isSelected = index == selectedIndex,
                        onClick = { onTabSelected(index) },
                        modifier = Modifier.fillMaxWidth(),
                        onContentPositioned = { coordinates ->
                            contentWidths[index] = with(density) {
                                coordinates.size.width.toDp()
                            }
                        },
                    )
                }
            }
        }

        if (tabWidths.containsKey(selectedIndex)) {
            TabIndicator(
                offset = indicatorOffset,
                width = indicatorWidth,
                height = indicatorHeight,
                color = indicatorColor,
            )
        }
    }
}

@Composable
private fun TabItemContent(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onContentPositioned: ((androidx.compose.ui.layout.LayoutCoordinates) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val contentBrandColor = LocalColors.current.content.contentBrandHigh
    val contentSecondaryColor = LocalColors.current.content.contentSecondary
    val contentPrimaryColor = LocalColors.current.content.contentPrimary
    val bgSubtleInteractive = LocalColors.current.interaction.bgSubtleInteractive
    val opacityDisabled = LocalOpacities.current.state.opacityDisabled
    val opacityPressed = LocalOpacities.current.state.opacityPressed

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            contentBrandColor
        } else {
            contentSecondaryColor
        },
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            contentBrandColor
        } else {
            contentPrimaryColor
        },
    )

    val textStyle = if (isSelected) {
        LocalTypographies.current.bodySmallSemiBold
    } else {
        LocalTypographies.current.bodySmallMedium
    }

    val isInteracting = (isHovered || isPressed) && !tab.isDisabled
    val wrapperBackgroundColor by animateColorAsState(
        targetValue = if (isInteracting) {
            bgSubtleInteractive
        } else {
            Color.Transparent
        },
    )

    val wrapperAlpha = if (isPressed && !tab.isDisabled) {
        opacityPressed
    } else {
        1f
    }

    val tabAlpha = if (tab.isDisabled) {
        opacityDisabled
    } else {
        1f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .alpha(alpha = tabAlpha)
            .heightIn(min = LocalSizes.current.size1100)
            .clickable(
                onClick = onClick,
                role = Role.Tab,
                enabled = !tab.isDisabled,
                interactionSource = interactionSource,
                indication = null,
            ).padding(horizontal = LocalSpaces.current.spacing200),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = LocalSpaces.current.spacing100),
            modifier = Modifier
                .alpha(alpha = wrapperAlpha)
                .background(
                    color = wrapperBackgroundColor,
                    shape = LocalShapes.current.radius150,
                ).padding(
                    horizontal = LocalSpaces.current.spacing200,
                    vertical = LocalSpaces.current.spacing100,
                ).then(
                    other = if (onContentPositioned != null) {
                        Modifier.onGloballyPositioned(onContentPositioned)
                    } else {
                        Modifier
                    },
                ),
        ) {
            if (tab.icon != null) {
                LemonadeUi.Icon(
                    icon = tab.icon,
                    contentDescription = null,
                    size = LemonadeAssetSize.Small,
                    tint = iconColor,
                )
            }

            // Reserve space with the heavier font to prevent layout shift on selection.
            Box {
                LemonadeUi.Text(
                    text = tab.label,
                    textStyle = LocalTypographies.current.bodySmallSemiBold,
                    color = Color.Transparent,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                LemonadeUi.Text(
                    text = tab.label,
                    textStyle = textStyle,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Suppress("UnusedPrivateMember")
@LemonadePreview
@Composable
private fun TabsPreview() {
    LemonadeUi.Tabs(
        tabs = listOf(
            TabItem(label = "Overview"),
            TabItem(label = "Details"),
            TabItem(label = "Reviews"),
        ),
        selectedIndex = 1,
        onTabSelected = { },
    )
}

@Suppress("UnusedPrivateMember")
@LemonadePreview
@Composable
private fun TabsWithIconsPreview() {
    LemonadeUi.Tabs(
        tabs = listOf(
            TabItem(
                label = "Overview",
                icon = LemonadeIcons.Home,
            ),
            TabItem(
                label = "Details",
                icon = LemonadeIcons.CircleInfo,
            ),
            TabItem(
                label = "Reviews",
                icon = LemonadeIcons.Star,
            ),
        ),
        selectedIndex = 0,
        onTabSelected = { },
    )
}

@Suppress("UnusedPrivateMember")
@LemonadePreview
@Composable
private fun TabsStretchPreview() {
    LemonadeUi.Tabs(
        tabs = listOf(
            TabItem(label = "Tab A"),
            TabItem(label = "Tab B"),
            TabItem(label = "Tab C"),
        ),
        selectedIndex = 0,
        onTabSelected = { },
        itemsSize = TabsItemSize.Stretch,
    )
}

@Suppress("UnusedPrivateMember")
@LemonadePreview
@Composable
private fun TabsManyItemsPreview() {
    LemonadeUi.Tabs(
        tabs = listOf(
            TabItem(label = "Dashboard"),
            TabItem(label = "Analytics"),
            TabItem(label = "Reports"),
            TabItem(label = "Settings"),
            TabItem(label = "Users"),
            TabItem(label = "Activity"),
        ),
        selectedIndex = 2,
        onTabSelected = { },
    )
}
