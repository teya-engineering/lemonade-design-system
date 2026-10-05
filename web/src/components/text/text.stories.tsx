import type { Meta, StoryObj } from '@storybook/react'
import { textStyles } from '../../text-styles.generated'
import { Text } from './text'
import type { LemonadeTextStyle } from './text.types'

const STYLES = Object.keys(textStyles) as LemonadeTextStyle[]

const meta: Meta<typeof Text> = {
  title: 'Components/Text',
  component: Text,
}
export default meta

export const AllStyles: StoryObj<typeof Text> = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)' }}>
      {STYLES.map((textStyle) => (
        <div key={textStyle} style={{ display: 'grid', gap: 'var(--lmnd-spacing-100)' }}>
          <Text textStyle={textStyle} as="p" style={{ margin: 0 }}>
            The quick brown fox jumps over the lazy dog
          </Text>
          <Text textStyle="bodyXSmallRegular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
            {textStyle}
          </Text>
        </div>
      ))}
    </div>
  ),
}

/** The element is the caller's: Lemonade text carries no semantics of its own. */
export const Elements: StoryObj<typeof Text> = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-300)' }}>
      <Text textStyle="displaySmall" as="h2" style={{ margin: 0 }}>
        an h2 at display-small
      </Text>
      <Text as="p" style={{ margin: 0 }}>
        a paragraph at body-medium-regular
      </Text>
      <Text textStyle="bodySmallMedium" as="label">
        a label at body-small-medium
      </Text>
    </div>
  ),
}

/** Children rather than a string prop, so a sentence can carry emphasis and links. */
export const WithMarkup: StoryObj<typeof Text> = {
  render: () => (
    <Text as="p" style={{ margin: 0, maxWidth: '28rem' }}>
      Paid <strong>£42.00</strong> to <a href="#merchant">Merchant</a> on 4 October. Your{' '}
      <Text textStyle="bodyMediumSemiBold">balance</Text> updates within a minute.
    </Text>
  ),
}
