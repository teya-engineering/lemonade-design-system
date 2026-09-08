// url=<LEMONADE_COMPONENTS>?node-id=20918-229
// source=swiftui/Sources/Lemonade/Components/LemonadeBoxSelection.swift
// component=BoxSelection
import figma from 'figma'

const instance = figma.selectedInstance

const variant = instance.getEnum('◇ Variant', {
  Filled: 'filled',
  Outlined: 'outlined',
})

// "N/A" is the Outlined variant, where the background does not apply — emitting
// a value there would imply a choice the design does not make.
const background = instance.getEnum('◇ Background', {
  Default: 'default',
  Elevated: 'elevated',
  'N/A': undefined,
})

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const content = instance.getSlot('🧩 Content Slot')

export default {
  example: figma.swift`LemonadeUi.BoxSelection(
    variant: .${variant}${background ? `,
    background: .${background}` : ''}${selected ? `,
    isSelected: true` : ''}${disabled ? `,
    enabled: false` : ''},
    onClick: { }
) {
    ${content}
}`,
  id: 'box-selection',
  metadata: { nestable: false },
}
