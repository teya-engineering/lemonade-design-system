// url=<LEMONADE_COMPONENTS>?node-id=22506-20491
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ProgressStepper.kt
// component=ProgressStepper
import figma from 'figma'
import { renderer } from '../shared/render'
import { progressStepperData } from '../shared/progress-stepper'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)
const { completedSteps, items } = progressStepperData(instance)

const rows = (items ?? []).map(({ label, subheading }) => {
  const args = [
    label !== undefined ? `label = "${quote(label)}"` : null,
    subheading !== undefined ? `subheading = "${quote(subheading)}"` : null,
  ].filter(Boolean)
  return `
        ProgressStepperItem(${args.join(', ')}),`
})
const itemsCode = items ? `listOf(${rows.join('')}
    )` : 'List(size = 3) { ProgressStepperItem() }'

export default {
  example: figma.kotlin`LemonadeUi.ProgressStepper(
    items = ${itemsCode},
    completedSteps = ${completedSteps},
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.ProgressStepper',
    'import com.teya.lemonade.ProgressStepperItem',
  ],
  id: 'progress-stepper',
  metadata: { nestable: true },
}
