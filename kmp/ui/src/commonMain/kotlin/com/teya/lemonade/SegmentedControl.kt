package com.teya.lemonade

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeSegmentedControlSize
import com.teya.lemonade.core.LemonadeShadow
import com.teya.lemonade.core.LemonadeTextStyle
import com.teya.lemonade.core.TabButtonProperties

/**
 * Selects a single option from two or more horizontal segments.
 *
 * Ideal for toggling between views or filtering content.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SegmentedControl(
 *     onTabSelected = { tabIndex -> /* ... */ },
 *     selectedTab = selectedTabIndex,
 *     properties = listOf(
 *         TabButtonProperties.labelAndIcon(label = "Tab 1", icon = LemonadeIcons.Heart),
 *         TabButtonProperties.labelAndIcon(label = "Tab 2", icon = LemonadeIcons.Laptop),
 *         TabButtonProperties.label(label = "Tab 3"),
 *     ),
 * )
 * ```
 *
 * @param properties the tab buttons to draw
 * @param selectedTab index of the selected tab
 * @param onTabSelected called with the index of the newly selected tab
 * @param size size of the control; defaults to [LemonadeSegmentedControlSize.Large]
 * @param modifier optional [Modifier] applied to the root container
 */
@Composable
public fun LemonadeUi.SegmentedControl(
    properties: List<TabButtonProperties>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    size: LemonadeSegmentedControlSize = LemonadeSegmentedControlSize.Large,
    modifier: Modifier = Modifier,
) {
    CoreSegmentedControl(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        modifier = modifier,
        tabCount = properties.size,
        size = size,
        content = { index ->
            val property = properties[index]
            val contentColor = if (selectedTab == index) {
                LocalColors.current.content.contentPrimary
            } else {
                LocalColors.current.content.contentSecondary
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = size.buttonContentGap(),
                ),
            ) {
                property.icon?.let { icon ->
                    LemonadeUi.Icon(
                        icon = icon,
                        contentDescription = property.label,
                        size = LemonadeAssetSize.Small,
                        tint = contentColor,
                    )
                }

                property.label?.let { label ->
                    LemonadeUi.Text(
                        text = label,
                        textStyle = size.textStyle(),
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor,
                    )
                }
            }
        },
    )
}

@Suppress("LongMethod")
@Composable
internal fun CoreSegmentedControl(
    content: @Composable BoxScope.(Int) -> Unit,
    tabCount: Int,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    size: LemonadeSegmentedControlSize = LemonadeSegmentedControlSize.Large,
    modifier: Modifier = Modifier,
) {
    require(
        value = tabCount > 0,
        lazyMessage = { "Tab count should be greater than zero." },
    )

    val density = LocalDensity.current
    val pillShape = RoundedCornerShape(percent = 50)
    val tabWidths = remember { mutableStateMapOf<Int, Dp>() }
    val tabOffsets = remember { mutableStateMapOf<Int, Dp>() }
    val pressedTabIndex = remember { mutableStateMapOf<Int, Boolean>() }
    val stretchAmount = 8.dp

    val selectedIndex = selectedTab.coerceIn(
        minimumValue = 0,
        maximumValue = tabCount - 1,
    )

    val pressedNonSelectedIndex by remember {
        derivedStateOf {
            pressedTabIndex.entries
                .firstOrNull { entry ->
                    entry.value && entry.key != selectedIndex
                }?.key
        }
    }

    val hasMeasurements by remember {
        derivedStateOf {
            tabWidths.containsKey(selectedIndex) && tabOffsets.containsKey(selectedIndex)
        }
    }

    val baseWidth = tabWidths[selectedIndex]
        ?: 0.dp
    val baseOffset = tabOffsets[selectedIndex]
        ?: 0.dp

    val targetWidth: Dp
    val targetOffset: Dp
    if (pressedNonSelectedIndex != null) {
        val pressedOffset = tabOffsets[pressedNonSelectedIndex]
            ?: baseOffset
        if (pressedOffset > baseOffset) {
            targetWidth = baseWidth + stretchAmount
            targetOffset = baseOffset
        } else {
            targetWidth = baseWidth + stretchAmount
            targetOffset = baseOffset - stretchAmount
        }
    } else {
        targetWidth = baseWidth
        targetOffset = baseOffset
    }

    val hasInitialMeasurement = remember { booleanArrayOf(false) }
    val animationSpec = if (hasInitialMeasurement[0]) IndicatorSpringSpec else snap()

    SideEffect {
        if (hasMeasurements) hasInitialMeasurement[0] = true
    }

    val indicatorWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = animationSpec,
    )
    val indicatorOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = animationSpec,
    )

    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = size.buttonMinWidth(),
                minHeight = size.buttonMinHeight(),
            ).height(height = size.containerHeight())
            .background(
                color = LocalColors.current.background.bgElevated,
                shape = pillShape,
            ).clip(shape = pillShape)
            .padding(all = size.containerPadding()),
    ) {
        if (hasMeasurements) {
            SlidingIndicator(
                indicatorOffset = indicatorOffset,
                indicatorWidth = indicatorWidth,
                density = density,
                pillShape = pillShape,
            )
        }

        SegmentedControlTabRow(
            tabCount = tabCount,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            size = size,
            density = density,
            pillShape = pillShape,
            tabWidths = tabWidths,
            tabOffsets = tabOffsets,
            pressedTabIndex = pressedTabIndex,
            content = content,
        )
    }
}

@Composable
private fun SlidingIndicator(
    indicatorOffset: Dp,
    indicatorWidth: Dp,
    density: Density,
    pillShape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = with(density) { indicatorOffset.roundToPx() },
                    y = 0,
                )
            }.width(width = indicatorWidth)
            .fillMaxHeight()
            .animateLemonadeShadow(
                shape = pillShape,
                shadow = LemonadeShadow.Xsmall,
            ).background(
                color = LocalColors.current.background.bgDefault,
                shape = pillShape,
            ),
    )
}

@Composable
private fun SegmentedControlTabRow(
    tabCount: Int,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    size: LemonadeSegmentedControlSize,
    density: Density,
    pillShape: RoundedCornerShape,
    tabWidths: MutableMap<Int, Dp>,
    tabOffsets: MutableMap<Int, Dp>,
    pressedTabIndex: MutableMap<Int, Boolean>,
    content: @Composable BoxScope.(Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalSpaces.current.spacing0,
        ),
    ) {
        repeat(times = tabCount) { tabIndex ->
            val tabInteractionSource = remember { MutableInteractionSource() }
            val isHovering by tabInteractionSource.collectIsHoveredAsState()
            val isPressed by tabInteractionSource.collectIsPressedAsState()

            SideEffect {
                pressedTabIndex[tabIndex] = isPressed
            }

            val animatedBackgroundColor by animateColorAsState(
                targetValue = when {
                    tabIndex == selectedTab -> LocalColors.current.background.bgDefault.copy(
                        alpha = LocalOpacities.current.base.opacity0,
                    )
                    isPressed -> LocalColors.current.interaction.bgDefaultPressed
                    isHovering -> LocalColors.current.interaction.bgDefaultInteractive
                    else -> LocalColors.current.background.bgDefault.copy(
                        alpha = LocalOpacities.current.base.opacity0,
                    )
                },
            )

            Box(
                content = { content(tabIndex) },
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxHeight()
                    .onGloballyPositioned { coordinates ->
                        with(density) {
                            val newWidth = coordinates.size.width.toDp()
                            val newOffset = coordinates.positionInParent().x.toDp()
                            if (tabWidths[tabIndex] != newWidth) {
                                tabWidths[tabIndex] = newWidth
                            }
                            if (tabOffsets[tabIndex] != newOffset) {
                                tabOffsets[tabIndex] = newOffset
                            }
                        }
                    }.clip(shape = pillShape)
                    .clickable(
                        onClick = { onTabSelected(tabIndex) },
                        role = Role.Tab,
                        interactionSource = tabInteractionSource,
                        indication = null,
                    ).background(color = animatedBackgroundColor)
                    .padding(
                        horizontal = size.buttonHorizontalPadding(),
                    ),
            )
        }
    }
}

@Composable
private fun LemonadeSegmentedControlSize.containerHeight(): Dp {
    val sizes = LocalSizes.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> sizes.size800
        LemonadeSegmentedControlSize.Medium -> sizes.size1000
        LemonadeSegmentedControlSize.Large -> sizes.size1200
    }
}

@Composable
private fun LemonadeSegmentedControlSize.containerPadding(): Dp {
    val spaces = LocalSpaces.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> spaces.spacing50
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> spaces.spacing100
    }
}

@Composable
private fun LemonadeSegmentedControlSize.buttonMinWidth(): Dp {
    val sizes = LocalSizes.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> sizes.size800
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> sizes.size1200
    }
}

@Composable
private fun LemonadeSegmentedControlSize.buttonMinHeight(): Dp {
    val sizes = LocalSizes.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> sizes.size700
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> sizes.size800
    }
}

@Composable
private fun LemonadeSegmentedControlSize.buttonContentGap(): Dp {
    val spaces = LocalSpaces.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> spaces.spacing50
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> spaces.spacing100
    }
}

@Composable
private fun LemonadeSegmentedControlSize.buttonHorizontalPadding(): Dp {
    val spaces = LocalSpaces.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> spaces.spacing200
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> spaces.spacing300
    }
}

@Composable
private fun LemonadeSegmentedControlSize.textStyle(): LemonadeTextStyle {
    val typography = LocalTypographies.current
    return when (this) {
        LemonadeSegmentedControlSize.Small -> typography.bodyXSmallMedium
        LemonadeSegmentedControlSize.Medium,
        LemonadeSegmentedControlSize.Large,
        -> typography.bodySmallMedium
    }
}

private val IndicatorSpringSpec = spring<Dp>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

@LemonadePreview
@Composable
private fun SegmentedControlPreview() {
    LemonadeUi.SegmentedControl(
        onTabSelected = { },
        selectedTab = 1,
        properties = listOf(
            TabButtonProperties.labelAndIcon(
                label = "Tab 1",
                icon = LemonadeIcons.Heart,
            ),
            TabButtonProperties.labelAndIcon(
                label = "Tab 2",
                icon = LemonadeIcons.Laptop,
            ),
            TabButtonProperties.label(label = "Tab 3"),
        ),
    )
}
