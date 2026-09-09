// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=swiftui/Sources/Lemonade/Components/LemonadeSwipeActionRow.swift
// component=SwipeActionRow
import figma from 'figma'

const instance = figma.selectedInstance

const showDivider = instance.getBoolean('◉ Show Divider')

// Figma offers a Leading placement; neither platform has a placement parameter,
// so a row designed that way cannot be built as drawn. The snippet says so
// rather than quietly emitting a trailing row.
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

export default {
  example: figma.swift`LemonadeUi.SwipeActionRow(${leading ? `
    // NOTE: this design places the actions leading, which the component does not support` : ''}
    // TODO: actions — one LemonadeSwipeAction per action in the design
    actions: []${showDivider ? `,
    showDivider: true` : ''}
) {
    /* row content */
}`,
  id: 'swipe-action-row',
  metadata: { nestable: false },
}
