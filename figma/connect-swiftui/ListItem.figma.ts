// url=<LEMONADE_COMPONENTS>?node-id=15094-5516
// source=swiftui/Sources/Lemonade/Components/LemonadeListItem.swift
// component=ListItem
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

const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const supportText = instance.getBoolean('◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

const navigationIndicator = instance.getBoolean('◉ Navigation Indicator')
const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
  ? slot('↪ 🧩 Leading', 'leading content')
  : '{ EmptyView() }'
const trailing = instance.getBoolean('◉ Show Trailing')
  ? slot('↪ 🧩 Trailing', 'trailing content')
  : '{ EmptyView() }'
const bottom = instance.getBoolean('◉ Show Bottom Slot')

export default {
  example: figma.swift`LemonadeUi.ListItem(
    label: "${label}"${supportText ? `,
    supportText: "${supportText}"` : ''}${navigationIndicator ? `,
    navigationIndicator: true` : ''}${isLoading ? `,
    isLoading: true` : ''}${showDivider ? `,
    showDivider: true` : ''},
    onListItemClick: { },
    leadingSlot: ${leading},
    trailingSlot: ${trailing}${bottom ? figma.swift`,
    slotContent: ${slot('↪ 🧩 Bottom Slot', 'bottom content')}` : ''}
)`,
  id: 'list-item',
  metadata: { nestable: true },
}
