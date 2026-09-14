// url=<LEMONADE_COMPONENTS>?node-id=21523-4508
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimeInput
import figma from 'figma'

const instance = figma.selectedInstance

const is24Hour = instance.getEnum('◇ Format', { '12 hour': false, '24 hour': true })

// The code takes a 0-23 hour; Figma shows a 12-hour clock with an AM/PM toggle.
const readTime = (holder) => {
  const fields = holder.findLayers(
    (node) =>
      node.type === 'INSTANCE' &&
      (node.name === '.Android/Building Blocks/Input' ||
        node.name === '.Android/Building Blocks/Direct Input (keyboard) input'),
  )
  const value = (field) =>
    field && field.type === 'INSTANCE' ? parseInt(field.getString('Time selector label text'), 10) || 0 : 0
  let hour = value(fields[0])
  const minute = value(fields[1])
  const period = holder.findLayers(
    (node) => node.type === 'INSTANCE' && node.name.startsWith('.Android/Building Blocks/Period Selector'),
  )[0]
  const pm = period && period.type === 'INSTANCE' ? period.getEnum('AM/PM', { True: true, False: false }) : false
  if (!is24Hour && hour <= 12) hour = pm ? (hour % 12) + 12 : hour % 12
  return { hour, minute }
}

const time = readTime(instance)

export default {
  example: figma.kotlin`val state = rememberLemonadeTimePickerState(
    initialHour = ${time.hour},
    initialMinute = ${time.minute},
    is24Hour = ${is24Hour},
)
LemonadeUi.TimeInput(state = state)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.TimeInput',
    'import com.teya.lemonade.rememberLemonadeTimePickerState',
  ],
  id: 'time-input',
  metadata: { nestable: true },
}
