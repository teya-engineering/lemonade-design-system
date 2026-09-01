package com.teya.lemonade

/** Fraction of the row's width a drag must cross for a full swipe to commit. */
private const val CommitFraction = 0.5f

/** Speed, in px/s, past which a flick decides the settle regardless of how far it travelled. */
private const val FlingVelocity = 400f

/** Where a released drag lands. */
internal enum class SwipeSettleTarget {
    Closed,
    Open,
    Committed,
}

/**
 * Resolves where a released drag settles.
 *
 * A commit outranks everything: once the row has crossed [CommitFraction] of its width the gesture
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
): SwipeSettleTarget = when {
    allowsFullSwipe && travel >= rowWidth * CommitFraction -> SwipeSettleTarget.Committed
    velocity >= FlingVelocity -> SwipeSettleTarget.Open
    velocity <= -FlingVelocity -> SwipeSettleTarget.Closed
    travel >= revealWidth / 2f -> SwipeSettleTarget.Open
    else -> SwipeSettleTarget.Closed
}
