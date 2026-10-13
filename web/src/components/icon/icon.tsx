import type { ComponentPropsWithoutRef, CSSProperties } from 'react'
import { iconNames } from '../../icons.generated'
import type { IconName } from '../../icons.generated'
import { iconClasses } from './icon.classes'
import type { IconAppearance } from './icon.types'

/** Where the package's `dist/assets/icons` are served from. */
export const defaultIconBasePath = '/assets/icons'

export type IconProps = Omit<ComponentPropsWithoutRef<'span'>, 'children'> &
  IconAppearance & {
    /** The icon's name, e.g. `heart`, `arrow-right`. */
    use: IconName
    /**
     * What a screen reader should say, or `null` when the icon repeats adjacent text and
     * should be skipped. Named as the platforms name it, and required for the same reason
     * they make it required-but-nullable: which one applies is never the component's call.
     * It is never rendered — visible text beside an icon is the caller's own markup.
     */
    contentDescription: string | null
    /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
    basePath?: string
  }

/**
 * Renders a Lemonade icon as a masked span, so it takes its colour from `currentColor` and
 * needs no fill rewriting.
 *
 * The element is fixed, as it is on the platforms. An icon that is also a link or a button
 * is that element wrapping this one, which keeps the interactive semantics where the
 * browser already puts them.
 *
 * @example
 * <Icon use="heart" contentDescription="Favourite" />
 * <a href="/favourites"><Icon use="heart" contentDescription="Favourites" /></a>
 */
export function Icon({
  use,
  contentDescription,
  size = 'medium',
  basePath = defaultIconBasePath,
  className,
  style,
  ...rest
}: IconProps) {
  // A name off the generated list means the mask resolves to nothing and the element
  // renders as blank space — silent, and easy to miss in a prototype.
  if (!iconNames.includes(use)) {
    console.error(new Error(`Lemonade: there is no icon called "${use}"`))
  }

  const classes = iconClasses({ size })

  // Filled in only where the caller left no value, then spread after their props. The other
  // order looks equivalent and is not: a wrapper forwarding `aria-label={undefined}` would
  // spread that over the name and leave the icon unlabelled.
  const aria: Record<string, unknown> = {}
  if (contentDescription === null) {
    if (rest['aria-hidden'] === undefined) aria['aria-hidden'] = true
  } else {
    if (rest.role === undefined) aria.role = 'img'
    if (rest['aria-label'] === undefined) aria['aria-label'] = contentDescription
  }

  return (
    <span
      {...rest}
      {...aria}
      className={className ? `${classes} ${className}` : classes}
      style={{ ...(style as CSSProperties), '--lmnd-icon': `url('${basePath}/${use}.svg')` } as CSSProperties}
    />
  )
}
