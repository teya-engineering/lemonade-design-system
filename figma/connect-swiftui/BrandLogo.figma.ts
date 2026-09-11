// url=<LEMONADE_COMPONENTS>?node-id=2210-700
// source=swiftui/Sources/Lemonade/Components/LemonadeBrandLogo.swift
// component=BrandLogo
import figma from 'figma'

const instance = figma.selectedInstance

const size = instance.getEnum('Size', {
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
})

// A brand's light and dark components resolve to the same enum entry: the
// artwork comes from the theme, not the node.
const logo = instance.getInstanceSwap('🧩 Brand')
let logoCode
if (logo && logo.type === 'INSTANCE') {
  logoCode = logo.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.BrandLogo(${logoCode ? figma.swift`
    logo: ${logoCode},` : ''}
    size: .${size}
)`,
  id: 'brand-logo',
  metadata: { nestable: true },
}
