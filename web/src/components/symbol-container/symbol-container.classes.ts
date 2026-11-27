import { kebab } from '../classes'
import type { SymbolContainerAppearance, SymbolContainerSize } from './symbol-container.types'

/** The type style the platforms pair with each size, for the initials form. */
const TEXT_STYLE: Record<SymbolContainerSize, string> = {
  xSmall: 'lmnd-text-body-xsmall-semibold',
  small: 'lmnd-text-body-small-semibold',
  medium: 'lmnd-text-body-small-semibold',
  large: 'lmnd-text-body-large-semibold',
  xLarge: 'lmnd-text-body-xlarge-semibold',
  xxLarge: 'lmnd-text-heading-small',
}

/**
 * The class list for a symbol container, for any stack. A Vue or Svelte consumer calls
 * this — or writes the same classes by hand — and gets what the React component renders.
 *
 * `voice: null` leaves the voice class off, for a themed container that sets the same three
 * custom properties from a hue instead.
 */
export function symbolContainerClasses(appearance: SymbolContainerAppearance = {}): string {
  const { voice = 'neutral', size = 'medium', shape = 'circle' } = appearance
  const classes = [
    'lmnd-symbol-container',
    `lmnd-symbol-container--${kebab(size)}`,
    `lmnd-symbol-container--${shape}`,
  ]
  if (voice !== null) classes.push(`lmnd-symbol-container--${kebab(voice)}`)
  return classes.join(' ')
}

/** The text style for the initials form at this size, which the label carries. */
export function symbolContainerTextStyle(size: SymbolContainerSize = 'medium'): string {
  return TEXT_STYLE[size]
}
