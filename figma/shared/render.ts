// Shared by every label's templates; `tag` is figma.kotlin, figma.swift or
// figma.typescript.

// A nested snippet keeps its own indentation, so its code is shifted to fit.
export const shift = (sections, pad) =>
  sections.map((section) =>
    section.type === 'CODE' ? { ...section, code: section.code.replace(/\n/g, `\n${pad}`) } : section,
  )

// Figma passes imports up only one level: a child's result carries its own
// imports but not its children's, so every child rendered here has its imports
// collected for the caller to re-export.
export const renderer = (instance, tag) => {
  const imports = new Set()
  // Escaping differs per language, and a tag that stopped reporting one would
  // silently drop Kotlin's `$` escape, so an unknown language fails the publish.
  const language = tag``.language
  if (language !== 'kotlin' && language !== 'swift' && language !== 'typescript') {
    throw new Error(`Code Connect template language is ${language}, expected kotlin, swift or typescript`)
  }
  const kotlin = language === 'kotlin'

  // Figma text is arbitrary: a quote ends the literal, a backslash escapes what
  // follows it, a newline breaks the line, and Kotlin reads `$name` as a
  // template expression. Swift and a JSX attribute have no `\$` escape, so that
  // one is Kotlin-only.
  const quote = (value) => {
    const escaped = String(value ?? '')
      .replace(/\\/g, '\\\\')
      .replace(/"/g, '\\"')
      .replace(/\r?\n/g, '\\n')
    return kotlin ? escaped.replace(/\$/g, '\\$') : escaped
  }

  const render = (child, pad) => {
    const { example } = child.executeTemplate()
    for (const section of example) for (const i of section.nestedImports ?? []) imports.add(i)
    return shift(example, pad)
  }

  // Lemonade components in a slot render as their own snippets; a slot holding
  // none of them returns undefined so the caller keeps its placeholder. Slots
  // also list instances connected under other labels only, which have no id
  // here and would render as another language.
  const snippets = (name, pad) => {
    const found = instance.getSlot(name)
    const children = (found && found.connectedInstances ? found.connectedInstances : []).filter(
      (child) => child.codeConnectId() !== null,
    )
    if (!children.length) return undefined
    let body = tag``
    for (const child of children) body = tag`${body}
${pad}${render(child, pad)}`
    return body
  }

  const slot = (name, placeholder, open = '{') => {
    const body = snippets(name, '        ')
    return body
      ? tag`${open}${body}
    }`
      : `${open} /* ${placeholder} */ }`
  }

  // An enum-typed parameter takes the glyph of the Icon a slot holds.
  const slotIcon = (name) => {
    const found = instance.getSlot(name)
    const icon = found && found.connectedInstances
      ? found.connectedInstances.find((child) => child.codeConnectId() !== null)
      : undefined
    const glyph = icon ? icon.getInstanceSwap('🧩 Icon') : undefined
    return glyph && glyph.type === 'INSTANCE' ? glyph.executeTemplate().example : undefined
  }

  return { imports, render, snippets, slot, slotIcon, quote }
}
