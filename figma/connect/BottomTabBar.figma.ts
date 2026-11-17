// url=<LEMONADE_COMPONENTS>?node-id=17947-25065
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/BottomTabBar.kt
// component=BottomTabBar
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote, slot, imports: slotImports } = renderer(instance, figma.kotlin)

// traverseInstances would list these last-to-first; the tabs are direct children.
const tabs = instance.findLayers(
  (node) => node.type === 'INSTANCE' && node.name === 'Bottom Tab Bar - Tab Item',
)

let items = figma.kotlin``
let selectedIndex = 0
let missingIcon = false
tabs.forEach((tab, index) => {
  if (tab.getEnum('◉ Is Selected', { True: true, False: false })) selectedIndex = index
  const glyph = tab.getInstanceSwap('🧩 Icon')
  const icon = glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
  if (!icon) missingIcon = true
  items = figma.kotlin`${items}
        BottomTabBarItem(label = "${quote(tab.getString('✍️ Label'))}", icon = ${icon ?? 'LemonadeIcons.Heart'}),`
})

const showAction = instance.getBoolean('◉ Show Action')
const action = showAction ? slot('Action Slot', 'the action beside the bar') : undefined
const actionSlot = showAction ? instance.getSlot('Action Slot') : undefined
const actionButton = actionSlot && actionSlot.connectedInstances ? actionSlot.connectedInstances[0] : undefined
const ghostAction = actionButton ? actionButton.getEnum('◇ Type', { Ghost: true }) === true : false

export default {
  example: figma.kotlin`LemonadeUi.BottomTabBar(
    items = listOf(${items}
    ),
    selectedIndex = ${selectedIndex},
    onItemSelected = { },${action ? figma.kotlin`
    floatingActionButton = ${action},` : ''}
)${ghostAction ? `
// NOTE: the design draws the ghost action on bgDefault with the Xlarge shadow,
// which IconButton has no parameter for; add them through its modifier.` : ''}${missingIcon ? `
// NOTE: a tab's icon did not resolve; set the entry it uses` : ''}
// NOTE: each tab shows one icon in Figma. Pair an outline icon with its solid
// variant through selectedIcon for the selected state.`,
  imports: [
    'import com.teya.lemonade.BottomTabBar',
    'import com.teya.lemonade.BottomTabBarItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeIcons',
    ...slotImports,
  ],
  id: 'bottom-tab-bar',
  metadata: { nestable: false },
}
