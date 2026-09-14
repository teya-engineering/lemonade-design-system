// url=<LEMONADE_COMPONENTS>?node-id=4446-691
// source=swiftui/Sources/Lemonade/Components/LemonadeSkeleton.swift
// component=BlockSkeleton
import figma from 'figma'

const instance = figma.selectedInstance

const radii = instance.getEnum('Radii', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
})

export default {
  example: figma.swift`LemonadeUi.BlockSkeleton()
// NOTE: BlockSkeleton has a fixed height and corner radius; the design's Radii=${radii} block has no code equivalent`,
  id: 'block-skeleton',
  metadata: { nestable: true },
}
