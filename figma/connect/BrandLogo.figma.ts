// url=<LEMONADE_COMPONENTS>?node-id=2210-700
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/BrandLogo.kt
// component=BrandLogo
import figma from 'figma'

const instance = figma.selectedInstance

const size = instance.getEnum('Size', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
})

// The swapped node may be either a brand's light or its dark component; both
// resolve to the same enum entry, since BrandLogo picks the artwork from the
// theme rather than from the node.
const logo = instance.getInstanceSwap('🧩 Brand')
let logoCode
if (logo && logo.type === 'INSTANCE') {
  logoCode = logo.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.BrandLogo(${logoCode ? figma.kotlin`
    logo = ${logoCode},` : ''}
    size = LemonadeAssetSize.${size},
)`,
  imports: [
    'import com.teya.lemonade.BrandLogo',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeAssetSize',
    'import com.teya.lemonade.core.LemonadeBrandLogos',
  ],
  id: 'brand-logo',
  metadata: { nestable: true },
}
