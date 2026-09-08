// url=<LEMONADE_COMPONENTS>?node-id=2258-4791
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SearchField.kt
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

export default {
  example: figma.kotlin`LemonadeUi.SearchField(
    input = "${input}",
    onInputChanged = { },${placeholder ? `
    placeholder = "${placeholder}",` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SearchField',
  ],
  id: 'search-field',
  metadata: { nestable: true },
}
