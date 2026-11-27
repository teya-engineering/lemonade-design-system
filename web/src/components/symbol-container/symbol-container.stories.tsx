import type { Meta, StoryObj } from '@storybook/react'
import type { CSSProperties, ReactNode } from 'react'
import type { ThemedHue } from '../../themed-hue'
import { Tag } from '../tag/tag'
import { SymbolContainer } from './symbol-container'
import type { SymbolContainerSize, SymbolContainerVoice } from './symbol-container.types'

const VOICES: SymbolContainerVoice[] = ['neutral', 'critical', 'warning', 'info', 'positive', 'brand', 'brandSubtle']
const SIZES: SymbolContainerSize[] = ['xSmall', 'small', 'medium', 'large', 'xLarge', 'xxLarge']
const HUES: ThemedHue[] = [
  'amber',
  'blue',
  'cyan',
  'fuchsia',
  'green',
  'greenLime',
  'indigo',
  'neutral',
  'orange',
  'pink',
  'purple',
  'red',
  'rose',
  'teal',
  'violet',
  'yellow',
  'yellowLime',
]

function Row({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      <code className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {label}
      </code>
      <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 'var(--lmnd-spacing-300)' }}>
        {children}
      </div>
    </div>
  )
}

const meta: Meta<typeof SymbolContainer> = {
  title: 'Components/SymbolContainer',
  component: SymbolContainer,
}
export default meta

export const Voices: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      {VOICES.map((voice) => (
        <Row key={voice} label={voice}>
          <SymbolContainer use="heart" contentDescription={null} voice={voice} />
          <SymbolContainer use="heart" contentDescription={null} voice={voice} shape="rounded" />
          <SymbolContainer text="FM" voice={voice} />
        </Row>
      ))}
    </div>
  ),
}

/** The container and the glyph inside it step up together. */
export const Sizes: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <Row label="circle">
        {SIZES.map((size) => (
          <SymbolContainer key={size} use="heart" contentDescription={null} size={size} voice="brand" />
        ))}
      </Row>
      <Row label="rounded — the radius steps with the size, levelling off at xLarge">
        {SIZES.map((size) => (
          <SymbolContainer key={size} use="heart" contentDescription={null} size={size} shape="rounded" voice="brand" />
        ))}
      </Row>
      <Row label="initials, with the type style each size pairs with">
        {SIZES.map((size) => (
          <SymbolContainer key={size} text="FM" size={size} voice="info" />
        ))}
      </Row>
    </div>
  ),
}

/**
 * A themed container reads one of the seventeen hues instead of a voice, setting the same
 * three properties. The hue is written as `var()` references, so it follows the theme.
 */
export const Themed: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <Row label="solid palettes">
        {HUES.map((hue) => (
          <SymbolContainer key={hue} text={hue.slice(0, 2).toUpperCase()} theme={{ hue }} />
        ))}
      </Row>
      <Row label="subtle palettes">
        {HUES.map((hue) => (
          <SymbolContainer key={hue} text={hue.slice(0, 2).toUpperCase()} theme={{ hue, subtle: true }} />
        ))}
      </Row>
    </div>
  ),
}

/**
 * The badge sits over the bottom-right corner, overhanging by spacing-100. Lemonade has no
 * web Badge yet, so the slot takes any node — a Tag stands in here.
 */
export const WithBadge: StoryObj = {
  render: () => (
    <Row label="badgeSlot">
      <SymbolContainer
        use="heart"
        contentDescription={null}
        size="xLarge"
        voice="brand"
        badgeSlot={<Tag label="3" voice="critical" />}
      />
      <SymbolContainer
        text="FM"
        size="xxLarge"
        theme={{ hue: 'violet' }}
        badgeSlot={<Tag label="New" voice="positive" />}
      />
    </Row>
  ),
}

/** A free slot, for content neither a glyph nor initials. */
export const CustomContent: StoryObj = {
  render: () => (
    <Row label="children">
      <SymbolContainer size="xLarge" voice="neutral">
        <span className="lmnd-text-body-large-semibold">£</span>
      </SymbolContainer>
    </Row>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds the
 * same container from the same markup — including the themed form, whose three properties
 * are written out rather than carried by a class.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-400)' }}>
      <Row label="plain HTML, no React">
        <span className="lmnd-symbol-container lmnd-symbol-container--medium lmnd-symbol-container--circle lmnd-symbol-container--brand">
          <span className="lmnd-icon" aria-hidden="true" style={{ '--lmnd-icon': "url('/assets/icons/heart.svg')" } as CSSProperties} />
        </span>
      </Row>
      <Row label="themed, plain HTML">
        <span
          className="lmnd-symbol-container lmnd-symbol-container--large lmnd-symbol-container--rounded"
          style={
            {
              '--lmnd-symbol-container-bg': 'var(--lmnd-color-themed-violet-subtle-background)',
              '--lmnd-symbol-container-border': 'var(--lmnd-color-themed-violet-subtle-border)',
              '--lmnd-symbol-container-content': 'var(--lmnd-color-themed-violet-subtle-on-background)',
            } as CSSProperties
          }
        >
          <span className="lmnd-text-body-large-semibold">FM</span>
        </span>
      </Row>
    </div>
  ),
}
