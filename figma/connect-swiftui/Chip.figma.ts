// url=<LEMONADE_COMPONENTS>?node-id=18177-1701
// source=swiftui/Sources/Lemonade/Components/LemonadeChip.swift
// component=Chip
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

// An enum-typed parameter takes the glyph of the Icon a slot holds.
const slotIcon = (name) => {
  const found = instance.getSlot(name)
  const icon = found && found.connectedInstances ? found.connectedInstances[0] : undefined
  const glyph = icon ? icon.getInstanceSwap('🧩 Icon') : undefined
  return glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
}

const label = instance.getString('✍️ Label')
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const error = instance.getEnum('◉ Has Error', { True: true, False: false })

// Disabled lives in Interaction State rather than its own flag.
const disabled = instance.getEnum('◇ Interaction State', {
  Rest: false,
  Pressed: false,
  Disabled: true,
})

const counter = instance.getBoolean('◉ Shown Counter')
  ? instance.getString('↪ ✍️ Counter')
  : undefined

const leading = instance.getBoolean('◉ Show Leading') ? instance.getSlot('↪ 🧩 Leading') : undefined
const trailing = instance.getBoolean('◉ Show Trailing') ? instance.getSlot('↪ 🧩 Trailing') : undefined
const leadingIcon = leading ? slotIcon('↪ 🧩 Leading') : undefined

export default {
  example: figma.swift`LemonadeUi.Chip(
    label: "${label}",
    selected: ${selected}${leadingIcon ? figma.swift`,
    leadingIcon: ${leadingIcon}` : ''}${counter ? `,
    counter: ${counter}` : ''}${disabled ? `,
    enabled: false` : ''}${error ? `,
    error: true` : ''},
    onChipClicked: { }
)`,
  id: 'chip',
  metadata: { nestable: true },
}
