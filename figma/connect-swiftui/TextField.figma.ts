// url=<LEMONADE_COMPONENTS>?node-id=5215-8657
// source=swiftui/Sources/Lemonade/Components/LemonadeTextField.swift
// component=TextField
import figma from 'figma'

const instance = figma.selectedInstance

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

// `input` is a Binding. .constant keeps the designed text visible and compiles
// as-is; swap it for a real @State binding when wiring the screen up.
export default {
  example: figma.swift`LemonadeUi.TextField(
    input: .constant("${input}")${label ? `,
    label: "${label}"` : ''}${optional ? `,
    optionalIndicator: "Optional"` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''},
    placeholderText: "${placeholder}"${errorMessage ? `,
    errorMessage: "${errorMessage}"` : ''}${hasError ? `,
    error: true` : ''}${disabled ? `,
    enabled: false` : ''}${
      leadingCode ? figma.swift`,
    leadingContent: { LemonadeUi.Icon(icon: ${leadingCode}, contentDescription: nil) }` : ''
    }${
      trailingCode ? figma.swift`,
    trailingContent: { LemonadeUi.Icon(icon: ${trailingCode}, contentDescription: nil) }` : ''
    }
)`,
  id: 'text-field',
  metadata: { nestable: true },
}
