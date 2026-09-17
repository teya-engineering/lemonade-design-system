// url=<LEMONADE_COMPONENTS>?node-id=10489-151885
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SelectListItem.kt
// component=SelectListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports, quote } = renderer(instance, figma.kotlin)

const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

const type = instance.getEnum('◇ Type', {
  Single: 'Single',
  Multiple: 'Multiple',
  Toggle: 'Toggle',
})

// Figma calls the borderless variant "Ghost"; the enum calls it Plain.
const variant = instance.getEnum('◇ Variant', { Ghost: 'Plain', Outlined: 'Outlined' })

const checked = instance.getEnum('◉ Is Checked', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const bottom = instance.getBoolean('◉ Show Bottom Slot')
export default {
  example: figma.kotlin`LemonadeUi.SelectListItem(
    label = "${quote(label)}",
    type = SelectListItemType.${type},
    checked = ${checked},
    onItemClicked = { },${variant && variant !== 'Plain' ? `
    variant = SelectListItemVariant.${variant},` : ''}${supportText ? `
    supportText = "${quote(supportText)}",` : ''}${disabled ? `
    enabled = false,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading', 'leading content')},` : ''}${trailing ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing', 'trailing content')},` : ''}${bottom ? figma.kotlin`
    slotContent = ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SelectListItem',
    'import com.teya.lemonade.core.SelectListItemType',
    'import com.teya.lemonade.core.SelectListItemVariant',
    ...slotImports,
  ],
  id: 'select-list-item',
  metadata: { nestable: true },
}
