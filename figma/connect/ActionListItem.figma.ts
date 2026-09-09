// url=<LEMONADE_COMPONENTS>?node-id=13212-12463
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ActionListItem
import figma from 'figma'

const instance = figma.selectedInstance

// All three strings are plain text layers rather than properties; the booleans
// only control their visibility.
const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const topLabel = instance.getBoolean('◉ Show Top Label') ? read('Top label') : undefined
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

// Figma calls the red voice "Danger"; the enum calls it Critical.
const voice = instance.getEnum('◇ Voice', { Neutral: 'Neutral', Danger: 'Critical' })

const navigationIndicator = instance.getBoolean('◉ Navigation Indicator')
const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')

export default {
  example: figma.kotlin`LemonadeUi.ActionListItem(
    label = "${label}",${topLabel ? `
    topLabel = "${topLabel}",` : ''}${supportText ? `
    supportText = "${supportText}",` : ''}
    onItemClicked = { },${voice !== 'Neutral' ? `
    voice = LemonadeListItemVoice.${voice},` : ''}${navigationIndicator ? `
    showNavigationIndicator = true,` : ''}${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? `
    leadingSlot = { /* leading content */ },` : ''}${trailing ? `
    trailingSlot = { /* trailing content */ },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.ActionListItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeListItemVoice',
  ],
  id: 'action-list-item',
  metadata: { nestable: true },
}
