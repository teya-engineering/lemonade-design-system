// url=<LEMONADE_COMPONENTS>?node-id=15106-421
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ResourceListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports } = renderer(instance, figma.kotlin)

const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : ''
}
const label = read('Label')
const value = read('Value')

const supportText = instance.getBoolean('◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })
const bottom = instance.getBoolean('◉ Show Bottom Slot')
export default {
  example: figma.kotlin`LemonadeUi.ResourceListItem(
    leadingSlot = ${slot('↪ 🧩 Leading', 'leading content')},
    label = "${label}",
    value = "${value}",${supportText ? `
    supportText = "${supportText}",` : ''}
    onItemClicked = { },${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${bottom ? figma.kotlin`
    addonSlot = ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.ResourceListItem',
    ...slotImports,
  ],
  id: 'resource-list-item',
  metadata: { nestable: true },
}
