/// <reference types="vite/client" />
import slotTypes from './slot.types.ts?raw'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Slot } from './slot'

describe('Slot', () => {
  it('renders the element it is given', () => {
    render(
      <Slot as="label" htmlFor="amount">
        Amount
      </Slot>,
    )
    expect(screen.getByText('Amount').tagName).toBe('LABEL')
  })

  it('defaults to a span', () => {
    render(<Slot>Plain</Slot>)
    expect(screen.getByText('Plain').tagName).toBe('SPAN')
  })

  // ComponentProps would include `ref`, and nothing here forwards it: React 18 drops a ref
  // passed to a plain function component with a warning, so accepting one in the types
  // promises something the runtime does not do. Checked against the source because the
  // difference is invisible at runtime — it only shows up in a consumer's typecheck.
  it('does not promise a ref it cannot forward', () => {
    expect(slotTypes).toContain('ComponentPropsWithoutRef')
    expect(slotTypes).not.toMatch(/\bComponentProps\b(?!WithoutRef)/)
  })
})
