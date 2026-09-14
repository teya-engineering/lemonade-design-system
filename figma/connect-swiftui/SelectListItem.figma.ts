// url=<LEMONADE_COMPONENTS>?node-id=10489-151885
// source=swiftui/Sources/Lemonade/Components/LemonadeSelectListItem.swift
// component=SelectListItem
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
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

const type = instance.getEnum('◇ Type', {
  Single: 'single',
  Multiple: 'multiple',
  Toggle: 'toggle',
})

// Figma calls the borderless variant "Ghost"; the enum calls it plain.
const variant = instance.getEnum('◇ Variant', { Ghost: 'plain', Outlined: 'outlined' })

const checked = instance.getEnum('◉ Is Checked', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
  ? slot('↪ 🧩 Leading', 'leading content')
  : '{ EmptyView() }'
const trailing = instance.getBoolean('◉ Show Trailing')
  ? slot('↪ 🧩 Trailing', 'trailing content')
  : '{ EmptyView() }'
const bottom = instance.getBoolean('◉ Show Bottom Slot')

export default {
  example: figma.swift`LemonadeUi.SelectListItem(
    label: "${label}",
    type: .${type},
    checked: ${checked},
    onItemClicked: { }${variant !== 'plain' ? `,
    variant: .${variant}` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''}${disabled ? `,
    enabled: false` : ''}${showDivider ? `,
    showDivider: true` : ''},
    leadingSlot: ${leading},
    trailingSlot: ${trailing}${bottom ? figma.swift`,
    slotContent: ${slot('↪ 🧩 Bottom Slot', 'bottom content')}` : ''}
)`,
  id: 'select-list-item',
  metadata: { nestable: true },
}
