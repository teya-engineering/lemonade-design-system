// url=<LEMONADE_COMPONENTS>?node-id=18130-69377
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ContentListItem.kt
// component=ContentListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports, quote } = renderer(instance, figma.kotlin)

const label = instance.getString('✍️ Label')
const value = instance.getString('✍️ Value')

const layout = instance.getEnum('◇ Layout', {
  Horizontal: 'Horizontal',
  Vertical: 'Vertical',
})

const density = instance.getEnum('◇ Density', {
  Comfortable: 'Comfortable',
  Compact: 'Compact',
})

const showDivider = instance.getEnum('◉ Show divider', { True: true, False: false })

const leadingSlot = instance.getBoolean('Show Leading')
const trailingSlot = instance.getBoolean('Show Trailing')
const contentSlot = instance.getBoolean('◉ Show Content Slot')
export default {
  example: figma.kotlin`LemonadeUi.ContentListItem(
    label = "${quote(label)}",
    value = "${quote(value)}",
    layout = LemonadeContentListItemLayout.${layout},
    density = LemonadeContentListItemDensity.${density},${showDivider ? `
    showDivider = true,` : ''}${
      leadingSlot ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading Slot', 'leading content')},` : ''
    }${
      trailingSlot ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing Slot', 'trailing content')},` : ''
    }${
      contentSlot ? figma.kotlin`
    contentSlot = ${slot('↪ 🧩 Content Slot', 'content')},` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.ContentListItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeContentListItemDensity',
    'import com.teya.lemonade.core.LemonadeContentListItemLayout',
    ...slotImports,
  ],
  id: 'content-list-item',
  metadata: { nestable: true },
}
