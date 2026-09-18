// url=<LEMONADE_COMPONENTS>?node-id=21779-772
// source=swiftui/Sources/Lemonade/Components/LemonadeSymbolContainer.swift
// component=SymbolContainer
import figma from 'figma'
import { renderer } from '../shared/render'
import { themeMap } from '../shared/themed'

const instance = figma.selectedInstance
const { snippets, quote } = renderer(instance, figma.swift)

const theme = instance.getEnum('◇ Theme', themeMap(([first, ...rest]) => `.${first.toLowerCase()}${rest.join('')}`))

const size = instance.getEnum('↕ Size', {
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
})

const shape = instance.getEnum('◇ Shape', { Rounded: 'rounded', Circular: 'circle' })

const contentType = instance.getEnum('◇ Content Type', { Icon: 'icon', Text: 'text' })

const iconSwap = contentType === 'icon' ? instance.getInstanceSwap('↪ 🧩 Icon') : undefined
const iconCode = iconSwap && iconSwap.type === 'INSTANCE' ? iconSwap.executeTemplate().example : undefined

const textLayer = contentType === 'text' ? instance.findText('A') : undefined
const text = textLayer && textLayer.type === 'TEXT' ? textLayer.textContent : ''

const badge = instance.getBoolean('◉ Show Accessory')

const tail = `,
    theme: ${theme},
    size: .${size},
    shape: .${shape}`

const badgeArg = badge ? figma.swift` {${snippets('↪ 🧩 Accessory', '    ') ?? `
    /* accessory */`}
}` : ''

export default {
  example:
    contentType === 'text'
      ? figma.swift`LemonadeUi.SymbolContainer(
    text: "${quote(text)}"${tail}
)${badgeArg}`
      : figma.swift`LemonadeUi.SymbolContainer(
    icon: ${iconCode ?? 'LemonadeIcon.heart'},
    contentDescription: nil${tail}
)${badgeArg}${iconCode ? '' : `
// NOTE: the design's icon did not resolve; set the case it uses`}`,
  id: 'symbol-container-themed',
  metadata: { nestable: true },
}
