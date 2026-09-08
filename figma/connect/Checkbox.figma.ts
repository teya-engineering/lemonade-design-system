// url=<LEMONADE_COMPONENTS>?node-id=2259-5432
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Checkbox.kt
// component=Checkbox
import figma from 'figma'

const instance = figma.selectedInstance

const status = instance.getEnum('◇ Status', {
  Selected: 'Checked',
  Unselected: 'Unchecked',
  Indeterminate: 'Indeterminate',
})

const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

// "Standalone" is the bare box; "Content" is the labelled overload.
const standalone = instance.getEnum('◇ Layout', { Standalone: true, Content: false })

const label = instance.getString('↪ ✍️ Label')
const supportText = instance.getBoolean('↪ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

export default {
  example: standalone
    ? figma.kotlin`LemonadeUi.Checkbox(
    status = CheckboxStatus.${status},
    onCheckboxClicked = { },${disabled ? `
    enabled = false,` : ''}
)`
    : figma.kotlin`LemonadeUi.Checkbox(
    status = CheckboxStatus.${status},
    onCheckboxClicked = { },
    label = "${label}",${supportText ? `
    supportText = "${supportText}",` : ''}${disabled ? `
    enabled = false,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.Checkbox',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.CheckboxStatus',
  ],
  id: 'checkbox',
  metadata: { nestable: true },
}
