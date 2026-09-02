import SwiftUI
import Lemonade

/// Shared medium-style date formatter.
///
/// Defined at file scope so it is built exactly once for the process -
/// `DateFormatter` initialization is expensive due to ObjC bridging and locale
/// loading, and this view reads it ~9 times per body pass. Matches the precedent
/// in `Sources/Lemonade/Components/Calendar/CalendarDayCell.swift`.
private let mediumDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateStyle = .medium
    return formatter
}()

struct InlineCalendarDisplayView: View {
    @StateObject private var defaultState = LemonadeInlineCalendarState(initialDate: Date())
    @StateObject private var trailingDotsState = LemonadeInlineCalendarState(initialDate: Date())
    @StateObject private var shortLabelsState = LemonadeInlineCalendarState(initialDate: Date())
    @StateObject private var constrainedState: LemonadeInlineCalendarState
    @StateObject private var compactSelectionState = LemonadeInlineCalendarState(initialDate: Date())
    @StateObject private var compactDotsState = LemonadeInlineCalendarState(initialDate: Date())
    @StateObject private var customColorsState = LemonadeInlineCalendarState(initialDate: Date())

    private let constrainedMinDate: Date
    private let constrainedMaxDate: Date

    init() {
        let today = Calendar.current.startOfDay(for: Date())
        let minDate = Calendar.current.date(byAdding: .day, value: -7, to: today)!
        let maxDate = Calendar.current.date(byAdding: .day, value: 30, to: today)!
        self.constrainedMinDate = minDate
        self.constrainedMaxDate = maxDate
        _constrainedState = StateObject(
            wrappedValue: LemonadeInlineCalendarState(
                initialDate: today,
                minDate: minDate,
                maxDate: maxDate
            )
        )
    }

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Default (today selected)") {
                    LemonadeUi.InlineCalendar(state: defaultState)
                    selectedDateLabel(for: defaultState)
                }

                sectionView(title: "With trailing content (dot on every 3rd day)") {
                    LemonadeUi.InlineCalendar(
                        state: trailingDotsState,
                        enabledDates: { isEveryThirdDay($0) }
                    ) { date, isSelected in
                        if isEveryThirdDay(date) {
                            eventDot(isSelected: isSelected)
                        }
                    }
                    selectedDateLabel(for: trailingDotsState)
                }

                sectionView(title: "Short day labels (Mon, Tue, Wed...)") {
                    LemonadeUi.InlineCalendar(
                        state: shortLabelsState,
                        dayLabelFormat: .short
                    )
                    selectedDateLabel(for: shortLabelsState)
                }

                sectionView(title: "Constrained range (7 days before to 30 days after)") {
                    LemonadeUi.InlineCalendar(state: constrainedState)

                    LemonadeUi.Text(
                        "Range: \(mediumDateFormatter.string(from: constrainedMinDate)) - \(mediumDateFormatter.string(from: constrainedMaxDate))",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )

                    selectedDateLabel(for: constrainedState)
                }

                sectionView(title: "Compact selection (day number only)") {
                    LemonadeUi.InlineCalendar(
                        state: compactSelectionState,
                        expandSelectionToLabel: false
                    )
                    selectedDateLabel(for: compactSelectionState)
                }

                sectionView(title: "Compact selection with trailing dots") {
                    LemonadeUi.InlineCalendar(
                        state: compactDotsState,
                        expandSelectionToLabel: false,
                        enabledDates: { isEveryThirdDay($0) }
                    ) { date, isSelected in
                        if isEveryThirdDay(date) {
                            eventDot(isSelected: isSelected)
                        }
                    }
                    selectedDateLabel(for: compactDotsState)
                }

                sectionView(title: "Custom selection colors") {
                    LemonadeUi.InlineCalendar(
                        state: customColorsState,
                        selectionBackgroundColor: LemonadeTheme.colors.interaction.bgInfoInteractive,
                        selectionContentColor: LemonadeTheme.colors.content.contentAlwaysLight
                    )
                    selectedDateLabel(for: customColorsState)
                }
            }
            .padding()
        }
        .navigationTitle("InlineCalendar")
    }

    private func isEveryThirdDay(_ date: Date) -> Bool {
        Calendar.current.component(.day, from: date) % 3 == 0
    }

    @ViewBuilder
    private func eventDot(isSelected: Bool) -> some View {
        Circle()
            .fill(isSelected
                ? LemonadeTheme.colors.content.contentOnBrandHigh
                : LemonadeTheme.colors.content.contentBrand)
            .frame(width: 6, height: 6)
    }

    @ViewBuilder
    private func selectedDateLabel(for state: LemonadeInlineCalendarState) -> some View {
        LemonadeUi.Text(
            "Selected: \(state.selectedDate.map { mediumDateFormatter.string(from: $0) } ?? "none")",
            textStyle: LemonadeTypography.shared.bodySmallRegular,
            color: LemonadeTheme.colors.content.contentSecondary
        )
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
        InlineCalendarDisplayView()
    }
}
