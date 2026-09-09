// url=<LEMONADE_COMPONENTS>?node-id=8122-18897
// source=kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/Tabs.kt
// component=TabItem
import figma from 'figma'

// A tab has no component of its own — it is one entry in the parent's list.
// Connected so Tabs can carry the designer's real labels, icons and selection.
const instance = figma.selectedInstance

// The label is a plain text layer rather than a property.
const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const icon = instance.getBoolean('◉ Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

// Selection lives on the tab in Figma but is an index on the parent, so it is
// surfaced through metadata.props for Tabs to fold into selectedIndex.
export default {
  example: figma.kotlin`TabItem(label = "${label}"${iconCode ? figma.kotlin`, icon = ${iconCode}` : ''}${disabled ? ', isDisabled = true' : ''})`,
  imports: ['import com.teya.lemonade.core.TabItem'],
  id: 'tab-item',
  metadata: { nestable: true, props: { selected: selected ? 'true' : 'false' } },
}
