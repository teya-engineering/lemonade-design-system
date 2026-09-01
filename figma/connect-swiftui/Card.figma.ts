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

export default {
  example: figma.swift`LemonadeUi.Card(
    contentPadding: .${padding},
    background: .${background}
) {
    ${content}
}`,
  id: 'card',
  metadata: { nestable: false },
}
