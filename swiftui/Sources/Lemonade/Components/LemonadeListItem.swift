import SwiftUI

// MARK: - SelectListItemType

/// Defines the selection behavior type for SelectListItem.
/// - `single`: Radio button selection (only one item can be selected)
/// - `multiple`: Checkbox selection (multiple items can be selected)
/// - `toggle`: Switch control in trailing slot (toggles on each tap)
public enum SelectListItemType {
    case single
    case multiple
    case toggle
}

// MARK: - SelectListItemVariant

/// Defines the visual variant of a SelectListItem.
/// - `plain`: Bare row, meant to sit inside a surrounding `LemonadeUi.Card` or surface. Uses
///   press/hover interaction background, supports dividers and loading skeletons.
/// - `outlined`: Card-shaped container with its own rounded background and border, brand-tinted
///   when selected. Stands alone in a stack without a surrounding surface.
public enum SelectListItemVariant {
    case plain
    case outlined
}

// MARK: - LemonadeListItemVoice

/// Defines the tone of voice for ListItem.
public enum LemonadeListItemVoice {
    case neutral
    case critical
    
    var interactionBackground: Color {
        switch self {
        case .neutral: return LemonadeTheme.colors.interaction.bgSubtleInteractive
        case .critical: return LemonadeTheme.colors.interaction.bgCriticalSubtleInteractive
        }
    }
    
    var contentColor: Color {
        switch self {
        case .neutral: return LemonadeTheme.colors.content.contentPrimary
        case .critical: return LemonadeTheme.colors.content.contentCritical
        }
    }
}

// MARK: - LemonadeListItemPriority

/// Defines which slot claims layout space first when the label and trailing
/// content compete for the available width.
/// - `label`: The label/content slot is laid out at its preferred width first; the
///   trailing slot compresses or truncates to fit.
/// - `trailing`: The trailing slot claims its intrinsic width first; the label
///   compresses or truncates to fit. This is the default.
/// - `both`: Neither slot is prioritized; the available width is split evenly so
///   the label and trailing content each occupy half, truncating together.
public enum LemonadeListItemPriority {
    case label
    case trailing
    case both
}

// MARK: - ListItem

public extension LemonadeUi {
    /// Convenience overload that composes standard label and support-text content from string
    /// parameters and delegates to the content-slot variant of ListItem.
    ///
    /// - Parameters:
    ///   - label: Label String to be displayed
    ///   - topLabel: Optional label displayed above the main label
    ///   - supportText: Optional support text displayed below the label
    ///   - voice: LemonadeListItemVoice to define tone of voice
    ///   - navigationIndicator: Shows a chevron-right navigation indicator
    ///   - isLoading: Shows a skeleton loading placeholder instead of content
    ///   - enabled: Flag to define if component is enabled
    ///   - showDivider: Flag to show a divider below the list item
    ///   - leadingAlignment: Vertical alignment of the leading slot. Pass `nil` (the default) to let the
    ///     component decide: it centers the leading slot against the text when the content is a single line
    ///     (no `topLabel`, `supportText`, or `slotContent`) and top-aligns it otherwise. Pass an explicit
    ///     value to override this automatic behavior.
    ///   - trailingAlignment: Vertical alignment of the content row — both the label/content and the trailing slot align to it (e.g. `.top` keeps them on the same first line when one wraps). Defaults to `.center`.
    ///   - priority: Which slot claims layout space first when label and trailing content compete for width. Use `.both` to split the width evenly. Defaults to `.trailing`.
    ///   - labelMaxLines: Maximum number of lines for the label before it truncates. Defaults to `nil` (no limit).
    ///   - labelOverflow: Truncation mode applied to the label when it exceeds `labelMaxLines`. Defaults to `.tail`.
    ///   - supportTextMaxLines: Maximum number of lines for the support text before it truncates. Defaults to `nil` (no limit).
    ///   - supportTextOverflow: Truncation mode applied to the support text when it exceeds `supportTextMaxLines`. Defaults to `.tail`.
    ///   - onListItemClick: Optional callback triggered on click interaction
    ///   - leadingSlot: Slot content to be placed in leading position
    ///   - trailingSlot: Slot content to be placed in trailing position
    ///   - slotContent: Optional slot content below the label and support text
    @ViewBuilder
    static func ListItem<LeadingContent: View, TrailingContent: View, SlotContent: View>(
        label: String,
        topLabel: String? = nil,
        supportText: String? = nil,
        voice: LemonadeListItemVoice = .neutral,
        navigationIndicator: Bool = false,
        isLoading: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment? = nil,
        trailingAlignment: VerticalAlignment = .center,
        priority: LemonadeListItemPriority = .trailing,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        supportTextMaxLines: Int? = nil,
        supportTextOverflow: Text.TruncationMode = .tail,
        onListItemClick: (() -> Void)? = nil,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent,
        @ViewBuilder trailingSlot: @escaping () -> TrailingContent,
        @ViewBuilder slotContent: @escaping () -> SlotContent = { EmptyView() }
    ) -> some View {
        if isLoading {
            ListItemSkeletonView(showDivider: showDivider)
        } else {
            // The content is effectively single-line when there is nothing stacked below the
            // label — no top label, no support text, and no custom slot content. In that case the
            // leading slot is centered against the lone text line unless the caller explicitly pins
            // an alignment.
            let isSingleLineContent = topLabel == nil
                && supportText == nil
                && SlotContent.self == EmptyView.self

            ListItem(
                voice: voice,
                navigationIndicator: navigationIndicator,
                enabled: enabled,
                showDivider: showDivider,
                leadingAlignment: leadingAlignment ?? (isSingleLineContent ? .center : .top),
                trailingAlignment: trailingAlignment,
                priority: priority,
                onListItemClick: onListItemClick,
                leadingSlot: leadingSlot,
                trailingSlot: trailingSlot,
                contentSlot: {
                    if let topLabel = topLabel {
                        LemonadeUi.Text(
                            topLabel,
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentSecondary
                        )
                    }

                    LemonadeUi.Text(
                        label,
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        color: voice.contentColor,
                        overflow: labelOverflow,
                        maxLines: labelMaxLines
                    )

                    if let supportText = supportText {
                        LemonadeUi.Text(
                            supportText,
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentSecondary,
                            overflow: supportTextOverflow,
                            maxLines: supportTextMaxLines
                        )
                    }
                    
                    if SlotContent.self != EmptyView.self {
                        slotContent()
                    }
                }
            )
        }
    }
    
    /// Foundational list-item overload that accepts a generic content slot for custom content,
    /// delegating layout and interaction handling to LemonadeCoreListItemView.
    ///
    /// - Parameters:
    ///   - voice: LemonadeListItemVoice to define tone of voice
    ///   - navigationIndicator: Shows a chevron-right navigation indicator
    ///   - enabled: Flag to define if component is enabled
    ///   - showDivider: Flag to show a divider below the list item
    ///   - leadingAlignment: Vertical alignment of the leading slot. Defaults to `.top`.
    ///   - trailingAlignment: Vertical alignment of the content row — both the label/content and the trailing slot align to it (e.g. `.top` keeps them on the same first line when one wraps). Defaults to `.center`.
    ///   - priority: Which slot claims layout space first when content and trailing slots compete for width. Use `.both` to split the width evenly. Defaults to `.trailing`.
    ///   - onListItemClick: Optional callback triggered on click interaction
    ///   - leadingSlot: Slot content to be placed in leading position
    ///   - trailingSlot: Slot content to be placed in trailing position
    ///   - contentSlot: Content slot for the main body of the list item
    @ViewBuilder
    static func ListItem<ContentSlot: View, LeadingContent: View, TrailingContent: View>(
        voice: LemonadeListItemVoice = .neutral,
        navigationIndicator: Bool = false,
        enabled: Bool = true,
        showDivider: Bool = false,
        leadingAlignment: VerticalAlignment = .top,
        trailingAlignment: VerticalAlignment = .center,
        priority: LemonadeListItemPriority = .trailing,
        onListItemClick: (() -> Void)? = nil,
        @ViewBuilder leadingSlot: @escaping () -> LeadingContent,
        @ViewBuilder trailingSlot: @escaping () -> TrailingContent,
        @ViewBuilder contentSlot: @escaping () -> ContentSlot
    ) -> some View {
        LemonadeCoreListItemView(
            contentSlot: contentSlot,
            voice: voice,
            navigationIndicator: navigationIndicator,
            enabled: enabled,
            showDivider: showDivider,
            leadingAlignment: leadingAlignment,
            trailingAlignment: trailingAlignment,
            priority: priority,
            onListItemClick: onListItemClick,
            leadingSlot: leadingSlot,
            trailingSlot: trailingSlot
        )
    }
}

// MARK: - Core ListItem View

struct LemonadeCoreListItemView<ContentSlot: View, LeadingContent: View, TrailingContent: View>: View {
    let contentSlot: () -> ContentSlot
    let voice: LemonadeListItemVoice
    let navigationIndicator: Bool
    let enabled: Bool
    let showDivider: Bool
    let leadingAlignment: VerticalAlignment
    let trailingAlignment: VerticalAlignment
    let priority: LemonadeListItemPriority
    let onListItemClick: (() -> Void)?
    let leadingSlot: () -> LeadingContent
    let trailingSlot: () -> TrailingContent
    
    private var hasLeading: Bool {
        LeadingContent.self != EmptyView.self
    }
    
    private var hasTrailing: Bool {
        TrailingContent.self != EmptyView.self
    }

    // The minimum width the filler (non-prioritized) slot keeps so a long
    // prioritized slot can't squeeze it down to just an ellipsis.
    private var minReadableSlotWidth: CGFloat { LemonadeTheme.sizes.size2000 }

    // The content slot is intrinsically sized only when the label is prioritized;
    // otherwise it expands to fill (and, for `.both`, shares the fill equally).
    private var contentMaxWidth: CGFloat? {
        priority == .label ? nil : .infinity
    }

    // When the trailing slot is prioritized, the content becomes the filler and
    // keeps a readable floor so a long trailing value can't squeeze it to an
    // ellipsis. Only applies when there's actually a trailing slot competing for
    // width — otherwise a label-only row would be forced wider than a narrow
    // container and overflow.
    private var contentMinWidth: CGFloat? {
        priority == .trailing && (hasTrailing || navigationIndicator)
            ? minReadableSlotWidth
            : nil
    }

    private var contentLayoutPriority: Double {
        priority == .label ? 1 : 0
    }

    // The trailing slot is intrinsically sized only when it is prioritized;
    // otherwise it expands to fill (and, for `.both`, shares the fill equally).
    private var trailingMinWidth: CGFloat? {
        priority == .label && (hasTrailing || navigationIndicator)
            ? minReadableSlotWidth
            : nil
    }

    private var trailingMaxWidth: CGFloat? {
        priority == .trailing ? nil : .infinity
    }

    private var trailingLayoutPriority: Double {
        priority == .trailing ? 1 : 0
    }

    // `.top` is documented as "keep the label and trailing slot on the same
    // first line when one wraps". Frame-top alignment can't deliver that:
    // `LemonadeUi.Text` pads a single line to its line-height box and centers
    // the glyphs within it, while a wrapping slot lays its first line flush to
    // the top — so the two first lines end up offset by the half-leading
    // (~2-3pt). Aligning on the first text baseline lines the glyphs up exactly,
    // regardless of how many lines each slot wraps to. Other alignments pass
    // through unchanged so `.center`/`.bottom` keep their literal meaning.
    private var contentRowAlignment: VerticalAlignment {
        trailingAlignment == .top ? .firstTextBaseline : trailingAlignment
    }

    var body: some View {
        ListItemSafeArea(showDivider: showDivider) {
            if let onClick = onListItemClick, enabled {
                Button(action: onClick) {
                    listItemContent
                }
                .buttonStyle(ListItemButtonStyle(voice: voice))
                .disabled(!enabled)
            } else {
                listItemContent
            }
        }
    }
    
    private var listItemContent: some View {
        HStack(alignment: leadingAlignment, spacing: 0) {
            if hasLeading {
                leadingSlot()
                    .frame(alignment: Alignment(horizontal: .center, vertical: leadingAlignment))
                    .padding(.trailing, LemonadeTheme.spaces.spacing300)
                    .padding(.vertical, LemonadeTheme.spaces.spacing50)
                    .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
            }
            
            HStack(alignment: contentRowAlignment, spacing: 0) {
                // The non-prioritized slot becomes the flexible filler: it expands to
                // claim the remaining width and truncates first, pinning the prioritized
                // (intrinsically-sized) slot to its edge.
                VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing0) {
                    contentSlot()
                }
                .frame(
                    minWidth: contentMinWidth,
                    maxWidth: contentMaxWidth,
                    maxHeight: .infinity,
                    alignment: Alignment(horizontal: .leading, vertical: trailingAlignment)
                )
                .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
                .layoutPriority(contentLayoutPriority)

                HStack(spacing: 0) {
                    if hasTrailing {
                        trailingSlot()
                    }

                    if navigationIndicator {
                        LemonadeUi.Icon(
                            icon: .chevronRight,
                            contentDescription: nil,
                            size: .medium,
                            tint: LemonadeTheme.colors.content.contentTertiary
                        )
                        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
                        .padding(.leading, LemonadeTheme.spaces.spacing100)
                    }
                }
                .frame(
                    minWidth: trailingMinWidth,
                    maxWidth: trailingMaxWidth,
                    alignment: .trailing
                )
                .padding(
                    .leading,
                    hasTrailing || navigationIndicator ? LemonadeTheme.spaces.spacing300 : 0
                )
                .layoutPriority(trailingLayoutPriority)
            }
        }
        .padding(.horizontal, LemonadeTheme.spaces.spacing300)
        .padding(.vertical, LemonadeTheme.spaces.spacing300)
        .frame(minHeight: LemonadeTheme.sizes.size1200)
        .fixedSize(horizontal: false, vertical: true)
    }
}

// MARK: - SafeArea Wrapper

struct ListItemSafeArea<Content: View>: View {
    let showDivider: Bool
    @ViewBuilder let content: () -> Content
    
    var body: some View {
        VStack(spacing: 0) {
            content()
                .padding(LemonadeTheme.spaces.spacing100)
            
            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            }
        }
    }
}

// MARK: - ListItem Button Style

struct ListItemButtonStyle: ButtonStyle {
    let voice: LemonadeListItemVoice
    
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .background(
                RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500)
                    .fill(
                        configuration.isPressed
                        ? voice.interactionBackground
                        : Color.clear
                    )
            )
            .contentShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius500))
            .animation(.easeInOut(duration: 0.15), value: configuration.isPressed)
    }
}

// MARK: - ListItem Skeleton (Loading State)

private struct ListItemSkeletonView: View {
    let showDivider: Bool
    
    var body: some View {
        ListItemSafeArea(showDivider: showDivider) {
            HStack(alignment: .top, spacing: 0) {
                LemonadeUi.CircleSkeleton(size: .xLarge)
                    .padding(.trailing, LemonadeTheme.spaces.spacing300)
                
                HStack(alignment: .top, spacing: 0) {
                    VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing0) {
                        LemonadeUi.LineSkeleton(size: .medium)
                        LemonadeUi.LineSkeleton(size: .xSmall)
                            .frame(width: 128)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Spacer()
                        .frame(width: LemonadeTheme.spaces.spacing800)
                    
                    LemonadeUi.LineSkeleton(size: .medium)
                        .frame(width: 54)
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing300)
            .padding(.vertical, LemonadeTheme.spaces.spacing300)
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeListItem_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Priority .trailing (default) — trailing keeps its width, label truncates
            LemonadeUi.ListItem(
                label: "Beneficiary account holder",
                showDivider: true,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: {
                    LemonadeUi.Text(
                        "International Holdings Ltd Partnership",
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        maxLines: 1
                    )
                }
            )

            // Priority .label — label keeps its width, trailing truncates
            LemonadeUi.ListItem(
                label: "Beneficiary account holder",
                showDivider: true,
                priority: .label,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: {
                    LemonadeUi.Text(
                        "International Holdings Ltd Partnership",
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        maxLines: 1
                    )
                }
            )

            // Priority .both — label and trailing each take half, truncating together
            LemonadeUi.ListItem(
                label: "Beneficiary account holder",
                showDivider: true,
                priority: .both,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: {
                    LemonadeUi.Text(
                        "International Holdings Ltd Partnership",
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        maxLines: 1
                    )
                }
            )

            // Top alignment — label keeps first-line alignment when trailing wraps
            LemonadeUi.ListItem(
                label: "Delivery to",
                showDivider: true,
                trailingAlignment: .top,
                priority: .label,
                leadingSlot: { EmptyView() },
                trailingSlot: {
                    LemonadeUi.Text(
                        "Rua de Olivenca, 55, esq 2, Algés, OX20 1PP",
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        textAlign: .trailing
                    )
                }
            )

            // SelectListItem - Single with divider
            LemonadeUi.SelectListItem(
                label: "Single Selection",
                type: .single,
                checked: true,
                onItemClicked: {},
                showDivider: true,
                supportText: "Support text"
            )
            
            // SelectListItem - Multiple with divider
            LemonadeUi.SelectListItem(
                label: "Multiple Selection",
                type: .multiple,
                checked: false,
                onItemClicked: {},
                showDivider: true,
                supportText: "Support text"
            )
            
            LemonadeUi.ListItem(
                label: "Resource Label",
                supportText: "09:37 • [Automated TQC TestingStore] Way4 ES 01",
                showDivider: true,
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .heart,
                        contentDescription: nil,
                        size: .medium,
                        shape: .rounded
                    )
                },
                trailingSlot: {
                    LemonadeUi.Text(
                        "$100.00",
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        textAlign: .trailing
                    )
                }
            )
            
            // ResourceListItem with divider
            LemonadeUi.ResourceListItem(
                label: "Resource Label",
                value: "$100.00",
                supportText: "09:37 • [Automated TQC TestingStore] Way4 ES 01",
                showDivider: true,
                onItemClicked: {},
            ) {
                LemonadeUi.SymbolContainer(
                    icon: .heart,
                    contentDescription: nil,
                    size: .medium,
                    shape: .rounded
                )
            }
            
            LemonadeUi.ResourceListItem(
                label: "14 Apr sales",
                value: "$5,000.00",
                supportText: "Paid on 22 Apr • Onion Garden",
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .check,
                        contentDescription: nil,
                        voice: .positive,
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
            
            // ResourceListItem with addon and divider
            LemonadeUi.ResourceListItem(
                label: "With Addon",
                value: "$50.00",
                supportText: "Metadata",
                onItemClicked: {},
                addonSlot: {
                    LemonadeUi.Tag(label: "Approved", voice: .positive)
                },
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .star,
                        contentDescription: nil,
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
            
            LemonadeUi.HorizontalDivider()
                .padding(.vertical, LemonadeTheme.spaces.spacing200)
            
            // ActionListItem with divider
            
            LemonadeUi.ActionListItem(
                label: "Action Item",
                supportText: "Support text",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .heart,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
            
            // ActionListItem - Critical with divider
            LemonadeUi.ActionListItem(
                label: "Delete Account",
                voice: .critical,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .trash,
                        contentDescription: nil,
                        size: .medium,
                        tint: LemonadeTheme.colors.content.contentCritical
                    )
                }
            )
            
            // ActionListItem - Loading
            LemonadeUi.ActionListItem(
                label: "Delete Account",
                isLoading: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .trash,
                        contentDescription: nil,
                        size: .medium,
                        tint: LemonadeTheme.colors.content.contentCritical
                    )
                }
            )
        }
        .previewLayout(.sizeThatFits)
    }
}
#endif
