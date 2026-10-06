/**
 * Mirrors `LemonadeAssetSize`, which Icon, CountryFlag and Spinner all take. Each entry
 * resolves to the size token Icon uses on the platforms: xSmall is size-300 through
 * xxxxLarge at size-1400.
 *
 * Nothing in this file may reference React: the root export reaches these types, and a
 * React type here would land in a shared declaration chunk the framework-free root then
 * has to import. `IconProps` lives in `icon.tsx` for that reason.
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

/** What `iconClasses` needs — the appearance, with no React in it. */
export type IconAppearance = {
  size?: LemonadeAssetSize
}
