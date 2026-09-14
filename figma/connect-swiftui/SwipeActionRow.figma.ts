// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=swiftui/Sources/Lemonade/Components/LemonadeSwipeActionRow.swift
// component=SwipeActionRow
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

const content = snippets('🧩 Sliding Item', '    ')

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

// The actions are data objects with enum-typed icons, which a slot resolves to
// neither.
export default {
  example: figma.swift`LemonadeUi.SwipeActionRow(
    // TODO: one action per action in the design
    ${leading ? 'leadingActions: []' : 'trailingActions: []'}${showDivider ? `,
    showDivider: true` : ''}
) {${content ?? `
    /* row content */`}
}`,

  id: 'swipe-action-row',
  metadata: { nestable: false },
}
