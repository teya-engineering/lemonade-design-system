// url=<LEMONADE_COMPONENTS>?node-id=5215-8657
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/TextField.kt
// component=TextField
import figma from 'figma'

const instance = figma.selectedInstance

// "Is Filled" is Figma's way of showing an empty vs. populated field.
const filled = instance.getEnum('◉ Is Filled', { True: true, False: false })
const input = filled ? instance.getString('✍️ Value') : ''

const placeholder = instance.getString('✍️ Placeholder')
const hasError = instance.getEnum('◉ Has Error', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const optional = instance.getBoolean('◉ Is Optional')

const label = instance.getBoolean('◉ Show Label') ? instance.getString('↪ ✍️ Label') : undefined
const supportText = instance.getBoolean('◉ Show Footer')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined
const errorMessage = hasError ? instance.getString('↪ ✍️ Error Message') : undefined

const leading = instance.getBoolean('◉ Show Leading') ? instance.getInstanceSwap('↪ Leading Item') : null
let leadingCode
if (leading && leading.type === 'INSTANCE') {
  leadingCode = leading.executeTemplate().example
}

const trailing = instance.getBoolean('◉ Show Trailing') ? instance.getInstanceSwap('↪ Trailing Item') : null
let trailingCode
if (trailing && trailing.type === 'INSTANCE') {
  trailingCode = trailing.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.TextField(
    input = "${input}",
    onInputChanged = { },${label ? `
    label = "${label}",` : ''}
    placeholderText = "${placeholder}",${supportText ? `
    supportText = "${supportText}",` : ''}${errorMessage ? `
    errorMessage = "${errorMessage}",` : ''}${hasError ? `
    error = true,` : ''}${optional ? `
    optionalIndicator = "Optional",` : ''}${disabled ? `
    enabled = false,` : ''}${
      leadingCode ? figma.kotlin`
    leadingContent = { LemonadeUi.Icon(icon = ${leadingCode}, contentDescription = null) },` : ''
    }${
      trailingCode ? figma.kotlin`
    trailingContent = { LemonadeUi.Icon(icon = ${trailingCode}, contentDescription = null) },` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.Icon',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.TextField',
    'import com.teya.lemonade.core.LemonadeIcons',
  ],
  id: 'text-field',
  metadata: { nestable: true },
}
