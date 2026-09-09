// url=<LEMONADE_COMPONENTS>?node-id=6501-5345
// source=kmp/calendar/src/commonMain/kotlin/com/teya/lemonade/DatePicker.kt
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
  example: figma.kotlin`LemonadeUi.DatePicker(${months ? `
    // NOTE: this design shows the months view, which the component does not implement` : ''}
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    monthFormatter = { month -> "" },
    weekdayAbbreviations = emptyList(),
    state = rememberDatePickerState(),
)`,
  imports: [
    'import com.teya.lemonade.DatePicker',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.rememberDatePickerState',
  ],
  id: 'date-picker',
  metadata: { nestable: false },
}
