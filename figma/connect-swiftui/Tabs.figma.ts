// url=<LEMONADE_COMPONENTS>?node-id=8122-19623
// source=swiftui/Sources/Lemonade/Components/LemonadeTabs.swift
// component=Tabs
import figma from 'figma'

const instance = figma.selectedInstance

const itemsSize = instance.getEnum('↕ Items Size', { Hug: 'hug', Stretch: 'stretch' })

// The Items variant tops out at "5+", so the count comes from resolving the
// tabs. Nine is the most the set lays out.
let tabs = figma.swift``
let count = 0
let selectedIndex = 0
for (let n = 1; n <= 9; n += 1) {
  const child = instance.findInstance(`Tab ${n}`)
  if (!child || child.type !== 'INSTANCE') continue
  const result = child.executeTemplate()
  // Selection is a tab property in Figma and an index here.
  if (result.metadata?.props?.selected === 'true') selectedIndex = count
  tabs = figma.swift`${tabs}
        ${result.example},`
  count += 1
}

export default {
  example: figma.swift`LemonadeUi.Tabs(
    tabs: [${tabs}
    ],
    selectedIndex: ${selectedIndex},
    onTabSelected: { _ in },
    itemsSize: .${itemsSize}
)`,
  id: 'tabs',
  metadata: { nestable: true },
}
