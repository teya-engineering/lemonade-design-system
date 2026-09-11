import SwiftUI

// MARK: - Spinner Size

/// Sizes a ``LemonadeUi/Spinner(size:tint:)`` can take, matching the Compose component.
public enum LemonadeSpinnerSize {
    case xSmall
    case small
    case medium
    case large
    case xLarge
    case xxLarge
    case xxxLarge
    case xxxxLarge

    /// Returns the CGFloat value for this size
    public var value: CGFloat {
        switch self {
        case .xSmall: return LemonadeTheme.sizes.size300
        case .small: return LemonadeTheme.sizes.size400
        case .medium: return LemonadeTheme.sizes.size500
        case .large: return LemonadeTheme.sizes.size600
        case .xLarge: return LemonadeTheme.sizes.size800
        case .xxLarge: return LemonadeTheme.sizes.size1000
        case .xxxLarge: return LemonadeTheme.sizes.size1200
        case .xxxxLarge: return LemonadeTheme.sizes.size1400
        }
    }
}

// MARK: - Spinner Component

public extension LemonadeUi {
    /// Spinner component for indicating loading state.
    /// Wraps the native iOS ProgressView with a customizable size and tint color.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Spinner()
    /// LemonadeUi.Spinner(size: .large)
    /// LemonadeUi.Spinner(tint: LemonadeTheme.colors.content.contentBrand)
    /// ```
    ///
    /// - Parameters:
    ///   - size: The spinner size. Defaults to `.medium`
    ///   - tint: The spinner color. Defaults to `contentSecondary`
    /// - Returns: A styled spinner view
    @ViewBuilder
    static func Spinner(
        size: LemonadeSpinnerSize = .medium,
        tint: Color = LemonadeTheme.colors.content.contentSecondary
    ) -> some View {
        ProgressView()
            .controlSize(size.controlSize)
            .tint(tint)
            .scaleEffect(size.value / size.controlSize.spinnerDimension)
            .frame(width: size.value, height: size.value)
    }
}

// ProgressView only draws at a fixed size per control size and scaleEffect
// stretches its bitmap, so each size scales from the closest native one to keep
// upscaling, and the blur it causes, to a minimum.
private extension LemonadeSpinnerSize {
    var controlSize: ControlSize {
        value > ControlSize.regular.spinnerDimension ? .large : .regular
    }
}

// ProgressView's measured native sizes. iOS's large spinner is 37pt, which no
// size token matches.
private extension ControlSize {
    var spinnerDimension: CGFloat {
        #if os(macOS)
        return LemonadeTheme.sizes.size800
        #else
        return self == .large ? 37 : LemonadeTheme.sizes.size500
        #endif
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeSpinner_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            LemonadeUi.Spinner()

            LemonadeUi.Spinner(size: .large)

            LemonadeUi.Spinner(size: .xxLarge)

            LemonadeUi.Spinner(
                tint: LemonadeTheme.colors.content.contentBrand
            )

            LemonadeUi.Spinner(
                tint: LemonadeTheme.colors.content.contentCritical
            )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
