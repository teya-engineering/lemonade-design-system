// url=<LEMONADE_COMPONENTS>?node-id=10489-151885
// source=swiftui/Sources/Lemonade/Components/LemonadeSelectListItem.swift
// component=SelectListItem
import figma from 'figma'

const instance = figma.selectedInstance

// Label and support text are plain text layers, not properties.
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

// Both builders are required by this overload, so an empty one is emitted where
// the design has no content.
const leading = instance.getBoolean('◉ Show Leading') ? '/* leading content */' : 'EmptyView()'
const trailing = instance.getBoolean('◉ Show Trailing') ? '/* trailing content */' : 'EmptyView()'
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
    leadingSlot: { ${leading} },
    trailingSlot: { ${trailing} }${bottom ? `,
    slotContent: { /* bottom content */ }` : ''}
)`,
  id: 'select-list-item',
  metadata: { nestable: true },
}
