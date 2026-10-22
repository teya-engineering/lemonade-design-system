import type { ComponentPropsWithoutRef, ReactNode } from 'react'
import { Icon, defaultIconBasePath } from '../icon/icon'
import { cardClasses } from './card.classes'
import type { CardAppearance, LemonadeCardHeadingStyle } from './card.types'

/** Mirrors `CardHeaderConfig`, with React nodes where the platforms take a builder. */
export type CardHeader = {
  title: string
  headingStyle?: LemonadeCardHeadingStyle
  subtitle?: string
  leadingSlot?: ReactNode
  trailingSlot?: ReactNode
  showNavigationIndicator?: boolean
}

/** Mirrors `CardFooterActionConfig`. */
export type CardFooterAction = {
  label: string
  onClick: () => void
}

export type CardProps = Omit<ComponentPropsWithoutRef<'div'>, 'dangerouslySetInnerHTML'> &
  CardAppearance & {
    header?: CardHeader
    footerAction?: CardFooterAction
    /**
     * Serving the icons elsewhere — a CDN, a different public directory — changes this. Only
     * the header's navigation indicator reads it; everything else in a card is the caller's.
     */
    basePath?: string
  }

const TITLE_TEXT_STYLE: Record<LemonadeCardHeadingStyle, string> = {
  default: 'lmnd-text-heading-xxsmall',
  overline: 'lmnd-text-body-xsmall-overline',
}

/**
 * A container for a section of a screen: an optional header, the content, and an optional
 * action across the bottom.
 *
 * The header and the footer action are object props, as they are on both platforms, so the
 * vocabulary is the same one — `title`, `headingStyle`, `showNavigationIndicator`. Their
 * slots are React nodes rather than builders, which §17.5 leaves to each platform.
 *
 * @example
 * <Card header={{ title: 'Recent activity' }} contentPadding="medium">…</Card>
 * <Card footerAction={{ label: 'See all', onClick: showAll }}>…</Card>
 */
export function Card({
  background = 'default',
  contentPadding = 'none',
  header,
  footerAction,
  basePath = defaultIconBasePath,
  className,
  children,
  ...rest
}: CardProps) {
  const classes = cardClasses({ background, contentPadding })

  return (
    <div {...rest} className={className ? `${classes} ${className}` : classes}>
      {header ? <CardHeaderRow {...header} basePath={basePath} /> : null}
      <div className="lmnd-card__content">{children}</div>
      {footerAction ? (
        <button type="button" className="lmnd-card__footer-action lmnd-text-body-small-semibold" onClick={footerAction.onClick}>
          {footerAction.label}
        </button>
      ) : null}
    </div>
  )
}

function CardHeaderRow({
  title,
  headingStyle = 'default',
  subtitle,
  leadingSlot,
  trailingSlot,
  showNavigationIndicator = false,
  basePath,
}: CardHeader & { basePath: string }) {
  const classes =
    headingStyle === 'overline' ? 'lmnd-card__header lmnd-card__header--overline' : 'lmnd-card__header'

  return (
    <div className={classes}>
      {leadingSlot}
      <div className="lmnd-card__header-text">
        <span className={`lmnd-card__title ${TITLE_TEXT_STYLE[headingStyle]}`}>{title}</span>
        {/* Blank, not just absent: a subtitle the caller computed and came up empty on would
            otherwise render as an empty second line under the title. */}
        {subtitle?.trim() ? (
          <span className="lmnd-card__subtitle lmnd-text-body-small-regular">{subtitle}</span>
        ) : null}
      </div>
      {trailingSlot}
      {showNavigationIndicator ? (
        // Decorative: it repeats what the card's own action already says, and the platforms
        // pass no description for it either.
        <Icon
          use="chevron-right"
          contentDescription={null}
          basePath={basePath}
          className="lmnd-card__navigation-indicator"
        />
      ) : null}
    </div>
  )
}
