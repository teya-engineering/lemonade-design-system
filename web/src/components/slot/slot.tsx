import type { ElementType } from 'react'
import type { SlotOwnProps } from './slot.types'

export type { SlotAsProps, SlotOwnProps, SlotProps } from './slot.types'

/**
 * Renders the element `as` names, with that element's own props typed.
 *
 * Every Lemonade component styles content without claiming what it means, so the tag
 * belongs to the caller. This keeps that one line in one place instead of each component
 * re-deriving it.
 */
export function Slot<E extends ElementType = ElementType>(props: SlotOwnProps<E>) {
  const { as, ...rest } = props
  const Element = (as ?? 'span') as ElementType

  return <Element {...rest} />
}
