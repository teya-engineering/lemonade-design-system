// url=<LEMONADE_COMPONENTS>?node-id=21523-4664
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimePicker
import figma from 'figma'
import { timeState } from '../shared/time'

const instance = figma.selectedInstance
const display = instance.findInstance('Android / Time Input')
const holder = display && display.type === 'INSTANCE' ? display : instance

export default {
  example: figma.kotlin`${timeState(instance, holder)}
LemonadeUi.TimePicker(state = state)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.TimePicker',
    'import com.teya.lemonade.rememberLemonadeTimePickerState',
  ],
  id: 'time-picker',
  metadata: { nestable: true },
}
