import SwiftUI
import Lemonade

struct ContentListItemDisplayView: View {
    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(spacing: .space.spacing400) {
                truncationSection
                horizontalSimpleSection
                horizontalSimpleLongTextSection
                horizontalLeadingTrailingSection
                horizontalContentSlotSection
                verticalSmallSection
                verticalSmallLeadingTrailingSection
                verticalLargeSection
                verticalLargeAllSlotsSection
                comfortableDensitySection
                compactDensitySection
                mixedListSection
            }
            .padding(.space.spacing400)
        }
        .background(.bg.bgSubtle)
        .navigationTitle("ContentListItem")
    }

    private var truncationSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Truncation")
        ) {
            LemonadeUi.ContentListItem(
                label: "Beneficiary account holder full legal name",
                value: "International Business Holdings Limited Partnership",
                showDivider: true,
                labelMaxLines: 1,
                labelOverflow: .tail,
                valueMaxLines: 1,
                valueOverflow: .tail
            )

            LemonadeUi.ContentListItem(
                label: "Registered business address for correspondence and billing",
                value: "Floor 12, One Canada Square, Canary Wharf, London E14 5AB, United Kingdom",
                layout: .vertical,
                showDivider: true,
                labelMaxLines: 1,
                labelOverflow: .tail,
                valueMaxLines: 2,
                valueOverflow: .tail
            )

            LemonadeUi.ContentListItem(
                label: "Registered business address for correspondence and billing",
                value: "Floor 12, One Canada Square, Canary Wharf, London E14 5AB, United Kingdom",
                layout: .vertical
            )
        }
    }

    private var horizontalSimpleSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Horizontal Simple")
        ) {
            LemonadeUi.ContentListItem(
                label: "Account holder",
                value: "John Doe",
                showDivider: true
            )

            LemonadeUi.ContentListItem(
                label: "Account number",
                value: "1234-5678"
            )
        }
    }

    private var horizontalSimpleLongTextSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Horizontal Simple — Long Text")
        ) {
            LemonadeUi.ContentListItem(
                label: "Terms and conditions agreement for the account holder regarding international transfers and currency exchange policies",
                value: "This value is intentionally very long to demonstrate how the horizontal simple layout handles multi-line text wrapping across several lines in a constrained width",
                showDivider: true
            )

            LemonadeUi.ContentListItem(
                label: "Short label",
                value: "A much longer value that should wrap onto multiple lines to test alignment behavior when only one side is long",
                showDivider: true,
                verticalAlignment: .top
            )

            LemonadeUi.ContentListItem(
                label: "Address",
                value: "Westminster, London SW1A 2HQ, United Kingdom",
                verticalAlignment: .top
            )
        }
    }

    private var horizontalLeadingTrailingSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Horizontal with Leading + Trailing")
        ) {
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
        }
    }

    private var horizontalContentSlotSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Horizontal with Content Slot")
        ) {
            LemonadeUi.ContentListItem(
                label: "Status",
                value: "Active",
                contentSlot: {
                    LemonadeUi.Tag(label: "Available", voice: .positive)
                }
            )
        }
    }

    private var verticalSmallSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Vertical Small")
        ) {
            LemonadeUi.ContentListItem(
                label: "Balance",
                value: "$1,234.56",
                layout: .vertical
            )
        }
    }

    private var verticalSmallLeadingTrailingSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Vertical Small with Leading + Trailing")
        ) {
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
                }
            )
        }
    }

    private var verticalLargeSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Vertical Large")
        ) {
            LemonadeUi.ContentListItem(
                label: "Total Balance",
                value: "$5,000.00",
                layout: .vertical,
                contentSlot: {
                    LemonadeUi.Tag(label: "Available", voice: .positive)
                }
            )
        }
    }

    private var verticalLargeAllSlotsSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Vertical Large with All Slots")
        ) {
            LemonadeUi.ContentListItem(
                label: "Total Balance",
                value: "$5,000.00",
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
        }
    }

    private var comfortableDensitySection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Density — Comfortable (default)")
        ) {
            LemonadeUi.ContentListItem(
                label: "Account holder",
                value: "John Doe",
                showDivider: true,
                density: .comfortable
            )

            LemonadeUi.ContentListItem(
                label: "Balance",
                value: "$1,234.56",
                layout: .vertical,
                density: .comfortable
            )
        }
    }

    private var compactDensitySection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Density — Compact")
        ) {
            LemonadeUi.ContentListItem(
                label: "Account holder",
                value: "John Doe",
                showDivider: true,
                density: .compact
            )

            LemonadeUi.ContentListItem(
                label: "Balance",
                value: "$1,234.56",
                layout: .vertical,
                density: .compact
            )
        }
    }

    private var mixedListSection: some View {
        LemonadeUi.Card(
            header: CardHeaderConfig(title: "Mixed List with Dividers")
        ) {
            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                showDivider: true
            )

            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                layout: .vertical,
                showDivider: true
            )

            LemonadeUi.ContentListItem(
                label: "Label",
                value: "Value",
                layout: .vertical,
                contentSlot: {
                    LemonadeUi.Tag(label: "Available", voice: .positive)
                }
            )
        }
    }
}

#Preview {
    NavigationStack {
        ContentListItemDisplayView()
    }
}
