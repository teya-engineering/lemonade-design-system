// url=<LEMONADE_COMPONENTS>?node-id=8122-18897
// source=kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Tabs.kt
// component=TabItem
import figma from 'figma'

// An internal Figma component is connected here because a tab has no component
// of its own: it is one entry in the parent's list.
const instance = figma.selectedInstance

const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const icon = instance.getBoolean('◉ Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

// Selection is a tab property in Figma and an index on the parent, so it is
// surfaced through metadata.props.
export default {
  example: figma.swift`LemonadeTabItem(label: "${label}"${iconCode ? figma.swift`, icon: ${iconCode}` : ''}${disabled ? ', isDisabled: true' : ''})`,

  id: 'tab-item',
  metadata: { nestable: true, props: { selected: selected ? 'true' : 'false' } },
}
