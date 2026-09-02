import SwiftUI
import Lemonade

struct SegmentedControlDisplayView: View {
    @State private var selectedTab1 = 0
    @State private var selectedTab2 = 1
    @State private var selectedTab3 = 0
    @State private var selectedTabMedium = 0
    @State private var selectedTabSmall = 0
    @State private var selectedTabIconOnly = 0
    @State private var selectedTabHugContent = 0
    @State private var selectedTabHugInHStack = 0

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                // Hug content with .fixedSize() — for inline use alongside other content
                sectionView(title: "Hug Content (.fixedSize)") {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Standalone .fixedSize():")
                            .font(.caption)
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .label("Week"),
                                .label("Day"),
                            ],
                            selectedTab: selectedTabHugContent,
                            size: .small,
                            onTabSelected: { selectedTabHugContent = $0 }
                        )
                        .fixedSize()
                        Text("In HStack with Spacer:")
                            .font(.caption)
                        HStack {
                            Text("Total spent")
                                .font(.title2)
                                .bold()
                            Spacer()
                            // Its own state: the two demos are separate instances, so moving
                            // one must not move the other.
                            LemonadeUi.SegmentedControl(
                                properties: [
                                    .label("Week"),
                                    .label("Day"),
                                ],
                                selectedTab: selectedTabHugInHStack,
                                size: .small,
                                onTabSelected: { selectedTabHugInHStack = $0 }
                            )
                            .fixedSize()
                        }
                    }
                }

                // Large (default)
                sectionView(title: "Large (Default)") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .label("Tab 1"),
                                .label("Tab 2"),
                                .label("Tab 3"),
                            ],
                            selectedTab: selectedTab1,
                            onTabSelected: { selectedTab1 = $0 }
                        )

                        Text("Selected: Tab \(selectedTab1 + 1)")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                // Medium
                sectionView(title: "Medium") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .label("Tab 1"),
                                .label("Tab 2"),
                                .label("Tab 3"),
                            ],
                            selectedTab: selectedTabMedium,
                            size: .medium,
                            onTabSelected: { selectedTabMedium = $0 }
                        )

                        Text("Selected: Tab \(selectedTabMedium + 1)")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                // Small
                sectionView(title: "Small") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .label("Tab 1"),
                                .label("Tab 2"),
                                .label("Tab 3"),
                            ],
                            selectedTab: selectedTabSmall,
                            size: .small,
                            onTabSelected: { selectedTabSmall = $0 }
                        )

                        Text("Selected: Tab \(selectedTabSmall + 1)")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                // With Icons
                sectionView(title: "With Icons") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .labelAndIcon("Favorites", icon: .heart),
                                .labelAndIcon("Bookmarks", icon: .star),
                                .labelAndIcon("Settings", icon: .gear),
                            ],
                            selectedTab: selectedTab2,
                            onTabSelected: { selectedTab2 = $0 }
                        )

                        Text("Selected: \(["Favorites", "Bookmarks", "Settings"][selectedTab2])")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                // Icon Only (Small)
                sectionView(title: "Icon Only (Small)") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .icon(.list),
                                .icon(.stackThree),
                            ],
                            selectedTab: selectedTabIconOnly,
                            size: .small,
                            onTabSelected: { selectedTabIconOnly = $0 }
                        )
                        .fixedSize(horizontal: true, vertical: false)
                    }
                }

                // Toggle
                sectionView(title: "Toggle Style") {
                    VStack(spacing: 16) {
                        LemonadeUi.SegmentedControl(
                            properties: [
                                .label("On"),
                                .label("Off"),
                            ],
                            selectedTab: selectedTab3,
                            onTabSelected: { selectedTab3 = $0 }
                        )

                        Text("Status: \(selectedTab3 == 0 ? "On" : "Off")")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("SegmentedControl")
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
        SegmentedControlDisplayView()
    }
}
