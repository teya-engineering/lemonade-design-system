import type { CSSProperties, ElementType } from 'react'
import { iconNames } from '../../icons.generated'
import type { IconName } from '../../icons.generated'
import { Slot } from '../slot/slot'
import type { SlotProps } from '../slot/slot.types'
import { iconClasses } from './icon.classes'
import type { IconAppearance } from './icon.types'

/** Where the package's `dist/assets/icons` are served from. */
export const defaultIconBasePath = '/assets/icons'

export const defaultIconElement = 'span'

export type IconOwnProps = IconAppearance & {
  /** The icon's name, e.g. `heart`, `arrow-right`. */
  use: IconName
  /**
   * What a screen reader should say, or `null` when the icon repeats adjacent text and
   * should be skipped. Required, like the platforms' `contentDescription`, because which
   * one applies is never the component's call.
   */
  label: string | null
  /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
  basePath?: string
}

export type IconProps<E extends ElementType = typeof defaultIconElement> = SlotProps<IconOwnProps, E, 'children'>

/**
 * Renders a Lemonade icon as a masked element, so it takes its colour from `currentColor`
 * and needs no fill rewriting.
 *
 * @example
 * <Icon use="heart" label="Favourite" />
 * <Icon use="arrow-right" size="small" label={null} />
 */
export function Icon<E extends ElementType = typeof defaultIconElement>({
  use,
  label,
  size = 'medium',
  basePath = defaultIconBasePath,
  className,
  style,
  ...rest
}: IconProps<E>) {
  // A name off the generated list means the mask resolves to nothing and the element
  // renders as blank space — silent, and easy to miss in a prototype.
  if (!iconNames.includes(use)) {
    console.error(new Error(`Lemonade: there is no icon called "${use}"`))
  }

  const classes = iconClasses({ size })

  return (
    <Slot<ElementType>
      as={defaultIconElement}
      {...rest}
      className={className ? `${classes} ${className}` : classes}
      style={{ ...(style as CSSProperties), '--lmnd-icon': `url('${basePath}/${use}.svg')` } as CSSProperties}
      role={label === null ? undefined : 'img'}
      aria-label={label ?? undefined}
      aria-hidden={label === null ? true : undefined}
    />
  )
}
