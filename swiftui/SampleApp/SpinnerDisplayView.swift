import SwiftUI
import Lemonade

struct SpinnerDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                defaultSection
                sizesSection
                customTintSection
                loadingButtonsSection
            }
            .padding()
        }
        .navigationTitle("Spinner")
    }

    private var defaultSection: some View {
        sectionView(title: "Default") {
            LemonadeUi.Spinner()
        }
    }

    private var sizesSection: some View {
        sectionView(title: "Sizes") {
            HStack(alignment: .bottom, spacing: 12) {
                ForEach(sizes, id: \.label) { size in
                    VStack(spacing: 8) {
                        LemonadeUi.Spinner(size: size.value)
                        Text(size.label)
                            .font(.caption)
                    }
                }
            }
        }
    }

    private let sizes: [(label: String, value: LemonadeSpinnerSize)] = [
        ("XS", .xSmall),
        ("S", .small),
        ("M", .medium),
        ("L", .large),
        ("XL", .xLarge),
        ("2XL", .xxLarge),
        ("3XL", .xxxLarge),
        ("4XL", .xxxxLarge),
    ]

    private var customTintSection: some View {
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
    }

    private var loadingButtonsSection: some View {
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
