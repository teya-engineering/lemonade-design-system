// url=<LEMONADE_COMPONENTS>?node-id=21519-2604
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimePickerDialog
import figma from 'figma'
import { timePickerDialog } from '../shared/time'

export default {
  ...timePickerDialog(figma.selectedInstance, true),
  id: 'time-picker-dialog-input',
  metadata: { nestable: true },
}
