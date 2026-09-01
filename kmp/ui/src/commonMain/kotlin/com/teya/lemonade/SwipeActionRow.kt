package com.teya.lemonade

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIconButtonShape
import com.teya.lemonade.core.LemonadeIcons
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Fraction of the row's width a drag must cross for a full swipe to commit. */
private const val COMMIT_FRACTION = 0.5f

/** Speed, in px/s, past which a flick decides the settle regardless of how far it travelled. */
private const val FLING_VELOCITY = 400f

/**
 * Growth applied to the first action while a full swipe is committed, taking it from the Small
 * button's 40dp to the 48dp the design asks for.
 */
private const val COMMITTED_SCALE = 1.2f

/** Where a released drag lands. */
internal enum class SwipeSettleTarget {
    Closed,
    Open,
    Committed,
}

/**
 * Resolves where a released drag settles.
 *
 * A commit outranks everything: once the row has crossed [COMMIT_FRACTION] of its width the gesture
 * has already been read as a full swipe, and dragging back at speed without crossing the threshold
 * again should not undo it. Otherwise a flick wins over position, so a short fast drag opens.
 *
 * @param travel distance the row has moved from closed, always positive.
 * @param velocity px/s at release, positive while still travelling open.
 * @param revealWidth width of the action strip, which is where an open row rests.
 * @param rowWidth full width of the row.
 * @param allowsFullSwipe whether a drag across the row may commit the first action.
 */
internal fun resolveSwipeSettle(
    travel: Float,
    velocity: Float,
    revealWidth: Float,
    rowWidth: Float,
    allowsFullSwipe: Boolean,
): SwipeSettleTarget =
    when {
        allowsFullSwipe && travel >= rowWidth * COMMIT_FRACTION -> SwipeSettleTarget.Committed
        velocity >= FLING_VELOCITY -> SwipeSettleTarget.Open
        velocity <= -FLING_VELOCITY -> SwipeSettleTarget.Closed
        travel >= revealWidth / 2f -> SwipeSettleTarget.Open
        else -> SwipeSettleTarget.Closed
    }

/**
 * One action revealed behind a [SwipeActionRow]. [contentDescription] has no default because it is
 * what publishes the action to TalkBack, where the gesture itself is invisible.
 */
public data class SwipeAction(
    val icon: LemonadeIcons,
    val contentDescription: String,
    val onClick: () -> Unit,
    val variant: LemonadeButtonVariant = LemonadeButtonVariant.Critical,
)

/** The buttons behind the row. Laid out at its natural width, which is what the row reveals. */
@Composable
private fun SwipeActionStrip(
    actions: List<SwipeAction>,
    committed: Boolean,
    onSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(
                start = LemonadeTheme.spaces.spacing300,
                end = LemonadeTheme.spaces.spacing200,
            ).onSizeChanged { onSizeChanged(it.width) }
            // The buttons stay in the semantics tree even while covered by the row, which would
            // duplicate every custom action. The gesture is visual; the custom action is the
            // accessible path.
            .clearAndSetSemantics { },
        horizontalArrangement = Arrangement.spacedBy(LemonadeTheme.spaces.spacing200),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEachIndexed { index, action ->
            // The first action is the one a full swipe fires, so it is the one that grows. It
            // grows by scaling rather than by changing size: a size change re-measures the strip
            // mid-drag, which rewrites the reveal width the settle is resolved against.
            val scale = if (committed && index == 0) COMMITTED_SCALE else 1f
            LemonadeUi.IconButton(
                icon = action.icon,
                contentDescription = action.contentDescription,
                onClick = action.onClick,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
                variant = action.variant,
                type = LemonadeButtonType.Solid,
                size = LemonadeButtonSize.Small,
                shape = LemonadeIconButtonShape.Circular,
            )
        }
    }
}

@Suppress("CyclomaticComplexMethod")
@Composable
private fun SwipeActionRowCore(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    actions: List<SwipeAction>,
    enabled: Boolean,
    allowsFullSwipe: Boolean,
    showDivider: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val travel = remember { Animatable(initialValue = 0f) }
    var revealWidth by remember { mutableFloatStateOf(0f) }
    var rowWidth by remember { mutableFloatStateOf(0f) }
    var committed by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // A reveal on the trailing edge travels left in LTR and right in RTL.
    val towardsTrailing = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 1f else -1f

    // The caller is the source of truth: another row opening closes this one. Keyed on `open`
    // alone: a settle animates itself, because it usually writes the value `open` already holds
    // and this effect would not restart.
    LaunchedEffect(open) {
        travel.animateTo(if (open) revealWidth else 0f, spring())
    }

    // The strip can measure after the row is already open, or re-measure when `actions` change,
    // so an open row would otherwise rest at a stale offset. Never under a live finger, where it
    // would fight the drag.
    LaunchedEffect(revealWidth) {
        if (open && !dragging) travel.animateTo(revealWidth, spring())
    }

    val dragState = rememberDraggableState { delta ->
        val ceiling = if (allowsFullSwipe) rowWidth else revealWidth
        val next = (travel.value + delta * towardsTrailing).coerceIn(0f, ceiling)
        val crossed = allowsFullSwipe && next >= rowWidth * COMMIT_FRACTION
        if (crossed != committed) {
            committed = crossed
            if (crossed) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        scope.launch { travel.snapTo(next) }
    }

    val highlight = LemonadeTheme.colors.interaction.bgSubtleInteractive
    val density = LocalDensity.current
    val gutterPx = with(density) { LemonadeTheme.spaces.spacing100.toPx() }
    val highlightRadiusPx = with(density) { LemonadeTheme.radius.radius500.toPx() }

    Column(modifier = modifier.onSizeChanged { rowWidth = it.width.toFloat() }) {
        Box(
            modifier = Modifier
                .clipToBounds()
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled && actions.isNotEmpty(),
                    onDragStarted = { dragging = true },
                    onDragStopped = { velocity ->
                        dragging = false
                        val target = resolveSwipeSettle(
                            travel = travel.value,
                            velocity = velocity * towardsTrailing,
                            revealWidth = revealWidth,
                            rowWidth = rowWidth,
                            allowsFullSwipe = allowsFullSwipe,
                        )
                        committed = false
                        // Every branch animates: settling usually writes the value `open` already
                        // holds, so nothing else would move the row off where the finger left it.
                        when (target) {
                            SwipeSettleTarget.Committed -> {
                                onOpenChange(false)
                                // Before the animation, not after: animateTo suspends until it
                                // settles, and the action must not wait on a spring.
                                actions.firstOrNull()?.onClick()
                                travel.animateTo(0f, spring())
                            }

                            SwipeSettleTarget.Open -> {
                                onOpenChange(true)
                                travel.animateTo(revealWidth, spring())
                            }

                            SwipeSettleTarget.Closed -> {
                                onOpenChange(false)
                                travel.animateTo(0f, spring())
                            }
                        }
                    },
                )
                // Merged, so TalkBack focuses this node instead of the merging node the wrapped
                // item's own `clickable` creates below it. Unmerged, the container is never
                // focused and its actions are never announced.
                .semantics(mergeDescendants = true) {
                    customActions = actions.map { action ->
                        CustomAccessibilityAction(action.contentDescription) {
                            action.onClick()
                            true
                        }
                    }
                },
        ) {
            SwipeActionStrip(
                actions = actions,
                committed = committed,
                // An empty strip still measures its padding, which would open the row onto a
                // bare gap.
                onSizeChanged = { revealWidth = if (actions.isEmpty()) 0f else it.toFloat() },
                modifier = Modifier.align(Alignment.CenterEnd),
            )
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = (travel.value * towardsTrailing).roundToInt(), y = 0) }
                    // The open row rests on the list item's own press highlight rather than a
                    // surface of its own: same fill, same radius, same gutter.
                    .drawBehind {
                        val progress = if (revealWidth > 0f) {
                            (travel.value / revealWidth).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                        if (progress > 0f) {
                            val gutter = gutterPx
                            drawRoundRect(
                                color = highlight,
                                topLeft = Offset(x = gutter, y = gutter),
                                size = Size(size.width - gutter * 2, size.height - gutter * 2),
                                cornerRadius = CornerRadius(highlightRadiusPx),
                                alpha = progress,
                            )
                        }
                    },
            ) {
                content()
                // A sibling drawn above the content, not a `clickable` on its parent: Compose
                // dispatches pointers children-first, so the wrapped item's own `clickable`
                // consumes the down and a parent would never see it.
                if (open) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            // Closing an open row is not a navigation, and it must not announce
                            // as one.
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onOpenChange(false) },
                    )
                }
            }
        }
        if (showDivider) {
            LemonadeUi.HorizontalDivider(
                modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing400),
            )
        }
    }
}

/**
 * Wraps a row with actions revealed by a horizontal drag.
 *
 * The wrapped item must not draw its own divider — pass `showDivider = false` to it and set
 * [showDivider] here instead. A list item draws its divider on the same modifier chain as its body,
 * so it would travel with the row and leave a gap at the trailing edge.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SwipeActionRow(
 *     actions = listOf(
 *         SwipeAction(
 *             icon = LemonadeIcons.Trash,
 *             contentDescription = "Remove account",
 *             onClick = { /* trigger an action */ },
 *         ),
 *     ),
 *     showDivider = true,
 * ) {
 *     LemonadeUi.ActionListItem(label = "Label", onItemClicked = { /* … */ })
 * }
 * ```
 * @param actions - the actions revealed on the trailing edge, outermost first.
 * @param modifier - [Modifier] applied to the base container.
 * @param enabled - flag to define whether the drag is active.
 * @param allowsFullSwipe - whether dragging across the row fires the first action on release.
 * @param showDivider - flag to show a divider below the row, which does not travel with it.
 * @param content - the row this wraps.
 */
@Composable
public fun LemonadeUi.SwipeActionRow(
    actions: List<SwipeAction>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allowsFullSwipe: Boolean = true,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    SwipeActionRowCore(
        open = open,
        onOpenChange = { open = it },
        actions = actions,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}

/**
 * [SwipeActionRow] whose open row is controlled by the caller, so a list can keep at most one row
 * open: hoist a single nullable id and hand every row the same pair.
 *
 * @param id - identity of this row, compared against [openId].
 * @param openId - identity of the row currently open, or null when none is.
 * @param onOpenIdChange - callback called with this row's [id] when it opens and null when it closes.
 */
@Composable
public fun LemonadeUi.SwipeActionRow(
    id: Any,
    openId: Any?,
    onOpenIdChange: (Any?) -> Unit,
    actions: List<SwipeAction>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allowsFullSwipe: Boolean = true,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    SwipeActionRowCore(
        open = openId == id,
        onOpenChange = { onOpenIdChange(if (it) id else null) },
        actions = actions,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}
