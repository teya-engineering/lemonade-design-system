import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { Spinner } from '../spinner/spinner'
import { buttonClasses } from './button.classes'
import type { ButtonAppearance } from './button.types'

export type ButtonProps = Omit<
  ComponentPropsWithoutRef<'button'>,
  // `disabled` is inverted as `enabled`; the other two cannot work on a component that
  // always renders its own children — React throws when both are given.
  'disabled' | 'children' | 'dangerouslySetInnerHTML'
> &
  ButtonAppearance & {
    label: string
    leadingIcon?: ReactNode
    trailingIcon?: ReactNode
    /** Mirrors the platforms' `enabled`; the DOM's inverted `disabled` is not exposed. */
    enabled?: boolean
  }

export function Button({
  label,
  variant = 'primary',
  emphasis = 'solid',
  size = 'large',
  enabled = true,
  loading = false,
  expandContents = false,
  leadingIcon,
  trailingIcon,
  // The DOM defaults a button in a form to "submit"; defaulting to "button" means adding
  // one to a form cannot submit it by accident.
  type = 'button',
  className,
  ...rest
}: ButtonProps) {
  const classes = buttonClasses({ variant, emphasis, size, loading, expandContents })

  // Filled in only where the caller left no value, then spread after their props: a wrapper
  // forwarding `aria-label={undefined}` would otherwise leave the spinner-only button
  // unnamed.
  const aria: Record<string, unknown> = {}
  if (loading) {
    if (rest['aria-busy'] === undefined) aria['aria-busy'] = true
    if (rest['aria-label'] === undefined) aria['aria-label'] = label
  }

  return (
    <button
      {...rest}
      {...aria}
      type={type}
      className={className ? `${classes} ${className}` : classes}
      disabled={!enabled || loading}
    >
      {loading ? (
        // Plain on purpose: button.css tints it, so hand-written markup gets the same
        // colour as this does.
        <Spinner contentDescription={null} />
      ) : (
        <>
          {leadingIcon}
          <span className="lmnd-button__label">{label}</span>
          {trailingIcon}
        </>
      )}
    </button>
  )
}
