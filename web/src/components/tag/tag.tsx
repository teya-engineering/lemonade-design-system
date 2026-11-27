import type { ComponentPropsWithoutRef } from 'react'
import type { IconName } from '../../icons.generated'
import { Icon, defaultIconBasePath } from '../icon/icon'
import { tagClasses } from './tag.classes'
import type { TagAppearance } from './tag.types'

export type TagProps = Omit<ComponentPropsWithoutRef<'span'>, 'children' | 'dangerouslySetInnerHTML'> &
  TagAppearance & {
    label: string
    /** An optional glyph before the label, decorative as it is on the platforms. */
    use?: IconName
    /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
    basePath?: string
  }

/**
 * A short, non-interactive label: a status, a category, a count.
 *
 * @example
 * <Tag label="Pending" voice="warning" />
 * <Tag label="Settled" voice="positive" use="check" />
 */
export function Tag({ label, voice = 'neutral', use, basePath = defaultIconBasePath, className, ...rest }: TagProps) {
  const classes = tagClasses({ voice })

  return (
    <span {...rest} className={className ? `${classes} ${className}` : classes}>
      {/* Decorative, as on the platforms: the label beside it already says what this is.
          Plain on purpose — tag.css sizes it, so hand-written markup matches. */}
      {use === undefined ? null : <Icon use={use} contentDescription={null} basePath={basePath} />}
      <span className="lmnd-tag__label">{label}</span>
    </span>
  )
}
