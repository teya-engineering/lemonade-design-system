// url=<LEMONADE_COMPONENTS>?node-id=20809-38675
// source=swiftui/Sources/Lemonade/Components/LemonadeTooltip.swift
// component=Tooltip
import figma from 'figma'

const instance = figma.selectedInstance

const content = instance.getString('✍️ Content')
const title = instance.getBoolean('◉ Show Title') ? instance.getString('↪ ✍️ Title') : undefined

const placement = instance.getEnum('◇ Indicator Placement', {
  None: 'none',
  'Top Left': 'topLeft',
  'Top Center': 'topCenter',
  'Top Right': 'topRight',
  'Right Top': 'rightTop',
  'Right Center': 'rightCenter',
  'Right Bottom': 'rightBottom',
  'Bottom Right': 'bottomRight',
  'Bottom Center': 'bottomCenter',
  'Bottom Left': 'bottomLeft',
  'Left Bottom': 'leftBottom',
  'Left Center': 'leftCenter',
  'Left Top': 'leftTop',
})

const cover = instance.getBoolean('◉ Show Cover') ? instance.getSlot('↪ 🧩 Cover Slot') : undefined
const footer = instance.getBoolean('◉ Show Footer') ? instance.getSlot('↪ 🧩 Footer') : undefined
const showClose = instance.getBoolean('◉ Show Close Button')

export default {
  example: figma.swift`LemonadeUi.Tooltip(
    content: "${content}"${title ? `,
    title: "${title}"` : ''},
    indicatorPlacement: .${placement}${showClose ? `,
    onClose: { }` : ''}${
      cover ? figma.swift`,
    cover: { /* cover */ }` : ''
    }${
      footer ? figma.swift`,
    footer: { _ in /* footer */ }` : ''
    }
)`,
  id: 'tooltip',
  metadata: { nestable: true },
}
