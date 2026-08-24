import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties } from 'react'
import { useMemo, useState } from 'react'
import { iconNames } from '../src/index'

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
    <button
      type="button"
      onClick={handleCopy}
      title={`Click to copy "${name}"`}
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: 'var(--lmnd-spacing-200)',
        padding: 'var(--lmnd-spacing-300)',
        background: copied ? 'var(--lmnd-color-bg-brand-subtle)' : 'var(--lmnd-color-bg-elevated)',
        border: '1px solid var(--lmnd-color-border-neutral-low)',
        borderRadius: 'var(--lmnd-radius-200)',
        cursor: 'pointer',
        color: 'var(--lmnd-color-content-primary)',
        font: 'inherit',
      }}
    >
      <span
        className="lmnd-icon"
        style={
          {
            width: 'var(--lmnd-size-800)',
            height: 'var(--lmnd-size-800)',
            '--lmnd-icon': `url('/assets/icons/${name}.svg')`,
          } as CSSProperties
        }
      />
      <code
        className="lmnd-text-body-xsmall-regular"
        style={{
          color: 'var(--lmnd-color-content-secondary)',
          textAlign: 'center',
          wordBreak: 'break-word',
        }}
      >
        {copied ? 'copied!' : name}
      </code>
    </button>
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
          border: '1px solid var(--lmnd-color-border-neutral-low)',
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
          gridTemplateColumns: 'repeat(auto-fill, minmax(6rem, 1fr))',
          gap: 'var(--lmnd-spacing-300)',
        }}
      >
        {filtered.map((name) => (
          <IconTile key={name} name={name} />
        ))}
      </div>
    </div>
  )
}
