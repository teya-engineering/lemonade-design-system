// url=<LEMONADE_COMPONENTS>?node-id=11090-33687
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/HistoryTimeline.kt
// component=HistoryTimelineItem
import figma from 'figma'
import { renderer } from '../shared/render'

// A row's text lives in a nested content instance and its voice on a nested
// indicator, so both are reached from here.
const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const read = (layer) => {
  const node = instance.findText(layer, { traverseInstances: true })
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const subheading = read('Subheading')
const description = read('Description')

const indicator = instance.findInstance('.History Item Indicator')
let voice
let current = 'false'
if (indicator && indicator.type === 'INSTANCE') {
  const props = indicator.executeTemplate().metadata?.props
  voice = props?.voice
  current = props?.current ?? 'false'
}

export default {
  example: figma.kotlin`HistoryTimelineItem(label = "${quote(label)}"${subheading ? `, subheading = "${quote(subheading)}"` : ''}${description ? `, description = "${quote(description)}"` : ''}${voice && voice !== 'Neutral' ? `, voice = HistoryItemVoice.${voice}` : ''})`,
  imports: [
    'import com.teya.lemonade.core.HistoryItemVoice',
    'import com.teya.lemonade.HistoryTimelineItem',
  ],
  id: 'history-item',
  metadata: { nestable: true, props: { current: current } },
}
