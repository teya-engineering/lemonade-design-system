// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=web/src/components/button/button.tsx
// component=Button
import figma from 'figma'

const instance = figma.selectedInstance

// `figma.code` is the tagged template; there is no figma.typescript, and a missing tag
// throws inside Figma, which publishes as an empty snippet rather than failing the upload.
const code = figma.code

// Figma text is arbitrary and this lands in a double-quoted JSX attribute, which is not a
// JavaScript string: a backslash is literal there, so `\"` would end the attribute and
// leave a stray backslash. Entities are the only escape JSX honours, and `&` has to go
// first or it would re-encode the ones added after it.
const quote = (value) =>
  String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\r?\n/g, ' ')

const label = instance.getString('✍️ Label')

const variant = instance.getEnum('◇ Variant', {
  Primary: 'primary',
  Secondary: 'secondary',
  Neutral: 'neutral',
  Critical: 'critical',
  'On Brand': 'onBrand',
  'On Color': 'onColor',
})

const emphasis = instance.getEnum('◇ Type', {
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

// Web ships no asset templates — an icon reaches a React call site as a `--lmnd-icon` URL
// rather than an enum entry — so there is no snippet for a nested icon to render as. The
// prop carries a placeholder instead. `undefined` holds the comment because a JSX attribute
// assigned only a comment is a syntax error (TS17000), so the snippet would not compile.
const iconProp = (name, placeholder) => `
  ${name}={undefined /* ${placeholder} */}`

export default {
  example: code`<Button
  label="${quote(label)}"
  variant="${variant}"
  emphasis="${emphasis}"
  size="${size}"
  onClick={() => {}}${leadingSlot ? iconProp('leadingIcon', 'leading icon') : ''}${
    trailingSlot ? iconProp('trailingIcon', 'trailing icon') : ''
  }${disabled ? `
  enabled={false}` : ''}${loading ? `
  loading` : ''}
/>`,
  imports: ["import { Button } from '@teya/lemonade-mobile-ds/react'"],
  id: 'button',
  metadata: { nestable: true },
}
