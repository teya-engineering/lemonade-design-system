// url=<LEMONADE_COMPONENTS>?node-id=11090-33609
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
// component=HistoryItemVoice
import figma from 'figma'

// Connected so the row can read the voice and current flag, which a parent
// cannot read off a child directly.
const instance = figma.selectedInstance

const voice = instance.getEnum('◇ Voice', { Neutral: 'neutral', Positive: 'positive' })
const current = instance.getEnum('◉ Is Current', { True: 'true', False: 'false' })

export default {
  example: figma.swift`LemonadeHistoryItemVoice.${voice}`,
  id: 'history-item-indicator',
  metadata: { nestable: true, props: { voice: voice, current: current } },
}
