// url=<LEMONADE_COMPONENTS>?node-id=10489-151885
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SelectListItem.kt
// component=SelectListItem
import figma from 'figma'

const instance = figma.selectedInstance

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
    label = "${label}",
    type = SelectListItemType.${type},
    checked = ${checked},
    onItemClicked = { },${variant !== 'Plain' ? `
    variant = SelectListItemVariant.${variant},` : ''}${supportText ? `
    supportText = "${supportText}",` : ''}${disabled ? `
    enabled = false,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? `
    leadingSlot = { /* leading content */ },` : ''}${trailing ? `
    trailingSlot = { /* trailing content */ },` : ''}${bottom ? `
    slotContent = { /* bottom content */ },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SelectListItem',
    'import com.teya.lemonade.core.SelectListItemType',
    'import com.teya.lemonade.core.SelectListItemVariant',
  ],
  id: 'select-list-item',
  metadata: { nestable: true },
}
