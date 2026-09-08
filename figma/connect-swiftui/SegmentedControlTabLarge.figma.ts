// url=<LEMONADE_COMPONENTS>?node-id=11526-16640
// source=swiftui/Sources/Lemonade/Components/LemonadeSegmentedControl.swift
// component=LemonadeTabButtonProperties
import figma from 'figma'

// A segmented control tab has no view of its own — it is one entry in the
// parent's `properties` array. Connected so SegmentedControl can carry the real
// labels and icons instead of placeholders.
const instance = figma.selectedInstance

const iconOnly = instance.getEnum('◇ Layout', { 'Icon Only': true, 'With Label': false })
const label = instance.getString('✍️ Label')

const icon = iconOnly || instance.getBoolean('◉ Show Icon')
  ? instance.getInstanceSwap('↪ 🧩 Icon')
  : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

// The factories take the label unlabelled: .label("Tab 1"), not .label(label:).
// Falls back to the label form when the icon cannot be resolved, so the emitted
// expression is always valid Swift rather than a call missing its argument.
export default {
  example: iconCode
    ? (iconOnly
        ? figma.swift`LemonadeTabButtonProperties.icon(${iconCode})`
        : figma.swift`LemonadeTabButtonProperties.labelAndIcon("${label}", icon: ${iconCode})`)
    : figma.swift`LemonadeTabButtonProperties.label("${label}")`,
  id: 'segmented-control-tab-large',
  metadata: { nestable: true },
}
