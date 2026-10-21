// url=<LEMONADE_COMPONENTS>?node-id=22109-5423
// source=swiftui/Sources/Lemonade/Components/LemonadeHistoryTimeline.swift
// component=LemonadeHistoryTimelineItem
import figma from 'figma'
import { renderer } from '../shared/render'

// A row's text lives in a nested content instance and its step state on a
// nested indicator. Only an Error step carries a voice in code.
const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

const read = (layer) => {
  const node = instance.findText(layer, { traverseInstances: true })
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const subheading = read('Subheading')
const description = read('Description')

const indicator = instance.findInstance('.History Steps Item Indicator')
const step = indicator && indicator.type === 'INSTANCE' ? indicator.executeTemplate().metadata?.props?.step : undefined

export default {
  example: figma.swift`LemonadeHistoryTimelineItem(label: "${quote(label)}"${subheading ? `, subheading: "${quote(subheading)}"` : ''}${description ? `, description: "${quote(description)}"` : ''}${step === 'Error' ? ', voice: .critical' : ''})`,
  id: 'history-steps-item',
  metadata: { nestable: true, props: { step: step } },
}
