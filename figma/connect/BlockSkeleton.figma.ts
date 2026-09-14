// url=<LEMONADE_COMPONENTS>?node-id=4446-691
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Skeleton.kt
// component=BlockSkeleton
import figma from 'figma'

const instance = figma.selectedInstance

const radii = instance.getEnum('Radii', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
})

export default {
  example: figma.kotlin`LemonadeUi.BlockSkeleton()
// NOTE: BlockSkeleton has a fixed height and corner radius; the design's Radii=${radii} block has no code equivalent`,
  imports: ['import com.teya.lemonade.BlockSkeleton', 'import com.teya.lemonade.LemonadeUi'],
  id: 'block-skeleton',
  metadata: { nestable: true },
}
