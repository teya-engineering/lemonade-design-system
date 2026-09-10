import SwiftUI

// MARK: - ActionListItem Component

public extension LemonadeUi {
    /// Basic building block for list items.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.ActionListItem(
    ///     label: "Label",
    ///     supportText: "Description",
    ///     showDivider: true,
    ///     onItemClicked: { /* action */ },
    ///     leadingSlot: { LemonadeUi.Icon(icon: .heart, contentDescription: nil) },
    ///     trailingSlot: { LemonadeUi.Tag(label: "New", voice: .warning) },
    ///     slotContent: { LemonadeUi.Text("Inline notice", textStyle: LemonadeTypography.shared.bodySmallRegular, color: LemonadeTheme.colors.content.contentCaution) }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - label: Label String to be displayed
    ///   - topLabel: Optional label displayed above the main label
    ///   - supportText: Text to be displayed as supportText below the label
    ///   - voice: LemonadeListItemVoice to define tone of voice. Defaults to .neutral
    ///   - showNavigationIndicator: Indicates navigation visually
    ///   - isLoading: Shows a skeleton loading placeholder instead of content
    ///   - enabled: Flag to define if component is enabled. Defaults to true
    ///   - showDivider: Flag to show a divider below the list item. Defaults to false
    ///   - leadingAlignment: Vertical alignment of the leading slot. Pass `nil` (the default) to let the
    ///     component center the leading slot against the text for single-line content (no `topLabel`,
    ///     `supportText`, or `slotContent`) and top-align it otherwise. Pass an explicit value to override.
    ///   - trailingAlignment: Vertical alignment of the trailing slot and navigation indicator. Defaults to `.center`.
    ///   - labelMaxLines: Maximum number of lines for the label before it truncates. Defaults to `nil` (no limit).
    ///   - labelOverflow: Truncation mode applied to the label when it exceeds `labelMaxLines`. Defaults to `.tail`.
    ///   - supportTextMaxLines: Maximum number of lines for the support text before it truncates. Defaults to `nil` (no limit).
    ///   - supportTextOverflow: Truncation mode applied to the support text when it exceeds `supportTextMaxLines`. Defaults to `.tail`.
    ///   - onItemClicked: Callback called when component is tapped
    ///   - leadingSlot: Slot content to be placed in leading position
    ///   - trailingSlot: Slot content to be placed in trailing position
    ///   - slotContent: Optional slot rendered below the support text, inside the label column.
    ///     Use for secondary content like an inline status text, badge, or compact widget that should
    ///     sit under the row's text.
    /// - Returns: A styled ActionListItem view
    @ViewBuilder
    static func ActionListItem<LeadingContent: View, TrailingContent: View, SlotContent: View>(
        label: String,
        topLabel: String? = nil,
        supportText: String? = nil,
        voice: LemonadeListItemVoice = .neutral,
        showNavigationIndicator: Bool = false,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment? = nil,
        trailingAlignment: VerticalAlignment = .center,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        supportTextMaxLines: Int? = nil,
        supportTextOverflow: Text.TruncationMode = .tail,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent,
        @ViewBuilder trailingSlot: @escaping () -> TrailingContent,
        @ViewBuilder slotContent: @escaping () -> SlotContent = { EmptyView() }
    ) -> some View {
        ListItem(
            label: label,
            topLabel: topLabel,
            supportText: supportText,
            voice: voice,
            navigationIndicator: showNavigationIndicator,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            leadingAlignment: leadingAlignment,
            trailingAlignment: trailingAlignment,
            labelMaxLines: labelMaxLines,
            labelOverflow: labelOverflow,
            supportTextMaxLines: supportTextMaxLines,
            supportTextOverflow: supportTextOverflow,
            onListItemClick: onItemClicked,
            leadingSlot: leadingSlot,
            trailingSlot: {
                trailingSlot()
                    .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            },
            slotContent: slotContent
        )
    }

    /// ActionListItem without trailing slot.
    @ViewBuilder
    static func ActionListItem<LeadingContent: View, SlotContent: View>(
        label: String,
        topLabel: String? = nil,
        supportText: String? = nil,
        voice: LemonadeListItemVoice = .neutral,
        showNavigationIndicator: Bool = false,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment? = nil,
        trailingAlignment: VerticalAlignment = .center,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        supportTextMaxLines: Int? = nil,
        supportTextOverflow: Text.TruncationMode = .tail,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent,
        @ViewBuilder slotContent: @escaping () -> SlotContent = { EmptyView() }
    ) -> some View {
        ActionListItem(
            label: label,
            topLabel: topLabel,
            supportText: supportText,
            voice: voice,
            showNavigationIndicator: showNavigationIndicator,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            leadingAlignment: leadingAlignment,
            trailingAlignment: trailingAlignment,
            labelMaxLines: labelMaxLines,
            labelOverflow: labelOverflow,
            supportTextMaxLines: supportTextMaxLines,
            supportTextOverflow: supportTextOverflow,
            onItemClicked: onItemClicked,
            leadingSlot: leadingSlot,
            trailingSlot: { EmptyView() },
            slotContent: slotContent
        )
    }

    /// ActionListItem without leading slot.
    @ViewBuilder
    static func ActionListItem<TrailingContent: View, SlotContent: View>(
        label: String,
        topLabel: String? = nil,
        supportText: String? = nil,
        voice: LemonadeListItemVoice = .neutral,
        showNavigationIndicator: Bool = false,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment? = nil,
        trailingAlignment: VerticalAlignment = .center,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        supportTextMaxLines: Int? = nil,
        supportTextOverflow: Text.TruncationMode = .tail,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder trailingSlot: @escaping () -> TrailingContent,
        @ViewBuilder slotContent: @escaping () -> SlotContent = { EmptyView() }
    ) -> some View {
        ActionListItem(
            label: label,
            topLabel: topLabel,
            supportText: supportText,
            voice: voice,
            showNavigationIndicator: showNavigationIndicator,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            leadingAlignment: leadingAlignment,
            trailingAlignment: trailingAlignment,
            labelMaxLines: labelMaxLines,
            labelOverflow: labelOverflow,
            supportTextMaxLines: supportTextMaxLines,
            supportTextOverflow: supportTextOverflow,
            onItemClicked: onItemClicked,
            leadingSlot: { EmptyView() },
            trailingSlot: trailingSlot,
            slotContent: slotContent
        )
    }

    /// ActionListItem without slots.
    @ViewBuilder
    static func ActionListItem<SlotContent: View>(
        label: String,
        topLabel: String? = nil,
        supportText: String? = nil,
        voice: LemonadeListItemVoice = .neutral,
        showNavigationIndicator: Bool = false,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment? = nil,
        trailingAlignment: VerticalAlignment = .center,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        supportTextMaxLines: Int? = nil,
        supportTextOverflow: Text.TruncationMode = .tail,
        onItemClicked: (() -> Void)? = nil,
        @ViewBuilder slotContent: @escaping () -> SlotContent = { EmptyView() }
    ) -> some View {
        ActionListItem(
            label: label,
            topLabel: topLabel,
            supportText: supportText,
            voice: voice,
            showNavigationIndicator: showNavigationIndicator,
            isLoading: isLoading,
            enabled: enabled,
            showDivider: showDivider,
            leadingAlignment: leadingAlignment,
            trailingAlignment: trailingAlignment,
            labelMaxLines: labelMaxLines,
            labelOverflow: labelOverflow,
            supportTextMaxLines: supportTextMaxLines,
            supportTextOverflow: supportTextOverflow,
            onItemClicked: onItemClicked,
            leadingSlot: { EmptyView() },
            trailingSlot: { EmptyView() },
            slotContent: slotContent
        )
    }
}

#if DEBUG
struct LemonadeActionListItem_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 0) {
            LemonadeUi.ActionListItem(
                label: "Action",
                supportText: "Support text",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .star,
                        contentDescription: nil,
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
            LemonadeUi.ActionListItem(
                label: "Action",
                supportText: "Support text",
                showNavigationIndicator: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .star,
                        contentDescription: nil,
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
        }
        .frame(maxHeight: .infinity, alignment: .top)
    }
}
#endif
