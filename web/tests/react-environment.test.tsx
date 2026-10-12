import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'

// Asserts the harness itself — jsx transform, jsdom, Testing Library, jest-dom matchers —
// so a red component test means the component is wrong rather than the environment.
describe('react test environment', () => {
  it('renders JSX into a DOM and queries it', () => {
    render(<button type="button">Press</button>)
    expect(screen.getByRole('button', { name: 'Press' })).toBeInTheDocument()
  })

  it('reads token custom properties set on the document', () => {
    document.documentElement.style.setProperty('--lmnd-spacing-200', '0.5rem')
    expect(getComputedStyle(document.documentElement).getPropertyValue('--lmnd-spacing-200').trim()).toBe('0.5rem')
  })
})
