// url=<LEMONADE_COMPONENTS>?node-id=15106-421
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ResourceListItem
import figma from 'figma'

const instance = figma.selectedInstance

// Label and value are plain text layers; only the description is a property.
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

// leadingSlot is required, so it is always emitted. Slots are a presence signal
// only — Figma cannot inline their contents into a Kotlin snippet.
export default {
  example: figma.kotlin`LemonadeUi.ResourceListItem(
    leadingSlot = { /* leading content */ },
    label = "${label}",
    value = "${value}",${supportText ? `
    supportText = "${supportText}",` : ''}
    onItemClicked = { },${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${bottom ? `
    addonSlot = { /* bottom content */ },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.ResourceListItem',
  ],
  id: 'resource-list-item',
  metadata: { nestable: true },
}
