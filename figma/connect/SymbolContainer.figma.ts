// url=<LEMONADE_COMPONENTS>?node-id=2210-520
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SymbolContainer.kt
// component=SymbolContainer
import figma from 'figma'

const instance = figma.selectedInstance

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
  ? instance.getSlot('↪ 🧩 Accessory')
  : undefined

const tail = `
    voice = SymbolContainerVoice.${voice},
    size = SymbolContainerSize.${size},
    shape = SymbolContainerShape.${shape},`

const badgeArg = badge ? figma.kotlin`
    badgeSlot = { /* accessory */ },` : ''

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
        : contentType === 'brand'
          ? figma.kotlin`LemonadeUi.SymbolContainer(
    contentSlot = { ${brandCode ? figma.kotlin`LemonadeUi.BrandLogo(logo = ${brandCode}, size = LemonadeAssetSize.Medium)` : ''} },${tail}${badgeArg}
)`
          : figma.kotlin`LemonadeUi.SymbolContainer(
    contentSlot = { /* TODO: your image */ },${tail}${badgeArg}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SymbolContainer',
    'import com.teya.lemonade.core.SymbolContainerShape',
    'import com.teya.lemonade.core.SymbolContainerSize',
    'import com.teya.lemonade.core.SymbolContainerVoice',
  ],
  id: 'symbol-container',
  metadata: { nestable: true },
}
