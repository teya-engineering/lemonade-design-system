import SwiftUI
import Lemonade

struct TabsDisplayView: View {
    @State private var hugSelectedIndex = 0
    @State private var stretchSelectedIndex = 0
    @State private var manyTabsSelectedIndex = 2
    @State private var iconTabsSelectedIndex = 0
    @State private var disabledTabsSelectedIndex = 0
    @State private var rightEdgeSelectedIndex = 5
    @State private var noDividerSelectedIndex = 0

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing800) {
                sectionView(title: "Hug (default)") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Overview"),
                            LemonadeTabItem(label: "Details"),
                            LemonadeTabItem(label: "Reviews")
                        ],
                        selectedIndex: hugSelectedIndex,
                        onTabSelected: { hugSelectedIndex = $0 }
                    )
                }

                sectionView(title: "Stretch") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Tab A"),
                            LemonadeTabItem(label: "Tab B"),
                            LemonadeTabItem(label: "Tab C")
                        ],
                        selectedIndex: stretchSelectedIndex,
                        onTabSelected: { stretchSelectedIndex = $0 },
                        itemsSize: .stretch
                    )
                }

                sectionView(title: "Many Tabs (scrollable)") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Dashboard"),
                            LemonadeTabItem(label: "Analytics"),
                            LemonadeTabItem(label: "Reports"),
                            LemonadeTabItem(label: "Settings"),
                            LemonadeTabItem(label: "Users"),
                            LemonadeTabItem(label: "Activity")
                        ],
                        selectedIndex: manyTabsSelectedIndex,
                        onTabSelected: { manyTabsSelectedIndex = $0 }
                    )
                }

                sectionView(title: "Right-edge selected (initial)") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "November"),
                            LemonadeTabItem(label: "December"),
                            LemonadeTabItem(label: "January"),
                            LemonadeTabItem(label: "February"),
                            LemonadeTabItem(label: "March"),
                            LemonadeTabItem(label: "April")
                        ],
                        selectedIndex: rightEdgeSelectedIndex,
                        onTabSelected: { rightEdgeSelectedIndex = $0 }
                    )
                }

                sectionView(title: "With Icons") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Home", icon: .home),
                            LemonadeTabItem(label: "Search", icon: .search),
                            LemonadeTabItem(label: "Profile", icon: .user)
                        ],
                        selectedIndex: iconTabsSelectedIndex,
                        onTabSelected: { iconTabsSelectedIndex = $0 }
                    )
                }

                sectionView(title: "With Disabled Tab") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Active"),
                            LemonadeTabItem(label: "Disabled", isDisabled: true),
                            LemonadeTabItem(label: "Also Active")
                        ],
                        selectedIndex: disabledTabsSelectedIndex,
                        onTabSelected: { disabledTabsSelectedIndex = $0 }
                    )
                }

                sectionView(title: "Without Divider") {
                    LemonadeUi.Tabs(
                        tabs: [
                            LemonadeTabItem(label: "Overview"),
                            LemonadeTabItem(label: "Details"),
                            LemonadeTabItem(label: "Reviews")
                        ],
                        selectedIndex: noDividerSelectedIndex,
                        onTabSelected: { noDividerSelectedIndex = $0 },
                        showDivider: false
                    )
                }
            }
            .padding(.vertical)
        }
        .navigationTitle("Tabs")
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)
                .padding(.horizontal)

            content()
        }
    }
}

#Preview {
    NavigationStack {
        TabsDisplayView()
    }
}
