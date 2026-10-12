import type { ElementType } from 'react'
import { labelling } from '../labelling'
import { Slot } from '../slot/slot'
import type { SlotProps } from '../slot/slot.types'
import { spinnerClasses } from './spinner.classes'
import type { SpinnerAppearance } from './spinner.types'

export const defaultSpinnerElement = 'span'

export type SpinnerOwnProps = SpinnerAppearance & {
  /**
   * What a screen reader should say, or `null` when something adjacent already says it.
   * Never rendered: for visible text, pair a `null` spinner with a Text inside one
   * `role="status"`.
   */
  contentDescription: string | null
}

export type SpinnerProps<E extends ElementType = typeof defaultSpinnerElement> = SlotProps<
  SpinnerOwnProps,
  E,
  'children'
>

/**
 * Shows that something is in progress, without blocking the interface.
 *
 * The ring is a masked conic gradient, so it costs no JavaScript and takes its colour from
 * `color` — which defaults to content-secondary, as it does on the platforms.
 *
 * @example
 * <Spinner contentDescription="Loading your balance" />
 * <Spinner size="small" contentDescription={null} />
 */
export function Spinner<E extends ElementType = typeof defaultSpinnerElement>({
  contentDescription,
  size = 'medium',
  as,
  className,
  ...rest
}: SpinnerProps<E>) {
  const classes = spinnerClasses({ size })

  return (
    <Slot<ElementType>
      as={as ?? defaultSpinnerElement}
      {...labelling(contentDescription, 'status', rest, as === undefined)}
      {...rest}
      className={className ? `${classes} ${className}` : classes}
    />
  )
}
