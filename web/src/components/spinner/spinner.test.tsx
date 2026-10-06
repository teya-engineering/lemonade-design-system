/// <reference types="vite/client" />
import spinnerCss from './spinner.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import type { LemonadeAssetSize } from '../../asset-size'
import { Spinner } from './spinner'
import { spinnerClasses } from './spinner.classes'

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

describe('Spinner', () => {
  it('defaults to medium, like the platforms', () => {
    render(<Spinner label="Loading" />)
    expect(screen.getByRole('status')).toHaveClass('lmnd-spinner', 'lmnd-spinner--medium')
  })

  it('renders the class list spinnerClasses returns, so a non-React consumer matches', () => {
    render(<Spinner label="Loading" size="xLarge" />)
    expect(screen.getByRole('status').className).toBe(spinnerClasses({ size: 'xLarge' }))
  })

  it('renders a span unless asked otherwise', () => {
    render(<Spinner label="Loading" />)
    expect(screen.getByRole('status').tagName).toBe('SPAN')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<Spinner label="Loading" className="centred" />)
    expect(screen.getByRole('status')).toHaveClass('lmnd-spinner', 'centred')
  })

  describe('accessibility', () => {
    // role="status" is a live region, so the label is announced when the spinner appears
    // rather than only when something moves focus to it.
    it('announces itself as a status', () => {
      render(<Spinner label="Loading your balance" />)
      expect(screen.getByRole('status', { name: 'Loading your balance' })).not.toHaveAttribute('aria-hidden')
    })

    it('hides itself when something adjacent already says it is loading', () => {
      render(<Spinner label={null} />)
      expect(screen.queryByRole('status')).toBeNull()
      expect(document.querySelector('.lmnd-spinner')).toHaveAttribute('aria-hidden', 'true')
    })
  })

  // The classes are the API for a stack that never loads this JavaScript, so a size the
  // component can emit with no rule behind it renders at the wrong size, silently.
  describe('the stylesheet behind the classes', () => {
    it.each(SIZES)('%s has a rule', (size) => {
      const [, modifier] = spinnerClasses({ size }).split(' ')
      expect(spinnerCss).toContain(`.${modifier}`)
    })

    // Without the keyframes the ring renders as a static arc — a component that looks
    // broken rather than busy, and nothing else in the suite would notice.
    it('animates with keyframes that exist', () => {
      const animation = spinnerCss.match(/animation:\s*([\w-]+)/)
      expect(animation?.[1]).toBeDefined()
      expect(spinnerCss).toContain(`@keyframes ${animation?.[1]}`)
    })

    // Compose's arc sweeps 285° and strokes at a tenth of the diameter: border-50 (2px)
    // over size-500 (20px). The mask is a percentage so it holds at every size — 80% of
    // the radius leaves a ring 20% of the radius thick, which is that tenth.
    it('matches the arc Compose draws', () => {
      expect(spinnerCss).toContain('conic-gradient(currentColor 285deg')
      expect(spinnerCss).toContain('radial-gradient(farthest-side, transparent 80%, #000 80%)')
    })

    it('slows rather than stops when motion is unwelcome', () => {
      expect(spinnerCss).toMatch(/prefers-reduced-motion: reduce/)
      expect(spinnerCss).toMatch(/animation-duration/)
    })
  })
})
