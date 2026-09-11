// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=swiftui/Sources/Lemonade/Components/LemonadeSwipeActionRow.swift
// component=SwipeActionRow
import figma from 'figma'

const instance = figma.selectedInstance

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

// The actions are SwipeAction data objects with enum-typed icons, which a Figma
// slot resolves to neither, so the list is left for the developer to fill.
export default {
  example: figma.swift`LemonadeUi.SwipeActionRow(
    // TODO: one action per action in the design
    ${leading ? 'leadingActions: [],' : 'trailingActions: [],'}${showDivider ? `,
    showDivider: true` : ''}
) {
    /* row content */
}`,

  id: 'swipe-action-row',
  metadata: { nestable: false },
}
