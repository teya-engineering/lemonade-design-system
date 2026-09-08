// url=<LEMONADE_COMPONENTS>?node-id=6529-8927
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/CountryFlag.kt
// component=CountryFlag
import figma from 'figma'

const instance = figma.selectedInstance

const size = instance.getEnum('Size', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
  '3XLarge': 'XXXLarge',
})

const shape = instance.getEnum('◇ Shape', {
  Circular: 'Circular',
  Rounded: 'Rounded',
})

const flag = instance.getInstanceSwap('◇ Flag')
let flagCode
if (flag && flag.type === 'INSTANCE') {
  flagCode = flag.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.CountryFlag(${flagCode ? figma.kotlin`
    flag = ${flagCode},` : ''}
    size = LemonadeAssetSize.${size},
    shape = CountryFlagShape.${shape},
)`,
  imports: [
    'import com.teya.lemonade.CountryFlag',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.CountryFlagShape',
    'import com.teya.lemonade.core.LemonadeAssetSize',
    'import com.teya.lemonade.core.LemonadeCountryFlags',
  ],
  id: 'country-flag',
  metadata: { nestable: true },
}
