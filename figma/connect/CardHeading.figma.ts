// url=<LEMONADE_COMPONENTS>?node-id=15115-987
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Card.kt
// component=CardHeaderConfig
import figma from 'figma'

const instance = figma.selectedInstance

const title = instance.getString('✍️ Title')
const subtitle = instance.getBoolean('◉ Show Subtitle', {
  true: instance.getString('↪ ✍️ Subtitle'),
  false: undefined,
})
const style = instance.getEnum('◇ Heading Style', {
  Default: 'Default',
  Overline: 'Overline',
})
const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
const navigation = instance.getBoolean('◉ Show Navigation Indicator')

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

const slot = (name, placeholder, open = '{') => {
  const body = snippets(name, '        ')
  return body ? figma.kotlin`${open}${body}
    }` : `${open} /* ${placeholder} */ }`
}

export default {
  example: figma.kotlin`CardHeaderConfig(
    title = "${title}",${style !== 'Default' ? `
    headingStyle = LemonadeCardHeadingStyle.${style},` : ''}${leading ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading', 'leading content')},` : ''}${trailing ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing', 'trailing content')},` : ''}${navigation ? `
    showNavigationIndicator = true,` : ''}${subtitle ? `
    subtitle = "${subtitle}",` : ''}
)`,
  imports: [
    'import com.teya.lemonade.CardHeaderConfig',
    ...(style !== 'Default' ? ['import com.teya.lemonade.core.LemonadeCardHeadingStyle'] : []),
    ...slotImports,
  ],
  id: 'card-heading',
  metadata: { nestable: true },
}
