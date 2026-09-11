// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tile.kt
// component=Tile
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')
const supportText = instance.getBoolean('◉ Show Support Text')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined

const variant = instance.getEnum('◇ Variant', { Filled: 'Filled', Outlined: 'Outlined' })
const orientation = instance.getEnum('◇ Orientation', {
  Vertical: 'Vertical',
  Horizontal: 'Horizontal',
})
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

// `icon` is required and enum-typed, and a slot cannot resolve to an enum value.
const topAccessory = instance.getBoolean('◉ Show Top Accessory')
  ? instance.getSlot('↪ 🧩 Top Accessory')
  : undefined

export default {
  example: figma.kotlin`LemonadeUi.Tile(
    label = "${label}",
    // TODO: icon — set the LemonadeIcons entry the design uses
    onClick = { },${supportText ? `
    supportText = "${supportText}",` : ''}${selected ? `
    isSelected = true,` : ''}${disabled ? `
    enabled = false,` : ''}
    variant = LemonadeTileVariant.${variant},
    orientation = LemonadeTileOrientation.${orientation},${
      topAccessory ? figma.kotlin`
    topAccessory = { /* top accessory */ },` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tile',
    'import com.teya.lemonade.core.LemonadeTileOrientation',
    'import com.teya.lemonade.core.LemonadeTileVariant',
  ],
  id: 'tile',
  metadata: { nestable: true },
}
