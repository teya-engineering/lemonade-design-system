// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=swiftui/Sources/Lemonade/Components/LemonadeSwipeActionRow.swift
// component=SwipeActionRow
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets } = renderer(instance, figma.swift)
const content = snippets('🧩 Sliding Item', '    ')

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

export default {
  example: figma.swift`LemonadeUi.SwipeActionRow(
    // TODO: one action per action in the design
    ${leading ? 'leadingActions: []' : 'trailingActions: []'}${showDivider ? `,
    showDivider: true` : ''}
) {${content ?? `
    /* row content */`}
}`,

  id: 'swipe-action-row',
  metadata: { nestable: false },
}
