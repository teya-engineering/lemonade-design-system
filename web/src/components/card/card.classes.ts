import { kebab } from '../classes'
import type { CardAppearance } from './card.types'

/**
 * The class list for a card, for any stack. A Vue or Svelte consumer calls this — or writes
 * the same classes by hand — and gets what the React component renders.
 *
 * The content padding is a modifier on the card rather than on the element it pads, so one
 * list covers the whole component: the rule behind it reaches `lmnd-card__content`.
 */
export function cardClasses(appearance: CardAppearance = {}): string {
  const { background = 'default', contentPadding = 'none' } = appearance
  return [
    'lmnd-card',
    `lmnd-card--${background}`,
    `lmnd-card--padding-${kebab(contentPadding)}`,
  ].join(' ')
}
