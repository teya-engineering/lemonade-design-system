import SwiftUI
import Lemonade

struct SelectFieldDisplayView: View {
    // A SelectField's whole job is to open a picker, so every enabled field here opens a
    // real one and writes back to its own value. They previously all had `onClick: {}`,
    // which left the screen looking interactive while nothing on it could change.
    private enum Picker: String, Identifiable {
        case basic, category, language, collection, required, country

        var id: String { rawValue }

        var title: String {
            switch self {
            case .basic: return "Select an option"
            case .category: return "Select a category"
            case .language: return "Select a language"
            case .collection: return "Select a collection"
            case .required: return "Select an option"
            case .country: return "Select a country"
            }
        }

        var options: [String] {
            switch self {
            case .basic, .required: return ["ABC", "DEF", "GHI"]
            case .category: return ["Groceries", "Transport", "Utilities"]
            case .language: return ["English", "Português", "Íslenska"]
            case .collection: return ["Favourites", "Archive", "Shared with me"]
            case .country: return ["United Kingdom", "Portugal", "Iceland"]
            }
        }
    }

    @State private var activePicker: Picker?
    @State private var basicValue: String? = "ABC"
    @State private var categoryValue: String?
    @State private var languageValue: String? = "English"
    @State private var collectionValue: String? = "Favourites"
    @State private var requiredValue: String?
    @State private var countryValue: String?

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing600) {
                // Basic
                sectionView(title: "Basic") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .basic },
                        selectedValue: basicValue,
                        placeholderText: "Select an option"
                    )
                }

                // With Label
                sectionView(title: "With Label") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .category },
                        selectedValue: categoryValue,
                        placeholderText: "Select a category",
                        label: "Category"
                    )
                }

                // Filled
                sectionView(title: "Filled") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .language },
                        selectedValue: languageValue,
                        label: "Language"
                    )
                }

                // With Leading Icon
                sectionView(title: "With Leading Icon") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .collection },
                        selectedValue: collectionValue,
                        label: "Collection"
                    ) {
                        LemonadeUi.Icon(
                            icon: .heart,
                            contentDescription: nil,
                            tint: LemonadeTheme.colors.content.contentSecondary
                        )
                    }
                }

                // With Error — stays in the error state until something is picked, which is
                // exactly what the error treatment is meant to show.
                sectionView(title: "With Error") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .required },
                        selectedValue: requiredValue,
                        placeholderText: "Select an option",
                        label: "Required Field",
                        errorMessage: "Please select an option",
                        error: requiredValue == nil
                    )
                }

                // With Support Text
                sectionView(title: "With Support Text") {
                    LemonadeUi.SelectField(
                        onClick: { activePicker = .country },
                        selectedValue: countryValue,
                        placeholderText: "Select a country",
                        label: "Country",
                        optionalIndicator: "Optional",
                        supportText: "Choose your country of residence"
                    )
                }

                // Disabled
                sectionView(title: "Disabled") {
                    LemonadeUi.SelectField(
                        onClick: {},
                        selectedValue: "Locked value",
                        label: "Disabled Field",
                        enabled: false
                    )
                }
            }
            .padding(LemonadeTheme.spaces.spacing400)
        }
        .navigationTitle("SelectField")
        .confirmationDialog(
            activePicker?.title ?? "",
            isPresented: Binding(
                get: { activePicker != nil },
                set: { if !$0 { activePicker = nil } }
            ),
            titleVisibility: .visible
        ) {
            if let picker = activePicker {
                ForEach(picker.options, id: \.self) { option in
                    SwiftUI.Button(option) { apply(option, to: picker) }
                }
            }
            SwiftUI.Button("Cancel", role: .cancel) {}
        }
    }

    private func apply(_ option: String, to picker: Picker) {
        switch picker {
        case .basic: basicValue = option
        case .category: categoryValue = option
        case .language: languageValue = option
        case .collection: collectionValue = option
        case .required: requiredValue = option
        case .country: countryValue = option
        }
    }

    private func sectionView<Content: View>(
        title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            SwiftUI.Text(title)
                .font(.headline)
                .foregroundStyle(LemonadeTheme.colors.content.contentSecondary)

            content()
        }
    }
}

#Preview {
    NavigationStack {
        SelectFieldDisplayView()
    }
}
