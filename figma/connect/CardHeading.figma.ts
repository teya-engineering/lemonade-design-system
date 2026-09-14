// url=<LEMONADE_COMPONENTS>?node-id=15115-987
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=CardHeaderConfig
import figma from 'figma'

const instance = figma.selectedInstance

const title = instance.getString('✍️ Title')
const subtitle = instance.getBoolean('◉ Show Subtitle', {
  true: instance.getString('↪ ✍️ Subtitle'),
  false: undefined,
})
const style = instance.getEnum('◇ Heading Style', {
  Default: 'Default',
  Overline: 'Overline',
})
const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const navigation = instance.getBoolean('◉ Show Navigation Indicator')

export default {
  example: figma.kotlin`CardHeaderConfig(title = "${title}"${style !== 'Default' ? `, headingStyle = LemonadeCardHeadingStyle.${style}` : ''}${leading ? ', leadingSlot = { /* leading content */ }' : ''}${trailing ? ', trailingSlot = { /* trailing content */ }' : ''}${navigation ? ', showNavigationIndicator = true' : ''}${subtitle ? `, subtitle = "${subtitle}"` : ''})`,
  imports: [
    'import com.teya.lemonade.CardHeaderConfig',
    ...(style !== 'Default' ? ['import com.teya.lemonade.core.LemonadeCardHeadingStyle'] : []),
  ],
  id: 'card-heading',
  metadata: { nestable: true },
}
