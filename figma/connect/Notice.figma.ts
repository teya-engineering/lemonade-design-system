// url=<LEMONADE_COMPONENTS>?node-id=6556-430
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Notice.kt
// component=Notice
import figma from 'figma'

const instance = figma.selectedInstance

const content = instance.getString('✍️ Content')

const voice = instance.getEnum('◇ Voice', {
  Info: 'Info',
  Positive: 'Positive',
  Warning: 'Warning',
  Critical: 'Critical',
  Neutral: 'Neutral',
})

// The variant axis is what actually decides whether a title renders; the
// Show Heading boolean toggles the same layer.
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
  example: figma.kotlin`LemonadeUi.Notice(
    content = "${content}",
    voice = NoticeVoice.${voice},${title ? `
    title = "${title}",` : ''}${showIcon ? '' : `
    showIcon = false,`}${actionLabel ? `
    actionLabel = "${actionLabel}",
    onActionClick = { },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Notice',
    'import com.teya.lemonade.core.NoticeVoice',
  ],
  id: 'notice',
  metadata: { nestable: true },
}
