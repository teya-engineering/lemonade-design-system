import SwiftUI

// MARK: - Settle policy

/// Fraction of the row's width a drag must cross for a full swipe to commit.
private let commitFraction: CGFloat = 0.5

/// Speed, in pt/s, past which a flick decides the settle regardless of how far it travelled.
private let flingVelocity: CGFloat = 400

/// Where a released drag lands.
enum SwipeSettleTarget {
    case closed
    case open
    case committed
}

/// Resolves where a released drag settles.
///
/// A commit outranks everything: once the row has crossed `commitFraction` of its width the gesture
/// has already been read as a full swipe, and dragging back at speed without crossing the threshold
/// again should not undo it. Otherwise a flick wins over position, so a short fast drag opens.
///
/// - Parameters:
///   - travel: distance the row has moved from closed, always positive.
///   - velocity: pt/s at release, positive while still travelling open.
///   - revealWidth: width of the action strip, which is where an open row rests.
///   - rowWidth: full width of the row.
///   - allowsFullSwipe: whether a drag across the row may commit the first action.
func resolveSwipeSettle(
    travel: CGFloat,
    velocity: CGFloat,
    revealWidth: CGFloat,
    rowWidth: CGFloat,
    allowsFullSwipe: Bool
) -> SwipeSettleTarget {
    if allowsFullSwipe, travel >= rowWidth * commitFraction {
        return .committed
    }
    if velocity >= flingVelocity {
        return .open
    }
    if velocity <= -flingVelocity {
        return .closed
    }
    return travel >= revealWidth / 2 ? .open : .closed
}
