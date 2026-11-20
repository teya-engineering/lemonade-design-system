import type { Meta, StoryObj } from '@storybook/react'
import type { ReactNode } from 'react'
import { HorizontalDivider, VerticalDivider } from './divider'

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      <code className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {label}
      </code>
      {children}
    </div>
  )
}

const meta: Meta<typeof HorizontalDivider> = {
  title: 'Components/Divider',
  component: HorizontalDivider,
}
export default meta

export const Horizontal: StoryObj<typeof HorizontalDivider> = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)', maxWidth: '24rem' }}>
      <Row label="solid">
        <HorizontalDivider />
      </Row>
      <Row label="dashed — the dash and the gap come from size-100 and spacing-100">
        <HorizontalDivider variant="dashed" />
      </Row>
    </div>
  ),
}

export const Labelled: StoryObj<typeof HorizontalDivider> = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)', maxWidth: '24rem' }}>
      <Row label="solid">
        <HorizontalDivider label="or" />
      </Row>
      <Row label="dashed">
        <HorizontalDivider label="pending" variant="dashed" />
      </Row>
    </div>
  ),
}

/** A vertical divider takes its height from the row, so it needs one to sit in. */
export const Vertical: StoryObj<typeof VerticalDivider> = {
  render: () => (
    <Row label="vertical, solid and dashed">
      <div
        style={{
          display: 'flex',
          alignItems: 'stretch',
          gap: 'var(--lmnd-spacing-400)',
          padding: 'var(--lmnd-spacing-300)',
        }}
      >
        <span className="lmnd-text-body-medium-regular">Today</span>
        <VerticalDivider />
        <span className="lmnd-text-body-medium-regular">This week</span>
        <VerticalDivider variant="dashed" />
        <span className="lmnd-text-body-medium-regular">This month</span>
      </div>
    </Row>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds the
 * same divider from the same markup.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)', maxWidth: '24rem' }}>
      <Row label="plain HTML, no React">
        <hr className="lmnd-divider" />
      </Row>
      <Row label="labelled, plain HTML">
        <div className="lmnd-divider lmnd-divider--labelled" role="separator" aria-label="or">
          <span className="lmnd-divider__line" />
          <span className="lmnd-divider__label lmnd-text-body-small-regular">or</span>
          <span className="lmnd-divider__line" />
        </div>
      </Row>
    </div>
  ),
}
