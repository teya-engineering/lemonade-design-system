// url=<LEMONADE_COMPONENTS>?node-id=18130-69377
// source=swiftui/Sources/Lemonade/Components/LemonadeContentListItem.swift
// component=ContentListItem
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

const label = instance.getString('✍️ Label')
const value = instance.getString('✍️ Value')

const layout = instance.getEnum('◇ Layout', {
  Horizontal: 'horizontal',
  Vertical: 'vertical',
})

const density = instance.getEnum('◇ Density', {
  Comfortable: 'comfortable',
  Compact: 'compact',
})

const showDivider = instance.getEnum('◉ Show divider', { True: true, False: false })

const leadingSlot = instance.getBoolean('Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined
const contentSlot = instance.getBoolean('◉ Show Content Slot')
  ? instance.getSlot('↪ 🧩 Content Slot')
  : undefined

export default {
  example: figma.swift`LemonadeUi.ContentListItem(
    label: "${label}",
    value: "${value}",
    layout: .${layout}${showDivider ? `,
    showDivider: true` : ''},
    density: .${density}${
      leadingSlot ? figma.swift`,
    leadingSlot: ${slot('↪ 🧩 Leading Slot', 'leading content')}` : ''
    }${
      trailingSlot ? figma.swift`,
    trailingSlot: ${slot('↪ 🧩 Trailing Slot', 'trailing content')}` : ''
    }${
      contentSlot ? figma.swift`,
    contentSlot: ${slot('↪ 🧩 Content Slot', 'content')}` : ''
    }
)`,
  id: 'content-list-item',
  metadata: { nestable: true },
}
