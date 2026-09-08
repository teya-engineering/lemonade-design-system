// url=<LEMONADE_COMPONENTS>?node-id=7851-17667
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SelectField.kt
// component=SelectField
import figma from 'figma'

const instance = figma.selectedInstance

const filled = instance.getEnum('◉ Is Filled', { True: true, False: false })
const selectedValue = filled ? instance.getString('✍️ Value') : undefined

const placeholder = instance.getString('✍️ Placeholder')
const hasError = instance.getEnum('◉ Has Error', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const optional = instance.getBoolean('◉ Is Optional')

const label = instance.getBoolean('◉ Show Label') ? instance.getString('↪ ✍️ Label') : undefined
const supportText = instance.getBoolean('◉ Show Footer')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined
const errorMessage = hasError ? instance.getString('↪ ✍️ Error Message') : undefined

// The set carries a separate leading slot per device; pick the one matching the variant.
const desktop = instance.getEnum('📱 Device', { Desktop: true, Mobile: false })
const leading = instance.getBoolean('◉ Show Leading')
  ? instance.getInstanceSwap(desktop ? '↪ 🖥️ Leading Item' : '↪ 📱 Leading Item')
  : null
let leadingCode
if (leading && leading.type === 'INSTANCE') {
  leadingCode = leading.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.SelectField(
    onClick = { },
    selectedValue = ${selectedValue ? `"${selectedValue}"` : 'null'},${label ? `
    label = "${label}",` : ''}
    placeholderText = "${placeholder}",${supportText ? `
    supportText = "${supportText}",` : ''}${errorMessage ? `
    errorMessage = "${errorMessage}",` : ''}${hasError ? `
    error = true,` : ''}${optional ? `
    optionalIndicator = "Optional",` : ''}${disabled ? `
    enabled = false,` : ''}${
      leadingCode ? figma.kotlin`
    leadingContent = { LemonadeUi.Icon(icon = ${leadingCode}, contentDescription = null) },` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.Icon',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SelectField',
    'import com.teya.lemonade.core.LemonadeIcons',
  ],
  id: 'select-field',
  metadata: { nestable: true },
}
