// url=<LEMONADE_COMPONENTS>?node-id=15094-5516
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ListItem
import figma from 'figma'

const instance = figma.selectedInstance

const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const supportText = instance.getBoolean('◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

const navigationIndicator = instance.getBoolean('◉ Navigation Indicator')
const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const bottom = instance.getBoolean('◉ Show Bottom Slot')

export default {
  example: figma.kotlin`LemonadeUi.ListItem(
    label = "${label}",${supportText ? `
    supportText = "${supportText}",` : ''}
    onListItemClick = { },${navigationIndicator ? `
    navigationIndicator = true,` : ''}${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? `
    leadingSlot = { /* leading content */ },` : ''}${trailing ? `
    trailingSlot = { /* trailing content */ },` : ''}${bottom ? `
    slotContent = { /* bottom content */ },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.ListItem',
  ],
  id: 'list-item',
  metadata: { nestable: true },
}
