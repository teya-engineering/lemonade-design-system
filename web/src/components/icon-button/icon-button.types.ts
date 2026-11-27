import type { LemonadeButtonSize, LemonadeButtonType, LemonadeButtonVariant } from '../button/button.types'

export type LemonadeIconButtonShape = 'rounded' | 'circular'

/**
 * What `iconButtonClasses` needs, and the whole vocabulary a non-React stack consumes.
 * Variant, emphasis and size are Button's — the platforms share those three enums between
 * the two components rather than declaring a second set.
 *
 * Nothing here may reference React. The root export reaches these types, and both entries
 * share this module, so a React type here lands in a shared declaration chunk that the
 * framework-free root then has to import. `IconButtonProps` lives in `icon-button.tsx` for
 * that reason.
 */
export type IconButtonAppearance = {
  variant?: LemonadeButtonVariant
  emphasis?: LemonadeButtonType
  size?: LemonadeButtonSize
  shape?: LemonadeIconButtonShape
  loading?: boolean
}
