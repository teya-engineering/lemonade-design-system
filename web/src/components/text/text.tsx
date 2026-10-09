import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { textStyles } from '../../text-styles.generated'
import type { LemonadeTextStyle } from './text.types'

/**
 * Lemonade text carries no semantics of its own, so the element is the caller's choice.
 * Restricted to the ones a text style is plausibly applied to, which keeps the prop
 * useful in an editor instead of accepting any tag.
 */
export type TextElement = 'span' | 'p' | 'div' | 'label' | 'strong' | 'em' | 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6'

export type TextProps = ComponentPropsWithoutRef<'span'> & {
  /** Required: text with nothing in it is not a thing worth rendering. */
  children: ReactNode
  /** Defaults to the style KMP's theme provides ambiently. */
  textStyle?: LemonadeTextStyle
  as?: TextElement
}

export function Text({ children, textStyle = 'bodyMediumRegular', as = 'span', className, ...rest }: TextProps) {
  const Tag = as
  const classes = textStyles[textStyle].className

  return (
    <Tag {...rest} className={className ? `${classes} ${className}` : classes}>
      {children}
    </Tag>
  )
}
