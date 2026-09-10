import Foundation
import XCTest
@testable import Lemonade

final class CalendarUtilsTests: XCTestCase {

    // These tests pin the invariant that keeps the header and the grid aligned: the label at
    // column c must name the weekday of the dates in column c.

    private let sundayFirst = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]

    private func gregorian(firstWeekday: Int) -> Calendar {
        var calendar = Calendar(identifier: .gregorian)
        calendar.firstWeekday = firstWeekday
        return calendar
    }

    // MARK: - orderedWeekdayAbbreviations

    func testSundayFirstCalendarKeepsLabelsUnchanged() {
        let labels = CalendarUtils.orderedWeekdayAbbreviations(sundayFirst, calendar: gregorian(firstWeekday: 1))

        XCTAssertEqual(labels, ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"])
    }

    func testMondayFirstCalendarRotatesLabelsToStartOnMonday() {
        let labels = CalendarUtils.orderedWeekdayAbbreviations(sundayFirst, calendar: gregorian(firstWeekday: 2))

        XCTAssertEqual(labels, ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"])
    }

    func testSaturdayFirstCalendarRotatesLabelsToStartOnSaturday() {
        let labels = CalendarUtils.orderedWeekdayAbbreviations(sundayFirst, calendar: gregorian(firstWeekday: 7))

        XCTAssertEqual(labels, ["Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"])
    }

    func testFewerThanSevenLabelsAreReturnedAsIs() {
        let labels = CalendarUtils.orderedWeekdayAbbreviations(["S", "M"], calendar: gregorian(firstWeekday: 2))

        XCTAssertEqual(labels, ["S", "M"])
    }

    // MARK: - Header/grid alignment

    func testJuly13th2026RendersUnderMondayOnAMondayFirstCalendar() {
        let calendar = gregorian(firstWeekday: 2)
        let labels = CalendarUtils.orderedWeekdayAbbreviations(sundayFirst, calendar: calendar)
        let days = CalendarUtils.generateMonthDays(year: 2026, month: 7, calendar: calendar)

        let july13 = days.firstIndex { date in
            calendar.component(.day, from: date) == 13 && calendar.component(.month, from: date) == 7
        }

        XCTAssertNotNil(july13)
        XCTAssertEqual(labels[july13! % 7], "Mon")
    }

    func testEveryColumnLabelMatchesItsDatesWeekdayForAllFirstWeekdays() {
        for firstWeekday in 1...7 {
            let calendar = gregorian(firstWeekday: firstWeekday)
            let labels = CalendarUtils.orderedWeekdayAbbreviations(sundayFirst, calendar: calendar)
            let days = CalendarUtils.generateMonthDays(year: 2026, month: 7, calendar: calendar)

            XCTAssertEqual(days.count, 42, "firstWeekday \(firstWeekday)")
            for (index, date) in days.enumerated() {
                let weekday = calendar.component(.weekday, from: date) // 1=Sun...7=Sat
                XCTAssertEqual(
                    labels[index % 7],
                    sundayFirst[weekday - 1],
                    "firstWeekday \(firstWeekday), cell \(index)"
                )
            }
        }
    }
}
