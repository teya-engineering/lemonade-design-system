// url=<LEMONADE_COMPONENTS>?node-id=11526-16640
// source=swiftui/Sources/Lemonade/Components/LemonadeSegmentedControl.swift
// component=LemonadeTabButtonProperties
import figma from 'figma'

// An internal Figma component is connected here because a tab has no view of
// its own: it is one entry in the parent's array.
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

export default {
  example: iconCode
    ? (iconOnly
        ? figma.swift`LemonadeTabButtonProperties.icon(${iconCode})`
        : figma.swift`LemonadeTabButtonProperties.labelAndIcon("${label}", icon: ${iconCode})`)
    : figma.swift`LemonadeTabButtonProperties.label("${label}")`,
  id: 'segmented-control-tab-large',
  metadata: { nestable: true },
}
