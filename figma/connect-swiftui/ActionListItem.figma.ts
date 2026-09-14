// url=<LEMONADE_COMPONENTS>?node-id=13212-12463
// source=swiftui/Sources/Lemonade/Components/LemonadeActionListItem.swift
// component=ActionListItem
import figma from 'figma'

const instance = figma.selectedInstance

// Lemonade components in a slot render as their own snippets, indented to fit;
// a slot holding none of them keeps its placeholder.
const snippets = (name, pad) => {
  const found = instance.getSlot(name)
  const children = found && found.connectedInstances ? found.connectedInstances : []
  if (!children.length) return undefined
  let body = figma.swift``
  for (const child of children) {
    const sections = child.executeTemplate().example.map((section) =>
      section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, `\n${pad}`) } : section,
    )
    body = figma.swift`${body}
${pad}${sections}`
  }
  return body
}

const slot = (name, placeholder, open = '{') => {
  const body = snippets(name, '        ')
  return body ? figma.swift`${open}${body}
    }` : `${open} /* ${placeholder} */ }`
}

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
    label: "${label}"${topLabel ? `,
    topLabel: "${topLabel}"` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''}${voice !== 'neutral' ? `,
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
