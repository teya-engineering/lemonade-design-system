// url=<LEMONADE_COMPONENTS>?node-id=18177-1701
// source=swiftui/Sources/Lemonade/Components/LemonadeChip.swift
// component=Chip
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slotIcon } = renderer(instance, figma.swift)
const label = instance.getString('✍️ Label')
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const error = instance.getEnum('◉ Has Error', { True: true, False: false })

// Disabled lives in Interaction State rather than its own flag.
const disabled = instance.getEnum('◇ Interaction State', {
  Rest: false,
  Pressed: false,
  Disabled: true,
})

const counter = instance.getBoolean('◉ Shown Counter')
  ? instance.getString('↪ ✍️ Counter')
  : undefined

const leading = instance.getBoolean('◉ Show Leading')
const leadingIcon = leading ? slotIcon('↪ 🧩 Leading') : undefined

export default {
  example: figma.swift`LemonadeUi.Chip(
    label: "${label}",
    selected: ${selected}${leadingIcon ? figma.swift`,
    leadingIcon: ${leadingIcon}` : ''}${counter ? `,
    counter: ${counter}` : ''}${disabled ? `,
    enabled: false` : ''}${error ? `,
    error: true` : ''},
    onChipClicked: { }
)`,
  id: 'chip',
  metadata: { nestable: true },
}
