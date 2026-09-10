import SwiftUI

// MARK: - Tile Variant

/// Defines the visual variant for Tile.
public enum LemonadeTileVariant {
    case filled
    case outlined

    var backgroundColor: Color {
        switch self {
        case .filled: return LemonadeTheme.colors.background.bgElevated
        case .outlined: return .clear
        }
    }

    var backgroundPressedColor: Color {
        switch self {
        case .filled: return LemonadeTheme.colors.interaction.bgElevatedPressed
        case .outlined: return LemonadeTheme.colors.interaction.bgDefaultPressed
        }
    }

    var borderColor: Color {
        switch self {
        case .filled: return .clear
        case .outlined: return LemonadeTheme.colors.border.borderNeutralMedium
        }
    }

    var borderWidth: CGFloat {
        switch self {
        case .filled: return 0
        case .outlined: return LemonadeTheme.borderWidth.base.border40
        }
    }
}

// MARK: - Tile Orientation

/// Defines the layout direction for Tile.
public enum LemonadeTileOrientation {
    /// Icon above label — the default layout.
    case vertical
    /// Icon to the left of the label.
    case horizontal
}

// MARK: - Tile Component

public extension LemonadeUi {
    /// A tile component with icon and label.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Tile(
    ///     label: "Label",
    ///     icon: .heart,
    ///     variant: .filled,
    ///     onClick: { /* action */ }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the tile
    ///   - icon: LemonadeIcon to be displayed in the leading position
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - isSelected: Whether the tile is in a selected state. Defaults to false
    ///   - supportText: Optional secondary text displayed below the label
    ///   - onClick: Callback called when component is tapped
    ///   - variant: LemonadeTileVariant to define visual style. Defaults to .filled
    ///   - orientation: LemonadeTileOrientation to define layout direction. Defaults to .vertical
    ///   - stretched: Whether the tile should stretch to fill available width. Defaults to false. Ignored when orientation is `.horizontal` — horizontal tiles always fill available width.
    /// - Returns: A styled Tile view
    @ViewBuilder
    static func Tile(
        label: String,
        icon: LemonadeIcon,
        enabled: Bool = true,
        isSelected: Bool = false,
        supportText: String? = nil,
        onClick: (() -> Void)? = nil,
        variant: LemonadeTileVariant = .filled,
        orientation: LemonadeTileOrientation = .vertical,
        stretched: Bool = false
    ) -> some View {
        LemonadeTileView<EmptyView>(
            label: label,
            icon: icon,
            enabled: enabled,
            isSelected: isSelected,
            supportText: supportText,
            onClick: onClick,
            variant: variant,
            orientation: orientation,
            stretched: stretched,
            topAccessory: nil
        )
    }

    /// A tile component with icon, label, and a top-right accessory view.
    /// The topAccessory is only rendered in `.vertical` orientation.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Tile(
    ///     label: "Label",
    ///     icon: .heart,
    ///     variant: .filled,
    ///     onClick: { /* action */ }
    /// ) {
    ///     Image(systemName: "info.circle")
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the tile
    ///   - icon: LemonadeIcon to be displayed in the leading position
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - isSelected: Whether the tile is in a selected state. Defaults to false
    ///   - supportText: Optional secondary text displayed below the label
    ///   - onClick: Callback called when component is tapped
    ///   - variant: LemonadeTileVariant to define visual style. Defaults to .filled
    ///   - orientation: LemonadeTileOrientation to define layout direction. Defaults to .vertical
    ///   - stretched: Whether the tile should stretch to fill available width. Defaults to false. Ignored when orientation is `.horizontal` — horizontal tiles always fill available width.
    ///   - topAccessory: A view rendered at the top-right of the tile (vertical only)
    /// - Returns: A styled Tile view
    @ViewBuilder
    static func Tile<TopAccessory: View>(
        label: String,
        icon: LemonadeIcon,
        enabled: Bool = true,
        isSelected: Bool = false,
        supportText: String? = nil,
        onClick: (() -> Void)? = nil,
        variant: LemonadeTileVariant = .filled,
        orientation: LemonadeTileOrientation = .vertical,
        stretched: Bool = false,
        @ViewBuilder topAccessory: @escaping () -> TopAccessory
    ) -> some View {
        LemonadeTileView(
            label: label,
            icon: icon,
            enabled: enabled,
            isSelected: isSelected,
            supportText: supportText,
            onClick: onClick,
            variant: variant,
            orientation: orientation,
            stretched: stretched,
            topAccessory: topAccessory
        )
    }

    /// A tile component with a custom leading slot view instead of an icon.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Tile(
    ///     label: "Custom",
    ///     variant: .filled,
    ///     leadingSlot: {
    ///         LemonadeUi.Icon(icon: .shoppingBag, contentDescription: nil, size: .medium)
    ///     }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the tile
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - isSelected: Whether the tile is in a selected state. Defaults to false
    ///   - supportText: Optional secondary text displayed below the label
    ///   - onClick: Callback called when component is tapped
    ///   - variant: LemonadeTileVariant to define visual style. Defaults to .filled
    ///   - orientation: LemonadeTileOrientation to define layout direction. Defaults to .vertical
    ///   - stretched: Whether the tile should stretch to fill available width. Defaults to false. Ignored when orientation is `.horizontal` — horizontal tiles always fill available width.
    ///   - leadingSlot: A custom view rendered where the icon would normally appear
    /// - Returns: A styled Tile view
    @ViewBuilder
    static func Tile<LeadingContent: View>(
        label: String,
        enabled: Bool = true,
        isSelected: Bool = false,
        supportText: String? = nil,
        onClick: (() -> Void)? = nil,
        variant: LemonadeTileVariant = .filled,
        orientation: LemonadeTileOrientation = .vertical,
        stretched: Bool = false,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent
    ) -> some View {
        LemonadeTileSlotView<LeadingContent, EmptyView>(
            label: label,
            enabled: enabled,
            isSelected: isSelected,
            supportText: supportText,
            onClick: onClick,
            variant: variant,
            orientation: orientation,
            stretched: stretched,
            leadingSlot: leadingSlot,
            topAccessory: nil
        )
    }

    /// A tile component with a custom leading slot view and a top-right accessory.
    /// The topAccessory is only rendered in `.vertical` orientation.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Tile(
    ///     label: "Custom",
    ///     variant: .filled,
    ///     leadingSlot: {
    ///         LemonadeUi.Icon(icon: .shoppingBag, contentDescription: nil, size: .medium)
    ///     },
    ///     topAccessory: {
    ///         LemonadeUi.Icon(icon: .circleInfo, contentDescription: nil, size: .small)
    ///     }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the tile
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - isSelected: Whether the tile is in a selected state. Defaults to false
    ///   - supportText: Optional secondary text displayed below the label
    ///   - onClick: Callback called when component is tapped
    ///   - variant: LemonadeTileVariant to define visual style. Defaults to .filled
    ///   - orientation: LemonadeTileOrientation to define layout direction. Defaults to .vertical
    ///   - stretched: Whether the tile should stretch to fill available width. Defaults to false. Ignored when orientation is `.horizontal` — horizontal tiles always fill available width.
    ///   - leadingSlot: A custom view rendered where the icon would normally appear
    ///   - topAccessory: A view rendered at the top-right of the tile (vertical only)
    /// - Returns: A styled Tile view
    @ViewBuilder
    static func Tile<LeadingContent: View, TopAccessory: View>(
        label: String,
        enabled: Bool = true,
        isSelected: Bool = false,
        supportText: String? = nil,
        onClick: (() -> Void)? = nil,
        variant: LemonadeTileVariant = .filled,
        orientation: LemonadeTileOrientation = .vertical,
        stretched: Bool = false,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent,
        @ViewBuilder topAccessory: @escaping () -> TopAccessory
    ) -> some View {
        LemonadeTileSlotView(
            label: label,
            enabled: enabled,
            isSelected: isSelected,
            supportText: supportText,
            onClick: onClick,
            variant: variant,
            orientation: orientation,
            stretched: stretched,
            leadingSlot: leadingSlot,
            topAccessory: topAccessory
        )
    }

}

// MARK: - Haptic Helpers

#if os(iOS)
fileprivate func triggerTouchHaptic() {
    UIImpactFeedbackGenerator(style: .light).impactOccurred()
}

fileprivate func triggerSelectionHaptic() {
    UISelectionFeedbackGenerator().selectionChanged()
}
#else
fileprivate func triggerTouchHaptic() {}
fileprivate func triggerSelectionHaptic() {}
#endif

// MARK: - Tile Button Style

private struct LemonadeTileButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .opacity(configuration.isPressed ? .opacity.opacityPressed : .opacity.opacity100)
            .scaleEffect(configuration.isPressed ? 0.96 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

// MARK: - Tile Style

private struct TileStyle {
    let backgroundColor: Color
    let borderColor: Color
    let borderWidth: CGFloat
}

private extension LemonadeTileVariant {
    func resolvedStyle(isSelected: Bool) -> TileStyle {
        if isSelected {
            return TileStyle(
                backgroundColor: LemonadeTheme.colors.background.bgDefault,
                borderColor: LemonadeTheme.colors.border.borderSelected,
                borderWidth: LemonadeTheme.borderWidth.base.border50
            )
        }
        return TileStyle(backgroundColor: backgroundColor, borderColor: borderColor, borderWidth: borderWidth)
    }
}

// MARK: - Internal Tile View

private struct LemonadeTileView<TopAccessory: View>: View {
    let label: String
    let icon: LemonadeIcon
    let enabled: Bool
    let isSelected: Bool
    let supportText: String?
    let onClick: (() -> Void)?
    let variant: LemonadeTileVariant
    let orientation: LemonadeTileOrientation
    let stretched: Bool
    let topAccessory: (() -> TopAccessory)?

    private let minWidth: CGFloat = 120
    private var minHeightHorizontal: CGFloat { LemonadeTheme.sizes.size1600 }

    private var tileStyle: TileStyle { variant.resolvedStyle(isSelected: isSelected) }

    @ViewBuilder
    private var tileContent: some View {
        switch orientation {
        case .horizontal:
            HStack(
                alignment: .center,
                spacing: LemonadeTheme.spaces.spacing200
            ) {
                LemonadeUi.Icon(
                    icon: icon,
                    contentDescription: nil,
                    size: .medium,
                    tint: LemonadeTheme.colors.content.contentPrimary
                )
                tileTextContent
            }
            .frame(minWidth: minWidth, maxWidth: .infinity, alignment: .leading)
            .padding(LemonadeTheme.spaces.spacing300)
        case .vertical:
            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
                HStack {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .medium,
                        tint: LemonadeTheme.colors.content.contentPrimary
                    )
                    Spacer(minLength: 0)
                    if let topAccessory {
                        topAccessory()
                    }
                }
                Spacer(minLength: 0)
                tileTextContent
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(LemonadeTheme.spaces.spacing300)
        }
    }

    private var tileTextContent: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.Text(
                label,
                textStyle: LemonadeTypography.shared.bodySmallMedium,
                color: LemonadeTheme.colors.content.contentPrimary,
                overflow: .tail,
                maxLines: 1
            )
            if let supportText {
                LemonadeUi.Text(
                    supportText,
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: LemonadeTheme.colors.content.contentSecondary,
                    overflow: .tail,
                    maxLines: 1,
                )
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }

    private var tileShape: some View {
        Group {
            if #available(iOS 16, macOS 13, *) {
                DefaultMinSize(minWidth: minWidth) {
                    tileContent
                }
            } else {
                tileContent
                    .frame(minWidth: minWidth)
            }
        }
        .applyIf(stretched || orientation == .horizontal) {
            $0.frame(maxWidth: .infinity, alignment: .leading)
        }
        .applyIf(orientation == .horizontal) {
            $0.frame(minHeight: minHeightHorizontal)
        }
        .background(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500).fill(tileStyle.backgroundColor))
        .overlay(
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                .stroke(tileStyle.borderColor, lineWidth: tileStyle.borderWidth)
        )
        .opacity(enabled ? .opacity.opacity100 : LemonadeTheme.opacity.state.opacityDisabled)
        .contentShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500))
        .onChange(of: isSelected) { newValue in
            if newValue { triggerSelectionHaptic() }
        }
    }

    var body: some View {
        if let onClick {
            Button(action: { triggerTouchHaptic(); onClick() }) { tileShape }
                .buttonStyle(LemonadeTileButtonStyle())
                .disabled(!enabled)
        } else {
            tileShape
        }
    }
}

// MARK: - Internal Tile Slot View

private struct LemonadeTileSlotView<LeadingContent: View, TopAccessory: View>: View {
    let label: String
    let enabled: Bool
    let isSelected: Bool
    let supportText: String?
    let onClick: (() -> Void)?
    let variant: LemonadeTileVariant
    let orientation: LemonadeTileOrientation
    let stretched: Bool
    let leadingSlot: () -> LeadingContent
    let topAccessory: (() -> TopAccessory)?

    private let minWidth: CGFloat = 120
    private var minHeightHorizontal: CGFloat { LemonadeTheme.sizes.size1600 }

    private var tileStyle: TileStyle { variant.resolvedStyle(isSelected: isSelected) }

    @ViewBuilder
    private var tileContent: some View {
        switch orientation {
        case .horizontal:
            HStack(
                alignment: .center,
                spacing: LemonadeTheme.spaces.spacing200
            ) {
                leadingSlot()
                tileTextContent
            }
            .frame(minWidth: minWidth, maxWidth: .infinity, alignment: .leading)
            .padding(LemonadeTheme.spaces.spacing300)
        case .vertical:
            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
                HStack {
                    leadingSlot()
                    Spacer(minLength: 0)
                    if let topAccessory {
                        topAccessory()
                    }
                }
                Spacer(minLength: 0)
                tileTextContent
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(LemonadeTheme.spaces.spacing300)
        }
    }

    private var tileTextContent: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.Text(
                label,
                textStyle: LemonadeTypography.shared.bodySmallMedium,
                color: LemonadeTheme.colors.content.contentPrimary,
                overflow: .tail,
                maxLines: 1
            )
            if let supportText {
                LemonadeUi.Text(
                    supportText,
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: LemonadeTheme.colors.content.contentSecondary,
                    overflow: .tail,
                    maxLines: 1
                )
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }

    private var tileShape: some View {
        Group {
            if #available(iOS 16, macOS 13, *) {
                DefaultMinSize(minWidth: minWidth) {
                    tileContent
                }
            } else {
                tileContent
                    .frame(minWidth: minWidth)
            }
        }
        .applyIf(stretched || orientation == .horizontal) {
            $0.frame(maxWidth: .infinity, alignment: .leading)
        }
        .applyIf(orientation == .horizontal) {
            $0.frame(minHeight: minHeightHorizontal)
        }
        .background(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500).fill(tileStyle.backgroundColor))
        .overlay(
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                .stroke(tileStyle.borderColor, lineWidth: tileStyle.borderWidth)
        )
        .opacity(enabled ? .opacity.opacity100 : LemonadeTheme.opacity.state.opacityDisabled)
        .contentShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500))
        .onChange(of: isSelected) { newValue in
            if newValue { triggerSelectionHaptic() }
        }
    }

    var body: some View {
        if let onClick {
            Button(action: { triggerTouchHaptic(); onClick() }) { tileShape }
                .buttonStyle(LemonadeTileButtonStyle())
                .disabled(!enabled)
        } else {
            tileShape
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeTile_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            verticalTiles
            horizontalTiles
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }

    private static var verticalTiles: some View {
        HStack(spacing: 16) {
            LemonadeUi.Tile(
                label: "Filled",
                icon: .heart,
                onClick: {},
                variant: .filled
            )
            LemonadeUi.Tile(
                label: "Outlined",
                icon: .star,
                variant: .outlined
            )
            LemonadeUi.Tile(
                label: "Selected",
                icon: .circleCheck,
                isSelected: true,
                variant: .filled
            )
        }
    }

    private static var horizontalTiles: some View {
        VStack(spacing: 8) {
            LemonadeUi.Tile(
                label: "Transfer",
                icon: .heart,
                onClick: {},
                variant: .filled,
                orientation: .horizontal,
                stretched: true
            )
            LemonadeUi.Tile(
                label: "Payments",
                icon: .star,
                variant: .outlined,
                orientation: .horizontal,
                stretched: true
            )
            LemonadeUi.Tile(
                label: "Selected",
                icon: .circleCheck,
                isSelected: true,
                variant: .filled,
                orientation: .horizontal,
                stretched: true
            )
            LemonadeUi.Tile(
                label: "With Support",
                icon: .heart,
                supportText: "Subtitle",
                variant: .filled,
                orientation: .horizontal,
                stretched: true
            )
            LemonadeUi.Tile(
                label: "Disabled",
                icon: .heart,
                enabled: false,
                variant: .filled,
                orientation: .horizontal,
                stretched: true
            )
        }
        .padding(.horizontal)
    }
}
#endif
