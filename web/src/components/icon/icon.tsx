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
   * should be skipped. Named as the platforms name it, and required for the same reason
   * they make it required-but-nullable: which one applies is never the component's call.
   * It is never rendered — visible text beside an icon is the caller's own markup.
   */
  contentDescription: string | null
  /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
  basePath?: string
}

export type IconProps<E extends ElementType = typeof defaultIconElement> = SlotProps<IconOwnProps, E, 'children'>

/**
 * Renders a Lemonade icon as a masked element, so it takes its colour from `currentColor`
 * and needs no fill rewriting.
 *
 * @example
 * <Icon use="heart" contentDescription="Favourite" />
 * <Icon use="arrow-right" size="small" contentDescription={null} />
 */
export function Icon<E extends ElementType = typeof defaultIconElement>({
  use,
  contentDescription,
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
      role={contentDescription === null ? undefined : 'img'}
      aria-label={contentDescription ?? undefined}
      aria-hidden={contentDescription === null ? true : undefined}
    />
  )
}
