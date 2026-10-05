import type { Meta, StoryObj } from '@storybook/react'
import { textStyles } from '../../text-styles.generated'
import { Text } from './text'
import type { LemonadeTextStyle } from './text.types'

const STYLES = Object.keys(textStyles) as LemonadeTextStyle[]

const meta: Meta<typeof Text> = {
  title: 'Components/Text',
  component: Text,
  args: { text: 'The quick brown fox jumps over the lazy dog' },
}
export default meta

export const AllStyles: StoryObj<typeof Text> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      {STYLES.map((textStyle) => (
        <div key={textStyle} style={{ display: 'grid', gap: 'var(--lmnd-spacing-100)' }}>
          <Text {...args} textStyle={textStyle} as="p" style={{ margin: 0 }} />
          <Text
            text={textStyle}
            textStyle="bodyXSmallRegular"
            as="span"
            style={{ color: 'var(--lmnd-color-content-secondary)' }}
          />
        </div>
      ))}
    </div>
  ),
}

/** The element is the caller's: Lemonade text carries no semantics of its own. */
export const Elements: StoryObj<typeof Text> = {
  render: (args) => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-300)' }}>
      <Text {...args} text="an h2 at display-small" textStyle="displaySmall" as="h2" style={{ margin: 0 }} />
      <Text {...args} text="a paragraph at body-medium-regular" as="p" style={{ margin: 0 }} />
      <Text {...args} text="a label at body-small-medium" textStyle="bodySmallMedium" as="label" />
    </div>
  ),
}
