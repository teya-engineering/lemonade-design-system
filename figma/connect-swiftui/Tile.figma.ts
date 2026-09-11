// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=swiftui/Sources/Lemonade/Components/LemonadeTile.swift
// component=Tile
import figma from 'figma'

const instance = figma.selectedInstance

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

// `icon` is required and enum-typed, and a slot cannot resolve to an enum value.
const topAccessory = instance.getBoolean('◉ Show Top Accessory')
  ? instance.getSlot('↪ 🧩 Top Accessory')
  : undefined

export default {
  example: topAccessory
    ? figma.swift`LemonadeUi.Tile(
    label: "${label}",
    // TODO: icon — set the LemonadeIcon case the design uses${supportText ? `,
    supportText: "${supportText}"` : ''}${selected ? `,
    isSelected: true` : ''}${disabled ? `,
    enabled: false` : ''},
    onClick: { },
    variant: .${variant},
    orientation: .${orientation}
) {
    /* top accessory */
}`
    : figma.swift`LemonadeUi.Tile(
    label: "${label}",
    // TODO: icon — set the LemonadeIcon case the design uses${supportText ? `,
    supportText: "${supportText}"` : ''}${selected ? `,
    isSelected: true` : ''}${disabled ? `,
    enabled: false` : ''},
    onClick: { },
    variant: .${variant},
    orientation: .${orientation}
)`,
  id: 'tile',
  metadata: { nestable: true },
}
