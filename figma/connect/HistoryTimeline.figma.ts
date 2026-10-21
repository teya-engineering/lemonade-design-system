// url=<LEMONADE_COMPONENTS>?node-id=22114-6120
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/HistoryTimeline.kt
// component=HistoryTimeline
import figma from 'figma'
import { stepsCurrentIndex } from '../shared/history'

const instance = figma.selectedInstance
const variant = instance.getEnum('◇ Variant', { Events: 'Events', Steps: 'Steps' })

// Current is a row property in Figma and an index here, so rows surface their
// state through metadata.props.
const slot = instance.getSlot('Items Slot')
const rows = (slot && slot.connectedInstances ? slot.connectedInstances : []).filter(
  (row) => row.codeConnectId() !== null,
)

let items = figma.kotlin``
const steps = []
let eventsCurrentIndex = 0
for (const row of rows) {
  const result = row.executeTemplate()
  const props = result.metadata?.props
  if (props?.current === 'true') eventsCurrentIndex = steps.length
  steps.push(props?.step)
  items = figma.kotlin`${items}
        ${result.example},`
}

const { currentIndex, unmatched } = variant === 'Steps'
  ? stepsCurrentIndex(steps)
  : { currentIndex: eventsCurrentIndex, unmatched: false }

export default {
  example: figma.kotlin`${unmatched ? `// NOTE: the design has no current step; the first upcoming one is shown as current
` : ''}LemonadeUi.HistoryTimeline(
    items = listOf(${items}
    ),
    currentIndex = ${currentIndex === null ? 'null' : `${currentIndex}`},${variant === 'Steps' ? `
    variant = HistoryTimelineVariant.Steps,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.HistoryTimeline',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.HistoryTimelineItem',
    ...(variant === 'Steps' ? ['import com.teya.lemonade.core.HistoryTimelineVariant'] : []),
  ],
  id: 'history-timeline',
  metadata: { nestable: false },
}
