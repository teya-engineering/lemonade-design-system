// url=<LEMONADE_COMPONENTS>?node-id=4365-4446
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Link.kt
// component=Link
import figma from 'figma'

const instance = figma.selectedInstance

const text = instance.getString('✍️ Label')

const icon = instance.getBoolean('◉ Show Leading') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.Link(
    text = "${text}",
    onClick = { },${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Link',
  ],
  id: 'link',
  metadata: { nestable: true },
}
