import SwiftUI
import Lemonade

// MARK: - TopBar Display View

struct TopBarDisplayView: View {

    var body: some View {
        List(TopBarDemo.allCases) { demo in
            NavigationLink(demo.title, value: demo)
        }
        .navigationTitle("TopBar")
        .navigationDestination(for: TopBarDemo.self) { demo in
            demo.destination
        }
    }
}

// MARK: - Demo Catalogue

/// One case per sub-demo. Value-based navigation keeps each demo unbuilt until it is
/// pushed, and the raw value doubles as a stable, content-derived identity.
private enum TopBarDemo: String, CaseIterable, Identifiable {
    case basic
    case basicClose
    case basicTrailingSlot
    case basicBottomSlot
    case basicSubheading
    case search
    case searchExpandedLabel
    case searchSubheading
    case compactLargePill
    case compactLarge
    case compactLargeSubheading
    case compactLargeSearch

    var id: String { rawValue }

    var title: String {
        switch self {
        case .basic: return "Basic (native back)"
        case .basicClose: return "Basic (close button)"
        case .basicTrailingSlot: return "Basic with Trailing Slot"
        case .basicBottomSlot: return "Basic with Bottom Slot"
        case .basicSubheading: return "Basic with Subheading"
        case .search: return "Search"
        case .searchExpandedLabel: return "Search with Expanded Label"
        case .searchSubheading: return "Search with Subheading"
        case .compactLargePill: return "Compact Large (pill)"
        case .compactLarge: return "Compact Large"
        case .compactLargeSubheading: return "Compact Large with Subheading"
        case .compactLargeSearch: return "Compact Large + Search"
        }
    }

    @ViewBuilder
    var destination: some View {
        switch self {
        case .basic: BasicTopBarDemo()
        case .basicClose: BasicCloseDemo()
        case .basicTrailingSlot: BasicTrailingSlotDemo()
        case .basicBottomSlot: BasicBottomSlotDemo()
        case .basicSubheading: BasicSubheadingDemo()
        case .search: SearchTopBarDemo()
        case .searchExpandedLabel: SearchExpandedLabelDemo()
        case .searchSubheading: SearchSubheadingDemo()
        case .compactLargePill: CompactLargePillDemo()
        case .compactLarge: CompactLargeDemo()
        case .compactLargeSubheading: CompactLargeSubheadingDemo()
        case .compactLargeSearch: CompactLargeSearchDemo()
        }
    }
}

// MARK: - Sample Content

private struct SampleListContent: View {
    var itemCount: Int = 30

    var body: some View {
        LazyVStack(spacing: 0) {
            ForEach(0..<itemCount, id: \.self) { index in
                HStack {
                    LemonadeUi.SymbolContainer(
                        icon: .store,
                        contentDescription: nil,
                        voice: .neutral,
                        size: .medium
                    )

                    VStack(alignment: .leading, spacing: 2) {
                        SwiftUI.Text("Item \(index + 1)")
                            .font(.body)
                        SwiftUI.Text("Scroll to see collapse behavior")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }

                    Spacer()

                    LemonadeUi.Icon(
                        icon: .chevronRight,
                        contentDescription: nil,
                        size: .small,
                        tint: LemonadeTheme.colors.content.contentTertiary
                    )
                }
                .padding(.horizontal, LemonadeSpacing.spacing400.value)
                .padding(.vertical, LemonadeSpacing.spacing300.value)

                LemonadeUi.HorizontalDivider()
            }
        }
    }
}

// MARK: - 1. Basic TopBar Demos

private struct BasicTopBarDemo: View {
    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(
            label: "Settings",
            navigationAction: NavigationAction(action: .back, onAction: {})
        )
    }
}

private struct BasicCloseDemo: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(
            label: "Edit Profile",
            navigationAction: NavigationAction(action: .close, onAction: { dismiss() })
        )
    }
}

private struct BasicTrailingSlotDemo: View {
    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(
            label: "Notifications",
            navigationAction: NavigationAction(action: .back, onAction: {})
        ) {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .bell, contentDescription: "Notifications")
                }
            }
            #if compiler(>=6.2)
            if #available(iOS 26, *) {
                ToolbarSpacer(.fixed, placement: .navigationBarTrailing)
            }
            #endif
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .ellipsisHorizontal, contentDescription: "More")
                }
            }
        }
    }
}

private struct BasicBottomSlotDemo: View {
    @State private var selectedTab = 0

    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(
            label: "Browse",
            navigationAction: NavigationAction(action: .back, onAction: {}),
            bottomSlot: {
                LemonadeUi.SegmentedControl(
                    properties: [.label("All"), .label("Recent"), .label("Favorites")],
                    selectedTab: selectedTab,
                    onTabSelected: { selectedTab = $0 }
                )
                .padding(.horizontal, LemonadeSpacing.spacing400.value)
                .padding(.bottom, LemonadeSpacing.spacing200.value)
            },
            toolbar: {
                ToolbarItem(placement: .navigationBarTrailing) {
                    EmptyView()
                }
            }
        )
    }
}

private struct BasicSubheadingDemo: View {
    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(
            label: "Account",
            subheading: "Signed in as john@example.com",
            navigationAction: NavigationAction(action: .back, onAction: {})
        ) {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .bell, contentDescription: "Notifications")
                }
            }
        }
    }
}

// MARK: - 2. Search TopBar Demos

private struct SearchTopBarDemo: View {
    @State private var searchQuery = ""

    private let allItems = (1...30).map { "Item \($0)" }

    private var filteredItems: [String] {
        guard !searchQuery.isEmpty else { return allItems }
        return allItems.filter { $0.localizedCaseInsensitiveContains(searchQuery) }
    }

    var body: some View {
        List {
            ForEach(filteredItems, id: \.self) { item in
                SwiftUI.Text(item)
            }
        }
        .lemonadeTopBar(
            label: "Search",
            searchInput: $searchQuery,
            navigationAction: NavigationAction(action: .back, onAction: {})
        )
    }
}

private struct SearchExpandedLabelDemo: View {
    @State private var searchQuery = ""

    private let products = [
        "iPhone 15 Pro", "MacBook Air M3", "iPad Pro", "Apple Watch Ultra",
        "AirPods Pro", "Mac Studio", "Apple TV 4K", "HomePod mini",
        "Vision Pro", "Mac Mini", "iMac 24\"", "MacBook Pro 16\"",
    ]

    private var filteredProducts: [String] {
        guard !searchQuery.isEmpty else { return products }
        return products.filter { $0.localizedCaseInsensitiveContains(searchQuery) }
    }

    var body: some View {
        List {
            ForEach(filteredProducts, id: \.self) { product in
                HStack {
                    SwiftUI.Text(product)
                    Spacer()
                    LemonadeUi.Icon(
                        icon: .chevronRight,
                        contentDescription: nil,
                        size: .small,
                        tint: LemonadeTheme.colors.content.contentTertiary
                    )
                }
            }
        }
        .lemonadeTopBar(
            label: "Products",
            searchInput: $searchQuery,
            searchPrompt: "Search products...",
            expandedLabel: "Discover",
            navigationAction: NavigationAction(action: .back, onAction: {})
        ) {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .filter, contentDescription: "Filter")
                }
            }
        }
    }
}

private struct SearchSubheadingDemo: View {
    @State private var searchQuery = ""

    private let contacts = [
        "Alice Anderson", "Bob Brown", "Carol Chen", "David Davis",
        "Eve Edwards", "Frank Foster", "Grace Green", "Henry Harris",
    ]

    private var filteredContacts: [String] {
        guard !searchQuery.isEmpty else { return contacts }
        return contacts.filter { $0.localizedCaseInsensitiveContains(searchQuery) }
    }

    var body: some View {
        List {
            ForEach(filteredContacts, id: \.self) { contact in
                SwiftUI.Text(contact)
            }
        }
        .lemonadeTopBar(
            label: "Contacts",
            subheading: "\(contacts.count) people",
            searchInput: $searchQuery,
            searchPrompt: "Search contacts...",
            navigationAction: NavigationAction(action: .back, onAction: {})
        )
    }
}

// MARK: - 3. Compact Large Demos

/// Helper: groups buttons in a pill (capsule) with glass effect.
private struct GlassPill<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        HStack(spacing: 4) {
            content()
        }
        .modifier(GlassCapsuleModifier())
    }
}

private struct GlassCapsuleModifier: ViewModifier {
    func body(content: Content) -> some View {
        #if compiler(>=6.2)
        if #available(iOS 26, *) {
            content.glassEffect(.regular.interactive(), in: .capsule)
        } else {
            content
                .padding(.horizontal, 4)
                .background(.ultraThinMaterial, in: Capsule())
        }
        #else
        content
            .padding(.horizontal, 4)
            .background(.ultraThinMaterial, in: Capsule())
        #endif
    }
}

/// The compact-large top bar is built for top-level screens: it hides the native back
/// button and fills the leading slot with its own title. Pushed onto a NavigationStack —
/// which is how the sample reaches these demos — that leaves no way back, so every
/// compact-large demo adds an explicit close button. It goes in the trailing group
/// because the leading slot is already occupied by the compact-large title.
private struct CompactLargeCloseButton: ToolbarContent {
    let dismiss: DismissAction

    var body: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button(action: { dismiss() }) {
                LemonadeUi.Icon(icon: .times, contentDescription: "Close")
                    .frame(
                        minWidth: LemonadeSizes.size800.value,
                        minHeight: LemonadeSizes.size800.value
                    )
                    .contentShape(Rectangle())
            }
        }
    }
}

private struct CompactLargePillDemo: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(label: "Home", subheading: "Pill trailing") {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .bell, contentDescription: "Notifications")
                }
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .gear, contentDescription: "Settings")
                }
            }
            CompactLargeCloseButton(dismiss: dismiss)
        }
    }
}

private struct CompactLargeDemo: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(label: "Home", subheading: nil) {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .bell, contentDescription: "Notifications")
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .gear, contentDescription: "Settings")
                }
            }
            CompactLargeCloseButton(dismiss: dismiss)
        }
    }
}

private struct CompactLargeSubheadingDemo: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            SampleListContent()
        }
        .lemonadeTopBar(label: "Home", subheading: "Welcome back, John") {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .bell, contentDescription: "Notifications")
                }
            }
            CompactLargeCloseButton(dismiss: dismiss)
        }
    }
}

// MARK: - 4. Compact Large + Search Demo

private struct CompactLargeSearchDemo: View {
    @Environment(\.dismiss) private var dismiss
    @State private var searchQuery = ""

    private let categories = [
        "Electronics", "Clothing", "Books", "Home & Garden",
        "Sports", "Toys", "Beauty", "Automotive",
        "Food & Beverages", "Health", "Music", "Movies",
        "Pets", "Office", "Travel", "Jewelry",
        "Baby", "Garden Tools", "Fitness", "Photography",
        "Gaming", "Art Supplies", "Kitchen", "Furniture",
    ]

    private var filteredCategories: [String] {
        guard !searchQuery.isEmpty else { return categories }
        return categories.filter { $0.localizedCaseInsensitiveContains(searchQuery) }
    }

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(filteredCategories, id: \.self) { category in
                    HStack {
                        SwiftUI.Text(category)
                        Spacer()
                        LemonadeUi.Icon(
                            icon: .chevronRight,
                            contentDescription: nil,
                            size: .small,
                            tint: LemonadeTheme.colors.content.contentTertiary
                        )
                    }
                    .padding(.horizontal, LemonadeSpacing.spacing400.value)
                    .padding(.vertical, LemonadeSpacing.spacing300.value)

                    LemonadeUi.HorizontalDivider()
                }
            }
        }
        .lemonadeTopBar(
            label: "Discover",
            subheading: "Find what you need",
            searchInput: $searchQuery,
            searchPrompt: "Search categories..."
        ) {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: {}) {
                    LemonadeUi.Icon(icon: .ellipsisHorizontal, contentDescription: "More")
                }
            }
            CompactLargeCloseButton(dismiss: dismiss)
        }
    }
}

// MARK: - Preview

#Preview {
    NavigationStack {
        TopBarDisplayView()
    }
}
