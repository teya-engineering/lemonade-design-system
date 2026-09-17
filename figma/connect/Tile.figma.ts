// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tile.kt
// component=Tile
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, slotIcon, imports: slotImports, quote } = renderer(instance, figma.kotlin)

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

const topAccessory = instance.getBoolean('◉ Show Top Accessory')
const icon = slotIcon('🧩 Leading Slot')

export default {
  example: figma.kotlin`LemonadeUi.Tile(
    label = "${quote(label)}",
    icon = ${icon ?? 'LemonadeIcons.Heart'},
    onClick = { },${supportText ? `
    supportText = "${quote(supportText)}",` : ''}${selected ? `
    isSelected = true,` : ''}${disabled ? `
    enabled = false,` : ''}
    variant = LemonadeTileVariant.${variant},
    orientation = LemonadeTileOrientation.${orientation},${
      topAccessory ? figma.kotlin`
    topAccessory = ${slot('↪ 🧩 Top Accessory', 'top accessory')},` : ''
    }
)${icon ? '' : `
// NOTE: the design's icon did not resolve; set the entry it uses`}`,
  imports: [
    'import com.teya.lemonade.core.LemonadeIcons',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tile',
    'import com.teya.lemonade.core.LemonadeTileOrientation',
    'import com.teya.lemonade.core.LemonadeTileVariant',
    ...slotImports,
  ],
  id: 'tile',
  metadata: { nestable: true },
}
