// url=<LEMONADE_COMPONENTS>?node-id=11090-33609
// source=kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/HistoryTimeline.kt
// component=HistoryItemVoice
import figma from 'figma'

// The indicator has no component of its own in code — it is the voice of a
// timeline row, plus whether that row is the current one. Connected so
// .History Item can read both, which a parent cannot do directly.
const instance = figma.selectedInstance

const voice = instance.getEnum('◇ Voice', { Neutral: 'Neutral', Positive: 'Positive' })
const current = instance.getEnum('◉ Is Current', { True: 'true', False: 'false' })

export default {
  example: figma.kotlin`HistoryItemVoice.${voice}`,
  id: 'history-item-indicator',
  metadata: { nestable: true, props: { voice: voice, current: current } },
}
