import type { Meta, StoryObj } from '@storybook/react'
import type { ReactNode } from 'react'
import { HorizontalDivider } from '../divider/divider'
import { Icon } from '../icon/icon'
import { IconButton } from '../icon-button/icon-button'
import { Card } from './card'
import type { LemonadeCardBackground, LemonadeCardPadding } from './card.types'

const BACKGROUNDS: LemonadeCardBackground[] = ['default', 'subtle', 'elevated']

/** The page background each card is shown against, chosen so the card is not the same colour. */
const PAGE: Record<LemonadeCardBackground, string> = {
  default: 'subtle',
  subtle: 'default',
  elevated: 'default',
}

/**
 * Every background on a page it contrasts with, derived rather than listed so one added to
 * the union cannot be left out — plus elevated a second time, on the other page. It is the
 * only translucent one, so it is the only one that changes with what is behind it.
 */
const EXAMPLES: { background: LemonadeCardBackground; page: string }[] = [
  ...BACKGROUNDS.map((background) => ({ background, page: PAGE[background] })),
  { background: 'elevated', page: 'subtle' },
]
const PADDINGS: LemonadeCardPadding[] = ['none', 'xSmall', 'small', 'medium']

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

/** The cards sit on the subtle page background, so `default` reads as a raised surface. */
function Page({ children }: { children: ReactNode }) {
  return (
    <div
      style={{
        display: 'grid',
        gap: 'var(--lmnd-spacing-500)',
        maxWidth: '26rem',
        padding: 'var(--lmnd-spacing-400)',
        background: 'var(--lmnd-color-bg-subtle)',
      }}
    >
      {children}
    </div>
  )
}

const meta: Meta<typeof Card> = {
  title: 'Components/Card',
  component: Card,
}
export default meta

/**
 * Each card gets the page background it contrasts with, because `default` and `subtle` are
 * the two page colours themselves: a card disappears on the page that matches it. `elevated`
 * is shown on both, being the only translucent one — 5% ink rather than an opaque grey, so
 * it takes a tint from the page and lands on a different colour over each.
 */
export const Backgrounds: StoryObj<typeof Card> = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-500)', maxWidth: '26rem' }}>
      {EXAMPLES.map(({ background, page }) => (
        <Row key={`${background}-on-${page}`} label={`${background} — on a ${page} page`}>
          <div style={{ padding: 'var(--lmnd-spacing-400)', background: `var(--lmnd-color-bg-${page})` }}>
            <Card background={background} contentPadding="medium">
              <span className="lmnd-text-body-medium-regular">£1,204.50 available</span>
            </Card>
          </div>
        </Row>
      ))}
    </div>
  ),
}

export const ContentPadding: StoryObj<typeof Card> = {
  render: () => (
    <Page>
      {PADDINGS.map((contentPadding) => (
        <Row key={contentPadding} label={contentPadding}>
          <Card contentPadding={contentPadding}>
            <span className="lmnd-text-body-medium-regular">£1,204.50 available</span>
          </Card>
        </Row>
      ))}
    </Page>
  ),
}

export const WithHeader: StoryObj<typeof Card> = {
  render: () => (
    <Page>
      <Row label="title only">
        <Card header={{ title: 'Recent activity' }} contentPadding="medium">
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
      <Row label="title and subtitle">
        <Card header={{ title: 'Recent activity', subtitle: 'Last 30 days' }} contentPadding="medium">
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
      <Row label="overline heading">
        <Card header={{ title: 'Recent activity', headingStyle: 'overline' }} contentPadding="medium">
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
      <Row label="leading and trailing slots, and the navigation indicator">
        <Card
          header={{
            title: 'Recent activity',
            subtitle: 'Last 30 days',
            leadingSlot: <Icon use="receipt" contentDescription={null} />,
            trailingSlot: <IconButton use="ellipsis-vertical" contentDescription="More actions" size="xSmall" emphasis="ghost" />,
            showNavigationIndicator: true,
          }}
          contentPadding="medium"
        >
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
      <Row label="a long title truncates rather than pushing the slots out">
        <Card
          header={{
            title: 'Recent activity across every account and every card you hold with us',
            trailingSlot: <Icon use="heart" contentDescription={null} />,
            showNavigationIndicator: true,
          }}
          contentPadding="medium"
        >
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
    </Page>
  ),
}

/** The footer fades to half opacity while pressed, as it does on the platforms. */
export const WithFooterAction: StoryObj<typeof Card> = {
  render: () => (
    <Page>
      <Row label="press it">
        <Card
          header={{ title: 'Recent activity' }}
          contentPadding="medium"
          footerAction={{ label: 'See all', onClick: () => {} }}
        >
          <span className="lmnd-text-body-medium-regular">Three payments today</span>
        </Card>
      </Row>
    </Page>
  ),
}

/** The common shape: a header, rows separated by dividers, and an action at the bottom. */
export const AList: StoryObj<typeof Card> = {
  render: () => (
    <Page>
      <Card
        header={{ title: 'Payments', headingStyle: 'overline' }}
        footerAction={{ label: 'See all payments', onClick: () => {} }}
      >
        {['Coffee shop', 'Train fare', 'Groceries'].map((row, index, rows) => (
          <div key={row}>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                padding: 'var(--lmnd-spacing-300) var(--lmnd-spacing-400)',
              }}
            >
              <span className="lmnd-text-body-medium-regular">{row}</span>
              <span className="lmnd-text-body-medium-semibold">£4.20</span>
            </div>
            {index < rows.length - 1 ? <HorizontalDivider /> : null}
          </div>
        ))}
      </Card>
    </Page>
  ),
}

/**
 * The classes are the API, so a stack that never loads this package's JavaScript builds the
 * same card from the same markup.
 */
export const WithoutReact: StoryObj = {
  render: () => (
    <Page>
      <Row label="plain HTML, no React">
        <div className="lmnd-card lmnd-card--default lmnd-card--padding-medium">
          <div className="lmnd-card__header">
            <div className="lmnd-card__header-text">
              <span className="lmnd-card__title lmnd-text-heading-xxsmall">Recent activity</span>
              <span className="lmnd-card__subtitle lmnd-text-body-small-regular">Last 30 days</span>
            </div>
          </div>
          <div className="lmnd-card__content">
            <span className="lmnd-text-body-medium-regular">Three payments today</span>
          </div>
          <button type="button" className="lmnd-card__footer-action lmnd-text-body-small-semibold">
            See all
          </button>
        </div>
      </Row>
    </Page>
  ),
}
