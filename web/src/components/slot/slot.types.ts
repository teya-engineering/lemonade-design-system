import type { ComponentProps, ElementType } from 'react'

/**
 * The element a component renders. Lemonade components carry styling, not semantics, so
 * the tag is the caller's choice: a label that is also a heading, an icon that is also a
 * link.
 */
export type SlotAsProps<E extends ElementType = ElementType> = {
  as?: E
}

/**
 * `as` plus whatever props that element actually takes, so `as="label"` accepts `htmlFor`
 * and `as="a"` accepts `href` — which a fixed prop type cannot express.
 */
export type SlotOwnProps<E extends ElementType, OmitIntrinsic extends string = ''> = SlotAsProps<E> &
  Omit<ComponentProps<E>, keyof SlotAsProps<E> | OmitIntrinsic>

/** A component's own props, plus the element's. `OmitIntrinsic` drops ones it owns. */
export type SlotProps<P extends object, E extends ElementType = ElementType, OmitIntrinsic extends string = ''> = P &
  SlotOwnProps<E, OmitIntrinsic>
