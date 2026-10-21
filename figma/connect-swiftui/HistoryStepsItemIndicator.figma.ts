// url=<LEMONADE_COMPONENTS>?node-id=22109-4080
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
// component=HistoryStepsItemIndicator
import figma from 'figma'

// Connected so the row can read the step state, which a parent cannot read off
// a child directly.
const instance = figma.selectedInstance

const step = instance.getEnum('◇ Variant', { Completed: 'Completed', Current: 'Current', Error: 'Error', Next: 'Next' })

export default {
  example: figma.swift`/* ${step} step */`,
  id: 'history-steps-item-indicator',
  metadata: { nestable: true, props: { step: step } },
}
