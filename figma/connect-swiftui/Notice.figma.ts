// url=<LEMONADE_COMPONENTS>?node-id=6556-430
// source=swiftui/Sources/Lemonade/Components/LemonadeNotice.swift
// component=Notice
import figma from 'figma'

const instance = figma.selectedInstance

const content = instance.getString('✍️ Content')

const voice = instance.getEnum('◇ Voice', {
  Info: 'info',
  Positive: 'positive',
  Warning: 'warning',
  Critical: 'critical',
  Neutral: 'neutral',
})

const withTitle = instance.getEnum('◇ Variant', {
  'Title + Description': true,
  'Description only': false,
})
const title = withTitle ? instance.getString('↪ ✍️ Title') : undefined

const showIcon = instance.getBoolean('◉ Show Icon')
const actionLabel = instance.getBoolean('◉ Show Action')
  ? instance.getString('↪ ✍️ Action Label')
  : undefined

export default {
  example: figma.swift`LemonadeUi.Notice(
    content: "${content}",
    voice: .${voice}${title ? `,
    title: "${title}"` : ''}${showIcon ? '' : `,
    showIcon: false`}${actionLabel ? `,
    actionLabel: "${actionLabel}",
    onActionClick: { }` : ''}
)`,
  id: 'notice',
  metadata: { nestable: true },
}
