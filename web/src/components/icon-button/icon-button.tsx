import type { ComponentPropsWithoutRef } from 'react'
import type { IconName } from '../../icons.generated'
import { Icon, defaultIconBasePath } from '../icon/icon'
import { Spinner } from '../spinner/spinner'
import { iconButtonClasses } from './icon-button.classes'
import type { IconButtonAppearance } from './icon-button.types'

export type IconButtonProps = Omit<
  ComponentPropsWithoutRef<'button'>,
  // `disabled` is inverted as `enabled`; the other two cannot work on a component that
  // always renders its own children — React throws when both are given.
  'disabled' | 'children' | 'dangerouslySetInnerHTML'
> &
  IconButtonAppearance & {
    /** The icon's name, e.g. `heart`, `arrow-right`. */
    use: IconName
    /**
     * What a screen reader should say. Required and non-null, unlike the platforms' nullable
     * parameter: there is no text here for it to repeat, so a button without it announces
     * only "button".
     */
    contentDescription: string
    /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
    basePath?: string
    /** Mirrors the platforms' `enabled`; the DOM's inverted `disabled` is not exposed. */
    enabled?: boolean
  }

/**
 * A button whose whole content is one icon.
 *
 * Variant, emphasis and size are Button's, and resolve to the same colours. The defaults
 * are not: the platforms default an icon button to neutral, subtle, medium.
 *
 * @example
 * <IconButton use="heart" contentDescription="Favourite" onClick={save} />
 * <IconButton use="times" contentDescription="Dismiss" shape="circular" variant="critical" />
 */
export function IconButton({
  use,
  contentDescription,
  variant = 'neutral',
  emphasis = 'subtle',
  size = 'medium',
  shape = 'rounded',
  enabled = true,
  loading = false,
  basePath = defaultIconBasePath,
  // The DOM defaults a button in a form to "submit"; defaulting to "button" means adding
  // one to a form cannot submit it by accident.
  type = 'button',
  className,
  ...rest
}: IconButtonProps) {
  // Required and non-null is as far as the type reaches; a blank string still produces the
  // unnamed button the prop exists to prevent, and nothing on screen would show it.
  if (contentDescription.trim() === '') {
    console.error(new Error('Lemonade: IconButton needs a contentDescription — it is the only name it has'))
  }

  const classes = iconButtonClasses({ variant, emphasis, size, shape, loading })

  // Filled in only where the caller left no value, then spread after their props: a wrapper
  // forwarding `aria-label={undefined}` would otherwise leave the button unnamed.
  const aria: Record<string, unknown> = {}
  if (rest['aria-label'] === undefined) aria['aria-label'] = contentDescription
  if (loading && rest['aria-busy'] === undefined) aria['aria-busy'] = true

  return (
    <button
      {...rest}
      {...aria}
      type={type}
      className={className ? `${classes} ${className}` : classes}
      disabled={!enabled || loading}
    >
      {/* Both are decorative: the button carries the name, so announcing the glyph as well
          would say it twice. Plain on purpose — icon-button.css sizes and tints them, so
          hand-written markup gets the same result. */}
      {loading ? <Spinner contentDescription={null} /> : <Icon use={use} contentDescription={null} basePath={basePath} />}
    </button>
  )
}
