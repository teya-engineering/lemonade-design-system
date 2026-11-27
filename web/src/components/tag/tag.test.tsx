/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import tagCss from './tag.css?raw'
import typographyCss from '../../../styles/typography.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Tag } from './tag'
import { tagClasses } from './tag.classes'
import type { TagVoice } from './tag.types'

const exhaustive = <T extends string>(members: Record<T, true>) => Object.keys(members) as T[]

const VOICES = exhaustive<TagVoice>({
  neutral: true,
  critical: true,
  warning: true,
  info: true,
  positive: true,
  neutralOnColor: true,
  featured: true,
})

describe('Tag', () => {
  it('renders its label', () => {
    render(<Tag label="Pending" />)
    expect(screen.getByText('Pending')).toHaveClass('lmnd-tag__label')
  })

  it('defaults to the neutral voice and the type style the platforms pair with it', () => {
    render(<Tag label="Pending" data-testid="tag" />)
    expect(screen.getByTestId('tag')).toHaveClass(
      'lmnd-tag',
      'lmnd-tag--neutral',
      'lmnd-text-body-xsmall-semibold',
    )
  })

  it('renders the class list tagClasses returns, so a non-React consumer gets the same result', () => {
    render(<Tag label="Overdue" voice="critical" data-testid="tag" />)
    expect(screen.getByTestId('tag').className).toBe(tagClasses({ voice: 'critical' }))
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(<Tag label="Live" voice="neutralOnColor" data-testid="tag" />)
    expect(screen.getByTestId('tag')).toHaveClass('lmnd-tag--neutral-on-color')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<Tag label="Pending" className="row-tag" data-testid="tag" />)
    expect(screen.getByTestId('tag')).toHaveClass('lmnd-tag', 'row-tag')
  })

  it('renders no glyph unless asked', () => {
    render(<Tag label="Pending" data-testid="tag" />)
    expect(screen.getByTestId('tag').querySelector('.lmnd-icon')).toBeNull()
  })

  // Decorative on the platforms too: the label beside it already says what the tag is.
  it('renders the glyph before the label, hidden from assistive tech', () => {
    render(<Tag label="Settled" use="heart" data-testid="tag" />)
    const icon = screen.getByTestId('tag').querySelector('.lmnd-icon')
    expect(icon).toHaveAttribute('aria-hidden', 'true')
    expect(icon?.nextElementSibling).toHaveClass('lmnd-tag__label')
  })

  it('passes basePath through to the glyph', () => {
    render(<Tag label="Settled" use="heart" basePath="https://cdn.example/icons" data-testid="tag" />)
    const icon = screen.getByTestId('tag').querySelector('.lmnd-icon')
    expect(icon?.getAttribute('style')).toContain("url('https://cdn.example/icons/heart.svg')")
  })

  // A tag is a label, not a control or a status region, on every platform.
  it('carries no role of its own', () => {
    render(<Tag label="Pending" data-testid="tag" />)
    expect(screen.getByTestId('tag')).not.toHaveAttribute('role')
  })
})

describe('tagClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(tagClasses()).toBe('lmnd-tag lmnd-tag--neutral lmnd-text-body-xsmall-semibold')
  })
})

// The class names are the public API: a non-React consumer writes them by hand, so a class
// the component emits with no rule behind it renders unstyled for them and for us.
describe('the class contract', () => {
  const emitted = new Set<string>()
  for (const voice of VOICES) {
    for (const name of tagClasses({ voice }).split(' ')) emitted.add(name)
  }

  /** Rendered by the component rather than returned by tagClasses. */
  const elements = ['lmnd-tag__label']

  it('emits the set of classes this suite checks', () => {
    expect([...emitted].sort()).toEqual([
      'lmnd-tag',
      'lmnd-tag--critical',
      'lmnd-tag--featured',
      'lmnd-tag--info',
      'lmnd-tag--neutral',
      'lmnd-tag--neutral-on-color',
      'lmnd-tag--positive',
      'lmnd-tag--warning',
      'lmnd-text-body-xsmall-semibold',
    ])
  })

  it.each([...emitted].filter((name) => name.startsWith('lmnd-tag')).concat(elements))(
    '%s has a rule behind it',
    (name) => {
      expect(new RegExp(`\\.${name}(?![\\w-])`).test(tagCss)).toBe(true)
    },
  )

  it.each([...emitted].filter((name) => name.startsWith('lmnd-text-')))(
    '%s is a generated text style',
    (name) => {
      expect(typographyCss).toContain(`.${name} {`)
    },
  )

  // The tag sets `display`, which beats the browser's `[hidden]` rule from the author
  // origin whatever the specificity — so `<Tag hidden />` would stay on screen.
  it('stays hidden when the caller hides it', () => {
    render(<Tag label="Pending" hidden data-testid="tag" />)
    expect(screen.getByTestId('tag')).toBeInTheDocument()
    expect(tagCss).toMatch(/\.lmnd-tag\[hidden\][^{]*\{[^}]*display:\s*none/)
  })

  // The platforms draw their ring without consuming layout. A tag has no fixed dimensions
  // for box-sizing to work against, so a real border would grow it by a pixel on each edge
  // and leave it taller than the same tag on mobile.
  it('draws its ring without taking up room', () => {
    expect(tagCss).toMatch(/\.lmnd-tag\s*\{[^}]*box-shadow:\s*inset[^}]*--lmnd-tag-border/)
    expect(tagCss).not.toMatch(/\.lmnd-tag\s*\{[^}]*\bborder:\s*var/)
  })

  // The label never wraps, so without this the tag's automatic minimum is the whole label
  // and it pushes out of a row rather than truncating. `max-width: 100%` does not cover it:
  // beside a sibling that cannot shrink, the whole row is already more room than is left.
  it('can shrink below its label beside a sibling that cannot', () => {
    expect(tagCss).toMatch(/\.lmnd-tag\s*\{[^}]*min-width:\s*0/)
  })

  // Checking each voice declares the three names leaves the other half open: rename what
  // the base rule reads and every voice still declares three properties nobody consumes,
  // painting an unstyled tag. Only comparing the two sets catches that.
  it('reads exactly the properties the voices set', () => {
    const names = (pattern: RegExp) => new Set([...tagCss.matchAll(pattern)].map((match) => match[1]))
    const set = names(/(--lmnd-tag-[\w-]+)\s*:/g)
    const read = names(/var\((--lmnd-tag-[\w-]+)\)/g)
    expect(set.size).toBe(3)
    expect([...read].sort()).toEqual([...set].sort())
  })

  // Each voice sets all three, and a missing one falls back to nothing rather than to a
  // default — an invisible fill or border, with every other check still green.
  it.each(VOICES)('%s sets a fill, a border and a content colour', (voice) => {
    const modifier = `lmnd-tag--${voice.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)}`
    const block = new RegExp(`\\.${modifier}\\s*\\{([^}]*)\\}`).exec(tagCss)?.[1] ?? ''
    expect(block).toContain('--lmnd-tag-bg:')
    expect(block).toContain('--lmnd-tag-border:')
    expect(block).toContain('--lmnd-tag-content:')
  })
})
