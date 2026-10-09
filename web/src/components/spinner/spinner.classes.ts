import { kebab } from '../classes'
import type { SpinnerAppearance } from './spinner.types'

/**
 * The class list for a spinner, for any stack. A consumer who never loads this JavaScript
 * writes the same classes by hand and gets the same ring.
 */
export function spinnerClasses(appearance: SpinnerAppearance = {}): string {
  const { size = 'medium' } = appearance
  return `lmnd-spinner lmnd-spinner--${kebab(size)}`
}
