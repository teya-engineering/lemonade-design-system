// url=<LEMONADE_COMPONENTS>?node-id=18177-1701
// source=swiftui/Sources/Lemonade/Components/LemonadeChip.swift
// component=Chip
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const error = instance.getEnum('◉ Has Error', { True: true, False: false })

// Chip folds the disabled state into Interaction State rather than exposing a
// separate flag; Rest and Pressed are runtime states with no code equivalent.
const disabled = instance.getEnum('◇ Interaction State', {
  Rest: false,
  Pressed: false,
  Disabled: true,
})

const counter = instance.getBoolean('◉ Shown Counter')
  ? instance.getString('↪ ✍️ Counter')
  : undefined

// leadingIcon and trailingIcon are enum-typed, and a Figma slot cannot be
// resolved to an enum value, so they are left out rather than guessed.
const leading = instance.getBoolean('◉ Show Leading') ? instance.getSlot('↪ 🧩 Leading') : undefined
const trailing = instance.getBoolean('◉ Show Trailing') ? instance.getSlot('↪ 🧩 Trailing') : undefined

export default {
  example: figma.swift`LemonadeUi.Chip(
    label: "${label}",
    selected: ${selected}${counter ? `,
    counter: ${counter}` : ''}${disabled ? `,
    enabled: false` : ''}${error ? `,
    error: true` : ''},
    onChipClicked: { }
)`,
  id: 'chip',
  metadata: { nestable: true },
}
