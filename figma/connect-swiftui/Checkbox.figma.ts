// url=<LEMONADE_COMPONENTS>?node-id=2259-5432
// source=swiftui/Sources/Lemonade/Components/LemonadeCheckbox.swift
// component=Checkbox
import figma from 'figma'

const instance = figma.selectedInstance

const status = instance.getEnum('◇ Status', {
  Selected: 'checked',
  Unselected: 'unchecked',
  Indeterminate: 'indeterminate',
})

const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const standalone = instance.getEnum('◇ Layout', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.swift`LemonadeUi.Checkbox(
    status: .${status},
    onCheckboxClicked: { }${disabled ? `,
    enabled: false` : ''}
)`
    : figma.swift`LemonadeUi.Checkbox(
    status: .${status},
    onCheckboxClicked: { },
    label: "${label}"${supportText ? `,
    supportText: "${supportText}"` : ''}${disabled ? `,
    enabled: false` : ''}
)`,
  id: 'checkbox',
  metadata: { nestable: true },
}
