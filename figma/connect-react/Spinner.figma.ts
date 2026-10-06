// url=<LEMONADE_COMPONENTS>?node-id=7132-127
// source=web/src/components/spinner/spinner.tsx
// component=Spinner
import figma from 'figma'

const instance = figma.selectedInstance

// `figma.code` is the tagged template; there is no figma.typescript, and a missing tag
// throws inside Figma, which publishes as an empty snippet rather than failing the upload.
const code = figma.code

// Figma writes 2X where LemonadeAssetSize repeats the X, and offers five of the eight
// sizes the code carries. Web spells them camelCase, the platforms PascalCase.
const size = instance.getEnum('Size', {
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
})

export default {
  // `contentDescription` is required, and a spinner a designer placed on its own is the
  // thing being announced, so the snippet names it rather than hiding it. Inside a button,
  // whose own label already says it, the caller passes null.
  example: code`<Spinner size="${size}" contentDescription="Loading" />`,
  imports: ["import { Spinner } from '@teya/lemonade-mobile-ds/react'"],
  id: 'spinner',
  metadata: { nestable: true },
}
