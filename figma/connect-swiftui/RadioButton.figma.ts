// url=<LEMONADE_COMPONENTS>?node-id=2259-5307
// source=swiftui/Sources/Lemonade/Components/LemonadeRadioButton.swift
// component=RadioButton
import figma from 'figma'

const instance = figma.selectedInstance

const checked = instance.getEnum('◉ Is Checked', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const standalone = instance.getEnum('◇ Layout', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.swift`LemonadeUi.RadioButton(
    checked: ${checked},
    onRadioButtonClicked: { }${disabled ? `,
    enabled: false` : ''}
)`
    : figma.swift`LemonadeUi.RadioButton(
    checked: ${checked},
    onRadioButtonClicked: { },
    label: "${label}"${supportText ? `,
    supportText: "${supportText}"` : ''}${disabled ? `,
    enabled: false` : ''}
)`,
  id: 'radio-button',
  metadata: { nestable: true },
}
