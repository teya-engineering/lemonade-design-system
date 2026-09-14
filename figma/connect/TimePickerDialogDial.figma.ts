// url=<LEMONADE_COMPONENTS>?node-id=21519-2543
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/TimePicker.kt
// component=TimePickerDialog
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

const buttons = instance.findLayers((node) => node.type === 'INSTANCE' && node.name === 'Primary button')
const label = (variant, fallback) => {
  const button = buttons.find(
    (b) => b.type === 'INSTANCE' && b.getEnum('◇ Variant', { Primary: 'Primary', Neutral: 'Neutral' }) === variant,
  )
  return button ? button.getString('✍️ Label') : fallback
}
const title = instance.getString('✍️ Headline')
const time = readTime(instance)

export default {
  example: figma.kotlin`val state = rememberLemonadeTimePickerState(
    initialHour = ${time.hour},
    initialMinute = ${time.minute},
    is24Hour = ${is24Hour},
)
LemonadeUi.TimePickerDialog(
    expanded = true,
    title = "${title}",
    confirmLabel = "${label('Primary', 'Confirm')}",
    cancelLabel = "${label('Neutral', 'Cancel')}",
    switchToInputLabel = "Switch to text input",
    switchToDialLabel = "Switch to clock dial",
    state = state,
    onDismissRequest = { },
    onConfirm = { hour, minute -> },
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.TimePickerDialog',
    'import com.teya.lemonade.rememberLemonadeTimePickerState',
  ],
  id: 'time-picker-dialog-dial',
  metadata: { nestable: true },
}
