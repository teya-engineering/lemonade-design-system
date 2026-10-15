import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties, ReactNode } from 'react'
import type { LemonadeButtonSize, LemonadeButtonType, LemonadeButtonVariant } from '../button/button.types'
import { IconButton } from './icon-button'

const VARIANTS: LemonadeButtonVariant[] = ['primary', 'secondary', 'neutral', 'critical', 'onBrand', 'onColor']
const EMPHASES: LemonadeButtonType[] = ['solid', 'subtle', 'ghost']
const SIZES: LemonadeButtonSize[] = ['xSmall', 'small', 'medium', 'large']

/**
 * onBrand and onColor are meant to sit on a filled surface, so the grid gives them one.
 * onColor's content is always-light, so its surface has to stay dark in both themes —
 * which is what the always-dark neutral is for.
 */
const SURFACE: Record<string, CSSProperties> = {
  onBrand: { background: 'var(--lmnd-color-bg-brand)' },
  onColor: { background: 'var(--lmnd-color-bg-always-dark)' },
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
          gap: 'var(--lmnd-spacing-300)',
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

const meta: Meta<typeof IconButton> = {
  title: 'Components/IconButton',
  component: IconButton,
  args: { use: 'heart', contentDescription: 'Favourite' },
}
export default meta

export const Variants: StoryObj<typeof IconButton> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      {VARIANTS.map((variant) => (
        <Row key={variant} label={variant}>
          {(variant === 'onBrand' || variant === 'onColor' ? ['solid' as const] : EMPHASES).map((emphasis) => (
            <IconButton key={emphasis} {...args} variant={variant} emphasis={emphasis} />
          ))}
        </Row>
      ))}
    </div>
  ),
}

/** The box, its radius and the glyph inside all step up together. */
export const Sizes: StoryObj<typeof IconButton> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      <Row label="rounded">
        {SIZES.map((size) => (
          <IconButton key={size} {...args} size={size} />
        ))}
      </Row>
      <Row label="circular">
        {SIZES.map((size) => (
          <IconButton key={size} {...args} size={size} shape="circular" />
        ))}
      </Row>
    </div>
  ),
}

export const States: StoryObj<typeof IconButton> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      <Row label="default, hover and press are live — try them">
        <IconButton {...args} />
        <IconButton {...args} use="trash" contentDescription="Delete" variant="critical" />
      </Row>
      <Row label="disabled">
        <IconButton {...args} enabled={false} />
        <IconButton {...args} variant="secondary" emphasis="solid" enabled={false} />
      </Row>
      <Row label="loading — the ring takes the button's content colour">
        <IconButton {...args} loading />
        <IconButton {...args} variant="primary" emphasis="solid" loading />
      </Row>
    </div>
  ),
}

/** A toolbar is the common case: one row of ghost buttons over the page background. */
export const Toolbar: StoryObj<typeof IconButton> = {
  render: () => (
    <Row label="toolbar">
      <IconButton use="search" contentDescription="Search" emphasis="ghost" />
      <IconButton use="bell" contentDescription="Notifications" emphasis="ghost" />
      <IconButton use="share" contentDescription="Share" emphasis="ghost" />
      <IconButton use="ellipsis-vertical" contentDescription="More actions" emphasis="ghost" />
    </Row>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds the
 * same button from the same markup. The glyph needs no size modifier — the button sizes it.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <Row label="plain HTML, no React">
      <button
        type="button"
        aria-label="Favourite"
        className="lmnd-icon-button lmnd-icon-button--neutral lmnd-icon-button--medium lmnd-icon-button--rounded lmnd-icon-button--subtle"
      >
        <span className="lmnd-icon" style={{ '--lmnd-icon': "url('/assets/icons/heart.svg')" } as CSSProperties} />
      </button>
    </Row>
  ),
}
