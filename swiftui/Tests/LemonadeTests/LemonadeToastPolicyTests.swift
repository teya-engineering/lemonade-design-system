import XCTest
@testable import Lemonade

/// Covers `LemonadeToastPolicy` — what a `show(_:)` call does when a toast is already on screen.
///
/// `.queue` is the default and is covered by `LemonadeToastQueueTests`. These guard `.replace`,
/// and that omitting the parameter still queues.
@MainActor
final class LemonadeToastPolicyTests: XCTestCase {

    /// Omitting the parameter must queue: the visible toast keeps the slot.
    func testQueueIsTheDefaultPolicy() {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success)
        manager.show(label: "Second", voice: .neutral)

        XCTAssertEqual(
            manager.currentToast?.label,
            "First",
            "The default policy replaced the visible toast — existing callers changed behaviour."
        )
    }

    /// The defining behaviour of `.replace`: no waiting for the visible toast.
    func testReplaceTakesOverImmediately() {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success)
        manager.show(label: "Second", voice: .neutral, policy: .replace)

        XCTAssertEqual(manager.currentToast?.label, "Second")
    }

    /// A replacement adopts the visible toast's identity, which is what makes the container update
    /// the pill where it stands rather than fade one out and animate another in.
    func testReplaceKeepsTheVisibleToastsIdentity() {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success)
        let original = try? XCTUnwrap(manager.currentToast?.id)

        manager.show(label: "Second", voice: .neutral, policy: .replace)

        XCTAssertEqual(manager.currentToast?.label, "Second")
        XCTAssertEqual(manager.currentToast?.id, original)
    }

    /// With nothing on screen there is no identity to adopt, so the toast arrives as its own and
    /// gets a normal entry animation.
    func testReplaceOnAnEmptyScreenKeepsItsOwnIdentity() {
        let manager = LemonadeToastManager()

        manager.show(label: "Only", voice: .success, policy: .replace)

        XCTAssertEqual(manager.currentToast?.label, "Only")
    }

    /// A replacement supersedes the backlog too, not just what is on screen — otherwise a toast
    /// queued earlier would surface after the message meant to replace it.
    ///
    /// Sampled after the replacement's own dismissal rather than during it: that is the moment a
    /// surviving queue entry would be promoted, so anything short of it proves nothing.
    func testReplaceDropsAlreadyQueuedToasts() async throws {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success, duration: .custom(0.2))
        manager.show(label: "Queued", voice: .neutral, duration: .custom(0.2))
        manager.show(label: "Latest", voice: .error, duration: .custom(0.2), policy: .replace)

        XCTAssertEqual(manager.currentToast?.label, "Latest")

        let settle = ToastAnimationConfig.duration + 0.2 + 0.3
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: settle))

        XCTAssertNil(
            manager.currentToast,
            "A toast queued before the replacement was promoted once it had gone."
        )
    }

    /// The case this exists for: a burst collapses to its last message instead of playing back
    /// after the taps have stopped.
    func testAReplaceBurstLeavesOnlyTheNewest() async throws {
        let manager = LemonadeToastManager()

        for item in 1...5 {
            manager.show(label: "Added item \(item)", voice: .success, policy: .replace)
        }

        XCTAssertEqual(manager.currentToast?.label, "Added item 5")

        let settle = ToastAnimationConfig.minimumVisible + 0.3
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: settle))

        XCTAssertEqual(manager.currentToast?.label, "Added item 5")
    }

    /// A replacement gets its own lifetime rather than inheriting the remainder of the one it
    /// replaced — `displayToast` re-arms the dismissal timer.
    func testAReplacementGetsItsOwnDuration() async throws {
        let manager = LemonadeToastManager()

        manager.show(label: "First", voice: .success, duration: .custom(0.3))
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: 0.4))
        manager.show(label: "Second", voice: .neutral, duration: .custom(0.3), policy: .replace)

        // The first toast's own dismissal would land about here; the second must outlive it.
        try await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: 0.3))

        XCTAssertEqual(manager.currentToast?.label, "Second")
    }

    /// A loading toast has no auto-dismiss timer of its own, but `.replace` still supersedes it.
    func testReplaceSupersedesALoadingToast() {
        let manager = LemonadeToastManager()

        manager.show(label: "Downloading…", voice: .loading)
        manager.show(label: "Download complete", voice: .success, policy: .replace)

        XCTAssertEqual(manager.currentToast?.label, "Download complete")
    }
}
