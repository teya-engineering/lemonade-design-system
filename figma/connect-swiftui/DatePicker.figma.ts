// url=<LEMONADE_COMPONENTS>?node-id=6501-5345
// source=swiftui/Sources/Lemonade/Components/LemonadeDatePicker.swift
// component=DatePicker
import figma from 'figma'

// The Figma component is a representative rendering: its only two properties are
// Device and View Type, and neither has a code counterpart. So this template
// carries no design intent — it exists to name the API, so that a calendar in a
// design is recognised as LemonadeUi.DatePicker rather than rebuilt from layers.
const instance = figma.selectedInstance

// The code renders days only; there is no months view to switch to.
const months = instance.getEnum('◇ View Type', { Months: true, Days: false })

export default {
  example: figma.swift`LemonadeUi.DatePicker(${months ? `
    // NOTE: this design shows the months view, which the component does not implement` : ''}
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    state: $datePickerState,
    monthFormatter: { _ in "" },
    weekdayAbbreviations: []
)`,
  id: 'date-picker',
  metadata: { nestable: false },
}
