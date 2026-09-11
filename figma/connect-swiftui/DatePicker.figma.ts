// url=<LEMONADE_COMPONENTS>?node-id=9498-6701
// source=swiftui/Sources/Lemonade/Components/LemonadeDatePicker.swift
// component=DatePicker
import figma from 'figma'

const instance = figma.selectedInstance

// The weeks say whether the design selects a range; the picker itself does not.
const isRange = [1, 2, 3, 4, 5, 6].some((n) => {
  const week = instance.findInstance(`Week ${n}`)
  if (!week || week.type !== 'INSTANCE') return false
  return week.executeTemplate().metadata?.props?.state === 'range'
})

export default {
  example: isRange
    ? figma.swift`LemonadeUi.DateRangePicker(
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    state: $dateRangePickerState,
    monthFormatter: { _ in "" },
    weekdayAbbreviations: []
)`
    : figma.swift`LemonadeUi.DatePicker(
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    state: $datePickerState,
    monthFormatter: { _ in "" },
    weekdayAbbreviations: []
)`,
  id: 'date-picker',
  metadata: { nestable: false },
}
