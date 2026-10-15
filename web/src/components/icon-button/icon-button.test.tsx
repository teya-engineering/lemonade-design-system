/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import iconButtonCss from './icon-button.css?raw'
import paletteCss from '../button-palette.css?raw'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { IconButton } from './icon-button'
import { iconButtonClasses } from './icon-button.classes'
import type { LemonadeButtonSize, LemonadeButtonType, LemonadeButtonVariant } from '../button/button.types'
import type { LemonadeIconButtonShape } from './icon-button.types'

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
const SHAPES = exhaustive<LemonadeIconButtonShape>({ rounded: true, circular: true })

describe('IconButton', () => {
  it('names the button with its contentDescription', () => {
    render(<IconButton use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('button', { name: 'Favourite' })).toBeInTheDocument()
  })

  // The name is on the button, so announcing the glyph as well would say it twice.
  it('hides the glyph from assistive tech', () => {
    render(<IconButton use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('button').querySelector('.lmnd-icon')).toHaveAttribute('aria-hidden', 'true')
  })

  it('defaults to neutral, subtle, medium, rounded — the platforms’ defaults, not Button’s', () => {
    render(<IconButton use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('button')).toHaveClass(
      'lmnd-icon-button',
      'lmnd-icon-button--neutral',
      'lmnd-icon-button--medium',
      'lmnd-icon-button--rounded',
      'lmnd-icon-button--subtle',
    )
  })

  it('renders the class list iconButtonClasses returns, so a non-React consumer gets the same result', () => {
    const appearance = { variant: 'critical', emphasis: 'ghost', size: 'xSmall', shape: 'circular' } as const
    render(<IconButton use="trash" contentDescription="Delete" {...appearance} />)
    expect(screen.getByRole('button').className).toBe(iconButtonClasses(appearance))
  })

  it('omits the emphasis modifier for the variants that carry one treatment', () => {
    render(<IconButton use="heart" contentDescription="Favourite" variant="onBrand" emphasis="ghost" />)
    expect(screen.getByRole('button').className).not.toContain('lmnd-icon-button--ghost')
    expect(screen.getByRole('button')).toHaveClass('lmnd-icon-button--on-brand')
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(<IconButton use="heart" contentDescription="Favourite" size="xSmall" variant="onColor" />)
    expect(screen.getByRole('button')).toHaveClass('lmnd-icon-button--x-small', 'lmnd-icon-button--on-color')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<IconButton use="heart" contentDescription="Favourite" className="header-action" />)
    expect(screen.getByRole('button')).toHaveClass('lmnd-icon-button', 'header-action')
  })

  it('passes basePath through to the glyph', () => {
    render(<IconButton use="heart" contentDescription="Favourite" basePath="https://cdn.example/icons" />)
    const icon = screen.getByRole('button').querySelector('.lmnd-icon')
    expect(icon?.getAttribute('style')).toContain("url('https://cdn.example/icons/heart.svg')")
  })

  it('disables from enabled={false} and does not fire', async () => {
    const onClick = vi.fn()
    render(<IconButton use="heart" contentDescription="Favourite" enabled={false} onClick={onClick} />)
    const button = screen.getByRole('button')
    expect(button).toBeDisabled()
    await userEvent.click(button)
    expect(onClick).not.toHaveBeenCalled()
  })

  it('calls onClick when enabled', async () => {
    const onClick = vi.fn()
    render(<IconButton use="heart" contentDescription="Favourite" onClick={onClick} />)
    await userEvent.click(screen.getByRole('button'))
    expect(onClick).toHaveBeenCalledTimes(1)
  })

  it('does not submit a form unless asked', () => {
    render(<IconButton use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'button')
  })

  it('passes the DOM type through, so a submit button is possible', () => {
    render(<IconButton use="heart" contentDescription="Save" type="submit" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'submit')
  })

  describe('loading', () => {
    it('replaces the glyph with the ring and keeps the name', () => {
      render(<IconButton use="heart" contentDescription="Favourite" loading />)
      const button = screen.getByRole('button', { name: 'Favourite' })
      expect(button).toHaveAttribute('aria-busy', 'true')
      expect(button.querySelector('.lmnd-spinner')).not.toBeNull()
      expect(button.querySelector('.lmnd-icon')).toBeNull()
    })

    it('blocks clicks, matching enabled && !loading on the platforms', async () => {
      const onClick = vi.fn()
      render(<IconButton use="heart" contentDescription="Favourite" loading onClick={onClick} />)
      const button = screen.getByRole('button')
      expect(button).toBeDisabled()
      await userEvent.click(button)
      expect(onClick).not.toHaveBeenCalled()
    })

    // The platforms tint the ring with the button's content colour; the spinner otherwise
    // defaults to content-secondary, which on a solid primary button is nearly invisible.
    it('tints the ring with the button content colour', () => {
      expect(iconButtonCss).toMatch(/\.lmnd-icon-button \.lmnd-spinner\s*\{[^}]*color:\s*inherit/)
    })
  })

  // Covering the two that were found by hand would leave the next one to be found the same
  // way: the rule is about spread order, so it holds for every attribute at once.
  it.each([
    ['aria-label', 'Add to favourites'],
    ['aria-busy', 'false'],
    ['aria-describedby', 'favourites-note'],
    ['aria-pressed', 'true'],
    ['aria-controls', 'drawer'],
  ])('keeps a caller %s', (attribute, value) => {
    render(<IconButton use="heart" contentDescription="Favourite" {...{ [attribute]: value }} data-testid="button" />)
    expect(screen.getByTestId('button')).toHaveAttribute(attribute, value)
  })

  // Forwarding `aria-label={undefined}` is how a wrapper passes "no opinion"; it must not
  // blank the only name this button has.
  it('keeps its name when the caller forwards an explicit undefined', () => {
    render(<IconButton use="heart" contentDescription="Favourite" aria-label={undefined} />)
    expect(screen.getByRole('button', { name: 'Favourite' })).toBeTruthy()
  })
})

describe('iconButtonClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(iconButtonClasses()).toBe(
      'lmnd-icon-button lmnd-icon-button--neutral lmnd-icon-button--medium lmnd-icon-button--rounded lmnd-icon-button--subtle',
    )
  })

  it('adds the loading modifier', () => {
    expect(iconButtonClasses({ loading: true })).toContain('lmnd-icon-button--loading')
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us. Nothing
// else catches it — the component and iconButtonClasses agree in JS whatever the CSS says.
describe('the class contract', () => {
  // The variant and emphasis rules live in button-palette.css, shared with Button, so the
  // contract spans both files rather than this component's stylesheet alone.
  const css = `${iconButtonCss}\n${paletteCss}`

  const emitted = new Set<string>()
  for (const variant of VARIANTS) {
    for (const emphasis of EMPHASES) {
      for (const size of SIZES) {
        for (const shape of SHAPES) {
          for (const loading of [true, false]) {
            for (const name of iconButtonClasses({ variant, emphasis, size, shape, loading }).split(' ')) {
              emitted.add(name)
            }
          }
        }
      }
    }
  }

  it('emits the set of classes this suite checks', () => {
    // Guards the loops above: a class the builder can emit that nobody checks.
    expect([...emitted].sort()).toEqual([
      'lmnd-icon-button',
      'lmnd-icon-button--circular',
      'lmnd-icon-button--critical',
      'lmnd-icon-button--ghost',
      'lmnd-icon-button--large',
      'lmnd-icon-button--loading',
      'lmnd-icon-button--medium',
      'lmnd-icon-button--neutral',
      'lmnd-icon-button--on-brand',
      'lmnd-icon-button--on-color',
      'lmnd-icon-button--primary',
      'lmnd-icon-button--rounded',
      'lmnd-icon-button--secondary',
      'lmnd-icon-button--small',
      'lmnd-icon-button--solid',
      'lmnd-icon-button--subtle',
      'lmnd-icon-button--x-small',
    ])
  })

  // A class name ends where a CSS identifier can no longer continue, so `--small` cannot be
  // satisfied by `--x-small` and deleting a base rule cannot pass on a modifier's.
  it.each([...emitted])('%s has a rule behind it', (name) => {
    expect(new RegExp(`\\.${name}(?![\\w-])`).test(css)).toBe(true)
  })

  // The glyph and the ring are sized by the button rather than by a prop, so every size has
  // to carry both rules — a missing one renders a default-size glyph in a smaller box.
  it.each(SIZES)('%s sizes the glyph and the ring it swaps in', (size) => {
    const modifier = `lmnd-icon-button--${size.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)}`
    expect(new RegExp(`\\.${modifier} \\.lmnd-icon(?![\\w-])`).test(css)).toBe(true)
    expect(new RegExp(`\\.${modifier} \\.lmnd-spinner(?![\\w-])`).test(css)).toBe(true)
  })
})
