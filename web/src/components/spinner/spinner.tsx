import type { ElementType } from 'react'
import { labelling } from '../labelling'
import { Slot } from '../slot/slot'
import type { SlotProps } from '../slot/slot.types'
import { spinnerClasses } from './spinner.classes'
import type { SpinnerAppearance } from './spinner.types'

export const defaultSpinnerElement = 'span'

export type SpinnerOwnProps = SpinnerAppearance & {
  /**
   * What a screen reader should say, or `null` when something adjacent already says it —
   * a button whose own label reads "Saving…". Required, like Icon's, because whether the
   * spinner is the announcement or a decoration of one is never the component's call.
   */
  label: string | null
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
 * <Spinner label="Loading your balance" />
 * <Spinner size="small" label={null} />
 */
export function Spinner<E extends ElementType = typeof defaultSpinnerElement>({
  label,
  size = 'medium',
  className,
  ...rest
}: SpinnerProps<E>) {
  const classes = spinnerClasses({ size })

  return (
    <Slot<ElementType>
      as={defaultSpinnerElement}
      {...labelling(label, 'status')}
      {...rest}
      className={className ? `${classes} ${className}` : classes}
    />
  )
}
