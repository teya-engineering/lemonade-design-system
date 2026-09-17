// url=<LEMONADE_COMPONENTS>?node-id=15153-702
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=CardFooterActionConfig
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const text = instance.findText('Action')
const label = text && text.type === 'TEXT' ? text.textContent : 'Action'

export default {
  example: figma.kotlin`CardFooterActionConfig(label = "${quote(label)}", onClick = { })`,
  imports: ['import com.teya.lemonade.CardFooterActionConfig'],
  id: 'card-footer-action',
  metadata: { nestable: true },
}
