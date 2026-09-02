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
