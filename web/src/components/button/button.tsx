import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { Spinner } from '../spinner/spinner'
import { buttonClasses } from './button.classes'
import type { ButtonAppearance } from './button.types'

export type ButtonProps = Omit<ComponentPropsWithoutRef<'button'>, 'disabled' | 'children'> &
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

  return (
    <button
      {...rest}
      type={type}
      className={className ? `${classes} ${className}` : classes}
      disabled={!enabled || loading}
      aria-busy={loading || undefined}
      // The spinner replaces the label, so the accessible name has to come from somewhere.
      aria-label={loading ? label : undefined}
    >
      {loading ? (
        // KMP tints the spinner with the button's content colour and leaves it at the
        // default size; button.css does the tinting, so this stays a plain Spinner.
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
