import SwiftUI
import Lemonade

// MARK: - Catalog Model

/// Value-based route for every demo screen in the catalog.
///
/// Using a plain `Hashable` value as the navigation route (instead of eagerly
/// building an `AnyView` per entry) means a destination view is only constructed
/// when the user actually pushes it. The raw value doubles as the display title.
private enum Demo: String, CaseIterable, Identifiable, Hashable {
    case colors = "Semantic Colors"
    case themedColors = "Themed Colors"
    case spacing = "Spacing"
    case radius = "Radius"
    case shadows = "Shadows"
    case sizes = "Sizes"
    case opacity = "Opacity"
    case borderWidth = "Border Width"

    case icons = "Icons"
    case brandLogos = "Brand Logos"
    case countryFlags = "Country Flags"

    case text = "Text"
    case markdown = "Markdown"

    case button = "Button"
    case iconButton = "IconButton"
    case checkbox = "Checkbox"
    case radioButton = "RadioButton"
    case switchControl = "Switch"
    case datePicker = "DatePicker"
    case inlineCalendar = "InlineCalendar"

    case textField = "TextField"
    case searchField = "SearchField"
    case selectField = "SelectField"
    case pinCode = "PinCode"

    case tag = "Tag"
    case badge = "Badge"
    case symbolContainer = "SymbolContainer"
    case card = "Card"
    case divider = "Divider"
    case notice = "Notice"
    case tooltip = "Tooltip"
    case historyTimeline = "HistoryTimeline"

    case chip = "Chip"
    case listItem = "ListItem"
    case contentListItem = "ContentListItem"
    case swipeActionRow = "SwipeActionRow"
    case segmentedControl = "SegmentedControl"
    case boxSelection = "BoxSelection"

    case link = "Link"
    case tabs = "Tabs"
    case tile = "Tile"
    case topBar = "TopBar"

    case skeleton = "Skeleton"
    case spinner = "Spinner"
    case toast = "Toast"

    var id: Self { self }

    var title: String { rawValue }
}

/// A titled group of demo entries, identified by its title, which is unique across the catalog.
private struct DemoSection: Identifiable {
    var id: String { title }
    let title: String
    let items: [Demo]
}

/// Every demo screen, grouped into the sections the home list renders.
private let demoSections: [DemoSection] = [
    DemoSection(
        title: "Foundations",
        items: [.themedColors, .colors, .spacing, .radius, .shadows, .sizes, .opacity, .borderWidth]
    ),
    DemoSection(
        title: "Assets",
        items: [.icons, .brandLogos, .countryFlags]
    ),
    DemoSection(
        title: "Typography",
        items: [.text, .markdown]
    ),
    DemoSection(
        title: "Form Controls",
        items: [.button, .iconButton, .checkbox, .radioButton, .switchControl, .datePicker, .inlineCalendar]
    ),
    DemoSection(
        title: "Input Fields",
        items: [.textField, .searchField, .selectField, .pinCode]
    ),
    DemoSection(
        title: "Display Components",
        items: [.tag, .badge, .symbolContainer, .card, .divider, .notice, .tooltip, .historyTimeline]
    ),
    DemoSection(
        title: "Selection & Lists",
        items: [.chip, .listItem, .contentListItem, .swipeActionRow, .segmentedControl, .boxSelection]
    ),
    DemoSection(
        title: "Navigation",
        items: [.link, .tabs, .tile, .topBar]
    ),
    DemoSection(
        title: "Feedback",
        items: [.skeleton, .spinner, .toast]
    )
]

private func filteredSections(matching searchText: String) -> [DemoSection] {
    let text = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
    guard !text.isEmpty else { return demoSections }

    return demoSections.compactMap { $0.narrowed(to: text) }
}

private extension DemoSection {
    func narrowed(to text: String) -> DemoSection? {
        let sectionMatches = title.localizedCaseInsensitiveContains(text)
        let matchedItems = items.filter { item in
            sectionMatches || item.title.localizedCaseInsensitiveContains(text)
        }
        return matchedItems.isEmpty ? nil : DemoSection(title: title, items: matchedItems)
    }
}

// MARK: - Home

struct HomeView: View {
    @EnvironmentObject private var styleHandler: LemonadeStyleHandler
    @State private var searchText: String = ""
    @State private var showSettings: Bool = false

    var body: some View {
        List {
            ForEach(filteredSections(matching: searchText)) { section in
                Section(section.title) {
                    ForEach(section.items) { item in
                        NavigationLink(item.title, value: item)
                    }
                }
            }
        }
        .navigationDestination(for: Demo.self) { demo in
            destination(for: demo)
        }
        .navigationTitle("Lemonade DS")
        .searchable(text: $searchText, placement: .navigationBarDrawer(displayMode: .automatic))
        .textInputAutocapitalization(.never)
        .disableAutocorrection(true)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                LemonadeUi.ToolbarIconButton(
                    icon: .gear,
                    contentDescription: "Settings",
                    action: { showSettings = true }
                )
            }
        }
        .sheet(isPresented: $showSettings) {
            SettingsView()
                .environmentObject(styleHandler)
                // Without detents the sheet presents at full height and reads as a takeover
                // instead of a bottom sheet with the catalog still behind it.
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
        }
    }

    @ViewBuilder
    private func destination(for demo: Demo) -> some View {
        switch demo {
        case .colors: ColorsDisplayView()
        case .themedColors: ThemedColorsDisplayView()
        case .spacing: SpacingDisplayView()
        case .radius: RadiusDisplayView()
        case .shadows: ShadowsDisplayView()
        case .sizes: SizesDisplayView()
        case .opacity: OpacityDisplayView()
        case .borderWidth: BorderWidthDisplayView()
        case .icons: IconsDisplayView()
        case .brandLogos: BrandLogosDisplayView()
        case .countryFlags: FlagsDisplayView()
        case .text: TextDisplayView()
        case .markdown: MarkdownDisplayView()
        case .button: ButtonDisplayView()
        case .iconButton: IconButtonDisplayView()
        case .checkbox: CheckboxDisplayView()
        case .radioButton: RadioButtonDisplayView()
        case .switchControl: SwitchDisplayView()
        case .datePicker: DatePickerDisplayView()
        case .inlineCalendar: InlineCalendarDisplayView()
        case .textField: TextFieldDisplayView()
        case .searchField: SearchFieldDisplayView()
        case .selectField: SelectFieldDisplayView()
        case .pinCode: PinCodeDisplayView()
        case .tag: TagDisplayView()
        case .badge: BadgeDisplayView()
        case .symbolContainer: SymbolContainerDisplayView()
        case .card: CardDisplayView()
        case .divider: DividerDisplayView()
        case .notice: NoticeDisplayView()
        case .tooltip: TooltipDisplayView()
        case .historyTimeline: HistoryTimelineDisplayView()
        case .chip: ChipDisplayView()
        case .listItem: ListItemDisplayView()
        case .contentListItem: ContentListItemDisplayView()
        case .swipeActionRow: SwipeActionRowDisplayView()
        case .segmentedControl: SegmentedControlDisplayView()
        case .boxSelection: BoxSelectionDisplayView()
        case .link: LinkDisplayView()
        case .tabs: TabsDisplayView()
        case .tile: TileDisplayView()
        case .topBar: TopBarDisplayView()
        case .skeleton: SkeletonDisplayView()
        case .spinner: SpinnerDisplayView()
        case .toast: ToastDisplayView()
        }
    }
}

#Preview {
    NavigationStack {
        HomeView()
    }
    .environmentObject(LemonadeStyleHandler())
}
