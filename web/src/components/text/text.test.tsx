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
  it('renders its children', () => {
    render(<Text>Account balance</Text>)
    expect(screen.getByText('Account balance')).toBeInTheDocument()
  })

  it('defaults to the style the platforms provide ambiently', () => {
    render(<Text>Account balance</Text>)
    expect(screen.getByText('Account balance')).toHaveClass('lmnd-text-body-medium-regular')
  })

  it('renders a span unless asked otherwise', () => {
    render(<Text>Account balance</Text>)
    expect(screen.getByText('Account balance').tagName).toBe('SPAN')
  })

  it('renders the element it is given', () => {
    render(<Text as="h2">Balance</Text>)
    expect(screen.getByRole('heading', { level: 2, name: 'Balance' })).toBeInTheDocument()
  })

  // The reason children won over a text prop: a label is not always one flat string.
  it('takes markup, not just a string', () => {
    render(
      <Text as="p">
        Paid <strong>£42.00</strong> to <a href="/merchant">Merchant</a>
      </Text>,
    )
    expect(screen.getByText('£42.00').tagName).toBe('STRONG')
    expect(screen.getByRole('link', { name: 'Merchant' })).toBeInTheDocument()
  })

  it('appends a caller className rather than replacing the style class', () => {
    render(<Text className="truncate">Account balance</Text>)
    expect(screen.getByText('Account balance')).toHaveClass('lmnd-text-body-medium-regular', 'truncate')
  })

  it('passes the rest through to the element', () => {
    render(
      <Text id="balance" aria-live="polite">
        Account balance
      </Text>,
    )
    const node = screen.getByText('Account balance')
    expect(node).toHaveAttribute('id', 'balance')
    expect(node).toHaveAttribute('aria-live', 'polite')
  })

  // The manifest and typography.css come from one converter, so every name here has a
  // class — but a name that stopped resolving would render text with no style at all,
  // silently, and nothing else would notice.
  it.each(Object.keys(textStyles) as LemonadeTextStyle[])('renders %s with its generated class', (textStyle) => {
    render(<Text textStyle={textStyle}>{textStyle}</Text>)
    expect(screen.getByText(textStyle)).toHaveClass(textStyles[textStyle].className)
  })

  // The platforms uppercase the string; web leaves the text alone and lets the class do it,
  // so the rendered content must stay as authored while the rule carries the transform.
  it('leaves overline text as authored and uppercases it in CSS', () => {
    render(<Text textStyle="bodyXSmallOverline">account balance</Text>)
    expect(screen.getByText('account balance')).toBeInTheDocument()

    const rule = typographyCss.slice(typographyCss.indexOf('.lmnd-text-body-xsmall-overline'))
    expect(rule.slice(0, rule.indexOf('}'))).toContain('text-transform: uppercase')
  })
})
