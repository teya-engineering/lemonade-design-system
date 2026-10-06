import type { Meta, StoryObj } from '@storybook/react'
import type { LemonadeAssetSize } from '../../asset-size'
import { Text } from '../text/text'
import { Spinner } from './spinner'

const SIZES: LemonadeAssetSize[] = [
  'xSmall',
  'small',
  'medium',
  'large',
  'xLarge',
  'xxLarge',
  'xxxLarge',
  'xxxxLarge',
]

const meta: Meta<typeof Spinner> = {
  title: 'Components/Spinner',
  component: Spinner,
  args: { label: 'Loading' },
}
export default meta

export const Sizes: StoryObj<typeof Spinner> = {
  render: (args) => (
    <div style={{ display: 'flex', alignItems: 'flex-end', gap: 'var(--lmnd-spacing-500)' }}>
      {SIZES.map((size) => (
        <div key={size} style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)', justifyItems: 'center' }}>
          <Spinner {...args} size={size} />
          <Text textStyle="bodyXSmallRegular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
            {size}
          </Text>
        </div>
      ))}
    </div>
  ),
}

/** The tint defaults to content-secondary, as on the platforms. Setting `color` overrides it. */
export const Colour: StoryObj<typeof Spinner> = {
  render: (args) => (
    <div style={{ display: 'flex', gap: 'var(--lmnd-spacing-500)' }}>
      {[undefined, 'brand-high', 'critical', 'positive'].map((slot) => (
        <div key={slot ?? 'default'} style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)', justifyItems: 'center' }}>
          <Spinner {...args} size="large" style={slot ? { color: `var(--lmnd-color-content-${slot})` } : undefined} />
          <Text textStyle="bodyXSmallRegular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
            {slot ?? 'default'}
          </Text>
        </div>
      ))}
    </div>
  ),
}

/** On a filled surface the spinner inherits nothing, so the colour is set with the rest of the content. */
export const OnColor: StoryObj<typeof Spinner> = {
  name: 'On colour',
  render: (args) => (
    <div
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 'var(--lmnd-spacing-300)',
        padding: 'var(--lmnd-spacing-400) var(--lmnd-spacing-500)',
        borderRadius: 'var(--lmnd-radius-400)',
        background: 'var(--lmnd-color-bg-brand)',
        color: 'var(--lmnd-color-content-on-brand-high)',
      }}
    >
      <Spinner {...args} size="small" label={null} style={{ color: 'inherit' }} />
      <Text textStyle="bodyMediumSemiBold">Saving your changes…</Text>
    </div>
  ),
}

/**
 * The spinner beside its own sentence is decorative — `label={null}`. A spinner standing
 * alone is the announcement, so it carries the label and `role="status"`.
 */
export const Labelling: StoryObj<typeof Spinner> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--lmnd-spacing-300)' }}>
        <Spinner {...args} size="small" label={null} />
        <Text>Checking your balance — the spinner is decorative here</Text>
      </span>
      <Spinner {...args} label="Checking your balance" />
    </div>
  ),
}
