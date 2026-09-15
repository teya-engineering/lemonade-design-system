// url=<LEMONADE_COMPONENTS>?node-id=21519-2543
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimePickerDialog
import figma from 'figma'
import { timePickerDialog } from '../shared/time'

export default {
  ...timePickerDialog(figma.selectedInstance, false),
  id: 'time-picker-dialog-dial',
  metadata: { nestable: true },
}
