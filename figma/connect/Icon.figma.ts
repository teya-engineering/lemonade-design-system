// url=<LEMONADE_COMPONENTS>?node-id=2195-83
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Icon.kt
// component=Icon
import figma from 'figma'

const instance = figma.selectedInstance

// Figma writes the largest three as 2X/3X/4X; LemonadeAssetSize repeats the X.
const size = instance.getEnum('Size', {
  XSmall: 'XSmall',
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
  '3XLarge': 'XXXLarge',
  '4XLarge': 'XXXXLarge',
})

// Resolves through the swapped glyph's own template, which emits a bare
// LemonadeIcons value.
const glyph = instance.getInstanceSwap('🧩 Icon')
let glyphCode
if (glyph && glyph.type === 'INSTANCE') {
  glyphCode = glyph.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.Icon(${glyphCode ? figma.kotlin`
    icon = ${glyphCode},` : ''}
    contentDescription = null,
    size = LemonadeAssetSize.${size},
)`,
  imports: [
    'import com.teya.lemonade.Icon',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeAssetSize',
    'import com.teya.lemonade.core.LemonadeIcons',
  ],
  id: 'icon',
  metadata: { nestable: true },
}
