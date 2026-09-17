// url=<LEMONADE_COMPONENTS>?node-id=6556-430
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Notice.kt
// component=Notice
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const content = instance.getString('✍️ Content')

const voice = instance.getEnum('◇ Voice', {
  Info: 'Info',
  Positive: 'Positive',
  Warning: 'Warning',
  Critical: 'Critical',
  Neutral: 'Neutral',
})

// Two properties gate the title; the variant axis is the authoritative one.
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
    content = "${quote(content)}",
    voice = NoticeVoice.${voice},${title ? `
    title = "${quote(title)}",` : ''}${showIcon ? '' : `
    showIcon = false,`}${actionLabel ? `
    actionLabel = "${quote(actionLabel)}",
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
