import type { DividerAppearance } from './divider.types'

/**
 * The class list for a divider, for any stack. A Vue or Svelte consumer calls this — or
 * writes the same classes by hand — and gets what the React components render.
 *
 * The base class is a solid horizontal line, so only the modifiers that change it are
 * emitted: a `--solid` or `--horizontal` class would have no rule to carry.
 */
export function dividerClasses(appearance: DividerAppearance = {}): string {
  const { variant = 'solid', orientation = 'horizontal', labelled = false } = appearance
  const classes = ['lmnd-divider']
  if (orientation === 'vertical') classes.push('lmnd-divider--vertical')
  if (variant === 'dashed') classes.push('lmnd-divider--dashed')
  if (labelled) classes.push('lmnd-divider--labelled')
  return classes.join(' ')
}
