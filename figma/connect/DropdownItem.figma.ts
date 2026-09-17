// url=<LEMONADE_COMPONENTS>?node-id=17681-581
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/Dropdown.kt
// component=DropdownItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const holder = instance.getBoolean('◉ Show Leading') ? instance.findInstance('Icon') : undefined
const glyph = holder && holder.type === 'INSTANCE' ? holder.getInstanceSwap('🧩 Icon') : undefined
const icon = glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
const trailing = instance.getBoolean('◉ Show Trailing')
const supportText = instance.getBoolean('◉ Show Support Text')

export default {
  example: figma.kotlin`LemonadeUi.DropdownItem(text = "${quote(instance.getString('✍️ Label'))}", onClick = { }${icon ? figma.kotlin`, leadingIcon = ${icon}` : ''}${trailing ? ', trailingSlot = { /* trailing content */ }' : ''})`,
  imports: [
    'import com.teya.lemonade.DropdownItem',
    'import com.teya.lemonade.LemonadeUi',
    ...(icon ? ['import com.teya.lemonade.core.LemonadeIcons'] : []),
  ],
  id: 'dropdown-item',
  metadata: { nestable: true, props: { supportText: supportText ? 'true' : 'false' } },
}
