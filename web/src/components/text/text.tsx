import type { ReactNode } from 'react'
import { textStyles } from '../../text-styles.generated'
import { Slot } from '../slot/slot'
import type { SlotProps } from '../slot/slot.types'
import type { LemonadeTextStyle } from './text.types'

/**
 * Lemonade text carries no semantics of its own, so the element is the caller's choice.
 * Constrained rather than any element: a text style belongs on something that holds text,
 * and `as` stays useful in an editor. Icon leaves it open, because an icon sits anywhere.
 */
export type TextElement = 'span' | 'p' | 'div' | 'label' | 'strong' | 'em' | 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6'

export const defaultTextElement = 'span'

export type TextOwnProps = {
  /** Required: text with nothing in it is not a thing worth rendering. */
  children: ReactNode
  /** Defaults to the style KMP's theme provides ambiently. */
  textStyle?: LemonadeTextStyle
}

export type TextProps<E extends TextElement = typeof defaultTextElement> = SlotProps<TextOwnProps, E>

export function Text<E extends TextElement = typeof defaultTextElement>({
  children,
  textStyle = 'bodyMediumRegular',
  as,
  className,
  ...rest
}: TextProps<E>) {
  const classes = textStyles[textStyle].className

  return (
    <Slot<TextElement> as={as ?? defaultTextElement} {...rest} className={className ? `${classes} ${className}` : classes}>
      {children}
    </Slot>
  )
}
