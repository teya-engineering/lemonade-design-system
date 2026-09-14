// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=Card
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

const content = snippets('🧩 Slot', '    ')

const background = instance.getEnum('◇ Background', {
  Default: 'default',
  Subtle: 'subtle',
  Elevated: 'elevated',
})

const padding = instance.getEnum('◇ Spacing', {
  None: 'none',
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
})

const nested = (show, layer) => {
  if (!instance.getBoolean(show)) return undefined
  const child = instance.findInstance(layer)
  if (!child || child.type !== 'INSTANCE') return undefined
  const result = child.executeTemplate()
  return {
    ...result,
    example: result.example.map((section) =>
      section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, '\n    ') } : section,
    ),
  }
}
const header = nested('◉ Show Heading', 'Card Heading')
const footer = nested('◉ Show Footer Action', 'Card Footer Action')

export default {
  example: figma.swift`LemonadeUi.Card(
    contentPadding: .${padding},
    background: .${background}${header ? figma.swift`,
    header: ${header.example}` : ''}${footer ? figma.swift`,
    footerAction: ${footer.example}` : ''}
) {${content ?? `
    /* card content */`}
}`,
  id: 'card',
  metadata: { nestable: false },
}
