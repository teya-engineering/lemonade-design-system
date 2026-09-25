import type { ButtonAppearance, LemonadeButtonSize } from './button.types'

const TEXT_STYLE: Record<LemonadeButtonSize, string> = {
  xSmall: 'lmnd-text-body-small-semibold',
  small: 'lmnd-text-body-small-semibold',
  medium: 'lmnd-text-body-medium-semibold',
  large: 'lmnd-text-body-medium-semibold',
}

/** The vocabularies are camelCase to match the platforms; every stylesheet here is kebab. */
function kebab(value: string): string {
  return value.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)
}

/**
 * The class list for a button, for any stack. A Vue or Svelte consumer calls this — or
 * writes the same classes by hand — and gets what the React component renders.
 */
export function buttonClasses(appearance: ButtonAppearance = {}): string {
  const { variant = 'primary', type = 'solid', size = 'large', loading = false, expandContents = false } = appearance
  const classes = ['lmnd-button', `lmnd-button--${kebab(variant)}`, `lmnd-button--${kebab(size)}`, TEXT_STYLE[size]]
  if (variant !== 'onBrand' && variant !== 'onColor') classes.push(`lmnd-button--${type}`)
  if (loading) classes.push('lmnd-button--loading')
  if (expandContents) classes.push('lmnd-button--expand')
  return classes.join(' ')
}
