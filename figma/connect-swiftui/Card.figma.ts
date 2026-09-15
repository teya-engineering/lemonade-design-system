// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=Card
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { render, snippets } = renderer(instance, figma.swift)
const content = snippets('🧩 Slot', '    ')

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

const nested = (show, layer) => {
  if (!instance.getBoolean(show)) return undefined
  const child = instance.findInstance(layer)
  return child && child.type === 'INSTANCE' ? render(child, '    ') : undefined
}
const header = nested('◉ Show Heading', 'Card Heading')
const footer = nested('◉ Show Footer Action', 'Card Footer Action')

export default {
  example: figma.swift`LemonadeUi.Card(
    contentPadding: .${padding},
    background: .${background}${header ? figma.swift`,
    header: ${header}` : ''}${footer ? figma.swift`,
    footerAction: ${footer}` : ''}
) {${content ?? `
    /* card content */`}
}`,
  id: 'card',
  metadata: { nestable: false },
}
