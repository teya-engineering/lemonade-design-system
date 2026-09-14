// url=<LEMONADE_COMPONENTS>?node-id=2309-13424
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Skeleton.kt
// component=LineSkeleton
import figma from 'figma'

const instance = figma.selectedInstance

// Figma names its heights one step above the enum: its Small is the size-400
// line the code calls XSmall. Matched on the height token, not the name.
const size = instance.getEnum('Height', {
  Small: 'XSmall',
  Medium: 'Small',
  Large: 'Medium',
  XLarge: 'Large',
  '2XLarge': 'XLarge',
  '3XLarge': 'XXLarge',
})

export default {
  example: figma.kotlin`LemonadeUi.LineSkeleton(
    size = LemonadeSkeletonSize.${size},
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.LineSkeleton',
    'import com.teya.lemonade.core.LemonadeSkeletonSize',
  ],
  id: 'line-skeleton',
  metadata: { nestable: true },
}
