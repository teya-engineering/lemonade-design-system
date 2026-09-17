// url=<LEMONADE_COMPONENTS>?node-id=5144-128
// source=swiftui/Sources/Lemonade/Components/LemonadeSwitch.swift
// component=Switch
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

const checked = instance.getEnum('◉ Checked', { True: true, False: false })
const disabled = instance.getEnum('◇ Is Disabled', { True: true, False: false })
const standalone = instance.getEnum('☰ Variant', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ ◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.swift`LemonadeUi.Switch(
    checked: ${checked},
    onCheckedChange: { _ in }${disabled ? `,
    enabled: false` : ''}
)`
    : figma.swift`LemonadeUi.Switch(
    checked: ${checked},
    onCheckedChange: { _ in },
    label: "${quote(label)}"${supportText ? `,
    supportText: "${quote(supportText)}"` : ''}${disabled ? `,
    enabled: false` : ''}
)`,
  id: 'switch',
  metadata: { nestable: true },
}
