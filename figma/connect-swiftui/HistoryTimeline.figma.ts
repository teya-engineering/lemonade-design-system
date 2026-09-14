// url=<LEMONADE_COMPONENTS>?node-id=11090-33664
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
// component=HistoryTimeline
import figma from 'figma'

const instance = figma.selectedInstance

// Rows share one layer name, so they are collected by name rather than index.
// Current is a row property in Figma and an index here, so rows surface it
// through metadata.props.
const rows = instance
  .findLayers((node) => node.name === '.History Item')
  .filter((node) => node.type === 'INSTANCE')

let items = figma.swift``
let count = 0
let currentIndex = 0
for (const row of rows) {
  const result = row.executeTemplate()
  if (result.metadata?.props?.current === 'true') currentIndex = count
  items = figma.swift`${items}
        ${result.example},`
  count += 1
}

export default {
  example: figma.swift`LemonadeUi.HistoryTimeline(
    items: [${items}
    ],
    currentIndex: ${currentIndex}
)`,
  id: 'history-timeline',
  metadata: { nestable: false },
}
