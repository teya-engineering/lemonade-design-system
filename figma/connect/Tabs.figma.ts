// url=<LEMONADE_COMPONENTS>?node-id=8122-19623
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tabs.kt
// component=Tabs
import figma from 'figma'

const instance = figma.selectedInstance

const itemsSize = instance.getEnum('↕ Items Size', { Hug: 'Hug', Stretch: 'Stretch' })

// The Items variant tops out at "5+", so the count comes from resolving the
// tabs. Nine is the most the set lays out.
const tabs = []
let selectedIndex = 0
for (let n = 1; n <= 9; n += 1) {
  const child = instance.findInstance(`Tab ${n}`)
  if (!child || child.type !== 'INSTANCE') continue
  const result = child.executeTemplate()
  // Selection is a tab property in Figma and an index here.
  if (result.metadata?.props?.selected === 'true') selectedIndex = tabs.length
  tabs.push(result.example)
}

const [t1, t2, t3, t4, t5, t6, t7, t8, t9] = tabs
const line = (t) => (t ? figma.kotlin`
        ${t},` : '')

export default {
  example: figma.kotlin`LemonadeUi.Tabs(
    tabs = listOf(${line(t1)}${line(t2)}${line(t3)}${line(t4)}${line(t5)}${line(t6)}${line(t7)}${line(t8)}${line(t9)}
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
