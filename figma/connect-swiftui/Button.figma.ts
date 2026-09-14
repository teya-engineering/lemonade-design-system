// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=swiftui/Sources/Lemonade/Components/LemonadeButton.swift
// component=Button
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

const variant = instance.getEnum('◇ Variant', {
  Primary: 'primary',
  Secondary: 'secondary',
  Neutral: 'neutral',
  Critical: 'critical',
  'On Brand': 'onBrand',
  'On Color': 'onColor',
})

const type = instance.getEnum('◇ Type', {
  Solid: 'solid',
  Subtle: 'subtle',
  Ghost: 'ghost',
})

const size = instance.getEnum('↕ Size', {
  Large: 'large',
  Medium: 'medium',
  Small: 'small',
  XSmall: 'xSmall',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const leadingSlot = instance.getBoolean('◉ Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('◉ Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined

// Swift rejects a trailing comma in an argument list, so every optional
// argument carries a leading comma instead.
export default {
  example: figma.swift`LemonadeUi.Button(
    label: "${label}",
    onClick: { }${
      leadingSlot ? figma.swift`,
    leadingSlot: ${slot('↪ 🧩 Leading Slot', 'leading content', '{ _ in')}` : ''
    }${
      trailingSlot ? figma.swift`,
    trailingSlot: ${slot('↪ 🧩 Trailing Slot', 'trailing content', '{ _ in')}` : ''
    },
    variant: .${variant},
    type: .${type},
    size: .${size}${disabled ? `,
    enabled: false` : ''}${loading ? `,
    loading: true` : ''}
)`,
  id: 'button',
  metadata: { nestable: true },
}
