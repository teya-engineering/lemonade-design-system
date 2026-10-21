// url=<LEMONADE_COMPONENTS>?node-id=22114-6120
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
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

let items = figma.swift``
const steps = []
let eventsCurrentIndex = 0
for (const row of rows) {
  const result = row.executeTemplate()
  const props = result.metadata?.props
  if (props?.current === 'true') eventsCurrentIndex = steps.length
  steps.push(props?.step)
  items = figma.swift`${items}
        ${result.example},`
}

const { currentIndex, unmatched } = variant === 'Steps'
  ? stepsCurrentIndex(steps)
  : { currentIndex: eventsCurrentIndex, unmatched: false }

export default {
  example: figma.swift`${unmatched ? `// NOTE: the design has no current step; the first upcoming one is shown as current
` : ''}LemonadeUi.HistoryTimeline(
    items: [${items}
    ],
    currentIndex: ${currentIndex === null ? 'nil' : `${currentIndex}`}${variant === 'Steps' ? `,
    variant: .steps` : ''}
)`,
  id: 'history-timeline',
  metadata: { nestable: false },
}
