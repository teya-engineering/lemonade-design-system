// url=<LEMONADE_COMPONENTS>?node-id=21779-772
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SymbolContainer.kt
// component=SymbolContainer
import figma from 'figma'
import { renderer } from '../shared/render'
import { themeMap } from '../shared/themed'

const instance = figma.selectedInstance
const { slot, imports: slotImports, quote } = renderer(instance, figma.kotlin)

const theme = instance.getEnum('◇ Theme', themeMap((words) => `ThemedHue.${words.join('')}`))

const size = instance.getEnum('↕ Size', {
  XSmall: 'XSmall',
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
})

const shape = instance.getEnum('◇ Shape', { Rounded: 'Rounded', Circular: 'Circle' })

const contentType = instance.getEnum('◇ Content Type', { Icon: 'icon', Text: 'text' })

const iconSwap = contentType === 'icon' ? instance.getInstanceSwap('↪ 🧩 Icon') : undefined
const iconCode = iconSwap && iconSwap.type === 'INSTANCE' ? iconSwap.executeTemplate().example : undefined

const textLayer = contentType === 'text' ? instance.findText('A') : undefined
const text = textLayer && textLayer.type === 'TEXT' ? textLayer.textContent : ''

const badge = instance.getBoolean('◉ Show Accessory')

const tail = `
    theme = ${theme},
    size = SymbolContainerSize.${size},
    shape = SymbolContainerShape.${shape},`

const optIn = '// NOTE: experimental; the caller opts in with @OptIn(ExperimentalLemonadeApi::class)'

const badgeArg = badge ? figma.kotlin`
    badgeSlot = ${slot('↪ 🧩 Accessory', 'accessory')},` : ''

export default {
  example:
    contentType === 'text'
      ? figma.kotlin`${optIn}
LemonadeUi.SymbolContainer(
    text = "${quote(text)}",${tail}${badgeArg}
)`
      : figma.kotlin`${optIn}
LemonadeUi.SymbolContainer(
    icon = ${iconCode ?? 'LemonadeIcons.Heart'},
    contentDescription = null,${tail}${badgeArg}
)${iconCode ? '' : `
// NOTE: the design's icon did not resolve; set the entry it uses`}`,
  imports: [
    'import com.teya.lemonade.core.LemonadeIcons',
    'import com.teya.lemonade.ExperimentalLemonadeApi',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SymbolContainer',
    'import com.teya.lemonade.ThemedHue',
    'import com.teya.lemonade.core.SymbolContainerShape',
    'import com.teya.lemonade.core.SymbolContainerSize',
    ...slotImports,
  ],
  id: 'symbol-container-themed',
  metadata: { nestable: true },
}
