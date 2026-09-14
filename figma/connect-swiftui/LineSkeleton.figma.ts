// url=<LEMONADE_COMPONENTS>?node-id=2309-13424
// source=swiftui/Sources/Lemonade/Components/LemonadeSkeleton.swift
// component=LineSkeleton
import figma from 'figma'

const instance = figma.selectedInstance

// Figma names its heights one step above the enum: its Small is the size-400
// line the code calls xSmall. Matched on the height token, not the name.
const size = instance.getEnum('Height', {
  Small: 'xSmall',
  Medium: 'small',
  Large: 'medium',
  XLarge: 'large',
  '2XLarge': 'xLarge',
  '3XLarge': 'xxLarge',
})

export default {
  example: figma.swift`LemonadeUi.LineSkeleton(
    size: .${size}
)`,
  id: 'line-skeleton',
  metadata: { nestable: true },
}
