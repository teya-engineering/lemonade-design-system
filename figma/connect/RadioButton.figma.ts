// url=<LEMONADE_COMPONENTS>?node-id=2259-5307
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/RadioButton.kt
// component=RadioButton
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const checked = instance.getEnum('◉ Is Checked', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const standalone = instance.getEnum('◇ Layout', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.kotlin`LemonadeUi.RadioButton(
    checked = ${checked},
    onRadioButtonClicked = { },${disabled ? `
    enabled = false,` : ''}
)`
    : figma.kotlin`LemonadeUi.RadioButton(
    checked = ${checked},
    onRadioButtonClicked = { },
    label = "${quote(label)}",${supportText ? `
    supportText = "${quote(supportText)}",` : ''}${disabled ? `
    enabled = false,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.RadioButton',
  ],
  id: 'radio-button',
  metadata: { nestable: true },
}
