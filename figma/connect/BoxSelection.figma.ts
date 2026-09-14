// url=<LEMONADE_COMPONENTS>?node-id=20918-229
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/BoxSelection.kt
// component=BoxSelection
import figma from 'figma'

const instance = figma.selectedInstance

const variant = instance.getEnum('◇ Variant', {
  Filled: 'Filled',
  Outlined: 'Outlined',
})

// "N/A" is the Outlined variant, where the background does not apply.
const background = instance.getEnum('◇ Background', {
  Default: 'Default',
  Elevated: 'Elevated',
  'N/A': undefined,
})

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

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

const content = snippets('🧩 Content Slot', '    ')

export default {
  example: figma.kotlin`LemonadeUi.BoxSelection(
    variant = LemonadeBoxSelectionVariant.${variant},${background ? `
    background = LemonadeBoxSelectionBackground.${background},` : ''}${selected ? `
    isSelected = true,` : ''}${disabled ? `
    enabled = false,` : ''}
    onClick = { },
) {${content ?? `
    /* content */`}
}`,
  imports: [
    'import com.teya.lemonade.BoxSelection',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeBoxSelectionBackground',
    'import com.teya.lemonade.core.LemonadeBoxSelectionVariant',
    ...slotImports,
  ],
  id: 'box-selection',
  metadata: { nestable: false },
}
