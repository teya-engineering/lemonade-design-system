import SwiftUI

// MARK: - Button Size

/// Button sizes following the Lemonade Design System.
public enum LemonadeButtonSize {
    case xSmall
    case small
    case medium
    case large
}

// MARK: - Button Component

public extension LemonadeUi {
    /// Lemonade labeled button component. Used for simple click actions with a text and optional icons.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Button(
    ///     label: "Click me!",
    ///     onClick: { print("Button clicked!") }
    /// )
    ///
    /// // Full width button using standard SwiftUI modifier
    /// LemonadeUi.Button(
    ///     label: "Full Width",
    ///     onClick: { }
    /// )
    /// .frame(maxWidth: .infinity)
    /// ```
    ///
    /// - Parameters:
    ///   - label: String to be displayed as the Button's label
    ///   - onClick: Callback to be invoked when the Button is clicked
    ///   - leadingIcon: LemonadeIcon shown before the label
    ///   - trailingIcon: LemonadeIcon shown after the label
    ///   - variant: LemonadeButtonVariant to style the Button accordingly
    ///   - type: LemonadeButtonType for the fill treatment (solid, subtle, ghost)
    ///   - size: LemonadeButtonSize to size the Button accordingly
    ///   - enabled: Boolean flag to enable or disable the Button
    ///   - loading: Boolean flag to show a loading spinner and disable interaction. Like `enabled`, loading dims the button to the disabled opacity.
    /// - Returns: A styled Button view
    @ViewBuilder
    static func Button(
        label: String,
        onClick: @escaping () -> Void,
        leadingIcon: LemonadeIcon? = nil,
        trailingIcon: LemonadeIcon? = nil,
        variant: LemonadeButtonVariant = .primary,
        type: LemonadeButtonType = .solid,
        size: LemonadeButtonSize = .large,
        enabled: Bool = true,
        loading: Bool = false
    ) -> some View {
        LemonadeButtonView(
            label: label,
            onClick: onClick,
            leadingIcon: leadingIcon,
            trailingIcon: trailingIcon,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading
        )
    }
}

// MARK: - Slot-based Button Component

public extension LemonadeUi {
    /// Lemonade labeled button component with custom leading and trailing slots.
    /// Used for advanced button layouts such as "Dual Action" buttons.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Button(
    ///     label: "Dual Action",
    ///     onClick: { },
    ///     trailingSlot: { colors in
    ///         LemonadeUi.VerticalDivider()
    ///         Image(systemName: "ellipsis")
    ///             .foregroundColor(colors.contentColor)
    ///     },
    ///     expandContents: true,
    ///     size: .medium
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: String to be displayed as the Button's label
    ///   - onClick: Callback to be invoked when the Button is clicked
    ///   - leadingSlot: Custom view builder for the leading area, receives LemonadeButtonColors
    ///   - trailingSlot: Custom view builder for the trailing area, receives LemonadeButtonColors
    ///   - expandContents: When true, the content area expands to fill available space between slots
    ///   - variant: LemonadeButtonVariant to style the Button accordingly
    ///   - type: LemonadeButtonType for the fill treatment (solid, subtle, ghost)
    ///   - size: LemonadeButtonSize to size the Button accordingly
    ///   - enabled: Boolean flag to enable or disable the Button
    ///   - loading: Boolean flag to show a loading spinner and disable interaction
    /// - Returns: A styled Button view
    @ViewBuilder
    static func Button<LeadingSlot: View, TrailingSlot: View>(
        label: String,
        onClick: @escaping () -> Void,
        @ViewBuilder leadingSlot: @escaping (LemonadeButtonColors) -> LeadingSlot,
        @ViewBuilder trailingSlot: @escaping (LemonadeButtonColors) -> TrailingSlot,
        expandContents: Bool = false,
        variant: LemonadeButtonVariant = .primary,
        type: LemonadeButtonType = .solid,
        size: LemonadeButtonSize = .large,
        enabled: Bool = true,
        loading: Bool = false
    ) -> some View {
        LemonadeSlotButtonView(
            label: label,
            onClick: onClick,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading,
            expandContents: expandContents,
            leadingSlot: leadingSlot,
            trailingSlot: trailingSlot
        )
    }

    /// Lemonade labeled button component with a custom trailing slot.
    @ViewBuilder
    static func Button<TrailingSlot: View>(
        label: String,
        onClick: @escaping () -> Void,
        @ViewBuilder trailingSlot: @escaping (LemonadeButtonColors) -> TrailingSlot,
        expandContents: Bool = false,
        variant: LemonadeButtonVariant = .primary,
        type: LemonadeButtonType = .solid,
        size: LemonadeButtonSize = .large,
        enabled: Bool = true,
        loading: Bool = false
    ) -> some View {
        LemonadeSlotButtonView(
            label: label,
            onClick: onClick,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading,
            expandContents: expandContents,
            leadingSlot: nil as ((LemonadeButtonColors) -> EmptyView)?,
            trailingSlot: trailingSlot
        )
    }

    /// Lemonade labeled button component with a custom leading slot.
    @ViewBuilder
    static func Button<LeadingSlot: View>(
        label: String,
        onClick: @escaping () -> Void,
        @ViewBuilder leadingSlot: @escaping (LemonadeButtonColors) -> LeadingSlot,
        expandContents: Bool = false,
        variant: LemonadeButtonVariant = .primary,
        type: LemonadeButtonType = .solid,
        size: LemonadeButtonSize = .large,
        enabled: Bool = true,
        loading: Bool = false
    ) -> some View {
        LemonadeSlotButtonView(
            label: label,
            onClick: onClick,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading,
            expandContents: expandContents,
            leadingSlot: leadingSlot,
            trailingSlot: nil as ((LemonadeButtonColors) -> EmptyView)?
        )
    }
}

// MARK: - Button Colors

public struct LemonadeButtonColors {
    public let contentColor: Color
    public let backgroundColor: Color
    public let pressedBackgroundColor: Color

    internal init(
        contentColor: Color,
        backgroundColor: Color,
        pressedBackgroundColor: Color
    ) {
        self.contentColor = contentColor
        self.backgroundColor = backgroundColor
        self.pressedBackgroundColor = pressedBackgroundColor
    }
}

// MARK: - Internal Button Content Data

private struct LemonadeButtonContentData {
    let verticalPadding: CGFloat
    let horizontalPadding: CGFloat
    let requiredHeight: CGFloat
    let minWidth: CGFloat
    let cornerRadius: CGFloat
    let textStyle: LemonadeTextStyle
}

// MARK: - Button Size Extension

private extension LemonadeButtonSize {
    var contentData: LemonadeButtonContentData {
        switch self {
        case .xSmall:
            return LemonadeButtonContentData(
                verticalPadding: LemonadeTheme.spaces.spacing100,
                horizontalPadding: LemonadeTheme.spaces.spacing200,
                requiredHeight: LemonadeTheme.sizes.size800,
                minWidth: LemonadeTheme.sizes.size1600,
                cornerRadius: LemonadeTheme.radius.radius250,
                textStyle: LemonadeTypography.shared.bodySmallSemiBold
            )
        case .small:
            return LemonadeButtonContentData(
                verticalPadding: LemonadeTheme.spaces.spacing200,
                horizontalPadding: LemonadeTheme.spaces.spacing300,
                requiredHeight: LemonadeTheme.sizes.size1000,
                minWidth: LemonadeTheme.sizes.size1600,
                cornerRadius: LemonadeTheme.radius.radius300,
                textStyle: LemonadeTypography.shared.bodySmallSemiBold
            )
        case .medium:
            return LemonadeButtonContentData(
                verticalPadding: LemonadeTheme.spaces.spacing300,
                horizontalPadding: LemonadeTheme.spaces.spacing400,
                requiredHeight: LemonadeTheme.sizes.size1200,
                minWidth: LemonadeTheme.sizes.size1600,
                cornerRadius: LemonadeTheme.radius.radius350,
                textStyle: LemonadeTypography.shared.bodyMediumSemiBold
            )
        case .large:
            return LemonadeButtonContentData(
                verticalPadding: LemonadeTheme.spaces.spacing300,
                horizontalPadding: LemonadeTheme.spaces.spacing400,
                requiredHeight: LemonadeTheme.sizes.size1400,
                minWidth: LemonadeTheme.sizes.size1600,
                cornerRadius: LemonadeTheme.radius.radius400,
                textStyle: LemonadeTypography.shared.bodyMediumSemiBold
            )
        }
    }
}

// MARK: - Button Color Resolution (Variant x Type)

private func resolveButtonColors(
    variant: LemonadeButtonVariant,
    type: LemonadeButtonType
) -> LemonadeButtonColors {
    switch (variant, type) {
    // MARK: Primary
    case (.primary, .solid):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentOnBrandHigh,
            backgroundColor: LemonadeTheme.colors.background.bgBrand,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgBrandInteractive
        )
    case (.primary, .subtle):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentBrandHigh,
            backgroundColor: LemonadeTheme.colors.background.bgBrandSubtle,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgSubtlePressed
        )
    case (.primary, .ghost):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentBrandHigh,
            backgroundColor: Color.clear,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgSubtlePressed
        )

    // MARK: Secondary
    case (.secondary, .solid):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimaryInverse,
            backgroundColor: LemonadeTheme.colors.background.bgSubtleInverse,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgNeutralPressed
        )
    case (.secondary, .subtle):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimary,
            backgroundColor: LemonadeTheme.colors.background.bgNeutralSubtle,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgNeutralSubtlePressed
        )
    case (.secondary, .ghost):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimary,
            backgroundColor: Color.clear,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgNeutralSubtlePressed
        )

    // MARK: Neutral
    case (.neutral, .solid):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimary,
            backgroundColor: LemonadeTheme.colors.background.bgElevated,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgElevatedPressed
        )
    case (.neutral, .subtle):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimary,
            backgroundColor: LemonadeTheme.colors.background.bgNeutralSubtle,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgNeutralSubtlePressed
        )
    case (.neutral, .ghost):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentPrimary,
            backgroundColor: Color.clear,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgNeutralSubtlePressed
        )

    // MARK: Critical
    case (.critical, .solid):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentAlwaysLight,
            backgroundColor: LemonadeTheme.colors.background.bgCritical,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgCriticalInteractive
        )
    case (.critical, .subtle):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentCritical,
            backgroundColor: LemonadeTheme.colors.background.bgCriticalSubtle,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgCriticalSubtleInteractive
        )
    case (.critical, .ghost):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentCritical,
            backgroundColor: Color.clear,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgCriticalSubtlePressed
        )

    // MARK: On Brand / On Color
    // Designed as a single Subtle treatment for placing a button on top of a brand- or
    // color-filled surface. They don't vary by type, so every type resolves to the same colors.
    case (.onBrand, _):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentOnBrandHigh,
            backgroundColor: LemonadeTheme.colors.background.bgBrandElevated,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgBrandElevatedInteractive
        )
    case (.onColor, _):
        return LemonadeButtonColors(
            contentColor: LemonadeTheme.colors.content.contentAlwaysLight,
            backgroundColor: LemonadeTheme.colors.background.bgAlwaysLightMedium,
            pressedBackgroundColor: LemonadeTheme.colors.interaction.bgAlwaysLightMediumInteractive
        )
    }
}

// MARK: - Internal Core Button View

private struct LemonadeCoreButtonView<LeadingSlot: View, TrailingSlot: View>: View {
    let label: String
    let onClick: () -> Void
    let variant: LemonadeButtonVariant
    let type: LemonadeButtonType
    let size: LemonadeButtonSize
    let enabled: Bool
    let loading: Bool
    let expandContents: Bool
    let contentSlot: (LemonadeButtonColors) -> AnyView
    let leadingSlot: ((LemonadeButtonColors) -> LeadingSlot)?
    let trailingSlot: ((LemonadeButtonColors) -> TrailingSlot)?

    @Environment(\.lemonadeButtonFullShape) private var isFullShape
    @State private var isPressed = false
    @LemonadeScale private var scale: LemonadeScaleFactors

    private var cornerRadius: CGFloat {
        isFullShape ? LemonadeTheme.radius.radiusFull : size.contentData.cornerRadius * scale.container
    }

    /// Whether the reader asked for text larger than the size the tokens were drawn for.
    private var growsWithText: Bool { scale.content > 1 }

    var body: some View {
        let colors = resolveButtonColors(variant: variant, type: type)
        let buttonShape = RoundedRectangle(cornerRadius: cornerRadius)

        // When disabled or loading, the whole button — fill and content together — dims to 50% via
        // a single `.opacity` modifier on the SwiftUI.Button, matching the Figma disabled treatment
        // (group opacity, letting the underlying surface show through).
        let pressedOpacity = isPressed ? LemonadeTheme.opacity.state.opacityPressed : LemonadeTheme.opacity.base.opacity100
        let dimmed = !enabled || loading
        let dimmedOpacity = dimmed ? LemonadeTheme.opacity.state.opacityDisabled : 1.0
        // Secondary Solid's opaque inverse fill dims to `opacity40` when dimmed, not
        // `opacityDisabled`. The `.opacity(… dimmedOpacity)` below already dims the whole button
        // by `opacityDisabled`, so pre-scale just the fill by the ratio so they multiply out to
        // `opacity40`.
        let disabledFillScale = (dimmed && variant == .secondary && type == .solid)
            ? LemonadeTheme.opacity.base.opacity40 / LemonadeTheme.opacity.state.opacityDisabled
            : 1.0
        SwiftUI.Button(action: onClick) {
            HStack(spacing: 0) {
                if !loading, let leadingSlot = leadingSlot {
                    leadingSlot(colors)
                }

                HStack(spacing: 0) {
                    Spacer(minLength: 0)
                    ZStack {
                        // Hide the content with opacity while loading instead of removing it
                        // from the hierarchy. A removed view fades out at its old absolute
                        // position, so if the button's frame animates at the same time the
                        // content detaches and fades mid-screen while the spinner rides the
                        // button to its new position. Kept in the layout, it moves with the
                        // button.
                        contentSlot(colors)
                            .opacity(loading ? 0 : 1)

                        if loading {
                            LemonadeUi.Spinner(tint: colors.contentColor)
                        }
                    }
                    Spacer(minLength: 0)
                }
                .padding(.vertical, size.contentData.verticalPadding * scale.container)
                .padding(.horizontal, size.contentData.horizontalPadding * scale.container)
                .if(expandContents) { view in
                    view.frame(maxWidth: .infinity)
                }

                if !loading, let trailingSlot = trailingSlot {
                    trailingSlot(colors)
                }
            }
            // The height is fixed at the default text size and a floor above it.
            //
            // A fixed height is also what holds the label to a single line: given no room to
            // wrap, the label truncates instead. That is the treatment every button in the
            // library ships with today, so keep it while the reader is at the size the tokens
            // were drawn for. Once the reader asks for larger text, the same fixed height leaves
            // the label to overflow the fill, which is drawn at the height of the frame, so let
            // the button grow with it.
            .frame(
                minHeight: size.contentData.requiredHeight * scale.container,
                maxHeight: growsWithText ? nil : size.contentData.requiredHeight * scale.container
            )
            .frame(minWidth: size.contentData.minWidth * scale.container)
            .background(buttonShape.fill(colors.backgroundColor.opacity(disabledFillScale)))
            // Keep the rounded hit target the removed .clipShape used to provide, without
            // reintroducing its offscreen pass.
            .contentShape(buttonShape)
        }
        .buttonStyle(LemonadePressTrackingButtonStyle(isPressed: $isPressed))
        .opacity(pressedOpacity * dimmedOpacity)
        .animation(.easeInOut(duration: 0.1), value: isPressed)
        .disabled(!enabled || loading)
        .fixedSize(horizontal: false, vertical: true)
    }
}

// MARK: - Conditional Modifier

private extension View {
    @ViewBuilder
    func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}

// MARK: - Internal Icon Button View

private struct LemonadeButtonView: View {
    let label: String
    let onClick: () -> Void
    let leadingIcon: LemonadeIcon?
    let trailingIcon: LemonadeIcon?
    let variant: LemonadeButtonVariant
    let type: LemonadeButtonType
    let size: LemonadeButtonSize
    let enabled: Bool
    let loading: Bool

    var body: some View {
        LemonadeCoreButtonView(
            label: label,
            onClick: onClick,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading,
            expandContents: false,
            contentSlot: { colors in
                AnyView(
                    LemonadeButtonLabelContent(
                        label: label,
                        leadingIcon: leadingIcon,
                        trailingIcon: trailingIcon,
                        textStyle: size.contentData.textStyle,
                        colors: colors
                    )
                )
            },
            leadingSlot: nil as ((LemonadeButtonColors) -> EmptyView)?,
            trailingSlot: nil as ((LemonadeButtonColors) -> EmptyView)?
        )
    }
}

// MARK: - Internal Button Label Content

/// The icon-and-label row of a labeled button.
private struct LemonadeButtonLabelContent: View {
    let label: String
    let leadingIcon: LemonadeIcon?
    let trailingIcon: LemonadeIcon?
    let textStyle: LemonadeTextStyle
    let colors: LemonadeButtonColors

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize
    @LemonadeScale private var scale: LemonadeScaleFactors

    var body: some View {
        // At the accessibility text sizes an icon beside the label leaves the label too little
        // width to render: the row asks for more than the button has, and the label is what gives
        // way. Stacking the icons over the label hands the whole width back to the text, which is
        // the treatment the Human Interface Guidelines ask for at these sizes.
        Group {
            if dynamicTypeSize.isAccessibilitySize {
                VStack(spacing: LemonadeTheme.spaces.spacing200 * scale.container) {
                    icon(leadingIcon)
                    text
                    icon(trailingIcon)
                }
            } else {
                HStack(spacing: 0) {
                    icon(leadingIcon)
                    text.padding(.horizontal, LemonadeTheme.spaces.spacing200 * scale.container)
                    icon(trailingIcon)
                }
            }
        }
        .environment(\.lemonadeIconScaling, true)
    }

    private var text: some View {
        LemonadeUi.Text(
            label,
            textStyle: textStyle,
            color: colors.contentColor
        )
        .multilineTextAlignment(.center)
    }

    @ViewBuilder
    private func icon(_ icon: LemonadeIcon?) -> some View {
        if let icon = icon {
            LemonadeUi.Icon(
                icon: icon,
                contentDescription: nil,
                size: .medium,
                tint: colors.contentColor
            )
        }
    }
}

// MARK: - Internal Slot Button View

private struct LemonadeSlotButtonView<LeadingSlot: View, TrailingSlot: View>: View {
    let label: String
    let onClick: () -> Void
    let variant: LemonadeButtonVariant
    let type: LemonadeButtonType
    let size: LemonadeButtonSize
    let enabled: Bool
    let loading: Bool
    let expandContents: Bool
    let leadingSlot: ((LemonadeButtonColors) -> LeadingSlot)?
    let trailingSlot: ((LemonadeButtonColors) -> TrailingSlot)?

    @LemonadeScale private var scale: LemonadeScaleFactors

    var body: some View {
        LemonadeCoreButtonView(
            label: label,
            onClick: onClick,
            variant: variant,
            type: type,
            size: size,
            enabled: enabled,
            loading: loading,
            expandContents: expandContents,
            contentSlot: { colors in
                AnyView(
                    LemonadeUi.Text(
                        label,
                        textStyle: size.contentData.textStyle,
                        color: colors.contentColor
                    )
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, LemonadeTheme.spaces.spacing200 * scale.container)
                )
            },
            leadingSlot: leadingSlot,
            trailingSlot: trailingSlot
        )
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeButton_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 16) {
            // Primary variants
            LemonadeUi.Button(
                label: "Primary",
                onClick: {}
            )

            LemonadeUi.Button(
                label: "Secondary",
                onClick: {},
                variant: .secondary
            )

            LemonadeUi.Button(
                label: "Neutral",
                onClick: {},
                variant: .neutral
            )

            LemonadeUi.Button(
                label: "Ghost Disabled",
                onClick: {},
                variant: .neutral,
                type: .ghost,
                enabled: false
            )

            LemonadeUi.Button(
                label: "Critical",
                onClick: {},
                variant: .critical
            )

            // With icons
            LemonadeUi.Button(
                label: "With Icons",
                onClick: {},
                leadingIcon: .airplane,
                trailingIcon: .chevronRight
            )

            // Sizes
            HStack(spacing: 8) {
                LemonadeUi.Button(
                    label: "XSmall",
                    onClick: {},
                    size: .xSmall
                )

                LemonadeUi.Button(
                    label: "Small",
                    onClick: {},
                    size: .small
                )

                LemonadeUi.Button(
                    label: "Medium",
                    onClick: {},
                    size: .medium
                )

                LemonadeUi.Button(
                    label: "Large",
                    onClick: {},
                    size: .large
                )
            }

            // Disabled
            LemonadeUi.Button(
                label: "Disabled",
                onClick: {},
                enabled: false
            )

            // Full width
            LemonadeUi.Button(
                label: "Full Width",
                onClick: {}
            )
            .frame(maxWidth: .infinity)
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
