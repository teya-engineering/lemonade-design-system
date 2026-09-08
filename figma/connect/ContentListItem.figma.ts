// url=<LEMONADE_COMPONENTS>?node-id=18130-69377
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ContentListItem.kt
// component=ContentListItem
import figma from 'figma'

const instance = figma.selectedInstance

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
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined
const contentSlot = instance.getBoolean('◉ Show Content Slot')
  ? instance.getSlot('↪ 🧩 Content Slot')
  : undefined

export default {
  example: figma.kotlin`LemonadeUi.ContentListItem(
    label = "${label}",
    value = "${value}",
    layout = LemonadeContentListItemLayout.${layout},
    density = LemonadeContentListItemDensity.${density},${showDivider ? `
    showDivider = true,` : ''}${
      leadingSlot ? figma.kotlin`
    leadingSlot = { ${leadingSlot} },` : ''
    }${
      trailingSlot ? figma.kotlin`
    trailingSlot = { ${trailingSlot} },` : ''
    }${
      contentSlot ? figma.kotlin`
    contentSlot = { ${contentSlot} },` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.ContentListItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeContentListItemDensity',
    'import com.teya.lemonade.core.LemonadeContentListItemLayout',
  ],
  id: 'content-list-item',
  metadata: { nestable: true },
}
