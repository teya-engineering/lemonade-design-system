// url=<LEMONADE_COMPONENTS>?node-id=22234-3422
// source=swiftui/Sources/Lemonade/Components/LemonadeTextArea.swift
// component=TextArea
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
const showFooter = instance.getBoolean('◉ Show Footer')
const supportText = showFooter && instance.getBoolean('↪ ◉ Show Support Text')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined
const errorMessage = hasError ? instance.getString('↪ ✍️ Error Message') : undefined

// The counter reads "0/1000": the number after the slash is the limit.
const counter = showFooter && instance.getBoolean('◉ Show Counter')
  ? instance.getString('↪ ✍️ Counter')
  : undefined
const maxLength = counter ? /\/\s*(\d+)/.exec(counter)?.[1] : undefined

// `input` is a Binding; .constant keeps the designed text and compiles as-is.
export default {
  example: figma.swift`${maxLength || !counter ? '' : `// TODO: maxLength — the counter "${quote(counter)}" has no limit to read
`}LemonadeUi.TextArea(
    input: .constant("${quote(input)}")${label ? `,
    label: "${quote(label)}"` : ''}${optional ? `,
    optionalIndicator: "Optional"` : ''}${supportText ? `,
    supportText: "${quote(supportText)}"` : ''},
    placeholderText: "${quote(placeholder)}"${errorMessage ? `,
    errorMessage: "${quote(errorMessage)}"` : ''}${maxLength ? `,
    maxLength: ${maxLength}` : ''}${hasError ? `,
    error: true` : ''}${disabled ? `,
    enabled: false` : ''}
)`,
  id: 'text-area',
  metadata: { nestable: true },
}
