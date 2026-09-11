// url=<LEMONADE_COMPONENTS>?node-id=6501-5513
// source=swiftui/Sources/Lemonade/Components/LemonadeDatePicker.swift
// component=DatePicker
import figma from 'figma'

// An internal Figma component is connected here so the picker above can tell a
// range design from a single-date one, which decides whether it is a DatePicker
// or a DateRangePicker.
const instance = figma.selectedInstance

const state = instance.getEnum('◇ State', {
  Default: 'default',
  Range: 'range',
  'Outside Days': 'default',
  Disabled: 'disabled',
})

export default {
  example: figma.swift``,
  id: 'date-picker-week-desktop',
  metadata: { nestable: true, props: { state: state } },
}
