// url=<LEMONADE_COMPONENTS>?node-id=15106-421
// source=swiftui/Sources/Lemonade/Components/LemonadeResourceListItem.swift
// component=ResourceListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, quote } = renderer(instance, figma.swift)
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
  example: figma.swift`LemonadeUi.ResourceListItem(
    label: "${quote(label)}",
    value: "${quote(value)}"${supportText ? `,
    supportText: "${quote(supportText)}"` : ''}${isLoading ? `,
    isLoading: true` : ''}${showDivider ? `,
    showDivider: true` : ''},
    onItemClicked: { },${bottom ? figma.swift`
    addonSlot: ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
    leadingSlot: ${slot('↪ 🧩 Leading', 'leading content')}
)`,
  id: 'resource-list-item',
  metadata: { nestable: true },
}
