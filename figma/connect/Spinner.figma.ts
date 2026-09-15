// url=<LEMONADE_COMPONENTS>?node-id=7132-127
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Spinner.kt
// component=Spinner
import figma from 'figma'

const instance = figma.selectedInstance

const size = instance.getEnum('Size', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': 'XXLarge',
})

export default {
  example: figma.kotlin`LemonadeUi.Spinner(
    size = LemonadeAssetSize.${size},
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Spinner',
    'import com.teya.lemonade.core.LemonadeAssetSize',
  ],
  id: 'spinner',
  metadata: { nestable: true },
}
