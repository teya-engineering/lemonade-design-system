import SwiftUI
import Lemonade

struct LinkDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Default") {
                    LemonadeUi.Link(
                        text: "Learn more",
                        onClick: { }
                    )
                }

                sectionView(title: "With Icon") {
                    VStack(alignment: .leading, spacing: 16) {
                        LemonadeUi.Link(
                            text: "External link",
                            onClick: { },
                            icon: .externalLink
                        )

                        LemonadeUi.Link(
                            text: "Go to settings",
                            onClick: { },
                            icon: .chevronRight
                        )
                    }
                }

                sectionView(title: "Sizes") {
                    VStack(alignment: .leading, spacing: 16) {
                        LemonadeUi.Link(
                            text: "Large link",
                            onClick: { },
                            icon: .externalLink,
                            size: .large
                        )

                        LemonadeUi.Link(
                            text: "Medium link",
                            onClick: { },
                            icon: .externalLink,
                            size: .medium
                        )

                        LemonadeUi.Link(
                            text: "Small link",
                            onClick: { },
                            icon: .externalLink,
                            size: .small
                        )
                    }
                }

                sectionView(title: "In Context") {
                    VStack(alignment: .leading, spacing: 4) {
                        LemonadeUi.Text(
                            "Need help with your account?",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: .content.contentSecondary
                        )

                        LemonadeUi.Link(
                            text: "Contact us",
                            onClick: { },
                            size: .small
                        )
                    }
                }

                sectionView(title: "Disabled") {
                    VStack(alignment: .leading, spacing: 16) {
                        LemonadeUi.Link(
                            text: "Disabled link",
                            onClick: { },
                            enabled: false
                        )

                        LemonadeUi.Link(
                            text: "Disabled with icon",
                            onClick: { },
                            enabled: false,
                            icon: .externalLink
                        )
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Link")
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
        LinkDisplayView()
    }
}
