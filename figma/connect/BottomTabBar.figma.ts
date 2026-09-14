// url=<LEMONADE_COMPONENTS>?node-id=17947-25065
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/BottomTabBar.kt
// component=BottomTabBar
import figma from 'figma'

const instance = figma.selectedInstance

// traverseInstances would list these last-to-first; the tabs are direct children.
const tabs = instance.findLayers(
  (node) => node.type === 'INSTANCE' && node.name === 'Bottom Tab Bar - Tab Item',
)

let items = figma.kotlin``
let selectedIndex = 0
tabs.forEach((tab, index) => {
  if (tab.getEnum('◉ Is Selected', { True: true, False: false })) selectedIndex = index
  const glyph = tab.getInstanceSwap('🧩 Icon')
  const icon = glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
  items = figma.kotlin`${items}
        BottomTabBarItem(label = "${tab.getString('✍️ Label')}"${icon ? figma.kotlin`, icon = ${icon}` : ''}),`
})

export default {
  example: figma.kotlin`LemonadeUi.BottomTabBar(
    items = listOf(${items}
    ),
    selectedIndex = ${selectedIndex},
    onItemSelected = { },
)`,
  imports: [
    'import com.teya.lemonade.BottomTabBar',
    'import com.teya.lemonade.BottomTabBarItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeIcons',
  ],
  id: 'bottom-tab-bar',
  metadata: { nestable: true },
}
