import type { ElementType } from 'react'
import type { SlotOwnProps } from './slot.types'

export type { SlotAsProps, SlotOwnProps, SlotProps } from './slot.types'

export const defaultSlotElement = 'span'

/**
 * Renders the element `as` names, with that element's own props typed.
 *
 * The generic defaults to the element this actually renders, not `ElementType`: with the
 * looser default, a `<Slot>` written without `as` type-checks against every element's
 * props at once, so a typo like `hrf` is accepted on a span.
 */
export function Slot<E extends ElementType = typeof defaultSlotElement>(props: SlotOwnProps<E>) {
  const { as, ...rest } = props
  const Element = (as ?? defaultSlotElement) as ElementType

  return <Element {...rest} />
}
