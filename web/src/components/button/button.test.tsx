import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { Button } from './button'
import { buttonClasses } from './button.classes'

describe('Button', () => {
  it('renders a button with its label', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button', { name: 'Add item' })).toBeInTheDocument()
  })

  it('defaults to primary, solid, large', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button')).toHaveClass(
      'lmnd-button',
      'lmnd-button--primary',
      'lmnd-button--large',
      'lmnd-button--solid',
      'lmnd-text-body-medium-semibold',
    )
  })

  it('renders the class list buttonClasses returns, so a non-React consumer gets the same result', () => {
    const appearance = { variant: 'critical', type: 'ghost', size: 'xSmall' } as const
    render(<Button label="Delete" {...appearance} />)
    expect(screen.getByRole('button').className).toBe(buttonClasses(appearance))
  })

  it('omits the type modifier for the variants that carry one treatment', () => {
    render(<Button label="Continue" variant="onBrand" type="ghost" />)
    expect(screen.getByRole('button').className).not.toContain('lmnd-button--ghost')
    expect(screen.getByRole('button')).toHaveClass('lmnd-button--on-brand')
  })

  it('kebab-cases the vocabulary in class names', () => {
    render(<Button label="Small" size="xSmall" variant="onColor" />)
    expect(screen.getByRole('button')).toHaveClass('lmnd-button--x-small', 'lmnd-button--on-color')
  })

  it('appends a caller className rather than replacing the contract', () => {
    render(<Button label="Add item" className="checkout-cta" />)
    const button = screen.getByRole('button')
    expect(button).toHaveClass('lmnd-button', 'checkout-cta')
  })

  it('disables from enabled={false} and does not fire', async () => {
    const onClick = vi.fn()
    render(<Button label="Add item" enabled={false} onClick={onClick} />)
    const button = screen.getByRole('button')
    expect(button).toBeDisabled()
    await userEvent.click(button)
    expect(onClick).not.toHaveBeenCalled()
  })

  it('calls onClick when enabled', async () => {
    const onClick = vi.fn()
    render(<Button label="Add item" onClick={onClick} />)
    await userEvent.click(screen.getByRole('button'))
    expect(onClick).toHaveBeenCalledTimes(1)
  })

  describe('loading', () => {
    it('keeps the label as the accessible name while replacing it on screen', () => {
      render(<Button label="Saving" loading />)
      const button = screen.getByRole('button', { name: 'Saving' })
      expect(button).toHaveAttribute('aria-busy', 'true')
      expect(button.querySelector('.lmnd-button__spinner')).not.toBeNull()
      expect(button.querySelector('.lmnd-button__label')).toBeNull()
    })

    it('blocks clicks, matching enabled && !loading on the platforms', async () => {
      const onClick = vi.fn()
      render(<Button label="Saving" loading onClick={onClick} />)
      const button = screen.getByRole('button')
      expect(button).toBeDisabled()
      await userEvent.click(button)
      expect(onClick).not.toHaveBeenCalled()
    })

    it('drops the icons', () => {
      render(<Button label="Saving" loading leadingIcon={<span data-testid="icon" />} />)
      expect(screen.queryByTestId('icon')).toBeNull()
    })
  })

  it('renders leading and trailing icons around the label', () => {
    render(
      <Button label="Add item" leadingIcon={<span data-testid="leading" />} trailingIcon={<span data-testid="trailing" />} />,
    )
    expect(screen.getByTestId('leading')).toBeInTheDocument()
    expect(screen.getByTestId('trailing')).toBeInTheDocument()
  })

  it('does not submit a form unless asked', () => {
    render(<Button label="Add item" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'button')
  })

  it('submits when htmlType says so', () => {
    render(<Button label="Pay" htmlType="submit" />)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'submit')
  })
})

describe('buttonClasses', () => {
  it('works with no arguments, for a consumer that wants the defaults', () => {
    expect(buttonClasses()).toBe(
      'lmnd-button lmnd-button--primary lmnd-button--large lmnd-text-body-medium-semibold lmnd-button--solid',
    )
  })

  it('pairs the small sizes with the small text style', () => {
    expect(buttonClasses({ size: 'small' })).toContain('lmnd-text-body-small-semibold')
    expect(buttonClasses({ size: 'medium' })).toContain('lmnd-text-body-medium-semibold')
  })

  it('adds the state modifiers', () => {
    expect(buttonClasses({ loading: true })).toContain('lmnd-button--loading')
    expect(buttonClasses({ expandContents: true })).toContain('lmnd-button--expand')
  })
})
