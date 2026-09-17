// url=<LEMONADE_COMPONENTS>?node-id=11526-16665
// source=swiftui/Sources/Lemonade/Components/LemonadeSegmentedControl.swift
// component=SegmentedControl
import figma from 'figma'

const instance = figma.selectedInstance

const segments = instance.getEnum('◇ Segments', { '2': 2, '3': 3, '4': 4, '5': 5 })

// Figma numbers the selected segment from 1; selectedTab is a 0-based index.
const selected = instance.getEnum('◇ Selected', { '1': 0, '2': 1, '3': 2, '4': 3, '5': 4 })

const size = instance.getEnum('◇ Size', {
  Large: 'large',
  Medium: 'medium',
  Small: 'small',
})

let tabs = figma.swift``
let count = 0
for (let n = 1; n <= segments; n += 1) {
  const child = instance.findInstance(`↪ Button ${n}`)
  if (!child || child.type !== 'INSTANCE') continue
  tabs = figma.swift`${tabs}
        ${child.executeTemplate().example},`
  count += 1
}

const placeholders = Array.from({ length: segments }, (_, i) => `
        LemonadeTabButtonProperties.label("Tab ${i + 1}"),`).join('')

export default {
  example: figma.swift`LemonadeUi.SegmentedControl(
    properties: [${count ? tabs : placeholders}
    ],
    selectedTab: ${selected},
    size: .${size},
    onTabSelected: { _ in }
)`,
  id: 'segmented-control',
  metadata: { nestable: true },
}
