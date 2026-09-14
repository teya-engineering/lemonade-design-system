// url=<LEMONADE_COMPONENTS>?node-id=21709-6930
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SwipeActionRow.kt
// component=SwipeActionRow
import figma from 'figma'

const instance = figma.selectedInstance

const showDivider = instance.getBoolean('◉ Show Divider')
const leading = instance.getEnum('◇ Actions Placement', { Leading: true, Trailing: false })

// The actions are data objects with enum-typed icons, which a slot resolves to
// neither.
// Lemonade components in a slot render as their own snippets, indented to fit;
// a slot holding none of them keeps its placeholder. Figma passes up only one
// level of imports, so the children's are re-exported for the parent's parent.
const slotImports = new Set()
const snippets = (name, pad) => {
  const found = instance.getSlot(name)
  const children = found && found.connectedInstances ? found.connectedInstances : []
  if (!children.length) return undefined
  let body = figma.kotlin``
  for (const child of children) {
    const { example } = child.executeTemplate()
    for (const section of example) for (const i of section.nestedImports ?? []) slotImports.add(i)
    const sections = example.map((section) =>
      section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, `\n${pad}`) } : section,
    )
    body = figma.kotlin`${body}
${pad}${sections}`
  }
  return body
}

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
    'import com.teya.lemonade.core.SwipeAction',
    ...slotImports,
  ],
  id: 'swipe-action-row',
  metadata: { nestable: false },
}
