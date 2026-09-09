// url=<LEMONADE_COMPONENTS>?node-id=11090-33664
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/HistoryTimeline.kt
// component=HistoryTimeline
import figma from 'figma'

const instance = figma.selectedInstance

// Rows are repeated instances of the same component, so they are collected by
// layer name rather than by an indexed name as Tabs does. Which row is current
// is a property of the row in Figma and an index on the parent, so each row
// surfaces it through metadata.props.
const rows = instance
  .findLayers((node) => node.name === '.History Item')
  .filter((node) => node.type === 'INSTANCE')

const items = []
let currentIndex = 0
for (const row of rows) {
  const result = row.executeTemplate()
  if (result.metadata?.props?.current === 'true') currentIndex = items.length
  items.push(result.example)
}

const [i1, i2, i3, i4, i5, i6] = items
const line = (i) => (i ? figma.kotlin`
        ${i},` : '')

export default {
  example: figma.kotlin`LemonadeUi.HistoryTimeline(
    items = listOf(${line(i1)}${line(i2)}${line(i3)}${line(i4)}${line(i5)}${line(i6)}
    ),
    currentIndex = ${currentIndex},
)`,
  imports: ['import com.teya.lemonade.HistoryTimeline', 'import com.teya.lemonade.LemonadeUi', 'import com.teya.lemonade.core.HistoryTimelineItem'],
  id: 'history-timeline',
  metadata: { nestable: false },
}
