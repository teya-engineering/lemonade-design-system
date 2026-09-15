// url=<LEMONADE_COMPONENTS>?node-id=2199-1082
// source=swiftui/Sources/Lemonade/Components/LemonadeDivider.swift
// component=Divider
import figma from 'figma'

// One Figma component over two views: orientation picks between them rather
// than being a parameter.
const instance = figma.selectedInstance

const vertical = instance.getEnum('Orientation', { Vertical: true, Horizontal: false })
const variant = instance.getEnum('Type', { Solid: 'solid', Dashed: 'dashed' })

// Only the horizontal divider takes a label.
const withLabel = instance.getEnum('Variant', { 'With Label': true, 'Line Only': false })
const labelLayer = withLabel ? instance.findText('Label') : undefined
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : undefined

export default {
  example: vertical
    ? figma.swift`LemonadeUi.VerticalDivider(
    variant: .${variant}
)`
    : figma.swift`LemonadeUi.HorizontalDivider(${label ? `
    label: "${label}",` : ''}
    variant: .${variant}
)`,
  id: 'divider',
  metadata: { nestable: true },
}
