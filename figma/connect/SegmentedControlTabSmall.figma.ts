// url=<LEMONADE_COMPONENTS>?node-id=15007-85857
// source=kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/SegmentedControl.kt
// component=TabButtonProperties
import figma from 'figma'

// An internal Figma component is connected here because a tab has no composable
// of its own: it is one entry in the parent's list.
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
        ? figma.kotlin`TabButtonProperties.icon(icon = ${iconCode})`
        : figma.kotlin`TabButtonProperties.labelAndIcon(label = "${label}", icon = ${iconCode})`)
    : figma.kotlin`TabButtonProperties.label(label = "${label}")`,
  imports: ['import com.teya.lemonade.core.TabButtonProperties'],
  id: 'segmented-control-tab-small',
  metadata: { nestable: true },
}
