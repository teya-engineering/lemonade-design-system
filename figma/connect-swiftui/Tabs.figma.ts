// url=<LEMONADE_COMPONENTS>?node-id=8122-19623
// source=swiftui/Sources/Lemonade/Components/LemonadeTabs.swift
// component=Tabs
import figma from 'figma'

const instance = figma.selectedInstance

const itemsSize = instance.getEnum('↕ Items Size', { Hug: 'hug', Stretch: 'stretch' })

// The Items variant tops out at "5+", but the tabs themselves are real named
// instances, so the count comes from resolving them rather than from the label.
// Nine is the most the set lays out.
const tabs = []
let selectedIndex = 0
for (let n = 1; n <= 9; n += 1) {
  const child = instance.findInstance(`Tab ${n}`)
  if (!child || child.type !== 'INSTANCE') continue
  const result = child.executeTemplate()
  // Selection is a property of the tab in Figma and an index on the parent, so
  // the child surfaces it through metadata.props.
  if (result.metadata?.props?.selected === 'true') selectedIndex = tabs.length
  tabs.push(result.example)
}

const [t1, t2, t3, t4, t5, t6, t7, t8, t9] = tabs
const line = (t) => (t ? figma.swift`
        ${t},` : '')

export default {
  example: figma.swift`LemonadeUi.Tabs(
    tabs: [${line(t1)}${line(t2)}${line(t3)}${line(t4)}${line(t5)}${line(t6)}${line(t7)}${line(t8)}${line(t9)}
    ],
    selectedIndex: ${selectedIndex},
    onTabSelected: { _ in },
    itemsSize: .${itemsSize}
)`,
  id: 'tabs',
  metadata: { nestable: true },
}
