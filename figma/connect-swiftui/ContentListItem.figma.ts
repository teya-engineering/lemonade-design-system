// url=<LEMONADE_COMPONENTS>?node-id=18130-69377
// source=swiftui/Sources/Lemonade/Components/LemonadeContentListItem.swift
// component=ContentListItem
import figma from 'figma'

const instance = figma.selectedInstance

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
    leadingSlot: { ${leadingSlot} }` : ''
    }${
      trailingSlot ? figma.swift`,
    trailingSlot: { ${trailingSlot} }` : ''
    }${
      contentSlot ? figma.swift`,
    contentSlot: { ${contentSlot} }` : ''
    }
)`,
  id: 'content-list-item',
  metadata: { nestable: true },
}
