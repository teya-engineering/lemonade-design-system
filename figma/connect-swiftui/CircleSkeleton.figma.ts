// url=<LEMONADE_COMPONENTS>?node-id=2309-13417
// source=swiftui/Sources/Lemonade/Components/LemonadeSkeleton.swift
// component=CircleSkeleton
import figma from 'figma'

// The Figma circle has no size property: it is drawn at 40px, size-1000.
export default {
  example: figma.swift`LemonadeUi.CircleSkeleton(
    size: .xxLarge
)`,
  id: 'circle-skeleton',
  metadata: { nestable: true },
}
