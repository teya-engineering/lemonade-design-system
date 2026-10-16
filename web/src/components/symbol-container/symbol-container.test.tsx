/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import symbolContainerCss from './symbol-container.css?raw'
import tokensCss from '../../../styles/tokens.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { SymbolContainer } from './symbol-container'
import { symbolContainerClasses, symbolContainerTextStyle } from './symbol-container.classes'
import type { SymbolContainerShape, SymbolContainerSize, SymbolContainerVoice } from './symbol-container.types'
import type { ThemedHue } from '../../themed-hue'

const exhaustive = <T extends string>(members: Record<T, true>) => Object.keys(members) as T[]

const VOICES = exhaustive<SymbolContainerVoice>({
  neutral: true,
  critical: true,
  warning: true,
  info: true,
  positive: true,
  brand: true,
  brandSubtle: true,
})
const SIZES = exhaustive<SymbolContainerSize>({
  xSmall: true,
  small: true,
  medium: true,
  large: true,
  xLarge: true,
  xxLarge: true,
})
const SHAPES = exhaustive<SymbolContainerShape>({ circle: true, rounded: true })
const HUES = exhaustive<ThemedHue>({
  amber: true,
  blue: true,
  cyan: true,
  fuchsia: true,
  green: true,
  greenLime: true,
  indigo: true,
  neutral: true,
  orange: true,
  pink: true,
  purple: true,
  red: true,
  rose: true,
  teal: true,
  violet: true,
  yellow: true,
  yellowLime: true,
})

const kebab = (value: string) => value.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)

describe('SymbolContainer', () => {
  it('defaults to neutral, medium, circle', () => {
    render(<SymbolContainer use="heart" contentDescription={null} data-testid="symbol" />)
    expect(screen.getByTestId('symbol')).toHaveClass(
      'lmnd-symbol-container',
      'lmnd-symbol-container--medium',
      'lmnd-symbol-container--circle',
      'lmnd-symbol-container--neutral',
    )
  })

  it('renders the class list symbolContainerClasses returns, so a non-React consumer matches', () => {
    const appearance = { voice: 'brand', size: 'large', shape: 'rounded' } as const
    render(<SymbolContainer use="heart" contentDescription={null} {...appearance} data-testid="symbol" />)
    expect(screen.getByTestId('symbol').className).toBe(symbolContainerClasses(appearance))
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(
      <SymbolContainer use="heart" contentDescription={null} voice="brandSubtle" size="xxLarge" data-testid="symbol" />,
    )
    expect(screen.getByTestId('symbol')).toHaveClass(
      'lmnd-symbol-container--brand-subtle',
      'lmnd-symbol-container--xx-large',
    )
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<SymbolContainer use="heart" contentDescription={null} className="row-avatar" data-testid="symbol" />)
    expect(screen.getByTestId('symbol')).toHaveClass('lmnd-symbol-container', 'row-avatar')
  })

  describe('content', () => {
    it('names the glyph when it carries meaning', () => {
      render(<SymbolContainer use="heart" contentDescription="Favourite" />)
      expect(screen.getByRole('img', { name: 'Favourite' })).toBeInTheDocument()
    })

    it('hides the glyph when the description is null', () => {
      render(<SymbolContainer use="heart" contentDescription={null} data-testid="symbol" />)
      expect(screen.getByTestId('symbol').querySelector('.lmnd-icon')).toHaveAttribute('aria-hidden', 'true')
    })

    it.each(SIZES)('pairs %s initials with the type style the platforms use', (size) => {
      render(<SymbolContainer text="FM" size={size} />)
      expect(screen.getByText('FM')).toHaveClass(symbolContainerTextStyle(size))
    })

    it('renders a free slot as given', () => {
      render(
        <SymbolContainer>
          <span data-testid="custom" />
        </SymbolContainer>,
      )
      expect(screen.getByTestId('custom')).toBeInTheDocument()
    })
  })

  describe('themed', () => {
    it('leaves the voice class off and sets the three properties from the hue', () => {
      render(<SymbolContainer use="heart" contentDescription={null} theme={{ hue: 'violet' }} data-testid="symbol" />)
      const symbol = screen.getByTestId('symbol')
      expect(symbol.className).not.toContain('lmnd-symbol-container--neutral')
      const style = symbol.getAttribute('style') ?? ''
      expect(style).toContain('--lmnd-symbol-container-bg: var(--lmnd-color-themed-violet-background)')
      expect(style).toContain('--lmnd-symbol-container-border: var(--lmnd-color-themed-violet-border)')
      expect(style).toContain('--lmnd-symbol-container-content: var(--lmnd-color-themed-violet-on-background)')
    })

    it('reads the subtle palette when asked', () => {
      render(
        <SymbolContainer
          use="heart"
          contentDescription={null}
          theme={{ hue: 'violet', subtle: true }}
          data-testid="symbol"
        />,
      )
      expect(screen.getByTestId('symbol').getAttribute('style')).toContain(
        '--lmnd-symbol-container-bg: var(--lmnd-color-themed-violet-subtle-background)',
      )
    })

    it('kebab-cases a two-word hue', () => {
      render(
        <SymbolContainer use="heart" contentDescription={null} theme={{ hue: 'yellowLime' }} data-testid="symbol" />,
      )
      expect(screen.getByTestId('symbol').getAttribute('style')).toContain('--lmnd-color-themed-yellow-lime-background')
    })

    it('keeps a caller style alongside the themed properties', () => {
      render(
        <SymbolContainer
          use="heart"
          contentDescription={null}
          theme={{ hue: 'blue' }}
          style={{ margin: '4px' }}
          data-testid="symbol"
        />,
      )
      const style = screen.getByTestId('symbol').getAttribute('style') ?? ''
      expect(style).toContain('margin: 4px')
      expect(style).toContain('--lmnd-color-themed-blue-background')
    })

    // The component builds these names by string, so a hue whose properties were never
    // generated would resolve to nothing and render an invisible surface.
    it.each(HUES)('%s resolves to generated properties, plain and subtle', (hue) => {
      for (const family of ['', '-subtle']) {
        for (const slot of ['background', 'border', 'on-background']) {
          expect(tokensCss).toContain(`--lmnd-color-themed-${kebab(hue)}${family}-${slot}:`)
        }
      }
    })
  })

  describe('badgeSlot', () => {
    it('renders nothing unless given one', () => {
      render(<SymbolContainer use="heart" contentDescription={null} data-testid="symbol" />)
      expect(screen.getByTestId('symbol').querySelector('.lmnd-symbol-container__badge')).toBeNull()
    })

    it('wraps what it is given in the badge slot', () => {
      render(
        <SymbolContainer use="heart" contentDescription={null} badgeSlot={<span data-testid="badge" />} data-testid="symbol" />,
      )
      expect(screen.getByTestId('symbol').querySelector('.lmnd-symbol-container__badge')).toContainElement(
        screen.getByTestId('badge'),
      )
    })
  })
})

describe('symbolContainerClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(symbolContainerClasses()).toBe(
      'lmnd-symbol-container lmnd-symbol-container--medium lmnd-symbol-container--circle lmnd-symbol-container--neutral',
    )
  })

  it('leaves the voice class off for a themed container', () => {
    expect(symbolContainerClasses({ voice: null })).not.toContain('--neutral')
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us.
describe('the class contract', () => {
  const emitted = new Set<string>()
  for (const voice of [...VOICES, null]) {
    for (const size of SIZES) {
      for (const shape of SHAPES) {
        for (const name of symbolContainerClasses({ voice, size, shape }).split(' ')) emitted.add(name)
      }
    }
  }

  /** Rendered by the component rather than returned by symbolContainerClasses. */
  const elements = ['lmnd-symbol-container__badge']

  it('emits the set of classes this suite checks', () => {
    expect([...emitted].sort()).toEqual([
      'lmnd-symbol-container',
      'lmnd-symbol-container--brand',
      'lmnd-symbol-container--brand-subtle',
      'lmnd-symbol-container--circle',
      'lmnd-symbol-container--critical',
      'lmnd-symbol-container--info',
      'lmnd-symbol-container--large',
      'lmnd-symbol-container--medium',
      'lmnd-symbol-container--neutral',
      'lmnd-symbol-container--positive',
      'lmnd-symbol-container--rounded',
      'lmnd-symbol-container--small',
      'lmnd-symbol-container--warning',
      'lmnd-symbol-container--x-large',
      'lmnd-symbol-container--x-small',
      'lmnd-symbol-container--xx-large',
    ])
  })

  it.each([...emitted, ...elements])('%s has a rule behind it', (name) => {
    expect(new RegExp(`\\.${name}(?![\\w-])`).test(symbolContainerCss)).toBe(true)
  })

  it.each(VOICES)('%s sets a fill, a border and a content colour', (voice) => {
    const block =
      new RegExp(`\\.lmnd-symbol-container--${kebab(voice)}\\s*\\{([^}]*)\\}`).exec(symbolContainerCss)?.[1] ?? ''
    expect(block).toContain('--lmnd-symbol-container-bg:')
    expect(block).toContain('--lmnd-symbol-container-border:')
    expect(block).toContain('--lmnd-symbol-container-content:')
  })

  // The container sizes its glyph rather than taking a prop for it, so every size has to
  // carry that rule — a missing one leaves a medium glyph in a container of another size.
  it.each(SIZES)('%s sizes the glyph inside it', (size) => {
    expect(
      new RegExp(`\\.lmnd-symbol-container--${kebab(size)} \\.lmnd-icon(?![\\w-])`).test(symbolContainerCss),
    ).toBe(true)
  })

  // Rounded's radius steps with the size, so each one needs its own pairing.
  it.each(SIZES)('%s has a rounded radius', (size) => {
    expect(
      new RegExp(`\\.lmnd-symbol-container--rounded\\.lmnd-symbol-container--${kebab(size)}(?![\\w-])`).test(
        symbolContainerCss,
      ),
    ).toBe(true)
  })
})
