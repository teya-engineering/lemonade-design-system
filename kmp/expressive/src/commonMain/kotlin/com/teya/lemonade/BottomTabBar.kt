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
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.LemonadeShadow
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
 * @param items non-empty list of [BottomTabBarItem] to show
 * @param selectedIndex index of the currently selected item, which must be within [items] indices.
 *   The pill animates to this slot when it changes
 * @param onItemSelected called with the index of the item the user selected
 * @param modifier [Modifier] applied to the root container
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
 * Bottom tab bar whose pill follows a fractional slot position, not an index.
 *
 * Drive [selectionPosition] from a gesture or transition — Android predictive back, say — to slide
 * the pill in lock-step with the finger.
 *
 * At rest, pass the selected index as a float. Rounding [selectionPosition] gives the discrete
 * selection used for the icon variant and accessibility.
 *
 * @param items non-empty list of [BottomTabBarItem] to show
 * @param selectionPosition fractional slot index of the pill, the selected index at rest
 * @param onItemSelected called with the index of the item the user selected
 * @param modifier [Modifier] applied to the root container
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

    val selectedIndex = selectionPosition
        .roundToInt()
        .coerceIn(
            minimumValue = 0,
            maximumValue = items.size - 1,
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
            // captured via onSizeChanged so the pill can be positioned in pixels.
            var rowWidthPx by remember { mutableIntStateOf(0) }
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .height(height = ItemHeight)
                    .onSizeChanged { size -> rowWidthPx = size.width },
            ) {
                if (rowWidthPx > 0) {
                    val slotWidthPx = rowWidthPx.toFloat() / items.size
                    val slotWidthDp = with(LocalDensity.current) { slotWidthPx.toDp() }
                    val clampedPosition = selectionPosition.coerceIn(
                        minimumValue = 0f,
                        maximumValue = (items.size - 1).toFloat(),
                    )

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (slotWidthPx * clampedPosition).roundToInt(),
                                    y = 0,
                                )
                            }.width(width = slotWidthDp)
                            .height(height = ItemHeight)
                            .clip(shape = LemonadeTheme.shapes.radiusFull)
                            .background(color = LemonadeTheme.colors.background.bgElevated),
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        BottomTabBarItemContent(
                            item = item,
                            isSelected = index == selectedIndex,
                            onClick = { onItemSelected(index) },
                            modifier = Modifier.weight(weight = 1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomTabBarItemContent(
    item: BottomTabBarItem,
    isSelected: Boolean,
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

        Spacer(modifier = Modifier.height(height = LemonadeTheme.spaces.spacing50))

        LemonadeUi.Text(
            text = item.label,
            textStyle = LemonadeTheme.typography.bodyXSmallMedium,
            color = LemonadeTheme.colors.content.contentPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
