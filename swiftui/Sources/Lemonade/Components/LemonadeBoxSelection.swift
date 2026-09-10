import SwiftUI

// MARK: - Box Selection Variant

/// Defines the visual variant for Box Selection.
public enum LemonadeBoxSelectionVariant {
    case filled
    case outlined
}

// MARK: - Box Selection Background

/// Surface behind a `.filled` box selection.
/// Ignored by `.outlined`, which has no fill.
public enum LemonadeBoxSelectionBackground {
    case `default`
    case elevated

    var color: Color {
        switch self {
        case .default: return LemonadeTheme.colors.background.bgDefault
        case .elevated: return LemonadeTheme.colors.background.bgElevated
        }
    }
}

// MARK: - Box Selection Component

public extension LemonadeUi {
    /// A selectable container used to present options and capture user choice, supporting single
    /// or multiple selection. It only renders the box — what goes inside it is entirely up to the
    /// caller.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.BoxSelection(
    ///     isSelected: isSelected,
    ///     onClick: { isSelected.toggle() }
    /// ) {
    ///     LemonadeUi.Text("Option")
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - variant: LemonadeBoxSelectionVariant to define visual style. Defaults to .filled
    ///   - background: LemonadeBoxSelectionBackground behind the content. Only applied by
    ///     `.filled`; a selected box always falls back to the default background, as per the
    ///     design. Defaults to .default
    ///   - isSelected: Whether the box is in a selected state. Changing it plays a selection
    ///     haptic. Defaults to false
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - contentPadding: LemonadeSpacing token applied between the box and its content.
    ///     Defaults to .spacing300
    ///   - radius: LemonadeRadius token applied to the box corners. Defaults to .radius500
    ///   - stretched: Whether the box should stretch to fill available width, laying the content
    ///     out from the top-leading corner. Defaults to false
    ///   - onClick: Callback called when the box is tapped. When nil the box is not tappable,
    ///     leaving the interaction to the content
    ///   - content: Content to display inside the box
    /// - Returns: A styled Box Selection view
    @ViewBuilder
    static func BoxSelection<Content: View>(
        variant: LemonadeBoxSelectionVariant = .filled,
        background: LemonadeBoxSelectionBackground = .default,
        isSelected: Bool = false,
        enabled: Bool = true,
        contentPadding: LemonadeSpacing = .spacing300,
        radius: LemonadeRadius = .radius500,
        stretched: Bool = false,
        onClick: (() -> Void)? = nil,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeBoxSelectionView(
            variant: variant,
            background: background,
            isSelected: isSelected,
            enabled: enabled,
            contentPadding: contentPadding,
            radius: radius,
            stretched: stretched,
            onClick: onClick,
            content: content
        )
    }
}

// MARK: - Box Selection Button Style

private struct LemonadeBoxSelectionButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .opacity(configuration.isPressed ? .opacity.opacityPressed : .opacity.opacity100)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

// MARK: - Box Selection Style

private struct BoxSelectionStyle {
    let backgroundColor: Color
    let borderColor: Color
    let borderWidth: CGFloat
}

private extension LemonadeBoxSelectionVariant {
    func resolvedStyle(
        background: LemonadeBoxSelectionBackground,
        isSelected: Bool
    ) -> BoxSelectionStyle {
        switch self {
        case .filled:
            return BoxSelectionStyle(
                // A selected box drops back to the default background so the selected border
                // reads against it, whichever background the caller asked for.
                backgroundColor: isSelected
                    ? LemonadeTheme.colors.background.bgDefault
                    : background.color,
                borderColor: isSelected ? LemonadeTheme.colors.border.borderSelected : .clear,
                borderWidth: isSelected ? LemonadeTheme.borderWidth.base.border50 : 0
            )
        case .outlined:
            return BoxSelectionStyle(
                backgroundColor: .clear,
                borderColor: isSelected
                    ? LemonadeTheme.colors.border.borderSelected
                    : LemonadeTheme.colors.border.borderNeutralMedium,
                borderWidth: isSelected
                    ? LemonadeTheme.borderWidth.base.border50
                    : LemonadeTheme.borderWidth.base.border40
            )
        }
    }
}

// MARK: - Internal Box Selection View

private struct LemonadeBoxSelectionView<Content: View>: View {
    let variant: LemonadeBoxSelectionVariant
    let background: LemonadeBoxSelectionBackground
    let isSelected: Bool
    let enabled: Bool
    let contentPadding: LemonadeSpacing
    let radius: LemonadeRadius
    let stretched: Bool
    let onClick: (() -> Void)?
    let content: () -> Content

    // Resolved once and shared by both branches of `body`; every `LemonadeTheme.colors.*`
    // read is a named asset-catalog lookup.
    private var styledContent: some View {
        let style = variant.resolvedStyle(background: background, isSelected: isSelected)
        let shape = radius.shape

        return content()
            .padding(contentPadding.value)
            .applyIf(stretched) {
                // .leading, not the default .center: a stretched box lays its content out from
                // the top-leading corner. Callers that want it centred say so in their content.
                $0.frame(maxWidth: .infinity, alignment: .leading)
            }
            .background(shape.fill(style.backgroundColor))
            .clipShape(shape)
            // strokeBorder, not stroke: the border sits inside the edge, not straddling it.
            .overlay(shape.strokeBorder(style.borderColor, lineWidth: style.borderWidth))
            .opacity(enabled ? .opacity.opacity100 : LemonadeTheme.opacity.state.opacityDisabled)
            .contentShape(shape)
            .animation(.easeInOut(duration: 0.2), value: isSelected)
            .selectionImpactFeedback(trigger: isSelected)
    }

    var body: some View {
        Group {
            if let onClick {
                Button(action: onClick) { styledContent }
                    .buttonStyle(LemonadeBoxSelectionButtonStyle())
                    .disabled(!enabled)
            } else {
                styledContent
            }
        }
        // The slot content is arbitrary, so it cannot be relied on to carry the selected state.
        // Publish it here or VoiceOver announces the box without saying whether it is the
        // chosen one.
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeBoxSelection_Previews: PreviewProvider {
    private static var variantsRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.BoxSelection(variant: .filled) {
                LemonadeUi.Text("Filled")
            }
            LemonadeUi.BoxSelection(variant: .outlined) {
                LemonadeUi.Text("Outlined")
            }
        }
    }

    private static var backgroundsRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.BoxSelection(background: .default) {
                LemonadeUi.Text("Default")
            }
            LemonadeUi.BoxSelection(background: .elevated) {
                LemonadeUi.Text("Elevated")
            }
        }
    }

    private static var selectedRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.BoxSelection(variant: .filled, isSelected: true, onClick: {}) {
                LemonadeUi.Text("Filled")
            }
            LemonadeUi.BoxSelection(variant: .outlined, isSelected: true, onClick: {}) {
                LemonadeUi.Text("Outlined")
            }
        }
    }

    private static var disabledRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.BoxSelection(variant: .filled, enabled: false, onClick: {}) {
                LemonadeUi.Text("Filled")
            }
            LemonadeUi.BoxSelection(variant: .outlined, enabled: false, onClick: {}) {
                LemonadeUi.Text("Outlined")
            }
        }
    }

    private static var paddingAndRadiusRow: some View {
        HStack(spacing: 16) {
            LemonadeUi.BoxSelection(
                variant: .outlined,
                contentPadding: .spacing600
            ) {
                LemonadeUi.Text("Spacing600")
            }
            LemonadeUi.BoxSelection(
                variant: .outlined,
                radius: .radius0
            ) {
                LemonadeUi.Text("Radius0")
            }
        }
    }

    static var previews: some View {
        VStack(spacing: 24) {
            variantsRow
            backgroundsRow
            selectedRow
            disabledRow
            paddingAndRadiusRow
        }
        .padding()
        .background(LemonadeTheme.colors.background.bgSubtle)
        .previewLayout(.sizeThatFits)
    }
}
#endif
