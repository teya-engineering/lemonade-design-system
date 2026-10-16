import type { ComponentPropsWithoutRef, HTMLAttributes } from 'react'
import { dividerClasses } from './divider.classes'
import type { DividerVariant } from './divider.types'

// Typed against HTMLElement rather than one tag: the labelled form renders a <div> and the
// plain one an <hr>, and the caller's attributes have to fit either.
export type HorizontalDividerProps = Omit<HTMLAttributes<HTMLElement>, 'children'> & {
  variant?: DividerVariant
  /** Shown in the middle of the line, as on the platforms. Without it the rule is unbroken. */
  label?: string
}

export type VerticalDividerProps = Omit<ComponentPropsWithoutRef<'hr'>, 'children'> & {
  variant?: DividerVariant
}

/**
 * A horizontal rule, optionally labelled.
 *
 * Two components rather than one with an `orientation` prop, as both platforms declare them.
 *
 * @example
 * <HorizontalDivider />
 * <HorizontalDivider label="or" variant="dashed" />
 */
export function HorizontalDivider({ variant = 'solid', label, className, ...rest }: HorizontalDividerProps) {
  const classes = dividerClasses({ variant, labelled: label !== undefined })
  const withCaller = className ? `${classes} ${className}` : classes

  // An <hr> is the separator wherever it can be one, but it cannot hold the label, so the
  // labelled form is a div that says what it is. Either way the element is this component's,
  // never the caller's: a separator's role is not a choice.
  if (label === undefined) {
    return <hr {...rest} className={withCaller} />
  }

  return (
    <div {...rest} role="separator" aria-label={rest['aria-label'] ?? label} className={withCaller}>
      <span className="lmnd-divider__line" />
      <span className="lmnd-divider__label lmnd-text-body-small-regular">{label}</span>
      <span className="lmnd-divider__line" />
    </div>
  )
}

/**
 * A vertical rule, taking its height from the row it sits in.
 *
 * @example
 * <VerticalDivider />
 */
export function VerticalDivider({ variant = 'solid', className, ...rest }: VerticalDividerProps) {
  const classes = dividerClasses({ variant, orientation: 'vertical' })
  return <hr {...rest} className={className ? `${classes} ${className}` : classes} />
}
