// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=Card
import figma from 'figma'

const instance = figma.selectedInstance

const background = instance.getEnum('◇ Background', {
  Default: 'default',
  Subtle: 'subtle',
  Elevated: 'elevated',
})

const padding = instance.getEnum('◇ Spacing', {
  None: 'none',
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
})

const content = instance.getSlot('🧩 Slot')

const nested = (show, layer) => {
  if (!instance.getBoolean(show)) return undefined
  const child = instance.findInstance(layer)
  return child && child.type === 'INSTANCE' ? child.executeTemplate() : undefined
}
const header = nested('◉ Show Heading', 'Card Heading')
const footer = nested('◉ Show Footer Action', 'Card Footer Action')

export default {
  example: figma.swift`LemonadeUi.Card(
    contentPadding: .${padding},
    background: .${background}${header ? figma.swift`,
    header: ${header.example}` : ''}${footer ? figma.swift`,
    footerAction: ${footer.example}` : ''}
) {
    /* card content */
}`,
  id: 'card',
  metadata: { nestable: false },
}
