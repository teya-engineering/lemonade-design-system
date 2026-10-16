import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties, ReactNode } from 'react'
import { Tag } from './tag'
import type { TagVoice } from './tag.types'

const VOICES: TagVoice[] = ['neutral', 'critical', 'warning', 'info', 'positive', 'neutralOnColor', 'featured']

/** neutralOnColor is meant to sit on a filled surface, so the grid gives it one. */
const SURFACE: Record<string, CSSProperties> = {
  neutralOnColor: { background: 'var(--lmnd-color-bg-always-dark)' },
}

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      <code className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {label}
      </code>
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          gap: 'var(--lmnd-spacing-200)',
          padding: 'var(--lmnd-spacing-300)',
          borderRadius: 'var(--lmnd-radius-400)',
          ...SURFACE[label.split(' ')[0]],
        }}
      >
        {children}
      </div>
    </div>
  )
}

const meta: Meta<typeof Tag> = {
  title: 'Components/Tag',
  component: Tag,
  args: { label: 'Pending' },
}
export default meta

export const Voices: StoryObj<typeof Tag> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      {VOICES.map((voice) => (
        <Row key={voice} label={voice}>
          <Tag {...args} voice={voice} label={voice} />
          <Tag {...args} voice={voice} label={voice} use="heart" />
        </Row>
      ))}
    </div>
  ),
}

/** A tag never wraps: a label too long for its container truncates. */
export const Truncation: StoryObj<typeof Tag> = {
  render: () => (
    <Row label="constrained to 8rem">
      <div style={{ display: 'flex', width: '8rem' }}>
        <Tag label="A label far longer than the room it has" use="heart" />
      </div>
    </Row>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds the
 * same tag from the same markup. The glyph needs no size modifier — the tag sizes it.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <Row label="plain HTML, no React">
      <span className="lmnd-tag lmnd-tag--positive lmnd-text-body-xsmall-semibold">
        <span className="lmnd-icon" aria-hidden="true" style={{ '--lmnd-icon': "url('/assets/icons/heart.svg')" } as CSSProperties} />
        <span className="lmnd-tag__label">Settled</span>
      </span>
    </Row>
  ),
}
