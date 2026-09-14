// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=swiftui/Sources/Lemonade/Components/LemonadeTile.swift
// component=Tile
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets, slotIcon } = renderer(instance, figma.swift)
const icon = slotIcon('🧩 Leading Slot')

const label = instance.getString('✍️ Label')
const supportText = instance.getBoolean('◉ Show Support Text')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined

const variant = instance.getEnum('◇ Variant', { Filled: 'filled', Outlined: 'outlined' })
const orientation = instance.getEnum('◇ Orientation', {
  Vertical: 'vertical',
  Horizontal: 'horizontal',
})
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const topAccessory = instance.getBoolean('◉ Show Top Accessory')
const accessory = topAccessory ? snippets('↪ 🧩 Top Accessory', '    ') : undefined

export default {
  example: figma.swift`LemonadeUi.Tile(
    label: "${label}",
    ${icon ? figma.swift`icon: ${icon}` : '// TODO: icon — set the LemonadeIcon case the design uses'}${disabled ? `,
    enabled: false` : ''}${selected ? `,
    isSelected: true` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''},
    onClick: { },
    variant: .${variant},
    orientation: .${orientation}
)${topAccessory ? figma.swift` {${accessory ?? `
    /* top accessory */`}
}` : ''}`,
  id: 'tile',
  metadata: { nestable: true },
}
