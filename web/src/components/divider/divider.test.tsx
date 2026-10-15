/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import dividerCss from './divider.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { HorizontalDivider, VerticalDivider } from './divider'
import { dividerClasses } from './divider.classes'
import type { DividerAppearance, DividerVariant } from './divider.types'

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

    // Both forms let the caller take the role; a divider used as decoration is theirs to
    // mark as such, and the two forms differing on that would be the surprise.
    it.each([{ label: 'or' }, {}])('lets the caller take the role (%o)', (props) => {
      render(<HorizontalDivider {...props} role="presentation" data-testid="divider" />)
      expect(screen.getByTestId('divider')).toHaveAttribute('role', 'presentation')
    })

    // Forwarding `aria-label={undefined}` is how a wrapper passes "no opinion"; it must not
    // leave the separator unnamed.
    it('keeps the label as the name when the caller forwards an explicit undefined', () => {
      render(<HorizontalDivider label="or" aria-label={undefined} />)
      expect(screen.getByRole('separator', { name: 'or' })).toBeInTheDocument()
    })

    // A label the caller computed and came up empty on would otherwise open a gap in the
    // rule with nothing in it, and name the separator with an empty string.
    it.each(['', '   '])('treats a blank label (%p) as no label', (label) => {
      render(<HorizontalDivider label={label} data-testid="divider" />)
      const divider = screen.getByTestId('divider')
      expect(divider.tagName).toBe('HR')
      expect(divider.className).toBe('lmnd-divider')
      expect(divider).not.toHaveAttribute('aria-label')
    })
  })
})

describe('VerticalDivider', () => {
  it('marks the orientation in the class list', () => {
    render(<VerticalDivider data-testid="divider" />)
    expect(screen.getByTestId('divider')).toHaveClass('lmnd-divider', 'lmnd-divider--vertical')
  })

  // An <hr> is a separator already, but ARIA defaults one to horizontal — so without this
  // the orientation exists only in the stylesheet, and a screen reader says the opposite.
  it('marks the orientation for assistive tech too', () => {
    render(<VerticalDivider data-testid="divider" />)
    expect(screen.getByTestId('divider')).toHaveAttribute('aria-orientation', 'vertical')
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
  // Spelled out rather than crossed, because a vertical divider cannot be labelled — the
  // appearance type rules that pairing out, so a loop over the product would not compile.
  const appearances: DividerAppearance[] = VARIANTS.flatMap((variant) => [
    { variant },
    { variant, labelled: true },
    { variant, orientation: 'vertical' as const },
  ])

  const emitted = new Set<string>()
  for (const appearance of appearances) {
    for (const name of dividerClasses(appearance).split(' ')) emitted.add(name)
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

  // The plain form is an <hr> with no author `display`, so the browser hides it unaided.
  // The labelled form sets one, which beats the browser's `[hidden]` rule from the author
  // origin — so the two forms would disagree about whether `hidden` works.
  it('hides the labelled form too, which sets a display of its own', () => {
    expect(dividerCss).toMatch(/\.lmnd-divider--labelled\[hidden\][^{]*\{[^}]*display:\s*none/)
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

  // The two orientations need different gradient directions. Asserting only that the
  // vertical rule mentions `to bottom` would pass while the base rule overrode it, so this
  // checks the thing that decides: the override carries more classes than the base, and the
  // line never sets a direction of its own to shadow what it inherits.
  it('turns the dashes with the orientation, by specificity rather than by rule order', () => {
    const declaresDirection = (selector: string) =>
      new RegExp(`${selector}\\s*\\{[^}]*--lmnd-divider-dash-direction:`).test(dividerCss)

    expect(declaresDirection('\\.lmnd-divider')).toBe(true)
    expect(declaresDirection('\\.lmnd-divider\\.lmnd-divider--vertical')).toBe(true)
    expect(declaresDirection('\\.lmnd-divider__line')).toBe(false)
  })
})
