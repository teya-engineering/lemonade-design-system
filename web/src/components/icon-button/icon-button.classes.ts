import { kebab } from '../classes'
import type { IconButtonAppearance } from './icon-button.types'

/**
 * The class list for an icon button, for any stack. A Vue or Svelte consumer calls this —
 * or writes the same classes by hand — and gets what the React component renders.
 */
export function iconButtonClasses(appearance: IconButtonAppearance = {}): string {
  const { variant = 'neutral', emphasis = 'subtle', size = 'medium', shape = 'rounded', loading = false } = appearance
  const classes = [
    'lmnd-icon-button',
    `lmnd-icon-button--${kebab(variant)}`,
    `lmnd-icon-button--${kebab(size)}`,
    `lmnd-icon-button--${shape}`,
  ]
  if (variant !== 'onBrand' && variant !== 'onColor') classes.push(`lmnd-icon-button--${emphasis}`)
  if (loading) classes.push('lmnd-icon-button--loading')
  return classes.join(' ')
}
