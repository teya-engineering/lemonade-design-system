// url=<LEMONADE_COMPONENTS>?node-id=11526-16665
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SegmentedControl.kt
// component=SegmentedControl
import figma from 'figma'

const instance = figma.selectedInstance

const segments = instance.getEnum('◇ Segments', { '2': 2, '3': 3, '4': 4, '5': 5 })

// Figma numbers the selected segment from 1; selectedTab is a 0-based index.
const selected = instance.getEnum('◇ Selected', { '1': 0, '2': 1, '3': 2, '4': 3, '5': 4 })

const size = instance.getEnum('◇ Size', {
  Large: 'Large',
  Medium: 'Medium',
  Small: 'Small',
})

// The tabs are named instances in document order, each carrying its own label
// and icon, so they resolve through their own template rather than being
// invented here. Five lookups covers the set's maximum.
const tab = (n) => {
  const child = instance.findInstance(`↪ Button ${n}`)
  return child && child.type === 'INSTANCE' ? child.executeTemplate().example : undefined
}
const t1 = tab(1)
const t2 = tab(2)
const t3 = tab(3)
const t4 = tab(4)
const t5 = tab(5)

// Only used when no tab resolves — an empty listOf() would be worse than saying
// how many segments the design has.
const placeholders = Array.from(
  { length: segments },
  (_, i) => `        TabButtonProperties.label(label = "Tab ${i + 1}"),`,
).join('\n')

export default {
  example: t1
    ? figma.kotlin`LemonadeUi.SegmentedControl(
    properties = listOf(${t1 ? figma.kotlin`
        ${t1},` : ''}${t2 ? figma.kotlin`
        ${t2},` : ''}${t3 ? figma.kotlin`
        ${t3},` : ''}${t4 ? figma.kotlin`
        ${t4},` : ''}${t5 ? figma.kotlin`
        ${t5},` : ''}
    ),
    selectedTab = ${selected},
    onTabSelected = { },
    size = LemonadeSegmentedControlSize.${size},
)`
    : figma.kotlin`LemonadeUi.SegmentedControl(
    properties = listOf(
${placeholders}
    ),
    selectedTab = ${selected},
    onTabSelected = { },
    size = LemonadeSegmentedControlSize.${size},
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SegmentedControl',
    'import com.teya.lemonade.core.LemonadeSegmentedControlSize',
    'import com.teya.lemonade.core.TabButtonProperties',
  ],
  id: 'segmented-control',
  metadata: { nestable: true },
}
