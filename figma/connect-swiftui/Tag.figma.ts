// url=<LEMONADE_COMPONENTS>?node-id=2199-1320
// source=swiftui/Sources/Lemonade/Components/LemonadeTag.swift
// component=Tag
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')

const voice = instance.getEnum('Voice', {
  Neutral: 'neutral',
  Critical: 'critical',
  Warning: 'warning',
  Info: 'info',
  Positive: 'positive',
  'Neutral On Color': 'neutralOnColor',
  Featured: 'featured',
})

const icon = instance.getBoolean('Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.Tag(
    label: "${label}"${iconCode ? figma.swift`,
    icon: ${iconCode}` : ''},
    voice: .${voice}
)`,
  id: 'tag',
  metadata: { nestable: true },
}
