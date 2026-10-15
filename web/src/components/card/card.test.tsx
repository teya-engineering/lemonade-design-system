/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import cardCss from './card.css?raw'
import typographyCss from '../../../styles/typography.css?raw'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { Card } from './card'
import { cardClasses } from './card.classes'
import type { LemonadeCardBackground, LemonadeCardHeadingStyle, LemonadeCardPadding } from './card.types'

const exhaustive = <T extends string>(members: Record<T, true>) => Object.keys(members) as T[]

const BACKGROUNDS = exhaustive<LemonadeCardBackground>({ default: true, subtle: true, elevated: true })
const PADDINGS = exhaustive<LemonadeCardPadding>({ none: true, xSmall: true, small: true, medium: true })
const HEADING_STYLES = exhaustive<LemonadeCardHeadingStyle>({ default: true, overline: true })

describe('Card', () => {
  it('renders its children inside the content element', () => {
    render(<Card data-testid="card">Balance</Card>)
    expect(screen.getByTestId('card').querySelector('.lmnd-card__content')).toHaveTextContent('Balance')
  })

  it('defaults to the default background and no content padding', () => {
    render(<Card data-testid="card">Balance</Card>)
    expect(screen.getByTestId('card')).toHaveClass('lmnd-card', 'lmnd-card--default', 'lmnd-card--padding-none')
  })

  it('renders the class list cardClasses returns, so a non-React consumer gets the same result', () => {
    const appearance = { background: 'elevated', contentPadding: 'medium' } as const
    render(
      <Card {...appearance} data-testid="card">
        Balance
      </Card>,
    )
    expect(screen.getByTestId('card').className).toBe(cardClasses(appearance))
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(
      <Card contentPadding="xSmall" data-testid="card">
        Balance
      </Card>,
    )
    expect(screen.getByTestId('card')).toHaveClass('lmnd-card--padding-x-small')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(
      <Card className="activity-card" data-testid="card">
        Balance
      </Card>,
    )
    expect(screen.getByTestId('card')).toHaveClass('lmnd-card', 'activity-card')
  })

  it('renders no header or footer unless given one', () => {
    render(<Card data-testid="card">Balance</Card>)
    const card = screen.getByTestId('card')
    expect(card.querySelector('.lmnd-card__header')).toBeNull()
    expect(card.querySelector('.lmnd-card__footer-action')).toBeNull()
  })

  describe('header', () => {
    it('shows the title', () => {
      render(<Card header={{ title: 'Recent activity' }}>Balance</Card>)
      expect(screen.getByText('Recent activity')).toHaveClass('lmnd-card__title')
    })

    it('shows the subtitle when there is one', () => {
      render(<Card header={{ title: 'Recent activity', subtitle: 'Last 30 days' }}>Balance</Card>)
      expect(screen.getByText('Last 30 days')).toHaveClass('lmnd-card__subtitle')
    })

    it('renders no subtitle element when there is none', () => {
      render(
        <Card header={{ title: 'Recent activity' }} data-testid="card">
          Balance
        </Card>,
      )
      expect(screen.getByTestId('card').querySelector('.lmnd-card__subtitle')).toBeNull()
    })

    it('renders the leading and trailing slots around the text', () => {
      render(
        <Card
          header={{
            title: 'Recent activity',
            leadingSlot: <span data-testid="leading" />,
            trailingSlot: <span data-testid="trailing" />,
          }}
        >
          Balance
        </Card>,
      )
      expect(screen.getByTestId('leading')).toBeInTheDocument()
      expect(screen.getByTestId('trailing')).toBeInTheDocument()
    })

    it('adds the navigation indicator when asked, as a decorative glyph', () => {
      render(<Card header={{ title: 'Recent activity', showNavigationIndicator: true }}>Balance</Card>)
      const indicator = document.querySelector('.lmnd-card__navigation-indicator')
      expect(indicator).toHaveClass('lmnd-icon')
      expect(indicator).toHaveAttribute('aria-hidden', 'true')
    })

    it('omits the navigation indicator by default', () => {
      render(<Card header={{ title: 'Recent activity' }}>Balance</Card>)
      expect(document.querySelector('.lmnd-card__navigation-indicator')).toBeNull()
    })

    it.each(HEADING_STYLES)('titles a %s heading with the style the platforms use', (headingStyle) => {
      render(<Card header={{ title: 'Recent activity', headingStyle }}>Balance</Card>)
      const expected = headingStyle === 'overline' ? 'lmnd-text-body-xsmall-overline' : 'lmnd-text-heading-xxsmall'
      expect(screen.getByText('Recent activity')).toHaveClass(expected)
    })

    // The overline's colour and its taller text box both hang off this one modifier, so a
    // header that loses it looks like a default heading in the wrong type style.
    it('marks the overline heading on the header', () => {
      render(<Card header={{ title: 'Activity', headingStyle: 'overline' }}>Balance</Card>)
      expect(document.querySelector('.lmnd-card__header')).toHaveClass('lmnd-card__header--overline')
    })
  })

  describe('footerAction', () => {
    it('renders a button with the label', () => {
      render(<Card footerAction={{ label: 'See all', onClick: vi.fn() }}>Balance</Card>)
      expect(screen.getByRole('button', { name: 'See all' })).toHaveClass('lmnd-card__footer-action')
    })

    it('calls onClick', async () => {
      const onClick = vi.fn()
      render(<Card footerAction={{ label: 'See all', onClick }}>Balance</Card>)
      await userEvent.click(screen.getByRole('button', { name: 'See all' }))
      expect(onClick).toHaveBeenCalledTimes(1)
    })

    it('does not submit a form it sits in', () => {
      render(<Card footerAction={{ label: 'See all', onClick: vi.fn() }}>Balance</Card>)
      expect(screen.getByRole('button', { name: 'See all' })).toHaveAttribute('type', 'button')
    })
  })
})

describe('cardClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(cardClasses()).toBe('lmnd-card lmnd-card--default lmnd-card--padding-none')
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us.
describe('the class contract', () => {
  const emitted = new Set<string>()
  for (const background of BACKGROUNDS) {
    for (const contentPadding of PADDINGS) {
      for (const name of cardClasses({ background, contentPadding }).split(' ')) emitted.add(name)
    }
  }

  /** Rendered by the component rather than returned by cardClasses. */
  const elements = [
    'lmnd-card__header',
    'lmnd-card__header--overline',
    'lmnd-card__header-text',
    'lmnd-card__title',
    'lmnd-card__subtitle',
    'lmnd-card__navigation-indicator',
    'lmnd-card__content',
    'lmnd-card__footer-action',
  ]

  it('emits the set of classes this suite checks', () => {
    expect([...emitted].sort()).toEqual([
      'lmnd-card',
      'lmnd-card--default',
      'lmnd-card--elevated',
      'lmnd-card--padding-medium',
      'lmnd-card--padding-none',
      'lmnd-card--padding-small',
      'lmnd-card--padding-x-small',
      'lmnd-card--subtle',
    ])
  })

  it.each([...emitted, ...elements])('%s has a rule behind it', (name) => {
    expect(new RegExp(`\\.${name}(?![\\w-])`).test(cardCss)).toBe(true)
  })

  // The padding modifier sits on the card and the padding lands on the content, so each one
  // has to reach that element — a rule that styled the card itself would pad the header too.
  it.each(PADDINGS)('%s padding reaches the content element', (padding) => {
    const modifier = `lmnd-card--padding-${padding.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)}`
    expect(new RegExp(`\\.${modifier} \\.lmnd-card__content(?![\\w-])`).test(cardCss)).toBe(true)
  })

  it.each(['lmnd-text-heading-xxsmall', 'lmnd-text-body-xsmall-overline', 'lmnd-text-body-small-regular', 'lmnd-text-body-small-semibold'])(
    '%s is a generated text style',
    (name) => {
      expect(typographyCss).toContain(`.${name} {`)
    },
  )
})
