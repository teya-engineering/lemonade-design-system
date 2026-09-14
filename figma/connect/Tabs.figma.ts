// url=<LEMONADE_COMPONENTS>?node-id=8122-19623
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tabs.kt
// component=Tabs
import figma from 'figma'

const instance = figma.selectedInstance

const itemsSize = instance.getEnum('↕ Items Size', { Hug: 'Hug', Stretch: 'Stretch' })

// The Items variant tops out at "5+", so the count comes from resolving the
// tabs. Nine is the most the set lays out.
let tabs = figma.kotlin``
let count = 0
let selectedIndex = 0
for (let n = 1; n <= 9; n += 1) {
  const child = instance.findInstance(`Tab ${n}`)
  if (!child || child.type !== 'INSTANCE') continue
  const result = child.executeTemplate()
  // Selection is a tab property in Figma and an index here.
  if (result.metadata?.props?.selected === 'true') selectedIndex = count
  tabs = figma.kotlin`${tabs}
        ${result.example},`
  count += 1
}

export default {
  example: figma.kotlin`LemonadeUi.Tabs(
    tabs = listOf(${tabs}
    ),
    selectedIndex = ${selectedIndex},
    onTabSelected = { },
    itemsSize = TabsItemSize.${itemsSize},
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tabs',
    'import com.teya.lemonade.core.TabItem',
    'import com.teya.lemonade.core.TabsItemSize',
  ],
  id: 'tabs',
  metadata: { nestable: true },
}
