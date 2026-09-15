// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=Card
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { render, snippets, imports: slotImports } = renderer(instance, figma.kotlin)

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

const nested = (show, layer) => {
  if (!instance.getBoolean(show)) return undefined
  const child = instance.findInstance(layer)
  return child && child.type === 'INSTANCE' ? render(child, '    ') : undefined
}
const header = nested('◉ Show Heading', 'Card Heading')
const footer = nested('◉ Show Footer Action', 'Card Footer Action')
const content = snippets('🧩 Slot', '    ')

export default {
  example: figma.kotlin`LemonadeUi.Card(
    background = LemonadeCardBackground.${background},
    contentPadding = LemonadeCardPadding.${padding},${header ? figma.kotlin`
    header = ${header},` : ''}${footer ? figma.kotlin`
    footerAction = ${footer},` : ''}
) {${content ?? `
    /* card content */`}
}`,
  imports: [
    'import com.teya.lemonade.Card',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeCardBackground',
    'import com.teya.lemonade.core.LemonadeCardPadding',
    ...slotImports,
  ],
  id: 'card',
  metadata: { nestable: false },
}
