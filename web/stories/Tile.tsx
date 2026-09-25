import type { CSSProperties, ReactNode } from 'react'

// Every tile in the gallery is built from this, so the icon page and the shadow page
// cannot drift apart. Not a story file — the stories glob only picks up *.stories.tsx.
const tile: CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  gap: 'var(--lmnd-spacing-200)',
  padding: 'var(--lmnd-spacing-800) var(--lmnd-spacing-400)',
  background: 'var(--lmnd-color-bg-elevated)',
  border: 'var(--lmnd-border-width-25) solid var(--lmnd-color-border-neutral-low)',
  borderRadius: 'var(--lmnd-radius-400)',
  color: 'var(--lmnd-color-content-primary)',
  font: 'inherit',
}

const caption: CSSProperties = {
  color: 'var(--lmnd-color-content-secondary)',
  textAlign: 'center',
  // One line, so every tile is the same height and the grid stays a grid. The title
  // attribute carries the full label for the ones that clip.
  maxWidth: '100%',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
}

export function Tile({
  label,
  title,
  highlighted = false,
  onClick,
  children,
}: {
  label: string
  title?: string
  highlighted?: boolean
  onClick?: () => void
  children: ReactNode
}) {
  const style: CSSProperties = {
    ...tile,
    ...(highlighted ? { background: 'var(--lmnd-color-bg-brand-subtle)' } : null),
    ...(onClick ? { cursor: 'pointer' } : null),
  }
  const body = (
    <>
      {children}
      <code className="lmnd-text-body-xsmall-regular" style={caption}>
        {label}
      </code>
    </>
  )

  // A tile that does nothing is not a control, so it must not be focusable.
  return onClick ? (
    <button type="button" onClick={onClick} title={title} style={style}>
      {body}
    </button>
  ) : (
    <div title={title} style={style}>
      {body}
    </div>
  )
}
