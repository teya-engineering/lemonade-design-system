import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties } from 'react'
import { useMemo, useState } from 'react'
import { iconNames } from '../src/index'
import { Tile } from './Tile'

// Named because the grid's column sizing has to compose them.
const GAP = 'var(--lmnd-spacing-300)'
const TILE_MIN = 'var(--lmnd-size-2400)'

function IconTile({ name }: { name: string }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = () => {
    // Best-effort: clipboard API needs a secure context, which the Storybook iframe has.
    navigator.clipboard?.writeText(name).then(
      () => {
        setCopied(true)
        setTimeout(() => setCopied(false), 1200)
      },
      () => {},
    )
  }

  return (
    <Tile
      label={copied ? 'copied!' : name}
      title={`Click to copy "${name}"`}
      highlighted={copied}
      onClick={handleCopy}
    >
      <span
        className="lmnd-icon"
        style={
          {
            width: 'var(--lmnd-size-500)',
            height: 'var(--lmnd-size-500)',
            '--lmnd-icon': `url('/assets/icons/${name}.svg')`,
          } as CSSProperties
        }
      />
    </Tile>
  )
}

const meta: Meta = { title: 'Foundations/Icons' }
export default meta

export const All: StoryObj = {
  render: () => <IconGrid />,
}

function IconGrid() {
  const [query, setQuery] = useState('')
  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase()
    if (!needle) return iconNames
    // Substring match, not prefix — this is the highest-traffic page, and designers
    // search for "arrow" or "card" from the middle of a name just as often.
    return iconNames.filter((name) => name.includes(needle))
  }, [query])

  return (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <input
        type="search"
        placeholder={`Search ${iconNames.length} icons…`}
        value={query}
        onChange={(event) => setQuery(event.target.value)}
        style={{
          padding: 'var(--lmnd-spacing-300)',
          borderRadius: 'var(--lmnd-radius-200)',
          border: 'var(--lmnd-border-width-25) solid var(--lmnd-color-border-neutral-low)',
          background: 'var(--lmnd-color-bg-default)',
          color: 'var(--lmnd-color-content-primary)',
          font: 'inherit',
        }}
      />
      <span className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {filtered.length} of {iconNames.length}
      </span>
      <div
        style={{
          display: 'grid',
          // Five tiles per row at most: a fifth of the row (less the four gaps) is the
          // track's minimum, so auto-fill can never fit a sixth. TILE_MIN is the floor, so
          // a phone-width canvas drops to three or four rather than squeezing five in.
          gridTemplateColumns: `repeat(auto-fill, minmax(max(${TILE_MIN}, calc((100% - 4 * ${GAP}) / 5)), 1fr))`,
          gap: GAP,
        }}
      >
        {filtered.map((name) => (
          <IconTile key={name} name={name} />
        ))}
      </div>
    </div>
  )
}
