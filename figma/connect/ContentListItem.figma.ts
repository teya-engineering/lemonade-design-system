// url=<LEMONADE_COMPONENTS>?node-id=18130-69377
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ContentListItem.kt
// component=ContentListItem
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')
const value = instance.getString('✍️ Value')

const layout = instance.getEnum('◇ Layout', {
  Horizontal: 'Horizontal',
  Vertical: 'Vertical',
})

const density = instance.getEnum('◇ Density', {
  Comfortable: 'Comfortable',
  Compact: 'Compact',
})

const showDivider = instance.getEnum('◉ Show divider', { True: true, False: false })

const leadingSlot = instance.getBoolean('Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined
const contentSlot = instance.getBoolean('◉ Show Content Slot')
  ? instance.getSlot('↪ 🧩 Content Slot')
  : undefined

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
  example: figma.kotlin`LemonadeUi.ContentListItem(
    label = "${label}",
    value = "${value}",
    layout = LemonadeContentListItemLayout.${layout},
    density = LemonadeContentListItemDensity.${density},${showDivider ? `
    showDivider = true,` : ''}${
      leadingSlot ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading Slot', 'leading content')},` : ''
    }${
      trailingSlot ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing Slot', 'trailing content')},` : ''
    }${
      contentSlot ? figma.kotlin`
    contentSlot = ${slot('↪ 🧩 Content Slot', 'content')},` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.ContentListItem',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeContentListItemDensity',
    'import com.teya.lemonade.core.LemonadeContentListItemLayout',
    ...slotImports,
  ],
  id: 'content-list-item',
  metadata: { nestable: true },
}
