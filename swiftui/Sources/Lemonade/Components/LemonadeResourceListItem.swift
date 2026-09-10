import SwiftUI

// MARK: - ResourceListItem Component

public extension LemonadeUi {
    /// A list item for resource info display.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.ResourceListItem(
    ///     label: "Label",
    ///     value: "Value",
    ///     supportText: "Description",
    ///     showDivider: true
    /// ) {
    ///     LemonadeUi.SymbolContainer(icon: .heart, contentDescription: nil)
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - label: Main String to be displayed
    ///   - value: Value String to be displayed in trailing position
    ///   - supportText: String to be displayed as supportText
    ///   - isLoading: Shows a skeleton loading placeholder instead of content
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - showDivider: Flag to show a divider below the list item. Defaults to false
    ///   - onItemClicked: Callback called when component is tapped
    ///   - addonSlot: Slot to be displayed below the value
    ///   - leadingSlot: Slot component to be placed in leading position
    /// - Returns: A styled ResourceListItem view
    @ViewBuilder
    static func ResourceListItem<LeadingContent: View, AddonContent: View>(
        label: String,
        value: String,
        supportText: String? = nil,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder addonSlot: @escaping () -> AddonContent,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent
    ) -> some View {
        ListItem(
            label: label,
            supportText: supportText,
            voice: .neutral,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            // The content's vertical position follows this alignment, so a row with no support
            // text only centres correctly while its label is short enough not to wrap.
            trailingAlignment: supportText == nil ? .center : .top,
            onListItemClick: onItemClicked,
            leadingSlot: {
                leadingSlot()
                    .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            },
            trailingSlot: {
                VStack(alignment: .trailing, spacing: .space.spacing0) {
                    LemonadeUi.Text(
                        value,
                        textStyle: LemonadeTypography.shared.bodyMediumMedium
                    )

                    addonSlot()
                }
                .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            }
        )
    }

    /// A list item for resource info display without addon slot.
    @ViewBuilder
    static func ResourceListItem<LeadingContent: View>(
        label: String,
        value: String,
        supportText: String? = nil,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent
    ) -> some View {
        ResourceListItem(
            label: label,
            value: value,
            supportText: supportText,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            onItemClicked: onItemClicked,
            addonSlot: { EmptyView() },
            leadingSlot: leadingSlot
        )
    }
}

#if DEBUG
struct LemonadeResourceListItem_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: .space.spacing0) {
            LemonadeUi.ResourceListItem(
                label: "Credit •••• 8931",
                value: "£1,234.56",
                supportText: "Today 12:54 • Onion Garden",
                showDivider: true,
                onItemClicked: {},
                addonSlot: {
                    LemonadeUi.Tag(
                        label: "Approved",
                        icon: .circleCheck,
                        voice: .positive
                    )
                },
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        shape: .rounded,
                        content: {
                            LemonadeUi.BrandLogo(
                                logo: .mastercard,
                                size: .medium
                            )
                        }
                    )
                }
            )
            
            LemonadeUi.ResourceListItem(
                label: "14 Apr sales",
                value: "$5,000.00",
                supportText: "Paid on 22 Apr • Onion Garden",
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .coins,
                        contentDescription: nil,
                        voice: .positive,
                        size: .large,
                        shape: .rounded
                    )
                }
            )
        }
    }
}
#endif
