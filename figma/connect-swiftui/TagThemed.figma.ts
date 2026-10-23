// url=<LEMONADE_COMPONENTS>?node-id=22216-418
// source=swiftui/Sources/Lemonade/Components/LemonadeTag.swift
// component=Tag
import figma from 'figma'
import { renderer } from '../shared/render'
import { swiftThemes } from '../shared/themed'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

const label = instance.getString('✍️ Label')

const theme = instance.getEnum('◇ Theme', swiftThemes)

const icon = instance.getBoolean('Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
const iconCode = icon && icon.type === 'INSTANCE' ? icon.executeTemplate().example : undefined

export default {
  example: figma.swift`LemonadeUi.Tag(
    label: "${quote(label)}"${iconCode ? figma.swift`,
    icon: ${iconCode}` : ''},
    theme: ${theme}
)`,
  id: 'tag-themed',
  metadata: { nestable: true },
}
