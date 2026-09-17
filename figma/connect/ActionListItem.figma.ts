// url=<LEMONADE_COMPONENTS>?node-id=13212-12463
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ActionListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports, quote } = renderer(instance, figma.kotlin)

const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const topLabel = instance.getBoolean('◉ Show Top Label') ? read('Top label') : undefined
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

const voice = instance.getEnum('◇ Voice', { Neutral: 'Neutral', Critical: 'Critical' })

const navigationIndicator = instance.getBoolean('◉ Navigation Indicator')
const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
export default {
  example: figma.kotlin`LemonadeUi.ActionListItem(
    label = "${quote(label)}",${topLabel ? `
    topLabel = "${quote(topLabel)}",` : ''}${supportText ? `
    supportText = "${quote(supportText)}",` : ''}
    onItemClicked = { },${voice && voice !== 'Neutral' ? `
    voice = LemonadeListItemVoice.${voice},` : ''}${navigationIndicator ? `
    showNavigationIndicator = true,` : ''}${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading Slot', 'leading content')},` : ''}${trailing ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing', 'trailing content')},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.ActionListItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeListItemVoice',
    ...slotImports,
  ],
  id: 'action-list-item',
  metadata: { nestable: true },
}
