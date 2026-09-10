import SwiftUI
import Lemonade

private struct PlanOption: Identifiable {
    let name: String
    let price: String
    let description: String
    let icon: LemonadeIcon
    var tag: String? = nil

    var id: String { name }
}

private let frequencyOptions = ["Weekly", "Monthly", "Yearly"]

private let plans = [
    PlanOption(
        name: "Starter",
        price: "Free",
        description: "Take card payments with a Teya reader and get paid the next working day.",
        icon: .card
    ),
    PlanOption(
        name: "Business",
        price: "£12.99 / month",
        description: "Everything in Starter, plus invoicing, expense tracking and same-day payouts.",
        icon: .chart,
        tag: "Most popular"
    ),
    PlanOption(
        name: "Enterprise",
        price: "Custom pricing",
        description: "Multi-site reporting, custom payout schedules and a dedicated account manager.",
        icon: .handCoins
    )
]

struct BoxSelectionDisplayView: View {
    @State private var isFilledSelected = true
    @State private var isOutlinedSelected = true
    @State private var selectedOption = frequencyOptions[0]
    @State private var selectedPlan = plans[1].name

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing600) {
                variantsSection
                backgroundSection
                selectedSection
                disabledSection
                contentPaddingSection
                radiusSection
                planCardsSection
                singleSelectionSection
            }
            .padding(LemonadeTheme.spaces.spacing400)
        }
        .background(.bg.bgSubtle)
        .navigationTitle("BoxSelection")
    }

    private var variantsSection: some View {
        sectionView(title: "Variants") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(variant: .filled, stretched: true) {
                    sampleContent(label: "Filled")
                }
                LemonadeUi.BoxSelection(variant: .outlined, stretched: true) {
                    sampleContent(label: "Outlined")
                }
            }
        }
    }

    private var backgroundSection: some View {
        sectionView(title: "Background") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(background: .default, stretched: true) {
                    sampleContent(label: "Default")
                }
                LemonadeUi.BoxSelection(background: .elevated, stretched: true) {
                    sampleContent(label: "Elevated")
                }
            }
        }
    }

    private var selectedSection: some View {
        sectionView(title: "Selected") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(
                    variant: .filled,
                    isSelected: isFilledSelected,
                    stretched: true,
                    onClick: { isFilledSelected.toggle() }
                ) {
                    sampleContent(label: "Filled")
                }
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    isSelected: isOutlinedSelected,
                    stretched: true,
                    onClick: { isOutlinedSelected.toggle() }
                ) {
                    sampleContent(label: "Outlined")
                }
            }
        }
    }

    private var disabledSection: some View {
        sectionView(title: "Disabled") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(
                    variant: .filled,
                    enabled: false,
                    stretched: true,
                    onClick: {}
                ) {
                    sampleContent(label: "Filled")
                }
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    enabled: false,
                    stretched: true,
                    onClick: {}
                ) {
                    sampleContent(label: "Outlined")
                }
            }
        }
    }

    private var contentPaddingSection: some View {
        sectionView(title: "Content Padding") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    contentPadding: .spacing100,
                    stretched: true
                ) {
                    sampleContent(label: "Spacing100")
                }
                LemonadeUi.BoxSelection(variant: .outlined, stretched: true) {
                    sampleContent(label: "Spacing300")
                }
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    contentPadding: .spacing600,
                    stretched: true
                ) {
                    sampleContent(label: "Spacing600")
                }
            }
        }
    }

    private var radiusSection: some View {
        sectionView(title: "Radius") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    radius: .radius0,
                    stretched: true
                ) {
                    sampleContent(label: "Radius0")
                }
                LemonadeUi.BoxSelection(variant: .outlined, stretched: true) {
                    sampleContent(label: "Radius500")
                }
                LemonadeUi.BoxSelection(
                    variant: .outlined,
                    radius: .radius800,
                    stretched: true
                ) {
                    sampleContent(label: "Radius800")
                }
            }
        }
    }

    private var planCardsSection: some View {
        sectionView(title: "Use Case: Plan Cards") {
            VStack(spacing: LemonadeTheme.spaces.spacing300) {
                ForEach(plans) { plan in
                    let isPlanSelected = selectedPlan == plan.name
                    let selectPlan = { selectedPlan = plan.name }

                    LemonadeUi.BoxSelection(
                        isSelected: isPlanSelected,
                        contentPadding: .spacing400,
                        radius: .radius600,
                        stretched: true,
                        onClick: selectPlan
                    ) {
                        planCardContent(
                            plan: plan,
                            isSelected: isPlanSelected,
                            onSelect: selectPlan
                        )
                    }
                }
            }
        }
    }

    private var singleSelectionSection: some View {
        sectionView(title: "Use Case: Single Selection") {
            HStack(spacing: LemonadeTheme.spaces.spacing300) {
                ForEach(frequencyOptions, id: \.self) { option in
                    LemonadeUi.BoxSelection(
                        variant: .outlined,
                        isSelected: selectedOption == option,
                        stretched: true,
                        onClick: { selectedOption = option }
                    ) {
                        LemonadeUi.Text(
                            option,
                            textStyle: LemonadeTypography.shared.bodySmallMedium
                        )
                        .frame(maxWidth: .infinity)
                    }
                }
            }
        }
    }

    private func planCardContent(
        plan: PlanOption,
        isSelected: Bool,
        onSelect: @escaping () -> Void
    ) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            HStack(alignment: .center, spacing: LemonadeTheme.spaces.spacing300) {
                LemonadeUi.SymbolContainer(
                    icon: plan.icon,
                    contentDescription: nil,
                    size: .large,
                    shape: .rounded
                )

                VStack(alignment: .leading, spacing: 0) {
                    LemonadeUi.Text(
                        plan.name,
                        textStyle: LemonadeTypography.shared.headingXSmall
                    )
                    LemonadeUi.Text(
                        plan.price,
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                LemonadeUi.RadioButton(
                    checked: isSelected,
                    onRadioButtonClicked: onSelect
                )
            }

            LemonadeUi.HorizontalDivider()

            LemonadeUi.Text(
                plan.description,
                textStyle: LemonadeTypography.shared.bodySmallRegular,
                color: .content.contentSecondary
            )
            .frame(maxWidth: .infinity, alignment: .leading)

            if let tag = plan.tag {
                LemonadeUi.Tag(label: tag, voice: .positive)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private func sampleContent(label: String) -> some View {
        VStack(spacing: LemonadeTheme.spaces.spacing200) {
            LemonadeUi.Icon(
                icon: .heart,
                contentDescription: nil,
                size: .medium
            )

            LemonadeUi.Text(
                label,
                textStyle: LemonadeTypography.shared.bodySmallMedium
            )
        }
        .frame(maxWidth: .infinity)
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            LemonadeUi.Text(
                title,
                textStyle: LemonadeTypography.shared.headingXSmall,
                color: .content.contentSecondary
            )

            content()
        }
    }
}

#Preview {
    NavigationStack {
        BoxSelectionDisplayView()
    }
}
