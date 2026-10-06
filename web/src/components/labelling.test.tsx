import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Icon } from './icon/icon'
import { Spinner } from './spinner/spinner'

// These components set their ARIA attributes before spreading the caller's props, because
// writing `role={…}` afterwards sets the attribute even when the expression is undefined,
// which throws away a role or label the caller passed on purpose.
describe('the caller has the last word on ARIA', () => {
  it('Icon keeps a role the caller supplied', () => {
    render(<Icon use="heart" contentDescription={null} role="presentation" data-testid="icon" />)
    expect(screen.getByTestId('icon')).toHaveAttribute('role', 'presentation')
  })

  it('Icon keeps a label the caller supplied', () => {
    render(<Icon use="heart" contentDescription={null} aria-label="Described elsewhere" data-testid="icon" />)
    expect(screen.getByTestId('icon')).toHaveAttribute('aria-label', 'Described elsewhere')
  })

  it('Spinner keeps a role the caller supplied', () => {
    render(<Spinner contentDescription={null} role="progressbar" data-testid="spinner" />)
    expect(screen.getByTestId('spinner')).toHaveAttribute('role', 'progressbar')
  })

  it('still applies its own when the caller supplies none', () => {
    render(<Spinner contentDescription="Loading" />)
    expect(screen.getByRole('status', { name: 'Loading' })).toBeTruthy()
  })
})
