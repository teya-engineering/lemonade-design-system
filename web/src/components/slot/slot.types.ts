import type { ComponentPropsWithoutRef, ElementType } from 'react'

/**
 * The element a component renders, chosen by the caller: a label that is also a heading,
 * an icon that is also a link. A component may add semantics on top of its own default
 * element — Icon names itself an image — but an element named here keeps its own.
 */
export type SlotAsProps<E extends ElementType = ElementType> = {
  as?: E
}

/**
 * `as` plus whatever props that element actually takes, so `as="label"` accepts `htmlFor`
 * and `as="a"` accepts `href` — which a fixed prop type cannot express.
 */
export type SlotOwnProps<E extends ElementType, OmitIntrinsic extends string = ''> = SlotAsProps<E> &
  Omit<ComponentPropsWithoutRef<E>, keyof SlotAsProps<E> | OmitIntrinsic>

/**
 * A component's own props, plus the element's. `OmitIntrinsic` drops ones it owns.
 *
 * The component's keys are removed from the element's rather than intersected with them:
 * a component `size` of `'small' | 'large'` against an `<input>`'s numeric one would
 * otherwise resolve to `never`, making the prop impossible to pass.
 */
export type SlotProps<P extends object, E extends ElementType = ElementType, OmitIntrinsic extends string = ''> = P &
  SlotOwnProps<E, OmitIntrinsic | Extract<keyof P, string>>
