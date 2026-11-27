export type LemonadeCardPadding = 'none' | 'xSmall' | 'small' | 'medium'

export type LemonadeCardBackground = 'default' | 'subtle' | 'elevated'

export type LemonadeCardHeadingStyle = 'default' | 'overline'

/**
 * What `cardClasses` needs, and the whole vocabulary a non-React stack consumes.
 *
 * Nothing here may reference React. The root export reaches these types, and both entries
 * share this module, so a React type here lands in a shared declaration chunk that the
 * framework-free root then has to import. `CardProps` lives in `card.tsx` for that reason,
 * along with the header and footer shapes, whose slots are React nodes.
 */
export type CardAppearance = {
  background?: LemonadeCardBackground
  contentPadding?: LemonadeCardPadding
}
