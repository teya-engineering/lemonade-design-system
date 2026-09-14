// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Button.kt
// component=Button
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')

const variant = instance.getEnum('◇ Variant', {
  Primary: 'Primary',
  Secondary: 'Secondary',
  Neutral: 'Neutral',
  Critical: 'Critical',
  'On Brand': 'OnBrand',
  'On Color': 'OnColor',
})

const type = instance.getEnum('◇ Type', {
  Solid: 'Solid',
  Subtle: 'Subtle',
  Ghost: 'Ghost',
})

const size = instance.getEnum('↕ Size', {
  Large: 'Large',
  Medium: 'Medium',
  Small: 'Small',
  XSmall: 'XSmall',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const leadingSlot = instance.getBoolean('◉ Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('◉ Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
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
  example: figma.kotlin`LemonadeUi.Button(
    label = "${label}",
    onClick = { },
    variant = LemonadeButtonVariant.${variant},
    type = LemonadeButtonType.${type},
    size = LemonadeButtonSize.${size},${
      leadingSlot ? figma.kotlin`
    leadingSlot = ${slot('↪ 🧩 Leading Slot', 'leading content')},` : ''
    }${
      trailingSlot ? figma.kotlin`
    trailingSlot = ${slot('↪ 🧩 Trailing Slot', 'trailing content')},` : ''
    }${disabled ? `
    enabled = false,` : ''}${loading ? `
    loading = true,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.Button',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeButtonSize',
    'import com.teya.lemonade.core.LemonadeButtonType',
    'import com.teya.lemonade.core.LemonadeButtonVariant',
    ...slotImports,
  ],
  id: 'button',
  metadata: { nestable: true },
}
