import XCTest
@testable import Lemonade

/// Covers the hand-off between a visible toast and one queued behind it.
///
/// A queued toast may only take the slot once `ToastAnimationConfig.minimumVisible` has
/// elapsed. That window is longer than the entry animation, so a toast is never faded out
/// while it is still appearing.
@MainActor
final class LemonadeToastQueueTests: XCTestCase {

    /// A queued toast must not cut the visible one short before it has finished
    /// animating in.
    func testQueuedToastDoesNotReplaceTheVisibleOneDuringItsEntryAnimation() async throws {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success)
        manager.show(label: "Second", voice: .neutral)

        XCTAssertEqual(manager.currentToast?.label, "First")

        // Sample inside the entry animation.
        let sample = ToastAnimationConfig.duration * 0.6
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: sample))

        XCTAssertEqual(
            manager.currentToast?.label,
            "First",
            "The first toast was replaced while it was still animating in."
        )
    }

    /// The queued toast still gets its turn once the minimum visible window elapses.
    func testQueuedToastIsShownAfterTheMinimumVisibleWindow() async throws {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success)
        manager.show(label: "Second", voice: .neutral)

        // `minimumVisible` plus enough slack for the dismissal task to run.
        let settle = ToastAnimationConfig.minimumVisible + 0.3
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: settle))

        XCTAssertEqual(
            manager.currentToast?.label,
            "Second",
            "The queued toast never replaced the first one."
        )
    }

    /// A lone toast must not be routed through the queue path — it keeps its full duration.
    func testSingleToastIsNotRoutedThroughTheTransitionPath() async throws {
        let manager = LemonadeToastManager()

        manager.show(label: "Only", voice: .success)

        // Past `minimumVisible`, so a toast wrongly scheduled as a transition would already be
        // gone — but far short of the `.short` lifetime of 0.35 + 3s.
        let sample = ToastAnimationConfig.minimumVisible + 0.3
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: sample))

        XCTAssertEqual(manager.currentToast?.label, "Only")
    }
}
