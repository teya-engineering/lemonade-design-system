import type { ComponentPropsWithoutRef } from 'react'
import { ariaFor } from '../aria'
import { spinnerClasses } from './spinner.classes'
import type { SpinnerAppearance } from './spinner.types'

export type SpinnerProps = Omit<ComponentPropsWithoutRef<'span'>, 'children'> &
  SpinnerAppearance & {
    /**
     * What a screen reader should say, or `null` when something adjacent already says it.
     * Never rendered: for visible text, pair a `null` spinner with a Text inside one
     * `role="status"`.
     */
    contentDescription: string | null
  }

/**
 * Shows that something is in progress, without blocking the interface.
 *
 * The ring is a masked conic gradient, so it costs no JavaScript and takes its colour from
 * `color` — which defaults to content-secondary, as it does on the platforms.
 *
 * The element is fixed, as it is on the platforms. `role="status"` is what makes the
 * announcement work, and it only applies to an element this component controls.
 *
 * @example
 * <Spinner contentDescription="Loading your balance" />
 * <Spinner size="small" contentDescription={null} />
 */
export function Spinner({ contentDescription, size = 'medium', className, ...rest }: SpinnerProps) {
  const classes = spinnerClasses({ size })

  return (
    <span
      {...rest}
      {...ariaFor(contentDescription, 'status', rest)}
      className={className ? `${classes} ${className}` : classes}
    />
  )
}
