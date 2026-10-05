/// <reference types="vite/client" />
// `?raw` rather than node:fs, so the package does not pull @types/node into a tsconfig it
// shares with src — that would make `process` and `Buffer` typecheck in a browser library.
import typographyCss from '../../../styles/typography.css?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { textStyles } from '../../text-styles.generated'
import { Text } from './text'
import type { LemonadeTextStyle } from './text.types'

describe('Text', () => {
  it('renders its text', () => {
    render(<Text text="Account balance" />)
    expect(screen.getByText('Account balance')).toBeInTheDocument()
  })

  it('defaults to the style the platforms provide ambiently', () => {
    render(<Text text="Account balance" />)
    expect(screen.getByText('Account balance')).toHaveClass('lmnd-text-body-medium-regular')
  })

  it('renders a span unless asked otherwise', () => {
    render(<Text text="Account balance" />)
    expect(screen.getByText('Account balance').tagName).toBe('SPAN')
  })

  it('renders the element it is given', () => {
    render(<Text text="Balance" as="h2" />)
    expect(screen.getByRole('heading', { level: 2, name: 'Balance' })).toBeInTheDocument()
  })

  it('appends a caller className rather than replacing the style class', () => {
    render(<Text text="Account balance" className="truncate" />)
    expect(screen.getByText('Account balance')).toHaveClass('lmnd-text-body-medium-regular', 'truncate')
  })

  it('passes the rest through to the element', () => {
    render(<Text text="Account balance" id="balance" aria-live="polite" />)
    const node = screen.getByText('Account balance')
    expect(node).toHaveAttribute('id', 'balance')
    expect(node).toHaveAttribute('aria-live', 'polite')
  })

  // The manifest and typography.css come from one converter, so every name here has a
  // class — but a name that stopped resolving would render text with no style at all,
  // silently, and nothing else would notice.
  it.each(Object.keys(textStyles) as LemonadeTextStyle[])('renders %s with its generated class', (textStyle) => {
    render(<Text text={textStyle} textStyle={textStyle} />)
    expect(screen.getByText(textStyle)).toHaveClass(textStyles[textStyle].className)
  })

  // The platforms uppercase the string; web leaves the text alone and lets the class do it,
  // so the rendered content must stay as authored while the rule carries the transform.
  it('leaves overline text as authored and uppercases it in CSS', () => {
    render(<Text text="account balance" textStyle="bodyXSmallOverline" />)
    expect(screen.getByText('account balance')).toBeInTheDocument()

    const rule = typographyCss.slice(typographyCss.indexOf('.lmnd-text-body-xsmall-overline'))
    expect(rule.slice(0, rule.indexOf('}'))).toContain('text-transform: uppercase')
  })
})
