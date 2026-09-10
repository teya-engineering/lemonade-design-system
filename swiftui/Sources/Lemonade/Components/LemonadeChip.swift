import SwiftUI

// MARK: - Chip Component

public extension LemonadeUi {
    /// A compact element used to display information, trigger actions, or represent selections.
    /// Commonly used for tags, filters, or interactive choices in dense interfaces.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Chip(
    ///     label: "Label",
    ///     selected: true,
    ///     leadingIcon: .airplane
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the chip
    ///   - selected: Set to true if the chip is in the selected state
    ///   - leadingIcon: Optional LemonadeIcon to be displayed in the leading position
    ///   - trailingIcon: Optional LemonadeIcon to be displayed in the trailing position
    ///   - counter: Optional Int number to be displayed in the chip
    ///   - enabled: Controls the enabled state of the chip. Defaults to true
    ///   - error: Set to true to display the chip in an error state (critical border and background). When true, takes precedence over `selected` styling. Defaults to false
    ///   - onChipClicked: Optional callback for when the chip is clicked
    ///   - onTrailingIconClick: Optional callback when the trailing icon is clicked
    /// - Returns: A styled Chip view
    @ViewBuilder
    static func Chip(
        label: String,
        selected: Bool,
        leadingIcon: LemonadeIcon? = nil,
        trailingIcon: LemonadeIcon? = nil,
        counter: Int? = nil,
        enabled: Bool = true,
        error: Bool = false,
        onChipClicked: (() -> Void)? = nil,
        onTrailingIconClick: (() -> Void)? = nil
    ) -> some View {
        LemonadeChipView(
            label: label,
            selected: selected,
            counter: counter,
            enabled: enabled,
            error: error,
            onChipClicked: onChipClicked,
            onTrailingIconClick: onTrailingIconClick,
            leadingContent: {
                if let icon = leadingIcon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .medium,
                        tint: error
                        ? LemonadeTheme.colors.content.contentCritical
                        : selected
                        ? LemonadeTheme.colors.content.contentBrandInverse
                        : LemonadeTheme.colors.content.contentPrimary
                    )
                }
            },
            trailingContent: {
                if let icon = trailingIcon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .small,
                        tint: error
                        ? LemonadeTheme.colors.content.contentPrimary
                        : selected
                        ? LemonadeTheme.colors.content.contentBrandInverse
                        : LemonadeTheme.colors.content.contentPrimary
                    )
                }
            }
        )
    }

    /// A compact element used to display information with a custom leading image.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Chip(
    ///     label: "Label",
    ///     selected: true,
    ///     leadingImage: Image("profile")
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the chip
    ///   - selected: Set to true if the chip is in the selected state
    ///   - leadingImage: Image to be displayed in the leading position
    ///   - trailingIcon: Optional LemonadeIcon to be displayed in the trailing position
    ///   - counter: Optional Int number to be displayed in the chip
    ///   - enabled: Controls the enabled state of the chip. Defaults to true
    ///   - error: Set to true to display the chip in an error state (critical border and background). When true, takes precedence over `selected` styling. Defaults to false
    ///   - onChipClicked: Optional callback for when the chip is clicked
    ///   - onTrailingIconClick: Optional callback when the trailing icon is clicked
    /// - Returns: A styled Chip view
    @ViewBuilder
    static func Chip(
        label: String,
        selected: Bool,
        leadingImage: Image,
        trailingIcon: LemonadeIcon? = nil,
        counter: Int? = nil,
        enabled: Bool = true,
        error: Bool = false,
        onChipClicked: (() -> Void)? = nil,
        onTrailingIconClick: (() -> Void)? = nil
    ) -> some View {
        LemonadeChipView(
            label: label,
            selected: selected,
            counter: counter,
            enabled: enabled,
            error: error,
            onChipClicked: onChipClicked,
            onTrailingIconClick: onTrailingIconClick,
            leadingContent: {
                leadingImage
                    .resizable()
                    .scaledToFill()
                    .frame(width: LemonadeTheme.sizes.size500, height: LemonadeTheme.sizes.size500)
                    .clipShape(Circle())
                    .overlay(
                        Circle()
                            .stroke(
                                LemonadeTheme.colors.border.borderNeutralMedium,
                                lineWidth: LemonadeTheme.borderWidth.base.border25
                            )
                    )
            },
            trailingContent: {
                if let icon = trailingIcon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .small,
                        tint: selected
                        ? LemonadeTheme.colors.content.contentBrandInverse
                        : LemonadeTheme.colors.content.contentPrimary
                    )
                }
            }
        )
    }
    
    /// A compact element with fully custom leading and trailing content.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Chip(
    ///     label: "Custom",
    ///     selected: false
    /// ) {
    ///     Circle().fill(.red).frame(width: 16, height: 16)
    /// } trailingContent: {
    ///     EmptyView()
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - label: The text to be displayed in the chip
    ///   - selected: Set to true if the chip is in the selected state
    ///   - counter: Optional Int number to be displayed in the chip
    ///   - enabled: Controls the enabled state of the chip. Defaults to true
    ///   - error: Set to true to display the chip in an error state (critical border and background). When true, takes precedence over `selected` styling. Defaults to false
    ///   - onChipClicked: Optional callback for when the chip is clicked
    ///   - onTrailingIconClick: Optional callback when the trailing icon is clicked
    ///   - leadingContent: Custom leading content
    ///   - trailingContent: Custom trailing content
    /// - Returns: A styled Chip view
    @ViewBuilder
    static func Chip<LeadingContent: View, TrailingContent: View>(
        label: String,
        selected: Bool,
        counter: Int? = nil,
        enabled: Bool = true,
        error: Bool = false,
        onChipClicked: (() -> Void)? = nil,
        onTrailingIconClick: (() -> Void)? = nil,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) -> some View {
        LemonadeChipView(
            label: label,
            selected: selected,
            counter: counter,
            enabled: enabled,
            error: error,
            onChipClicked: onChipClicked,
            onTrailingIconClick: onTrailingIconClick,
            leadingContent: leadingContent,
            trailingContent: trailingContent
        )
    }
}

// MARK: - Internal Chip View

private struct LemonadeChipView<LeadingContent: View, TrailingContent: View>: View {
    let label: String
    let selected: Bool
    let counter: Int?
    let enabled: Bool
    let error: Bool
    let onChipClicked: (() -> Void)?
    let onTrailingIconClick: (() -> Void)?
    @ViewBuilder let leadingContent: () -> LeadingContent
    @ViewBuilder let trailingContent: () -> TrailingContent

    @State private var isPressed = false

    private let minWidth: CGFloat = .size.size1600
    private let minHeight: CGFloat = .size.size800

    private var backgroundColor: Color {
        if error {
            return isPressed
                ? LemonadeTheme.colors.interaction.bgCriticalSubtleInteractive
                : LemonadeTheme.colors.background.bgCriticalSubtle
        }
        if isPressed {
            return selected
            ? LemonadeTheme.colors.interaction.bgBrandHighInteractive
            : LemonadeTheme.colors.interaction.bgSubtleInteractive
        }
        return selected
        ? LemonadeTheme.colors.background.bgBrandHigh
        : LemonadeTheme.colors.background.bgElevated
    }

    private var contentColor: Color {
        error
        ? LemonadeTheme.colors.content.contentPrimary
        : selected
        ? LemonadeTheme.colors.content.contentBrandInverse
        : LemonadeTheme.colors.content.contentPrimary
    }

    private var chipContent: some View {
        HStack(spacing: .space.spacing0) {
            leadingContent()

            LemonadeUi.Text(
                label,
                textStyle: LemonadeTypography.shared.bodySmallMedium,
                color: contentColor
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing100)

            if let counter = counter {
                LemonadeUi.Text("\(counter)", font: .bodyXSmallSemiBold)
                    .foregroundStyle(LemonadeTheme.colors.content.contentOnBrandHigh)
                    .padding(.horizontal, LemonadeTheme.spaces.spacing100)
                    .frame(minWidth: .size.size450, minHeight: .size.size400)
                    .background(Capsule().fill(.bg.bgBrand))
                    .padding(.trailing, .space.spacing50)
            }

            if let onTrailingIconClick = onTrailingIconClick {
                SwiftUI.Button(action: onTrailingIconClick) {
                    trailingContent()
                }
                .buttonStyle(PlainButtonStyle())
                .disabled(!enabled)
            } else {
                trailingContent()
            }
        }
        .padding(.space.spacing200)
        .frame(minWidth: minWidth, minHeight: minHeight)
        .background(RoundedRectangle(cornerRadius: .radius.radiusFull).fill(backgroundColor))
        .overlay {
            if error {
                RoundedRectangle(cornerRadius: .radius.radiusFull)
                    .stroke(
                        LemonadeTheme.colors.border.borderCritical,
                        lineWidth: LemonadeTheme.borderWidth.base.border40
                    )
                    .transition(.opacity)
            }
        }
        .opacity(enabled ? 1.0 : .opacity.opacityDisabled)
        .contentShape(Capsule())
        .animation(.easeInOut(duration: 0.15), value: backgroundColor)
    }
    
    var body: some View {
        if let onChipClicked = onChipClicked {
            SwiftUI.Button(action: onChipClicked) {
                chipContent
            }
            .buttonStyle(LemonadePressTrackingButtonStyle(isPressed: $isPressed))
            .disabled(!enabled)
        } else {
            chipContent
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeChip_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 16) {
            HStack(spacing: 8) {
                LemonadeUi.Chip(label: "Unselected", selected: false)
                LemonadeUi.Chip(label: "Selected", selected: true)
            }
            
            HStack(spacing: 8) {
                LemonadeUi.Chip(label: "Label", selected: false, counter: 5)
                LemonadeUi.Chip(label: "Label", selected: true, counter: 12)
            }
            
            HStack(spacing: 8) {
                LemonadeUi.Chip(
                    label: "Leading",
                    selected: false,
                    leadingIcon: .heart
                )
                LemonadeUi.Chip(
                    label: "Trailing",
                    selected: true,
                    trailingIcon: .circleX
                )
            }
            
            LemonadeUi.Chip(
                label: "Both Icons",
                selected: false,
                leadingIcon: .star,
                trailingIcon: .circleX
            )
            
            HStack(spacing: 8) {
                LemonadeUi.Chip(label: "Disabled", selected: false, enabled: false)
                LemonadeUi.Chip(label: "Disabled", selected: true, enabled: false)
            }

            HStack(spacing: 8) {
                LemonadeUi.Chip(label: "Error", selected: false, error: true)
                LemonadeUi.Chip(label: "Error Disabled", selected: false, enabled: false, error: true)
            }
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
