// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SwipeActionRow.kt
// component=SwipeActionRow
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets, imports: slotImports } = renderer(instance, figma.kotlin)

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

const content = snippets('🧩 Sliding Item', '    ')

export default {
  example: figma.kotlin`LemonadeUi.SwipeActionRow(
    // TODO: one action per action in the design
    ${leading ? 'leadingActions = emptyList(),' : 'trailingActions = emptyList(),'}${showDivider ? `
    showDivider = true,` : ''}
) {${content ?? `
    /* row content */`}
}`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SwipeActionRow',
    ...slotImports,
  ],
  id: 'swipe-action-row',
  metadata: { nestable: false },
}
