// url=<LEMONADE_COMPONENTS>?node-id=10643-14087
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=Card
import figma from 'figma'

const instance = figma.selectedInstance

const background = instance.getEnum('◇ Background', {
  Default: 'Default',
  Subtle: 'Subtle',
  Elevated: 'Elevated',
})

const padding = instance.getEnum('◇ Spacing', {
  None: 'None',
  XSmall: 'XSmall',
  Small: 'Small',
  Medium: 'Medium',
})

const nested = (show, layer) => {
  if (!instance.getBoolean(show)) return undefined
  const child = instance.findInstance(layer)
  if (!child || child.type !== 'INSTANCE') return undefined
  const result = child.executeTemplate()
  return {
    ...result,
    example: result.example.map((section) =>
      section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, '\n    ') } : section,
    ),
  }
}
const header = nested('◉ Show Heading', 'Card Heading')
const footer = nested('◉ Show Footer Action', 'Card Footer Action')

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

const content = snippets('🧩 Slot', '    ')

export default {
  example: figma.kotlin`LemonadeUi.Card(
    background = LemonadeCardBackground.${background},
    contentPadding = LemonadeCardPadding.${padding},${header ? figma.kotlin`
    header = ${header.example},` : ''}${footer ? figma.kotlin`
    footerAction = ${footer.example},` : ''}
) {${content ?? `
    /* card content */`}
}`,
  imports: [
    'import com.teya.lemonade.Card',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeCardBackground',
    'import com.teya.lemonade.core.LemonadeCardPadding',
    ...slotImports,
  ],
  id: 'card',
  metadata: { nestable: false },
}
