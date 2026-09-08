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

// The tabs are named instances in document order, each carrying its own label
// and icon, so they resolve through their own template rather than being
// invented here. Five lookups covers the set's maximum.
const tab = (n) => {
  const child = instance.findInstance(`↪ Button ${n}`)
  return child && child.type === 'INSTANCE' ? child.executeTemplate().example : undefined
}
const t1 = tab(1)
const t2 = tab(2)
const t3 = tab(3)
const t4 = tab(4)
const t5 = tab(5)

// Only used when no tab resolves — an empty array would say less than the
// segment count does.
const placeholders = Array.from(
  { length: segments },
  (_, i) => `        LemonadeTabButtonProperties.label("Tab ${i + 1}"),`,
).join('\n')

export default {
  example: t1
    ? figma.swift`LemonadeUi.SegmentedControl(
    properties: [${t1 ? figma.swift`
        ${t1},` : ''}${t2 ? figma.swift`
        ${t2},` : ''}${t3 ? figma.swift`
        ${t3},` : ''}${t4 ? figma.swift`
        ${t4},` : ''}${t5 ? figma.swift`
        ${t5},` : ''}
    ],
    selectedTab: ${selected},
    size: .${size},
    onTabSelected: { _ in }
)`
    : figma.swift`LemonadeUi.SegmentedControl(
    properties: [
${placeholders}
    ],
    selectedTab: ${selected},
    size: .${size},
    onTabSelected: { _ in }
)`,
  id: 'segmented-control',
  metadata: { nestable: true },
}
