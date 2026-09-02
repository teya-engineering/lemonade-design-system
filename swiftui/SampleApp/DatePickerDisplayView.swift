import SwiftUI
import Lemonade

// MARK: - Shared Formatters

// Defined at file scope so they are built exactly once for the process -
// `DateFormatter` initialization is expensive due to ObjC bridging and locale
// loading, and these were previously re-created on every access. Matches the
// precedent in `Sources/Lemonade/Components/Calendar/CalendarDayCell.swift`.

private let mediumDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateStyle = .medium
    return formatter
}()

private let monthSymbols: [String] = DateFormatter().monthSymbols

private let demoMonthFormatter: (Int) -> String = { month in
    monthSymbols[month - 1]
}

private let demoWeekdays = ["S", "M", "T", "W", "T", "F", "S"]

/// The min/max bounds for the constrained demo picker, resolved once per process.
private let constrainedDateBounds: (min: Date, max: Date) = {
    let calendar = Calendar.current
    let today = calendar.startOfDay(for: Date())
    return (
        min: calendar.date(byAdding: .day, value: -7, to: today) ?? today,
        max: calendar.date(byAdding: .day, value: 30, to: today) ?? today
    )
}()

struct DatePickerDisplayView: View {
    @State private var singleState = LemonadeDatePickerState()
    @State private var rangeState = LemonadeDateRangePickerState()
    @State private var constrainedState = LemonadeDatePickerState(
        minDate: constrainedDateBounds.min,
        maxDate: constrainedDateBounds.max
    )
    @State private var maxRangeState = LemonadeDateRangePickerState(maxRangeDays: 7)

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Single Date Picker") {
                    VStack(alignment: .leading, spacing: 12) {
                        LemonadeUi.DatePicker(
                            state: $singleState,
                            monthFormatter: demoMonthFormatter,
                            weekdayAbbreviations: demoWeekdays
                        )

                        if let date = singleState.selectedDate {
                            LemonadeUi.Text(
                                "Selected: \(mediumDateFormatter.string(from: date))",
                                textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                                color: LemonadeTheme.colors.content.contentSecondary
                            )
                        }
                    }
                }

                sectionView(title: "Date Range Picker") {
                    VStack(alignment: .leading, spacing: 12) {
                        LemonadeUi.DateRangePicker(
                            state: $rangeState,
                            monthFormatter: demoMonthFormatter,
                            weekdayAbbreviations: demoWeekdays
                        )

                        if let start = rangeState.selectedStartDate, let end = rangeState.selectedEndDate {
                            LemonadeUi.Text(
                                "Range: \(mediumDateFormatter.string(from: start)) - \(mediumDateFormatter.string(from: end))",
                                textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                                color: LemonadeTheme.colors.content.contentSecondary
                            )
                        }
                    }
                }

                sectionView(title: "With Min/Max Constraints") {
                    VStack(alignment: .leading, spacing: 12) {
                        LemonadeUi.DatePicker(
                            state: $constrainedState,
                            monthFormatter: demoMonthFormatter,
                            weekdayAbbreviations: demoWeekdays
                        )

                        LemonadeUi.Text(
                            "Range: past 7 days to next 30 days",
                            textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                            color: LemonadeTheme.colors.content.contentSecondary
                        )

                        if let date = constrainedState.selectedDate {
                            LemonadeUi.Text(
                                "Selected: \(mediumDateFormatter.string(from: date))",
                                textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                                color: LemonadeTheme.colors.content.contentSecondary
                            )
                        }
                    }
                }

                sectionView(title: "Range with Max Days (7)") {
                    LemonadeUi.DateRangePicker(
                        state: $maxRangeState,
                        monthFormatter: demoMonthFormatter,
                        weekdayAbbreviations: demoWeekdays
                    )
                }
            }
            .padding()
        }
        .navigationTitle("DatePicker")
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
        DatePickerDisplayView()
    }
}
