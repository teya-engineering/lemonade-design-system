import type { LemonadeTextStyles } from '../../text-styles.generated'

/**
 * Mirrors `LemonadeTextStyle`. The names come from the generated manifest, so a style
 * added in Figma is spellable here as soon as the converters run.
 *
 * Nothing in this file may reference React: the root export reaches these types, and a
 * React type here would land in a shared declaration chunk the framework-free root then
 * has to import. `TextProps` lives in `text.tsx` for that reason.
 */
export type LemonadeTextStyle = keyof LemonadeTextStyles
