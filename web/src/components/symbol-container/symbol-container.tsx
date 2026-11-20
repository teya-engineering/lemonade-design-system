import type { CSSProperties, ComponentPropsWithoutRef, ReactNode } from 'react'
import type { IconName } from '../../icons.generated'
import { themedSurface } from '../../themed-hue'
import type { ThemedStyle } from '../../themed-hue'
import { Icon, defaultIconBasePath } from '../icon/icon'
import { symbolContainerClasses, symbolContainerTextStyle } from './symbol-container.classes'
import type { SymbolContainerShape, SymbolContainerSize, SymbolContainerVoice } from './symbol-container.types'

/** The values React draws nothing for, and so neither does a slot wrapping them. */
const renders = (node: ReactNode) => node !== undefined && node !== null && typeof node !== 'boolean'

/**
 * One of the three content forms, mirroring the platforms' three pairs of overloads. The
 * `never`s are what make mixing them a type error rather than a silent precedence rule.
 */
type SymbolContainerContent =
  | { use: IconName; contentDescription: string | null; text?: never; children?: never }
  | { text: string; use?: never; contentDescription?: never; children?: never }
  | { children: ReactNode; use?: never; contentDescription?: never; text?: never }

/** Either a voice or a themed hue, never both — again as the platforms overload it. */
type SymbolContainerPalette =
  | { voice?: SymbolContainerVoice; theme?: never }
  | { theme: ThemedStyle; voice?: never }

export type SymbolContainerProps = Omit<ComponentPropsWithoutRef<'span'>, 'children' | 'dangerouslySetInnerHTML'> &
  SymbolContainerContent &
  SymbolContainerPalette & {
    size?: SymbolContainerSize
    shape?: SymbolContainerShape
    /** Sits over the container's bottom-right corner. Any node — Lemonade has no web Badge yet. */
    badgeSlot?: ReactNode
    /** Serving the icons elsewhere — a CDN, a different public directory — changes this. */
    basePath?: string
  }

/**
 * A shaped, tinted container around one symbol: a glyph, a pair of initials, or whatever
 * else a caller puts in it.
 *
 * The platforms reach the same surface through six overloads — three content forms by two
 * palettes. Web takes one component whose props are a union of the same six, since a
 * JavaScript call site has no overload resolution to lean on.
 *
 * @example
 * <SymbolContainer use="heart" contentDescription="Favourite" voice="brand" />
 * <SymbolContainer text="FM" size="large" theme={{ hue: 'violet', subtle: true }} />
 */
export function SymbolContainer({
  use,
  contentDescription,
  text,
  children,
  voice,
  theme,
  size = 'medium',
  shape = 'circle',
  badgeSlot,
  basePath = defaultIconBasePath,
  className,
  style,
  ...rest
}: SymbolContainerProps) {
  // A themed container carries no voice class: it sets the same three properties from its hue.
  const classes = symbolContainerClasses({ voice: theme === undefined ? (voice ?? 'neutral') : null, size, shape })
  const themed = theme === undefined ? undefined : themedSurface(theme)

  return (
    <span
      {...rest}
      className={className ? `${classes} ${className}` : classes}
      style={
        themed === undefined
          ? style
          : ({
              ...(style as CSSProperties),
              '--lmnd-symbol-container-bg': themed.background,
              '--lmnd-symbol-container-border': themed.border,
              '--lmnd-symbol-container-content': themed.content,
            } as CSSProperties)
      }
    >
      {use === undefined ? null : (
        // Plain on purpose: symbol-container.css sizes it per container size, so markup
        // written by hand gets the same glyph without repeating the table.
        <Icon use={use} contentDescription={contentDescription ?? null} basePath={basePath} />
      )}
      {text === undefined ? null : <span className={symbolContainerTextStyle(size)}>{text}</span>}
      {children}
      {/* Neither `!== undefined` nor truthiness: `badgeSlot={unread > 0 && <Badge/>}` passes
          `false` when there is no badge, which would hang an empty box off the corner, and
          `badgeSlot={0}` is a badge that reads zero. What React itself renders as nothing is
          what gets no wrapper. */}
      {renders(badgeSlot) ? <span className="lmnd-symbol-container__badge">{badgeSlot}</span> : null}
    </span>
  )
}
