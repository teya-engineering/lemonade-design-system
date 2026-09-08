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

// The segment labels are not exposed as properties, so only the count carries
// over; the placeholders are meant to be replaced.
const tabs = Array.from(
  { length: segments },
  (_, i) => `        TabButtonProperties.label(label = "Tab ${i + 1}"),`,
).join('\n')

export default {
  example: figma.kotlin`LemonadeUi.SegmentedControl(
    properties = listOf(
${tabs}
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
