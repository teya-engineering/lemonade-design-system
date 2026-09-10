import SwiftUI
import Lemonade

struct SearchFieldDisplayView: View {
    @State private var searchText1 = ""
    @State private var searchText2 = "Sample search"
    @State private var searchText3 = ""
    @State private var searchText4 = ""
    @State private var searchText5 = ""
    @State private var productSearch = ""

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                basicSection
                withContentSection
                withCallbacksSection
                cancelCallbackSection
                notDismissibleSection
                disabledSection
                usageExampleSection
            }
            .padding()
        }
        .navigationTitle("SearchField")
    }

    private var basicSection: some View {
        sectionView(title: "Basic") {
            LemonadeUi.SearchField(
                input: $searchText1,
                placeholder: "Search..."
            )
        }
    }

    private var withContentSection: some View {
        sectionView(title: "With Content") {
            LemonadeUi.SearchField(
                input: $searchText2,
                placeholder: "Search..."
            )
        }
    }

    private var withCallbacksSection: some View {
        sectionView(title: "With Callbacks") {
            VStack(alignment: .leading, spacing: 8) {
                LemonadeUi.SearchField(
                    input: $searchText3,
                    onInputChanged: { newValue in
                        print("Search changed: \(newValue)")
                    },
                    placeholder: "Type to search...",
                    onInputClear: {
                        print("Search cleared")
                    }
                )

                if !searchText3.isEmpty {
                    Text("Searching for: \(searchText3)")
                        .font(.caption)
                        .foregroundStyle(.content.contentSecondary)
                }
            }
        }
    }

    private var cancelCallbackSection: some View {
        sectionView(title: "Cancel Callback") {
            VStack(alignment: .leading, spacing: 8) {
                LemonadeUi.SearchField(
                    input: $searchText4,
                    placeholder: "Search and cancel...",
                    onCancel: { print("Search dismissed") },
                    cancelContentDescription: "Cancel search"
                )

                Text("Cancelling drops the focus and hides the keyboard; the input stays as typed. onCancel then runs for whatever the query was driving.")
                    .font(.caption)
                    .foregroundStyle(.content.contentSecondary)
            }
        }
    }

    private var notDismissibleSection: some View {
        sectionView(title: "Not Dismissible") {
            LemonadeUi.SearchField(
                input: $searchText5,
                placeholder: "No cancel button...",
                dismissible: false
            )
        }
    }

    private var disabledSection: some View {
        sectionView(title: "Disabled") {
            LemonadeUi.SearchField(
                input: .constant(""),
                placeholder: "Search disabled...",
                enabled: false
            )
        }
    }

    private var usageExampleSection: some View {
        sectionView(title: "Usage Example") {
            VStack(spacing: 16) {
                LemonadeUi.SearchField(
                    input: $productSearch,
                    placeholder: "Search products..."
                )

                if productSearch.isEmpty {
                    VStack(spacing: 8) {
                        ForEach(["iPhone 15", "MacBook Pro", "iPad Air", "Apple Watch"], id: \.self) { item in
                            HStack {
                                Text(item)
                                Spacer()
                                LemonadeUi.Icon(
                                    icon: .chevronRight,
                                    contentDescription: nil,
                                    size: .small,
                                    tint: .content.contentTertiary
                                )
                            }
                            .padding(.vertical, 8)
                        }
                    }
                } else {
                    let filtered = ["iPhone 15", "MacBook Pro", "iPad Air", "Apple Watch"].filter {
                        $0.localizedCaseInsensitiveContains(productSearch)
                    }

                    if filtered.isEmpty {
                        Text("No results found")
                            .foregroundStyle(.content.contentSecondary)
                            .padding()
                    } else {
                        ForEach(filtered, id: \.self) { item in
                            HStack {
                                Text(item)
                                Spacer()
                                LemonadeUi.Icon(
                                    icon: .chevronRight,
                                    contentDescription: nil,
                                    size: .small,
                                    tint: .content.contentTertiary
                                )
                            }
                            .padding(.vertical, 8)
                        }
                    }
                }
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
        SearchFieldDisplayView()
    }
}
