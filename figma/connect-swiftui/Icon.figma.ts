// url=<LEMONADE_COMPONENTS>?node-id=2195-83
// source=swiftui/Sources/Lemonade/Components/LemonadeIcon.swift
// component=Icon
import figma from 'figma'

const instance = figma.selectedInstance

// Figma writes the largest three as 2X/3X/4X; LemonadeUiIconSize repeats the x.
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
  example: figma.swift`LemonadeUi.Icon(${glyphCode ? figma.swift`
    icon: ${glyphCode},` : ''}
    contentDescription: nil,
    size: .${size}
)`,
  id: 'icon',
  metadata: { nestable: true },
}
