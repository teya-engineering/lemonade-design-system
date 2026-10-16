/** Mirrors `TagVoice`. */
export type TagVoice = 'neutral' | 'critical' | 'warning' | 'info' | 'positive' | 'neutralOnColor' | 'featured'

/**
 * What `tagClasses` needs, and the whole vocabulary a non-React stack consumes.
 *
 * Nothing here may reference React. The root export reaches these types, and both entries
 * share this module, so a React type here lands in a shared declaration chunk that the
 * framework-free root then has to import. `TagProps` lives in `tag.tsx` for that reason.
 */
export type TagAppearance = {
  voice?: TagVoice
}
