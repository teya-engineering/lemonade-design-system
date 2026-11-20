import { kebab } from '../classes'
import type { TagAppearance } from './tag.types'

/**
 * The class list for a tag, for any stack. A Vue or Svelte consumer calls this — or writes
 * the same classes by hand — and gets what the React component renders.
 */
export function tagClasses(appearance: TagAppearance = {}): string {
  const { voice = 'neutral' } = appearance
  return `lmnd-tag lmnd-tag--${kebab(voice)} lmnd-text-body-xsmall-semibold`
}
