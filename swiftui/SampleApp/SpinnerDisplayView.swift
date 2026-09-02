import SwiftUI
import Lemonade

struct SpinnerDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                // Default
                sectionView(title: "Default") {
                    LemonadeUi.Spinner()
                }

                // Custom Tint
                sectionView(title: "Custom Tint") {
                    HStack(spacing: 24) {
                        VStack(spacing: 8) {
                            LemonadeUi.Spinner(
                                tint: LemonadeTheme.colors.content.contentBrand
                            )
                            Text("Brand")
                                .font(.caption)
                        }

                        VStack(spacing: 8) {
                            LemonadeUi.Spinner(
                                tint: LemonadeTheme.colors.content.contentCritical
                            )
                            Text("Critical")
                                .font(.caption)
                        }

                        VStack(spacing: 8) {
                            LemonadeUi.Spinner(
                                tint: LemonadeTheme.colors.content.contentPositive
                            )
                            Text("Positive")
                                .font(.caption)
                        }
                    }
                }

                // Loading Buttons - All Variants
                sectionView(title: "Loading Buttons") {
                    VStack(spacing: 12) {
                        LemonadeUi.Button(
                            label: "Primary",
                            onClick: { },
                            variant: .primary,
                            loading: true
                        )
                        .frame(maxWidth: .infinity)

                        LemonadeUi.Button(
                            label: "Secondary",
                            onClick: { },
                            variant: .secondary,
                            loading: true
                        )
                        .frame(maxWidth: .infinity)

                        LemonadeUi.Button(
                            label: "Neutral",
                            onClick: { },
                            variant: .neutral,
                            loading: true
                        )
                        .frame(maxWidth: .infinity)

                        LemonadeUi.Button(
                            label: "Critical",
                            onClick: { },
                            variant: .critical,
                            loading: true
                        )
                        .frame(maxWidth: .infinity)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Spinner")
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
        SpinnerDisplayView()
    }
}
