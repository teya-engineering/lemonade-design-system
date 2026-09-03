import type { Meta, StoryObj } from '@storybook/react'
import { textStyles } from '../src/index'

const PANGRAM = 'The quick brown fox jumps over the lazy dog — 0123456789'

const meta: Meta = { title: 'Foundations/Typography' }
export default meta

export const All: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-600)' }}>
      {/* Object.values reads whatever the converter emitted, so this list cannot drift
          from text-styles.json / typography.css. */}
      {Object.values(textStyles).map((style) => (
        <div key={style.className} style={{ display: 'grid', gap: 'var(--lmnd-spacing-100)' }}>
          <p className={style.className} style={{ margin: 0, color: 'var(--lmnd-color-content-primary)' }}>
            {PANGRAM}
          </p>
          <code
            className="lmnd-text-body-xsmall-regular"
            style={{ color: 'var(--lmnd-color-content-secondary)' }}
          >
            .{style.className}
          </code>
        </div>
      ))}
    </div>
  ),
}
