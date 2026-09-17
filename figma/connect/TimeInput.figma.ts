// url=<LEMONADE_COMPONENTS>?node-id=21523-4508
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimeInput
import figma from 'figma'
import { timeState } from '../shared/time'

const instance = figma.selectedInstance

export default {
  example: figma.kotlin`${timeState(instance)}
LemonadeUi.TimeInput(state = state)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.TimeInput',
    'import com.teya.lemonade.rememberLemonadeTimePickerState',
  ],
  id: 'time-input',
  metadata: { nestable: false },
}
