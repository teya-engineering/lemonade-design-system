/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import buttonCss from './button.css?raw'
import typographyCss from '../../../styles/typography.css?raw'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { Button } from './button'
import { buttonClasses } from './button.classes'
import type { LemonadeButtonSize, LemonadeButtonType, LemonadeButtonVariant } from './button.types'

/**
 * Annotating these as `T[]` only says every entry is a member, not that every member is an
 * entry — so a value added to a union would leave this suite green while its class went
 * unchecked. Keying a record by the union makes the compiler require all of them, and the
 * values come back out as the list.
 */
const exhaustive = <T extends string>(members: Record<T, true>) => Object.keys(members) as T[]

const VARIANTS = exhaustive<LemonadeButtonVariant>({
  primary: true,
  secondary: true,
  neutral: true,
  critical: true,
  onBrand: true,
  onColor: true,
})
const EMPHASES = exhaustive<LemonadeButtonType>({ solid: true, subtle: true, ghost: true })
const SIZES = exhaustive<LemonadeButtonSize>({ xSmall: true, small: true, medium: true, large: true })


describe('Button', () => {
  it('renders a button with its label', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button', { name: 'Add item' })).toBeInTheDocument()
  })

  it('defaults to primary, solid, large', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button')).toHaveClass(
      'lmnd-button',
      'lmnd-button--primary',
      'lmnd-button--large',
      'lmnd-button--solid',
      'lmnd-text-body-medium-semibold',
    )
  })

  it('renders the class list buttonClasses returns, so a non-React consumer gets the same result', () => {
    const appearance = { variant: 'critical', emphasis: 'ghost', size: 'xSmall' } as const
    render(<Button label="Delete" {...appearance} />)
    expect(screen.getByRole('button').className).toBe(buttonClasses(appearance))
  })

  it('omits the emphasis modifier for the variants that carry one treatment', () => {
    render(<Button label="Continue" variant="onBrand" emphasis="ghost" />)
    expect(screen.getByRole('button').className).not.toContain('lmnd-button--ghost')
    expect(screen.getByRole('button')).toHaveClass('lmnd-button--on-brand')
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(<Button label="Small" size="xSmall" variant="onColor" />)
    expect(screen.getByRole('button')).toHaveClass('lmnd-button--x-small', 'lmnd-button--on-color')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<Button label="Add item" className="checkout-cta" />)
    const button = screen.getByRole('button')
    expect(button).toHaveClass('lmnd-button', 'checkout-cta')
  })

  it('disables from enabled={false} and does not fire', async () => {
    const onClick = vi.fn()
    render(<Button label="Add item" enabled={false} onClick={onClick} />)
    const button = screen.getByRole('button')
    expect(button).toBeDisabled()
    await userEvent.click(button)
    expect(onClick).not.toHaveBeenCalled()
  })

  it('calls onClick when enabled', async () => {
    const onClick = vi.fn()
    render(<Button label="Add item" onClick={onClick} />)
    await userEvent.click(screen.getByRole('button'))
    expect(onClick).toHaveBeenCalledTimes(1)
  })

  describe('loading', () => {
    it('keeps the label as the accessible name while replacing it on screen', () => {
      render(<Button label="Saving" loading />)
      const button = screen.getByRole('button', { name: 'Saving' })
      expect(button).toHaveAttribute('aria-busy', 'true')
      expect(button.querySelector('.lmnd-spinner')).not.toBeNull()
      expect(button.querySelector('.lmnd-button__label')).toBeNull()
    })

    // KMP passes the button's content colour as the spinner's tint. The spinner otherwise
    // defaults to content-secondary, which on a solid primary button is nearly invisible.
    it('tints the spinner with the button content colour', () => {
      expect(buttonCss).toMatch(/\.lmnd-button \.lmnd-spinner\s*\{[^}]*color:\s*inherit/)
    })

    it('keeps a caller-supplied name rather than overwriting it', () => {
      render(<Button label="Saving" loading aria-label="Saving your changes" />)
      expect(screen.getByRole('button', { name: 'Saving your changes' })).toBeTruthy()
    })

    it('leaves an idle button named by its own label', () => {
      render(<Button label="Pay" aria-label="Pay £42.00 to Merchant" />)
      expect(screen.getByRole('button', { name: 'Pay £42.00 to Merchant' })).toBeTruthy()
    })

    // Covering the two that were found by hand would leave the next one to be found the
    // same way: the rule is about spread order, so it holds for every attribute at once.
    it.each([
      ['aria-label', 'Pay £42.00 to Merchant'],
      ['aria-busy', 'false'],
      ['aria-describedby', 'fee-note'],
      ['aria-pressed', 'true'],
      ['aria-controls', 'drawer'],
    ])('keeps a caller %s while idle', (attribute, value) => {
      render(<Button label="Pay" {...{ [attribute]: value }} data-testid="button" />)
      expect(screen.getByTestId('button')).toHaveAttribute(attribute, value)
    })

    it.each([
      ['aria-label', 'Saving your changes'],
      ['aria-describedby', 'fee-note'],
      ['aria-controls', 'drawer'],
    ])('keeps a caller %s while loading', (attribute, value) => {
      render(<Button label="Saving" loading {...{ [attribute]: value }} data-testid="button" />)
      expect(screen.getByTestId('button')).toHaveAttribute(attribute, value)
    })

    // Forwarding `aria-label={undefined}` is how a wrapper passes "no opinion"; it must
    // not blank the name the spinner-only button depends on.
    it('keeps its loading name when the caller forwards an explicit undefined', () => {
      render(<Button label="Saving" loading aria-label={undefined} />)
      expect(screen.getByRole('button', { name: 'Saving' })).toBeTruthy()
    })

    it('blocks clicks, matching enabled && !loading on the platforms', async () => {
      const onClick = vi.fn()
      render(<Button label="Saving" loading onClick={onClick} />)
      const button = screen.getByRole('button')
      expect(button).toBeDisabled()
      await userEvent.click(button)
      expect(onClick).not.toHaveBeenCalled()
    })

    it('drops the icons', () => {
      render(<Button label="Saving" loading leadingIcon={<span data-testid="icon" />} />)
      expect(screen.queryByTestId('icon')).toBeNull()
    })
  })

  it('renders leading and trailing icons around the label', () => {
    render(
      <Button label="Add item" leadingIcon={<span data-testid="leading" />} trailingIcon={<span data-testid="trailing" />} />,
    )
    expect(screen.getByTestId('leading')).toBeInTheDocument()
    expect(screen.getByTestId('trailing')).toBeInTheDocument()
  })

  it('does not submit a form unless asked', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'button')
  })

  it('passes the DOM type through, so a submit button is possible', () => {
    render(<Button label="Pay" type="submit" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'submit')
  })
})

describe('buttonClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(buttonClasses()).toBe(
      'lmnd-button lmnd-button--primary lmnd-button--large lmnd-text-body-medium-semibold lmnd-button--solid',
    )
  })

  it('pairs the small sizes with the small text style', () => {
    expect(buttonClasses({ size: 'small' })).toContain('lmnd-text-body-small-semibold')
    expect(buttonClasses({ size: 'medium' })).toContain('lmnd-text-body-medium-semibold')
  })

  it('adds the state modifiers', () => {
    expect(buttonClasses({ loading: true })).toContain('lmnd-button--loading')
    expect(buttonClasses({ expandContents: true })).toContain('lmnd-button--expand')
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us. Nothing
// else catches it — the component and buttonClasses agree in JS whatever the CSS says.
describe('the class contract', () => {
  const css = buttonCss
  const typography = typographyCss

  const emitted = new Set<string>()
  for (const variant of VARIANTS) {
    for (const emphasis of EMPHASES) {
      for (const size of SIZES) {
        for (const loading of [true, false]) {
          for (const expandContents of [true, false]) {
            for (const name of buttonClasses({ variant, emphasis, size, loading, expandContents }).split(' ')) {
              emitted.add(name)
            }
          }
        }
      }
    }
  }

  // Rendered by the component rather than returned by buttonClasses. The loading ring is
  // Spinner's class, and its rules live in spinner.css — what button.css owns is the one
  // rule that tints it, which is checked separately.
  const elements = ['lmnd-button__label']

  it('emits the set of classes this suite checks', () => {
    // Guards the loops above: a class the builder can emit that nobody checks.
    expect([...emitted].sort()).toEqual([
      'lmnd-button',
      'lmnd-button--critical',
      'lmnd-button--expand',
      'lmnd-button--ghost',
      'lmnd-button--large',
      'lmnd-button--loading',
      'lmnd-button--medium',
      'lmnd-button--neutral',
      'lmnd-button--on-brand',
      'lmnd-button--on-color',
      'lmnd-button--primary',
      'lmnd-button--secondary',
      'lmnd-button--small',
      'lmnd-button--solid',
      'lmnd-button--subtle',
      'lmnd-button--x-small',
      'lmnd-text-body-medium-semibold',
      'lmnd-text-body-small-semibold',
    ])
  })

  it.each([...emitted].filter((name) => name.startsWith('lmnd-button')).concat(elements))(
    '%s is defined in button.css',
    (name) => {
      expect(css).toContain(`.${name}`)
    },
  )

  it.each([...emitted].filter((name) => name.startsWith('lmnd-text-')))(
    '%s is defined in typography.css',
    (name) => {
      expect(typography).toContain(`.${name} {`)
    },
  )
})
