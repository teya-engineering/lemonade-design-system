// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=Card
import figma from 'figma'

const instance = figma.selectedInstance

const background = instance.getEnum('◇ Background', {
  Default: 'Default',
  Subtle: 'Subtle',
  Elevated: 'Elevated',
})

const padding = instance.getEnum('◇ Spacing', {
  None: 'None',
  XSmall: 'XSmall',
  Small: 'Small',
  Medium: 'Medium',
})

const content = instance.getSlot('🧩 Slot')

export default {
  example: figma.kotlin`LemonadeUi.Card(
    background = LemonadeCardBackground.${background},
    contentPadding = LemonadeCardPadding.${padding},
) {
    ${content}
}`,
  imports: [
    'import com.teya.lemonade.Card',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeCardBackground',
    'import com.teya.lemonade.core.LemonadeCardPadding',
  ],
  id: 'card',
  metadata: { nestable: false },
}
