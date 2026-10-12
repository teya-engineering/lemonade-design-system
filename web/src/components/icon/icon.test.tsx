/// <reference types="vite/client" />
import iconCss from '../../../styles/icon.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { Icon, defaultIconBasePath } from './icon'
import { iconClasses } from './icon.classes'
import type { LemonadeAssetSize } from './icon.types'

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

describe('Icon', () => {
  it('masks the named asset', () => {
    render(<Icon use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('img', { name: 'Favourite' })).toHaveStyle({
      '--lmnd-icon': `url('${defaultIconBasePath}/heart.svg')`,
    })
  })

  it('defaults to medium, like the platforms', () => {
    render(<Icon use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('img')).toHaveClass('lmnd-icon', 'lmnd-icon--medium')
  })

  it('renders the class list iconClasses returns, so a non-React consumer matches', () => {
    render(<Icon use="heart" contentDescription="Favourite" size="xxLarge" />)
    expect(screen.getByRole('img').className).toBe(iconClasses({ size: 'xxLarge' }))
  })

  it('serves from somewhere else when told to', () => {
    render(<Icon use="heart" contentDescription="Favourite" basePath="https://cdn.example.com/icons" />)
    expect(screen.getByRole('img')).toHaveStyle({ '--lmnd-icon': "url('https://cdn.example.com/icons/heart.svg')" })
  })

  it('renders a span unless asked otherwise', () => {
    render(<Icon use="heart" contentDescription="Favourite" />)
    expect(screen.getByRole('img').tagName).toBe('SPAN')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<Icon use="heart" contentDescription="Favourite" className="pulse" />)
    expect(screen.getByRole('img')).toHaveClass('lmnd-icon', 'pulse')
  })

  describe('accessibility', () => {
    it('announces the label it is given', () => {
      render(<Icon use="heart" contentDescription="Favourite" />)
      const node = screen.getByRole('img', { name: 'Favourite' })
      expect(node).not.toHaveAttribute('aria-hidden')
    })

    // The decorative case has to be spelled, not defaulted: an icon beside its own label
    // read twice is worse than one read never.
    it('hides itself from the tree when the label is null', () => {
      render(<Icon use="heart" contentDescription={null} />)
      expect(screen.queryByRole('img')).toBeNull()
      expect(document.querySelector('.lmnd-icon')).toHaveAttribute('aria-hidden', 'true')
    })
  })

  // The polymorphic API exists so an icon can be a link or a button. Those elements say
  // what they are; overwriting that with role="img" would hide the interaction.
  describe('an element the caller chose', () => {
    it('keeps its own role and takes only the name', () => {
      render(
        <Icon as="a" href="/favourites" use="heart" contentDescription="Favourites" data-testid="icon" />,
      )
      const node = screen.getByTestId('icon')
      expect(node.tagName).toBe('A')
      expect(node).not.toHaveAttribute('role')
      expect(node).toHaveAttribute('aria-label', 'Favourites')
    })

    it('still takes role="img" when it renders the default element', () => {
      render(<Icon use="heart" contentDescription="Favourites" />)
      expect(screen.getByRole('img', { name: 'Favourites' })).toBeTruthy()
    })

    // A focusable element carrying aria-hidden is reachable by keyboard and absent from the
    // accessibility tree, which is worse than either on its own. The component only ever
    // hides the element it owns.
    it.each(['a', 'button', 'div'] as const)('does not hide a %s the caller chose', (element) => {
      render(<Icon as={element} use="heart" contentDescription={null} data-testid="icon" />)
      expect(screen.getByTestId('icon')).not.toHaveAttribute('aria-hidden')
    })

    it('still hides the span it renders by default', () => {
      render(<Icon use="heart" contentDescription={null} data-testid="icon" />)
      expect(screen.getByTestId('icon')).toHaveAttribute('aria-hidden', 'true')
    })

    it('does not discard a role the caller passed', () => {
      render(<Icon use="heart" contentDescription={null} role="presentation" data-testid="icon" />)
      expect(screen.getByTestId('icon')).toHaveAttribute('role', 'presentation')
    })
  })

  it('reports an unknown name instead of rendering blank space', () => {
    const error = vi.spyOn(console, 'error').mockImplementation(() => {})
    // @ts-expect-error — the point is the name a JavaScript caller can still pass.
    render(<Icon use="not-an-icon" contentDescription="Nothing" />)
    expect(error).toHaveBeenCalledOnce()
    error.mockRestore()
  })

  // The classes are the API for a stack that never loads this JavaScript, so a size the
  // component can emit with no rule behind it renders at the wrong size, silently.
  it.each(SIZES)('%s has a rule in icon.css', (size) => {
    const [, modifier] = iconClasses({ size }).split(' ')
    expect(iconCss).toContain(`.${modifier}`)
  })
})
