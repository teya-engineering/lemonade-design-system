// url=<LEMONADE_COMPONENTS>?node-id=17985-27526
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/BottomSheet.kt
// component=BottomSheet
import figma from 'figma'

const instance = figma.selectedInstance

const subtle = instance.getEnum('◇ Background', { Default: false, Subtle: true })
const actions = instance.getBoolean('◉ Show Bottom Actions')

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

const actionCode = snippets('↪ 🧩 Actions', '    ')

// A nested snippet keeps its own indentation, so shift it into the lambda.
const indent = (sections) =>
  sections.map((section) =>
    section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, '\n    ') } : section,
  )

// The code sheet has no header: its title goes in the content, as in its KDoc.
const bar = instance.findInstance('Bottom Sheet Top Bar')
const hasBar = bar && bar.type === 'INSTANCE'
const title = hasBar ? bar.getString('✍️ Title') : undefined
const subheading = hasBar && bar.getBoolean('◉ Show Subheading') ? bar.getString('↪ ✍️ Subheading') : undefined
const grabber = hasBar ? bar.getBoolean('◉ Show Grabber') : true

const search = instance.findInstance('Search Field')
const searchCode = search && search.type === 'INSTANCE' ? indent(search.executeTemplate().example) : undefined

export default {
  example: figma.kotlin`LemonadeUi.BottomSheet(
    expanded = true,
    onDismissRequest = { },${grabber ? '' : `
    showDragHandle = false,`}
    skipPartiallyExpanded = true,${subtle ? `
    background = LemonadeBottomSheetVariant.Subtle,` : ''}
) {${title ? `
    LemonadeUi.Text(text = "${title}", textStyle = LemonadeTheme.typography.headingSmall)` : ''}${subheading ? `
    LemonadeUi.Text(text = "${subheading}")` : ''}${searchCode ? figma.kotlin`
    ${searchCode}` : ''}
    /* sheet content */${actions && actionCode ? actionCode : actions ? `
    /* bottom actions */` : ''}
}`,
  imports: [
    'import com.teya.lemonade.BottomSheet',
    'import com.teya.lemonade.LemonadeTheme',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Text',
    ...(subtle ? ['import com.teya.lemonade.core.LemonadeBottomSheetVariant'] : []),
    ...slotImports,
  ],
  id: 'bottom-sheet-full-screen',
  metadata: { nestable: true },
}
