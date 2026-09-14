// url=<LEMONADE_COMPONENTS>?node-id=17678-59
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/Dropdown.kt
// component=Dropdown
import figma from 'figma'

const instance = figma.selectedInstance

// Figma groups items into sections; the code takes a flat list. Each level is
// searched on its own because traverseInstances lists layers last-to-first.
const named = (node, name) =>
  node.findLayers((layer) => layer.type === 'INSTANCE' && layer.name === name)

let items = figma.kotlin``
let headings = false
let dividers = false
let supportText = false
for (const section of named(instance, '.Dropdown Section')) {
  if (named(section, '.Heading Row').length) headings = true
  if (named(section, 'Divider').length) dividers = true
  for (const item of named(section, '.Menu Item')) {
    const result = item.executeTemplate()
    if (result.metadata?.props?.supportText === 'true') supportText = true
    items = figma.kotlin`${items}
    ${result.example}`
  }
}

const unsupported = [
  headings ? 'section headings' : undefined,
  dividers ? 'section dividers' : undefined,
  supportText ? 'item support text' : undefined,
].filter(Boolean)

export default {
  example: figma.kotlin`LemonadeUi.Dropdown(
    expanded = true,
    onDismissRequest = { },
) {${items}
}${unsupported.length ? `
// NOTE: Dropdown has no ${unsupported.join(', ')}` : ''}`,
  imports: ['import com.teya.lemonade.Dropdown', 'import com.teya.lemonade.LemonadeUi'],
  id: 'dropdown',
  metadata: { nestable: true },
}
