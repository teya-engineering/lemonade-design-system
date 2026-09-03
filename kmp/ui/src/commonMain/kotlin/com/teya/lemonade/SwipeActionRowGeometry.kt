package com.teya.lemonade

/*
 * The geometry a swipe action row is drawn from.
 *
 * Pure functions of the row's travel, kept apart from the component that animates them: nothing
 * here springs, remembers, or reads a theme, so every rule the swipe follows can be pinned by a
 * test. The component decides what the travel is; this decides what the row looks like at that
 * travel, and where a released one lands.
 */

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
        // Nothing to open onto: there are no actions. Asked before the commit, because a row
        // with nothing behind it has nothing to fire.
        firstActionReveal <= 0f -> SwipeSettleTarget.Closed

        // A row that has not been measured has no width to have crossed half of: the threshold
        // would be zero, and every release — including one that never moved — would commit.
        allowsFullSwipe && rowWidth > 0f && travel >= swipeCommitThreshold(rowWidth = rowWidth) ->
            SwipeSettleTarget.Committed
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
