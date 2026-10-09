/** Mirrors `LemonadeButtonVariant`. */
export type LemonadeButtonVariant = 'primary' | 'secondary' | 'neutral' | 'critical' | 'onBrand' | 'onColor'

/**
 * Mirrors `LemonadeButtonType`. Carried by the `emphasis` prop, because `type` on a
 * <button> is the DOM's own attribute. `onBrand` and `onColor` carry one treatment and
 * ignore it.
 */
export type LemonadeButtonType = 'solid' | 'subtle' | 'ghost'

/** Mirrors `LemonadeButtonSize`. */
export type LemonadeButtonSize = 'xSmall' | 'small' | 'medium' | 'large'

/**
 * What `buttonClasses` needs, and the whole vocabulary a non-React stack consumes.
 *
 * Nothing here may reference React. The root export reaches these types, and both
 * entries share this module, so a React type here lands in a shared declaration chunk
 * that the framework-free root then has to import. `ButtonProps` lives in `button.tsx`
 * for that reason.
 */
export type ButtonAppearance = {
  variant?: LemonadeButtonVariant
  emphasis?: LemonadeButtonType
  size?: LemonadeButtonSize
  loading?: boolean
  expandContents?: boolean
}
