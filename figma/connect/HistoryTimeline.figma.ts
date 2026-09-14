// url=<LEMONADE_COMPONENTS>?node-id=11090-33664
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/HistoryTimeline.kt
// component=HistoryTimeline
import figma from 'figma'

const instance = figma.selectedInstance

// Rows share one layer name, so they are collected by name rather than index.
// Current is a row property in Figma and an index here, so rows surface it
// through metadata.props.
const rows = instance
  .findLayers((node) => node.name === '.History Item')
  .filter((node) => node.type === 'INSTANCE')

let items = figma.kotlin``
let count = 0
let currentIndex = 0
for (const row of rows) {
  const result = row.executeTemplate()
  if (result.metadata?.props?.current === 'true') currentIndex = count
  items = figma.kotlin`${items}
        ${result.example},`
  count += 1
}

export default {
  example: figma.kotlin`LemonadeUi.HistoryTimeline(
    items = listOf(${items}
    ),
    currentIndex = ${currentIndex},
)`,
  imports: ['import com.teya.lemonade.HistoryTimeline', 'import com.teya.lemonade.LemonadeUi', 'import com.teya.lemonade.core.HistoryTimelineItem'],
  id: 'history-timeline',
  metadata: { nestable: false },
}
