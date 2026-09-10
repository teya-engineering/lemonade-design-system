import SwiftUI

// MARK: - LemonadeContentListItemLayout

/// Defines the layout arrangement for ContentListItem.
/// - `horizontal`: Label on the left, value on the right (same line)
/// - `vertical`: Label on top, value below
public enum LemonadeContentListItemLayout {
    case horizontal
    case vertical
}

// MARK: - LemonadeContentListItemDensity

/// Defines the vertical density for ContentListItem.
/// - `comfortable`: Larger vertical padding (spacing400)
/// - `compact`: Reduced vertical padding (spacing200)
public enum LemonadeContentListItemDensity {
    case comfortable
    case compact
}

// MARK: - Public API

public extension LemonadeUi {
    /// A display-only list item for showing label-value pairs.
    ///
    /// Supports horizontal (label left, value right) and vertical (label top, value bottom) layouts.
    /// In vertical layout, providing a `contentSlot` switches the value to a larger typography.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.ContentListItem(
    ///     label: "Account holder",
    ///     value: "John Doe"
    /// )
    ///
    /// LemonadeUi.ContentListItem(
    ///     label: "Balance",
    ///     value: "$1,234.56",
    ///     layout: .vertical
    /// ) {
    ///     LemonadeUi.Tag(label: "Available", voice: .positive)
    /// }
    /// ```
    ///
    /// - Parameters:
    ///   - label: Label String describing the data field
    ///   - value: Value String to display
    ///   - layout: Horizontal or vertical arrangement
    ///   - showDivider: Whether to display a bottom divider below the item
    ///   - density: Controls the vertical padding. Defaults to `.comfortable`
    ///   - leadingSlot: Optional leading element (e.g. SymbolContainer)
    ///   - trailingSlot: Optional trailing element (e.g. icon action)
    ///   - contentSlot: Optional additional content. In vertical layout, switches value to larger typography
    ///   - verticalAlignment: Vertical alignment for horizontal layout (default `.center`)
    ///   - labelMaxLines: Maximum number of lines for the label before it truncates. Defaults to `nil` (no limit).
    ///   - labelOverflow: Truncation mode applied to the label when it exceeds `labelMaxLines`. Defaults to `.tail`.
    ///   - valueMaxLines: Maximum number of lines for the value before it truncates. Defaults to `nil` (no limit).
    ///   - valueOverflow: Truncation mode applied to the value when it exceeds `valueMaxLines`. Defaults to `.tail`.
    @ViewBuilder
    static func ContentListItem<Leading: View, Trailing: View, Content: View>(
        label: String,
        value: String,
        layout: LemonadeContentListItemLayout = .horizontal,
        showDivider: Bool = false,
        density: LemonadeContentListItemDensity = .comfortable,
        verticalAlignment: VerticalAlignment = .center,
        labelMaxLines: Int? = nil,
        labelOverflow: Text.TruncationMode = .tail,
        valueMaxLines: Int? = nil,
        valueOverflow: Text.TruncationMode = .tail,
        @ViewBuilder leadingSlot: @escaping () -> Leading = { EmptyView() },
        @ViewBuilder trailingSlot: @escaping () -> Trailing = { EmptyView() },
        @ViewBuilder contentSlot: @escaping () -> Content = { EmptyView() }
    ) -> some View {
        LemonadeContentListItemView(
            label: label,
            value: value,
            layout: layout,
            showDivider: showDivider,
            density: density,
            verticalAlignment: verticalAlignment,
            labelMaxLines: labelMaxLines,
            labelOverflow: labelOverflow,
            valueMaxLines: valueMaxLines,
            valueOverflow: valueOverflow,
            leadingSlot: leadingSlot,
            trailingSlot: trailingSlot,
            contentSlot: contentSlot
        )
    }
}

// MARK: - Internal View

private struct LemonadeContentListItemView<Leading: View, Trailing: View, Content: View>: View {
    let label: String
    let value: String
    let layout: LemonadeContentListItemLayout
    let showDivider: Bool
    let density: LemonadeContentListItemDensity
    let verticalAlignment: VerticalAlignment
    let labelMaxLines: Int?
    let labelOverflow: Text.TruncationMode
    let valueMaxLines: Int?
    let valueOverflow: Text.TruncationMode
    @ViewBuilder let leadingSlot: () -> Leading
    @ViewBuilder let trailingSlot: () -> Trailing
    @ViewBuilder let contentSlot: () -> Content

    private var verticalPadding: CGFloat {
        switch density {
        case .comfortable:
            LemonadeTheme.spaces.spacing400
        case .compact:
            LemonadeTheme.spaces.spacing200
        }
    }

    private var hasContentSlot: Bool {
        Content.self != EmptyView.self
    }

    private var hasTrailingSlot: Bool {
        Trailing.self != EmptyView.self
    }

    private var hasLeadingSlot: Bool {
        Leading.self != EmptyView.self
    }

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch layout {
                case .horizontal:
                    horizontalLayout
                case .vertical:
                    verticalLayout
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            .padding(.vertical, verticalPadding)

            if showDivider {
                LemonadeUi.HorizontalDivider()
                    .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            }
        }
    }

    private var horizontalLayout: some View {
        HStack(alignment: verticalAlignment, spacing: LemonadeTheme.spaces.spacing300) {
            if hasLeadingSlot {
                leadingSlot()
            }

            VStack(alignment: .leading, spacing: 0) {
                LemonadeUi.Text(
                    label,
                    textStyle: LemonadeTypography.shared.bodyMediumRegular,
                    color: LemonadeTheme.colors.content.contentSecondary,
                    overflow: labelOverflow,
                    maxLines: labelMaxLines
                )

                if hasContentSlot {
                    contentSlot()
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: LemonadeTheme.spaces.spacing300) {
                LemonadeUi.Text(
                    value,
                    textStyle: LemonadeTypography.shared.bodyMediumMedium,
                    textAlign: .trailing,
                    color: LemonadeTheme.colors.content.contentPrimary,
                    overflow: valueOverflow,
                    maxLines: valueMaxLines
                )

                if hasTrailingSlot {
                    trailingSlot()
                }
            }
        }
    }

    private var verticalLayout: some View {
        HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing300) {
            if hasLeadingSlot {
                leadingSlot()
            }

            VStack(alignment: .leading, spacing: 0) {
                LemonadeUi.Text(
                    label,
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: LemonadeTheme.colors.content.contentSecondary,
                    overflow: labelOverflow,
                    maxLines: labelMaxLines
                )

                HStack(spacing: LemonadeTheme.spaces.spacing100) {
                    LemonadeUi.Text(
                        value,
                        textStyle: hasContentSlot
                            ? LemonadeTypography.shared.bodyXLargeSemiBold
                            : LemonadeTypography.shared.bodyMediumMedium,
                        color: LemonadeTheme.colors.content.contentPrimary,
                        overflow: valueOverflow,
                        maxLines: valueMaxLines
                    )
                    .frame(maxWidth: .infinity, alignment: .leading)

                    if hasTrailingSlot {
                        trailingSlot()
                    }
                }

                if hasContentSlot {
                    contentSlot()
                }
            }
        }
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeContentListItem_Previews: PreviewProvider {
    private static var stackedList: some View {
        VStack(spacing: 0) {
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                showDivider: true
            )
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                showDivider: true
            )
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value"
            )
        }
    }

    private static var stackedCompactList: some View {
        VStack(spacing: 0) {
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                showDivider: true,
                density: .compact
            )
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                showDivider: true,
                density: .compact
            )
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                density: .compact
            )
        }
    }

    static var previews: some View {
        ScrollView {
            VStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.ContentListItem(
                    label: "Account holder",
                    value: "John Doe"
                )

                LemonadeUi.ContentListItem(
                    label: "Label",
                    value: "Value",
                    leadingSlot: {
                        LemonadeUi.SymbolContainer(
                            icon: .heart,
                            contentDescription: nil,
                            size: .medium
                        )
                    },
                    trailingSlot: {
                        LemonadeUi.Icon(
                            icon: .pencilLine,
                            contentDescription: "Edit",
                            size: .medium,
                            tint: LemonadeTheme.colors.content.contentBrand
                        )
                    }
                )

                LemonadeUi.HorizontalDivider()

                LemonadeUi.ContentListItem(
                    label: "Balance",
                    value: "$1,234.56",
                    layout: .vertical
                )

                LemonadeUi.ContentListItem(
                    label: "Balance",
                    value: "$1,234.56",
                    layout: .vertical,
                    leadingSlot: {
                        LemonadeUi.SymbolContainer(
                            icon: .heart,
                            contentDescription: nil,
                            size: .medium
                        )
                    },
                    trailingSlot: {
                        LemonadeUi.Icon(
                            icon: .pencilLine,
                            contentDescription: "Edit",
                            size: .medium,
                            tint: LemonadeTheme.colors.content.contentBrand
                        )
                    },
                    contentSlot: {
                        LemonadeUi.Tag(label: "Available", voice: .positive)
                    }
                )

                stackedList

                stackedCompactList
            }
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
