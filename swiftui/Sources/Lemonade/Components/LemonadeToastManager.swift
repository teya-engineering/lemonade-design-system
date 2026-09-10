import SwiftUI
import Combine

// MARK: - Toast Animation Constants

/// Animation configuration for toast transitions.
enum ToastAnimationConfig: Sendable {
    /// Duration for entry/exit animations
    static let duration: TimeInterval = 0.35

    /// Spring animation for natural feel
    static var spring: Animation {
        .spring(response: duration, dampingFraction: 0.8)
    }

    /// Smooth animation for fading effects
    static var smooth: Animation {
        .easeInOut(duration: duration)
    }

    /// Interactive spring for drag gestures
    static var interactiveSpring: Animation {
        .spring(response: 0.3, dampingFraction: 0.7)
    }

    /// Fallback slide offset if toast height not yet measured
    static let fallbackSlideOffset: CGFloat = 200

    /// Fade exit scale
    static let fadeScale: CGFloat = 0.88

    /// Fade exit blur radius
    static let fadeBlur: CGFloat = 10

    /// Drag threshold to trigger dismiss
    static let dragDismissThreshold: CGFloat = 25

    /// Shortest time a toast stays on screen before a queued toast is allowed to replace it.
    ///
    /// Must exceed `duration`, the entry animation: otherwise two `show(_:)` calls in quick
    /// succession start fading the first toast before it has finished appearing, which reads
    /// as a flicker rather than a transition.
    static let minimumVisible: TimeInterval = duration + 0.35

    /// Convert seconds to nanoseconds for Task.sleep
    static func nanoseconds(from seconds: TimeInterval) -> UInt64 {
        UInt64(seconds * 1_000_000_000)
    }
}

// MARK: - Toast Duration

/// Predefined duration values for toast notifications.
public enum LemonadeToastDuration: Sendable, Equatable {
    /// Short duration: 3 seconds
    case short
    /// Medium duration: 6 seconds
    case medium
    /// Long duration: 9 seconds
    case long
    /// Custom duration
    case custom(TimeInterval)

    public var timeInterval: TimeInterval {
        switch self {
        case .short: return 3
        case .medium: return 6
        case .long: return 9
        case .custom(let duration): return duration
        }
    }
}

// MARK: - Toast Policy

/// How a `show(_:)` call behaves when a toast is already on screen.
public enum LemonadeToastPolicy: Sendable, Equatable {
    /// Wait for the visible toast, then take its place. Every message is seen, in call order.
    ///
    /// Suits toasts that each report a distinct outcome the user should not miss. It does mean a
    /// burst of calls plays back after the burst itself has ended, since each toast is held for
    /// `ToastAnimationConfig.minimumVisible` before the next may replace it.
    case queue

    /// Supersede the visible toast, and drop anything queued behind it.
    ///
    /// Suits a repeated action reporting its running state — a till adding items to a basket —
    /// where only the newest message is worth reading and a backlog would outlive the taps that
    /// produced it.
    ///
    /// The queue is cleared rather than kept, so nothing older can surface after the message that
    /// replaced it.
    case replace
}

// MARK: - Toast Item

/// Represents a toast notification to be displayed.
public struct LemonadeToastItem: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let label: String
    public let voice: LemonadeToastVoice
    public let icon: LemonadeIcon?
    public let duration: LemonadeToastDuration
    public let isDismissible: Bool
    public let actionLabel: String?
    public let onAction: (@MainActor @Sendable () -> Void)?
    public let paddingValues: EdgeInsets?

    public init(
        id: UUID = UUID(),
        label: String,
        voice: LemonadeToastVoice = .neutral,
        icon: LemonadeIcon? = nil,
        duration: LemonadeToastDuration = .short,
        isDismissible: Bool = true,
        actionLabel: String? = nil,
        onAction: (@MainActor @Sendable () -> Void)? = nil,
        paddingValues: EdgeInsets? = nil
    ) {
        self.id = id
        self.label = label
        self.voice = voice
        self.icon = icon
        self.duration = duration
        self.isDismissible = isDismissible
        self.actionLabel = actionLabel
        self.onAction = onAction
        self.paddingValues = paddingValues
    }

    public static func == (lhs: LemonadeToastItem, rhs: LemonadeToastItem) -> Bool {
        lhs.id == rhs.id
    }

    /// This toast's content under an existing toast's identity.
    ///
    /// The container keys its transitions on `id`, so reusing one makes a replacement land as an
    /// update to the toast already on screen — the pill keeps its place and its text changes —
    /// rather than a fade out and a fresh entry animation.
    func adoptingIdentity(of other: LemonadeToastItem) -> LemonadeToastItem {
        LemonadeToastItem(
            id: other.id,
            label: label,
            voice: voice,
            icon: icon,
            duration: duration,
            isDismissible: isDismissible,
            actionLabel: actionLabel,
            onAction: onAction,
            paddingValues: paddingValues
        )
    }
}

// MARK: - Toast Manager

/// A manager for displaying toast notifications.
///
/// Use the environment object to show toasts from any view in your hierarchy.
///
/// ## Setup
/// Add the toast container modifier to your root view:
/// ```swift
/// ContentView()
///     .lemonadeToastContainer()
/// ```
///
/// ## Usage
/// ```swift
/// struct MyView: View {
///     @EnvironmentObject private var toastManager: LemonadeToastManager
///
///     var body: some View {
///         Button("Show Toast") {
///             toastManager.show(
///                 label: "Changes saved",
///                 voice: .success
///             )
///         }
///     }
/// }
/// ```
@MainActor
public final class LemonadeToastManager: ObservableObject {
    /// The currently displayed toast, if any.
    @Published public private(set) var currentToast: LemonadeToastItem?

    private var pendingToasts: [LemonadeToastItem] = []

    private var dismissTask: Task<Void, Never>?

    /// Monotonic timestamp of when the current toast was put on screen, used to hold it there
    /// for at least `ToastAnimationConfig.minimumVisible` before a queued toast replaces it.
    ///
    /// Only read via `remainingMinimumVisible`, which is only reachable while a toast is showing,
    /// so it needs no "not yet shown" state.
    private var currentToastShownAt: DispatchTime = .now()

    /// How much longer the visible toast must stay up before a queued one may replace it.
    private var remainingMinimumVisible: TimeInterval {
        let elapsedNanoseconds = DispatchTime.now().uptimeNanoseconds - currentToastShownAt.uptimeNanoseconds
        let elapsed = TimeInterval(elapsedNanoseconds) / 1_000_000_000
        return max(0, ToastAnimationConfig.minimumVisible - elapsed)
    }

    public init() {}

    /// Shows a toast notification.
    ///
    /// - Parameters:
    ///   - label: The message to display.
    ///   - voice: The toast variant (success, error, neutral, loading).
    ///   - icon: Custom icon for neutral toasts only.
    ///   - duration: How long the toast should be visible. Ignored when `voice` is `.loading`.
    ///   - dismissible: Whether the toast can be dismissed by swiping. Ignored (forced off) when `voice` is `.loading`.
    ///   - actionLabel: Optional label for the action button shown at the trailing end of the toast.
    ///   - onAction: Optional callback invoked when the action button is tapped. The button is only shown when both `actionLabel` and `onAction` are non-nil.
    ///   - paddingValues: Extra space to clear around the toast, e.g. to raise it clear of a screen's
    ///     persistent bottom action button, or to inset it from a side element. Bottom/leading/trailing
    ///     are honored; a zero edge falls back to the standard margin for that edge (there's no way to
    ///     tell "unset" from "explicitly zero" on a plain `EdgeInsets`). The top inset is never honored —
    ///     the toast is always bottom-anchored with intrinsic height, so it has no visible effect.
    ///     Defaults to `nil` (standard margins on every edge).
    ///   - policy: What to do when a toast is already on screen — wait behind it (`.queue`, the
    ///     default, preserving existing behaviour) or supersede it and drop anything queued
    ///     (`.replace`).
    ///
    /// Use `.loading` to communicate an ongoing action (e.g. "Downloading your document…"). A loading
    /// toast shows a spinner and persists until you call ``dismiss()`` or replace it with another `show`.
    public func show(
        label: String,
        voice: LemonadeToastVoice = .neutral,
        icon: LemonadeIcon? = nil,
        duration: LemonadeToastDuration = .short,
        dismissible: Bool = true,
        actionLabel: String? = nil,
        onAction: (@MainActor @Sendable () -> Void)? = nil,
        paddingValues: EdgeInsets? = nil,
        policy: LemonadeToastPolicy = .queue
    ) {
        let toast = LemonadeToastItem(
            label: label,
            voice: voice,
            icon: icon,
            duration: duration,
            // A loading toast describes an ongoing action: it cannot be swiped away.
            isDismissible: voice == .loading ? false : dismissible,
            actionLabel: actionLabel,
            onAction: onAction,
            paddingValues: paddingValues
        )

        switch policy {
        case .replace:
            // Anything still queued is older than this toast, so it is no longer worth showing —
            // left in place it would surface after the message meant to supersede it.
            //
            // Deliberately not `dismiss()`: that promotes a queued toast into the slot we are
            // about to overwrite, consuming it unseen.
            pendingToasts.removeAll()
            // Reuse the visible toast's identity so its content is updated where it stands. Going
            // through `displayToast` either way restarts the dismissal timer, so the replacement
            // gets its own full duration rather than inheriting what was left of the old one.
            displayToast(currentToast.map(toast.adoptingIdentity(of:)) ?? toast)
        case .queue where currentToast != nil:
            pendingToasts.append(toast)
            scheduleTransition()
        case .queue:
            displayToast(toast)
        }
    }

    /// Dismisses the current toast.
    public func dismiss() {
        dismissTask?.cancel()
        dismissTask = nil

        let hadPending = !pendingToasts.isEmpty

        if hadPending {
            showNextToastIfAvailable()
        } else {
            currentToast = nil
        }
    }

    private func displayToast(_ toast: LemonadeToastItem) {
        dismissTask?.cancel()
        currentToast = toast
        currentToastShownAt = .now()

        // A loading toast persists until explicitly dismissed or replaced — skip the auto-dismiss timer.
        guard toast.voice != .loading else { return }

        let totalDelay = ToastAnimationConfig.duration + toast.duration.timeInterval
        scheduleAutoDismiss(after: totalDelay)
    }

    /// Schedules auto-dismissal of the current toast.
    /// - Parameter delay: Total delay including entry animation time.
    private func scheduleAutoDismiss(after delay: TimeInterval) {
        dismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ToastAnimationConfig.nanoseconds(from: delay))
            guard !Task.isCancelled else { return }
            dismiss()
        }
    }

    /// Schedules transition to pending toast.
    ///
    /// Holds the visible toast for the remainder of `ToastAnimationConfig.minimumVisible` rather
    /// than dismissing it on a flat delay — a queued toast must not cut short the one on screen
    /// before it has finished animating in and been readable.
    private func scheduleTransition() {
        dismissTask?.cancel()
        scheduleAutoDismiss(after: remainingMinimumVisible)
    }

    private func showNextToastIfAvailable() {
        guard !pendingToasts.isEmpty else {
            return
        }
        let nextToast = pendingToasts.removeFirst()
        displayToast(nextToast)
    }
}
