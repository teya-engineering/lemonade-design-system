import SwiftUI

/// Drives the looping positive sweep that marks a step in progress: a gradient grows along the
/// track, fades out, and restarts once per `cycleDuration`. `content` receives how far the
/// gradient reaches (0 to 1) and its opacity. Reduce Motion shows the gradient fully drawn instead.
struct LemonadeProgressSweep<Content: View>: View {
    let cycleDuration: TimeInterval
    @ViewBuilder let content: (_ progress: CGFloat, _ opacity: Double) -> Content

    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var startDate = Date()

    private var fillDuration: TimeInterval { cycleDuration * 0.8 }
    private var fadeStart: TimeInterval { cycleDuration * 0.6 }

    var body: some View {
        if reduceMotion {
            content(1, 1)
        } else {
            TimelineView(.animation) { context in
                let time = context.date.timeIntervalSince(startDate)
                    .truncatingRemainder(dividingBy: cycleDuration)
                let progress = progress(at: time)
                content(progress, progress > 0 ? opacity(at: time) : 0)
            }
        }
    }

    private func progress(at time: TimeInterval) -> CGFloat {
        guard time < fillDuration else { return 1 }
        return Self.easeInOut(time / fillDuration)
    }

    private func opacity(at time: TimeInterval) -> Double {
        guard time > fadeStart else { return 1 }
        return max(0, 1 - (time - fadeStart) / (fillDuration - fadeStart))
    }

    /// `cubic-bezier(0.42, 0, 0.58, 1)`, the curve SwiftUI's `.easeInOut` and Compose's `EaseInOut` use.
    private static func easeInOut(_ x: Double) -> CGFloat {
        var low = 0.0
        var high = 1.0
        var t = x
        for _ in 0..<20 {
            let inverse = 1 - t
            let curveX = 3 * inverse * inverse * t * 0.42 + 3 * inverse * t * t * 0.58 + t * t * t
            if curveX < x { low = t } else { high = t }
            t = (low + high) / 2
        }
        return CGFloat(3 * t * t - 2 * t * t * t)
    }
}
