// url=<LEMONADE_COMPONENTS>?node-id=2195-83
// source=swiftui/Sources/Lemonade/Components/LemonadeIcon.swift
// component=Icon
import figma from 'figma'

const instance = figma.selectedInstance

// Figma writes 2X/3X/4X; LemonadeUiIconSize repeats the x.
const size = instance.getEnum('Size', {
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
  '3XLarge': 'xxxLarge',
  '4XLarge': 'xxxxLarge',
})

const glyph = instance.getInstanceSwap('🧩 Icon')
let glyphCode
if (glyph && glyph.type === 'INSTANCE') {
  glyphCode = glyph.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.Icon(
    icon: ${glyphCode ?? 'LemonadeIcon.heart'},
    contentDescription: nil,
    size: .${size}
)${glyphCode ? '' : `
// NOTE: the design's icon did not resolve; set the case it uses`}`,
  id: 'icon',
  metadata: { nestable: true },
}
