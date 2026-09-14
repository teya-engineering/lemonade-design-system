// url=<LEMONADE_COMPONENTS>?node-id=15106-421
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt
// component=ResourceListItem
import figma from 'figma'

const instance = figma.selectedInstance

const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : ''
}
const label = read('Label')
const value = read('Value')

const supportText = instance.getBoolean('◉ Show Description')
  ? instance.getString('↪ ✍️ Description')
  : undefined

const isLoading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })
const bottom = instance.getBoolean('◉ Show Bottom Slot')

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
  example: figma.kotlin`LemonadeUi.ResourceListItem(
    leadingSlot = ${slot('↪ 🧩 Leading', 'leading content')},
    label = "${label}",
    value = "${value}",${supportText ? `
    supportText = "${supportText}",` : ''}
    onItemClicked = { },${isLoading ? `
    isLoading = true,` : ''}${showDivider ? `
    showDivider = true,` : ''}${bottom ? figma.kotlin`
    addonSlot = ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.ResourceListItem',
    ...slotImports,
  ],
  id: 'resource-list-item',
  metadata: { nestable: true },
}
