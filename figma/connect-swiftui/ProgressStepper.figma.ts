// url=<LEMONADE_COMPONENTS>?node-id=22506-20491
// source=swiftui/Sources/Lemonade/Components/LemonadeProgressStepper.swift
// component=ProgressStepper
import figma from 'figma'
import { renderer } from '../shared/render'
import { progressStepperData } from '../shared/progress-stepper'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)
const { completedSteps, items } = progressStepperData(instance)

const rows = (items ?? []).map(({ label, subheading }) => {
  const args = [
    label !== undefined ? `label: "${quote(label)}"` : null,
    subheading !== undefined ? `subheading: "${quote(subheading)}"` : null,
  ].filter(Boolean)
  return `
        LemonadeProgressStepperItem(${args.join(', ')})`
})
const itemsCode = items ? `[${rows.join(',')}
    ]` : 'Array(repeating: LemonadeProgressStepperItem(), count: 3)'

export default {
  example: figma.swift`LemonadeUi.ProgressStepper(
    items: ${itemsCode},
    completedSteps: ${completedSteps}
)`,
  id: 'progress-stepper',
  metadata: { nestable: true },
}
