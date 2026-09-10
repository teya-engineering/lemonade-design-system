import SwiftUI
import Lemonade

struct CheckboxDisplayView: View {
    @State private var isChecked1 = false
    @State private var isChecked2 = true
    @State private var isIndeterminate = true
    @State private var labeledChecked = false
    @State private var rememberMe = true
    @State private var selectAllStatus: CheckboxStatus = .indeterminate

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                statesSection
                interactiveSection
                withLabelSection
                disabledSection
            }
            .padding()
        }
        .navigationTitle("Checkbox")
    }

    private var statesSection: some View {
        sectionView(title: "States") {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 24) {
                    VStack(spacing: 8) {
                        LemonadeUi.Checkbox(
                            status: .unchecked,
                            onCheckboxClicked: {}
                        )
                        Text("Unchecked")
                            .font(.caption)
                    }

                    VStack(spacing: 8) {
                        LemonadeUi.Checkbox(
                            status: .checked,
                            onCheckboxClicked: {}
                        )
                        Text("Checked")
                            .font(.caption)
                    }

                    VStack(spacing: 8) {
                        LemonadeUi.Checkbox(
                            status: .indeterminate,
                            onCheckboxClicked: {}
                        )
                        Text("Indeterminate")
                            .font(.caption)
                    }
                }
                // Hit testing stays off so every status keeps its own swatch instead of
                // collapsing into whichever one was tapped last.
                .allowsHitTesting(false)
            }
        }
    }

    private var interactiveSection: some View {
        sectionView(title: "Interactive") {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 24) {
                    LemonadeUi.Checkbox(
                        status: isChecked1 ? .checked : .unchecked,
                        onCheckboxClicked: { isChecked1.toggle() }
                    )
                    Text("Tap to toggle")
                }

                HStack(spacing: 24) {
                    LemonadeUi.Checkbox(
                        status: isChecked2 ? .checked : .unchecked,
                        onCheckboxClicked: { isChecked2.toggle() }
                    )
                    Text("Initially checked")
                }
            }
        }
    }

    private var withLabelSection: some View {
        sectionView(title: "With Label") {
            VStack(alignment: .leading, spacing: 16) {
                LemonadeUi.Checkbox(
                    status: labeledChecked ? .checked : .unchecked,
                    onCheckboxClicked: { labeledChecked.toggle() },
                    label: "Accept terms and conditions"
                )

                LemonadeUi.Checkbox(
                    status: rememberMe ? .checked : .unchecked,
                    onCheckboxClicked: { rememberMe.toggle() },
                    label: "Remember me"
                )

                // Cycling rather than toggling keeps the indeterminate status reachable.
                LemonadeUi.Checkbox(
                    status: selectAllStatus,
                    onCheckboxClicked: { selectAllStatus = nextStatus(after: selectAllStatus) },
                    label: "Select all items"
                )
            }
        }
    }

    private var disabledSection: some View {
        sectionView(title: "Disabled") {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 24) {
                    LemonadeUi.Checkbox(
                        status: .unchecked,
                        onCheckboxClicked: {},
                        enabled: false
                    )
                    Text("Disabled unchecked")
                        .foregroundStyle(.content.contentSecondary)
                }

                HStack(spacing: 24) {
                    LemonadeUi.Checkbox(
                        status: .checked,
                        onCheckboxClicked: {},
                        enabled: false
                    )
                    Text("Disabled checked")
                        .foregroundStyle(.content.contentSecondary)
                }

                LemonadeUi.Checkbox(
                    status: .checked,
                    onCheckboxClicked: {},
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

    private func nextStatus(after status: CheckboxStatus) -> CheckboxStatus {
        switch status {
        case .indeterminate: return .checked
        case .checked: return .unchecked
        case .unchecked: return .indeterminate
        @unknown default: return .unchecked
        }
    }
}

#Preview {
    NavigationStack {
        CheckboxDisplayView()
    }
}
