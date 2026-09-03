// url=<LEMONADE_COMPONENTS>?node-id=7851-17667
// source=swiftui/Sources/Lemonade/Components/LemonadeSelectField.swift
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

const desktop = instance.getEnum('📱 Device', { Desktop: true, Mobile: false })
const leading = instance.getBoolean('◉ Show Leading')
  ? instance.getInstanceSwap(desktop ? '↪ 🖥️ Leading Item' : '↪ 📱 Leading Item')
  : null
let leadingCode
if (leading && leading.type === 'INSTANCE') {
  leadingCode = leading.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.SelectField(
    onClick: { },
    selectedValue: ${selectedValue ? `"${selectedValue}"` : 'nil'},
    placeholderText: "${placeholder}"${label ? `,
    label: "${label}"` : ''}${optional ? `,
    optionalIndicator: "Optional"` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''}${errorMessage ? `,
    errorMessage: "${errorMessage}"` : ''}${hasError ? `,
    error: true` : ''}${disabled ? `,
    enabled: false` : ''}${
      leadingCode ? figma.swift`,
    leadingContent: { LemonadeUi.Icon(icon: ${leadingCode}, contentDescription: nil) }` : ''
    }
)`,
  id: 'select-field',
  metadata: { nestable: true },
}
