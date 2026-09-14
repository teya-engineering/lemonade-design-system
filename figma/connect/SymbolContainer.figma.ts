// url=<LEMONADE_COMPONENTS>?node-id=2210-520
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SymbolContainer.kt
// component=SymbolContainer
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports } = renderer(instance, figma.kotlin)

// Figma calls the amber voice "Caution"; the enum calls it Warning.
const voice = instance.getEnum('◇ Voice', {
  Neutral: 'Neutral',
  Critical: 'Critical',
  Caution: 'Warning',
  Info: 'Info',
  Positive: 'Positive',
  Brand: 'Brand',
  'Brand Subtle': 'BrandSubtle',
})

const size = instance.getEnum('↕ Size', {
  XSmall: 'XSmall',
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
})

const shape = instance.getEnum('◇ Shape', { Rounded: 'Rounded', Circular: 'Circle' })

const contentType = instance.getEnum('◇ Content Type', {
  Icon: 'icon',
  Text: 'text',
  'Brand Logo': 'brand',
  Image: 'image',
})

const swap = (name) => {
  const node = instance.getInstanceSwap(name)
  return node && node.type === 'INSTANCE' ? node.executeTemplate().example : undefined
}
const iconCode = contentType === 'icon' ? swap('↪ 🧩 Icon') : undefined
const brandCode = contentType === 'brand' ? swap('↪ 🧩 Brand') : undefined

const textLayer = contentType === 'text' ? instance.findText('A') : undefined
const text = textLayer && textLayer.type === 'TEXT' ? textLayer.textContent : ''

const badge = instance.getBoolean('◉ Show Accessory')

const tail = `
    voice = SymbolContainerVoice.${voice},
    size = SymbolContainerSize.${size},
    shape = SymbolContainerShape.${shape},`

const content = contentType === 'brand'
  ? brandCode ? figma.kotlin`LemonadeUi.BrandLogo(logo = ${brandCode}, size = LemonadeAssetSize.Medium)` : ''
  : '/* TODO: your image */'

const badgeArg = badge ? figma.kotlin`
    badgeSlot = ${slot('↪ 🧩 Accessory', 'accessory')},` : ''

export default {
  example:
    contentType === 'icon'
      ? figma.kotlin`LemonadeUi.SymbolContainer(${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}
    contentDescription = null,${tail}${badgeArg}
)`
      : contentType === 'text'
        ? figma.kotlin`LemonadeUi.SymbolContainer(
    text = "${text}",${tail}${badgeArg}
)`
        : figma.kotlin`LemonadeUi.SymbolContainer(
    contentSlot = { ${content} },${tail}${badgeArg}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SymbolContainer',
    'import com.teya.lemonade.core.SymbolContainerShape',
    'import com.teya.lemonade.core.SymbolContainerSize',
    'import com.teya.lemonade.core.SymbolContainerVoice',
    ...slotImports,
    ...(iconCode ? ['import com.teya.lemonade.core.LemonadeIcons'] : []),
    ...(brandCode ? ['import com.teya.lemonade.BrandLogo', 'import com.teya.lemonade.core.LemonadeAssetSize', 'import com.teya.lemonade.core.LemonadeBrandLogos'] : []),
  ],
  id: 'symbol-container',
  metadata: { nestable: true },
}
