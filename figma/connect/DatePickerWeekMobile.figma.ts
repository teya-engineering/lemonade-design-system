// url=<LEMONADE_COMPONENTS>?node-id=9498-6556
// source=kmp/calendar/src/commonMain/kotlin/com/teya/lemonade/DatePicker.kt
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
  example: figma.kotlin``,
  id: 'date-picker-week-mobile',
  metadata: { nestable: true, props: { state: state } },
}
