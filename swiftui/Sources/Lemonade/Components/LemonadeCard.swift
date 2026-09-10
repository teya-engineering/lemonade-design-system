import SwiftUI

// MARK: - Card Padding

/// Padding options for Card content.
public enum LemonadeCardPadding {
    case none
    case xSmall
    case small
    case medium

    var spacing: CGFloat {
        switch self {
        case .none: return LemonadeTheme.spaces.spacing0
        case .xSmall: return LemonadeTheme.spaces.spacing100
        case .small: return LemonadeTheme.spaces.spacing200
        case .medium: return LemonadeTheme.spaces.spacing400
        }
    }
}

// MARK: - Card Background

/// Background style options for Card.
public enum LemonadeCardBackground {
    case `default`
    case subtle
    case elevated

    var color: Color {
        switch self {
        case .default: return LemonadeTheme.colors.background.bgDefault
        case .subtle: return LemonadeTheme.colors.background.bgSubtle
        case .elevated: return LemonadeTheme.colors.background.bgElevated
        }
    }
}

// MARK: - Card Heading Style

/// Heading style options for Card header.
public enum LemonadeCardHeadingStyle {
    case `default`
    case overline

    var textStyle: LemonadeTextStyle {
        switch self {
        case .default: return LemonadeTypography.shared.headingXXSmall
        case .overline: return LemonadeTypography.shared.bodyXSmallOverline
        }
    }

    var textColor: Color {
        switch self {
        case .default: return LemonadeTheme.colors.content.contentPrimary
        case .overline: return LemonadeTheme.colors.content.contentSecondary
        }
    }

    /// An overline's line box is shorter than a default heading's, so a header titled with only
    /// an overline would sit tighter. The minimum evens the two out; the default heading is
    /// already taller and keeps wrapping its own text.
    var minTextBoxHeight: CGFloat? {
        switch self {
        case .default: return nil
        case .overline: return LemonadeTheme.sizes.size500
        }
    }
}

// MARK: - Card Header Config

/// Configuration for the Card header.
public struct CardHeaderConfig<LeadingContent: View, TrailingContent: View> {
    let title: String
    let subtitle: String?
    let headingStyle: LemonadeCardHeadingStyle
    let leadingSlot: (() -> LeadingContent)?
    let trailingSlot: (() -> TrailingContent)?
    let showNavigationIndicator: Bool

    public init(
        title: String,
        subtitle: String? = nil,
        headingStyle: LemonadeCardHeadingStyle = .default,
        leadingSlot: (() -> LeadingContent)? = nil,
        trailingSlot: (() -> TrailingContent)? = nil,
        showNavigationIndicator: Bool = false
    ) {
        self.title = title
        self.subtitle = subtitle
        self.headingStyle = headingStyle
        self.leadingSlot = leadingSlot
        self.trailingSlot = trailingSlot
        self.showNavigationIndicator = showNavigationIndicator
    }
}

extension CardHeaderConfig where LeadingContent == EmptyView, TrailingContent == EmptyView {
    public init(
        title: String,
        subtitle: String? = nil,
        headingStyle: LemonadeCardHeadingStyle = .default,
        showNavigationIndicator: Bool = false
    ) {
        self.title = title
        self.subtitle = subtitle
        self.headingStyle = headingStyle
        self.leadingSlot = nil
        self.trailingSlot = nil
        self.showNavigationIndicator = showNavigationIndicator
    }
}

extension CardHeaderConfig where LeadingContent == EmptyView {
    public init(
        title: String,
        subtitle: String? = nil,
        headingStyle: LemonadeCardHeadingStyle = .default,
        trailingSlot: (() -> TrailingContent)? = nil,
        showNavigationIndicator: Bool = false
    ) {
        self.title = title
        self.subtitle = subtitle
        self.headingStyle = headingStyle
        self.leadingSlot = nil
        self.trailingSlot = trailingSlot
        self.showNavigationIndicator = showNavigationIndicator
    }
}

extension CardHeaderConfig where TrailingContent == EmptyView {
    public init(
        title: String,
        subtitle: String? = nil,
        headingStyle: LemonadeCardHeadingStyle = .default,
        leadingSlot: (() -> LeadingContent)? = nil,
        showNavigationIndicator: Bool = false
    ) {
        self.title = title
        self.subtitle = subtitle
        self.headingStyle = headingStyle
        self.leadingSlot = leadingSlot
        self.trailingSlot = nil
        self.showNavigationIndicator = showNavigationIndicator
    }
}

// MARK: - Card Footer Action Config

/// Configuration for the Card footer action.
public struct CardFooterActionConfig {
    let label: String
    let onClick: () -> Void

    public init(label: String, onClick: @escaping () -> Void) {
        self.label = label
        self.onClick = onClick
    }
}

// MARK: - Card Component

public extension LemonadeUi {
    /// A card container component with optional header and configurable padding and background.
    ///
    /// - Parameters:
    ///   - contentPadding: LemonadeCardPadding for the content area. Defaults to .none
    ///   - background: LemonadeCardBackground style. Defaults to .default
    ///   - header: Optional CardHeaderConfig for the header
    ///   - content: Content to display inside the card
    /// - Returns: A styled Card view
    @ViewBuilder
    static func Card<Content: View, LeadingContent: View, TrailingContent: View>(
        contentPadding: LemonadeCardPadding = .none,
        background: LemonadeCardBackground = .default,
        header: CardHeaderConfig<LeadingContent, TrailingContent>? = nil,
        footerAction: CardFooterActionConfig? = nil,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeCardView(
            contentPadding: contentPadding,
            background: background,
            header: header,
            footerAction: footerAction,
            content: content
        )
    }

    /// A card container component without header.
    ///
    /// - Parameters:
    ///   - contentPadding: LemonadeCardPadding for the content area. Defaults to .none
    ///   - background: LemonadeCardBackground style. Defaults to .default
    ///   - footerAction: Optional CardFooterActionConfig for the footer action
    ///   - content: Content to display inside the card
    /// - Returns: A styled Card view
    @ViewBuilder
    static func Card<Content: View>(
        contentPadding: LemonadeCardPadding = .none,
        background: LemonadeCardBackground = .default,
        footerAction: CardFooterActionConfig? = nil,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeCardView<Content, EmptyView, EmptyView>(
            contentPadding: contentPadding,
            background: background,
            header: nil,
            footerAction: footerAction,
            content: content
        )
    }
}

// MARK: - Internal Card View

private struct LemonadeCardView<Content: View, LeadingContent: View, TrailingContent: View>: View {
    let contentPadding: LemonadeCardPadding
    let background: LemonadeCardBackground
    let header: CardHeaderConfig<LeadingContent, TrailingContent>?
    let footerAction: CardFooterActionConfig?
    let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if let header = header {
                LemonadeCardHeader(config: header)
            }

            VStack(alignment: .leading, spacing: 0) {
                content()
            }
            .padding(contentPadding.spacing)

            if let footerAction = footerAction {
                LemonadeCardFooterAction(config: footerAction)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(background.color)
        .clipShape(LemonadeTheme.shapes.semantic.radiusContainerDefault)
    }
}

private struct LemonadeCardHeader<LeadingContent: View, TrailingContent: View>: View {
    let config: CardHeaderConfig<LeadingContent, TrailingContent>

    var body: some View {
        HStack(alignment: .center, spacing: LemonadeTheme.spaces.spacing200) {
            if let leadingSlot = config.leadingSlot {
                leadingSlot()
            }

            VStack(alignment: .leading, spacing: 0) {
                LemonadeUi.Text(
                    config.title,
                    textStyle: config.headingStyle.textStyle,
                    color: config.headingStyle.textColor,
                    overflow: .tail,
                    maxLines: 1
                )
                if let subtitle = config.subtitle {
                    LemonadeUi.Text(
                        subtitle,
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary,
                        overflow: .tail,
                        maxLines: 1
                    )
                }
            }
            // The vertical half of the alignment is load-bearing: it is what centres an
            // overline inside the taller box its minimum height opens up.
            .frame(
                maxWidth: .infinity,
                minHeight: config.headingStyle.minTextBoxHeight,
                alignment: Alignment(horizontal: .leading, vertical: .center)
            )

            if let trailingSlot = config.trailingSlot {
                trailingSlot()
            }

            if config.showNavigationIndicator {
                LemonadeUi.Icon(
                    icon: .chevronRight,
                    contentDescription: nil,
                    size: .medium,
                    tint: LemonadeTheme.colors.content.contentSecondary
                )
            }
        }
        .padding(.horizontal, LemonadeTheme.spaces.spacing400)
        .padding(.top, LemonadeTheme.spaces.spacing300)
    }
}

private struct LemonadeCardFooterAction: View {
    let config: CardFooterActionConfig

    var body: some View {
        Button(action: config.onClick) {
            LemonadeUi.Text(
                config.label,
                textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                color: LemonadeTheme.colors.content.contentPrimary
            )
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.horizontal, LemonadeTheme.spaces.spacing400)
            .padding(.top, LemonadeTheme.spaces.spacing200)
            .padding(.bottom, LemonadeTheme.spaces.spacing400)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeCard_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            LemonadeUi.Card(contentPadding: .medium) {
                LemonadeUi.Text("This is card content")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(title: "Card Title")
            ) {
                LemonadeUi.Text("Content with header")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(
                    title: "Card with Tag",
                    trailingSlot: {
                        LemonadeUi.Tag(label: "New", voice: .positive)
                    }
                )
            ) {
                LemonadeUi.Text("Content with header and trailing tag")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(
                    title: "Card Title",
                    subtitle: "Subtitle",
                    trailingSlot: {
                        LemonadeUi.Tag(label: "Label", voice: .positive)
                    }
                )
            ) {
                LemonadeUi.Text("Content with subtitle")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(
                    title: "Overline Title",
                    subtitle: "Subtitle",
                    headingStyle: .overline,
                    trailingSlot: {
                        LemonadeUi.Tag(label: "Label", voice: .positive)
                    }
                )
            ) {
                LemonadeUi.Text("Content with overline heading and subtitle")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(
                    title: "Overline Title",
                    headingStyle: .overline
                )
            ) {
                LemonadeUi.Text("Content with overline heading")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                header: CardHeaderConfig(
                    title: "Navigable Card",
                    showNavigationIndicator: true
                )
            ) {
                LemonadeUi.Text("Card with navigation indicator")
            }

            LemonadeUi.Card(
                contentPadding: .medium,
                background: .elevated
            ) {
                LemonadeUi.Text("Elevated background card")
            }
        }
        .padding()
        .background(Color.gray.opacity(0.1))
        .previewLayout(.sizeThatFits)
    }
}
#endif
