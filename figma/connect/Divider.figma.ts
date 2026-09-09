// url=<LEMONADE_COMPONENTS>?node-id=2199-1082
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Divider.kt
// component=Divider
import figma from 'figma'

// One Figma component, two composables: orientation picks between them rather
// than being a parameter.
const instance = figma.selectedInstance

const vertical = instance.getEnum('Orientation', { Vertical: true, Horizontal: false })
const variant = instance.getEnum('Type', { Solid: 'Solid', Dashed: 'Dashed' })

// Only the horizontal divider takes a label, which is why the labelled variant
// has no vertical counterpart in code.
const withLabel = instance.getEnum('Variant', { 'With Label': true, 'Line Only': false })
const labelLayer = withLabel ? instance.findText('Label') : undefined
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : undefined

export default {
  example: vertical
    ? figma.kotlin`LemonadeUi.VerticalDivider(
    variant = DividerVariant.${variant},
)`
    : figma.kotlin`LemonadeUi.HorizontalDivider(${label ? `
    label = "${label}",` : ''}
    variant = DividerVariant.${variant},
)`,
  imports: [
    'import com.teya.lemonade.Divider',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.DividerVariant',
  ],
  id: 'divider',
  metadata: { nestable: true },
}
