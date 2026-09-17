// url=<LEMONADE_COMPONENTS>?node-id=13212-12463
// source=swiftui/Sources/Lemonade/Components/LemonadeActionListItem.swift
// component=ActionListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, quote } = renderer(instance, figma.swift)
const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const topLabel = instance.getBoolean('◉ Show Top Label') ? read('Top label') : undefined
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

const voice = instance.getEnum('◇ Voice', { Neutral: 'neutral', Critical: 'critical' })

const navigationIndicator = instance.getBoolean('◉ Navigation Indicator')
const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
  ? slot('↪ 🧩 Leading Slot', 'leading content')
  : '{ EmptyView() }'
const trailing = instance.getBoolean('◉ Show Trailing')
  ? slot('↪ 🧩 Trailing', 'trailing content')
  : '{ EmptyView() }'

export default {
  example: figma.swift`LemonadeUi.ActionListItem(
    label: "${quote(label)}"${topLabel ? `,
    topLabel: "${quote(topLabel)}"` : ''}${supportText ? `,
    supportText: "${quote(supportText)}"` : ''}${voice && voice !== 'neutral' ? `,
    voice: .${voice}` : ''}${navigationIndicator ? `,
    showNavigationIndicator: true` : ''}${isLoading ? `,
    isLoading: true` : ''}${showDivider ? `,
    showDivider: true` : ''},
    onItemClicked: { },
    leadingSlot: ${leading},
    trailingSlot: ${trailing}
)`,
  id: 'action-list-item',
  metadata: { nestable: true },
}
