// url=<LEMONADE_COMPONENTS>?node-id=5215-8657
// source=swiftui/Sources/Lemonade/Components/LemonadeTextField.swift
// component=TextField
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

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

// `input` is a Binding; .constant keeps the designed text and compiles as-is.
export default {
  example: figma.swift`LemonadeUi.TextField(
    input: .constant("${quote(input)}")${label ? `,
    label: "${quote(label)}"` : ''}${optional ? `,
    optionalIndicator: "Optional"` : ''}${supportText ? `,
    supportText: "${quote(supportText)}"` : ''},
    placeholderText: "${quote(placeholder)}"${errorMessage ? `,
    errorMessage: "${quote(errorMessage)}"` : ''}${hasError ? `,
    error: true` : ''}${disabled ? `,
    enabled: false` : ''}${
      leadingCode || trailingCode ? figma.swift`,
    leadingContent: { ${leadingCode ? figma.swift`LemonadeUi.Icon(icon: ${leadingCode}, contentDescription: nil)` : 'EmptyView()'} },
    trailingContent: { ${trailingCode ? figma.swift`LemonadeUi.Icon(icon: ${trailingCode}, contentDescription: nil)` : 'EmptyView()'} }` : ''
    }
)`,
  id: 'text-field',
  metadata: { nestable: true },
}
