// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SwipeActionRow.kt
// component=SwipeActionRow
import figma from 'figma'

const instance = figma.selectedInstance

const showDivider = instance.getBoolean('◉ Show Divider')

// Figma offers a Leading placement; neither platform has a placement parameter,
// so a row designed that way cannot be built as drawn. The snippet says so
// rather than quietly emitting a trailing row.
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

export default {
  example: figma.kotlin`LemonadeUi.SwipeActionRow(${leading ? `
    // NOTE: this design places the actions leading, which the component does not support` : ''}
    // TODO: actions — one SwipeAction per action in the design
    actions = emptyList(),${showDivider ? `
    showDivider = true,` : ''}
) {
    /* row content */
}`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SwipeActionRow',
    'import com.teya.lemonade.core.SwipeAction',
  ],
  id: 'swipe-action-row',
  metadata: { nestable: false },
}
