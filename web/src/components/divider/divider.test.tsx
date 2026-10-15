/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import dividerCss from './divider.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { HorizontalDivider, VerticalDivider } from './divider'
import { dividerClasses } from './divider.classes'
import type { DividerVariant } from './divider.types'

const exhaustive = <T extends string>(members: Record<T, true>) => Object.keys(members) as T[]
const VARIANTS = exhaustive<DividerVariant>({ solid: true, dashed: true })

describe('HorizontalDivider', () => {
  it('renders an hr, which is the separator the browser already has', () => {
    render(<HorizontalDivider data-testid="divider" />)
    const divider = screen.getByTestId('divider')
    expect(divider.tagName).toBe('HR')
    expect(divider).toHaveClass('lmnd-divider')
  })

  it('adds no modifier for the solid horizontal default', () => {
    render(<HorizontalDivider data-testid="divider" />)
    expect(screen.getByTestId('divider').className).toBe('lmnd-divider')
  })

  it('marks the dashed variant', () => {
    render(<HorizontalDivider variant="dashed" data-testid="divider" />)
    expect(screen.getByTestId('divider')).toHaveClass('lmnd-divider--dashed')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<HorizontalDivider className="section-rule" data-testid="divider" />)
    expect(screen.getByTestId('divider')).toHaveClass('lmnd-divider', 'section-rule')
  })

  describe('labelled', () => {
    it('shows the label between two lines', () => {
      render(<HorizontalDivider label="or" data-testid="divider" />)
      const divider = screen.getByTestId('divider')
      expect(divider).toHaveClass('lmnd-divider--labelled')
      expect(divider.querySelectorAll('.lmnd-divider__line')).toHaveLength(2)
      expect(screen.getByText('or')).toHaveClass('lmnd-divider__label')
    })

    // An <hr> cannot hold the label, so this form is a div — which has to say what it is,
    // or the separator disappears from the accessibility tree.
    it('keeps the separator role and names it', () => {
      render(<HorizontalDivider label="or" />)
      expect(screen.getByRole('separator', { name: 'or' })).toBeInTheDocument()
    })

    it('lets the caller name it something else', () => {
      render(<HorizontalDivider label="or" aria-label="or, choose another method" />)
      expect(screen.getByRole('separator', { name: 'or, choose another method' })).toBeInTheDocument()
    })

    // Forwarding `aria-label={undefined}` is how a wrapper passes "no opinion"; it must not
    // leave the separator unnamed.
    it('keeps the label as the name when the caller forwards an explicit undefined', () => {
      render(<HorizontalDivider label="or" aria-label={undefined} />)
      expect(screen.getByRole('separator', { name: 'or' })).toBeInTheDocument()
    })

    it('renders an empty label rather than treating it as absent', () => {
      render(<HorizontalDivider label="" data-testid="divider" />)
      expect(screen.getByTestId('divider')).toHaveClass('lmnd-divider--labelled')
    })
  })
})

describe('VerticalDivider', () => {
  it('marks the orientation', () => {
    render(<VerticalDivider data-testid="divider" />)
    expect(screen.getByTestId('divider')).toHaveClass('lmnd-divider', 'lmnd-divider--vertical')
  })

  it('renders the class list dividerClasses returns, so a non-React consumer gets the same result', () => {
    render(<VerticalDivider variant="dashed" data-testid="divider" />)
    expect(screen.getByTestId('divider').className).toBe(
      dividerClasses({ variant: 'dashed', orientation: 'vertical' }),
    )
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us.
describe('the class contract', () => {
  const emitted = new Set<string>()
  for (const variant of VARIANTS) {
    for (const orientation of ['horizontal', 'vertical'] as const) {
      for (const labelled of [true, false]) {
        for (const name of dividerClasses({ variant, orientation, labelled }).split(' ')) emitted.add(name)
      }
    }
  }

  /** Rendered by the components rather than returned by dividerClasses. */
  const elements = ['lmnd-divider__line', 'lmnd-divider__label']

  it('emits the set of classes this suite checks', () => {
    expect([...emitted].sort()).toEqual([
      'lmnd-divider',
      'lmnd-divider--dashed',
      'lmnd-divider--labelled',
      'lmnd-divider--vertical',
    ])
  })

  it.each([...emitted, ...elements])('%s has a rule behind it', (name) => {
    expect(new RegExp(`\\.${name}(?![\\w-])`).test(dividerCss)).toBe(true)
  })

  // A browser picks its own dash length; the platforms take theirs from size-100 and
  // spacing-100, so the gradient is what keeps them in step. Comments are stripped first,
  // because the one above this rule in the stylesheet names the property it avoids.
  it('draws the dashes from the tokens rather than the browser default', () => {
    const declarations = dividerCss.replace(/\/\*[\s\S]*?\*\//g, '')
    expect(declarations).toContain('repeating-linear-gradient')
    expect(declarations).toContain('var(--lmnd-size-100)')
    expect(declarations).toContain('var(--lmnd-spacing-100)')
    expect(declarations).not.toMatch(/border-style:\s*dashed/)
  })

  // The two orientations need different gradient directions, and resolving that by rule
  // order would break the moment a rule moved.
  it('turns the dashes with the orientation', () => {
    expect(dividerCss).toMatch(/\.lmnd-divider--vertical[^}]*--lmnd-divider-dash-direction:\s*to bottom/)
  })
})
