// url=<LEMONADE_COMPONENTS>?node-id=10489-151885
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/SelectListItem.kt
// component=SelectListItem
import figma from 'figma'

const instance = figma.selectedInstance

const read = (layer) => {
  const node = instance.findText(layer)
  return node && node.type === 'TEXT' ? node.textContent : undefined
}
const label = read('Label') ?? ''
const supportText = instance.getBoolean('◉ Show Support Text') ? read('Support text') : undefined

const type = instance.getEnum('◇ Type', {
  Single: 'Single',
  Multiple: 'Multiple',
  Toggle: 'Toggle',
})

// Figma calls the borderless variant "Ghost"; the enum calls it Plain.
const variant = instance.getEnum('◇ Variant', { Ghost: 'Plain', Outlined: 'Outlined' })

const checked = instance.getEnum('◉ Is Checked', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const showDivider = instance.getEnum('◉ Show Divider', { True: true, False: false })

const leading = instance.getBoolean('◉ Show Leading')
const trailing = instance.getBoolean('◉ Show Trailing')
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
  example: figma.kotlin`LemonadeUi.SelectListItem(
    label = "${label}",
    type = SelectListItemType.${type},
    checked = ${checked},
    onItemClicked = { },${variant !== 'Plain' ? `
    variant = SelectListItemVariant.${variant},` : ''}${supportText ? `
    supportText = "${supportText}",` : ''}${disabled ? `
    enabled = false,` : ''}${showDivider ? `
    showDivider = true,` : ''}${leading ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading', 'leading content')},` : ''}${trailing ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing', 'trailing content')},` : ''}${bottom ? figma.kotlin`
    slotContent = ${slot('↪ 🧩 Bottom Slot', 'bottom content')},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.SelectListItem',
    'import com.teya.lemonade.core.SelectListItemType',
    'import com.teya.lemonade.core.SelectListItemVariant',
    ...slotImports,
  ],
  id: 'select-list-item',
  metadata: { nestable: true },
}
