// url=<LEMONADE_COMPONENTS>?node-id=20918-229
// source=swiftui/Sources/Lemonade/Components/LemonadeBoxSelection.swift
// component=BoxSelection
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

const content = snippets('🧩 Content Slot', '    ')

const variant = instance.getEnum('◇ Variant', {
  Filled: 'filled',
  Outlined: 'outlined',
})

// "N/A" is the Outlined variant, where the background does not apply.
const background = instance.getEnum('◇ Background', {
  Default: 'default',
  Elevated: 'elevated',
  'N/A': undefined,
})

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

export default {
  example: figma.swift`LemonadeUi.BoxSelection(
    variant: .${variant}${background ? `,
    background: .${background}` : ''}${selected ? `,
    isSelected: true` : ''}${disabled ? `,
    enabled: false` : ''},
    onClick: { }
) {${content ?? `
    /* content */`}
}`,
  id: 'box-selection',
  metadata: { nestable: false },
}
