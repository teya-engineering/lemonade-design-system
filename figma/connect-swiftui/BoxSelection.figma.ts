// url=<LEMONADE_COMPONENTS>?node-id=20918-229
// source=swiftui/Sources/Lemonade/Components/LemonadeBoxSelection.swift
// component=BoxSelection
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets } = renderer(instance, figma.swift)
const content = snippets('🧩 Content Slot', '    ')

const variant = instance.getEnum('◇ Variant', {
  Filled: 'filled',
  Outlined: 'outlined',
})

// "N/A" is the Outlined variant, where the background does not apply.
const background = instance.getEnum('◇ Background', {
  Default: 'default',
  Elevated: 'elevated',
  'N/A': undefined,
})

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

export default {
  example: figma.swift`LemonadeUi.BoxSelection(
    variant: .${variant}${background ? `,
    background: .${background}` : ''}${selected ? `,
    isSelected: true` : ''}${disabled ? `,
    enabled: false` : ''},
    onClick: { }
) {${content ?? `
    /* content */`}
}`,
  id: 'box-selection',
  metadata: { nestable: false },
}
