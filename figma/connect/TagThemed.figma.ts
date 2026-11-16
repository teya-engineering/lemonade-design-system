// url=<LEMONADE_COMPONENTS>?node-id=22216-418
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tag.kt
// component=Tag
import figma from 'figma'
import { renderer } from '../shared/render'
import { kotlinThemes } from '../shared/themed'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const label = instance.getString('✍️ Label')

const theme = instance.getEnum('◇ Theme', kotlinThemes)

const icon = instance.getBoolean('Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
const iconCode = icon && icon.type === 'INSTANCE' ? icon.executeTemplate().example : undefined

export default {
  example: figma.kotlin`// NOTE: experimental; the caller opts in with @OptIn(ExperimentalLemonadeApi::class)
LemonadeUi.Tag(
    label = "${quote(label)}",
    theme = ${theme},${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.ExperimentalLemonadeApi',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tag',
    'import com.teya.lemonade.ThemedHue',
    ...(iconCode ? ['import com.teya.lemonade.core.LemonadeIcons'] : []),
  ],
  id: 'tag-themed',
  metadata: { nestable: true },
}
