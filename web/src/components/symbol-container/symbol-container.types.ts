export type SymbolContainerVoice =
  | 'neutral'
  | 'critical'
  | 'warning'
  | 'info'
  | 'positive'
  | 'brand'
  | 'brandSubtle'

export type SymbolContainerSize = 'xSmall' | 'small' | 'medium' | 'large' | 'xLarge' | 'xxLarge'

export type SymbolContainerShape = 'circle' | 'rounded'

/**
 * What `symbolContainerClasses` needs, and the whole vocabulary a non-React stack consumes.
 *
 * `voice` is absent when the surface is themed instead: a themed container sets the same
 * three custom properties from a hue, so there is no voice class on it. See
 * `themedSurface` in `src/themed-hue.ts`.
 *
 * Nothing here may reference React. The root export reaches these types, and both entries
 * share this module, so a React type here lands in a shared declaration chunk that the
 * framework-free root then has to import. `SymbolContainerProps` lives in the `.tsx` for
 * that reason, along with the badge slot.
 */
export type SymbolContainerAppearance = {
  voice?: SymbolContainerVoice | null
  size?: SymbolContainerSize
  shape?: SymbolContainerShape
}
