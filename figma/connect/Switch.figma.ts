// url=<LEMONADE_COMPONENTS>?node-id=5144-128
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Switch.kt
// component=Switch
import figma from 'figma'

const instance = figma.selectedInstance

const checked = instance.getEnum('◉ Checked', { True: true, False: false })
const disabled = instance.getEnum('◇ Is Disabled', { True: true, False: false })
const standalone = instance.getEnum('☰ Variant', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ ◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.kotlin`LemonadeUi.Switch(
    checked = ${checked},
    onCheckedChange = { },${disabled ? `
    enabled = false,` : ''}
)`
    : figma.kotlin`LemonadeUi.Switch(
    checked = ${checked},
    onCheckedChange = { },
    label = "${label}",${supportText ? `
    supportText = "${supportText}",` : ''}${disabled ? `
    enabled = false,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Switch',
  ],
  id: 'switch',
  metadata: { nestable: true },
}
