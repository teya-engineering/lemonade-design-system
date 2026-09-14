// url=<LEMONADE_COMPONENTS>?node-id=2309-13417
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Skeleton.kt
// component=CircleSkeleton
import figma from 'figma'

// The Figma circle has no size property: it is drawn at 40px, size-1000.
export default {
  example: figma.kotlin`LemonadeUi.CircleSkeleton(
    size = LemonadeSkeletonSize.XXLarge,
)`,
  imports: [
    'import com.teya.lemonade.CircleSkeleton',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeSkeletonSize',
  ],
  id: 'circle-skeleton',
  metadata: { nestable: true },
}
