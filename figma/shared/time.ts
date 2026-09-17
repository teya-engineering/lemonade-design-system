import figma from 'figma'
import { renderer } from './render'

// Code Connect names the fields inside the dial's nested Time Input after that
// component's own building block, not the one Figma shows there.
const FIELDS = ['.Android/Building Blocks/Input', '.Android/Building Blocks/Direct Input (keyboard) input']
const PERIOD = '.Android/Building Blocks/Period Selector'

// The code takes a 0-23 hour; Figma shows a 12-hour clock with an AM/PM toggle.
const readTime = (holder, is24Hour) => {
  const layers = holder.findLayers(
    (node) => node.type === 'INSTANCE' && (FIELDS.includes(node.name) || node.name.startsWith(PERIOD)),
  )
  const [hourField, minuteField] = layers.filter((node) => FIELDS.includes(node.name))
  const period = layers.find((node) => node.name.startsWith(PERIOD))
  const value = (field) => (field ? parseInt(field.getString('Time selector label text'), 10) || 0 : 0)
  const pm = period ? period.getEnum('AM/PM', { True: true, False: false }) : false
  const hour = value(hourField)
  return {
    hour: !is24Hour && hour <= 12 ? (pm ? (hour % 12) + 12 : hour % 12) : hour,
    minute: value(minuteField),
  }
}

// The state every time picker composable takes, seeded with the designed time.
export const timeState = (instance, holder = instance) => {
  const is24Hour = instance.getEnum('◇ Format', { '12 hour': false, '24 hour': true }) ?? false
  const time = readTime(holder, is24Hour)
  return figma.kotlin`val state = rememberLemonadeTimePickerState(
    initialHour = ${time.hour},
    initialMinute = ${time.minute},
    is24Hour = ${is24Hour},
)`
}

// The Dial and Input dialog sets differ only in the mode the dialog opens on.
export const timePickerDialog = (instance, inputMode) => {
  const { quote } = renderer(instance, figma.kotlin)
  const buttons = instance.findLayers((node) => node.type === 'INSTANCE' && node.name === 'Primary button')
  const label = (variant, fallback) => {
    const button = buttons.find(
      (b) => b.getEnum('◇ Variant', { Primary: 'Primary', Neutral: 'Neutral' }) === variant,
    )
    return button ? button.getString('✍️ Label') : fallback
  }
  return {
    example: figma.kotlin`${timeState(instance)}
LemonadeUi.TimePickerDialog(
    expanded = true,
    title = "${quote(instance.getString('✍️ Headline'))}",
    confirmLabel = "${quote(label('Primary', 'Confirm'))}",
    cancelLabel = "${quote(label('Neutral', 'Cancel'))}",
    switchToInputLabel = "Switch to text input",
    switchToDialLabel = "Switch to clock dial",
    state = state,
    onDismissRequest = { },
    onConfirm = { hour, minute -> },${inputMode ? `
    initialDisplayMode = LemonadeTimePickerDisplayMode.Input,` : ''}
)`,
    imports: [
      ...(inputMode ? ['import com.teya.lemonade.LemonadeTimePickerDisplayMode'] : []),
      'import com.teya.lemonade.LemonadeUi',
      'import com.teya.lemonade.TimePickerDialog',
      'import com.teya.lemonade.rememberLemonadeTimePickerState',
    ],
  }
}
