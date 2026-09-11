// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SwipeActionRow.kt
// component=SwipeActionRow
import figma from 'figma'

const instance = figma.selectedInstance

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

// The actions are data objects with enum-typed icons, which a slot resolves to
// neither.
export default {
  example: figma.kotlin`LemonadeUi.SwipeActionRow(
    // TODO: one action per action in the design
    ${leading ? 'leadingActions = emptyList(),' : 'trailingActions = emptyList(),'}${showDivider ? `
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
