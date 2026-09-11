// url=<LEMONADE_COMPONENTS>?node-id=9498-6701
// source=kmp/calendar/src/commonMain/kotlin/com/teya/lemonade/DatePicker.kt
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
    ? figma.kotlin`LemonadeUi.DateRangePicker(
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    monthFormatter = { month -> "" },
    weekdayAbbreviations = emptyList(),
    state = rememberDateRangePickerState(),
)`
    : figma.kotlin`LemonadeUi.DatePicker(
    // TODO: monthFormatter and weekdayAbbreviations are locale data the design does not carry
    monthFormatter = { month -> "" },
    weekdayAbbreviations = emptyList(),
    state = rememberDatePickerState(),
)`,
  imports: [
    'import com.teya.lemonade.DatePicker',
    'import com.teya.lemonade.LemonadeUi',
  ],
  id: 'date-picker',
  metadata: { nestable: false },
}
