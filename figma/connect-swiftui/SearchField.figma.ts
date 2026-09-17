// url=<LEMONADE_COMPONENTS>?node-id=2258-4791
// source=swiftui/Sources/Lemonade/Components/LemonadeSearchField.swift
// component=SearchField
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

const filled = instance.getEnum('◉ Is Filled', { True: true, False: false })
const valueLayer = instance.findText('Value')
const placeholderLayer = instance.findText('Placeholder')
const input = filled && valueLayer && valueLayer.type === 'TEXT' ? valueLayer.textContent : ''
const placeholder =
  placeholderLayer && placeholderLayer.type === 'TEXT' ? placeholderLayer.textContent : undefined

// `input` is a Binding; .constant keeps the designed text and compiles as-is.
export default {
  example: figma.swift`LemonadeUi.SearchField(
    input: .constant("${quote(input)}")${placeholder ? `,
    placeholder: "${quote(placeholder)}"` : ''}
)`,
  id: 'search-field',
  metadata: { nestable: true },
}
