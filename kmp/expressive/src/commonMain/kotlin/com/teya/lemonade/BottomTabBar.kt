@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("MatchingDeclarationName")

package com.teya.lemonade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A single item in [LemonadeUi.BottomTabBar].
 *
 * @param label text label shown below the icon
 * @param icon [LemonadeIcons] shown above the label while the item is not selected
 * @param selectedIcon optional [LemonadeIcons] shown while the item is selected. Pair an outline
 *   [icon] with its solid variant here — `Wallet` with `WalletSolid`, say. `null` uses [icon] for
 *   both states
 */
public data class BottomTabBarItem(
    val label: String,
    val icon: LemonadeIcons,
    val selectedIcon: LemonadeIcons? = null,
)

/**
 * A floating, pill-shaped bottom navigation bar for top-level destinations.
 *
 * Built on Material3's [HorizontalFloatingToolbar] so the bar inherits its elevation, motion and
 * interaction styling. Items are distributed equally across the bar; the selected item is marked by
 * a single elevated pill that slides between slots as the selection changes. The component paints a
 * soft scroll-edge gradient behind itself so it floats nicely over scrollable content.
 *
 * The component already applies [androidx.compose.foundation.layout.navigationBarsPadding], so
 * callers do not have to pad around the system navigation bar themselves.
 *
 * Pass a [floatingActionButton] — usually a circular [LemonadeUi.IconButton] — to place an action
 * beside the bar. When a slot is narrower than the width a label needs, only the selected
 * item keeps its label; the others show just their icon.
 *
 * ## Usage
 * ```kotlin
 * var selectedIndex by remember { mutableStateOf(value = 0) }
 *
 * LemonadeUi.BottomTabBar(
 *     items = listOf(
 *         BottomTabBarItem(label = "Home", icon = LemonadeIcons.Home),
 *         BottomTabBarItem(label = "Sales", icon = LemonadeIcons.ChartStats),
 *         BottomTabBarItem(label = "Money", icon = LemonadeIcons.Wallet),
 *     ),
 *     selectedIndex = selectedIndex,
 *     onItemSelected = { index -> selectedIndex = index },
 *     floatingActionButton = {
 *         LemonadeUi.IconButton(
 *             icon = LemonadeIcons.Plus,
 *             contentDescription = "Add",
 *             onClick = { },
 *             size = LemonadeButtonSize.Large,
 *             shape = LemonadeIconButtonShape.Circular,
 *         )
 *     },
 * )
 * ```
 *
 * @param items non-empty list of [BottomTabBarItem] to show
 * @param selectedIndex index of the currently selected item, which must be within [items] indices.
 *   The pill animates to this slot when it changes
 * @param onItemSelected called with the index of the item the user selected
 * @param modifier [Modifier] applied to the root container
 * @param floatingActionButton optional action shown at the trailing end of the bar
 */
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
) {
    require(
        value = selectedIndex in items.indices,
        lazyMessage = {
            "Selected index ($selectedIndex) is out of bounds for items list of size ${items.size}."
        },
    )
    val selectionPosition = animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bottomTabBarSelection",
    )
    CoreBottomTabBar(
        items = items,
        selectionPosition = { selectionPosition.value },
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
        floatingActionButton = floatingActionButton,
    )
}

@Deprecated(
    message = "Use the overload with floatingActionButton.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomTabBar(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

/**
 * Bottom tab bar whose pill follows a fractional slot position, not an index.
 *
 * Drive [selectionPosition] from a gesture or transition — Android predictive back, say — to slide
 * the pill in lock-step with the finger.
 *
 * At rest, pass the selected index as a float. A tab is drawn as selected once the pill comes to
 * rest on it, so a tab the pill slides past on its way to another stays unselected.
 *
 * @param items non-empty list of [BottomTabBarItem] to show
 * @param selectionPosition fractional slot index of the pill, the selected index at rest
 * @param onItemSelected called with the index of the item the user selected
 * @param modifier [Modifier] applied to the root container
 * @param floatingActionButton optional action shown at the trailing end of the bar
 */
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectionPosition: Float,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
) {
    val restingIndex = rememberRestingIndex(selectionPosition = selectionPosition)
    val currentPosition by rememberUpdatedState(newValue = selectionPosition)
    CoreBottomTabBar(
        items = items,
        selectionPosition = { currentPosition },
        selectedIndex = restingIndex.coerceIn(range = items.indices),
        onItemSelected = onItemSelected,
        modifier = modifier,
        floatingActionButton = floatingActionButton,
    )
}

@Deprecated(
    message = "Use the overload with floatingActionButton.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectionPosition: Float,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomTabBar(
        items = items,
        selectionPosition = selectionPosition,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

private val MaxBarWidth: Dp = 500.dp
private val ItemHeight: Dp = 54.dp
private const val SELECTED_ICON_SCALE = 1.12f
private const val ICON_CROSSFADE_MS = 200
private const val RESTING_SETTLE_MS = 32L
private const val RESTING_POSITION_TOLERANCE = 0.001f

@Composable
private fun rememberRestingIndex(selectionPosition: Float): Int {
    var restingIndex by remember { mutableIntStateOf(value = selectionPosition.roundToInt()) }
    val currentPosition by rememberUpdatedState(newValue = selectionPosition)
    LaunchedEffect(Unit) {
        snapshotFlow { currentPosition }.collectLatest { position ->
            val nearestIndex = position.roundToInt()
            if (abs(x = position - nearestIndex) < RESTING_POSITION_TOLERANCE) {
                delay(timeMillis = RESTING_SETTLE_MS)
                restingIndex = nearestIndex
            }
        }
    }
    return restingIndex
}

@Composable
internal fun CoreBottomTabBar(
    items: List<BottomTabBarItem>,
    selectionPosition: () -> Float,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
) {
    require(
        value = items.isNotEmpty(),
        lazyMessage = { "BottomTabBar items list should not be empty." },
    )

    val edgeColor = LemonadeTheme.colors.background.bgSubtle

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        edgeColor.copy(alpha = 0f),
                        edgeColor,
                    ),
                ),
            ).navigationBarsPadding()
            .padding(all = LemonadeTheme.spaces.spacing400)
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        HorizontalFloatingToolbar(
            modifier = Modifier
                .weight(
                    weight = 1f,
                    fill = false,
                ).widthIn(max = MaxBarWidth)
                .lemonadeShadow(
                    shadow = LemonadeShadow.Xlarge,
                    shape = LemonadeTheme.shapes.radiusFull,
                ),
            expanded = true,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = LemonadeTheme.colors.background.bgDefault,
            ),
            contentPadding = PaddingValues(all = LemonadeTheme.spaces.spacing100),
        ) {
            // Plain Box (not BoxWithConstraints): HorizontalFloatingToolbar queries the intrinsic widths
            // of its content, and a SubcomposeLayout throws on intrinsic queries. The row width is
            // captured via onSizeChanged so the pill can be positioned in pixels. The items compose only
            // once it is known, so each label's AnimatedVisibility starts at its final state.
            var rowWidthPx by remember { mutableIntStateOf(0) }
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .height(height = ItemHeight)
                    .onSizeChanged { size -> rowWidthPx = size.width },
            ) {
                if (rowWidthPx > 0) {
                    val density = LocalDensity.current
                    val rowWidth = rowWidthPx.toFloat()
                    val overlapPx = with(density) { LemonadeTheme.spaces.spacing200.toPx() }
                    // The pill is wider than a slot, so the content centres are pulled in until a pill
                    // centred on the first or last item still fits inside the row.
                    val pillWidthPx = (rowWidth / items.size + 2 * overlapPx).coerceAtMost(maximumValue = rowWidth)
                    val stepPx = if (items.size > 1) (rowWidth - pillWidthPx) / (items.size - 1) else 0f
                    val labelsOnlyWhenSelected = with(density) { stepPx.toDp() } < LemonadeTheme.sizes.size1600
                    val lastIndex = (items.size - 1).toFloat()

                    Box(
                        modifier = Modifier
                            .offset {
                                val position = selectionPosition().coerceIn(
                                    minimumValue = 0f,
                                    maximumValue = lastIndex,
                                )
                                IntOffset(x = (stepPx * position).roundToInt(), y = 0)
                            }.width(width = with(density) { pillWidthPx.toDp() })
                            .height(height = ItemHeight)
                            .clip(shape = LemonadeTheme.shapes.radiusFull)
                            .background(color = LemonadeTheme.colors.background.bgElevated),
                    )

                    items.forEachIndexed { index, item ->
                        val centerPx = pillWidthPx / 2f + stepPx * index
                        val startPx = if (index == 0) 0f else centerPx - stepPx / 2f
                        val endPx = if (index == items.lastIndex) rowWidth else centerPx + stepPx / 2f
                        BottomTabBarItemContent(
                            item = item,
                            isSelected = index == selectedIndex,
                            showLabel = !labelsOnlyWhenSelected || index == selectedIndex,
                            contentOffsetPx = centerPx - (startPx + endPx) / 2f,
                            onClick = { onItemSelected(index) },
                            modifier = Modifier
                                .offset { IntOffset(x = startPx.roundToInt(), y = 0) }
                                .width(width = with(density) { (endPx - startPx).toDp() }),
                        )
                    }
                }
            }
        }
        if (floatingActionButton != null) {
            Spacer(modifier = Modifier.width(width = LemonadeTheme.spaces.spacing200))
            floatingActionButton()
        }
    }
}

@Composable
private fun BottomTabBarItemContent(
    item: BottomTabBarItem,
    isSelected: Boolean,
    showLabel: Boolean,
    contentOffsetPx: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(height = ItemHeight)
            .clip(shape = LemonadeTheme.shapes.radiusFull)
            .clickable(
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = LocalEffects.current.interactionIndication,
            ).semantics { selected = isSelected }
            .padding(vertical = LemonadeTheme.spaces.spacing200),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset { IntOffset(x = contentOffsetPx.roundToInt(), y = 0) },
        ) {
            val displayedIcon = if (isSelected) {
                item.selectedIcon
                    ?: item.icon
            } else {
                item.icon
            }
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) SELECTED_ICON_SCALE else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                label = "tabIconScale",
            )

            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            ) {
                Crossfade(
                    targetState = displayedIcon,
                    animationSpec = tween(durationMillis = ICON_CROSSFADE_MS),
                    label = "tabIcon",
                ) { icon ->
                    LemonadeUi.Icon(
                        icon = icon,
                        contentDescription = null,
                        size = LemonadeAssetSize.Medium,
                        tint = LemonadeTheme.colors.content.contentPrimary,
                    )
                }
            }

            AnimatedVisibility(
                visible = showLabel,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                label = "tabLabel",
            ) {
                LemonadeUi.Text(
                    text = item.label,
                    textStyle = LemonadeTheme.typography.bodyXSmallMedium,
                    color = LemonadeTheme.colors.content.contentPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = LemonadeTheme.spaces.spacing50),
                )
            }
        }
    }
}
