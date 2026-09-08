// url=<LEMONADE_COMPONENTS>?node-id=2258-4791
// source=swiftui/Sources/Lemonade/Components/LemonadeSearchField.swift
// component=SearchField
import figma from 'figma'

const instance = figma.selectedInstance

// Neither the query nor the placeholder is a component property; they are text
// layers, and which one is visible depends on Is Filled.
const filled = instance.getEnum('◉ Is Filled', { True: true, False: false })
const valueLayer = instance.findText('Value')
const placeholderLayer = instance.findText('Placeholder')
const input = filled && valueLayer && valueLayer.type === 'TEXT' ? valueLayer.textContent : ''
const placeholder =
  placeholderLayer && placeholderLayer.type === 'TEXT' ? placeholderLayer.textContent : undefined

// `input` is a Binding. .constant keeps the designed text visible and compiles
// as-is; swap it for a real @State binding when wiring the screen up.
export default {
  example: figma.swift`LemonadeUi.SearchField(
    input: .constant("${input}")${placeholder ? `,
    placeholder: "${placeholder}"` : ''}
)`,
  id: 'search-field',
  metadata: { nestable: true },
}
