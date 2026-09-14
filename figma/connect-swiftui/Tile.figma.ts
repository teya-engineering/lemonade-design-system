// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=swiftui/Sources/Lemonade/Components/LemonadeTile.swift
// component=Tile
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

const icon = slotIcon('🧩 Leading Slot')
const accessory = snippets('↪ 🧩 Top Accessory', '    ')

const label = instance.getString('✍️ Label')
const supportText = instance.getBoolean('◉ Show Support Text')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined

const variant = instance.getEnum('◇ Variant', { Filled: 'filled', Outlined: 'outlined' })
const orientation = instance.getEnum('◇ Orientation', {
  Vertical: 'vertical',
  Horizontal: 'horizontal',
})
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

// `icon` is required and enum-typed, and a slot cannot resolve to an enum value.
const topAccessory = instance.getBoolean('◉ Show Top Accessory')
  ? instance.getSlot('↪ 🧩 Top Accessory')
  : undefined

export default {
  example: topAccessory
    ? figma.swift`LemonadeUi.Tile(
    label: "${label}",
    ${icon ? figma.swift`icon: ${icon}` : '// TODO: icon — set the LemonadeIcon case the design uses'}${disabled ? `,
    enabled: false` : ''}${selected ? `,
    isSelected: true` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''},
    onClick: { },
    variant: .${variant},
    orientation: .${orientation}
) {${accessory ?? `
    /* top accessory */`}
}`
    : figma.swift`LemonadeUi.Tile(
    label: "${label}",
    ${icon ? figma.swift`icon: ${icon}` : '// TODO: icon — set the LemonadeIcon case the design uses'}${disabled ? `,
    enabled: false` : ''}${selected ? `,
    isSelected: true` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''},
    onClick: { },
    variant: .${variant},
    orientation: .${orientation}
)`,
  id: 'tile',
  metadata: { nestable: true },
}
