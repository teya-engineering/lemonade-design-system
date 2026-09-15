// url=<LEMONADE_COMPONENTS>?node-id=6529-8927
// source=swiftui/Sources/Lemonade/Components/LemonadeCountryFlag.swift
// component=CountryFlag
import figma from 'figma'

const instance = figma.selectedInstance

// Neither side offers an xSmall here.
const size = instance.getEnum('Size', {
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
  '3XLarge': 'xxxLarge',
})

const shape = instance.getEnum('◇ Shape', {
  Circular: 'circular',
  Rounded: 'rounded',
})

const flag = instance.getInstanceSwap('◇ Flag')
let flagCode
if (flag && flag.type === 'INSTANCE') {
  flagCode = flag.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.CountryFlag(${flagCode ? figma.swift`
    flag: ${flagCode},` : ''}
    size: .${size},
    shape: .${shape}
)`,
  id: 'country-flag',
  metadata: { nestable: true },
}
