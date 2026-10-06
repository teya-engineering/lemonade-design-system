import type { Meta, StoryObj } from '@storybook/react'
import { useMemo, useState } from 'react'
import { iconNames } from '../../icons.generated'
import { Text } from '../text/text'
import { Icon } from './icon'
import type { LemonadeAssetSize } from './icon.types'

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

const meta: Meta<typeof Icon> = {
  title: 'Components/Icon',
  component: Icon,
  args: { use: 'heart', label: 'Favourite' },
}
export default meta

export const Sizes: StoryObj<typeof Icon> = {
  render: (args) => (
    <div style={{ display: 'flex', alignItems: 'flex-end', gap: 'var(--lmnd-spacing-500)' }}>
      {SIZES.map((size) => (
        <div key={size} style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)', justifyItems: 'center' }}>
          <Icon {...args} size={size} />
          <Text textStyle="bodyXSmallRegular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
            {size}
          </Text>
        </div>
      ))}
    </div>
  ),
}

/** The mask takes currentColor, so an icon is coloured by whatever contains it. */
export const Colour: StoryObj<typeof Icon> = {
  render: (args) => (
    <div style={{ display: 'flex', gap: 'var(--lmnd-spacing-500)' }}>
      {['primary', 'secondary', 'brand-high', 'critical', 'positive'].map((slot) => (
        <div
          key={slot}
          style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)', justifyItems: 'center', color: `var(--lmnd-color-content-${slot})` }}
        >
          <Icon {...args} size="large" />
          <Text textStyle="bodyXSmallRegular">{slot}</Text>
        </div>
      ))}
    </div>
  ),
}

/** `label={null}` hides the icon from assistive tech, for when the text beside it says the same. */
export const Labelling: StoryObj<typeof Icon> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--lmnd-spacing-200)' }}>
        <Icon {...args} use="check" label={null} />
        <Text>Payment received — the icon is decorative here</Text>
      </span>
      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--lmnd-spacing-200)' }}>
        <Icon {...args} use="check" label="Payment received" />
        <Text textStyle="bodySmallRegular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
          with a label, it is announced as an image
        </Text>
      </span>
    </div>
  ),
}

export const Every: StoryObj<typeof Icon> = {
  name: 'Every icon',
  render: (args) => {
    const [query, setQuery] = useState('')
    const filtered = useMemo(() => {
      const needle = query.trim().toLowerCase()
      return needle ? iconNames.filter((name) => name.includes(needle)) : iconNames
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
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(max(6rem, calc((100% - 4 * var(--lmnd-spacing-300)) / 5)), 1fr))',
            gap: 'var(--lmnd-spacing-300)',
          }}
        >
          {filtered.map((name) => (
            <div
              key={name}
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 'var(--lmnd-spacing-200)',
                padding: 'var(--lmnd-spacing-800) var(--lmnd-spacing-400)',
                background: 'var(--lmnd-color-bg-elevated)',
                border: 'var(--lmnd-border-width-25) solid var(--lmnd-color-border-neutral-low)',
                borderRadius: 'var(--lmnd-radius-400)',
              }}
            >
              <Icon {...args} use={name} label={null} />
              <Text
                textStyle="bodyXSmallRegular"
                style={{
                  color: 'var(--lmnd-color-content-secondary)',
                  maxWidth: '100%',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {name}
              </Text>
            </div>
          ))}
        </div>
      </div>
    )
  },
}
