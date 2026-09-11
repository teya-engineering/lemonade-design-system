// url=<LEMONADE_COMPONENTS>?node-id=15094-5516
// source=swiftui/Sources/Lemonade/Components/LemonadeListItem.swift
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
  ? '/* leading content */'
  : 'EmptyView()'
const trailing = instance.getBoolean('◉ Show Trailing')
  ? '/* trailing content */'
  : 'EmptyView()'
const bottom = instance.getBoolean('◉ Show Bottom Slot')

export default {
  example: figma.swift`LemonadeUi.ListItem(
    label: "${label}"${supportText ? `,
    supportText: "${supportText}"` : ''}${navigationIndicator ? `,
    navigationIndicator: true` : ''}${isLoading ? `,
    isLoading: true` : ''}${showDivider ? `,
    showDivider: true` : ''},
    onListItemClick: { },
    leadingSlot: { ${leading} },
    trailingSlot: { ${trailing} }${bottom ? `,
    slotContent: { /* bottom content */ }` : ''}
)`,
  id: 'list-item',
  metadata: { nestable: true },
}
