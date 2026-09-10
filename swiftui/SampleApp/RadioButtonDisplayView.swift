import SwiftUI
import Lemonade

struct RadioButtonDisplayView: View {
    @State private var selectedOption = 0
    @State private var selectedShipping = 0
    @State private var selectedPlan = 0

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                statesSection
                interactiveGroupSection
                withLabelSection
                withSupportTextSection
                disabledSection
            }
            .padding()
        }
        .navigationTitle("RadioButton")
    }

    private var statesSection: some View {
        sectionView(title: "States") {
            HStack(spacing: 24) {
                VStack(spacing: 8) {
                    LemonadeUi.RadioButton(
                        checked: false,
                        onRadioButtonClicked: {}
                    )
                    Text("Unchecked")
                        .font(.caption)
                }

                VStack(spacing: 8) {
                    LemonadeUi.RadioButton(
                        checked: true,
                        onRadioButtonClicked: {}
                    )
                    Text("Checked")
                        .font(.caption)
                }
            }
            // A radio button cannot be unchecked by tapping it, so hit testing stays off
            // and both states keep their own swatch.
            .allowsHitTesting(false)
        }
    }

    private var interactiveGroupSection: some View {
        sectionView(title: "Interactive Group") {
            VStack(alignment: .leading, spacing: 16) {
                ForEach(0..<3) { index in
                    HStack(spacing: 12) {
                        LemonadeUi.RadioButton(
                            checked: selectedOption == index,
                            onRadioButtonClicked: { selectedOption = index }
                        )
                        Text("Option \(index + 1)")
                    }
                }
            }
        }
    }

    private var withLabelSection: some View {
        sectionView(title: "With Label") {
            VStack(alignment: .leading, spacing: 16) {
                LemonadeUi.RadioButton(
                    checked: selectedShipping == 0,
                    onRadioButtonClicked: { selectedShipping = 0 },
                    label: "Free shipping"
                )

                LemonadeUi.RadioButton(
                    checked: selectedShipping == 1,
                    onRadioButtonClicked: { selectedShipping = 1 },
                    label: "Express delivery"
                )

                LemonadeUi.RadioButton(
                    checked: selectedShipping == 2,
                    onRadioButtonClicked: { selectedShipping = 2 },
                    label: "Same day delivery"
                )
            }
        }
    }

    private var withSupportTextSection: some View {
        sectionView(title: "With Support Text") {
            VStack(alignment: .leading, spacing: 16) {
                LemonadeUi.RadioButton(
                    checked: selectedPlan == 0,
                    onRadioButtonClicked: { selectedPlan = 0 },
                    label: "Standard Plan",
                    supportText: "$9.99/month - Basic features"
                )

                LemonadeUi.RadioButton(
                    checked: selectedPlan == 1,
                    onRadioButtonClicked: { selectedPlan = 1 },
                    label: "Premium Plan",
                    supportText: "$19.99/month - All features included"
                )
            }
        }
    }

    private var disabledSection: some View {
        sectionView(title: "Disabled") {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 24) {
                    LemonadeUi.RadioButton(
                        checked: false,
                        onRadioButtonClicked: {},
                        enabled: false
                    )
                    Text("Disabled unchecked")
                        .foregroundStyle(.content.contentSecondary)
                }

                HStack(spacing: 24) {
                    LemonadeUi.RadioButton(
                        checked: true,
                        onRadioButtonClicked: {},
                        enabled: false
                    )
                    Text("Disabled checked")
                        .foregroundStyle(.content.contentSecondary)
                }

                LemonadeUi.RadioButton(
                    checked: true,
                    onRadioButtonClicked: {},
                    label: "Disabled with label",
                    enabled: false
                )
            }
        }
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)

            content()
        }
    }
}

#Preview {
    NavigationStack {
        RadioButtonDisplayView()
    }
}
