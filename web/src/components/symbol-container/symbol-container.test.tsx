/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import symbolContainerCss from './symbol-container.css?raw'
import tokensCss from '../../../styles/tokens.css?raw'
import typographyCss from '../../../styles/typography.css?raw'
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

    // Written out rather than read back from the builder, which would compare the table to
    // itself and pass on every wrong entry.
    it.each([
      ['xSmall', 'lmnd-text-body-xsmall-semibold'],
      ['small', 'lmnd-text-body-small-semibold'],
      ['medium', 'lmnd-text-body-small-semibold'],
      ['large', 'lmnd-text-body-large-semibold'],
      ['xLarge', 'lmnd-text-body-xlarge-semibold'],
      ['xxLarge', 'lmnd-text-heading-small'],
    ] as [SymbolContainerSize, string][])(
      'pairs %s initials with the type style the platforms use',
      (size, textStyle) => {
        render(<SymbolContainer text="FM" size={size} />)
        expect(screen.getByText('FM')).toHaveClass(textStyle)
        expect(symbolContainerTextStyle(size)).toBe(textStyle)
        // The other suites check their styles against the generated sheet; without it a
        // typo here renders the initials at the inherited size and nothing notices.
        expect(typographyCss).toContain(`.${textStyle} {`)
      },
    )

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
    // `badgeSlot={unread > 0 && <Badge/>}` passes `false`, not nothing, when there is no
    // badge — and an empty wrapper would still hang off the corner.
    it.each([undefined, null, false])('renders no wrapper for %p', (badgeSlot) => {
      render(
        <SymbolContainer use="heart" contentDescription={null} badgeSlot={badgeSlot} data-testid="symbol" />,
      )
      expect(screen.getByTestId('symbol').querySelector('.lmnd-symbol-container__badge')).toBeNull()
    })

    // Zero is a badge that reads zero, not an absent one.
    it('renders a zero count', () => {
      render(<SymbolContainer use="heart" contentDescription={null} badgeSlot={0} data-testid="symbol" />)
      expect(screen.getByTestId('symbol').querySelector('.lmnd-symbol-container__badge')).toHaveTextContent('0')
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

  // A deliberate divergence, recorded in §17.5: the platforms ring brand with border-brand,
  // which is the same colour as bg-brand in the dark theme, and brand-subtle with a white
  // border-on-brand-low, which is drawn for a saturated fill rather than 30% of one. Both
  // take the neutral ring here. Restoring parity would look like a fix and put them back.
  it.each(['brand', 'brand-subtle'])('rings %s with neutral-low, not the platforms’ token', (voice) => {
    const block = new RegExp(`\\.lmnd-symbol-container--${voice}\\s*\\{([^}]*)\\}`).exec(symbolContainerCss)?.[1] ?? ''
    expect(block).toContain('--lmnd-symbol-container-border: var(--lmnd-color-border-neutral-low)')
  })

  // Checking each voice declares the three names leaves the other half open: rename what
  // the base rule reads and every voice still declares three properties nobody consumes,
  // painting a transparent container. Only comparing the two sets catches that.
  it('reads exactly the properties the voices set', () => {
    const names = (pattern: RegExp) =>
      new Set([...symbolContainerCss.matchAll(pattern)].map((match) => match[1]))
    const set = names(/(--lmnd-symbol-container-[\w-]+)\s*:/g)
    const read = names(/var\((--lmnd-symbol-container-[\w-]+)\)/g)
    expect(set.size).toBe(3)
    expect([...read].sort()).toEqual([...set].sort())
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
  // The container sets `display`, which beats the browser's `[hidden]` rule from the author
  // origin whatever the specificity — so `<SymbolContainer hidden />` would stay on screen.
  it('stays hidden when the caller hides it', () => {
    render(<SymbolContainer use="heart" contentDescription={null} hidden data-testid="symbol" />)
    expect(screen.getByTestId('symbol')).toBeInTheDocument()
    expect(symbolContainerCss).toMatch(/\.lmnd-symbol-container\[hidden\][^{]*\{[^}]*display:\s*none/)
  })

  // The corner anchoring only lands where intended if the wrapper is the size of the badge.
  // As a line box it would reserve descender room under an icon sitting on the baseline,
  // and `bottom` would anchor that empty strip instead.
  it('hugs the badge rather than laying it out on a baseline', () => {
    expect(symbolContainerCss).toMatch(/\.lmnd-symbol-container__badge\s*\{[^}]*display:\s*flex/)
  })

  // Matched as a direct child: a badge is a descendant too, and these rules outrank
  // `.lmnd-icon--<size>`, so a descendant selector would resize a glyph inside the badge to
  // the container's own size.
  it.each(SIZES)('%s sizes its own glyph and not one inside the badge', (size) => {
    expect(
      new RegExp(`\\.lmnd-symbol-container--${kebab(size)} > \\.lmnd-icon(?![\\w-])`).test(symbolContainerCss),
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
