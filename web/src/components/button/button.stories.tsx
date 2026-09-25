import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties } from 'react'
import { Button } from './button'
import type { LemonadeButtonSize, LemonadeButtonType, LemonadeButtonVariant } from './button.types'

const VARIANTS: LemonadeButtonVariant[] = ['primary', 'secondary', 'neutral', 'critical', 'onBrand', 'onColor']
const TYPES: LemonadeButtonType[] = ['solid', 'subtle', 'ghost']
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

function Icon({ name }: { name: string }) {
  return (
    <span
      className="lmnd-icon"
      style={{ '--lmnd-icon': `url('/assets/icons/${name}.svg')` } as CSSProperties}
      aria-hidden="true"
    />
  )
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
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

const meta: Meta<typeof Button> = {
  title: 'Components/Button',
  component: Button,
  args: { label: 'Add item' },
}
export default meta

export const Variants: StoryObj<typeof Button> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      {VARIANTS.map((variant) => (
        <Row key={variant} label={variant}>
          {(variant === 'onBrand' || variant === 'onColor' ? ['solid' as const] : TYPES).map((type) => (
            <Button key={type} {...args} variant={variant} type={type} />
          ))}
        </Row>
      ))}
    </div>
  ),
}

export const Sizes: StoryObj<typeof Button> = {
  render: (args) => (
    <Row label="sizes">
      {SIZES.map((size) => (
        <Button key={size} {...args} size={size} label={size} />
      ))}
    </Row>
  ),
}

export const States: StoryObj<typeof Button> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      <Row label="default, hover and press are live — try them">
        <Button {...args} />
        <Button {...args} variant="critical" label="Delete" />
      </Row>
      <Row label="disabled">
        <Button {...args} enabled={false} />
        <Button {...args} variant="secondary" enabled={false} label="Secondary solid dims its fill further" />
      </Row>
      <Row label="loading">
        <Button {...args} loading label="Saving" />
        <Button {...args} variant="critical" loading label="Deleting" />
      </Row>
    </div>
  ),
}

export const WithIcons: StoryObj<typeof Button> = {
  render: (args) => (
    <Row label="icons">
      <Button {...args} leadingIcon={<Icon name="plus" />} />
      <Button {...args} trailingIcon={<Icon name="arrow-right" />} label="Continue" />
      <Button {...args} leadingIcon={<Icon name="plus" />} trailingIcon={<Icon name="arrow-right" />} />
    </Row>
  ),
}

/** expandContents stretches the label area, leaving the icons at the edges. */
export const ExpandContents: StoryObj<typeof Button> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-300)', maxWidth: '20rem' }}>
      <Button
        {...args}
        expandContents
        style={{ width: '100%' }}
        leadingIcon={<Icon name="plus" />}
        trailingIcon={<Icon name="arrow-right" />}
      />
      <Button
        {...args}
        style={{ width: '100%' }}
        leadingIcon={<Icon name="plus" />}
        trailingIcon={<Icon name="arrow-right" />}
      />
    </div>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds
 * the same button from the same markup.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <Row label="plain HTML, no React">
      <button
        type="button"
        className="lmnd-button lmnd-button--primary lmnd-button--solid lmnd-button--large lmnd-text-body-medium-semibold"
      >
        <span className="lmnd-button__label">Add item</span>
      </button>
    </Row>
  ),
}
