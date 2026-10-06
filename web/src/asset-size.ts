/**
 * Mirrors KMP's `LemonadeAssetSize`, which Icon, Spinner and CountryFlag all take. Each
 * entry resolves to the size token the platforms use: xSmall is size-300 through
 * xxxxLarge at size-1400.
 *
 * Nothing here may reference React. The framework-free root reaches this module, and a
 * React type in it would land in a shared declaration chunk the root then has to import.
 */
export type LemonadeAssetSize =
  | 'xSmall'
  | 'small'
  | 'medium'
  | 'large'
  | 'xLarge'
  | 'xxLarge'
  | 'xxxLarge'
  | 'xxxxLarge'
