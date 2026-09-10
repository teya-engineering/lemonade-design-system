@file:Suppress("TooManyFunctions")

package com.teya.lemonade

import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.teya.lemonade.core.LemonadeAssetSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeButtonVariant
import com.teya.lemonade.core.LemonadeIcons
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Stiffness of the spring a released row travels on.
 *
 * The one animation the reveal rides: ω = 12.5 rad/s at unit mass, about half a second end to end.
 * [Spring.DampingRatioNoBouncy] is the critical damping for it, so the row arrives without springing
 * past and coming back.
 */
private const val SETTLE_STIFFNESS = 156.25f

/**
 * How far the row may drift before an open one counts as scrolled past.
 *
 * Enough to sit out the rounding a layout pass can move it by, and far short of a deliberate scroll.
 */
private val SCROLL_SLACK = 4.dp

/**
 * What is left of the row on screen once a commit has claimed it.
 *
 * A commit parks the row a sliver short of carrying it off, which keeps it a row rather than a bare
 * action.
 */
private val COMMIT_INSET = 20.dp

/**
 * Stiffness of the spring a commit claims the row on.
 *
 * ω = 35 rad/s at unit mass: a fifth of a second end to end, against the settle's half. The action
 * reaches half its width within 50ms of the crossing and 94% of it within 130ms.
 */
private const val COMMIT_STIFFNESS = 1225f

private val settleSpring: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = SETTLE_STIFFNESS,
)

private val commitSpring: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = COMMIT_STIFFNESS,
)

private val bumpSpring: SpringSpec<Float> = spring(
    dampingRatio = BUMP_DAMPING,
    stiffness = BUMP_STIFFNESS,
)

private val noSpring: SnapSpec<Float> = snap()

/**
 * How much of an action's arrival is held back for the end.
 *
 * The last of it springs into place when the row has revealed the action fully, so it lands rather
 * than simply stopping. A bounce this shallow rings about 3% past its resting size, which at 1.5dp
 * of a 48dp action stays well inside the gap the action sits in.
 */
private const val BUMP_DEPTH = 0.12f

/**
 * How far out an action has to be before it lands.
 *
 * Not all the way: the row settles onto its last action asymptotically, so a trigger sitting on that
 * exact position is only reached as the spring runs out — the action would then start its landing
 * after the row had already stopped. Firing at most of the way out puts the two together, and
 * staggers a pair, each landing as the row clears it.
 */
private const val BUMP_TRIGGER = 0.7f

/**
 * The spring that lands it, ω² for a 0.25s response. Quick, so it lands with the row rather than
 * trailing it: the row's own settle is 0.5s, and a bump that long is still arriving after the row
 * has stopped, which reads as a second movement rather than the end of the first.
 *
 * [BUMP_DAMPING] is what makes it ring: 0.4 overshoots by about 2%.
 */
private const val BUMP_STIFFNESS = 631f
private const val BUMP_DAMPING = 0.4f

/**
 * What a group tells its rows: which one has the open slot.
 *
 * The count is what makes it a signal rather than a value. Two rows opening in turn both leave
 * [opener] set, and a row that closed and reopened would look unchanged — the count moves either
 * way, so every row hears every announcement.
 *
 * An announcement means two things at once, which is what lets one counter serve both: *I am the
 * open row now*, and *do not close on the tap in flight*. The group's own tap-to-close waits a
 * frame and then stands down if the count moved, so anything inside a group that acts on a tap has
 * to announce or the row underneath it closes.
 */
internal data class SwipeActionGroupSignal(
    val announcements: Int = 0,
    val opener: Any? = null,
)

private val LocalSwipeActionGroupSignal = compositionLocalOf { SwipeActionGroupSignal() }

/** Null outside a group, which is what leaves a lone row owning its own state. */
private val LocalSwipeActionGroupAnnounce = compositionLocalOf<((Any?) -> Unit)?> { null }

/**
 * Groups swipe rows so that at most one of them is open.
 *
 * A row cannot see a touch that lands outside it, so the group is what carries the news: opening one
 * closes the rest, and a tap anywhere inside closes whichever is open. Wrap the list, or the screen
 * — anything a reader would take as "somewhere else".
 *
 * Rows manage themselves inside it, including the ones given an [id] and [openId], so nothing has to
 * be hoisted to get this.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SwipeActionGroup {
 *     Column {
 *         accounts.forEach { account ->
 *             LemonadeUi.SwipeActionRow(trailingActions = listOf(remove(account))) {
 *                 LemonadeUi.ActionListItem(label = account.name, onItemClicked = { })
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * The group is a [Box] around [content] — it has to be, to watch for the tap that closes an open
 * row — so it takes part in the layout. Pass [modifier] whatever the content would have had.
 *
 * @param modifier [Modifier] applied to the group
 * @param content the rows, and whatever else the group covers
 */
@Composable
public fun LemonadeUi.SwipeActionGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var signal by remember { mutableStateOf(SwipeActionGroupSignal()) }
    val scope = rememberCoroutineScope()
    val announce: (Any?) -> Unit = { opener ->
        signal = SwipeActionGroupSignal(
            announcements = signal.announcements + 1,
            opener = opener,
        )
    }
    // The tap that fires an action is this same tap, seen here on the initial pass and by the
    // action on the final one, so the decision waits a frame for that row to claim the slot.
    val closeUnlessClaimed: () -> Unit = {
        val seen = signal.announcements
        scope.launch {
            withFrameNanos { }
            if (signal.announcements == seen) announce(null)
        }
    }
    CompositionLocalProvider(
        LocalSwipeActionGroupSignal provides signal,
        LocalSwipeActionGroupAnnounce provides announce,
    ) {
        Box(
            modifier = modifier.pointerInput(Unit) {
                awaitTapsWithoutConsuming(onTap = closeUnlessClaimed)
            },
        ) {
            content()
        }
    }
}

/**
 * Runs [onTap] for every tap inside.
 *
 * Watched on the initial pass and never consumed, so the tap still reaches whatever was tapped:
 * closing an open row must not cost the reader the tap that closed it.
 */
private suspend fun PointerInputScope.awaitTapsWithoutConsuming(onTap: () -> Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = PointerEventPass.Initial,
        )
        var travelled = 0f
        var pressed = true
        while (pressed) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val pointer = event.changes.firstOrNull { change ->
                change.id == down.id
            }
            if (pointer == null) {
                pressed = false
            } else {
                travelled += (pointer.position - pointer.previousPosition).getDistance()
                pressed = pointer.pressed
                if (!pressed && travelled < viewConfiguration.touchSlop) onTap()
            }
        }
    }
}

/**
 * One action revealed behind a [SwipeActionRow]. [contentDescription] has no default because it is
 * what publishes the action to TalkBack, where the gesture itself is invisible.
 *
 * @property keepsRowOpen whether the row stays open once this has fired. Firing an action normally
 * closes the row, which is wrong for one that puts something on screen about it — a confirmation
 * asking whether to go ahead reads oddly over a row that has already tidied itself away. Nothing
 * inside the row closes it again afterwards: the reader does, by tapping away or swiping another
 * row, or the caller does through the [openId] overload. An action that opens something modal
 * needs that overload, because the modal takes the taps the reader would have closed it with.
 */
@Immutable
public data class SwipeAction(
    val icon: LemonadeIcons,
    val contentDescription: String,
    val onClick: () -> Unit,
    val variant: LemonadeButtonVariant = LemonadeButtonVariant.Critical,
    val keepsRowOpen: Boolean = false,
)

/** [resolveSwipeRevealWidth] against the theme the row is drawn in. */
@Composable
@ReadOnlyComposable
private fun swipeRevealWidth(through: Int): Float =
    with(LocalDensity.current) {
        resolveSwipeRevealWidth(
            count = through,
            actionWidth = LemonadeTheme.sizes.size1200.toPx(),
            gap = LemonadeTheme.spaces.spacing200.toPx(),
            padding = LemonadeTheme.spaces.spacing300.toPx() +
                LemonadeTheme.spaces.spacing400.toPx(),
        )
    }

/** Which edge of the row this side's actions sit against. */
private val SwipeActionSide.alignment: Alignment
    get() = if (this == SwipeActionSide.Leading) Alignment.CenterStart else Alignment.CenterEnd

/**
 * The actions behind the row on one of its edges, drawn as far as the row has revealed them.
 *
 * Takes the row's travel rather than the geometry read off it, so the whole reveal is resolved in
 * one place and an action's arrival cannot drift from the width it is derived from. [travel] is the
 * magnitude the row has moved onto *this* side, so the side the row is not showing is handed zero
 * and draws nothing.
 *
 * Mirrored off [side]: an action sits against the edge it is revealed from, grows inwards from it,
 * and stacks away from it.
 */
@Composable
private fun SwipeActionStrip(
    actions: List<SwipeAction>,
    side: SwipeActionSide,
    travel: () -> Float,
    rowWidth: () -> Float,
    committed: () -> Boolean,
    committedStretch: Dp,
    onFired: (SwipeAction) -> Unit,
    dim: () -> Float,
    holding: Boolean,
    modifier: Modifier = Modifier,
) {
    val step = LemonadeTheme.sizes.size1200 + LemonadeTheme.spaces.spacing200
    val density = LocalDensity.current
    val leading = side == SwipeActionSide.Leading
    // Into the row, against the side's own outward travel.
    val towardsInside = -side.sign
    val outerPadding = LemonadeTheme.spaces.spacing400
    val innerPadding = LemonadeTheme.spaces.spacing300
    Box(
        modifier = modifier
            .padding(
                start = if (leading) outerPadding else innerPadding,
                end = if (leading) innerPadding else outerPadding,
            )
            // The actions stay in the semantics tree even while the row covers them, which would
            // duplicate every custom action. The gesture is visual; the custom action is the
            // accessible path.
            .clearAndSetSemantics { },
        contentAlignment = side.alignment,
    ) {
        // Outermost last, so it is drawn on top: the first action is the one a full swipe fires,
        // and the one that stretches over the actions beside it.
        val shown = travel()
        val actionWidth = with(density) { LemonadeTheme.sizes.size1200.toPx() }
        val stripReveal = swipeRevealWidth(through = actions.size)
        val displaced = resolveSwipeDisplacedOpacity(
            travel = shown,
            rowWidth = rowWidth(),
        )
        actions.indices
            .reversed()
            .forEach { index ->
                val revealed = resolveSwipeStripReveal(
                    travel = shown,
                    // The last action's own reveal is the whole strip's.
                    actionReveal = if (index == actions.lastIndex) {
                        stripReveal
                    } else {
                        swipeRevealWidth(through = index + 1)
                    },
                    stripReveal = stripReveal,
                    actionWidth = actionWidth,
                )
                // The slack goes to the first action's width and to everything else's position, so
                // a stretching action pushes the ones beside it along rather than growing over
                // them. Their gaps hold, and the strip still ends exactly one leading gap ahead of
                // the row however far it is dragged.
                val push = if (index == 0) 0.dp else with(density) { revealed.stretch.toDp() }
                SwipeActionCapsule(
                    action = actions[index],
                    reveal = revealed,
                    // Each action lands as the row clears it, so the second of a pair bumps in on
                    // its own rather than with the first.
                    arrived = revealed.scale >= BUMP_TRIGGER,
                    opacity = if (index == 0) 1f else displaced,
                    stretches = index == 0,
                    committed = committed() && index == 0,
                    committedStretch = committedStretch,
                    towardsInside = towardsInside,
                    onFired = onFired,
                    dim = dim(),
                    holding = holding,
                    modifier = Modifier.offset(x = (step * index + push) * towardsInside),
                )
            }
    }
}

/**
 * One action: a capsule that is a circle until a full swipe stretches it.
 *
 * Drawn here rather than with [LemonadeUi.IconButton], whose size is square and fixed, but off the
 * same colours so the two stay in step.
 */
@Composable
private fun SwipeActionCapsule(
    action: SwipeAction,
    reveal: SwipeStripReveal,
    arrived: Boolean,
    opacity: Float,
    stretches: Boolean,
    committed: Boolean,
    committedStretch: Dp,
    towardsInside: Float,
    onFired: (SwipeAction) -> Unit,
    dim: Float,
    holding: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = resolveIconButtonColors(
        variant = action.variant,
        type = LemonadeButtonType.Solid,
    )
    val size = LemonadeTheme.sizes.size1200
    val dimFloor = LocalOpacities.current.base.opacity30
    val bump by animateFloatAsState(
        targetValue = if (arrived) 1f else 1f - BUMP_DEPTH,
        animationSpec = bumpSpring,
        label = "swipeActionBump",
    )
    val scale = reveal.scale * bump
    val interactionSource = remember { MutableInteractionSource() }
    val background by colors.animatedBackground(interactionSource = interactionSource)
    // Taken straight off the row's position rather than animated: an action that springs towards
    // the gap overshoots into it the moment the row comes back, and settling would animate a spring
    // of its own that no longer agrees with the row's.
    val stretch = with(LocalDensity.current) {
        (if (stretches) reveal.stretch else 0f).toDp()
    }
    // The icon slides to the centre of the capsule's inner end as a commit takes the row, against
    // the width the commit is heading for rather than the one it has: a spring chasing a target
    // that is itself still moving never catches it. Sprung both ways, unlike the row's own claim,
    // so the icon comes back to the centre rather than popping there.
    val iconClaim by animateFloatAsState(
        targetValue = if (committed) 1f else 0f,
        animationSpec = commitSpring,
        label = "swipeActionIcon",
    )
    val iconOffset = committedStretch / 2 * iconClaim * towardsInside
    val capsuleBackground = drained(
        color = background,
        amount = dim,
    )
    Box(
        modifier = modifier
            .size(
                width = size + stretch,
                height = size,
            ).graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = scale * opacity * (1f - dim * (1f - dimFloor))
            }.clip(shape = LemonadeTheme.shapes.radiusFull)
            .clickable(
                // A second tap on a destructive action is the one thing this must not allow, and
                // an inline confirmation leaves the capsule reachable.
                enabled = !holding,
                onClick = {
                    onFired(action)
                },
                role = Role.Button,
                interactionSource = interactionSource,
                indication = LocalEffects.current.interactionIndication,
            ).background(color = capsuleBackground),
        contentAlignment = Alignment.Center,
    ) {
        LemonadeUi.Icon(
            icon = action.icon,
            contentDescription = action.contentDescription,
            size = LemonadeAssetSize.Large,
            tint = drained(
                color = colors.contentColor,
                amount = dim,
            ),
            modifier = Modifier.offset(x = iconOffset),
        )
    }
}

/**
 * [color] drained of [amount] of its colour.
 *
 * What a grayscale filter would leave of it, mixed back in by how far the drain has gone.
 */
private fun drained(
    color: Color,
    amount: Float,
): Color {
    if (amount <= 0f) {
        return color
    }
    val luma = 0.213f * color.red + 0.715f * color.green + 0.072f * color.blue
    return lerp(
        start = color,
        stop = Color(
            red = luma,
            green = luma,
            blue = luma,
            alpha = color.alpha,
        ),
        fraction = amount,
    )
}

/**
 * Where the row is drawn, blending the finger's travel with the commit's [claimed] share.
 *
 * @param handingBack whether the drag has left a commit behind and is carrying the row's lead back
 */
private fun resolveSwipeDrawnTravel(
    travel: Float,
    commitTravel: Float,
    commitThreshold: Float,
    handingBack: Boolean,
    claimed: Float,
): Float {
    val base = if (handingBack) {
        resolveSwipeReleasedTravel(
            travel = travel,
            commitTravel = commitTravel,
            threshold = commitThreshold,
        )
    } else {
        travel
    }
    return base + (commitTravel - base) * claimed
}

/**
 * Closes an open row that has been scrolled past.
 *
 * Anchored on the first placement that repeats, never on the first placement at all: a screen still
 * finding its own size moves every row on it, and a row composed already open would take the
 * unplaced 0 for where it opened and close itself the moment the real position arrived.
 */
@Composable
private fun Modifier.closeWhenScrolledPast(
    open: Boolean,
    onClose: () -> Unit,
): Modifier {
    val slack = with(LocalDensity.current) { SCROLL_SLACK.toPx() }
    var rowY by remember { mutableFloatStateOf(0f) }
    var openedAt by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(open) {
        if (!open) openedAt = null
    }
    return onGloballyPositioned { coordinates ->
        val y = coordinates.positionInRoot().y
        val layoutSettled = y == rowY
        rowY = y
        if (!open) return@onGloballyPositioned
        val anchor = openedAt
        when {
            anchor == null -> if (layoutSettled) openedAt = y
            abs(y - anchor) > slack -> onClose()
        }
    }
}

/** The list item's own press highlight, drawn behind a row that is being [handled]. */
@Composable
private fun Modifier.swipeRowHighlight(handled: Boolean): Modifier {
    val color = LemonadeTheme.colors.interaction.bgSubtleInteractive
    val density = LocalDensity.current
    val gutter = with(density) { LemonadeTheme.spaces.spacing100.toPx() }
    val radius = with(density) { LemonadeTheme.radius.radius500.toPx() }
    // Snapped on and eased off rather than following the travel: the row is being handled from the
    // first pixel, and is still being handled until it has finished arriving.
    val alpha = animateFloatAsState(
        targetValue = if (handled) 1f else 0f,
        animationSpec = if (handled) noSpring else settleSpring,
        label = "swipeRowHighlight",
    )
    return drawBehind {
        if (alpha.value <= 0f) return@drawBehind
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = gutter,
                y = gutter,
            ),
            size = Size(
                width = size.width - gutter * 2,
                height = size.height - gutter * 2,
            ),
            cornerRadius = CornerRadius(radius),
            alpha = alpha.value,
        )
    }
}

/** Closes the row on a tap anywhere over the content it covers. */
@Composable
private fun SwipeRowTapToClose(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClose,
        ),
    )
}

/** The [actions] as accessibility actions, each firing through [onFired]. */
private fun swipeRowCustomActions(
    actions: List<SwipeAction>,
    onFired: (SwipeAction) -> Unit,
): List<CustomAccessibilityAction> =
    actions.map { action ->
        CustomAccessibilityAction(label = action.contentDescription) {
            onFired(action)
            true
        }
    }

@Suppress("CyclomaticComplexMethod")
@Composable
private fun SwipeActionRowCore(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    leadingActions: List<SwipeAction>,
    trailingActions: List<SwipeAction>,
    enabled: Boolean,
    allowsFullSwipe: Boolean,
    showDivider: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    // Signed: negative onto the leading actions, positive onto the trailing ones.
    //
    // A plain value the drag writes as it happens, not an `Animatable` a launched coroutine catches
    // up with: an `Animatable` lets the later mutation win, so a delta landing after a settle had
    // started would cancel the settle and park the row wherever the finger let go.
    //
    // Nothing in this function's body may read it, or the row recomposes on every frame of every
    // drag and every settle — and with it the content it wraps, which is the caller's whole list
    // item. The layout and draw lambdas below read it instead. [SwipeActionStrip] is the exception,
    // by design: an action's width and scale are composition-level, so a drag recomposes the strip
    // and its handful of capsules, and nothing above them.
    val travel = remember { mutableFloatStateOf(0f) }
    // The one animation allowed to write `travel`, held so a new one, or a finger, can end it.
    val settling = remember { mutableStateOf<Job?>(null) }
    // Where the row was last sent. The open/close effect checks it before settling: a drag that
    // opens or closes the row flips `open` too, and re-settling from rest would cancel the spring
    // the release velocity is riding on — every flick, a frame after it started.
    val settleTarget = remember { mutableFloatStateOf(Float.NaN) }
    var rowWidth by remember { mutableFloatStateOf(0f) }
    // Its own, so a row needs no identity from the caller to take part in a group.
    val groupIdentity = remember { Any() }
    val groupSignal = LocalSwipeActionGroupSignal.current
    val announce = LocalSwipeActionGroupAnnounce.current
    // The side rather than a flag: the strip that stretches and the icon that slides are one
    // edge's, not both.
    var committedSide by remember { mutableStateOf<SwipeActionSide?>(null) }
    // Which side the row is open on. Null until a drag or a caller says.
    var openSide by remember { mutableStateOf<SwipeActionSide?>(null) }
    // The side the live gesture owns. Outside composition, like `travel`: written on the first
    // delta of every drag, and nothing drawn reads it.
    val gestureSide = remember { mutableStateOf<SwipeActionSide?>(null) }
    // Whether a committed swipe is holding the row where it left it — all the way across, with the
    // action still stretched behind it — rather than at the reveal. Cleared when the row closes, or
    // when a finger takes hold of it again.
    var held by remember { mutableStateOf(false) }
    // Whether an action is holding the row open behind something it opened. The actions are then
    // nothing the reader can act on — whatever they opened is — so they are drawn as inert.
    var holding by remember { mutableStateOf(false) }
    // Whether this drag has left a commit behind and is carrying the row's lead back with it.
    var releasing by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current

    // A reveal on the trailing edge travels left in LTR and right in RTL.
    val towardsTrailing = if (LocalLayoutDirection.current == LayoutDirection.Rtl) 1f else -1f

    /** Carries the row to [target], from whatever it is doing now. */
    val settleTo = { target: Float, velocity: Float ->
        settling.value?.cancel()
        settleTarget.floatValue = target
        settling.value = scope.launch {
            animate(
                initialValue = travel.floatValue,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = settleSpring,
            ) { value, _ -> travel.floatValue = value }
        }
    }

    val leadingReveal = swipeRevealWidth(through = leadingActions.size)
    val trailingReveal = swipeRevealWidth(through = trailingActions.size)
    val oneActionReveal = swipeRevealWidth(through = 1)
    val commitInset = with(density) { COMMIT_INSET.toPx() }

    val actionsOn = { side: SwipeActionSide ->
        if (side == SwipeActionSide.Leading) leadingActions else trailingActions
    }
    val revealOn = { side: SwipeActionSide ->
        if (side == SwipeActionSide.Leading) leadingReveal else trailingReveal
    }
    // Where a commit parks the row: as far as it goes, less the sliver COMMIT_INSET keeps of it.
    val commitTravelOn = { side: SwipeActionSide ->
        maxOf(
            a = revealOn(side),
            b = rowWidth - commitInset,
        )
    }

    // The side the row would open onto with nothing having said otherwise: whichever edge has
    // actions, trailing first, so a row opened by its caller opens the way it always did.
    val restingSide = openSide
        ?: if (trailingActions.isNotEmpty()) SwipeActionSide.Trailing else SwipeActionSide.Leading
    // Where the row rests while open, signed: at the reveal, or wherever a commit is holding it.
    val restingTravel = restingSide.sign *
        if (held) commitTravelOn(restingSide) else revealOn(restingSide)

    // How far the commit has claimed the row off the finger, blended into the drawn travel so the
    // crossing springs rather than jumps.
    val claimed = animateFloatAsState(
        targetValue = if (committedSide != null) 1f else 0f,
        // Sprung on the way out, snapped on the way back: leaving a commit hands the row to
        // [resolveSwipeReleasedTravel], which picks it up exactly where the claim had it.
        animationSpec = if (committedSide != null) commitSpring else noSpring,
        label = "swipeCommitClaim",
    )
    // A lambda, so the travel is read where it is drawn and never in this composition. Resolved on
    // the magnitude and signed back.
    val shown = {
        val reached = travel.floatValue
        val side = swipeTravelSide(travel = reached)
            ?: restingSide
        val drawn = resolveSwipeDrawnTravel(
            travel = abs(reached),
            commitTravel = commitTravelOn(side),
            commitThreshold = swipeCommitThreshold(rowWidth = rowWidth),
            handingBack = releasing,
            claimed = claimed.value,
        )
        drawn * side.sign
    }
    // What one side's strip has been revealed by: nothing at all unless the row is showing it.
    val shownOn = { side: SwipeActionSide ->
        val reached = shown()
        if (swipeTravelSide(travel = reached) == side) abs(reached) else 0f
    }

    // Claiming the slot again is what keeps the group's own tap — the same one that fired this —
    // from closing the row underneath it.
    val fired: (SwipeAction) -> Unit = { action ->
        action.onClick()
        if (action.keepsRowOpen) {
            holding = true
            announce?.invoke(groupIdentity)
        } else {
            onOpenChange(false)
        }
    }

    // The caller is the source of truth: another row opening closes this one. Keyed on `open`
    // alone: a settle animates itself, because it usually writes the value `open` already holds
    // and this effect would not restart.
    LaunchedEffect(open) {
        if (open) {
            announce?.invoke(groupIdentity)
        } else {
            held = false
            committedSide = null
            holding = false
            // Safe to forget while the row travels home: the sign `travel` still carries is what
            // is drawn from.
            openSide = null
        }
        val want = if (open) restingTravel else 0f
        if (settleTarget.floatValue != want) settleTo(want, 0f)
    }

    // Another row took the slot, or the group was touched and nothing holds it. Keyed on the count
    // so a row hears every announcement, and ignored before the first so a row that starts open
    // stays that way.
    LaunchedEffect(groupSignal) {
        if (open && groupSignal.announcements > 0 && groupSignal.opener !== groupIdentity) {
            onOpenChange(false)
        }
    }

    // Either list can change while the row is open, and an open row would otherwise rest at a stale
    // offset. Keyed on the counts the reveals are a function of rather than on the reveal itself,
    // which also moves when the side does — and a drag that settles open is what sets the side, so
    // that key would restart every release's spring from rest a frame after it began. Never under a
    // live finger either, where it would fight the drag.
    LaunchedEffect(leadingActions.size, trailingActions.size) {
        if (open && !dragging && !held) {
            settleTo(restingSide.sign * revealOn(restingSide), 0f)
        }
    }

    val dragState = rememberDraggableState { delta ->
        val towards = delta * towardsTrailing
        val side = gestureSide.value
            ?: resolveSwipeGestureSide(travel = travel.floatValue, delta = towards)
        gestureSide.value = side
        val next = side?.let { owned ->
            resolveSwipeTravel(
                travel = travel.floatValue,
                delta = towards,
                side = owned,
                ceiling = resolveSwipeCeiling(
                    revealWidth = revealOn(owned),
                    rowWidth = rowWidth,
                    allowsFullSwipe = allowsFullSwipe,
                ),
            )
        }
            ?: 0f
        val crossed = side?.takeIf {
            swipeCrossedCommit(
                travel = next,
                rowWidth = rowWidth,
                allowsFullSwipe = allowsFullSwipe,
            )
        }
        if (crossed != committedSide) {
            committedSide = crossed
            releasing = crossed == null
            // Felt either way: crossing back is the moment the gesture stops belonging to the
            // action.
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        travel.floatValue = next
    }

    // The actions are still there while something they opened has the reader's attention, but they
    // are not what is being answered.
    val dim = animateFloatAsState(
        targetValue = if (holding) 1f else 0f,
        animationSpec = settleSpring,
        label = "swipeActionsDimmed",
    )

    Column(
        modifier = modifier
            .onSizeChanged { size -> rowWidth = size.width.toFloat() }
            .closeWhenScrolledPast(
                open = open,
                onClose = { onOpenChange(false) },
            ),
    ) {
        Box(
            modifier = Modifier
                .clipToBounds()
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled && (leadingActions.isNotEmpty() || trailingActions.isNotEmpty()),
                    onDragStarted = {
                        // The finger outranks whatever the row was doing.
                        settling.value?.cancel()
                        held = false
                        holding = false
                        releasing = false
                        dragging = true
                        gestureSide.value = null
                        // Announced on the first touch rather than when the row settles open: a
                        // reader who has started on another row has already left the open one.
                        announce?.invoke(groupIdentity)
                    },
                    onDragStopped = { velocity ->
                        dragging = false
                        val side = gestureSide.value
                            ?: restingSide
                        gestureSide.value = null
                        // Everything below then reads as it always did: travel and velocity both
                        // positive while the row is still opening.
                        val sign = side.sign
                        // Read before the claim is folded in: a drag coming back from a commit
                        // draws the row ahead of the finger, and settling on the drawn value would
                        // fire the action from a third of the way across.
                        val reached = abs(travel.floatValue)
                        travel.floatValue = shown()
                        releasing = false
                        val released = velocity * towardsTrailing * sign
                        val target = resolveSwipeSettle(
                            travel = reached,
                            velocity = released,
                            // Nothing to open onto is what closes a release on an edge with
                            // nothing behind it: an empty side's reveal is zero.
                            firstActionReveal = minOf(
                                a = revealOn(side),
                                b = oneActionReveal,
                            ),
                            rowWidth = rowWidth,
                            allowsFullSwipe = allowsFullSwipe,
                        )
                        val commits = target == SwipeSettleTarget.Committed
                        val first = actionsOn(side).firstOrNull()
                        // A commit fires the first action, so the row rests where that action
                        // asks: away, or held all the way across, the action still stretched,
                        // behind whatever the action has just put on screen.
                        val holds = commits && first?.keepsRowOpen == true
                        val opens = target == SwipeSettleTarget.Open || holds
                        held = holds
                        holding = holds
                        committedSide = if (holds) side else null
                        openSide = if (opens) side else null
                        onOpenChange(opens)
                        if (commits) {
                            // Before the animation, not after: animateTo suspends until it
                            // settles, and the action must not wait on a spring.
                            first?.onClick()
                        }
                        // Always animates: settling usually writes the value `open` already holds,
                        // so nothing else would move the row off where the finger left it.
                        val settlesTo = when {
                            holds -> sign * commitTravelOn(side)
                            opens -> sign * revealOn(side)
                            else -> 0f
                        }
                        settleTo(settlesTo, released * sign)
                    },
                )
                // Merged, so TalkBack focuses this node instead of the merging node the wrapped
                // item's own `clickable` creates below it. Unmerged, the container is never
                // focused and its actions are never announced.
                .semantics(mergeDescendants = true) {
                    // Through `fired`, not straight to `onClick`: an action reached this way has to
                    // close or hold the row exactly as a tapped one does. Gated like the capsules,
                    // so a reader cannot fire an action the row has drawn inert. Both edges in the
                    // order a reader would find them: the gesture is what is invisible here, not
                    // the side.
                    customActions = if (enabled && !holding) {
                        swipeRowCustomActions(
                            actions = leadingActions + trailingActions,
                            onFired = fired,
                        )
                    } else {
                        emptyList()
                    }
                },
        ) {
            // An edge with no actions is left out entirely: an empty strip still reads the row's
            // travel in its own composition, so it would recompose every frame of every drag to
            // draw nothing. Emptiness is not a function of travel, so this cannot fire
            // mid-gesture.
            SwipeActionSide.entries.forEach { side ->
                key(side) {
                    val sideActions = actionsOn(side)
                    if (sideActions.isNotEmpty()) {
                        SwipeActionStrip(
                            actions = sideActions,
                            side = side,
                            travel = { shownOn(side) },
                            rowWidth = { rowWidth },
                            committed = { committedSide == side },
                            // How far the first action has stretched once a commit has parked the
                            // row: what the icon is sliding towards from the crossing onwards.
                            committedStretch = with(density) {
                                (commitTravelOn(side) - revealOn(side)).toDp()
                            },
                            onFired = fired,
                            dim = { dim.value },
                            holding = holding,
                            modifier = Modifier.align(
                                if (side == SwipeActionSide.Leading) {
                                    Alignment.CenterStart
                                } else {
                                    Alignment.CenterEnd
                                },
                            ),
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (shown() * towardsTrailing).roundToInt(),
                            y = 0,
                        )
                    }.swipeRowHighlight(handled = dragging || open),
            ) {
                content()
                // A sibling drawn above the content, not a `clickable` on its parent: Compose
                // dispatches pointers children-first, so the wrapped item's own `clickable`
                // consumes the down and a parent would never see it.
                if (open) {
                    SwipeRowTapToClose(
                        onClose = { onOpenChange(false) },
                        modifier = Modifier.matchParentSize(),
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
 *     trailingActions = listOf(
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
 *
 * Actions may sit on either edge, or both. A drag takes the side it sets off towards and keeps it
 * for the rest of the gesture, so one drag never reveals both. A row opened by its caller rather
 * than by a drag opens onto [trailingActions], falling back to [leadingActions] only when there
 * are none.
 *
 * @param leadingActions the actions revealed on the leading edge, outermost first
 * @param trailingActions the actions revealed on the trailing edge, outermost first
 * @param modifier [Modifier] applied to the base container
 * @param enabled whether the drag is active
 * @param allowsFullSwipe whether dragging across the row fires the first action of whichever edge
 *  is being dragged, on release
 * @param showDivider whether to show a divider below the row, which does not travel with it
 * @param content the row this wraps
 */
@Composable
public fun LemonadeUi.SwipeActionRow(
    leadingActions: List<SwipeAction> = emptyList(),
    trailingActions: List<SwipeAction> = emptyList(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allowsFullSwipe: Boolean = true,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    SwipeActionRowCore(
        open = open,
        onOpenChange = { opening -> open = opening },
        leadingActions = leadingActions,
        trailingActions = trailingActions,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}

/**
 * Binary compatibility for callers compiled against the row when its only actions were the trailing
 * ones. Keeps the symbol that shipped, delegating to the overload above.
 */
@Deprecated(
    message = "Use trailingActions.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.SwipeActionRow(
    actions: List<SwipeAction>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allowsFullSwipe: Boolean = true,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    SwipeActionRow(
        leadingActions = emptyList(),
        trailingActions = actions,
        modifier = modifier,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        content = content,
    )
}

/**
 * [SwipeActionRow] whose open row is controlled by the caller.
 *
 * Keeping one row open at a time needs nothing from the caller — that is what
 * [LemonadeUi.SwipeActionGroup] is for, and it works on these rows too. Reach for this overload
 * when the caller has to be able to close the row itself: after an action with
 * [SwipeAction.keepsRowOpen] has fired, the row waits on the reader, and only an [openId] the
 * caller owns can put it back.
 *
 * @param id identity of this row, compared against [openId]
 * @param openId identity of the row currently open, or null when none is
 * @param onOpenIdChange called with this row's [id] when it opens and null when it closes
 */
@Composable
public fun LemonadeUi.SwipeActionRow(
    id: Any,
    openId: Any?,
    onOpenIdChange: (Any?) -> Unit,
    leadingActions: List<SwipeAction> = emptyList(),
    trailingActions: List<SwipeAction> = emptyList(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    allowsFullSwipe: Boolean = true,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    SwipeActionRowCore(
        open = openId == id,
        onOpenChange = { opening ->
            when {
                opening -> onOpenIdChange(id)
                // Only ever clears its own slot: a drag that settles closed on one row would
                // otherwise close whichever row the caller actually has open.
                openId == id -> onOpenIdChange(null)
            }
        },
        leadingActions = leadingActions,
        trailingActions = trailingActions,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}

/**
 * Binary compatibility for callers compiled against the controlled row when its only actions were
 * the trailing ones. Keeps the symbol that shipped, delegating to the overload above.
 */
@Deprecated(
    message = "Use trailingActions.",
    level = DeprecationLevel.HIDDEN,
)
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
    SwipeActionRow(
        id = id,
        openId = openId,
        onOpenIdChange = onOpenIdChange,
        leadingActions = emptyList(),
        trailingActions = actions,
        modifier = modifier,
        enabled = enabled,
        allowsFullSwipe = allowsFullSwipe,
        showDivider = showDivider,
        content = content,
    )
}
