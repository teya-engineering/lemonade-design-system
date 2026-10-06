// url=<LEMONADE_COMPONENTS>?node-id=2195-83
// source=web/src/components/icon/icon.tsx
// component=Icon
import figma from 'figma'

const instance = figma.selectedInstance

// `figma.code` is the tagged template; there is no figma.typescript, and a missing tag
// throws inside Figma, which publishes as an empty snippet rather than failing the upload.
const code = figma.code

// Figma writes 2X/3X/4X; LemonadeAssetSize repeats the X. Web spells them camelCase.
const size = instance.getEnum('Size', {
  XSmall: 'xSmall',
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
  '3XLarge': 'xxxLarge',
  '4XLarge': 'xxxxLarge',
})

/**
 * Compose resolves the glyph by running the swapped instance's own template, which yields
 * `LemonadeIcons.Heart`. Web has no asset templates — an icon reaches a React call site as
 * a name, not an enum entry — so this reads the instance's name instead. Figma names its
 * icon components exactly as the package names the files, so `arrow-right` needs no
 * translation, only the defensive lowercase.
 */
const glyph = instance.getInstanceSwap('🧩 Icon')
const named = glyph && glyph.type === 'INSTANCE' && typeof glyph.name === 'string' ? glyph.name.trim().toLowerCase() : ''
// Falls back to a real icon rather than an empty `use`, which would typecheck as a string
// and then render nothing.
const use = named || 'heart'

export default {
  // Decorative by default: an icon in a design is nearly always beside the text that names
  // it, and `contentDescription` is required so the choice is visible either way.
  example: code`<Icon use="${use}" size="${size}" contentDescription={null} />`,
  imports: ["import { Icon } from '@teya/lemonade-mobile-ds/react'"],
  id: 'icon',
  metadata: { nestable: true },
}
