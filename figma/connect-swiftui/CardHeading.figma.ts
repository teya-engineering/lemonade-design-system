// url=<LEMONADE_COMPONENTS>?node-id=15115-987
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=CardHeaderConfig
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, quote } = renderer(instance, figma.swift)
const title = instance.getString('✍️ Title')
const subtitle = instance.getBoolean('◉ Show Subtitle', {
  true: instance.getString('↪ ✍️ Subtitle'),
  false: undefined,
})
const style = instance.getEnum('◇ Heading Style', {
  Default: 'default',
  Overline: 'overline',
})
const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const navigation = instance.getBoolean('◉ Show Navigation Indicator')

export default {
  example: figma.swift`CardHeaderConfig(
    title: "${quote(title)}"${subtitle ? `,
    subtitle: "${quote(subtitle)}"` : ''}${style && style !== 'default' ? `,
    headingStyle: .${style}` : ''}${leading ? figma.swift`,
    leadingSlot: ${slot('↪ 🧩 Leading', 'leading content')}` : ''}${trailing ? figma.swift`,
    trailingSlot: ${slot('↪ 🧩 Trailing', 'trailing content')}` : ''}${navigation ? `,
    showNavigationIndicator: true` : ''}
)`,
  id: 'card-heading',
  metadata: { nestable: true },
}
