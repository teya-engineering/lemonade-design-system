// url=<LEMONADE_COMPONENTS>?node-id=15115-987
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=CardHeaderConfig
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

const title = instance.getString('✍️ Title')
const subtitle = instance.getBoolean('◉ Show Subtitle', {
  true: instance.getString('↪ ✍️ Subtitle'),
  false: undefined,
})
const style = instance.getEnum('◇ Heading Style', {
  Default: 'default',
  Overline: 'overline',
})
const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const navigation = instance.getBoolean('◉ Show Navigation Indicator')

export default {
  example: figma.swift`CardHeaderConfig(
    title: "${title}"${subtitle ? `,
    subtitle: "${subtitle}"` : ''}${style !== 'default' ? `,
    headingStyle: .${style}` : ''}${leading ? figma.swift`,
    leadingSlot: ${slot('↪ 🧩 Leading', 'leading content')}` : ''}${trailing ? figma.swift`,
    trailingSlot: ${slot('↪ 🧩 Trailing', 'trailing content')}` : ''}${navigation ? `,
    showNavigationIndicator: true` : ''}
)`,
  id: 'card-heading',
  metadata: { nestable: true },
}
