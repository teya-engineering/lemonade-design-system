import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { buttonClasses } from './button.classes'
import type { ButtonAppearance } from './button.types'

export type ButtonProps = Omit<ComponentPropsWithoutRef<'button'>, 'type' | 'disabled' | 'children'> &
  ButtonAppearance & {
    label: string
    leadingIcon?: ReactNode
    trailingIcon?: ReactNode
    /** Mirrors the platforms' `enabled`; the DOM's inverted `disabled` is not exposed. */
    enabled?: boolean
    /**
     * The DOM `type` attribute, which `type` itself shadows. Left at `button` so a
     * button inside a form does not submit it by accident.
     */
    htmlType?: 'button' | 'submit' | 'reset'
  }

export function Button({
  label,
  variant = 'primary',
  type = 'solid',
  size = 'large',
  enabled = true,
  loading = false,
  expandContents = false,
  leadingIcon,
  trailingIcon,
  htmlType = 'button',
  className,
  ...rest
}: ButtonProps) {
  const classes = buttonClasses({ variant, type, size, loading, expandContents })

  return (
    <button
      {...rest}
      type={htmlType}
      className={className ? `${classes} ${className}` : classes}
      disabled={!enabled || loading}
      aria-busy={loading || undefined}
      // The spinner replaces the label, so the accessible name has to come from somewhere.
      aria-label={loading ? label : undefined}
    >
      {loading ? (
        <span className="lmnd-button__spinner" aria-hidden="true" />
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
