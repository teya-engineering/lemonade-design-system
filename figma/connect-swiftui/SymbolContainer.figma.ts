// url=<LEMONADE_COMPONENTS>?node-id=2210-520
// source=swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift
// component=SymbolContainer
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets, slot } = renderer(instance, figma.swift)

// Figma calls the amber voice "Caution"; the enum calls it warning.
const voice = instance.getEnum('◇ Voice', {
  Neutral: 'neutral',
  Critical: 'critical',
  Caution: 'warning',
  Info: 'info',
  Positive: 'positive',
  Brand: 'brand',
  'Brand Subtle': 'brandSubtle',
})

const size = instance.getEnum('↕ Size', {
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
})

const shape = instance.getEnum('◇ Shape', { Rounded: 'rounded', Circular: 'circle' })

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

const tail = `,
    voice: .${voice},
    size: .${size},
    shape: .${shape}`

// When content is the trailing closure, badgeSlot has to be a labelled argument.
const contentClosure = contentType !== 'icon' && contentType !== 'text'
const badgeArg = badge && !contentClosure ? figma.swift` {${snippets('↪ 🧩 Accessory', '    ') ?? `
    /* accessory */`}
}` : ''
const badgeSlotArg = badge && contentClosure ? figma.swift`,
    badgeSlot: ${slot('↪ 🧩 Accessory', 'accessory')}` : ''

const content = contentType === 'brand'
  ? brandCode ? figma.swift`LemonadeUi.BrandLogo(logo: ${brandCode}, size: .medium)` : ''
  : '// TODO: your image'

export default {
  example:
    contentType === 'icon'
      ? figma.swift`LemonadeUi.SymbolContainer(${iconCode ? figma.swift`
    icon: ${iconCode},` : ''}
    contentDescription: nil${tail}
)${badgeArg}`
      : contentType === 'text'
        ? figma.swift`LemonadeUi.SymbolContainer(
    text: "${text}"${tail}
)${badgeArg}`
        : figma.swift`LemonadeUi.SymbolContainer(
    voice: .${voice},
    size: .${size},
    shape: .${shape}${badgeSlotArg}
) {
    ${content}
}`,
  id: 'symbol-container',
  metadata: { nestable: true },
}
