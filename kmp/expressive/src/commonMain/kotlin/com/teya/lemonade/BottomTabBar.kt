@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("MatchingDeclarationName")

package com.teya.lemonade

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow
import kotlin.math.roundToInt

/**
 * Represents a single item in a [LemonadeUi.BottomTabBar].
 *
 * @param label The text label displayed below the icon.
 * @param icon The [LemonadeIcons] displayed above the label when the item is not selected.
 * @param selectedIcon Optional [LemonadeIcons] rendered while the item is selected. A common
 *   pattern is to pair an outline `icon` with its solid variant here (e.g. `Wallet` /
 *   `WalletSolid`). When `null`, [icon] is used for both states.
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
 * The component already applies `Modifier.navigationBarsPadding`, so callers do not need to pad
 * around the system navigation bar themselves.
 *
 * ## Large font scales
 * Items grow with the user's font scale rather than clipping their label. When the labels no longer
 * fit their slots the bar drops all of them at once and shows only icons, rather than ellipsising
 * them down to a couple of meaningless characters. The labels are still announced by accessibility
 * services, as each item's content description.
 *
 * The fit is measured, not pinned to a font-scale threshold, so how long the labels survive depends
 * on the space actually available: a tablet keeps its labels at text sizes where a compact phone
 * cannot.
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
 * )
 * ```
 *
 * @param items A non-empty list of [BottomTabBarItem] to display.
 * @param selectedIndex The index of the currently selected item. Must be within [items] indices.
 *   The pill animates to this slot when it changes.
 * @param onItemSelected A callback invoked with the index of the item the user selected.
 * @param modifier The [Modifier] to be applied to the root container of the component.
 */
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(
        value = selectedIndex in items.indices,
        lazyMessage = {
            "Selected index ($selectedIndex) is out of bounds for items list of size ${items.size}."
        },
    )
    val selectionPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bottomTabBarSelection",
    )
    CoreBottomTabBar(
        items = items,
        selectionPosition = selectionPosition,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

/**
 * Bottom tab bar whose selection pill is driven by a fractional slot position rather than a discrete
 * index — drive [selectionPosition] from a gesture or transition (e.g. Android predictive back) to
 * slide the pill in lock-step with the finger.
 *
 * At rest, pass the selected index as a float. The discrete selection used for the icon variant and
 * accessibility is derived by rounding [selectionPosition].
 *
 * @param items A non-empty list of [BottomTabBarItem] to display.
 * @param selectionPosition The fractional slot index of the pill (resting = the selected index).
 * @param onItemSelected A callback invoked with the index of the item the user selected.
 * @param modifier The [Modifier] to be applied to the root container of the component.
 */
@Composable
public fun LemonadeUi.BottomTabBar(
    items: List<BottomTabBarItem>,
    selectionPosition: Float,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    CoreBottomTabBar(
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

@Composable
internal fun CoreBottomTabBar(
    items: List<BottomTabBarItem>,
    selectionPosition: Float,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(
        value = items.isNotEmpty(),
        lazyMessage = { "BottomTabBar items list should not be empty." },
    )

    val selectedIndex = selectionPosition.roundToInt().coerceIn(0, items.size - 1)
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
                .weight(weight = 1f, fill = false)
                .widthIn(max = MaxBarWidth)
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
            // captured via onSizeChanged so the pill can be positioned in pixels.
            var rowWidthPx by remember { mutableIntStateOf(0) }
            val showLabels = rememberLabelsFit(
                items = items,
                rowWidthPx = rowWidthPx,
            )
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .onSizeChanged { rowWidthPx = it.width },
            ) {
                if (rowWidthPx > 0) {
                    val slotWidthPx = rowWidthPx.toFloat() / items.size
                    val slotWidthDp = with(LocalDensity.current) { slotWidthPx.toDp() }
                    val clampedPosition = selectionPosition.coerceIn(0f, (items.size - 1).toFloat())

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(x = (slotWidthPx * clampedPosition).roundToInt(), y = 0) }
                            .width(width = slotWidthDp)
                            .fillMaxHeight()
                            .clip(shape = LemonadeTheme.shapes.radiusFull)
                            .background(color = LemonadeTheme.colors.background.bgElevated),
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        BottomTabBarItemContent(
                            item = item,
                            isSelected = index == selectedIndex,
                            showLabel = showLabels,
                            onClick = { onItemSelected(index) },
                            modifier = Modifier.weight(weight = 1f),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Whether every label fits its slot at the current text size, screen width and item count.
 *
 * A label ellipsised down to a couple of characters carries no meaning, so the bar drops all of
 * them at once and lets the icons speak instead. The decision is measured rather than pinned to a
 * font-scale threshold: how much room a label needs depends just as much on the label itself, the
 * number of items and the width available — a tablet at 500dp keeps its labels at text sizes where
 * a 320dp phone cannot.
 *
 * The measurement is uniform on purpose. Hiding only the labels that overflow would leave a bar
 * with some items labelled and some not.
 *
 * Returns `true` until the row has been measured, so bars whose labels fit — the common case —
 * never flash through a label-less first frame.
 */
@Composable
private fun rememberLabelsFit(
    items: List<BottomTabBarItem>,
    rowWidthPx: Int,
): Boolean {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = LemonadeTheme.typography.bodyXSmallMedium.textStyle
    val density = LocalDensity.current
    // Mirrors the horizontal padding the label itself carries, so neighbouring slots never touch.
    val labelGutterPx = with(density) { (LemonadeTheme.spaces.spacing100 * 2).roundToPx() }

    return remember(rowWidthPx, items, labelStyle, textMeasurer, labelGutterPx) {
        if (rowWidthPx == 0) return@remember true

        val availableWidthPx = rowWidthPx / items.size - labelGutterPx
        items.all { item ->
            val measured = textMeasurer.measure(
                text = item.label,
                style = labelStyle,
                maxLines = 1,
            )
            measured.size.width <= availableWidthPx
        }
    }
}

@Composable
private fun BottomTabBarItemContent(
    item: BottomTabBarItem,
    isSelected: Boolean,
    showLabel: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .heightIn(min = ItemHeight)
            .clip(shape = LemonadeTheme.shapes.radiusFull)
            .clickable(
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = LocalEffects.current.interactionIndication,
            ).semantics {
                selected = isSelected
                // With the label hidden the tab itself has to carry the name.
                if (!showLabel) contentDescription = item.label
            }.padding(vertical = LemonadeTheme.spaces.spacing200),
    ) {
        val displayedIcon = if (isSelected) {
            item.selectedIcon
                ?: item.icon
        } else {
            item.icon
        }
        // Selected icon pops (spring) and crossfades to its variant.
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

        if (showLabel) {
            Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing50))

            LemonadeUi.Text(
                text = item.label,
                modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing100),
                textStyle = LemonadeTheme.typography.bodyXSmallMedium,
                color = LemonadeTheme.colors.content.contentPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
