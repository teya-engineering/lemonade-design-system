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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
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
 * Stiffness of the spring a released row travels on, and the one animation the reveal rides.
 *
 * Fitted to iOS frame by frame: a settle of 90dp lands within 0.123dp of a critically damped spring
 * at ω = 12.5 rad/s across the whole animation, which is inside the pixel quantisation of the
 * measurement. Stiffness is ω² at unit mass, and [Spring.DampingRatioNoBouncy] is the critical
 * damping — the row arrives without springing past and coming back.
 */
private const val SETTLE_STIFFNESS = 156.25f

/**
 * How far the row may drift on screen before an open one counts as scrolled past. Enough to sit out
 * the rounding a layout pass can move it by, and far short of a deliberate scroll.
 */
private val SCROLL_SLACK = 4.dp

/**
 * Fraction of the row's width a drag must cross for a full swipe to commit.
 *
 * Measured off iOS frame by frame: a 440pt row commits as the drag passes 240pt, which is 0.546 of
 * it. Far enough past halfway that the reader has to mean it.
 */
private const val COMMIT_FRACTION = 0.55f

/**
 * Travel a drag has to cross for a full swipe to commit, for a row [rowWidth] px wide.
 *
 * One place, because everything hangs off it: the haptic, the icon's slide, the strip's dimming,
 * where the row parks, and whether a release fires the action. Restating it is how the five drift.
 */
internal fun swipeCommitThreshold(rowWidth: Float): Float = rowWidth * COMMIT_FRACTION

/**
 * What is left of the row on screen once a commit has claimed it. iOS stops the row 18.3pt short of
 * carrying it off, which keeps the row a row rather than a bare action.
 */
private val COMMIT_INSET = 20.dp

/**
 * Stiffness of the spring a commit claims the row on — ω = 35 rad/s, a fifth of a second end to
 * end, against the settle's half. Fitted to iOS frame by frame: the action reaches half its width
 * within 50ms of the crossing and 94% within 130ms, which a critically damped spring at that ω
 * tracks to within a frame across the whole animation.
 */
private const val COMMIT_STIFFNESS = 1225f

/** Deceleration a released row is left to coast on, matching a scroll's normal rate. */
private const val DECELERATION_RATE = 0.998f

/**
 * Travel that rests the row on the first [count] actions: the whole reveal at every action, and one
 * action's own share of it at its index plus one.
 *
 * Computed rather than measured: the strip changes width as the first action stretches, so anything
 * measured off it would move under the model driving it.
 *
 * @param count how many of the row's actions the row rests on.
 * @param actionWidth width of one action, in px.
 * @param gap space between two actions, in px.
 * @param padding space ahead of the first action and behind the last, in px.
 */
internal fun resolveSwipeRevealWidth(
    count: Int,
    actionWidth: Float,
    gap: Float,
    padding: Float,
): Float {
    if (count <= 0) {
        return 0f
    }
    return padding + count * actionWidth + (count - 1) * gap
}

/**
 * Where a drag that let go at [velocity] px/s would have come to rest: the distance a second of that
 * speed covers, scaled by how long the deceleration takes to eat it.
 */
private fun projectedTravel(
    travel: Float,
    velocity: Float,
): Float = travel + velocity / 1000f * DECELERATION_RATE / (1f - DECELERATION_RATE)

/** The spring [SETTLE_STIFFNESS] describes, for the row's own travel. */
private val settleSpring: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = SETTLE_STIFFNESS,
)

/** The spring [COMMIT_STIFFNESS] describes, for the claim a commit takes of the row. */
private val commitSpring: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = COMMIT_STIFFNESS,
)

/** The spring [BUMP_STIFFNESS] describes, for an action landing. */
private val bumpSpring: SpringSpec<Float> = spring(
    dampingRatio = BUMP_DAMPING,
    stiffness = BUMP_STIFFNESS,
)

/** No spring at all: the value is already where it needs to be. */
private val noSpring: SnapSpec<Float> = snap()

/**
 * Where the row is drawn while a drag is bringing it back from a commit.
 *
 * The claim is not handed back on a spring of its own, which would step the row across whatever the
 * finger is doing. The row keeps the lead the commit gave it and gives it up in proportion to the
 * finger, so it arrives home exactly as the finger does. Continuous at the crossing by
 * construction: a drag can only leave a commit at the threshold, and the threshold times the gain
 * is where the commit had it.
 *
 * @param travel where the finger has the row, always positive.
 * @param commitTravel where a commit parks the row.
 * @param threshold travel a drag commits at.
 */
internal fun resolveSwipeReleasedTravel(
    travel: Float,
    commitTravel: Float,
    threshold: Float,
): Float {
    if (threshold <= 0f) {
        return travel
    }
    return minOf(travel * (commitTravel / threshold), commitTravel)
}

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
 * again should not undo it.
 *
 * Otherwise the row stays open only if the drag would have brought the first action all the way out
 * — not where the finger let go, but where the row's own momentum was taking it. A flick opens a row
 * the finger never carried that far, and a slow drag of the same length does not, off one threshold
 * rather than a speed rule sitting in front of it. It is also what closes a row flung back: the
 * projection lands short of the threshold.
 *
 * The commit is the exception, and reads [travel] itself. Momentum must not fire an action across a
 * row the finger never crossed.
 *
 * @param travel distance the row has moved from closed, always positive.
 * @param velocity px/s at release, positive while still travelling open.
 * @param firstActionReveal travel that brings the first action fully out, which is what a release
 *  has to reach for the row to stay open.
 * @param rowWidth full width of the row.
 * @param allowsFullSwipe whether a drag across the row may commit the first action.
 */
internal fun resolveSwipeSettle(
    travel: Float,
    velocity: Float,
    firstActionReveal: Float,
    rowWidth: Float,
    allowsFullSwipe: Boolean,
): SwipeSettleTarget =
    when {
        allowsFullSwipe && travel >= swipeCommitThreshold(rowWidth = rowWidth) ->
            SwipeSettleTarget.Committed

        // Nothing to open onto: there are no actions.
        firstActionReveal <= 0f -> SwipeSettleTarget.Closed
        projectedTravel(travel = travel, velocity = velocity) >= firstActionReveal ->
            SwipeSettleTarget.Open

        else -> SwipeSettleTarget.Closed
    }

/**
 * Opacity an action being pushed along has dimmed to once the row has travelled its whole width.
 * `opacity20`, held as a plain number so the reveal stays resolvable without a theme.
 */
private const val DISPLACED_FLOOR = 0.2f

/**
 * Opacity of the actions a stretching one is pushing along.
 *
 * A swipe past the commit threshold is taking the row over, and the actions it is displacing recede
 * as it does rather than riding out at full strength: unchanged through the reveal, down to
 * [DISPLACED_FLOOR] by the time the row has travelled its whole width.
 *
 * @param travel distance the row has moved from closed, always positive.
 * @param rowWidth full width of the row.
 */
internal fun resolveSwipeDisplacedOpacity(
    travel: Float,
    rowWidth: Float,
): Float {
    val takeover = swipeCommitThreshold(rowWidth = rowWidth)
    if (rowWidth <= takeover) {
        return 1f
    }
    val progress = ((travel - takeover) / (rowWidth - takeover)).coerceIn(0f, 1f)
    return 1f - (1f - DISPLACED_FLOOR) * progress
}

/**
 * How much of an action's arrival is held back for the end.
 *
 * The last of it springs into place when the row has revealed the action fully, so it lands rather
 * than simply stopping. Measured off iOS, where an action's scale rings about 3% past its resting
 * size before settling — a bounce this shallow overshoots by about that, and at 1.5dp of a 48dp
 * action it stays well inside the gap the action sits in.
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
 * [BUMP_DAMPING] is what makes it ring — 0.4 overshoots by about 2%, the same as iOS.
 */
private const val BUMP_STIFFNESS = 631f
private const val BUMP_DAMPING = 0.4f

/** How the action strip draws itself part-way through a reveal. */
internal data class SwipeStripReveal(
    /**
     * 0..1. The strip scales about its centre by this and fades by the same amount, so an action
     * arriving grows and appears as one movement.
     */
    val scale: Float,
    /** Width added to the leading side of the first action once the strip is at full size. */
    val stretch: Float,
)

/**
 * Resolves how far one action has been revealed.
 *
 * An action is the gap the row has opened for it: [travel] less everything between it and the row's
 * trailing edge is the width it wants, and the rest follows from that. Short of its own width it
 * scales into it; past it the first action — the one a full swipe fires — stretches to fill it.
 * Because that width *is* the gap, an action never reaches under the row, whatever the row is drawn
 * on.
 *
 * Resolved per action rather than for the strip, so each one arrives as the row uncovers it: the
 * second action of a pair waits until the row has cleared the first.
 *
 * Nothing here is animated. Everything is a function of where the row is, so an action can never get
 * out of step with the row it belongs to — a spring chasing the gap overshoots into it the moment
 * the row comes back. The row's own animation carries all of it.
 *
 * The stretch waits for the whole strip, not for this action's share of it. An action that grew into
 * its own leftover would grow over the actions still queued behind it — for the outermost of a pair,
 * from the moment the row rests open.
 *
 * @param travel distance the row has moved from closed, always positive.
 * @param actionReveal travel that rests the row on this action, so the strip's width up to and
 *  including it.
 * @param stripReveal travel that rests the row on every action, which is where a stretch starts.
 * @param actionWidth the action's own width, which is what it scales towards.
 */
internal fun resolveSwipeStripReveal(
    travel: Float,
    actionReveal: Float,
    stripReveal: Float,
    actionWidth: Float,
): SwipeStripReveal {
    if (actionWidth <= 0f) {
        return SwipeStripReveal(scale = 0f, stretch = 0f)
    }
    // An action grows over the last half of its own width. Scaling about its centre, that walks its
    // leading edge out at exactly the rate the row is travelling — so from nothing to full size,
    // and on past it as the capsule stretches, the action's leading edge sits one leading gap ahead
    // of the row. The action *is* the gap the row has opened, at every point of the drag.
    val growth = actionWidth / 2f
    val scale = ((travel - (actionReveal - growth)) / growth)
        .coerceIn(minimumValue = 0f, maximumValue = 1f)
    return SwipeStripReveal(
        scale = scale,
        stretch = (travel - stripReveal).coerceAtLeast(minimumValue = 0f),
    )
}

/**
 * What a group tells its rows: one of them has taken the open slot, or nothing has.
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
 * Rows manage themselves inside it, including the ones given an `id` and `openId`, so nothing has to
 * be hoisted to get this.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.SwipeActionGroup {
 *     Column {
 *         accounts.forEach { account ->
 *             LemonadeUi.SwipeActionRow(actions = listOf(remove(account))) {
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
 * @param modifier - [Modifier] applied to the group.
 * @param content - the rows, and whatever else the group covers.
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
    CompositionLocalProvider(
        LocalSwipeActionGroupSignal provides signal,
        LocalSwipeActionGroupAnnounce provides announce,
    ) {
        Box(
            modifier = modifier.pointerInput(Unit) {
                awaitEachGesture {
                    // Watched on the initial pass and never consumed, so the tap still reaches
                    // whatever was tapped. Closing an open row is not meant to cost the reader the
                    // tap that closed it. A gesture that travelled is a swipe, and a row settling
                    // out of one announces itself.
                    val down = awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial,
                    )
                    var travelled = 0f
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        travelled += (change.position - change.previousPosition).getDistance()
                        if (!change.pressed) {
                            // Settled a frame later, and only if nothing claimed the slot
                            // meanwhile. The tap that fires an action is this same tap, seen here
                            // on the initial pass and by the action on the final one — waiting a
                            // frame means a row that has claimed the slot has said so by the time
                            // this decides, and this leaves it alone.
                            if (travelled < viewConfiguration.touchSlop) {
                                val seen = signal.announcements
                                scope.launch {
                                    withFrameNanos { }
                                    if (signal.announcements == seen) announce(null)
                                }
                            }
                            break
                        }
                    }
                }
            },
        ) {
            content()
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
 * row, or the caller does through the `openId` overload. An action that opens something modal
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

/**
 * The actions behind the row, drawn as far as the row has revealed them.
 *
 * Takes the row's travel rather than the geometry read off it, so the whole reveal is resolved in
 * one place and an action's arrival cannot drift from the width it is derived from.
 */
@Composable
private fun SwipeActionStrip(
    actions: List<SwipeAction>,
    travel: () -> Float,
    rowWidth: () -> Float,
    committed: () -> Boolean,
    committedStretch: Dp,
    onFired: (SwipeAction) -> Unit,
    dim: () -> Float,
    modifier: Modifier = Modifier,
) {
    val step = LemonadeTheme.sizes.size1200 + LemonadeTheme.spaces.spacing200
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .padding(
                start = LemonadeTheme.spaces.spacing300,
                end = LemonadeTheme.spaces.spacing400,
            )
            // The actions stay in the semantics tree even while the row covers them, which would
            // duplicate every custom action. The gesture is visual; the custom action is the
            // accessible path.
            .clearAndSetSemantics { },
        contentAlignment = Alignment.CenterEnd,
    ) {
        // Outermost last, so it is drawn on top: the first action is the one a full swipe fires,
        // and the one that stretches over the actions beside it.
        val shown = travel()
        val actionWidth = with(density) { LemonadeTheme.sizes.size1200.toPx() }
        val stripReveal = swipeRevealWidth(through = actions.size)
        actions.indices.reversed().forEach { index ->
            val revealed = resolveSwipeStripReveal(
                travel = shown,
                actionReveal = swipeRevealWidth(through = index + 1),
                stripReveal = stripReveal,
                actionWidth = actionWidth,
            )
            // The slack goes to the first action's width and to everything else's position, so a
            // stretching action pushes the ones beside it along rather than growing over them.
            // Their gaps hold, and the strip still ends exactly one leading gap ahead of the row
            // however far it is dragged.
            val push = if (index == 0) 0.dp else with(density) { revealed.stretch.toDp() }
            SwipeActionCapsule(
                action = actions[index],
                reveal = revealed,
                // Each action lands as the row clears it, so the second of a pair bumps in on its
                // own rather than with the first.
                arrived = revealed.scale >= BUMP_TRIGGER,
                opacity = if (index == 0) {
                    1f
                } else {
                    resolveSwipeDisplacedOpacity(travel = shown, rowWidth = rowWidth())
                },
                stretches = index == 0,
                committed = committed() && index == 0,
                committedStretch = committedStretch,
                onFired = onFired,
                dim = dim(),
                modifier = Modifier.offset(x = -step * index - push),
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
    onFired: (SwipeAction) -> Unit,
    dim: Float,
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
    // The press and hover treatment LemonadeUi.IconButton gives its own button, off the same rule.
    val interactionSource = remember { MutableInteractionSource() }
    val background by colors.animatedBackground(interactionSource = interactionSource)
    // Taken straight off the row's position rather than animated: an action that springs towards
    // the gap overshoots into it the moment the row comes back, and settling would animate a spring
    // of its own that no longer agrees with the row's.
    val stretch = with(LocalDensity.current) {
        (if (stretches) reveal.stretch else 0f).toDp()
    }
    // Centred in the capsule until the swipe commits, then it slides to the centre of the capsule's
    // leading end — where the action would sit if it had stayed a circle and the row had simply
    // carried on past it.
    //
    // Against the width the commit is heading for rather than the one it has: a spring chasing a
    // target that is itself still moving never catches it, which left the icon a third of its
    // travel behind the capsule it slides in. iOS holds the two within 0.012 of each other the
    // whole way, which is what this is.
    //
    // A claim of its own, not the row's: that one snaps back so the row can be handed to
    // [resolveSwipeReleasedTravel] where it stands, while the icon is sprung both ways — it comes
    // back to the centre the moment the gesture stops belonging to the action, rather than popping
    // there.
    val iconClaim by animateFloatAsState(
        targetValue = if (committed) 1f else 0f,
        animationSpec = commitSpring,
        label = "swipeActionIcon",
    )
    val iconOffset = -committedStretch / 2 * iconClaim
    Box(
        modifier = modifier
            .size(width = size + stretch, height = size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = scale * opacity * (1f - dim * (1f - dimFloor))
            }.clip(shape = LemonadeTheme.shapes.radiusFull)
            .clickable(
                onClick = {
                    onFired(action)
                },
                role = Role.Button,
                interactionSource = interactionSource,
                indication = LocalEffects.current.interactionIndication,
            ).background(color = drained(color = background, amount = dim)),
        contentAlignment = Alignment.Center,
    ) {
        LemonadeUi.Icon(
            icon = action.icon,
            contentDescription = action.contentDescription,
            size = LemonadeAssetSize.Large,
            tint = drained(color = colors.contentColor, amount = dim),
            modifier = Modifier.offset(x = iconOffset),
        )
    }
}

/**
 * [color] drained of [amount] of its colour: what a grayscale filter would leave of it, mixed back
 * in by how far the drain has gone.
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
        stop = Color(red = luma, green = luma, blue = luma, alpha = color.alpha),
        fraction = amount,
    )
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
    // A plain value the drag writes as it happens, not an `Animatable` a launched coroutine
    // catches up with. Every delta used to launch its own `snapTo`, and one landing after the
    // settle had started cancelled it — an `Animatable` lets the later mutation win — leaving the
    // row parked wherever the finger let go until something else moved it.
    // Held rather than read: nothing in this function's body may touch `travel`, or the row
    // recomposes on every frame of every drag and every settle — and with it the content it wraps,
    // which is the caller's whole list item. The layout and draw lambdas below read it instead, so
    // a moving row is re-laid-out and redrawn without being composed again.
    //
    // [SwipeActionStrip] is the exception, by design: it reads the lambda in its own composition,
    // because an action's width and scale are composition-level. A drag recomposes the strip and
    // its handful of capsules, and nothing above them.
    val travel = remember { mutableFloatStateOf(0f) }
    // The one animation allowed to write `travel`, held so a new one, or a finger, can end it.
    val settling = remember { mutableStateOf<Job?>(null) }
    var rowWidth by remember { mutableFloatStateOf(0f) }
    // What this row answers to inside a group. Its own, so a row needs no identity from the caller
    // to take part.
    val groupIdentity = remember { Any() }
    val groupSignal = LocalSwipeActionGroupSignal.current
    val announce = LocalSwipeActionGroupAnnounce.current
    // Where the row sits on screen, and where it sat when it opened. A row that has moved since is
    // being scrolled past, and an open row scrolling away is one the reader has left behind.
    var rowY by remember { mutableFloatStateOf(0f) }
    var openedAt by remember { mutableStateOf<Float?>(null) }
    val scrollSlack = with(LocalDensity.current) { SCROLL_SLACK.toPx() }
    var committed by remember { mutableStateOf(false) }
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
        settling.value = scope.launch {
            animate(
                initialValue = travel.floatValue,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = settleSpring,
            ) { value, _ -> travel.floatValue = value }
        }
    }

    val revealWidth = swipeRevealWidth(through = actions.size)
    val firstActionReveal = swipeRevealWidth(through = 1)
    // Where a commit parks the row: as far as it goes, less the sliver iOS leaves of it.
    val commitTravel = maxOf(revealWidth, rowWidth - with(density) { COMMIT_INSET.toPx() })
    // Where the row rests while open: at the reveal, or wherever a commit is holding it.
    val restingTravel = if (held) commitTravel else revealWidth
    // How far the first action has stretched once a commit has parked the row: what the icon is
    // sliding towards from the moment the crossing happens.
    val committedStretch = with(density) { (commitTravel - revealWidth).toDp() }

    // How far the commit has claimed the row off the finger. Crossing the threshold takes the row
    // out of the drag's hands and carries it the rest of the way itself; dragging back below hands
    // it back. The blend is what makes both a spring rather than a jump, and what keeps the finger
    // in charge on the way there.
    val claimed = animateFloatAsState(
        targetValue = if (committed) 1f else 0f,
        // Sprung on the way out, snapped on the way back: leaving a commit hands the row to
        // [resolveSwipeReleasedTravel], which picks it up exactly where the claim had it.
        animationSpec = if (committed) commitSpring else noSpring,
        label = "swipeCommitClaim",
    )
    // What the row draws, resolved wherever it is needed rather than here: the finger's own travel,
    // or the lead a commit gave it being given back in proportion to the finger, blended with
    // however far the commit has claimed the row.
    val shown = {
        val reached = travel.floatValue
        val base = if (releasing) {
            resolveSwipeReleasedTravel(
                travel = reached,
                commitTravel = commitTravel,
                threshold = swipeCommitThreshold(rowWidth = rowWidth),
            )
        } else {
            reached
        }
        base + (commitTravel - base) * claimed.value
    }

    // A tapped action tidies the row away after it, unless it has put something on screen that the
    // row is the subject of. Claiming the slot again is what keeps the group's own tap — the same
    // one that fired this — from closing the row underneath it.
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
        openedAt = if (open) rowY else null
        if (open) {
            announce?.invoke(groupIdentity)
        } else {
            held = false
            committed = false
            holding = false
        }
        settleTo(if (open) restingTravel else 0f, 0f)
    }

    // Another row took the slot, or the group was touched and nothing holds it. Keyed on the count
    // so a row hears every announcement, and ignored before the first so a row that starts open
    // stays that way.
    LaunchedEffect(groupSignal) {
        if (open && groupSignal.announcements > 0 && groupSignal.opener !== groupIdentity) {
            onOpenChange(false)
        }
    }

    // `actions` can change while the row is open, and an open row would otherwise rest at a stale
    // offset. Never under a live finger, where it would fight the drag.
    LaunchedEffect(revealWidth) {
        if (open && !dragging && !held) settleTo(revealWidth, 0f)
    }

    val dragState = rememberDraggableState { delta ->
        val ceiling = if (allowsFullSwipe) rowWidth else revealWidth
        val next = (travel.floatValue + delta * towardsTrailing).coerceIn(0f, ceiling)
        val crossed = allowsFullSwipe && next >= swipeCommitThreshold(rowWidth = rowWidth)
        if (crossed != committed) {
            committed = crossed
            releasing = !crossed
            // Felt either way: crossing back is the moment the gesture stops belonging to the
            // action, which is as worth knowing as the moment it started to.
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        travel.floatValue = next
    }

    val highlight = LemonadeTheme.colors.interaction.bgSubtleInteractive
    // On the moment a finger takes the row, gone by the time it has carried it home. Snapping in
    // and easing out rather than following the travel: keyed off the travel it would blink off the
    // frame the row landed, and the row is still being handled until it has finished arriving.
    val handled = dragging || open
    val highlightAlpha = animateFloatAsState(
        targetValue = if (handled) 1f else 0f,
        animationSpec = if (handled) noSpring else settleSpring,
        label = "swipeRowHighlight",
    )
    // Drained of colour and dimmed while something an action opened has the reader's attention:
    // the actions are still there, and still where they were, but they are not what is being
    // answered.
    val dim = animateFloatAsState(
        targetValue = if (holding) 1f else 0f,
        animationSpec = settleSpring,
        label = "swipeActionsDimmed",
    )
    val gutterPx = with(density) { LemonadeTheme.spaces.spacing100.toPx() }
    val highlightRadiusPx = with(density) { LemonadeTheme.radius.radius500.toPx() }

    Column(
        modifier = modifier
            .onSizeChanged { rowWidth = it.width.toFloat() }
            .onGloballyPositioned { coordinates ->
                val y = coordinates.positionInRoot().y
                rowY = y
                // Scrolled past, so the row is no longer the one being read.
                val opened = openedAt
                if (open && opened != null && abs(y - opened) > scrollSlack) onOpenChange(false)
            },
    ) {
        Box(
            modifier = Modifier
                .clipToBounds()
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled && actions.isNotEmpty(),
                    onDragStarted = {
                        // The finger outranks whatever the row was doing, and nothing is holding
                        // the row any more: this drag settles it wherever it asks.
                        settling.value?.cancel()
                        held = false
                        holding = false
                        releasing = false
                        dragging = true
                        // Claimed, so this is the row being read now. Announced here rather than
                        // when the row settles open: a reader who has started on another row has
                        // already left the open one, and waiting for the release leaves it
                        // sitting there through the whole gesture.
                        announce?.invoke(groupIdentity)
                    },
                    onDragStopped = { velocity ->
                        dragging = false
                        // Where the finger left the row, read before the claim is folded in. A drag
                        // coming back from a commit draws the row ahead of the finger — by the
                        // gain in [resolveSwipeReleasedTravel] — and settling on the drawn value
                        // would fire the action from a third of the way across, after crossing
                        // back had already told the reader the gesture was no longer its.
                        val reached = travel.floatValue
                        // The claim is spent: the row settles from where it is being drawn.
                        travel.floatValue = shown()
                        releasing = false
                        // The spring picks up the speed the finger let go at rather than starting
                        // from rest, so the row carries straight on out of the drag.
                        val released = velocity * towardsTrailing
                        val target = resolveSwipeSettle(
                            travel = reached,
                            velocity = released,
                            firstActionReveal = firstActionReveal,
                            rowWidth = rowWidth,
                            allowsFullSwipe = allowsFullSwipe,
                        )
                        committed = false
                        // Every branch animates: settling usually writes the value `open` already
                        // holds, so nothing else would move the row off where the finger left it.
                        when (target) {
                            SwipeSettleTarget.Committed -> {
                                val first = actions.firstOrNull()
                                // A commit fires the first action, so the row rests where that
                                // action asks: away, or held all the way across, the action still
                                // stretched, behind whatever the action has just put on screen.
                                val holds = first?.keepsRowOpen == true
                                held = holds
                                committed = holds
                                holding = holds
                                onOpenChange(holds)
                                // Before the animation, not after: animateTo suspends until it
                                // settles, and the action must not wait on a spring.
                                first?.onClick()
                                settleTo(if (holds) commitTravel else 0f, released)
                            }

                            SwipeSettleTarget.Open -> {
                                onOpenChange(true)
                                settleTo(revealWidth, released)
                            }

                            SwipeSettleTarget.Closed -> {
                                onOpenChange(false)
                                settleTo(0f, released)
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
                travel = shown,
                rowWidth = { rowWidth },
                committed = { committed },
                committedStretch = committedStretch,
                onFired = fired,
                dim = { dim.value },
                modifier = Modifier.align(Alignment.CenterEnd),
            )
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = (shown() * towardsTrailing).roundToInt(), y = 0) }
                    // A row under the finger rests on the list item's own press highlight rather
                    // than on a surface of its own: same fill, same radius, same gutter. It is on
                    // for the whole gesture, not proportional to the travel — the row is being
                    // handled from the first pixel.
                    .drawBehind {
                        if (highlightAlpha.value > 0f) {
                            val gutter = gutterPx
                            drawRoundRect(
                                color = highlight,
                                topLeft = Offset(x = gutter, y = gutter),
                                size = Size(size.width - gutter * 2, size.height - gutter * 2),
                                cornerRadius = CornerRadius(highlightRadiusPx),
                                alpha = highlightAlpha.value,
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
 * [SwipeActionRow] whose open row is controlled by the caller.
 *
 * Keeping one row open at a time needs nothing from the caller — that is what
 * [LemonadeUi.SwipeActionGroup] is for, and it works on these rows too. Reach for this overload
 * when the caller has to be able to close the row itself: after an action with
 * [SwipeAction.keepsRowOpen] has fired, the row waits on the reader, and only an `openId` the
 * caller owns can put it back.
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
