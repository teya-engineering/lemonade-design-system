// url=<LEMONADE_COMPONENTS>?node-id=11090-33609
// source=kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/HistoryTimeline.kt
// component=HistoryItemVoice
import figma from 'figma'

// Connected so the row can read the voice and current flag, which a parent
// cannot read off a child directly.
const instance = figma.selectedInstance

const voice = instance.getEnum('◇ Voice', { Neutral: 'Neutral', Positive: 'Positive' })
const current = instance.getEnum('◉ Is Current', { True: 'true', False: 'false' })

export default {
  example: figma.kotlin`HistoryItemVoice.${voice}`,
  id: 'history-item-indicator',
  metadata: { nestable: true, props: { voice: voice, current: current } },
}
