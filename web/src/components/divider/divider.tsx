import type { ComponentPropsWithoutRef, HTMLAttributes } from 'react'
import { dividerClasses } from './divider.classes'
import type { DividerVariant } from './divider.types'

// Typed against HTMLElement rather than one tag: the labelled form renders a <div> and the
// plain one an <hr>, and the caller's attributes have to fit either. Both forms render their
// own content, and React throws when that is combined with dangerouslySetInnerHTML.
export type HorizontalDividerProps = Omit<HTMLAttributes<HTMLElement>, 'children' | 'dangerouslySetInnerHTML'> & {
  variant?: DividerVariant
  /**
   * Shown in the middle of the line, as on the platforms. Without it — or with nothing but
   * whitespace — the rule is unbroken.
   */
  label?: string
}

export type VerticalDividerProps = Omit<
  ComponentPropsWithoutRef<'hr'>,
  'children' | 'dangerouslySetInnerHTML'
> & {
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
  // A label of nothing but whitespace is not a label: it would open a gap in the rule with
  // no text in it, and name the separator with an empty string.
  const labelled = label !== undefined && label.trim() !== ''
  const classes = dividerClasses({ variant, labelled })
  const withCaller = className ? `${classes} ${className}` : classes

  // An <hr> is the separator wherever it can be one, but it cannot hold a label, so the
  // labelled form is a div that has to say what it is. The caller can still override the
  // role on either form — a divider used as decoration is theirs to mark as such.
  if (!labelled) {
    return <hr {...rest} className={withCaller} />
  }

  return (
    <div
      {...rest}
      role={rest.role ?? 'separator'}
      aria-label={rest['aria-label'] ?? label}
      className={withCaller}
    >
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
  return (
    <hr
      {...rest}
      // An <hr> is a separator already, but one that reads as horizontal: that is the ARIA
      // default, and the orientation otherwise exists only in the stylesheet.
      aria-orientation={rest['aria-orientation'] ?? 'vertical'}
      className={className ? `${classes} ${className}` : classes}
    />
  )
}
