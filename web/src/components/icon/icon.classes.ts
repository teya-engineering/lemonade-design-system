import { kebab } from '../classes'
import type { IconAppearance } from './icon.types'

/**
 * The class list for an icon, for any stack. A Vue or Svelte consumer calls this — or
 * writes the same classes by hand — and gets what the React component renders.
 */
export function iconClasses(appearance: IconAppearance = {}): string {
  const { size = 'medium' } = appearance
  return `lmnd-icon lmnd-icon--${kebab(size)}`
}
