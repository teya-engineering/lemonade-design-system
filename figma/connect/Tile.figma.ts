// url=<LEMONADE_COMPONENTS>?node-id=11099-25988
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tile.kt
// component=Tile
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')
const supportText = instance.getBoolean('◉ Show Support Text')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined

const variant = instance.getEnum('◇ Variant', { Filled: 'Filled', Outlined: 'Outlined' })
const orientation = instance.getEnum('◇ Orientation', {
  Vertical: 'Vertical',
  Horizontal: 'Horizontal',
})
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const topAccessory = instance.getBoolean('◉ Show Top Accessory')
  ? instance.getSlot('↪ 🧩 Top Accessory')
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

// An enum-typed parameter takes the glyph of the Icon a slot holds.
const slotIcon = (name) => {
  const found = instance.getSlot(name)
  const icon = found && found.connectedInstances ? found.connectedInstances[0] : undefined
  const glyph = icon ? icon.getInstanceSwap('🧩 Icon') : undefined
  return glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
}

const icon = slotIcon('🧩 Leading Slot')

export default {
  example: figma.kotlin`LemonadeUi.Tile(
    label = "${label}",${icon ? figma.kotlin`
    icon = ${icon},` : `
    // TODO: icon — set the LemonadeIcons entry the design uses`}
    onClick = { },${supportText ? `
    supportText = "${supportText}",` : ''}${selected ? `
    isSelected = true,` : ''}${disabled ? `
    enabled = false,` : ''}
    variant = LemonadeTileVariant.${variant},
    orientation = LemonadeTileOrientation.${orientation},${
      topAccessory ? figma.kotlin`
    topAccessory = ${slot('↪ 🧩 Top Accessory', 'top accessory')},` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tile',
    'import com.teya.lemonade.core.LemonadeTileOrientation',
    'import com.teya.lemonade.core.LemonadeTileVariant',
    ...slotImports,
    ...(icon ? ['import com.teya.lemonade.core.LemonadeIcons'] : []),
  ],
  id: 'tile',
  metadata: { nestable: true },
}
