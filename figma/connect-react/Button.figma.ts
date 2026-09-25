// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=web/src/components/button/button.tsx
// component=Button
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { snippets, imports: slotImports, quote } = renderer(instance, figma.typescript)

const label = instance.getString('✍️ Label')

const variant = instance.getEnum('◇ Variant', {
  Primary: 'primary',
  Secondary: 'secondary',
  Neutral: 'neutral',
  Critical: 'critical',
  'On Brand': 'onBrand',
  'On Color': 'onColor',
})

const type = instance.getEnum('◇ Type', {
  Solid: 'solid',
  Subtle: 'subtle',
  Ghost: 'ghost',
})

const size = instance.getEnum('↕ Size', {
  Large: 'large',
  Medium: 'medium',
  Small: 'small',
  XSmall: 'xSmall',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const leadingSlot = instance.getBoolean('◉ Show Leading')
const trailingSlot = instance.getBoolean('◉ Show Trailing')

// Web ships no asset templates — an icon is a `--lmnd-icon` URL rather than an enum
// entry — so a nested icon usually has no React snippet to render and the prop falls back
// to a placeholder. `undefined` carries the comment because a JSX attribute assigned only
// a comment is a syntax error (TS17000), so the snippet would not compile.
const iconProp = (name, slotName, placeholder) => {
  const body = snippets(slotName, '    ')
  return body
    ? figma.typescript`
  ${name}={${body}}`
    : `
  ${name}={undefined /* ${placeholder} */}`
}

export default {
  example: figma.typescript`<Button
  label="${quote(label)}"
  variant="${variant}"
  type="${type}"
  size="${size}"
  onClick={() => {}}${leadingSlot ? iconProp('leadingIcon', '↪ 🧩 Leading Slot', 'leading icon') : ''}${
    trailingSlot ? iconProp('trailingIcon', '↪ 🧩 Trailing Slot', 'trailing icon') : ''
  }${disabled ? `
  enabled={false}` : ''}${loading ? `
  loading` : ''}
/>`,
  imports: ["import { Button } from '@teya/lemonade-mobile-ds/react'", ...slotImports],
  id: 'button',
  metadata: { nestable: true },
}
