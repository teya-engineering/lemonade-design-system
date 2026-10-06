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
