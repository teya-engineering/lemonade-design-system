// url=<LEMONADE_COMPONENTS>?node-id=11090-33687
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
// component=LemonadeHistoryTimelineItem
import figma from 'figma'

// A row's text lives in a nested content instance and its voice on a nested
// indicator, so both are reached from here.
const instance = figma.selectedInstance

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
  example: figma.swift`LemonadeHistoryTimelineItem(label: "${label}"${subheading ? `, subheading: "${subheading}"` : ''}${description ? `, description: "${description}"` : ''}${voice && voice !== 'neutral' ? `, voice: .${voice}` : ''})`,
  id: 'history-item',
  metadata: { nestable: true, props: { current: current } },
}
