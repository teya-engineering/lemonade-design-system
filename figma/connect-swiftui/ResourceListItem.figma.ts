// url=<LEMONADE_COMPONENTS>?node-id=15106-421
// source=swiftui/Sources/Lemonade/Components/LemonadeResourceListItem.swift
// component=ResourceListItem
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
    label: "${label}",
    value: "${value}"${supportText ? `,
    supportText: "${supportText}"` : ''}${isLoading ? `,
    isLoading: true` : ''}${showDivider ? `,
    showDivider: true` : ''},
    onItemClicked: { },${bottom ? figma.swift`
    addonSlot: ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
    leadingSlot: ${slot('↪ 🧩 Leading', 'leading content')}
)`,
  id: 'resource-list-item',
  metadata: { nestable: true },
}
