// url=<LEMONADE_COMPONENTS>?node-id=4365-4446
// source=swiftui/Sources/Lemonade/Components/LemonadeLink.swift
// component=Link
import figma from 'figma'

const instance = figma.selectedInstance

const text = instance.getString('✍️ Label')

const icon = instance.getBoolean('◉ Show Leading') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.Link(
    text: "${text}",
    onClick: { }${iconCode ? figma.swift`,
    icon: ${iconCode}` : ''}
)`,
  id: 'link',
  metadata: { nestable: true },
}
