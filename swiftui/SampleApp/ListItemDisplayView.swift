import Lemonade
import SwiftUI

private struct OutlinedOption: Identifiable {
    let id = UUID()
    let label: String
    let icon: LemonadeIcon
}

private struct TrailingPreset {
    let label: String
    let voice: TagVoice
}

private let trailingPresets: [TrailingPreset] = [
    TrailingPreset(label: "New", voice: .info),
    TrailingPreset(label: "Recommended", voice: .positive),
    TrailingPreset(label: "Popular", voice: .neutral),
]

struct ListItemPriorityPreview: View {
    // Both strings are long enough to compete for the row's width, which is what makes the
    // priority differences visible.
    private let label = "Beneficiary account holder"
    private let value = "International Holdings Ltd Partnership"

    @ViewBuilder
    private func valueText() -> some View {
        LemonadeUi.Text(
            value,
            textStyle: LemonadeTypography.shared.bodyMediumMedium,
            maxLines: 1
        )
    }

    @ViewBuilder
    private func addressText() -> some View {
        LemonadeUi.Text(
            "Rua de Olivenca, 55, esq 2, Algés, OX20 1PP",
            textAlign: .trailing,
            color: LemonadeTheme.colors.content.contentSecondary
        )
    }

    var body: some View {
        VStack(alignment: .leading, spacing: .space.spacing400) {
            LemonadeUi.Text("ListItem — Layout Priority", font: .headingXSmall)

            trailingPrioritySection
            labelPrioritySection
            bothPrioritySection
            labelFloorSection
            firstLineAlignmentSection
        }
    }

    private var trailingPrioritySection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "Priority: .trailing (default)",
                subtitle: "Trailing keeps its full width; the label truncates to fit."
            )
        ) {
            LemonadeUi.ListItem(
                label: "Delivery to",
                showDivider: true,
                trailingAlignment: .top,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: { addressText() }
            )
            LemonadeUi.ListItem(
                label: label,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: { valueText() }
            )
        }
    }

    private var labelPrioritySection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "Priority: .label",
                subtitle: "Label keeps its full width; the trailing content truncates to fit."
            )
        ) {
            LemonadeUi.ListItem(
                label: label,
                showDivider: true,
                priority: .label,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: { valueText() }
            )

            // A label long enough to fill the row truncates rather than starving the
            // trailing slot below its minimum readable width.
            LemonadeUi.ListItem(
                label: "Beneficiary account holder full legal registered name",
                priority: .label,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: { valueText() }
            )
        }
    }

    private var bothPrioritySection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "Priority: .both",
                subtitle: "Neither side wins — the width splits 50/50 and both truncate together."
            )
        ) {
            LemonadeUi.ListItem(
                label: label,
                priority: .both,
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: { valueText() }
            )
        }
    }

    private var labelFloorSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "Priority: .trailing — label floor",
                subtitle: "Even with a very long trailing value, the label keeps a readable minimum width instead of collapsing to just an ellipsis."
            )
        ) {
            LemonadeUi.ListItem(
                label: "Beneficiary",
                labelMaxLines: 1,
                leadingSlot: { EmptyView() },
                trailingSlot: {
                    LemonadeUi.Text(
                        "International Holdings Ltd Partnership and Subsidiaries",
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        maxLines: 1
                    )
                }
            )
        }
    }

    private var firstLineAlignmentSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "First-line alignment",
                subtitle: "trailingAlignment: .top keeps the label aligned with the trailing's first line when it wraps; .center is the default."
            )
        ) {
            LemonadeUi.ListItem(
                label: "Delivery to",
                showDivider: true,
                trailingAlignment: .top,
                priority: .label,
                leadingSlot: { EmptyView() },
                trailingSlot: { addressText() }
            )
            LemonadeUi.ListItem(
                label: "Delivery to",
                trailingAlignment: .center,
                priority: .label,
                leadingSlot: { EmptyView() },
                trailingSlot: { addressText() }
            )
        }
    }
}

struct ResourceListItemPreview: View {
    var body: some View {
        VStack(alignment: .leading, spacing: .space.spacing400) {
            LemonadeUi.Text("ResourceListItem", font: .headingXSmall)

            resourceListItemSection
            resourceListItemWithAddonSection
        }
    }

    private var resourceListItemSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "ResourceListItem")
        ) {
            LemonadeUi.ResourceListItem(
                label: "Credit •••• 8931",
                value: "$1,234.56",
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
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
        }
    }

    private var resourceListItemWithAddonSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ResourceListItem with Addon"
            )
        ) {
            LemonadeUi.ResourceListItem(
                label: "Last Transaction",
                value: "-$50.00",
                supportText: "Yesterday",
                showDivider: false,
                addonSlot: {
                    LemonadeUi.Tag(label: "Pending", voice: .warning)
                },
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .arrowUpRight,
                        contentDescription: nil,
                        voice: .critical,
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
        }
    }
}

struct ActionListItemPreview: View {
    var body: some View {
        VStack(alignment: .leading, spacing: .space.spacing400) {
            LemonadeUi.Text("ActionListItem", font: .headingXSmall)

            truncationSection
            actionListItemSection
            trailingAlignmentSection
            leadingAlignmentSection
            withTrailingSection
            figmaReferenceSection
            slotContentSection
            criticalVoiceSection
            disabledStatesSection
            loadingStateSection
        }
    }

    private var truncationSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "ActionListItem - Truncation")
        ) {
            LemonadeUi.ActionListItem(
                label: "This is a very long label that should be truncated to a single line with an ellipsis",
                supportText: "And this is an even longer support text that wraps across multiple lines "
                    + "before it gets truncated, so we can clearly see the maxLines and overflow props "
                    + "doing their job on the second line of the row",
                showNavigationIndicator: true,
                showDivider: true,
                labelMaxLines: 1,
                labelOverflow: .tail,
                supportTextMaxLines: 2,
                supportTextOverflow: .tail,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .bell,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Same long label without truncation wraps onto as many lines as it needs",
                supportText: "Default behaviour: no maxLines set, so this support text wraps freely "
                    + "instead of being clipped",
                showNavigationIndicator: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .bell,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
        }
    }

    private var actionListItemSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "ActionListItem")
        ) {
            LemonadeUi.ActionListItem(
                label: "Settings",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .gear,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Notifications",
                supportText: "Manage your notifications",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .bell,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Privacy",
                showNavigationIndicator: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .padlock,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
        }
    }

    private var trailingAlignmentSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem - Trailing Alignment"
            )
        ) {
            LemonadeUi.ActionListItem(
                label: "Top aligned",
                supportText: "trailingAlignment: .top\nSecond line",
                showNavigationIndicator: true,
                showDivider: true,
                trailingAlignment: .top,
                onItemClicked: {},
                trailingSlot: {
                    LemonadeUi.Tag(label: "Top", voice: .info)
                }
            )

            LemonadeUi.ActionListItem(
                label: "Center aligned",
                supportText: "trailingAlignment: .center\nSecond line",
                showNavigationIndicator: true,
                showDivider: true,
                trailingAlignment: .center,
                onItemClicked: {},
                trailingSlot: {
                    LemonadeUi.Tag(label: "Center", voice: .positive)
                }
            )

            LemonadeUi.ActionListItem(
                label: "Bottom aligned",
                supportText: "trailingAlignment: .bottom\nSecond line",
                showNavigationIndicator: true,
                showDivider: false,
                trailingAlignment: .bottom,
                onItemClicked: {},
                trailingSlot: {
                    LemonadeUi.Tag(label: "Bottom", voice: .warning)
                }
            )
        }
    }

    private var leadingAlignmentSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem - Leading Alignment"
            )
        ) {
            LemonadeUi.ActionListItem(
                label: "Top aligned (default)",
                supportText: "leadingAlignment: .top\nSecond line",
                showNavigationIndicator: true,
                showDivider: true,
                leadingAlignment: .top,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .gear,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Center aligned",
                supportText: "leadingAlignment: .center\nSecond line",
                showNavigationIndicator: true,
                showDivider: true,
                leadingAlignment: .center,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .bell,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Bottom aligned",
                supportText: "leadingAlignment: .bottom\nSecond line",
                showNavigationIndicator: true,
                showDivider: false,
                leadingAlignment: .bottom,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .padlock,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
        }
    }

    private var withTrailingSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem with Trailing"
            )
        ) {
            LemonadeUi.ActionListItem(
                label: "Updates Available",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .download,
                        contentDescription: nil,
                        size: .medium
                    )
                },
                trailingSlot: {
                    LemonadeUi.Badge(text: "3", size: .small)
                }
            )

            LemonadeUi.ActionListItem(
                label: "New Features",
                showNavigationIndicator: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .sparkles,
                        contentDescription: nil,
                        size: .medium
                    )
                },
                trailingSlot: {
                    LemonadeUi.Tag(label: "New", voice: .positive)
                }
            )
        }
    }

    private var figmaReferenceSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem - Figma reference"
            )
        ) {
            LemonadeUi.ActionListItem(
                label: "Account ***4236",
                supportText: "PT50 0002 0123 1234…\n1 store linked",
                showNavigationIndicator: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        icon: .bank,
                        contentDescription: nil,
                        voice: .neutral,
                        size: .medium,
                        shape: .rounded
                    )
                },
                trailingSlot: {
                    LemonadeUi.Tag(
                        label: "Settlements",
                        voice: .positive
                    )
                }
            )
        }
    }

    private var slotContentSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem - Slot Content"
            )
        ) {
            LemonadeUi.ActionListItem(
                label: "Account ***4236",
                supportText: "PT50 0002 0123 1234…",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .bank,
                        contentDescription: nil,
                        size: .medium
                    )
                },
                slotContent: {
                    LemonadeUi.Tag(
                        label: "Settlements",
                        voice: .positive
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Payout",
                supportText: "Arrives tomorrow",
                showNavigationIndicator: true,
                showDivider: true,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .coins,
                        contentDescription: nil,
                        size: .medium
                    )
                },
                slotContent: {
                    LemonadeUi.Text(
                        "Reference: PYT-00123 • EUR account",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Updates Available",
                supportText: "3 new features",
                showNavigationIndicator: true,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .download,
                        contentDescription: nil,
                        size: .medium
                    )
                },
                slotContent: {
                    LemonadeUi.Badge(text: "3", size: .small)
                }
            )
        }
    }

    private var criticalVoiceSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "ActionListItem - Critical Voice"
            )
        ) {
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
                        tint: .content.contentCritical
                    )
                }
            )

            LemonadeUi.ActionListItem(
                label: "Log Out",
                supportText: "You will need to sign in again",
                voice: .critical,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .logOut,
                        contentDescription: nil,
                        size: .medium,
                        tint: .content.contentCritical
                    )
                }
            )
        }
    }

    private var disabledStatesSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "Disabled States")
        ) {
            LemonadeUi.ActionListItem(
                label: "Disabled Action",
                enabled: false,
                showDivider: false,
                onItemClicked: {},
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .gear,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
        }
    }

    private var loadingStateSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "Loading State")
        ) {
            LemonadeUi.ActionListItem(
                label: "",
                isLoading: true,
                showDivider: true
            )

            LemonadeUi.ActionListItem(
                label: "",
                isLoading: true,
                showDivider: true
            )

            LemonadeUi.ActionListItem(
                label: "",
                isLoading: true,
                showDivider: false
            )
        }
    }
}

struct SelectListItemPreview: View {
    @State private var singleSelection = 0
    @State private var multipleSelections: Set<Int> = [0]
    @State private var toggleStates: [Bool] = [true, false, true]
    @State private var leadingSelection = 0
    @State private var emailDigest = true
    @State private var marketingEmails = false

    var body: some View {
        VStack(alignment: .leading, spacing: .space.spacing400) {
            LemonadeUi.Text("SelectListItem", font: .headingXSmall)

            singleSection
            multipleSection
            toggleSection
            withLeadingSection
            slotContentSection
            disabledStatesSection
        }
    }

    private var singleSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "SelectListItem - Single"
            )
        ) {
            ForEach(0..<3, id: \.self) { index in
                LemonadeUi.SelectListItem(
                    label: "Option \(index + 1)",
                    type: .single,
                    checked: singleSelection == index,
                    onItemClicked: { singleSelection = index },
                    showDivider: index < 2,
                    supportText: index == 0
                    ? "With support text" : nil
                )
            }
        }
    }

    private var multipleSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "SelectListItem - Multiple"
            )
        ) {
            ForEach(0..<3, id: \.self) { index in
                LemonadeUi.SelectListItem(
                    label: "Item \(index + 1)",
                    type: .multiple,
                    checked: multipleSelections.contains(index),
                    onItemClicked: {
                        if multipleSelections.contains(index) {
                            multipleSelections.remove(index)
                        } else {
                            multipleSelections.insert(index)
                        }
                    },
                    showDivider: index < 2
                )
            }
        }
    }

    private var toggleSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "SelectListItem - Toggle"
            )
        ) {
            ForEach(0..<3, id: \.self) { index in
                LemonadeUi.SelectListItem(
                    label: "Setting \(index + 1)",
                    type: .toggle,
                    checked: toggleStates[index],
                    onItemClicked: { toggleStates[index].toggle() },
                    showDivider: index < 2,
                    supportText: index == 0
                    ? "With support text" : nil
                )
            }
        }
    }

    private var withLeadingSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "SelectListItem with Leading"
            )
        ) {
            LemonadeUi.SelectListItem(
                label: "With Icon",
                type: .single,
                checked: leadingSelection == 0,
                onItemClicked: { leadingSelection = 0 },
                supportText: "Leading icon example",
                leadingSlot: {
                    LemonadeUi.Icon(
                        icon: .star,
                        contentDescription: nil,
                        size: .medium
                    )
                }
            )
            LemonadeUi.SelectListItem(
                label: "With Leading",
                type: .single,
                checked: leadingSelection == 1,
                onItemClicked: { leadingSelection = 1 },
                leadingSlot: {
                    LemonadeUi.SymbolContainer(
                        text: "LM",
                        size: .medium,
                        shape: .rounded
                    )
                }
            )
        }
    }

    private var slotContentSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(
                title: "SelectListItem - Slot Content"
            )
        ) {
            LemonadeUi.SelectListItem(
                label: "Email digest",
                type: .toggle,
                checked: emailDigest,
                onItemClicked: { emailDigest.toggle() },
                showDivider: true,
                supportText: "Weekly summary of your account",
                slotContent: {
                    LemonadeUi.Tag(label: "Recommended", voice: .positive)
                }
            )

            LemonadeUi.SelectListItem(
                label: "Marketing emails",
                type: .toggle,
                checked: marketingEmails,
                onItemClicked: { marketingEmails.toggle() },
                supportText: "Offers from partners",
                slotContent: {
                    LemonadeUi.Text(
                        "You can opt out at any time",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }
            )
        }
    }

    private var disabledStatesSection: some View {
        LemonadeUi.Card(
            contentPadding: .none,
            header: CardHeaderConfig(title: "Disabled States")
        ) {
            LemonadeUi.SelectListItem(
                label: "Disabled Option",
                type: .single,
                checked: false,
                onItemClicked: {},
                enabled: false
            )
        }
    }
}

struct OutlinedSelectListItemPreview: View {
    @State private var outlinedWithLeading = 0
    @State private var outlinedWithTrailing = 1
    @State private var outlinedLabelOnly = 0
    @State private var outlinedWithSupport = 0
    @State private var outlinedMultiple: Set<Int> = [0]
    @State private var outlinedSlotContent = 0

    private let outlinedOptions: [OutlinedOption] = [
        OutlinedOption(label: "Option A", icon: .heart),
        OutlinedOption(label: "Option B", icon: .star),
        OutlinedOption(label: "Option C", icon: .sparkles),
        OutlinedOption(label: "Option D", icon: .gift),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: .space.spacing400) {
            LemonadeUi.Text("SelectListItem", font: .headingXSmall)

            leadingIconOnlySection
            leadingAndTrailingTagSection
            labelOnlySection
            withSupportTextSection
            multipleSection
            slotContentSection
            disabledStatesSection
        }
    }

    private var leadingIconOnlySection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — Leading icon only"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(
                    Array(outlinedOptions.enumerated()),
                    id: \.element.id
                ) { index, option in
                    let isChecked = outlinedWithLeading == index
                    LemonadeUi.SelectListItem(
                        label: option.label,
                        type: .single,
                        checked: isChecked,
                        onItemClicked: { outlinedWithLeading = index },
                        variant: .outlined,
                        leadingSlot: {
                            LemonadeUi.SymbolContainer(
                                icon: option.icon,
                                contentDescription: nil,
                                voice: isChecked ? .positive : .neutral,
                                size: .medium,
                                shape: .rounded
                            )
                        }
                    )
                }
            }
        }
    }

    private var leadingAndTrailingTagSection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — Leading + trailing tag"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(
                    Array(outlinedOptions.prefix(3).enumerated()),
                    id: \.element.id
                ) { index, option in
                    let isChecked = outlinedWithTrailing == index
                    let preset = trailingPresets[index]
                    LemonadeUi.SelectListItem(
                        label: option.label,
                        type: .single,
                        checked: isChecked,
                        onItemClicked: { outlinedWithTrailing = index },
                        variant: .outlined,
                        leadingSlot: {
                            LemonadeUi.SymbolContainer(
                                icon: option.icon,
                                contentDescription: nil,
                                voice: isChecked ? .positive : .neutral,
                                size: .medium,
                                shape: .rounded
                            )
                        },
                        trailingSlot: {
                            LemonadeUi.Tag(
                                label: preset.label,
                                voice: preset.voice
                            )
                        }
                    )
                }
            }
        }
    }

    private var labelOnlySection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — Label only (no leading, no trailing)"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(0..<3, id: \.self) { index in
                    LemonadeUi.SelectListItem(
                        label: "Option \(index + 1)",
                        type: .single,
                        checked: outlinedLabelOnly == index,
                        onItemClicked: { outlinedLabelOnly = index },
                        variant: .outlined
                    )
                }
            }
        }
    }

    private var withSupportTextSection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — With support text"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(
                    Array(outlinedOptions.prefix(3).enumerated()),
                    id: \.element.id
                ) { index, option in
                    LemonadeUi.SelectListItem(
                        label: option.label,
                        type: .single,
                        checked: outlinedWithSupport == index,
                        onItemClicked: { outlinedWithSupport = index },
                        variant: .outlined,
                        supportText:
                            "Short description for \(option.label.lowercased())",
                        leadingSlot: {
                            LemonadeUi.SymbolContainer(
                                icon: option.icon,
                                contentDescription: nil,
                                voice: .neutral,
                                size: .medium,
                                shape: .rounded
                            )
                        }
                    )
                }
            }
        }
    }

    private var multipleSection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(title: "Outlined — Multiple")
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(
                    Array(outlinedOptions.enumerated()),
                    id: \.element.id
                ) { index, option in
                    let isChecked = outlinedMultiple.contains(index)
                    LemonadeUi.SelectListItem(
                        label: option.label,
                        type: .multiple,
                        checked: isChecked,
                        onItemClicked: {
                            if isChecked {
                                outlinedMultiple.remove(index)
                            } else {
                                outlinedMultiple.insert(index)
                            }
                        },
                        variant: .outlined,
                        supportText: index == 0 ? "Tap to toggle" : nil,
                        leadingSlot: {
                            LemonadeUi.SymbolContainer(
                                icon: option.icon,
                                contentDescription: nil,
                                voice: isChecked ? .positive : .neutral,
                                size: .medium,
                                shape: .rounded
                            )
                        }
                    )
                }
            }
        }
    }

    private var slotContentSection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — Slot Content"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                ForEach(
                    Array(outlinedOptions.prefix(3).enumerated()),
                    id: \.element.id
                ) { index, option in
                    let isChecked = outlinedSlotContent == index
                    let preset = trailingPresets[index]
                    LemonadeUi.SelectListItem(
                        label: option.label,
                        type: .single,
                        checked: isChecked,
                        onItemClicked: { outlinedSlotContent = index },
                        variant: .outlined,
                        supportText:
                            "Short description for \(option.label.lowercased())",
                        leadingSlot: {
                            LemonadeUi.SymbolContainer(
                                icon: option.icon,
                                contentDescription: nil,
                                voice: isChecked ? .positive : .neutral,
                                size: .medium,
                                shape: .rounded
                            )
                        },
                        slotContent: {
                            LemonadeUi.Tag(
                                label: preset.label,
                                voice: preset.voice
                            )
                        }
                    )
                }
            }
        }
    }

    private var disabledStatesSection: some View {
        LemonadeUi.Card(
            contentPadding: .xSmall,
            header: CardHeaderConfig(
                title: "Outlined — Disabled states"
            )
        ) {
            VStack(spacing: LemonadeTheme.spaces.spacing100) {
                LemonadeUi.SelectListItem(
                    label: "Disabled, no leading",
                    type: .single,
                    checked: false,
                    onItemClicked: {},
                    variant: .outlined,
                    enabled: false
                )

                LemonadeUi.SelectListItem(
                    label: "Disabled, with leading",
                    type: .single,
                    checked: false,
                    onItemClicked: {},
                    variant: .outlined,
                    enabled: false,
                    leadingSlot: {
                        LemonadeUi.SymbolContainer(
                            icon: .padlock,
                            contentDescription: nil,
                            voice: .neutral,
                            size: .medium,
                            shape: .rounded
                        )
                    }
                )

                LemonadeUi.SelectListItem(
                    label: "Disabled, with trailing tag",
                    type: .single,
                    checked: false,
                    onItemClicked: {},
                    variant: .outlined,
                    enabled: false,
                    leadingSlot: {
                        LemonadeUi.SymbolContainer(
                            icon: .bell,
                            contentDescription: nil,
                            voice: .neutral,
                            size: .medium,
                            shape: .rounded
                        )
                    },
                    trailingSlot: {
                        LemonadeUi.Tag(label: "Coming Soon")
                    }
                )
            }
        }
    }
}

struct ListItemDisplayView: View {
    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(spacing: .space.spacing800) {
                ListItemPriorityPreview()
                ResourceListItemPreview()
                SelectListItemPreview()
                OutlinedSelectListItemPreview()
                ActionListItemPreview()
            }
            .padding(.space.spacing400)
        }
        .background(.bg.bgSubtle)
        .navigationTitle("ListItem")
    }
}

#Preview {
    NavigationStack {
        ListItemDisplayView()
    }
}
